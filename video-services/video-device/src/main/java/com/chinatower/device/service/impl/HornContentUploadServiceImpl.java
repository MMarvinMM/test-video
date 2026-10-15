package com.chinatower.device.service.impl;

import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.device.dto.*;
import com.chinatower.device.entity.vo.TalkResultVO;
import com.chinatower.device.entity.vo.VmHornContentVO;
import com.chinatower.device.service.IHornContentUploadService;
import com.chinatower.device.upload.BASE64DecodedMultipartFile;
import com.chinatower.device.utils.AIotPlatformRequestUtils;
import com.chinatower.device.utils.HornRequestUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 广播内容音频上传平台接口类
 */
@Service
@Slf4j
public class HornContentUploadServiceImpl implements IHornContentUploadService {

    @Autowired
    private HornRequestUtils hornRequestUtils;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    private AIotPlatformRequestUtils aIotUtils;

    @Override
    public String addHornAudioUpload(VmHornContentVO hornContentConfDTO) {
        String base64 = "";
        String audioId = "";
        HttpURLConnection connection = null;
        String fileUrl = hornContentConfDTO.getFileUrl();
        // 对fileUrl进行obs加密
        String obsFileUrl = fileUrlEncryptUtil.dealObsFileUrl(fileUrl);
        log.info("obsFileUrl:{}", obsFileUrl);
        try {
            URL url = new URL(obsFileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10 * 1000);
            connection.connect();

            try (InputStream in = connection.getInputStream()) {
                byte[] bytes = IOUtils.toByteArray(in);
                base64 = Base64.getEncoder().encodeToString(bytes);
            }

            // 获取到音频，转成MultipartFile流
            String relativePath = "data:audio/wav;base64," + base64;
            MultipartFile multipartFile = BASE64DecodedMultipartFile.base64ToMultipart(relativePath);

            // 上传音频到基础平台
            audioId = uploadAudio(multipartFile, hornContentConfDTO, obsFileUrl);

        } catch (Exception e) {
            log.error("上传音频到基础平台失败！", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return audioId;
    }

    private String uploadAudio(MultipartFile multipartFile, VmHornContentVO hornContentConfDTO, String obsFileUrl) {
        String audioId;
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        if (aIotUtils.isCallAIot(null)) {
            HornAudioUploadAIotVO hornAudioUploadAIotVO = new HornAudioUploadAIotVO();
            hornAudioUploadAIotVO.setMultipartFile(obsFileUrl);
            hornAudioUploadAIotVO.setDesc(hornContentConfDTO.getFileName());
            hornAudioUploadAIotVO.setAccessNode(platformCode);
            hornAudioUploadAIotVO.setAcceptNode(platformCode);
            TalkResultVO<HornAudioUploadRspVO> talkResultVO = hornRequestUtils.callHornSystem(hornAudioUploadAIotVO, NetConnectUtils.RequestParamter.URLMappingEnum.AUDIO_UPLOAD, HornAudioUploadRspVO.class, null);
            audioId = talkResultVO.getData().getAudioId();
        }
        else {
            HornAudioUploadVO hornAudioUploadVO = new HornAudioUploadVO();
            hornAudioUploadVO.setMultipartFile(multipartFile);
            hornAudioUploadVO.setDesc(hornContentConfDTO.getFileName());
            hornAudioUploadVO.setAccessNode(platformCode);
            hornAudioUploadVO.setAcceptNode(platformCode);
            List<MultipartFile> multipartFiles = new ArrayList<>();
            multipartFiles.add(multipartFile);
            TalkResultVO<HornAudioUploadRspVO> talkResultVO = hornRequestUtils.callHornSystemForm(hornAudioUploadVO, multipartFiles, NetConnectUtils.RequestParamter.URLMappingEnum.AUDIO_UPLOAD, HornAudioUploadRspVO.class, null);
            audioId = talkResultVO.getData().getAudioId();
        }
        return audioId;
    }
}
