package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.api.ITradeExportWarningService;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.dto.HandleAlarmImgDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.vo.OrderCustomerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;


/**
 * @Description AI生成告警后原图/框图复合油管线图处理
 * @Author cui.shijie
 * @Date 2023/10/23
 * @Version release_v1.6.20
 **/
@Slf4j
@Component
@Scope("prototype")
public class AISpecialHandler extends AbstractHandler {

    @Resource
    private ITradeExportWarningService iTradeExportWarningService;

    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("AI生成告警后原图/框图复合油管线图处理入参：{}", JSON.toJSONString(receiveAlarmDTO));
        // 批量保存告警信息
        try {
            if ("1".equals(receiveAlarmDTO.getReceiveAlarmVO().getAlarmSource())){
                List<ReceiveAlarmVO> alarms = receiveAlarmDTO.getList();
                List<ReceiveAlarmVO> operations = receiveAlarmDTO.getOperations();
                List<ReceiveAlarmVO> intercepts = receiveAlarmDTO.getIntercepts();
                List<OrderCustomerVO> customers = receiveAlarmDTO.getCustomers();
                String pipelines ="";
                if (!CollectionUtils.isEmpty(customers)) {
                    pipelines = customers.get(0).getPipelines();
                }
                if (!StringUtils.isEmpty(pipelines)){
                    HandleAlarmImgDTO handleAlarmImgDTO = new HandleAlarmImgDTO();
                    handleAlarmImgDTO.setPipelines(pipelines);
                    handleAlarmImgDTO.setListRemark(false);
                    handleAlarmImgDTO.setInterceptsRemark(false);
                    handleAlarmImgDTO.setOperationsRemark(false);
                    if (!CollectionUtils.isEmpty(alarms)) {
                        handleAlarmImgDTO.setListRemark(true);
                        this.setHandleImg(alarms,handleAlarmImgDTO);
                    }
                    if (!CollectionUtils.isEmpty(operations)) {
                        handleAlarmImgDTO.setOperationsRemark(true);
                        this.setHandleImg(operations,handleAlarmImgDTO);
                    }
                    if (!CollectionUtils.isEmpty(intercepts)) {
                        handleAlarmImgDTO.setInterceptsRemark(true);
                        this.setHandleImg(intercepts,handleAlarmImgDTO);
                    }
                    log.info("开始调用api-export AI生成告警后原图/框图复合油管线图处理：{}", JSON.toJSONString(handleAlarmImgDTO));
                    iTradeExportWarningService.handleReceiveAlarmImg(handleAlarmImgDTO);
                }
            }
        }
        catch (Exception e) {
            log.error("AI生成告警后原图/框图复合油管线图失败!", e);
        }
        return true;
    }
    public void setHandleImg(List<ReceiveAlarmVO> list,HandleAlarmImgDTO handleAlarmImgDTO){
        handleAlarmImgDTO.setFileImgUrlSrc(list.get(0).getOriginalPictureUrl());
        handleAlarmImgDTO.setFileImgUrlIcon(list.get(0).getAlarmPictureUrl());
        handleAlarmImgDTO.setWarningOrderId(list.get(0).getWarningOrderId());
    }
}
