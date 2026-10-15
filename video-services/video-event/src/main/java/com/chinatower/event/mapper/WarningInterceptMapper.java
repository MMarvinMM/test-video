package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.vo.AlarmClassTimeVO;
import com.chinatower.event.dataobject.WarningInterceptDO;
import com.chinatower.event.dto.InterceptDTO;
import com.chinatower.event.entity.dto.WarningInterceptDTO;
import com.chinatower.event.entity.vo.InterceptVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 告警拦截信息表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-27
 */
public interface WarningInterceptMapper extends BaseMapper<WarningInterceptDO> {

    /**
     * 回收站列表
     *
     * @param dto
     * @return
     */
    List<InterceptVO> getInterceptList(@Param("param") WarningInterceptDTO dto);

    /**
     * 更新图片
     * @param map
     * @return
     */
    int updateInterceptImg(Map map);

    /**
     * 详情
     * @param orderId
     * @param userId
     * @return
     */

    InterceptVO getInterceptInfo(String orderId,String userId);

    /**
     * 真警
     * @param dto
     * @return
     */
    int updateIntercept(InterceptDTO dto);

    /**
     * @Description 详情
     * @author wjd
     * @Date 17:18 2021/7/7
     * @Param [flowTemplateVO]
     **/
    FlowTemplateVO getTemplateInfo(FlowTemplateVO flowTemplateVO);

    /**
     * 根据租户id、设备编码查询组织编码集合
     * @param deviceCode
     * @param tenantId
     * @return
     */
    List<String> getOrganDevice(String deviceCode, String tenantId);

    /**
     * 获取设备所在部门处置角色人员，如果是多个人员，调度到部门；如果是一个人，调度到个人
     * @param orgIdList
     * @return
     */
    List<UsersVO> getUserByOrg(List<String> orgIdList);

    /**
     * 获取主键
     * @return
     */
    String getUUID();

    /**
     * @Description 获取流程时间信息
     * @author wjd
     * @Date 15:42 2021/7/7
     * @Param [alarmClassTimeVO]
     **/
    List<Map<String, Object>> getTimeLimitByInfo(AlarmClassTimeVO alarmClassTimeVO);

    /**
     * 批量保存压制数据
     * @param list
     * @return
     */
    int insertInterceptAlarm(List<ReceiveAlarmVO> list);


}
