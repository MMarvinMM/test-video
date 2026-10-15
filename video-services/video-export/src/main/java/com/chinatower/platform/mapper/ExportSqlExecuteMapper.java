package com.chinatower.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.entity.export.ExportSqlExecute;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Entity com.chinatower.platform.entity.ExportSqlExecute
 */
public interface ExportSqlExecuteMapper extends BaseMapper<ExportSqlExecute> {

    /**
     * 功能说明 : 通过导出模块和登录人，以及是否存在未执行的sql 来判断是否可以继续点击下载导出服务
     * @author 杨洪洲
     * @date 9/29/21
     * @throws
     * @param exportModule
     * @return int
     * @requestID  R-ZBZB-202109280789713 web-admin拆分改造
     */
    int selectToCheckOne(@Param("exportModule") String exportModule, @Param("createBy") String createBy);

    /**
     * 功能说明 : 查询导出sql 执行记录列表
     * @author 杨洪洲
     * @date 9/29/21
     * @throws
     * @param
     * @return List<com.chinatower.energy.project.exports.domain.ExportSqlExecute>
     * @requestID R-ZBZB-202109280789714 新增功能 export拆分改造
     */
    List<ExportSqlExecute> selectExportSqlExecuteList();


    /**
     * 功能说明 : 修改导出sql 执行记录
     * @author 杨洪洲
     * @date 9/29/21
     * @param exportSqlExecute
     * @return int
     * @requestID R-ZBZB-202109280789714 新增功能 export拆分改造
     */
    int updateExportSqlExecute(ExportSqlExecute exportSqlExecute);
}




