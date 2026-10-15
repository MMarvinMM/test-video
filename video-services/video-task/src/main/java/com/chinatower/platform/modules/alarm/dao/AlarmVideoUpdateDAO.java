package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.CaptureVideoVO;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.entity.vo.SecondRecogBackVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface AlarmVideoUpdateDAO {

    @DS("base")
    List<CaptureVideoVO> selectDownLoad();


    /**
     * 更新抓视频结果
     * @param captureVideoVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int updateVideoResult(CaptureVideoVO captureVideoVO);

    /**
     * 查询告警编号、视频url
     * @param orderIds
     * @param tableName
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<OrderInfoVO> getOrderAlarmInfo(@Param("param") List<String> orderIds,String tableName);
}
