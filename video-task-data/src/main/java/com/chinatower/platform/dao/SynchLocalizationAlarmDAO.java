package com.chinatower.platform.dao;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.dto.OrderFileDTO;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.event.entity.dto.OrderFileDO;
import com.chinatower.event.entity.vo.AlarmTypeVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.InterceptOrderEntity;
import com.chinatower.platform.modules.OperateOrderEntity;
import com.chinatower.platform.modules.OrderFlowRecordEntity;
import com.chinatower.platform.modules.WarningOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
@DS(EbikeDataBaseConstant.SLAVE)
public interface SynchLocalizationAlarmDAO {

    List<WarningOrderEntity> queryOrderList(@Param("tableName") String tableName,@Param("lastTableName") String lastTableName, @Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("offset") int offset, @Param("pageSize") int pageSize);

    List<OrderFlowRecordEntity> queryFlowRecordList(@Param("tableName") String tableName, @Param("orderIdList") List<String> orderIdList);

    List<OperateOrderEntity> queryOperateOrderList(@Param("date") Date date,@Param("offset") int offset, @Param("pageSize") int pageSize);

    List<InterceptOrderEntity> queryInterceptOrderList(@Param("date") Date date,@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 获取告警流程配置信息
     * @return
     */
    List<Map> getAlarmFlowConfigList();

    /**
     * 获取研判、调度、核实的附件
     * @return
     */
    List<OrderFileDO> getOrderFileList();

    /**
     * 获取处置附件图片
     * @return
     */
    List<Map> getHandlePhotos();

    /**
     * 获取研判、调度、核实环节水印附件
     * @return
     */
    List<OrderFileDO> getOrderFilesAll();

    /**
     * 获取处置环节水印附件
     * @return
     */
    List<OrderFileDO> getProcessOrderPhotosAll();

    /**
     * 查询第三方告警来源
     * @return
     */
    List<Map> getThirdPartyAlarmSource();

    @DS(EbikeDataBaseConstant.BASE)
    List<Map> queryAlgorithmConfig();
}
