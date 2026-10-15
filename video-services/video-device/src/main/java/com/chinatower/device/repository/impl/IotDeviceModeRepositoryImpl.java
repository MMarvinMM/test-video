package com.chinatower.device.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.IotDeviceModeDO;
import com.chinatower.common.core.dto.IotDeviceModeDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.device.mapper.IotDeviceModeMapper;
import com.chinatower.device.repository.IIotDeviceModeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IotDeviceModeRepositoryImpl extends ServiceImpl<IotDeviceModeMapper, IotDeviceModeDO> implements IIotDeviceModeRepository {


    @Autowired
    private IotDeviceModeMapper iotDeviceModeMapper;

    @Override
    public Result<Object> queryIotDeviceModeList(IotDeviceModeDTO model) {
        Result<Object> result = null;
        QueryWrapper<IotDeviceModeDO> queryWrapper = new QueryWrapper();
        if (StringUtils.isNotEmpty(model.getMfgrCode())) {
            queryWrapper.lambda().eq(IotDeviceModeDO::getMfgrCode, model.getMfgrCode());
        }
        if (StringUtils.isNotEmpty(model.getDeviceType())) {
            queryWrapper.lambda().eq(IotDeviceModeDO::getDeviceType, model.getDeviceType());
        }

        if (StringUtils.isNotEmpty(model.getDeviceModelCode())) {
            queryWrapper.lambda().like(IotDeviceModeDO::getDeviceModelCode, model.getDeviceModelCode());
        }

        if (StringUtils.isNotEmpty(model.getCreateBy())) {
            queryWrapper.lambda().like(IotDeviceModeDO::getCreateBy, model.getCreateBy());
        }

        if (StringUtils.isNotEmpty(model.getUpdateBy())) {
            queryWrapper.lambda().like(IotDeviceModeDO::getUpdateBy, model.getUpdateBy());
        }

        if (model.getCreateTimeStart() != null) {
            queryWrapper.lambda().ge(IotDeviceModeDO::getCreateTime, model.getCreateTimeStart());
        }
        if (model.getCreateTimeEnd() != null) {
            queryWrapper.lambda().le(IotDeviceModeDO::getCreateTime, model.getCreateTimeEnd());
        }

        if (model.getUpdateTimeStart() != null) {
            queryWrapper.lambda().ge(IotDeviceModeDO::getUpdateTime, model.getUpdateTimeStart());
        }
        if (model.getUpdateTimeEnd() != null) {
            queryWrapper.lambda().le(IotDeviceModeDO::getUpdateTime, model.getUpdateTimeEnd());
        }
        if (StringUtils.isNotEmpty(model.getSortName())) {
            if (StringUtils.equals("createTime", model.getSortName())) {
                if (StringUtils.equals("desc", model.getSort())) {
                    queryWrapper.lambda().orderByDesc(IotDeviceModeDO::getCreateTime);
                } else {
                    queryWrapper.lambda().orderByAsc(IotDeviceModeDO::getCreateTime);
                }
            } else if (StringUtils.equals("updateTime", model.getSortName())) {
                if (StringUtils.equals("desc", model.getSort())) {
                    queryWrapper.lambda().orderByDesc(IotDeviceModeDO::getUpdateTime);
                } else {
                    queryWrapper.lambda().orderByAsc(IotDeviceModeDO::getUpdateTime);
                }
            }
        } else {
            queryWrapper.lambda().orderByDesc(IotDeviceModeDO::getCreateTime);
        }
        if (model.isEnablePage()) {
            //分页查询
            Page<IotDeviceModeDO> page = new Page<>(model.getPageNum(), model.getPageSize());
            IPage<IotDeviceModeDO> iPage = iotDeviceModeMapper.selectPage(page, queryWrapper);
            result = Result.data(iPage);
        } else {
            if (StringUtils.isNotEmpty(model.getIotModelIds())) {
                queryWrapper.lambda().in(IotDeviceModeDO::getIotModelId, model.getIotModelIds());
            }
            result = Result.data(iotDeviceModeMapper.selectList(queryWrapper));
        }
        return result;
    }
}
