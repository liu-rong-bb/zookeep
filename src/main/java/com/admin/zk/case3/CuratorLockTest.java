package com.admin.zk.case3;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Autor liurong
 * @Data 2022/8/4
 * @Description: 用框架创建分布式锁
 */
public class CuratorLockTest {

    private String rootNode = "/locks";

    private String connectString = "hadoop001:2181,hadoop003:2181,hadoop004:2181";

    private int timeOut = 2000;

    public static void main(String[] args) {
        new CuratorLockTest().test();
    }


    private void test() {

        final InterProcessLock lock1 = new InterProcessMutex(getCuratorFramework(), rootNode);

        final InterProcessLock lock2 = new InterProcessMutex(getCuratorFramework(), rootNode);

        new Thread(new Runnable() {
            public void run() {
                try {
                    lock1.acquire();
                    System.out.println("线程1获取到锁");

                    lock1.acquire();
                    System.out.println("线程1再次获取到锁");

                    Thread.sleep(5 * 1000);

                    lock1.release();
                    System.out.println("线程1释放锁");

                    lock1.release();
                    System.out.println("线程1再释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                try {
                    lock2.acquire();
                    System.out.println("线程2获取到锁");

                    lock2.acquire();
                    System.out.println("线程2再次获取到锁");

                    Thread.sleep(5 * 1000);

                    lock2.release();
                    System.out.println("线程2释放锁");

                    lock2.release();
                    System.out.println("线程2再释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public CuratorFramework getCuratorFramework () {
        // 策略模式，初始时间3秒，重试3次
        RetryPolicy policy = new ExponentialBackoffRetry(3000, 3);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .connectionTimeoutMs(timeOut)
                .sessionTimeoutMs(timeOut)
                .retryPolicy(policy).build();

        // 启动客户端
        client.start();
        System.out.println("客户端启动成功");
        return client;
    }
}