package com.high.concurrent.redis.demo.highconcurrentredis.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Author daituo
 * @Date 2019-3-25
 **/
@Repository
public class TicketDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** 查询余票 */
    public String findTicketNum(String ticketName) {
        String sql = "SELECT num FROM t_ticket WHERE NAME = ?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, ticketName);
        return map.get("num").toString();
    }


}
