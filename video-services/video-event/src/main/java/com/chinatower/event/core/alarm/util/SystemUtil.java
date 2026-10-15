package com.chinatower.event.core.alarm.util;

import com.chinatower.common.entity.Result;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("SystemUtil")
@Slf4j
public class SystemUtil {

    @Autowired
    private SystemFeignClient staticSystemFeignClient;

    public List<SysDictDataResponse> getEnum(String enumTypeId){
        Result<List<SysDictDataResponse>> res = staticSystemFeignClient.dictDataType(enumTypeId);
        List<SysDictDataResponse> dictList = res.getData();
        return dictList;
    }
}
