package com.admin.zk.case1;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Autor liurong
 * @Data 2022/8/4
 * @Description:
 */
public class DistributeClient {

    private String connectString = "hadoop001:2181,hadoop003:2181,hadoop004:2181";

    private int sessionTimeout = 2000;

    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        DistributeClient client = new DistributeClient();
        // 获取zk连接
        client.getConnect();

        //  监听/server下的节点增加和删除
        client.getServerList();

        // 业务逻辑
        client.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void getServerList() throws KeeperException, InterruptedException {
        List<String> childrenList = zooKeeper.getChildren("/servers", true);
        // 存值的数组
        ArrayList<String> servers = new ArrayList<String>();
        for(String children :childrenList) {
            // 拿到节点的值
            byte[] data = zooKeeper.getData("/servers/" + children, false, null);
            servers.add(new String(data));
        }
        System.out.println(servers);
    }

    private void getConnect() throws IOException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                // 时刻检测
                try {
                    getServerList();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}