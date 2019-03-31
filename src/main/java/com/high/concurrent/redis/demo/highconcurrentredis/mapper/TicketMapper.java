package com.high.concurrent.redis.demo.highconcurrentredis.mapper;

import com.high.concurrent.redis.demo.highconcurrentredis.entity.Ticket;
import com.high.concurrent.redis.demo.highconcurrentredis.entity.TicketExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
* 请添加描述
* @author FGVTH
* @time 2019-03-31
*/
public interface TicketMapper {
    long countByExample(TicketExample example);

    int deleteByExample(TicketExample example);

    int insert(Ticket record);

    int insertSelective(Ticket record);

    List<Ticket> selectByExample(TicketExample example);

    int updateByExampleSelective(@Param("record") Ticket record, @Param("example") TicketExample example);

    int updateByExample(@Param("record") Ticket record, @Param("example") TicketExample example);
}