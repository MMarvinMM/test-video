package com.chinatower.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.platform.entity.export.ExportFileInfo;
import com.chinatower.platform.mapper.ExportFileInfoMapper;
import com.chinatower.platform.service.ExportFileInfoService;
import com.chinatower.platform.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 导出文件信息实现类
 * @Author: 李峥
 * @Date: 2021/12/03 16:05
 **/
@Service
public class ExportFileInfoServiceImpl extends ServiceImpl<ExportFileInfoMapper, ExportFileInfo>
    implements ExportFileInfoService {

    /**
     * 功能说明 : 查询导出文件地址记录列表
     * @author 杨洪洲
     * @date 9/29/21
     * @throws
     * @param exportFileInfo 导出文件地址记录
     * @return 导出文件地址记录
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    @Override
    public List<ExportFileInfo> selectExportFileInfoList(ExportFileInfo exportFileInfo) {
        //默认查询当前登录人下的文件记录
        exportFileInfo.setCreateBy(SecurityUtils.getUsername());
        return this.baseMapper.selectExportFileInfoList(exportFileInfo);
    }


    /**
     * 功能说明 : 查询导出文件地址记录数量
     * @author 杨洪洲
     * @date 9/29/21
     * @throws
     * @param exportFileInfo 导出文件地址记录
     * @return 导出文件地址记录数量
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    @Override
    public int selectExportFileInfoCount(ExportFileInfo exportFileInfo) {
        //默认查询当前登录人下的文件记录数量
        exportFileInfo.setCreateBy(SecurityUtils.getUsername());

        return this.baseMapper.selectExportFileInfoCount(exportFileInfo);
    }

    /**
     * 功能说明 : 更改已导出文件的状态
     * @author 杨洪洲
     * @date 9/29/21
     * @throws
     * @param ids 导出文件地址记录ID
     * @return
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    @Override
    public void updateBatchDownloadFlag(List<Long> ids) {
        this.baseMapper.updateBatchDownloadFlag(ids);
    }

}




