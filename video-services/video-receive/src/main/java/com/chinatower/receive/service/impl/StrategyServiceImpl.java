package com.chinatower.receive.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.event.constants.Constants;
import com.chinatower.receive.service.StrategyService;
import com.chinatower.receive.vo.entity.SuppressPointPTVO;
import com.chinatower.receive.vo.entity.VmAlarmAreaSuppressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 17:02
 */
@Service
@Slf4j
public class StrategyServiceImpl implements StrategyService {

    @Override
    public void groupPT(VmAlarmAreaSuppressVO areaTemp) {
        if(areaTemp.getPMin()!=null && !"".equals(areaTemp.getPMin()) && areaTemp.getPMax()!=null && !"".equals(areaTemp.getPMax())){
            List<SuppressPointPTVO> list =new ArrayList<>();
            SuppressPointPTVO pointVo1 = new SuppressPointPTVO();
            pointVo1.setSuppressPointId(Constants.ONE);
            pointVo1.setPointP(areaTemp.getPMin());
            if(areaTemp.getTMax()!=null && !"".equals(areaTemp.getTMax()) ){
                pointVo1.setPointT(areaTemp.getTMax());
            }else{
                pointVo1.setPointT(String.valueOf(Constants.T_JUST_90));
            }
            list.add(pointVo1);
            SuppressPointPTVO pointVo2 = new SuppressPointPTVO();
            pointVo2.setSuppressPointId(Constants.TWO);
            pointVo2.setPointP(areaTemp.getPMin());
            if(areaTemp.getTMin()!=null && !"".equals(areaTemp.getTMin()) ){
                pointVo2.setPointT(areaTemp.getTMin());
            }else{
                pointVo2.setPointT(String.valueOf(Constants.T_LOST_90));
            }
            list.add(pointVo2);
            SuppressPointPTVO pointVo3 = new SuppressPointPTVO();
            pointVo3.setSuppressPointId(Constants.THREE);
            pointVo3.setPointP(areaTemp.getPMax());
            if(areaTemp.getTMin()!=null && !"".equals(areaTemp.getTMin()) ){
                pointVo3.setPointT(areaTemp.getTMin());
            }else{
                pointVo3.setPointT(String.valueOf(Constants.T_LOST_90));
            }
            list.add(pointVo3);
            SuppressPointPTVO pointVo4 = new SuppressPointPTVO();
            pointVo4.setSuppressPointId(Constants.FOUR);
            pointVo4.setPointP(areaTemp.getPMax());
            if(areaTemp.getTMax()!=null && !"".equals(areaTemp.getTMax()) ){
                pointVo4.setPointT(areaTemp.getTMax());
            }else{
                pointVo4.setPointT(String.valueOf(Constants.T_JUST_90));
            }
            list.add(pointVo4);
            SuppressPointPTVO pointVo5 = new SuppressPointPTVO();
            pointVo5.setSuppressPointId(Constants.FIVE);
            pointVo5.setPointP(areaTemp.getPMin());
            if(areaTemp.getTMax()!=null && !"".equals(areaTemp.getTMax()) ){
                pointVo5.setPointT(areaTemp.getTMax());
            }else{
                pointVo5.setPointT(String.valueOf(Constants.T_JUST_90));
            }
            list.add(pointVo5);
            String jsonPoint = JSONObject.toJSONString(list);
            areaTemp.setPointsPt(jsonPoint);
        }
    }

}
