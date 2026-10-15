package com.chinatower.outdata.api.transfer.api;


import com.alibaba.fastjson.JSON;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.entity.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Slf4j
@Component
public class ITransferService {

    @Autowired
    private NetConnectUtils connectUtils;


    @ApiOperation(value = "获取海图瓦片接口")
    @GetMapping("/chart/fishArea/getTile")
    public Result<byte[]> getTile(@RequestParam("node") String node,
                                  @RequestParam("userName")String userName,
                                  @RequestParam("level")Integer level,
                                  @RequestParam("col")Integer col,
                                  @RequestParam("row")Integer row,
                                  @RequestParam("skipCode")String skipCode) {

        log.info("getTile node:{}, userName:{}, level:{}, col:{}, row:{} skipCode:{}", node, userName, level, col, row, skipCode);
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("user_name", userName);
            paramMap.put("level", level);
            paramMap.put("row", row);
            paramMap.put("col", col);
            if (Objects.nonNull(skipCode)) {
                paramMap.put("skipcode", skipCode);
            }

            NetConnectUtils.RequestParamter<Object> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("获取海图瓦片接口入参{}", JSON.toJSONString(paramMap));
            //设置请求参数
            parameter.setBody(paramMap);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_TILE_RADAR);
            path.setNode(node);
            //设置地址
            parameter.setPath(path);
            ResponseBody responseBody = connectUtils.get4Response(parameter, "/chart/{node}/gettile");
            log.info("获取海图瓦片接口返回参数{}", JSON.toJSONString(responseBody));
            return Result.data(responseBody.bytes());
        } catch (Exception e) {
            log.info("雷达获取海图瓦片接口调用异常:", e);
            return Result.fail("雷达获取海图瓦片接口调用异常:" + e.getMessage());
        }
    }


    @ApiOperation(value = "获取渔区瓦片接口")
    @GetMapping("/chart/fishArea/getExtendTile")
    public Result<byte[]> getExtendTile(@RequestParam("tileType") String tileType,
                                        @RequestParam("userName")String userName,
                                        @RequestParam("level")Integer level,
                                        @RequestParam("col")Integer col,
                                        @RequestParam("row")Integer row) {
        log.info("getExtendTile tileType:{}, userName:{}, level:{}, col:{}, row:{}", tileType, userName, level, col, row);
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("user_name", userName);
            paramMap.put("level", level);
            paramMap.put("row", row);
            paramMap.put("col", col);
            paramMap.put("tile_type", tileType);

            NetConnectUtils.RequestParamter<Object> parameter = new NetConnectUtils.RequestParamter<>();
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            log.info("获取渔区瓦片接口入参{}", JSON.toJSONString(paramMap));
            //设置请求参数
            parameter.setBody(paramMap);
            //获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_EXTEND_TILE_RADAR);
            //设置地址
            parameter.setPath(path);
            ResponseBody responseBody = connectUtils.get4Response(parameter, "/chart/fisharea/getextendtile");
            log.info("获取渔区瓦片接口返回参数{}", JSON.toJSONString(responseBody));
            return Result.data(responseBody.bytes());
        } catch (Exception e) {
            log.info("雷达获取渔区瓦片接口调用异常:", e);
            return Result.fail("雷达获取渔区瓦片接口调用异常:" + e.getMessage());
        }
    }
}
