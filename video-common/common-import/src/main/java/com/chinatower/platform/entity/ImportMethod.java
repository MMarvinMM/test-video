package com.chinatower.platform.entity;

import java.util.List;

/**
 * 导入方法接口
 * @author WANGZIC
 * @param <T>
 */
public interface ImportMethod<T> {

    /**
     * 实际执行方法
     * @param params 导入参数
     * @return 返回结果
     */
    List<SysImportDetail> runImport(ImportParams<T> params);
}
