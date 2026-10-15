package com.chinatower.bdm.modules.licensePlateLibrary.service.impl;


import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.common.NonceUtil;
import com.chinatower.bdm.modules.licensePlateLibrary.entity.CheckpointVehicleLicensePlateImportVO;
import com.chinatower.bdm.modules.licensePlateLibrary.mapper.CheckpointVehicleLicensePlateMapper;
import com.chinatower.bdm.modules.licensePlateLibrary.service.CheckpointVehicleLicensePlateService;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAlgorithmConfigService;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
public class CheckpointVehicleLicensePlateServiceImpl extends ServiceImpl<CheckpointVehicleLicensePlateMapper, CheckpointVehicleLicensePlate> implements CheckpointVehicleLicensePlateService {

    @Autowired
    private CheckpointVehicleLicensePlateMapper checkpointVehicleLicensePlateMapper;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;


    /**
     * 黑白名单新增修改接口
     *
     * @return String
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    public Result<String> insertOrUpdateVehicleLicensePlate(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate) {
        log.info("黑白名单新增修改---请求报文------" + JSONObject.toJSONString(checkpointVehicleLicensePlate));
        //车牌号效验
        if (StringUtils.isNotEmpty(checkpointVehicleLicensePlate.getVehicleNumber()) && !checkPlateNumberFormat(checkpointVehicleLicensePlate.getVehicleNumber())) {
            return Result.fail("请输入正确的车牌号");
        }
        checkpointVehicleLicensePlate.setValidPeriodVO(JSON.toJSONString(checkpointVehicleLicensePlate.getSxTime()));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        checkpointVehicleLicensePlate.setTenantId(user.getTenantId());
        checkpointVehicleLicensePlate.setIndustryCode(user.getIndustryCode());
        checkpointVehicleLicensePlate.setAppVerCode(user.getAppVerCode());
        //判断是否为重复车牌号
        int count = checkpointVehicleLicensePlateMapper.checkVehicleLicensePlate(checkpointVehicleLicensePlate);
        if (count > 0) {
            return Result.fail("车牌号已存在");
        }
        //使用id判断是新增还是修改
        if (StringUtils.isNotEmpty(checkpointVehicleLicensePlate.getId())) {
            checkpointVehicleLicensePlate.setModifier(user.getUserName());
            checkpointVehicleLicensePlate.setUpdateTime(LocalDateTime.now().toString());
            checkpointVehicleLicensePlateMapper.updateVehicleLicensePlate(checkpointVehicleLicensePlate);
            return Result.success("名单修改成功");
        } else {
            checkpointVehicleLicensePlate.setCreator(user.getUserName());
            checkpointVehicleLicensePlate.setCreateTime(LocalDateTime.now().toString());
            checkpointVehicleLicensePlateMapper.insertVehicleLicensePlate(checkpointVehicleLicensePlate);
            return Result.success("名单新增成功");
        }

    }


    //车牌号效验
    public boolean checkPlateNumberFormat(String content) {
        String pattern = "";

        Boolean someSpecialChars = false;

        String[] specialChars = {"学", "挂", "领", "试", "超", "练", "警"};
        for (String charToCheck : specialChars) {
            if (content.contains(charToCheck)) {
                someSpecialChars = true;
            }
        }
        if(someSpecialChars) {
            pattern = Constants.SPECIAL_PATTERN;
        } else {
            // 新能源车牌
            if (content.length() == 8) {
                pattern = Constants.NEW_ENERGY_PATTERN;
            } else if (content.length() == 7) {
                // 普通车牌
                pattern = Constants.ORDINARY_PATTERN;
            }
        }
        return Pattern.matches(pattern, content);
    }


    /**
     * 黑白名单删除接口
     *
     * @return String
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    public Result<String> deleteVehicleLicensePlate(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate) {
        log.info("黑白名单删除---请求报文------" + JSONObject.toJSONString(checkpointVehicleLicensePlate));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        checkpointVehicleLicensePlate.setModifier(user.getUserName());
        checkpointVehicleLicensePlate.setUpdateTime(LocalDateTime.now().toString());
        checkpointVehicleLicensePlateMapper.deleteVehicleLicensePlate(checkpointVehicleLicensePlate);
        CheckpointVehicleLicensePlate vehicleLicensePlateDetail = checkpointVehicleLicensePlateMapper.getVehicleLicensePlateDetail(checkpointVehicleLicensePlate.getId());
        if (vehicleLicensePlateDetail != null) {
            checkpointVehicleLicensePlateMapper.deleteGiveLicenseRelByVehicleNumber(vehicleLicensePlateDetail.getVehicleNumber(), vehicleLicensePlateDetail.getBlackOrWhite());
        }
        return Result.success("名单删除成功");
    }


    /**
     * 获取黑白名单信息详情
     *
     * @return String
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    public Result<CheckpointVehicleLicensePlate> getVehicleLicensePlateDetail(String id) {
        CheckpointVehicleLicensePlate vehicleLicensePlate = checkpointVehicleLicensePlateMapper.getVehicleLicensePlateDetail(id);
        String imgUrl = vehicleLicensePlate.getImgUrl();
        if (StringUtils.isNotEmpty(imgUrl)) {
            getUrl(vehicleLicensePlate, imgUrl);
        }
        return Result.data(vehicleLicensePlate);
    }


    @Override
    public List<CheckpointVehicleLicensePlate> listVehicleLicensePlateByCondition(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate) {
        List<CheckpointVehicleLicensePlate> list = checkpointVehicleLicensePlateMapper.listVehicleLicensePlateByCondition(checkpointVehicleLicensePlate);
        list.forEach(e -> {
            if (StringUtils.isNotEmpty(e.getValidPeriodVO())) {
                e.setSxTime(JSON.parseArray(e.getValidPeriodVO()));
            }
            String imgUrl = e.getImgUrl();
            if (StringUtils.isNotEmpty(imgUrl)) {
                getUrl(e, imgUrl);
            }

        });
        return list;
    }

    private void getUrl(CheckpointVehicleLicensePlate e, String imgUrl) {
        List<String> oldUrlList = new ArrayList<>();
        List<String> newUrlList = new ArrayList<>();
        if (imgUrl.contains(Constants.COMMA)) {
            Collections.addAll(oldUrlList, imgUrl.split(Constants.COMMA));
        } else {
            oldUrlList.add(imgUrl);
        }
        Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(oldUrlList);
        for (String url : oldUrlList) {
            String encryptUrl = stringStringMap.get(url);
            newUrlList.add(encryptUrl);
        }
        e.setImgUrl(String.join(Constants.COMMA, newUrlList));
    }

    @Override
    public List<SysImportDetail> importData(ImportParams<CheckpointVehicleLicensePlateImportVO> params) {
        List<SysImportDetail> details = new ArrayList<>();
        int rowNum = params.getStartRowNum();
        SysUser loginUser = MapUtil.get(params.getExtraParams(), Constants.LOGIN_USER, SysUser.class);
        for (CheckpointVehicleLicensePlateImportVO importVO : params.getList()) {
            SysImportDetail detail = new SysImportDetail(params.getImportId(), rowNum);
            try {
                // 创建可直接用于数据库插入的对象
                CheckpointVehicleLicensePlate forSave = new CheckpointVehicleLicensePlate();
                // 将excel读取的属性值拷贝到可插入数据库对象中
                BeanUtils.copyProperties(importVO, forSave);

                forSave.setTenantId(loginUser.getTenantId());
                forSave.setIndustryCode(loginUser.getIndustryCode());
                forSave.setBlackOrWhite(MapUtil.get(params.getExtraParams(), "blackOrWhite", Integer.class));

                // 校验
                if (ObjectUtil.isEmpty(forSave.getVehicleNumber())
                        || ObjectUtil.isEmpty(forSave.getValidPeriod())) {
                    detail.setFailReason(Constants.NET_NAME_NULL);
                    continue;
                }
                if (!checkPlateNumberFormat(forSave.getVehicleNumber())) {
                    detail.setFailReason(Constants.VEHICLE_LICENSE_PLATE_ERROR);
                    continue;
                }

                // 防止全角冒号的出现，统一check转成半角
                forSave.setValidPeriod(forSave.getValidPeriod().replace("：", ":"));

                if (!Pattern.matches(Constants.VALID_PERIOD_REGEX, forSave.getValidPeriod())) {
                    detail.setFailReason(Constants.VALID_PERIOD_FORMAT_ERROR);
                    continue;
                }

                // 校验时间段是否重叠， 09:00~10:00,9:45~10:30 即为重叠
                if (NonceUtil.checkTimeRangesOverlap(forSave.getValidPeriod())) {
                    detail.setFailReason(Constants.VALID_PERIOD_OVERLAP_ERROR);
                    continue;
                }

                // 时间段起始时间是否大于结束时间  2:00~3:00,4:00~5:00,7:00~6:00 不符合要求
                if(!NonceUtil.areAllValidTimeRanges(forSave.getValidPeriod())) {
                    detail.setFailReason(Constants.VALID_PERIOD_STARTGTEND_ERROR);
                    continue;
                }

                var vehicleLicenseCunt = checkpointVehicleLicensePlateMapper.checkVehicleLicensePlate(forSave);
                if (vehicleLicenseCunt != 0) {
                    detail.setFailReason(Constants.VEHICLE_LICENSE_PLATE_HAVED);
                    continue;
                }

                // 从excel导入的时间段
                String originalValidPeriodString = forSave.getValidPeriod();
                // 替换成需要格式
                String newValidPeriodString = originalValidPeriodString.replace("~", "-");
                forSave.setValidPeriod(newValidPeriodString);

                // 组装validPeriodVO
                forSave.setValidPeriodVO(buildValidPeriodVOColumn(newValidPeriodString));
                // 组装数据
                forSave.setIndustryCode(loginUser.getIndustryCode());
                forSave.setAppVerCode(loginUser.getAppVerCode());
                forSave.setCreator(loginUser.getUserName());
                forSave.setProvinceCode(loginUser.getProvinceCode());
                forSave.setCityCode(loginUser.getCityCode());
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMATE);
                forSave.setCreateTime(now.format(formatter));
                int saveFlag = checkpointVehicleLicensePlateMapper.insertVehicleLicensePlate(forSave);
                if (saveFlag == 1) {
                    detail.setHandleResult(com.chinatower.platform.common.constant.Constants.S);
                } else {
                    detail.setFailReason("添加失败");
                }
            } catch (Exception e) {
                detail.setFailReason("处理异常：" + ExceptionUtil.stacktraceToString(e, 1000));
                log.error(e.getMessage(), e);
            } finally {
                rowNum++;
                details.add(detail);
            }
        }
        return details;
    }


    /**
     * 组装ValidPeriodVO字段
     *
     * @param newValidPeriodString 格式 01:09-02:45,08:30-09:23
     * @return [{"value":["00:00","23:59"]},{"value":["00:00","23:59"]},{"value":["00:00","23:59"]}]
     */
    private String buildValidPeriodVOColumn(String newValidPeriodString) {
        JSONArray sxTime = new JSONArray();

        // 先拆成若干个时间段 ["01:09-02:45", "08:30-09:23"]
        String[] timeSlotArry = newValidPeriodString.split(Constants.COMMA);

        for (String timeSlot : timeSlotArry) {
            String[] timeElementArray = timeSlot.split("-");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", timeElementArray);
            sxTime.add(jsonObject);
        }
        return sxTime.toJSONString();
    }
}


