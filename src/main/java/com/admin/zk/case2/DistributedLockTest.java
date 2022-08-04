package com.admin.zk.case2;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * @Autor liurong
 * @Data 2022/8/4
 * @Description:
 */
public class DistributedLockTest {

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        final DistributedLock lock1 = new DistributedLock();

        final DistributedLock lock2 = new DistributedLock();

        new Thread(new Runnable() {
            public void run() {
                try {
                    lock1.zkLock();
                    System.out.println("线程1启动获取到锁");
                    Thread.sleep(5 * 1000);

                    lock1.unZkLock();
                    System.out.println("线程1释放锁");
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                try {
                    lock2.zkLock();
                    System.out.println("线程2启动获取到锁");
                    Thread.sleep(5 * 1000);

                    lock2.unZkLock();
                    System.out.println("线程2释放锁");
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}