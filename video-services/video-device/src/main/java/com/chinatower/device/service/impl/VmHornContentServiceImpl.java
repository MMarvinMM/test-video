package com.chinatower.device.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.AudioDetailRequestDTO;
import com.chinatower.device.dto.AudioDetailResponseDTO;
import com.chinatower.device.dto.DeleteAudioRequestDTO;
import com.chinatower.device.dto.HornAuditionDTO;
import com.chinatower.device.dto.TextDetailRequestDTO;
import com.chinatower.device.dto.TextDetailResponseDTO;
import com.chinatower.device.entity.dto.VmHornAuditionRequestDTO;
import com.chinatower.device.entity.dto.VmHornContentConfDTO;
import com.chinatower.device.entity.dto.VmHornContentConfRequestListDTO;
import com.chinatower.device.entity.dto.VmHornContentRequestDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.mapper.VmHornContentMapper;
import com.chinatower.device.service.IHornContentUploadService;
import com.chinatower.device.service.IVmHornContentService;
import com.chinatower.device.utils.HornRequestUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author willem
 * @date 2024/10/9
 * @description
 */
@Slf4j
@Service
public class VmHornContentServiceImpl implements IVmHornContentService {

    private static final long ONE_HOURS_MILLIS = 3600000; // 1小时=3600000毫秒
    private static final long ONE_MINUTES_MILLIS = 60000; // 1分钟=60000毫秒
    private static final long ONE_SECONDS_MILLIS = 1000; // 1秒=1000毫秒

    private static final String PCM_SUFFIX_STR = ".pcm";

    @Autowired
    private VmHornContentMapper vmHornContentMapper;

    @Resource
    private NetConnectUtils connectUtils;

    @Autowired
    private IHornContentUploadService hornContentUploadService;

    @Autowired
    private HornRequestUtils hornRequestUtils;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 添加广播内容
     *
     * @param param
     * @return
     */
    @Override
    public Result<String> add(VmHornContentVO param) {
        SysUser user = getLoginUser();
        if (ObjectUtil.isEmpty(user)) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        param.setCreator(user.getUserName());
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        param.setCreateTime(new Date());
        param.setDeleteFlag(0);
        param.setAuditStatus(StringUtils.isEmpty(param.getAuditStatus()) ? "0" : param.getAuditStatus());
        param.setAppVerCode(user.getAppVerCode());
        Integer exist = vmHornContentMapper.titleExist(param);
        if (exist > 0) {
            return Result.fail("文件标题已存在");
        }
        //若为音频，则调用音频上传接口
        if (param.getContentType() == 0) {
            boolean isPcm = StringUtils.isNotEmpty(param.getFileUrl()) && param.getFileUrl().endsWith(PCM_SUFFIX_STR);
            String audioId;
            int fileType;
            // pcm格式音频不调用基础服务，并记录文件类型
            if (isPcm) {
                audioId = "-1";
                fileType = 1;
            } else {
                audioId = hornContentUploadService.addHornAudioUpload(param);
                fileType = 0;
            }
            if (StrUtil.isBlank(audioId)) {
                return Result.fail("音频上传基础平台失败");
            }
            param.setResourceId(audioId);
            param.setBaseFileUrl(param.getFileUrl());
            param.setFileType(fileType);
        }
        Integer add = vmHornContentMapper.add(param);
        if (add > 0) {
            return Result.data(String.valueOf(param.getId()));
        }
        return Result.fail("添加失败");
    }

    /**
     * 修改广播内容
     *
     * @param param
     * @return
     */
    @Override
    public Result<String> update(VmHornContentVO param) {
        SysUser user = getLoginUser();
        if (ObjectUtil.isEmpty(user)) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        param.setModifier(user.getUserName());
        param.setUpdateTime(new Date());
        Integer exist = vmHornContentMapper.titleExist(param);
        if (exist > 0) {
            return Result.fail("文件标题已存在");
        }
        VmHornContentConfDTO param1 = new VmHornContentConfDTO();
        param1.setUpdateTime(new Date());
        param1.setModifier(user.getUserName());
        List<String> ids = new ArrayList<>();
        ids.add(String.valueOf(param.getId()));
        param1.setIds(ids);
        Integer count = vmHornContentMapper.contentHasRel(param1);
        if (count > 0) {
            return Result.fail("该内容当前存在关联的任务信息，请先解除与任务的关联或删除相应任务后，再次尝试编辑操作");
        }
        //若为音频，则调用音频上传接口
        if (param.getContentType() == 0) {

            boolean isPcm = StringUtils.isNotEmpty(param.getFileUrl()) && param.getFileUrl().endsWith(PCM_SUFFIX_STR);
            String audioId;
            int fileType;
            // pcm格式音频不调用基础服务，并记录文件类型
            if (isPcm) {
                audioId = "-1";
                fileType = 1;
            } else {
                audioId = hornContentUploadService.addHornAudioUpload(param);
                if (StrUtil.isBlank(audioId)) {
                    return Result.fail("音频上传基础平台失败");
                }
                fileType = 0;
                // 平台新增了音频之后， 在删除之前保存的音频
                deleteAudioById(param.getId());
            }
            param.setResourceId(audioId);
            param.setBaseFileUrl(param.getFileUrl());
            param.setFileUrl(null);
            param.setContentValue(null);
            param.setFileDuration(null);
            param.setFileType(fileType);
        } else {
            // 可能是音频修改成文本，这里调用一把删除
            deleteAudioById(param.getId());
            param.setResourceId(null);
            param.setFileUrl(null);
            param.setBaseFileUrl(null);
            param.setFileName(null);
            param.setFileDuration(null);
            param.setFileType(0);
        }
        Integer update = vmHornContentMapper.update(param);
        if (update > 0) {
            return Result.success("修改成功");
        }
        return Result.fail("修改失败");
    }

    private void deleteAudioById(Long id) {
        try {
            VmHornContentConfDTO vmHornContentConfDTO = vmHornContentMapper.getHornContentByResourceId(id);
            boolean isPcm = StringUtils.isNotEmpty(vmHornContentConfDTO.getBaseFileUrl()) && vmHornContentConfDTO.getBaseFileUrl().endsWith(PCM_SUFFIX_STR);
            if (isPcm) {
                // pcm文件不处理
                return;
            }

            if (ObjectUtil.isNotNull(vmHornContentConfDTO) && ObjectUtil.isNotEmpty(vmHornContentConfDTO.getResourceId())) {
                List<String> audioIdList = new ArrayList<>();
                audioIdList.add(vmHornContentConfDTO.getResourceId());
                deleteAudio(audioIdList);
            }
        } catch (Exception e) {
            log.error("删除音频失败，id为{}", id);
        }
    }

    /**
     * 审核
     *
     * @param param
     * @return
     */
    @Override
    public Result<String> audit(VmHornContentVO param) {
        SysUser user = getLoginUser();
        if (ObjectUtil.isEmpty(user)) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        param.setAuditor(user.getUserName());
        param.setAuditTime(new Date());
        Integer audit = vmHornContentMapper.audit(param);
        if (audit > 0) {
            return Result.success("审核成功");
        }
        return Result.fail("审核失败");
    }

    @Override
    public Result<String> delete(List<String> ids) {
        SysUser user = getLoginUser();
        if (ObjectUtil.isEmpty(user)) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        VmHornContentConfDTO param = new VmHornContentConfDTO();
        param.setUpdateTime(new Date());
        param.setModifier(user.getUserName());
        param.setIds(ids);
        Integer count = vmHornContentMapper.contentHasRel(param);
        if (count > 0) {
            return Result.fail("该内容当前存在关联的任务信息，请先解除与任务的关联或删除相应任务后，再次尝试删除操作");
        }
        List<VmHornContentConfDTO> list = vmHornContentMapper.selectContentTypeByResourceIds(ids);
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> audioIdList = list.stream()
                    // pcm格式的文件不调用远程接口
                    .filter(x -> StringUtils.isEmpty(x.getBaseFileUrl()) || !x.getBaseFileUrl().endsWith(PCM_SUFFIX_STR))
                    .map(VmHornContentConfDTO::getResourceId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(audioIdList)) {
                deleteAudio(audioIdList);
            }
        }

        Integer delete = vmHornContentMapper.delete(param, ids);
        if (delete > 0) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    /**
     * 删除音频
     */
    private void deleteAudio(List<String> audioIdList) {
        DeleteAudioRequestDTO deleteAudioRequestDTO = new DeleteAudioRequestDTO();
        deleteAudioRequestDTO.setAudioIdList(audioIdList);
        deleteAudioRequestDTO.setAcceptNode(SecurityUtils.getLoginUser().getUser().getPlatformCode());
        hornRequestUtils.callHornSystem(deleteAudioRequestDTO, NetConnectUtils.RequestParamter.URLMappingEnum.DELETE_AUDIO, null, null);
    }

    /**
     * 列表分页查询
     *
     * @param param
     * @return
     */
    @Override
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code")
    public Result<PageInfo<VmHornContentConfDTO>> list(VmHornContentConfRequestListDTO param) {
        if (ObjectUtil.isNotEmpty(param.getPageSize()) && ObjectUtil.isNotEmpty(param.getPageNum())) {
            PageMethod.startPage(param.getPageNum(), param.getPageSize());
        }
        List<VmHornContentConfDTO> list = vmHornContentMapper.list(param);
        log.info("list:{}", JSONUtil.toJsonStr(list));
        PageInfo<VmHornContentConfDTO> pageInfo = new PageInfo<>(list);
        log.info("pageInfo:{}", JSONUtil.toJsonStr(pageInfo));
        return Result.data(pageInfo);
    }

    /**
     * 任务管理查询内容信息列表查询
     *
     * @param param
     * @return
     */
    @Override
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code")
    public Result<PageInfo<VmHornContentConfDTO>> getListByContentType(VmHornContentRequestDTO param) {
        if (param.getPageSize() != null && param.getPageNum() != null) {
            PageMethod.startPage(param.getPageNum(), param.getPageSize());
        }
        param.setTenantId(getLoginUser().getTenantId());
        param.setIndustryCode(getLoginUser().getIndustryCode());
        List<VmHornContentConfDTO> list = vmHornContentMapper.getListByContentType(param);
        for (VmHornContentConfDTO dto : list) {
            if (StringUtils.isNotEmpty(dto.getFileDuration())) {
                // 这里获取的是毫秒值
                long fileDuration = Long.parseLong(dto.getFileDuration());
                // 格式化时长字段
                dto.setFileDurationStr(formatDuration(fileDuration));
            }
        }
        PageInfo<VmHornContentConfDTO> pageInfo = new PageInfo<>(list);
        return Result.data(pageInfo);
    }

    private String formatDuration(long durationMillis) {
        long hours = durationMillis / ONE_HOURS_MILLIS; // 1小时=3600000毫秒
        long minutes = (durationMillis % ONE_HOURS_MILLIS) / ONE_MINUTES_MILLIS; // 1分钟=60000毫秒
        long seconds = (durationMillis % ONE_MINUTES_MILLIS) / ONE_SECONDS_MILLIS; // 1秒=1000毫秒
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("时");
        }
        if (hours > 0 || minutes > 0) {
            sb.append(minutes).append("分");
        }
        sb.append(seconds).append("秒");
        // 如果durationMillis为0，确保返回"0秒"
        return sb.length() == 0 ? "0秒" : sb.toString();
    }

    /**
     * 试听
     *
     * @param param
     * @return
     */
    @Override
    public Result<HornAuditionResultVO> audition(VmHornAuditionRequestDTO param) {
        Integer contentType = param.getContentType();
        VmHornContentConfDTO vmHornContentConfDTO = vmHornContentMapper.getHornContentByResourceId(param.getId());
        if (ObjectUtil.isEmpty(vmHornContentConfDTO)) {
            log.info("audition 试听资源不存在");
            return Result.fail("试听资源不存在");
        }
        //文本试听
        if (contentType == 1) {
            HornAuditionDTO auditionParam = new HornAuditionDTO();
            BeanUtils.copyProperties(param, auditionParam);
            auditionParam.setTxtContent(vmHornContentConfDTO.getContentValue());
            auditionParam.setTxtName(vmHornContentConfDTO.getContentTitle());
            TalkResultVO<AuditionV2VO> talkResultVO = hornRequestUtils.callHornSystem(auditionParam, NetConnectUtils.RequestParamter.URLMappingEnum.TXT_AUDITION, AuditionV2VO.class, null);
            HornAuditionResultVO resultVO = new HornAuditionResultVO();
            HornAuditionTextResultVO textResultVo = new HornAuditionTextResultVO();
            textResultVo.setAudioType(talkResultVO.getData().getAudioType());
            textResultVo.setContentValue(talkResultVO.getData().getDataList());
            resultVO.setText(textResultVo);
            return Result.data(resultVO);
        }
        if (contentType == 0) {
            if (ObjectUtil.isEmpty(vmHornContentConfDTO) || StrUtil.isBlank(vmHornContentConfDTO.getFileUrl())) {
                log.info("audition 试听资源不存在");
                return Result.fail("试听资源不存在");
            }
            String obsFileUrl = fileUrlEncryptUtil.dealObsFileUrl(vmHornContentConfDTO.getFileUrl());
            log.info("试听资源obsFileUrl:{}", obsFileUrl);
            HornAuditionResultVO resultVO = new HornAuditionResultVO();
            HornAuditionAudioResultVO audioResultVO = new HornAuditionAudioResultVO();
            audioResultVO.setFileDuration(vmHornContentConfDTO.getFileDuration());
            audioResultVO.setFileName(vmHornContentConfDTO.getFileName());
            audioResultVO.setFileUrl(obsFileUrl);
            resultVO.setAudio(audioResultVO);
            return Result.data(resultVO);
        }
        return Result.fail("试听失败");
    }

    /**
     * 音频详情
     *
     * @param audioId
     * @return
     */
    @Override
    public Result<AudioDetailResponseDTO> audioDetail(String audioId) {
        AudioDetailRequestDTO dto = new AudioDetailRequestDTO();
        dto.setAudioId(audioId);
        TalkResultVO<AudioDetailResponseDTO> talkResultVO = hornRequestUtils.callHornSystem(dto, NetConnectUtils.RequestParamter.URLMappingEnum.AUDIO_DETAIL, AudioDetailResponseDTO.class, null);
        return Result.data(talkResultVO.getData());
    }

    /**
     * 文本详情
     *
     * @param txtId
     * @return
     */
    @Override
    public Result<TextDetailResponseDTO> textDetail(String txtId) {
        TextDetailRequestDTO dto = new TextDetailRequestDTO();
        dto.setTxtId(txtId);
        TalkResultVO<TextDetailResponseDTO> talkResultVO = hornRequestUtils.callHornSystem(dto, NetConnectUtils.RequestParamter.URLMappingEnum.TEXT_DETAIL, TextDetailResponseDTO.class, null);
        return Result.data(talkResultVO.getData());
    }

    /**
     * 查询广播内容详情
     *
     * @param hornContentConfDTO
     * @return
     */
    @Override
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code")
    public Result<VmHornContentConfDTO> detail(VmHornContentConfRequestListDTO hornContentConfDTO) {
        VmHornContentConfDTO vmHornContentConfDTO = vmHornContentMapper.detail(hornContentConfDTO);
        if (ObjectUtil.isNotEmpty(vmHornContentConfDTO)) {
            // obs加密baseFileUrl字段
            vmHornContentConfDTO.setBaseFileUrl(fileUrlEncryptUtil.dealObsFileUrl(vmHornContentConfDTO.getBaseFileUrl()));
            vmHornContentConfDTO.setFileUrl(fileUrlEncryptUtil.dealObsFileUrl(vmHornContentConfDTO.getFileUrl()));
        }
        return Result.data(vmHornContentConfDTO);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    private SysUser getLoginUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (ObjectUtil.isEmpty(loginUser)) {
            return null;
        }
        return loginUser.getUser();
    }
}
