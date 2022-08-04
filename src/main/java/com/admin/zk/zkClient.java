package com.admin.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @Autor liurong
 * @Data 2022/8/2
 * @Description:
 */
public class zkClient {

    // 连接主机名称，逗号后多个中间不能有空格
    private static String connectString = "hadoop001:2181,hadoop003:2181,hadoop004:2181";

    // 连接时间
    private static int sessionTimeOut = 2000;

    ZooKeeper zkClient;

    /**
     * 连接项目
     *
     * @throws IOException
     */
    @Before
    public void init() throws IOException {

         zkClient = new ZooKeeper(connectString, sessionTimeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                // System.out.println("__________________________");
                // List<String> childrens = null;
                // try {
                //     childrens = zkClient.getChildren("/", true);
                // } catch (KeeperException e) {
                //     e.printStackTrace();
                // } catch (InterruptedException e) {
                //     e.printStackTrace();
                // }
                // for (String children : childrens) {
                //     System.out.println(children);
                // }
                // System.out.println("__________________________");

            }
        });
    }

    /**
     * 创建节点
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void create() throws KeeperException, InterruptedException {
        String nodeCreated = zkClient.create("/sanguo", "diaochan".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * 监听节点变化
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        List<String> childrens = zkClient.getChildren("/", true);

        for (String children : childrens) {
            System.out.println(children);
        }

        // 延时
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 判断节点是否存在
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void isExit() throws KeeperException, InterruptedException {
        Stat stat = zkClient.exists("/sanguo", false);
        System.out.println("isExists: "  + stat==null?  "no" : "yes");
    }

}