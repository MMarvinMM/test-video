package com.chinatower.event.api.feign.factory;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.domain.AlarmSoundDO;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.api.feign.client.IAlarmTypeService;
import com.chinatower.event.entity.dto.AlarmReasonQueryDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.dto.AlarmTypeTagInfoDTO;
import com.chinatower.event.entity.vo.AlarmReasonVO;
import com.chinatower.event.entity.vo.AlarmSourceSubTypeVO;
import com.chinatower.event.entity.vo.AlarmTypeTagInfoVO;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 告警类型相关降级接口
 */
@Component
public class AlarmTypeFallbackFactory implements FallbackFactory<IAlarmTypeService> {

    private static final Logger log = LoggerFactory.getLogger(AlarmTypeFallbackFactory.class);

    @Override
    public IAlarmTypeService create(Throwable throwable) {
        log.error("告警类型相关接口调用失败:", throwable);

        return new IAlarmTypeService() {
            @Override
            public Result<List<AlarmSourceSubTypeVO>> findAlarmSourceSubType(AlarmSourceSubTypeDTO req) {
                return Result.fail("查询告警来源及告警类型失败:" + throwable.getMessage());
            }

            @Override
            public Result<AlarmSoundDO> selectById(Long id) {
                return Result.fail("根据告警声音id查询告警声音信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<AlarmReasonVO>> selectAlarmReasonList(AlarmReasonQueryDTO param) {
                return Result.fail("告警原因列表查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<AlarmTypeTagInfoVO>> selectRelateTagInfoByAlarmType(AlarmTypeTagInfoDTO req) {
                return Result.fail("客户告警标签关联信息查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<AlarmTypeTagInfoVO>> selectTagInfoByTenant() {
                return Result.fail("告警类型标签信息查询失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo findList(AlarmTypeClassDTO dto) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }
        };
    }
}
