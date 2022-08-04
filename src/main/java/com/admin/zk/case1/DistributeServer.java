package com.admin.zk.case1;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @Autor liurong
 * @Data 2022/8/4
 * @Description:
 */
public class DistributeServer {

    private static String connectString = "hadoop001:2181,hadoop003:2181,hadoop004:2181";

    private int sessionTimeout = 2000;

    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        DistributeServer server = new DistributeServer();
        // 获取zk连接
        server.getConnect();
        
        // 注册服务器ZK集群
        server.regist(args[0]);

        // 启动业务逻辑（sleep一下）
        server.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void regist(String hostname) throws KeeperException, InterruptedException {
        String create = zooKeeper.create("/servers/" + hostname, hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println(hostname + " is online");
    }

    private void getConnect() throws IOException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }
}