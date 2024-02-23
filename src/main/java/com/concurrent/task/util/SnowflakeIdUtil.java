package com.concurrent.task.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author : kenny
 * @since : 2024/2/20
 **/
public class SnowflakeIdUtil {
    private static final SnowflakeIdUtil instance = new SnowflakeIdUtil();
    private Snowflake snowflake;

    public static long nextId() {
        return instance.snowflakeId();
    }

    private long snowflakeId() {
        return snowflake.nextId();
    }

    private SnowflakeIdUtil() {
        genSnowFlake();
    }

    private void genSnowFlake() {
        String ipStr = NetUtil.getLocalhostStr();
        long ip = 0;
        if(StrUtil.isNotBlank(ipStr)) {
            ip = NetUtil.ipv4ToLong(ipStr);
        }
        long workerId = ip & 31;
        long dataCenterId = (ip >> 5) & 31;
        snowflake = IdUtil.createSnowflake(workerId, dataCenterId);
    }
}
