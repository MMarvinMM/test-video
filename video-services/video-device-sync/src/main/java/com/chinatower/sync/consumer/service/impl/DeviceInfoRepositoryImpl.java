package com.chinatower.sync.consumer.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.dto.SiteInfoDto;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.sync.consumer.dao.DeviceInfoMapper;
import com.chinatower.sync.consumer.entity.ChannelDetectionFactorDTO;
import com.chinatower.sync.consumer.entity.ChannelDetectionFileUrlVO;
import com.chinatower.sync.consumer.entity.DeviceInfoDO;
import com.chinatower.sync.consumer.entity.VodResultVO;
import com.chinatower.sync.consumer.service.IDeviceInfoRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
@Slf4j
@Service
public class DeviceInfoRepositoryImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfoDO> implements IDeviceInfoRepository {

    private final String VIDEO_JITTER = "video_jitter";

    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public void updateDeviceInfoBySiteInfo(SiteInfoDto siteInfoDto) {
        if (StringUtils.isBlank(siteInfoDto.getSiteCode())) {
            throw new CustomException(Constants.SITE_CODE_IS_EMPTY);
        }
        DeviceInfoDO uptDeviceInfo = new DeviceInfoDO();
        uptDeviceInfo.setLocation(siteInfoDto.getLocation());
        uptDeviceInfo.setLongitude(String.valueOf(siteInfoDto.getLongitude()));
        uptDeviceInfo.setLatitude(String.valueOf(siteInfoDto.getLatitude()));
        uptDeviceInfo.setSiteName(siteInfoDto.getSiteName());
        uptDeviceInfo.setAreaCode(siteInfoDto.getAreaCode());
        uptDeviceInfo.setAdmCode(siteInfoDto.getAdmCode());
        uptDeviceInfo.setOrgCode(siteInfoDto.getOrgCode());
        LambdaUpdateWrapper<DeviceInfoDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DeviceInfoDO::getSiteCode, siteInfoDto.getSiteCode()).eq(DeviceInfoDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
        updateWrapper.set(DeviceInfoDO::getModifierDate, new Date());
        this.baseMapper.update(uptDeviceInfo, updateWrapper);
    }

    @Override
    public List<DeviceInfoDO> listDeviceByCodes(List<String> deviceCodes) {
        List<DeviceInfoDO> deviceInfoDOList = new ArrayList<>(0);
        if (deviceCodes == null || deviceCodes.isEmpty()) {
            return deviceInfoDOList;
        }
        QueryWrapper<DeviceInfoDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("device_id", "device_code", "app_mod_flag");
        queryWrapper.eq("delete_flag", DeviceConstants.DELETE_FLAG_NORMAL).in("device_code", deviceCodes);
        deviceInfoDOList = this.baseMapper.selectList(queryWrapper);
        return deviceInfoDOList;
    }

    /**
     * 视频质量诊断结果
     * @param messages
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> dealVmsVodResult(List<String> messages) {
        log.info("视频质量诊断结果入参：{}", messages);
        List<VodResultVO> list = Lists.newArrayList();
        messages.forEach(message -> {
            VodResultVO resultVO = JSONObject.parseObject(message, VodResultVO.class);
            list.add(resultVO);
        });
        //处理诊断结果信息
        batchUpdateDiagnoseInfo(list);
        //处理检测因子信息
        batchDealChannelFactor(list);
        return Result.success("频质量诊断结果更新成功！");
    }

    /**
     * 批量处理检测因子信息
     * @param list
     */
    public void batchDealChannelFactor(List<VodResultVO> list) {
        //1.首先根据根据通道进行分组,这时候按照通道分组的里面可能同时有视频和图片的检测项，需要取通道_视频/图片各自最新的一条检测数据
        Map<String, List<VodResultVO>> vodResultMap = list.stream().collect(Collectors.groupingBy(VodResultVO::getChannelCode));
        //检测项要更新的数据
        List<VodResultVO> lastList = Lists.newArrayList();
        //2.根据通道分组后，分别取每个通道里视频和图片检测项最新的一条数据（图片和通道是分别传过来的报文）
        vodResultMap.forEach((k, v) -> {
            //获取视频最新的一条检测项
            VodResultVO videoResultVO = v.stream().filter(item -> !CollectionUtils.isEmpty(item.getDiagnoseResultItems())
                            && item.getDiagnoseResultItems().stream().anyMatch(diagnoseResultItem -> Objects.equals(diagnoseResultItem.getAlgorithmCode(), VIDEO_JITTER)))
                    .max(Comparator.comparing(VodResultVO::getDiagnoseTime)).orElse(null);
            //获取图片最新的一条的检测项
            VodResultVO picResultVO = v.stream().filter(item -> !CollectionUtils.isEmpty(item.getDiagnoseResultItems())
                            && item.getDiagnoseResultItems().stream().noneMatch(diagnoseResultItem -> Objects.equals(diagnoseResultItem.getAlgorithmCode(), VIDEO_JITTER)))
                    .max(Comparator.comparing(VodResultVO::getDiagnoseTime)).orElse(null);
            //视频检测项
            if (Objects.nonNull(videoResultVO)) {
                lastList.add(videoResultVO);
            }
            //图片检测项
            if (Objects.nonNull(picResultVO)) {
                lastList.add(picResultVO);
            }

        });
        log.info("获取到视频检测质量该批次最新的视频/图片报文数据: {}", lastList);
        if (!CollectionUtils.isEmpty(lastList)) {
            //更新通道表中的图片和视频
            List<ChannelDetectionFactorDTO> updateChannelList = Lists.newArrayList();

            //需要删除的检测因子的通道
            List<String> deleteList = Lists.newArrayList();

            //需要新增的检测因子数据
            List<ChannelDetectionFactorDTO> insertList = Lists.newArrayList();

            //获取该批次传过来的通道编码，查询数据库中该设备的检测时间和通道编码
            List<String> channelList = lastList.stream().map(VodResultVO::getChannelCode).distinct().collect(Collectors.toList());
            //3.查询数据库中通道的检测因子数据
            List<ChannelDetectionFactorDTO> channelDetectionFactorDTOList = baseMapper.getDeviceFactorListByChannelList(channelList);
            //4.比较数据库中的检测时间和报文的检测时间，如果报文的检测时间大于数据库中的检测时间，则更新数据库中的检测时间和检测结果
            Map<String, List<VodResultVO>> lastChannelMap =lastList.stream().collect(Collectors.groupingBy(VodResultVO::getChannelCode));
            //循环判断每组通道的 图片/视频检测数据是否是最新的（比数据库中记录的检测时间更大），一个通道可能有两条数据， 视频一条，图片一条
            lastChannelMap.forEach((k, v) -> {
                //获取数据库中的检测因子数据
                List<ChannelDetectionFactorDTO> filterList = channelDetectionFactorDTOList.stream().
                        filter(item -> StringUtils.equals(k, item.getChannelCode())).collect(Collectors.toList());
                //如果数据库中没有通道的检测则直接新增
                if (CollectionUtils.isEmpty(filterList)) {
                    addNewChannelFactor(v, updateChannelList, insertList, 1);
                }
                //如果数据库中有则需要判断时间
                else {
                    //1.上报的报文数据库中没有，则直接新增      2.上报的报文数据库中有，检测时间比较数据中大，则需要先删除再新增

                    //先取出数据库中与上报报文中都存在的检测因子数据
                    VodResultVO vodResultVO = v.stream().filter(vo -> filterList.stream().anyMatch(filterVO ->
                            vo.getDiagnoseResultItems().stream().anyMatch(diagnoseResultItemVO ->
                                    StringUtils.equals(filterVO.getFactorCode(), diagnoseResultItemVO.getAlgorithmCode())))).findFirst().orElse(null);
                    //如果数据库中已经有了检测因子数据，则需要判断时间
                    if (Objects.nonNull(vodResultVO)) {
                        //如果数据库中的检测时间小于上报的检测时间，则需要先将数据库中的先删除再新增
                        ChannelDetectionFactorDTO filterChannelVO = filterList.stream().filter(filterVO -> vodResultVO.getDiagnoseResultItems().stream()
                                .anyMatch(detectResultItemVO -> StringUtils.equals(filterVO.getFactorCode(), detectResultItemVO.getAlgorithmCode())
                                                                && filterVO.getDiagnoseTime().before(detectResultItemVO.getDiagnoseTime())))
                                .findFirst().orElse(null);
                        if (Objects.nonNull(filterChannelVO)) {
                            //如果数据库中存在历史的检测数据则先删除
                            deleteList.add(filterChannelVO.getChannelCode());
                            addNewChannelFactor(v, updateChannelList, insertList, 2);
                        }

                    }
                    //如果没有该检测因子则直接新增
                    else {
                        addNewChannelFactor(v, updateChannelList, insertList, 1);
                    }
                }

            });

            //需要删除的通道的检测因子数据
            if (!CollectionUtils.isEmpty(deleteList)) {
                log.info("质量监测上报----需要删除的检测因子的通道,{}", deleteList);
                baseMapper.deleteDeviceFactorListByChannelList(deleteList);
            }
            //需要新增的通道的检测因子数据
            if (!CollectionUtils.isEmpty(insertList)) {
                log.info("质量监测上报----需要新增的检测因子的通道,{}", insertList);
                baseMapper.batchInsertDetection(insertList);
            }
            if (!CollectionUtils.isEmpty(updateChannelList)) {
                log.info("质量监测上报----需要修改的检测因子的通道,{}", updateChannelList);
                baseMapper.batchUpdateChannelDiaFile(updateChannelList);
            }


        }


    }

    private void addNewChannelFactor(List<VodResultVO> v, List<ChannelDetectionFactorDTO> updateChannelList, List<ChannelDetectionFactorDTO> insertList, int operType) {
        Gson gson = new Gson();
        v.forEach(item -> {
            item.getDiagnoseResultItems().forEach(items -> {
                ChannelDetectionFactorDTO dto = new ChannelDetectionFactorDTO();
                dto.setDeviceCode(item.getDeviceCode());
                dto.setChannelCode(item.getChannelCode());
                dto.setFactorCode(items.getAlgorithmCode());
                dto.setFactorName(items.getAlgorithmName());
                dto.setFactorValue(String.valueOf(items.getDetectResult()));
                dto.setDiagnoseTime(items.getDiagnoseTime());
                insertList.add(dto);
            });
        });
        //获取视频最新的一条检测项
        VodResultVO videoResultVO = v.stream().filter(item -> !CollectionUtils.isEmpty(item.getDiagnoseResultItems())
                        && item.getDiagnoseResultItems().stream().anyMatch(diagnoseResultItem -> Objects.equals(diagnoseResultItem.getAlgorithmCode(), VIDEO_JITTER)))
                .max(Comparator.comparing(VodResultVO::getDiagnoseTime)).orElse(null);
        //获取图片最新的一条的检测项
        VodResultVO picResultVO = v.stream().filter(item -> !CollectionUtils.isEmpty(item.getDiagnoseResultItems())
                        && item.getDiagnoseResultItems().stream().noneMatch(diagnoseResultItem -> Objects.equals(diagnoseResultItem.getAlgorithmCode(), VIDEO_JITTER)))
                .max(Comparator.comparing(VodResultVO::getDiagnoseTime)).orElse(null);

        ChannelDetectionFactorDTO channelDetectionFactorDTO = new ChannelDetectionFactorDTO();
        if (Objects.nonNull(videoResultVO)) {
            ChannelDetectionFileUrlVO channelDetectionFileUrlVO = gson.fromJson(videoResultVO.getImgUrl(), ChannelDetectionFileUrlVO.class);
            channelDetectionFactorDTO.setDiagnoseVideoUrl(channelDetectionFileUrlVO.getPreviewAddress());
            //如果是删除则需要更新null
        } else if (Objects.equals(operType, 2)) {
            channelDetectionFactorDTO.setDiagnoseVideoUrl(Constants.DEL_FLAG);
        }
        if (Objects.nonNull(picResultVO)) {
            ChannelDetectionFileUrlVO channelDetectionFileUrlVO = gson.fromJson(picResultVO.getImgUrl(), ChannelDetectionFileUrlVO.class);
            channelDetectionFactorDTO.setDiagnoseImageUrl(channelDetectionFileUrlVO.getPreviewAddress());
        } else if (Objects.equals(operType, 2)) {
            channelDetectionFactorDTO.setDiagnoseImageUrl(Constants.DEL_FLAG);
        }
        channelDetectionFactorDTO.setChannelCode(v.get(0).getChannelCode());
        updateChannelList.add(channelDetectionFactorDTO);
    }

    /**
     *
     * @param list
     */
    public void batchUpdateDiagnoseInfo(List<VodResultVO> list) {
        //开始处理诊断结果信息
        //1.首先对设备编码去重，拿到本批次上传的设备编码 设备编码->设备检测结果
        Map<String, List<VodResultVO>> vodResultMap = list.stream().collect(Collectors.groupingBy(VodResultVO::getDeviceCode));

        //2.根据设备编码查询数据库中该设备上次的检测时间，以及最小的通道编码
        Set<String> deviceCodeSet = vodResultMap.keySet();
        List<DeviceInfoVO> deviceInfoVOList = baseMapper.getDeviceChannelByDeviceCodeList(deviceCodeSet);
        log.info("视频质量诊断结果根据设备查询数据库结果:{}", deviceInfoVOList);
        //要更新检测时间的数据集
        List<VodResultVO> updateList = Lists.newArrayList();
        //3.代码匹配，上传的报文中的通道是否是可见光，不是可见光则直接丢弃
        //循环上传的报文
        vodResultMap.forEach((k, v) -> {
            DeviceInfoVO deviceInfoVO = deviceInfoVOList.stream().filter(device -> StringUtils.equals(k, device.getDeviceCode())).findFirst().orElse(null);
            //查询到数据库中的设备编码、最小的通道编码、检测时间
            if (Objects.nonNull(deviceInfoVO)) {
                //根据可见光（通道编码最小的通道，用数据库查询出来的通道编码跟报文上传的通道编码匹配，同时检测时间点是在数据库记录的检测时间之后）筛选出最新的那条检测数据(获取检测时间最新的那条)
                VodResultVO vodResultVO = v.stream().
                        filter(channel -> StringUtils.equals(channel.getChannelCode(), deviceInfoVO.getChannelCode())
                                && DateUtil.compare(channel.getDiagnoseTime(), deviceInfoVO.getDiagnoseTime()) > 0)
                        .max(Comparator.comparing(VodResultVO::getDiagnoseTime)).orElse(null);
                //如果有可见光有最新的检测结果，则将结果更新到数据库中
                if (Objects.nonNull(vodResultVO)) {
                    updateList.add(vodResultVO);
                }

            }
        });
        log.info("视频质量诊断结果更新集合: {}", updateList);
        //4.获取设备时间_可见光通道最新的一条记录，并将该设备的检测结果更新到数据库中
        if (!CollectionUtils.isEmpty(updateList)) {
            baseMapper.batchUpdateDiagnoseInfo(updateList);
        }
    }

}
