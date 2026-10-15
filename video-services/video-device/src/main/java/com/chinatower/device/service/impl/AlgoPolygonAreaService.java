package com.chinatower.device.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.device.mapper.AlgorithmConfigMapper;
import com.chinatower.device.mapper.PolygonPatchAlgoAreaMapper;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.utils.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName AlgoPolygonAreaService
 * @Description 算法的图斑识别区域
 * @Author zhaozj
 * @Date 2022/4/12
 **/
@Service
@Slf4j
public class AlgoPolygonAreaService {
    @Resource
    AlgorithmConfigMapper algorithmConfigMapper;
    @Autowired
    PolygonPatchAlgoAreaMapper polygonMapper;

    /**
     * 功能描述：对已绑定算法的图斑识别区域修改处理方法
     * @param patchResList
     * @author: zhaozj
     * @date: 2022/4/11
     * @return
     */
    public void handlePolygonAreaBindAlgo(List<PolygonPatchConfig> patchResList) {
        try {
            log.info("对已绑定算法的图斑识别区域修改处理方法---handlePolygonAreaBindAlgo入参：" +
                    JSON.toJSONString(patchResList));
            List<String> polygonIdList = new ArrayList<>();
            for (PolygonPatchConfig config : patchResList) {
                polygonIdList.add(config.getPolygonId());
            }

            PolygonPatchAlgoAreaVO polygonPatchAlgoAreaVO = new PolygonPatchAlgoAreaVO();
            polygonPatchAlgoAreaVO.setPolygonIdList(polygonIdList);
            //根据polygon_id、monitor_area_id查询出vm_polygon_patch_algo_area_rela所有配置该图斑的算法configId
            List<PolygonPatchAlgoAreaDO> algoPolyList = polygonMapper.getPolygonPatchAlgoAreaList(polygonPatchAlgoAreaVO);
            if (!CollectionUtils.isEmpty(algoPolyList)) {
                List<AlgorithmConfigVO> configList = new ArrayList<>();
                for (PolygonPatchAlgoAreaDO vo :algoPolyList){
                    AlgorithmConfigVO algorithmConfigVO = new AlgorithmConfigVO();
                    algorithmConfigVO.setConfigId(vo.getConfigId());
                    configList.add(algorithmConfigVO);
                }
                //根据configList查出对应的算法信息集合，然后更新其中points识别区域数据，并同步算法仓
                List<AlgorithmAreaVO> areas = algorithmConfigMapper.getAlgorithmAreaRow2("",configList);
                List<AlgorithmAreaVO> uptList = new ArrayList<>();
                for (AlgorithmAreaVO area : areas) {
                    if (StringUtils.isNotEmpty(area.getPoints())) {
                        List<AlgorithmMonitorAreaVO> monitorArea = JSON.parseArray(area.getPoints(),
                                AlgorithmMonitorAreaVO.class);
                        //识别区域ID相同的数据，将新的区域点替换旧的区域点
                        List<AlgorithmMonitorAreaVO> pointsList = monitorArea.stream().map(item -> {
                            patchResList.stream().filter(patch ->
                                    Objects.equals(item.getMonitorAreaId(),patch.getMonitorAreaId())).forEach(patch-> {
                                item.setMoniPointList(patch.getPolygonPointList());
                            });
                            return item;
                        }).collect(Collectors.toList());

                        ObjectMapper mapper = new ObjectMapper();
                        area.setPoints(mapper.writeValueAsString(pointsList));
                        area.setAlgorithmStatus(AlgorithmAreaVO.ALGORITHMLOADINGSTATUS);
                        area.setModifier(SecurityUtils.getOperator());
                        uptList.add(area);
                    }
                }

                log.info("handlePolygonAreaBindAlgo需要更新识别区域的数据:{}", JSON.toJSONString(uptList));
                if (!uptList.isEmpty()) {
                    //批量更新识别区域与状态
                    algorithmConfigMapper.updAlgoAreaBatch(uptList);
                }
            }
        } catch (Exception e) {
            log.error("对已绑定算法的图斑识别区域修改处理方法异常", e);
        }
    }


}




