package com.chinatower.device.service;

import com.chinatower.device.entity.vo.VmHornContentVO;


/**
 * 广播内容音频上传平台接口类
 */
public interface IHornContentUploadService {

    /**
     * 上传音频
     *
     * @param param 广播内容配置信息
     * @return 返回结果
     */
    String addHornAudioUpload(VmHornContentVO param);

}
