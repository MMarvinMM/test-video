package com.chinatower.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.entity.export.ExportFileInfo;

import java.util.List;

/**
 * @Entity com.chinatower.platform.entity.ExportFileInfo
 */
public interface ExportFileInfoMapper extends BaseMapper<ExportFileInfo> {
    /**
     * 功能说明 : 查询导出文件地址记录列表
     * @author 杨洪洲
     * @date 9/29/21
     * @throws
     * @param exportFileInfo 导出文件地址记录
     * @return 导出文件地址记录
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    List<ExportFileInfo> selectExportFileInfoList(ExportFileInfo exportFileInfo);

    /**
     * 功能说明 : 查询导出文件地址记录数量
     * @author 杨洪洲
     * @date 9/29/21
     * @throws
     * @param exportFileInfo 导出文件地址记录
     * @return 导出文件地址记录数量
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    int selectExportFileInfoCount(ExportFileInfo exportFileInfo);

    /**
     * 功能说明 : 更改已导出文件的状态
     * @author 杨洪洲
     * @date 9/29/21
     * @throws
     * @param ids 导出文件地址记录ID
     * @return
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    void updateBatchDownloadFlag(List<Long> ids);
}




