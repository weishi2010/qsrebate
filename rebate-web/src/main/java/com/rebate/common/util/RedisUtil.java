package com.rebate.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
    private static Logger logger = Logger.getLogger(RedisUtil.class);
    private static int MAX_ACTIVE = 1024;       // 最大连接数
    private static int MAX_IDLE = 200;          // 设置最大空闲数
    private static int MAX_WAIT = 10000;        // 最大连接时间
    private static boolean BORROW = true;         // 在borrow一个事例时是否提前进行validate操作
    private JedisPool pool = null;

    private Jedis jedis;

    private String masterAddrs;
    private int timeout = 60 * 60 * 24;

    public RedisUtil() {
    }

    public void init() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_ACTIVE);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(BORROW);

        if (StringUtils.isBlank(masterAddrs)) {
            throw new RuntimeException("redis masterAddrs is must be seted!");
        }
        try {
            String[] array = masterAddrs.split(":");
            String ip = array[0];
            int port = Integer.parseInt(array[1]);
            pool = new JedisPool(config, ip, port, timeout);

            if (pool != null) {
                jedis = pool.getResource();
            } else {
                logger.error("获取不到连接池连接!");
            }
        } catch (Exception e) {
            logger.error("创建连接池异常!",e);
        }
    }

    /**
     * @param @param  key
     * @param @param  seconds
     * @param @return
     * @return boolean 返回类型
     * @Description:设置失效时间
     */
    public void disableTime(String key, int seconds) {
        try {
            jedis.expire(key, seconds);

        } catch (Exception e) {
            logger.debug("设置失效失败.");
        }
    }


    /**
     * @param @param  key
     * @param @param  obj
     * @param @return
     * @return boolean 返回类型
     * @Description:插入对象
     */
    public boolean set(String key, String value) {

        try {
            String code = jedis.set(key, value);
            if (code.equals("ok")) {
                return true;
            }
        } catch (Exception e) {
            logger.debug("插入数据有异常.");
            return false;
        }
        return false;
    }

    public boolean set(String key, String value,int timeout) {

        try {
            String code = jedis.set(key, value);
            jedis.expire(key,timeout);
            if (code.equals("ok")) {
                return true;
            }
        } catch (Exception e) {
            logger.debug("插入数据有异常.");
            return false;
        }
        return false;
    }

    public Long incr(String key) {

        try {
            return jedis.incr(key);
        } catch (Exception e) {
            logger.debug("incr error!");
            return 0l;
        }
    }

    public String get(String key) {
        try {
            return jedis.get(key);
        } catch (Exception e) {
            throw new RuntimeException("Redis get error!");
        }
    }

    /**
     * @param @param  key
     * @param @return
     * @return boolean 返回类型
     * @Description:删除key
     */
    public boolean delKey(String key) {
        try {
            Long code = jedis.del(key);
            if (code > 1) {
                return true;
            }
        } catch (Exception e) {
            logger.debug("删除key异常.");
            return false;
        }
        return false;
    }

    public String getMasterAddrs() {
        return masterAddrs;
    }

    public void setMasterAddrs(String masterAddrs) {
        this.masterAddrs = masterAddrs;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}