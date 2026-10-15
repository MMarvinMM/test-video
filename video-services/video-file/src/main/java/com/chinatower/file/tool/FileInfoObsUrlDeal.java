package com.chinatower.file.tool;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.vo.SysFileInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName ObsFileUrlEncryptDeal
 * @Deacription Obs文件地址加密处理
 * @Author peng.haibo
 * @Date 2022/8/2 10:25
 * @Version 1.0
 **/
@Component
public class FileInfoObsUrlDeal {

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * @param fileInfo 参数描述
     * @return com.chinatower.platform.project.system.domain.SysFileInfo
     * @Description 处理Obs文件URL加密返回
     * @author peng.haibo
     * @date 2022/8/2
     */
    public SysFileInfo dealObsFileUrl(SysFileInfo fileInfo) {
        return dealObsFileUrl(fileInfo, false, null);
    }

    public SysFileInfo dealObsFileUrl(SysFileInfo fileInfo, Boolean useOfs, String colonyCode) {
        if (fileInfo == null || StringUtils.isEmpty(fileInfo.getFileUrl())) {
            return fileInfo;
        }
        fileInfo.setFileUrl(fileUrlEncryptUtil.dealObsFileUrl(fileInfo.getFileUrl(), useOfs, colonyCode));
        return fileInfo;
    }

}
