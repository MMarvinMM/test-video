package com.chinatower.platform.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.ImportMethod;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.entity.SysImportRecord;

import java.util.List;

/**
 * 功能说明: 导入记录Service接口
 *
 * @author wangzic
 * @date 2020-09-07
 * @requestID
 */
public interface ISysImportService {

    /**
     * 功能说明: 查询最新导入记录信息
     * @author wangzic
     * @param importId 导入记录编码
     * @return 导入记录集合
     * @throws
     * @requestID
     */
    SysImportRecord selectLatestRecordById(String importId);

    /**
     * 功能说明: 查询导入记录列表
     * @author wangzic
     * @param sysImportRecord 导入记录
     * @return 导入记录集合
     * @requestID
     */
    List<SysImportRecord> selectImportRecordList(SysImportRecord sysImportRecord);

    /**
     * 功能说明: 根据导入数量自动选择多线程或单线程导入
     * @author wangzic
     * @param params 导入参数
     * @param importMethod 导入方法
     * @param <T> 导入对象
     * @throws
     * @return 执行结果
     * @requestID
     */
    <T> Result<String> autoThreadImport(ImportParams<T> params, ImportMethod<T> importMethod);

    /**
     * 功能说明: 根据导入记录编码查询导入记录明细
     * @author wangzic
     * @param detail 查询条件
     * @return 导入记录明细
     * @throws
     * @requestID
     */
    List<SysImportDetail> selectImportDetailList(SysImportDetail detail);

}
