package com.chinatower.bdm.modules.layer.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.modules.layer.service.ILayerService;
import com.chinatower.bdm.modules.layer.service.LayerPgService;
import com.chinatower.common.core.dto.bdm.LayerAddDTO;
import com.chinatower.common.core.dto.bdm.LayerResponseDTO;
import com.chinatower.common.core.enums.SysLayerCategoryEnums;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaController {

    private final LayerPgService layerPgService;
    private final ILayerService layerService;


    @KafkaListener(topics = {"geo_add_layer"}, containerFactory = "kafkaListenerContainerFactoryK2")
    public void radarReportConsumer(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String) kafkaMessage.get();
                log.info("接收数据geo_add_layer：{}  ", message);
                LayerAddDTO dto = JSON.parseObject(message, LayerAddDTO.class);
                try {
                    String layerId = layerPgService.addLayer(dto);
                    if (Boolean.TRUE.equals(dto.getGeoJsonFlag())){
                        LayerResponseDTO layer = layerService.selectLayerById(layerId, SysLayerCategoryEnums.ONE.getCode());
                        layerPgService.sendGeoJson(JSON.toJSONString(Result.data(layer)));
                    }
                }catch (CustomException e){
                    layerPgService.sendGeoJson(JSON.toJSONString(Result.fail(e.getMessage())));
                }catch (Exception e){
                    log.error("新增图层失败",e);
                    layerPgService.sendGeoJson(JSON.toJSONString(Result.fail(e.getMessage())));
                }
            }
        } catch (Exception e) {
            log.error("unionTargetJs 消费异常:", e);
        }
    }
}
