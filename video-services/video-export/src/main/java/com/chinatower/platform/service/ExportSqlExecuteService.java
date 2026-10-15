package com.chinatower.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.platform.entity.export.ExportSqlExecute;

import java.util.List;

/**
 * @Description: 导出sql信息接口
 * @Author: 李峥
 * @Date: 2021/12/03 16:05
 **/
public interface ExportSqlExecuteService extends IService<ExportSqlExecute> {
    /**
     * 新增导出sql 执行记录
     *
     * @param exportSqlExecute 导出sql 执行记录
     * @return 结果
     */
    int insertExportSqlExecute(ExportSqlExecute exportSqlExecute);


    /**
     * 功能说明 : 新增导出sql 执行记录 api
     *
     * @param sql                 导出sql 执行记录
     * @param param               查询入参json
     * @param exportModule        导出模块
     * @param exportClass         导出实体class
     * @param exportStrategyClass 导出数据处理策略实体类型class
     * @param exportDataBase      导出sql所在数据库名
     * @return 结果
     * @author 杨洪洲
     * @date 9/29/21
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    int addExportSqlExecuteApi(String sql, String param, String exportModule, String exportClass, String exportStrategyClass, String exportStrategyParam, String exportDataBase);

    /**
     * 功能说明 : 查询导出sql 执行记录列表
     *
     * @param
     * @return List<com.chinatower.energy.project.exports.domain.ExportSqlExecute>
     * @throws
     * @author 杨洪洲
     * @date 9/29/21
     * @requestID R-ZBZB-202109280789714 新增功能 export拆分改造
     */
    List<ExportSqlExecute> selectExportSqlExecuteList();


    /**
     * 功能说明 : 修改导出sql 执行记录
     *
     * @param exportSqlExecute
     * @return int
     * @author 杨洪洲
     * @date 9/29/21
     * @requestID R-ZBZB-202109280789714 新增功能 export拆分改造
     */
    boolean updateExportSqlExecute(ExportSqlExecute exportSqlExecute);
}
