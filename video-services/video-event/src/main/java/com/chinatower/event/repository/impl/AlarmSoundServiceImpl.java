package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.AlarmSoundDO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.AlarmSoundDTO;
import com.chinatower.event.mapper.AlarmSoundMapper;
import com.chinatower.event.repository.IAlarmSoundService;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.model.dto.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@DS(EbikeDataBaseConstant.BASE)
public class AlarmSoundServiceImpl extends ServiceImpl<AlarmSoundMapper, AlarmSoundDO> implements IAlarmSoundService {
    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Override
    public Result<IPage<AlarmSoundDO>> queryAlarmSoundList(AlarmSoundDTO model) {
        QueryWrapper<AlarmSoundDO> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(model.getAlarmSoundName())) {
            queryWrapper.lambda().like(AlarmSoundDO::getAlarmSoundName, model.getAlarmSoundName());
        }
        if (StringUtils.isNotEmpty(model.getDocName())) {
            queryWrapper.lambda().like(AlarmSoundDO::getDocName, model.getDocName());
        }
        queryWrapper.lambda().and(wq -> wq
                .eq(AlarmSoundDO::getTenantId, model.getTenantId())
                .eq(AlarmSoundDO::getIndustryCode,model.getIndustryCode())
                .or().eq(AlarmSoundDO::getSoundType, EnumUtil.AlarmSoundType.DEFAULT.getCode()));

        if (StringUtils.isNotEmpty(model.getSortName())) {
            if (StringUtils.equals("playTime", model.getSortName())) {
                if (StringUtils.equals("desc", model.getSort())) {
                    queryWrapper.lambda().orderByDesc(AlarmSoundDO::getPlayTime);
                } else {
                    queryWrapper.lambda().orderByAsc(AlarmSoundDO::getPlayTime);
                }
            } else if (StringUtils.equals("createTime", model.getSortName())) {
                if (StringUtils.equals("desc", model.getSort())) {
                    queryWrapper.lambda().orderByDesc(AlarmSoundDO::getCreateTime);
                } else {
                    queryWrapper.lambda().orderByAsc(AlarmSoundDO::getCreateTime);
                }
            } else if (StringUtils.equals("updateTime", model.getSortName())) {
                if (StringUtils.equals("desc", model.getSort())) {
                    queryWrapper.lambda().orderByDesc(AlarmSoundDO::getUpdateTime);
                } else {
                    queryWrapper.lambda().orderByAsc(AlarmSoundDO::getUpdateTime);
                }
            }
        } else {
            queryWrapper.lambda().orderByAsc(AlarmSoundDO::getSoundType).orderByDesc(AlarmSoundDO::getCreateTime);
        }
        //分页查询
        Page<AlarmSoundDO> page = new Page<>(model.getPageNum(), model.getPageSize());
        IPage<AlarmSoundDO> iPage = this.baseMapper.selectPage(page, queryWrapper);
        List<String> urlList = iPage.getRecords().stream().map(AlarmSoundDO::getDocUrl).filter(Objects::nonNull).collect(Collectors.toList());
        Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(urlList);

        //查询该客户下是否已有启用
        QueryWrapper<AlarmSoundDO> queryWrapperType = new QueryWrapper<>();
        queryWrapperType.lambda()
                .eq(AlarmSoundDO::getTenantId, model.getTenantId())
                .eq(AlarmSoundDO::getIndustryCode,model.getIndustryCode())
                .eq(AlarmSoundDO::getStatusCd, EnumUtil.AlarmSoundStatusCd.enable.getCode());
        Integer count = this.baseMapper.selectCount(queryWrapperType);

        iPage.getRecords().forEach(alarmSoundDO -> {
            if (EnumUtil.AlarmSoundType.DEFAULT.getCode().equals(alarmSoundDO.getSoundType())) {
                alarmSoundDO.setStatusCd(EnumUtil.AlarmSoundStatusCd.enable.getCode());
//                QueryWrapper<AlarmSoundDO> queryWrapperType = new QueryWrapper<>();
//                queryWrapperType.lambda()
//                        .eq(AlarmSoundDO::getTenantId, model.getTenantId())
//                        .eq(AlarmSoundDO::getIndustryCode,model.getIndustryCode())
//                        .eq(AlarmSoundDO::getStatusCd, EnumUtil.AlarmSoundStatusCd.enable.getCode());
                if (count > 0) {
                    alarmSoundDO.setStatusCd(EnumUtil.AlarmSoundStatusCd.Disable.getCode());
                }
            }
            alarmSoundDO.setDocUrl(stringStringMap.get(alarmSoundDO.getDocUrl()));
        });
        return Result.data(iPage);
    }

    @Override
    public Result<String> modifyAlarmSound(AlarmSoundDO model) {
        AlarmSoundDO alarmSoundDO = this.baseMapper.selectById(model.getAlarmSoundId());
        if (alarmSoundDO == null) {
            return Result.fail(Constants.SOUND_INFORMATION_DOES_NOT_EXIST);
        }
        if (EnumUtil.AlarmSoundType.DEFAULT.getCode().equals(alarmSoundDO.getSoundType())) {
            return Result.fail("默认声音不可修改");
        }
        UpdateWrapper<AlarmSoundDO> updateWrapper = new UpdateWrapper<>();
        // 不修改租户id
        model.setTenantId(null);
        model.setUpdateTime(DateUtils.getNowDate());
        updateWrapper.lambda().eq(AlarmSoundDO::getAlarmSoundId, model.getAlarmSoundId());
        this.baseMapper.update(model, updateWrapper);
        return Result.success("修改成功");
    }

    @Override
    public Result<AlarmSoundDO> addAlarmSound(AlarmSoundDO model) {
        model.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(model.getStatusCd())) {
            model.setStatusCd(EnumUtil.AlarmSoundStatusCd.Disable.getCode());
        }
        if (StringUtils.isEmpty(model.getSoundType())) {
            model.setSoundType(EnumUtil.AlarmSoundType.URGENT.getCode());
        }
        this.baseMapper.insert(model);
        return Result.success("添加成功");
    }

    @Override
    public Result<String> deleteAlarmSound(Long alarmSoundId) {
        //查询获取设别编码
        AlarmSoundDO model = this.baseMapper.selectById(alarmSoundId);
        if (model == null) {
            return Result.fail(Constants.SOUND_INFORMATION_DOES_NOT_EXIST);
        }
        if (EnumUtil.AlarmSoundType.DEFAULT.getCode().equals(model.getSoundType())) {
            return Result.fail("默认声音不可删除");
        }
        if (EnumUtil.AlarmSoundStatusCd.enable.getCode().equals(model.getStatusCd())) {
            return Result.fail("已启用告警声音不可删除");
        }
        this.baseMapper.deleteById(alarmSoundId);
        return Result.success("删除成功");
    }

    @Override
    public Result<AlarmSoundDO> selectByAlarmSoundId(Long alarmSoundId, LoginUser loginUser) {
        AlarmSoundDO model = this.baseMapper.selectById(alarmSoundId);
        if (model == null) {
            return Result.fail(Constants.SOUND_INFORMATION_DOES_NOT_EXIST);
        }
        if (EnumUtil.AlarmSoundType.DEFAULT.getCode().equals(model.getSoundType())) {
            //查询该客户下是否已有启用
            model.setStatusCd(EnumUtil.AlarmSoundStatusCd.enable.getCode());
            QueryWrapper<AlarmSoundDO> queryWrapperType = new QueryWrapper<>();
            queryWrapperType.lambda()
                    .eq(AlarmSoundDO::getTenantId, loginUser.getUser().getTenantId())
                    .eq(AlarmSoundDO::getIndustryCode,loginUser.getUser().getIndustryCode())
                    .eq(AlarmSoundDO::getStatusCd, EnumUtil.AlarmSoundStatusCd.enable.getCode());
            if (this.baseMapper.selectCount(queryWrapperType) > 0) {
                model.setStatusCd(EnumUtil.AlarmSoundStatusCd.Disable.getCode());
            }
        }
        model.setDocUrl(fileUrlEncryptUtil.dealObsFileUrl(model.getDocUrl()));
        return Result.data(model);
    }

    @Override
    public Result<String> updateStatusCd(AlarmSoundDTO model, LoginUser loginUser) {
        AlarmSoundDO alarmSoundDO = this.baseMapper.selectById(model.getAlarmSoundId());
        if (alarmSoundDO == null) {
            return Result.fail(Constants.SOUND_INFORMATION_DOES_NOT_EXIST);
        }
        if (StringUtils.isEmpty(model.getStatusCd())) {
            return Result.fail("状态信息必传");
        }
        UpdateWrapper<AlarmSoundDO> updateWrapper = null;
        if (EnumUtil.AlarmSoundStatusCd.enable.getCode().equals(model.getStatusCd())) {
            //启用  查询已有启用，修改为停用
            updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(AlarmSoundDO::getStatusCd, EnumUtil.AlarmSoundStatusCd.Disable.getCode())
                    .eq(AlarmSoundDO::getStatusCd, model.getStatusCd())
                    .eq(AlarmSoundDO::getIndustryCode,loginUser.getUser().getIndustryCode())
                    .eq(AlarmSoundDO::getTenantId, loginUser.getUser().getTenantId());
            this.baseMapper.update(null, updateWrapper);
            if (EnumUtil.AlarmSoundType.DEFAULT.getCode().equals(alarmSoundDO.getSoundType())) {
                return Result.success("启用成功");
            }
        } else {
            if (EnumUtil.AlarmSoundType.DEFAULT.getCode().equals(alarmSoundDO.getSoundType())) {
                QueryWrapper<AlarmSoundDO> queryWrapperType = new QueryWrapper<>();
                queryWrapperType.lambda()
                        .eq(AlarmSoundDO::getTenantId, loginUser.getUser().getTenantId())
                        .eq(AlarmSoundDO::getIndustryCode,loginUser.getUser().getIndustryCode())
                        .eq(AlarmSoundDO::getStatusCd, EnumUtil.AlarmSoundStatusCd.enable.getCode());
                if (this.baseMapper.selectCount(queryWrapperType) > 0) {
                    return Result.success("停用成功");
                }
                return Result.fail(ResultCode.FAILURE.getCode(),"当前无其他启用告警声音，默认声音不可停用");
            }
        }
        updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(AlarmSoundDO::getStatusCd, model.getStatusCd())
                .eq(AlarmSoundDO::getAlarmSoundId, model.getAlarmSoundId());
        this.baseMapper.update(null, updateWrapper);

        return Result.success("启用/停用成功");
    }

    @Override
    public Result<String> reset(LoginUser loginUser) {
        //查询已有启用，修改为停用
        UpdateWrapper<AlarmSoundDO> updateWrapper = null;
        updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(AlarmSoundDO::getStatusCd, EnumUtil.AlarmSoundStatusCd.Disable.getCode())
                .set(AlarmSoundDO::getUpdateTime, DateUtils.getNowDate())
                .eq(AlarmSoundDO::getStatusCd, EnumUtil.AlarmSoundStatusCd.enable.getCode())
                .eq(AlarmSoundDO::getIndustryCode,loginUser.getUser().getIndustryCode())
                .eq(AlarmSoundDO::getTenantId, loginUser.getUser().getTenantId());
        this.baseMapper.update(null, updateWrapper);
        return Result.success("恢复默认成功");
    }

    @Override
    public Result<List<AlarmSoundDO>> enableData(LoginUser loginUser) {
        QueryWrapper<AlarmSoundDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(AlarmSoundDO::getStatusCd, EnumUtil.AlarmSoundStatusCd.enable.getCode())
                .eq(AlarmSoundDO::getTenantId, loginUser.getUser().getTenantId())
                .eq(AlarmSoundDO::getIndustryCode,loginUser.getUser().getIndustryCode())
                .orderByDesc(AlarmSoundDO::getCreateTime);
        List<AlarmSoundDO> alarmSoundDOList = this.baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(alarmSoundDOList)) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(AlarmSoundDO::getSoundType, EnumUtil.AlarmSoundType.DEFAULT.getCode());
            return Result.data(this.baseMapper.selectList(queryWrapper));
        }
        return Result.data(alarmSoundDOList);
    }

    @Override
    public Result<AlarmSoundDO> selectByTenantId(LoginUser loginUser) {
        QueryWrapper<AlarmSoundDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(AlarmSoundDO::getTenantId, loginUser.getUser().getTenantId())
                .eq(AlarmSoundDO::getIndustryCode,loginUser.getUser().getIndustryCode())
                .eq(AlarmSoundDO::getStatusCd, EnumUtil.AlarmSoundStatusCd.enable.getCode());
        List<AlarmSoundDO> alarmSoundDOList = this.baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(alarmSoundDOList)) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(AlarmSoundDO::getSoundType, EnumUtil.AlarmSoundType.DEFAULT.getCode());
            alarmSoundDOList = this.baseMapper.selectList(queryWrapper);
        }
        if (CollectionUtils.isEmpty(alarmSoundDOList)) {
            return Result.data(new AlarmSoundDO());
        }
        AlarmSoundDO soundDO = alarmSoundDOList.get(0);
        List<String> docUrlList = new ArrayList<>();
        docUrlList.add(soundDO.getDocUrl());
        Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(docUrlList);
        soundDO.setDocUrl(stringStringMap.get(soundDO.getDocUrl()));
        return Result.data(soundDO);
    }

    @Override
    public Result<AlarmSoundDO> selectById(Long id) {
        AlarmSoundDO alarmSoundInfo = this.baseMapper.selectById(id);
        if (alarmSoundInfo == null) {
            return Result.data(new AlarmSoundDO());
        }
        alarmSoundInfo.setDocUrl(fileUrlEncryptUtil.dealObsFileUrl(alarmSoundInfo.getDocUrl()));
        return Result.data(alarmSoundInfo);
    }

    @Override
    public Result<String> updateDefault(AlarmSoundDO model) {
        UpdateWrapper<AlarmSoundDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(AlarmSoundDO::getDocUrl, model.getDocUrl())
                .eq(AlarmSoundDO::getAlarmSoundId, model.getAlarmSoundId());
        this.baseMapper.update(null, updateWrapper);
        return Result.success("修改默认告警声音成功");
    }

    @Override
    public Result<Boolean> checkAlarmSoundName(AlarmSoundDO model) {
        QueryWrapper<AlarmSoundDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(AlarmSoundDO::getAlarmSoundName, model.getAlarmSoundName())
                .eq(AlarmSoundDO::getTenantId, model.getTenantId())
                .eq(AlarmSoundDO::getIndustryCode, model.getIndustryCode());
        Integer count = this.baseMapper.selectCount(queryWrapper);
        boolean flag = Objects.equals(count, 0);
        return Result.data(flag);
    }

    @Override
    public Result<List<AlarmSoundDO>> selectAlarmSoundList(AlarmSoundDTO model) {
        QueryWrapper<AlarmSoundDO> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(model.getAlarmSoundName())) {
            queryWrapper.lambda().like(AlarmSoundDO::getAlarmSoundName, model.getAlarmSoundName());
        }
        if (StringUtils.isNotEmpty(model.getDocName())) {
            queryWrapper.lambda().like(AlarmSoundDO::getDocName, model.getDocName());
        }
        queryWrapper.lambda().and(wq -> wq
                .eq(AlarmSoundDO::getTenantId, model.getTenantId())
                .eq(AlarmSoundDO::getIndustryCode, model.getIndustryCode())
                .or().eq(AlarmSoundDO::getSoundType, EnumUtil.AlarmSoundType.DEFAULT.getCode()));
        List<AlarmSoundDO> alarmSoundDOList = this.baseMapper.selectList(queryWrapper);
        dealSoundUrl(alarmSoundDOList);
        return Result.data(alarmSoundDOList);
    }

    private void dealSoundUrl(List<AlarmSoundDO> alarmSoundDOList){
        List<String> oldUrlList = alarmSoundDOList.stream().map(AlarmSoundDO::getDocUrl).collect(Collectors.toList());
        Map<String, String> newUrlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(oldUrlList);
        alarmSoundDOList.forEach(alarmSoundDO -> {
            if (StringUtils.isNotEmpty(alarmSoundDO.getDocUrl())){
                alarmSoundDO.setDocUrl(newUrlMap.get(alarmSoundDO.getDocUrl()));
            }
        });
    }

    @Override
    public Result<List<AlarmSoundDO>> selectDefaultAlarmSoundList(AlarmSoundDTO model) {
        QueryWrapper<AlarmSoundDO> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(model.getAlarmSoundName())) {
            queryWrapper.lambda().like(AlarmSoundDO::getAlarmSoundName, model.getAlarmSoundName());
        }
        if (StringUtils.isNotEmpty(model.getDocName())) {
            queryWrapper.lambda().like(AlarmSoundDO::getDocName, model.getDocName());
        }
        queryWrapper.lambda().eq(AlarmSoundDO::getSoundType, EnumUtil.AlarmSoundType.DEFAULT.getCode());
        List<AlarmSoundDO> alarmSoundDOList = this.baseMapper.selectList(queryWrapper);
        dealSoundUrl(alarmSoundDOList);
        return Result.data(alarmSoundDOList);
    }
}
