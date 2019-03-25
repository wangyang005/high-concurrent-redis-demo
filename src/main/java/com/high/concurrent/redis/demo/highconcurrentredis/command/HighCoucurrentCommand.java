package com.high.concurrent.redis.demo.highconcurrentredis.command;

import com.high.concurrent.redis.demo.highconcurrentredis.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * 模拟高并发访问查询火车票余票
 * @Author daituo
 * @Date 2019-3-25
 **/
@Component
public class HighCoucurrentCommand implements CommandLineRunner {

    private int threadNum = 100;
    private static final String ticketName = "G299";

    /** 用countDownLatch模拟高并发*/
    private CountDownLatch countDownLatch = new CountDownLatch(threadNum);

    @Autowired
    private TicketService ticketService;

    @Override
    public void run(String... strings) {
        /** 模拟高并发访问查询火车票余票 */
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threads.length; i++) {
             Thread thread = new Thread(() -> {
                try {
                    countDownLatch.await();

                    /** 当2000个线程启动后，并发访问,先访问redis缓存，然后在访问数据库*/
                    ticketService.findTicketNum(ticketName);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            threads[i] = thread;
            threads[i].start();
            countDownLatch.countDown();
        }
    }
}
