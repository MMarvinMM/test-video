package com.chinatower.platform.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.AlarmFlowConfigDO;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowModel;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 14:28
 */
@Mapper
public interface IVmAlarmFlowConfigMapper extends BaseMapper<AlarmFlowConfigDO> {



    /**
     * 根据条件查询流程环节
     * @param model
     * @return
     */
    List<FlowModel> queryFlowLink(AlarmFlowConfigModel model);

}
