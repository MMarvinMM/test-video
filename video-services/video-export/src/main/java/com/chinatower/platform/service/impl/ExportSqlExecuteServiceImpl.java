package com.chinatower.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.export.ExportSqlExecute;
import com.chinatower.platform.mapper.ExportSqlExecuteMapper;
import com.chinatower.platform.service.ExportSqlExecuteService;
import com.chinatower.platform.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 导出sql信息实现类
 * @Author: 李峥
 * @Date: 2021/12/03 16:05
 **/
@Service
public class ExportSqlExecuteServiceImpl extends ServiceImpl<ExportSqlExecuteMapper, ExportSqlExecute>
        implements ExportSqlExecuteService {

    private static final String ADMIN = "admin";

    /**
     * 功能说明 : 新增导出sql 执行记录
     *
     * @param exportSqlExecute 导出sql 执行记录
     * @return 结果
     * @throws
     * @author 杨洪洲
     * @date 9/29/21
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    @Override
    @Transactional
    public int insertExportSqlExecute(ExportSqlExecute exportSqlExecute) {
        exportSqlExecute.setCreateBy(SecurityUtils.getUsername() == null ? ADMIN : SecurityUtils.getUsername());
        exportSqlExecute.setUpdateBy(SecurityUtils.getUsername() == null ? ADMIN : SecurityUtils.getUsername());
        return this.baseMapper.insert(exportSqlExecute);
    }

    /**
     * 功能说明 : 新增导出sql 执行记录
     *
     * @param sql                 导出sql 执行记录
     * @param exportStrategyClass 导出数据处理策略实体类型
     * @return 结果
     * @throws
     * @author 杨洪洲
     * @date 9/29/21
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    @Override
    public int addExportSqlExecuteApi(String sql, String param, String exportModule, String exportClass, String exportStrategyClass, String exportStrategyParam, String exportDataBase) {
        //唯一性校验
        this.checkOne(exportModule);
        ExportSqlExecute exportSqlExecute = new ExportSqlExecute();
        exportSqlExecute.setSqlValue(sql);
        exportSqlExecute.setExportModule(exportModule);
        exportSqlExecute.setRequestParam(param);
        exportSqlExecute.setExportTimes((byte) 0);
        exportSqlExecute.setExportClass(exportClass);
        exportSqlExecute.setExportStrategyClass(exportStrategyClass);
        exportSqlExecute.setExportStrategyParam(exportStrategyParam);
        exportSqlExecute.setExportDatabase(exportDataBase);
        return this.insertExportSqlExecute(exportSqlExecute);
    }

    @Override
    public List<ExportSqlExecute> selectExportSqlExecuteList() {
        return this.baseMapper.selectExportSqlExecuteList();
    }

    @Override
    @Transactional
    public boolean updateExportSqlExecute(ExportSqlExecute exportSqlExecute) {
        int i = this.baseMapper.updateExportSqlExecute(exportSqlExecute);
        if (i > 0) {
            return true;
        }
        return false;
    }

    /**
     * 功能说明 : 通过导出模块和登录人，以及是否存在未执行的sql 来判断是否可以继续点击下载导出服务
     *
     * @param exportModule
     * @return int
     * @throws
     * @author 杨洪洲
     * @date 9/29/21
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    private void checkOne(String exportModule) {
        int num = this.baseMapper.selectToCheckOne(exportModule, SecurityUtils.getUsername());
        if (num != 0) {
            throw new CustomException("您正在导出此文件，请稍后再试！", 200);
        }
    }
}




