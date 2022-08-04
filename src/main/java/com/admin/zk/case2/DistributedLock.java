package com.admin.zk.case2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Autor liurong
 * @Data 2022/8/4
 * @Description: 分布式锁实现(手动)
 */
public class DistributedLock {

    private final String connectString = "hadoop001:2181,hadoop003:2181,hadoop004:2181";

    private final int sessionTimeout = 2000;

    private CountDownLatch connectLatch = new CountDownLatch(1);

    private ZooKeeper zooKeeper;

    private String rootNode = "locks";

    private String subNode = "seq-";

    // 当前Client等待的子节点
    private String waitPath;

    private CountDownLatch nodeLatch = new CountDownLatch(1);

    private String currentNode;

    public DistributedLock() throws IOException, InterruptedException, KeeperException {
        // 获取连接
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                // connectLatch连接如果连接到zk， 可以释放
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    connectLatch.countDown();
                }

                // 如果监听的节点被删除， 可以释放
                if(watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)) {
                    nodeLatch.countDown();
                }
            }
        });

        // 等待zk正式连接完成，再往下走
        connectLatch.await();

        // 判断根节点/locks是否存在
        Stat stat = zooKeeper.exists("/" + rootNode, false);

        // 如果为空，创建节点
        if(stat == null) {
            zooKeeper.create("/" + rootNode, rootNode.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    // 对zk加锁
    public void zkLock() throws KeeperException, InterruptedException {
        // 创建临时带序号的节点
        currentNode = zooKeeper.create("/" + rootNode + "/" + subNode, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        // 获取所有节点
        List<String> childrenNodes = zooKeeper.getChildren("/" + rootNode, false);
        // 判断创建的节点是否最小的节点，如果是获取所，如果不是见听他序号前一个节点
        if(childrenNodes.size() == 1) {
            return;
        } else {
            String thisNode = currentNode.substring(("/" + rootNode + "/").length());
            // 排序
            Collections.sort(childrenNodes);
            // 获取节点位置
            int index = childrenNodes.indexOf(thisNode);
            if(index == -1) {
                System.out.println("获取节点错误！");
            } else if(index == 0) {
                return;
            } else {
                this.waitPath = "/" + rootNode + "/" + childrenNodes.get(index -1);
                //  在 waitPath 上注册监听器, 当 waitPath 被删除时, zookeeper 会回调监听器的 process 方法
                zooKeeper.getData(waitPath, true, null);
                // 等待监听完就执行下一步
                nodeLatch.await();
                return;
            }
        }

    }

    // 对ZK进行解锁
    public void unZkLock() throws KeeperException, InterruptedException {
    // 删除节点
        zooKeeper.delete(currentNode, -1);
    }
}