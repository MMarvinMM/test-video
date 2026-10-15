package com.chinatower.bdm.modules.timeline.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.api.entity.AlgorithmPicResultVO;
import com.chinatower.bdm.modules.timeline.dto.LayerInfoDto;
import com.chinatower.bdm.modules.timeline.dto.VmSensingAlgorithmTaskInfoDto;
import com.chinatower.bdm.modules.timeline.dto.VmSensingAlgorithmTaskInfoQueryDto;
import com.chinatower.bdm.modules.timeline.entity.VmSensingAlgorithmTaskInfo;
import com.chinatower.bdm.modules.timeline.mapper.VmSensingAlgorithmTaskInfoMapper;
import com.chinatower.bdm.modules.timeline.service.VmSensingAlgorithmTaskInfoService;
import com.chinatower.bdm.modules.timeline.vo.VmSensingAlgorithmTaskInfoVo;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.CompletableFutureHelper;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.feign.client.AlarmFlowDeployFeign;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.SecondRecogFileVO;
import com.chinatower.event.entity.vo.SecondRecogSendVO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.common.utils.DateUtils;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 遥感算法任务信息表(VmSensingAlgorithmTaskInfo)表服务实现类
 *
 * @author makejava
 * @since 2024-05-27
 */
@Slf4j
@Service("vmSensingAlgorithmTaskInfoService")
public class VmSensingAlgorithmTaskInfoServiceImpl implements VmSensingAlgorithmTaskInfoService {

    @Resource
    private VmSensingAlgorithmTaskInfoMapper vmSensingAlgorithmTaskInfoMapper;
    @Autowired
    private AlarmFlowDeployFeign alarmFlowDeployFeign;
    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    private static final String vm_sensing_algorithm_task_info = "vm_sensing_algorithm_task_info";

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public VmSensingAlgorithmTaskInfoVo queryById(Long id) {
        VmSensingAlgorithmTaskInfoVo vo = new VmSensingAlgorithmTaskInfoVo();
        VmSensingAlgorithmTaskInfo taskInfo = vmSensingAlgorithmTaskInfoMapper.queryById(id);
        if (Objects.isNull(taskInfo)) {
            throw new CustomException("不存在当前查询数据");
        }
        // 复制数据库的值到VO对象
        BeanUtils.copyBeanProp(vo, taskInfo);
        vo.setLayerOneInfo(JSONUtil.toBean(taskInfo.getLayerOneInfo(), LayerInfoDto.class));
        vo.setLayerTwoInfo(JSONUtil.toBean(taskInfo.getLayerTwoInfo(), LayerInfoDto.class));
        vo.setSecondRecogResult(null);
        return vo;
    }

    /**
     * 分页查询
     *
     * @param queryDto 筛选条件
     * @return 查询结果
     */
    @Override
    public List<VmSensingAlgorithmTaskInfoVo> queryByPage(VmSensingAlgorithmTaskInfoQueryDto queryDto) {
        // 获取分页
        Integer pageNum = queryDto.getPageNum();
        Integer pageSize = queryDto.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        List<VmSensingAlgorithmTaskInfoVo> vmSensingAlgorithmTaskInfoVos = vmSensingAlgorithmTaskInfoMapper.queryAllByLimit(queryDto);
        vmSensingAlgorithmTaskInfoVos.forEach(taskInfoVo -> {
            taskInfoVo.setLayerOneInfo(JSONUtil.toBean(taskInfoVo.getLayerOneInfoStr(), LayerInfoDto.class));
            taskInfoVo.setLayerTwoInfo(JSONUtil.toBean(taskInfoVo.getLayerTwoInfoStr(), LayerInfoDto.class));
        });
        if (StrUtil.isEmpty(queryDto.getShowRecogResult())) {
            vmSensingAlgorithmTaskInfoVos.forEach(taskInfoVo -> {
                taskInfoVo.setSecondRecogResult(null);
            });
        }
        return vmSensingAlgorithmTaskInfoVos;
    }

    /**
     * 新增数据
     *
     * @param taskInfoDto 实例对象
     * @return 影响行数
     */
    @Transactional
    @Override
    public int insert(VmSensingAlgorithmTaskInfoDto taskInfoDto) {
        log.info("遥感算法任务新增: {}", JSON.toJSONString(taskInfoDto));
        // 校验任务信息
        checkTaskInfo(taskInfoDto);
        VmSensingAlgorithmTaskInfo taskInfo = new VmSensingAlgorithmTaskInfo();
        BeanUtils.copyBeanProp(taskInfo, taskInfoDto);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        taskInfo.setTenantId(loginUser.getUser().getTenantId());
        taskInfo.setTenantName(loginUser.getUser().getTenantName());
        taskInfo.setAppVerCode(loginUser.getUser().getAppVerCode());
        taskInfo.setIndustryCode(loginUser.getUser().getIndustryCode());
        taskInfo.setDeleteFlag(Constants.ZERO_NUM);
        taskInfo.setCreator(loginUser.getUsername());
        taskInfo.setCreateTime(DateUtils.getNowDate());
        taskInfo.setLayerOneInfo(JSONUtil.toJsonStr(taskInfoDto.getLayerOneInfo()));
        taskInfo.setLayerTwoInfo(JSONUtil.toJsonStr(taskInfoDto.getLayerTwoInfo()));
        // 新增时任务状态为执行中
        taskInfo.setTaskStatus(Constants.ONE_NUM);
        if (Constants.ONE_NUM == taskInfoDto.getDefaultFlag()) {
            // 新增时如何设置为默认则状态为待默认（因为任务状态为执行中）
            taskInfo.setDefaultFlag(Constants.THREE_NUM);
        }
        int insertCount = vmSensingAlgorithmTaskInfoMapper.insert(taskInfo);
        if (insertCount > 0) {
            // 调用算法仓的接口
            CompletableFuture.runAsync(() -> synAnalysisTask(taskInfo));
        }
        return insertCount;
    }

    private void synAnalysisTask(VmSensingAlgorithmTaskInfo taskInfo) {
        List<SecondRecogSendVO> params = new ArrayList<>();
        params.add(convertTaskInfoParam(taskInfo, taskInfo.getLayerOneInfo(), taskInfo.getLayerTwoInfo()));
        log.info("遥感算法调用算法仓接口参数: {}", JSON.toJSONString(params));
        Result<String> result = alarmFlowDeployFeign.synAnalysisTask(params);
        log.info("遥感算法调用算法仓接口返回结果: {}", JSON.toJSONString(result));
        if (StrUtil.isNotBlank(result.getData())) {
            String taskBatchId = result.getData();
            taskInfo.setTaskBatchId(taskBatchId);
            vmSensingAlgorithmTaskInfoMapper.update(taskInfo);
        }
    }

    private SecondRecogSendVO convertTaskInfoParam(VmSensingAlgorithmTaskInfo taskInfo, String layerOneInfoStr, String layerTwoInfoStr) {
        LayerInfoDto layerOneInfo = JSONUtil.toBean(layerOneInfoStr, LayerInfoDto.class);
        LayerInfoDto layerTwoInfo = JSONUtil.toBean(layerTwoInfoStr, LayerInfoDto.class);
        SecondRecogSendVO secondRecogSendVO = new SecondRecogSendVO();
        secondRecogSendVO.setTaskId(vm_sensing_algorithm_task_info + "/" + Constants.FOUR + "/" + taskInfo.getId().toString() + "/" + UUID.fastUUID());
        secondRecogSendVO.setAlgorithmCode(taskInfo.getAlgorithmCode());
        SecondRecogFileVO fileVO = new SecondRecogFileVO();
        fileVO.setType("image");
        fileVO.setImgUrls(formatPicUrl(layerOneInfo));
        fileVO.setBaselinePicUrl(formatPicUrl(layerTwoInfo));
        secondRecogSendVO.setFileInfo(fileVO);
        return secondRecogSendVO;
    }

    private String formatPicUrl(LayerInfoDto layerInfo) {
        String originUrl = layerInfo.getOriginUrl();
        if (StringUtils.isEmpty(originUrl)) {
            throw new CustomException("图层OBS地址不能为空");
        }
        String encryptUrl = fileUrlEncryptUtil.dealObsFileUrl(originUrl);
        log.info("遥感算法originUrl={}, 加密后url:{}", originUrl, encryptUrl);
        return StringUtils.isNotEmpty(encryptUrl) ? encryptUrl : originUrl;
    }

    /**
     * 校验任务信息
     *
     * @param taskInfoDto 任务对象
     */
    private void checkTaskInfo(VmSensingAlgorithmTaskInfoDto taskInfoDto) {
        if (Objects.isNull(taskInfoDto)) {
            throw new CustomException("任务信息不能为空");
        }
        // 校验任务名称
        if (StringUtils.isEmpty(taskInfoDto.getTaskName())) {
            throw new CustomException("任务名称不能为空");
        }
        if (taskInfoDto.getTaskName().length() > 30) {
            throw new CustomException("任务名称最多30个字符");
        }
        if (Objects.isNull(taskInfoDto.getLayerOneInfo()) || Objects.isNull(taskInfoDto.getLayerTwoInfo())) {
            throw new CustomException("图层信息不能为空");
        }
        // 校验图层一和图层二不能重复
        if (StringUtils.isNotEmpty(taskInfoDto.getLayerOneInfo().getLayerId()) && StringUtils.isNotEmpty(taskInfoDto.getLayerTwoInfo().getLayerId())) {
            if (taskInfoDto.getLayerOneInfo().getLayerId().equals(taskInfoDto.getLayerTwoInfo().getLayerId())) {
                throw new CustomException("图层一和图层二不能重复");
            }
        }
        if (StringUtils.isEmpty(taskInfoDto.getLayerOneInfo().getOriginUrl()) || StringUtils.isEmpty(taskInfoDto.getLayerTwoInfo().getOriginUrl())) {
            throw new CustomException("图层obs地址不能为空");
        }

    }

    /**
     * 修改数据
     *
     * @param taskInfoDto 实例对象
     * @return 影响行数
     */
    @Transactional
    @Override
    public int update(VmSensingAlgorithmTaskInfoDto taskInfoDto) {
        log.info("遥感算法任务修改: {}", JSON.toJSONString(taskInfoDto));
        // 校验任务信息
        checkTaskInfo(taskInfoDto);
        if (Objects.isNull(taskInfoDto.getId())) {
            throw new CustomException("修改数据失败，主键为空");
        }
        VmSensingAlgorithmTaskInfo taskInfoDb = vmSensingAlgorithmTaskInfoMapper.queryById(taskInfoDto.getId());
        if (Objects.isNull(taskInfoDb)) {
            throw new CustomException("修改失败，数据不存在");
        }
        BeanUtils.copyBeanProp(taskInfoDb, taskInfoDto);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        taskInfoDb.setModifier(loginUser.getUsername());
        taskInfoDb.setUpdateTime(DateUtils.getNowDate());
        taskInfoDb.setTaskStatus(Constants.ONE_NUM);
        taskInfoDb.setLayerOneInfo(JSONUtil.toJsonStr(taskInfoDto.getLayerOneInfo()));
        taskInfoDb.setLayerTwoInfo(JSONUtil.toJsonStr(taskInfoDto.getLayerTwoInfo()));
        if (Constants.ONE_NUM == taskInfoDto.getDefaultFlag()) {
            // 修改时如果设置为默认则状态为待默认（因为任务状态为执行中）
            taskInfoDb.setDefaultFlag(Constants.THREE_NUM);
        }
        int updateCount = vmSensingAlgorithmTaskInfoMapper.update(taskInfoDb);
        if (updateCount > 0) {
            // 调用算法仓的接口
            CompletableFuture.runAsync(() -> synAnalysisTask(taskInfoDb));
        }
        return updateCount;
    }

    /**
     * 通过主键删除数据（软删除）
     *
     * @param id 主键
     * @return 是否成功
     */
    @Transactional
    @Override
    public boolean deleteById(Long id) {
        log.info("遥感算法任务删除: {}", id);
        int delCount = vmSensingAlgorithmTaskInfoMapper.deleteById(id);
        return delCount > 0;
    }

    /**
     * 设置默认任务
     * 只能有一个任务为默认，设置新的默认任务时，会先将所有任务的默认状态取消
     *
     * @return 是否成功
     */
    @Transactional
    @Override
    public boolean updateDefaultTaskById(VmSensingAlgorithmTaskInfoDto taskInfoDto) {
        Long id = taskInfoDto.getId();
        log.info("设置默认任务: {}", id);
        // 校验任务ID
        if (Objects.isNull(id)) {
            throw new CustomException("任务ID不能为空");
        }
        // 查询任务是否存在且未删除
        VmSensingAlgorithmTaskInfo taskInfo = vmSensingAlgorithmTaskInfoMapper.queryById(id);
        if (Objects.isNull(taskInfo)) {
            throw new CustomException("任务不存在");
        }
        // 校验任务状态是否为成功（2），只有成功的任务才能设置为默认
        if (taskInfo.getTaskStatus() == null || !taskInfo.getTaskStatus().equals(Constants.TWO_NUM)) {
            throw new CustomException("只有任务状态为成功的任务才能设置为默认");
        }
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 先将所有任务的默认状态取消
        vmSensingAlgorithmTaskInfoMapper.updateAllDefaultFlagToZero(taskInfoDto);
        // 再将指定任务设置为默认
        VmSensingAlgorithmTaskInfo updateInfo = new VmSensingAlgorithmTaskInfo();
        updateInfo.setId(id);
        updateInfo.setDefaultFlag(Constants.ONE_NUM);
        updateInfo.setModifier(loginUser.getUsername());
        updateInfo.setUpdateTime(DateUtils.getNowDate());
        int updateCount = vmSensingAlgorithmTaskInfoMapper.update(updateInfo);
        return updateCount > 0;
    }

    @Override
    public boolean updateAlgorithmPicResult(AlgorithmPicResultVO algorithmPicResultVO) {
        log.info("遥感算法更新算法图片结果: {}", JSON.toJSONString(algorithmPicResultVO));
        Long taskInfoId = algorithmPicResultVO.getTaskInfoId();
        if (Objects.isNull(taskInfoId)) {
            throw new CustomException("任务ID不能为空");
        }
        VmSensingAlgorithmTaskInfo taskInfo = vmSensingAlgorithmTaskInfoMapper.queryById(taskInfoId);
        if (Objects.isNull(taskInfo)) {
            log.info("任务不存在: {}", taskInfoId);
            return false;
        }
        taskInfo.setSecondRecogResult(algorithmPicResultVO.getSecondRecogResult());
        // 根据算法仓返回的二次识别结果，解析 success / message，设置任务状态与错误信息
        fillTaskStatusAndErrorMsg(taskInfo, algorithmPicResultVO.getSecondRecogResult());
        if (Constants.TWO_NUM == taskInfo.getTaskStatus() && Constants.THREE_NUM == taskInfo.getDefaultFlag()) {
            VmSensingAlgorithmTaskInfoDto taskInfoDto = new VmSensingAlgorithmTaskInfoDto();
            taskInfoDto.setId(taskInfoId);
            taskInfoDto.setDataScope(String.format("and tenant_id = '%s' and industry_code = '%s'", taskInfo.getTenantId(), taskInfo.getIndustryCode()));
            vmSensingAlgorithmTaskInfoMapper.updateAllDefaultFlagToZero(taskInfoDto);
            taskInfo.setDefaultFlag(Constants.ONE_NUM);
        }
        int updateCount = vmSensingAlgorithmTaskInfoMapper.update(taskInfo);
        return updateCount > 0;
    }

    /**
     * 解析 secondRecogResult 中的 extendField → attrList → attrType=yangao 对应的 JSON
     * success=true  → 任务成功（2）
     * success=false → 任务失败（3）
     * message       → 错误信息
     *
     * @param taskInfo          任务实体
     * @param secondRecogResult 算法返回的二次识别结果 JSON 字符串
     */
    private void fillTaskStatusAndErrorMsg(VmSensingAlgorithmTaskInfo taskInfo, String secondRecogResult) {
        if (StrUtil.isBlank(secondRecogResult)) {
            return;
        }
        try {
            JSONObject root = JSONUtil.parseObj(secondRecogResult);
            Object extendFieldObj = root.get(com.chinatower.bdm.common.Constants.EXTEND_FIELD);
            if (Objects.isNull(extendFieldObj)) {
                return;
            }

            JSONArray extendArray = JSONUtil.parseArray(extendFieldObj);
            Boolean successFlag = null;
            String message = null;

            for (Object extendItem : extendArray) {
                if (!(extendItem instanceof JSONObject)) {
                    continue;
                }
                JSONObject extendJson = (JSONObject) extendItem;
                JSONArray attrList = extendJson.getJSONArray(com.chinatower.bdm.common.Constants.ATTR_LIST);
                if (Objects.isNull(attrList)) {
                    continue;
                }
                for (Object attrObj : attrList) {
                    if (!(attrObj instanceof JSONObject)) {
                        continue;
                    }
                    JSONObject attrJson = (JSONObject) attrObj;
                    String attrValue = attrJson.getStr(com.chinatower.bdm.common.Constants.ATTR_VALUE);
                    if (StrUtil.isBlank(attrValue)) {
                        continue;
                    }
                    // attrValue 内部为 JSON，结构示例：
                    // {"success": false, "message": "The two images do not have overlapping areas.", "data": null, "count": 0}
                    JSONObject valueJson = JSONUtil.parseObj(attrValue);
                    if (valueJson.containsKey(com.chinatower.bdm.common.Constants.SUCCESS_MSG) || valueJson.containsKey(com.chinatower.bdm.common.Constants.MESSAGE)) {
                        if (valueJson.containsKey(com.chinatower.bdm.common.Constants.SUCCESS_MSG)) {
                            successFlag = valueJson.getBool(com.chinatower.bdm.common.Constants.SUCCESS_MSG);
                        }
                        if (valueJson.containsKey(com.chinatower.bdm.common.Constants.MESSAGE)) {
                            message = valueJson.getStr(com.chinatower.bdm.common.Constants.MESSAGE);
                        }
                    }
                }
            }

            if (Boolean.TRUE.equals(successFlag)) {
                // success = true → 任务状态：2（成功）
                taskInfo.setTaskStatus(Constants.TWO_NUM);
            } else if (Boolean.FALSE.equals(successFlag)) {
                // success = false → 任务状态：3（失败）
                taskInfo.setTaskStatus(Constants.THREE_NUM);
            } else {
                taskInfo.setTaskStatus(Constants.TWO_NUM);
            }
            // 仅当 message 非空时覆盖错误信息
            if (StrUtil.isNotBlank(message)) {
                taskInfo.setErrorMsg(message);
            }
        } catch (Exception e) {
            // 解析异常时不影响主流程，只记录日志方便排查
            log.error("解析 secondRecogResult 设置任务状态、错误信息失败，secondRecogResult={}", secondRecogResult, e);
        }
    }

}
