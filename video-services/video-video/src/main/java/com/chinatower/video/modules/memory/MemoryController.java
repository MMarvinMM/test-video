package com.chinatower.video.modules.memory;

import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.video.api.entity.MemoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 功能描述: 用户记忆点缓存接口controller
 *
 * @param
 * @return
 * @author zhaozj
 * @date 2022/03/22 11:28
 * @requestID: R-ZBZB-
 */
@RestController
@RequestMapping("/video")
@Api(tags = "用户记忆点缓存接口")
@Slf4j
public class MemoryController {

    /**
     * 功能描述: 查看用户记忆点缓存信息通用接口
     *
     * @param memoryVO
     * @return Result
     * @author zhaozj
     * @date 2022/03/22 11:28
     * @requestID: R-ZBZB-_林业_监测预警
     */
    @PostMapping("/getUserMemoryInfo")
    @ApiOperation("查看用户记忆点缓存信息通用接口")
    public Result getUserMemoryInfo(@RequestBody @Validated MemoryVO memoryVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String userId = user.getUserId();
        String industryCode = user.getIndustryCode();

        List<MemoryVO> memList = new ArrayList<>();
        for (String memoryType : memoryVO.getMemoryTypeList()) {
            MemoryVO memory = new MemoryVO();
            String memoryValue = RedisUtil.get(Constants.REDIS_MEMORY + industryCode + memoryType
                    + Constants.REDIS_TYPE + userId, RedisDBType.PRIMARY);
            memory.setUserId(userId);
            memory.setMemoryType(memoryType);
            memory.setMemoryValue(memoryValue);
            memList.add(memory);
        }

        return Result.data(memList);
    }

    /**
     * 功能描述: 更新用户记忆点缓存信息通用接口
     *
     * @param  memoryVO
     * @return  Result
     * @author zhaozj
     * @date 2022/03/22 11:28
     * @requestID: R-ZBZB-_林业_监测预警
     */
    @PostMapping("/uptUserMemoryInfo")
    @ApiOperation("更新用户记忆点缓存信息通用接口")
    public Result uptUserMemoryInfo(@RequestBody MemoryVO memoryVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String userId = user.getUserId();
        String industryCode = user.getIndustryCode();
        RedisUtil.set(Constants.REDIS_MEMORY + industryCode + memoryVO.getMemoryType() + Constants.REDIS_TYPE + userId,
                memoryVO.getMemoryValue(), RedisDBType.PRIMARY);
        return Result.success("更新用户记忆点缓存信息成功！");
    }

    /**
     * 功能描述: 查看用户浏览历史信息接口
     *
     * @param
     * @return  Result
     * @author zhaozj
     * @date 2022/03/22 11:28
     * @requestID: R-ZBZB-_林业_监测预警
     */
    @PostMapping("/getUserSearchHistory")
    @ApiOperation("查看用户浏览历史信息接口")
    public Result getUserSearchHistory() {
        return Result.data(this.getHisList());
    }


    /**
     * 功能描述: 更新用户浏览历史信息接口
     *
     * @param  memoryVO
     * @return  Result
     * @author zhaozj
     * @date 2022/03/22 11:28
     * @requestID: R-ZBZB-_林业_监测预警
     */
    @PostMapping("/uptUserSearchHistory")
    @ApiOperation("更新用户浏览历史信息接口")
    public Result uptUserSearchHistory(@RequestBody MemoryVO memoryVO) {
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        LinkedList<String> hisList = this.getHisList();
        if (hisList.size() >= Constants.SEARCH_HISTORY_NUM) {
            hisList.removeFirst();
        }
        hisList.addLast(memoryVO.getMemoryValue());

        RedisUtil.set(Constants.REDIS_SEARCH_HISTORY + userId, hisList, RedisDBType.PRIMARY);
        return Result.success("更新用户浏览历史信息成功！");
    }


    /**
     * 功能描述: 清除用户浏览历史信息接口
     *
     * @param
     * @return  Result
     * @author zhaozj
     * @date 2022/03/22 11:28
     * @requestID: R-ZBZB-_林业_监测预警
     */
    @PostMapping("/delUserSearchHistory")
    @ApiOperation("清除用户浏览历史信息接口")
    public Result delUserSearchHistory() {
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        RedisUtil.delete(Constants.REDIS_SEARCH_HISTORY + userId, RedisDBType.PRIMARY);
        return Result.success("清除用户浏览历史信息成功！");
    }

    /**
     * 功能描述: 根据key删除redis缓存
     *
     * @param
     * @return  Result
     * @author hued
     * @date 2022年5月16日14:27:22
     * @requestID: R-ZBZB-_林业_监测预警
     */
    @GetMapping("/delRedisKey/{key}")
    @ApiOperation("根据key删除redis缓存")
    public Result delRedisKey(@PathVariable("key") String key) {
        if(!StringUtils.isEmpty(key)){
            if (RedisUtil.hasKey(key, RedisDBType.PRIMARY)) {
                RedisUtil.delete(key, RedisDBType.PRIMARY);
            }else{
                return Result.fail("请求key不存在！");
            }
        }else{
            return Result.fail("请求格式不正确！");
        }
        return Result.success(key + "清除成功！");
    }

    /**
     * 功能描述: 查看用户浏览历史信息
     *
     * @param
     * @return  Result
     * @author zhaozj
     * @date 2022/03/22 11:28
     * @requestID: R-ZBZB-_林业_监测预警
     */
    private LinkedList<String> getHisList (){
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();

        ArrayList arrayList = (ArrayList) JSONArray.parseArray(
                RedisUtil.get(Constants.REDIS_SEARCH_HISTORY + userId, RedisDBType.PRIMARY), String.class);
        if (CollectionUtils.isEmpty(arrayList)) {
            arrayList = new ArrayList();
        }
        LinkedList<String> hisList = new LinkedList<>(arrayList);
        return hisList;
    }
}
