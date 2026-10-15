package com.chinatower.platform.project.tool.redis.domain;

import com.alibaba.fastjson.JSON;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysDictType;
import com.chinatower.platform.project.system.domain.SysConfig;
import com.chinatower.platform.project.system.mapper.SysConfigMapper;
import com.chinatower.platform.project.system.mapper.SysDictDataMapper;
import com.chinatower.platform.project.system.mapper.SysDictTypeMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static com.chinatower.platform.framework.config.CacheRunner.fillMap;

@Slf4j
@Component
public class RedisReceiver {
    @Resource
    SysDictDataMapper mapper;
    @Resource
    SysDictTypeMapper dictTypeMapper;
    @Resource
    SysConfigMapper sysConfigMapper;

    public void receiveMessage(String message) {
        log.info("subscribe receive message:{}", message);

        Msg msg = JSON.parseObject(message, Msg.class);

        List<SysDictData> dataList = Collections.emptyList();
        if (msg.table == Msg.Table.SysDictData) {
            dataList = mapper.selectAll();
        }

        List<SysDictType> dictTypeList = Collections.emptyList();
        if (msg.table == Msg.Table.SysDictType) {
            dictTypeList = dictTypeMapper.selectDictTypeAll();
        }

        List<SysConfig> sysConfigList = Collections.emptyList();
        if (msg.table == Msg.Table.SysConfig) {
            sysConfigList = sysConfigMapper.selectAll();
        }

        fillMap(dataList, dictTypeList, sysConfigList, msg.table);
    }

    @Data
    public static class Msg {
        private Table table;

        public enum Table {
            SysDictData,
            SysDictType,
            SysConfig,
            ;

//            public abstract Supplier<List> supplier();
        }
    }

}