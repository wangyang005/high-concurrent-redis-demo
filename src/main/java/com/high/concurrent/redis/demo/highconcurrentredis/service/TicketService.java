package com.high.concurrent.redis.demo.highconcurrentredis.service;

import com.high.concurrent.redis.demo.highconcurrentredis.annotation.Cache;
import com.high.concurrent.redis.demo.highconcurrentredis.dao.TicketDao;
import com.high.concurrent.redis.demo.highconcurrentredis.entity.Ticket;
import com.high.concurrent.redis.demo.highconcurrentredis.entity.TicketExample;
import com.high.concurrent.redis.demo.highconcurrentredis.mapper.TicketMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author daituo
 * @Date 2019-3-25
 **/
@Service
@Slf4j
public class TicketService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TicketDao ticketDao;
    @Resource
    private TicketMapper ticketMapper;

    /** 用一个并发安全的map集合模拟不同票的锁*/
    private Map<String,Object> lock = new ConcurrentHashMap<>();

    public String findTicketNum(String ticketName) {

        /***************缓存使用策略1：先从redis缓存中获取余票信息，缓存中没有，则从数据库获取。************************
         **************缺点：当缓存瞬间失效时，高并发情况下，大量请求怼到数据库，可能造成数据库崩溃 *************************/
        ///** 先从redis缓存中获取余票信息 */
        //String ticketNum = stringRedisTemplate.opsForValue().get(ticketName);
        //if (!StringUtils.isEmpty(ticketNum)) {
        //    log.info("线程："+Thread.currentThread().getName()+" 从缓存中获取余票："+ticketNum);
        //    return ticketName;
        //}
        //
        ///** 缓存中没有，从数据库获取*/
        //ticketNum = ticketDao.findTicketNum(ticketName);
        //log.info("线程："+Thread.currentThread().getName()+" 从数据库中获取余票<<<<<<<<<<<<<<<<"+ticketNum);
        //stringRedisTemplate.opsForValue().set(ticketName,ticketNum,120,TimeUnit.SECONDS);



        /********缓存使用策略2：先从redis缓存中获取余票信息，缓存中没有，则加锁处理，保证只会查询一次数据库，其余请求从缓存中获取。
                              至少数据库不会因为缓存失效导致崩溃***
         **************缺点：高并发情况下，大量请求阻塞在锁处；锁的粒度太粗，假设场景：如果不同用户查询不同的车次余票，会阻塞在同一把锁 ***/
        //String ticketNum = stringRedisTemplate.opsForValue().get(ticketName);
        //if (!StringUtils.isEmpty(ticketNum)) {
        //    log.info("线程："+Thread.currentThread().getName()+" 从缓存中获取余票："+ticketNum);
        //    return ticketName;
        //}
        //
        //synchronized (this) {
        //    ticketNum = stringRedisTemplate.opsForValue().get(ticketName);
        //    if (!StringUtils.isEmpty(ticketNum)) {
        //        log.info("线程："+Thread.currentThread().getName()+" 从缓存中获取余票："+ticketNum);
        //        return ticketName;
        //    }
        //    ticketNum = ticketDao.findTicketNum(ticketName);
        //    log.info("线程："+Thread.currentThread().getName()+" 从数据库中获取余票<<<<<<<<<<<<<<<<"+ticketNum);
        //    stringRedisTemplate.opsForValue().set(ticketName,ticketNum,120,TimeUnit.SECONDS);
        //}


        /********缓存使用策略3：先从redis缓存中获取余票信息，缓存中没有，使用ConcurrentHashMap.putIfAbsent(K,V)模拟获取锁的操作
         *              优点：高并发情况下，不会造成线程阻塞；锁的细粒度控制***/
        String ticketNum = stringRedisTemplate.opsForValue().get(ticketName);
        if (!StringUtils.isEmpty(ticketNum)) {
            log.info("线程："+Thread.currentThread().getName()+" 从缓存中获取余票："+ticketNum);
            return ticketName;
        }
        /** 定义是否获取到锁*/
        boolean isLock = false;
        /**
         * ConcurrentHashMap提供了线程安全的操作 putIfAbsent(K,V)如果K不存在，则put成功，返回null,表示获取到锁，
         *      如果K已经存在，则put失败，返回V，表示获取锁失败
         * 高并发情况下，线程不会阻塞，只有一个线程能够获取到锁
         * 这样达到了，查询不同的票次，有不同的锁
         */
        isLock = lock.putIfAbsent(ticketName,"lock") == null;
        /** 如果获取到锁，则从数据库查询*/
        if (isLock) {
            ticketNum = ticketDao.findTicketNum(ticketName);
            stringRedisTemplate.opsForValue().set(ticketName,ticketNum,120,TimeUnit.SECONDS);
        } else {
            /** 对于没有获取到锁的线程，可以直接返回一个结果，也可以等待几秒后重试*/
            ticketNum = "0";
        }
        log.info("线程："+Thread.currentThread().getName()+" 从数据库中获取余票<<<<<<<<<<<<<<<<"+ticketNum);
        return ticketNum;
    }


    /**
     * 使用自定义缓存注解实现：先从redis缓存中获取票信息，缓存中没有，则从数据库获取的策略
     * @param ticketName 票名
     */
    @Cache(key = "'ticket_' + #ticketName") //这里使用SpringEL表达式：#ticketName当做占位符
    public Ticket findTicketByCache(String ticketName) {
        TicketExample ticketExample = new TicketExample();
        ticketExample.createCriteria().andNameEqualTo(ticketName);
        return ticketMapper.selectByExample(ticketExample).get(0);
    }
}
