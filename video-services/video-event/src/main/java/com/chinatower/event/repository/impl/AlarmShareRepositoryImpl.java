package com.chinatower.event.repository.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;

import com.chinatower.event.entity.dto.AddShareDTO;
import com.chinatower.event.entity.dto.AlarmShareDTO;
import com.chinatower.event.mapper.AlarmShareMapper;
import com.chinatower.event.repository.IAlarmShareService;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * <p>
 * 告警类型标签表 服务实现类
 * </p>
 *
 * @author chenmf
 * @since 2022-01-26
 */
@Service
@Slf4j
public class AlarmShareRepositoryImpl implements IAlarmShareService {

    @Autowired
    private AlarmShareMapper alarmShareMapper;

    /**
     * 查询告警标签数量
     *
     * @return
     * @author yhb
     * @date 2022/4/26
     * @version
     */
    @Override
    public AlarmShareDTO addAlarmShare(AddShareDTO addShareDTO) {
        log.info("告警分享新增入参：{}", JSON.toJSONString(addShareDTO));
        //获取操作人
        String operator = SecurityUtils.getOperator();
        AlarmShareDTO resultShare = new AlarmShareDTO();
        String shareId = IdUtil.simpleUUID().toUpperCase(Locale.ROOT).substring(0,8);
        addShareDTO.setShareId(shareId);
        addShareDTO.setCreator(operator);
        alarmShareMapper.addAlarmShare(addShareDTO);
        BeanUtils.copyProperties(addShareDTO,resultShare);
        return resultShare;
    }
}
