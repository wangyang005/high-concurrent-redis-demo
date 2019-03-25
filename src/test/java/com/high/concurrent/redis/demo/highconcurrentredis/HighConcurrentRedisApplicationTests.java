package com.high.concurrent.redis.demo.highconcurrentredis;

import com.high.concurrent.redis.demo.highconcurrentredis.service.TicketService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HighConcurrentRedisApplicationTests {

    @Autowired
    private TicketService ticketService;

    @Test
    public void contextLoads() {
    }

}
