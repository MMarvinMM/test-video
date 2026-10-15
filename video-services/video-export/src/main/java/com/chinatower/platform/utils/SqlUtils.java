package com.chinatower.platform.utils;

import com.alibaba.fastjson.JSON;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.service.ExportSqlExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 功能说明 : 生成导出sql
 *
 * @author 杨洪洲
 * @date 9/29/21
 * @requestID R-ZBZB-202109280789713 web-admin拆分改造
 */
@Slf4j
@Component
public class SqlUtils {
    private static SqlSessionFactory sqlSessionFactory;

    private static SqlInterceptor sqlInterceptor;

    private static ExportSqlExecuteService exportSqlExecuteService;

    @Autowired
    public static void setSqlSessionFactory(SqlSessionFactory factory) {
        SqlUtils.sqlSessionFactory = factory;
    }

    @Autowired
    public static void setSqlInterceptor(SqlInterceptor interceptor) {
        SqlUtils.sqlInterceptor = interceptor;
    }

    @Autowired
    public static void setIExportSqlExecuteService(ExportSqlExecuteService service) {
        SqlUtils.exportSqlExecuteService = service;
    }

    /**
     * 功能说明 : 底层生成导出sql方法
     *
     * @param mapperPathName      XXXMapper.class.getName().sqlId
     *                            com.chinatower.energy.project.exports.mapper.ExportFileInfoMapper.selectExportFileInfoCount  mapper地址和对应sqlId
     * @param object              参数对象
     * @param exportModule        导出模块：自定义，每个模块唯一
     * @param exportClass         导出实体类名 例如：ExportFileInfo.class.getSimpleName()
     * @param exportStrategyClass 导出数据处理策略实体类型class
     * @param exportStrategyParam 导出数据处理策略参数
     * @param <T>
     * @return
     * @author 杨洪洲
     * @date 9/29/21
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    public static <T> void getCommonSql(String mapperPathName, T object, String exportModule, String exportClass, String exportStrategyClass,
                                        String exportStrategyParam, String dataBase) {

        //通过sqlSessionFactory 获取sql
        BoundSql boundSql = sqlSessionFactory.getConfiguration()
                .getMappedStatement(mapperPathName)
                .getBoundSql(object);

        //获取sql
        String sql = sqlInterceptor.getSql(sqlSessionFactory.getConfiguration(), boundSql);
        log.info("导出sql:{}", sql);
        exportSqlExecuteService.addExportSqlExecuteApi(sql, JSON.toJSONString(object), exportModule, exportClass, exportStrategyClass, exportStrategyParam, dataBase);


        throw new CustomException("由于数据过多，请稍后到右上角下载图标去下载!", 200);
    }

    /**
     * 功能说明 : 对外提供生成导出sql调用方法，可自己创建对应策略传参的方法
     *
     * @param mapperPathName      XXXMapper.class.getName().sqlId
     *                            com.chinatower.energy.project.exports.mapper.ExportFileInfoMapper.selectExportFileInfoCount  mapper地址和对应sqlId
     * @param object              参数对象
     * @param exportModule        导出模块：自定义，每个模块唯一
     * @param exportClass         导出实体类名 例如：ExportFileInfo.class.getSimpleName()
     * @param exportStrategyClass 导出数据处理策略实体类型class
     * @return
     * @throws
     * @author 杨洪洲
     * @date 9/30/21
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    public static <T> void getSql(String mapperPathName, T object, String exportModule, String exportClass, String exportStrategyClass, String dataBase) {
        //不需要策略参数
        getCommonSql(mapperPathName, object, exportModule, exportClass, exportStrategyClass, null, dataBase);
    }


    /**
     * 功能说明 : 对外提供生成导出sql调用方法，可自己创建对应策略传参的方法
     *
     * @param mapperPathName      XXXMapper.class.getName().sqlId
     *                            com.chinatower.energy.project.exports.mapper.ExportFileInfoMapper.selectExportFileInfoCount  mapper地址和对应sqlId
     * @param object              参数对象
     * @param exportModule        导出模块：自定义，每个模块唯一
     * @param exportClass         导出实体类名 例如：ExportFileInfo.class.getSimpleName()
     * @param exportStrategyClass 导出数据处理策略实体类型class
     * @return
     * @throws
     * @author 杨洪洲
     * @date 9/30/21
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    public static <T> void getSqlHaveUserPermission(String mapperPathName, T object, String exportModule, String exportClass, String exportStrategyClass, String dataBase) {
        //获取登录用户权限
        String exportStrategyParam = JSON.toJSONString(SecurityUtils.getLoginUser());
        //将权限数据作为策略参数
        getCommonSql(mapperPathName, object, exportModule, exportClass, exportStrategyClass, exportStrategyParam, dataBase);
    }

}
