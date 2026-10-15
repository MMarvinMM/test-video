package com.chinatower.platform.framework.config;

import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysDictType;
import com.chinatower.platform.project.system.domain.SysConfig;
import com.chinatower.platform.project.system.mapper.SysConfigMapper;
import com.chinatower.platform.project.system.mapper.SysDictDataMapper;
import com.chinatower.platform.project.system.mapper.SysDictTypeMapper;
import com.chinatower.platform.project.tool.redis.domain.RedisReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
public class CacheRunner implements ApplicationRunner {
    @Resource
    SysDictDataMapper mapper;
    @Resource
    SysDictTypeMapper dictTypeMapper;
    @Resource
    SysConfigMapper sysConfigMapper;


    public static final Map<Long, SysDictData> DICT_DATA_MAP_DICT_CODE = new ConcurrentHashMap<>();
    public static final Map<String, List<SysDictData>> DICT_DATA_MAP_DICT_TYPE = new ConcurrentHashMap<>();
    public static final Map<String, SysDictType> DICT_TYPE_MAP_DICT_TYPE = new ConcurrentHashMap<>();
    public static final Map<Long, SysDictType> DICT_TYPE_MAP_DICT_CODE = new ConcurrentHashMap<>();
    public static final Map<String, SysConfig> SYS_CONFIG_MAP_CONFIG_KEY = new ConcurrentHashMap<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("缓存配置开始");
        List<SysDictData> dataList = mapper.selectAll();
        List<SysDictType> dictTypeList = dictTypeMapper.selectDictTypeAll();
        List<SysConfig> sysConfigList = sysConfigMapper.selectAll();
        fillMap(dataList, dictTypeList, sysConfigList, RedisReceiver.Msg.Table.SysDictType,
                RedisReceiver.Msg.Table.SysDictData, RedisReceiver.Msg.Table.SysConfig);
        log.info("缓存配置结束");
    }

    public static void fillMap(List<SysDictData> dataList, List<SysDictType> dictTypeList,
                               List<SysConfig> sysConfigList,
                               RedisReceiver.Msg.Table... tables) {
        log.info("cache fill map = {}", Arrays.toString(tables));
        if (tables == null) {
            return;
        }
        for (RedisReceiver.Msg.Table table : tables) {
            if (table == RedisReceiver.Msg.Table.SysDictData) {
                DICT_DATA_MAP_DICT_CODE.clear();
                DICT_DATA_MAP_DICT_TYPE.clear();
                for (SysDictData sysDictData : dataList) {
                    DICT_DATA_MAP_DICT_CODE.put(sysDictData.getDictCode(), sysDictData);
                    List<SysDictData> datas = DICT_DATA_MAP_DICT_TYPE.get(sysDictData.getDictType());
                    if (datas == null){
                        datas = new ArrayList<>();
                    }
                    datas.add(sysDictData);
                    DICT_DATA_MAP_DICT_TYPE.put(sysDictData.getDictType(), datas);
                }
            }
            if (table == RedisReceiver.Msg.Table.SysDictType) {
                DICT_TYPE_MAP_DICT_TYPE.clear();
                DICT_TYPE_MAP_DICT_CODE.clear();
                for (SysDictType sysDictType : dictTypeList) {
                    DICT_TYPE_MAP_DICT_TYPE.put(sysDictType.getDictType(), sysDictType);
                    DICT_TYPE_MAP_DICT_CODE.put(sysDictType.getDictId(), sysDictType);
                }
            }
            if (table == RedisReceiver.Msg.Table.SysConfig) {
                SYS_CONFIG_MAP_CONFIG_KEY.clear();
                for (SysConfig sysConfig : sysConfigList) {
                    SYS_CONFIG_MAP_CONFIG_KEY.put(sysConfig.getConfigKey(), sysConfig);
                }
            }
        }
    }
}
