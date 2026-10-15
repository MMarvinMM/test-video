package com.chinatower.platform.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.constant.Constants;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportRecord;
import com.chinatower.platform.mapper.SysImportDetailMapper;
import com.chinatower.platform.mapper.SysImportRecordMapper;
import com.chinatower.platform.entity.ImportMethod;
import com.chinatower.platform.service.ISysImportService;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletionService;

/**
 * 导入记录Service业务层处理
 *
 * @author wangzic
 * @date 2020-09-07
 */

@Slf4j
@Service
@SuppressWarnings("all")
public class SysImportServiceImpl implements ISysImportService {

    @Autowired
    private SysImportRecordMapper sysImportRecordMapper;

    @Autowired
    private SysImportDetailMapper sysImportDetailMapper;

    @Override
    @DataScope(deptColumn = "T.DEPT_ID")
    public List<SysImportRecord> selectImportRecordList(SysImportRecord sysImportRecord) {
        List<SysImportRecord> sysImportRecords = sysImportRecordMapper.selectImportRecordList(sysImportRecord);
        sysImportRecords.forEach(record -> {
            if(ObjectUtil.isNotNull(record) ){
                record.setFileUrl(AESUtil.decryptHttp(record.getFileUrl()));
            }
        });
        return sysImportRecords;
    }

    @Override
    public List<SysImportDetail> selectImportDetailList(SysImportDetail detail) {
        return sysImportDetailMapper.selectList(new LambdaQueryWrapper<SysImportDetail>()
                .eq(StrUtil.isNotBlank(detail.getImportId()),SysImportDetail::getImportId,detail.getImportId())
                .eq(ObjectUtil.isNotNull(detail.getImportRow()),SysImportDetail::getImportRow,detail.getImportRow())
                .eq(StrUtil.isNotBlank(detail.getHandleResult()),SysImportDetail::getHandleResult,detail.getHandleResult())
                .like(StrUtil.isNotBlank(detail.getFailReason()),SysImportDetail::getFailReason,detail.getFailReason())
                .orderByAsc(SysImportDetail::getImportRow));
    }


    @Override
    public SysImportRecord selectLatestRecordById(String importId) {
        SysImportRecord record = sysImportRecordMapper.selectLatestRecordById(importId);
        if(ObjectUtil.isNotNull(record)){
            record.setFileUrl(AESUtil.decryptHttp(record.getFileUrl()));
        }
        return record;
    }

    private int batchInsertImportDetail(String importId, int begin, int end, String failReason) {
        List<SysImportDetail> list = new ArrayList<>();
        for(int i=begin; i<=end; i++){
            list.add(new SysImportDetail(importId,i, Constants.F,failReason));
        }
        return sysImportDetailMapper.batchInsertImportDetail(list);
    }

    private <T> String batchHandleImport(ImportParams<T> params, ImportMethod<T> importMethod){
        StringBuilder result = new StringBuilder();
        result.append("【").append(params.getStartRowNum()).append(params.getEndRowNum()).append("】行处理");
        try {
            List<SysImportDetail> sysImportDetails = importMethod.runImport(params);
            sysImportDetailMapper.batchInsertImportDetail(sysImportDetails);
            result.append("完成；\n");
        }catch (Exception e){
            String failReason = ExceptionUtil.stacktraceToString(e,1000);
            this.batchInsertImportDetail(
                    params.getImportId(), params.getStartRowNum(), params.getEndRowNum(), failReason);
            result.append("失败；\n");
        }
        return result.toString();
    }

    private  <T> void multiThreadImport(ImportParams<T> params, ImportMethod<T> importMethod){
        //拆分任务
        List<ImportParams<T>> paramsList = new ArrayList<>();
        List<List<T>> dataLists = CollUtil.split(params.getList(), params.getSplitSize());
        for (int i = 0; i < dataLists.size(); i++) {
            paramsList.add(new ImportParams<T>(dataLists.get(i),params.getImportId(),params.getExtraParams(),i,params.getSplitSize(), params.getImportType()));
        }

        StringBuilder sb = new StringBuilder();
        CompletionService<String> service = ThreadUtil.newCompletionService();
        try {
            paramsList.forEach(p -> service.submit(()-> this.batchHandleImport(p,importMethod)));
            //等待线程池任务执行完成并统计，通过take获取Future结果,此方法会阻塞
            for (int j = 0; j < paramsList.size(); j++) {
                sb.append(service.take().get());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            SysImportRecord record = this.selectLatestRecordById(params.getImportId());
            record.setProcessState(Constants.Y);
            record.setProcessInfo(sb.toString());
            record.setUpdateTime(new Date());
            sysImportRecordMapper.updateById(record);
        }
    }

    private <T> void singleThreadImport(ImportParams<T> importParams, ImportMethod<T> importMethod){
        String result = this.batchHandleImport(importParams,importMethod);
        SysImportRecord record = this.selectLatestRecordById(importParams.getImportId());
        record.setProcessState(Constants.Y);
        record.setProcessInfo(result);
        record.setUpdateTime(new Date());
        sysImportRecordMapper.updateById(record);
    }

    @Override
    public <T> Result<String> autoThreadImport(ImportParams<T> importParams, ImportMethod<T> importMethod) {
        Date realDate = null;
        try{
            Date date = new Date();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 设置成东八区时间
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

            String realDateString = dateFormat.format(date);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            realDate = df.parse(realDateString);
        }catch (Exception e){
            throw new CustomException("日期格式转换错误,导入记录新增失败");
        }

        List<T> dataList = importParams.getList();
        SysImportRecord record = new SysImportRecord();
        record.setImportId(IdUtil.objectId());
        record.setFileName(importParams.getFileName());
        record.setFileUrl(importParams.getFileUrl());
        record.setImportType(importParams.getImportType());
        record.setImportTotal(importParams.getList().size());
        record.setSuccessTotal(0);
        record.setFailureTotal(0);
        record.setProcessState(Constants.N);
        record.setCreateTime(realDate);
        record.setCreateBy(SecurityUtils.getUsername());
        record.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        if(sysImportRecordMapper.insert(record) == 0){
            throw new CustomException("导入记录新增失败");
        }
        importParams.setImportId(record.getImportId());
        new Thread(() -> {
            // 处理具体的逻辑
            if(dataList.size()>importParams.getSplitSize()){
                this.multiThreadImport(importParams,importMethod);
            }else {
                this.singleThreadImport(importParams,importMethod);
            }
        }).start();
        return Result.data(record.getImportId());
    }


}
