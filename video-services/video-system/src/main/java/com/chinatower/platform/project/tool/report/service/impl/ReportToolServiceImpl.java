package com.chinatower.platform.project.tool.report.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.framework.web.page.TableDataInfo;
import com.chinatower.platform.project.system.service.ISysAreaService;
import com.chinatower.platform.project.system.service.ISysDeptService;
import com.chinatower.platform.project.system.service.ISysDictDataService;
import com.chinatower.platform.project.tool.report.domain.ReportColumn;
import com.chinatower.platform.project.tool.report.domain.ReportCondition;
import com.chinatower.platform.project.tool.report.domain.ReportTool;
import com.chinatower.platform.project.tool.report.mapper.ReportToolMapper;
import com.chinatower.platform.project.tool.report.service.IReportToolService;
import com.chinatower.platform.project.tool.report.util.SqlLabelParseUtil;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.SysArea;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;

/**
 * 报表配置Service业务层处理
 * 
 * @author wangzic
 * @date 2020-02-14
 */
@Slf4j
@Service
public class ReportToolServiceImpl implements IReportToolService {

    @Autowired
    private ReportToolMapper reportToolMapper;

    @Value("${maxPageSize.oracle:1000}")
    private int oracleMaxPageSize;

    @Value("${maxPageSize.mongodb:1000}")
    private int mongoDbMaxPageSize;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysAreaService areaService;

    @Autowired
    private ISysDictDataService dictDataService;

    @Resource(name = "primaryMongoTemplate")
    private MongoTemplate primaryMongoTemplate;

    @Resource(name = "secondaryMongoTemplate")
    private MongoTemplate secondaryMongoTemplate;

    /**
     * 查询报表配置
     * 
     * @param id 报表配置ID
     * @return 报表配置
     */
    @Override
    public ReportTool selectReportConfigById(Integer id) {
        return reportToolMapper.selectReportConfigById(id);
    }

    /**
     * 查询报表配置列表
     * 
     * @param reportTool 报表配置
     * @return 报表配置
     */
    @DataScope
    @Override
    public List<ReportTool> selectReportConfigList(ReportTool reportTool) {
        return reportToolMapper.selectReportConfigList(reportTool);
    }

    /**
     * 新增报表配置
     * 
     * @param reportTool 报表配置
     * @return 结果
     */
    @Override
    public int insertReportConfig(ReportTool reportTool) {
        reportTool.setCreateBy(SecurityUtils.getUsername());
        reportTool.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        reportTool.setCreateTime(new Date());
        return reportToolMapper.insertReportConfig(reportTool);
    }

    /**
     * 修改报表配置
     * 
     * @param reportTool 报表配置
     * @return 结果
     */
    @Override
    public int updateReportConfig(ReportTool reportTool){
        reportTool.setUpdateBy(SecurityUtils.getUsername());
        reportTool.setUpdateTime(new Date());
        return reportToolMapper.updateReportConfig(reportTool);
    }

    /**
     * 批量删除报表配置
     * 
     * @param ids 需要删除的报表配置ID
     * @return 结果
     */
    @Override
    public int deleteReportConfigByIds(Integer[] ids) {
        return reportToolMapper.deleteReportConfigByIds(ids);
    }

    /**
     * 删除报表配置信息
     * 
     * @param id 报表配置ID
     * @return 结果
     */
    @Override
    public int deleteReportConfigById(Integer id) {
        return reportToolMapper.deleteReportConfigById(id);
    }

    private Boolean isHasKeyword(Object object){
        String[] keywords = {"and ","truncate ","select ","update ","delete ","drop ","alert ","or ",
                "union ","like ","where ","from ","order ","||","(",")","'","\""};
        int i = 0;
        boolean has = false;
        while (!has && i<keywords.length){
            if(ObjectUtil.isNotNull(object) && object.toString().toLowerCase().contains(keywords[i])){
                has =true;
            }
            i++;
        }
        return has;
    }

    @DataScope
    @Override
    public TableDataInfo selectByDynamicSql(ReportCondition condition) {
        TableDataInfo rspData = new TableDataInfo();
        try {
            rspData.setCode(HttpStatus.SUCCESS);
            String sql = condition.getReportSql();
            Map<String,Object> params = condition.getParams();
            for(String key:params.keySet()){
                if(isHasKeyword(params.get(key))){
                    throw new CustomException("输入内容中包含屏蔽词汇");
                }
            }
            int pageNum = ObjectUtil.isNotEmpty(condition.getPageNum())?condition.getPageNum():1;
            int pageSize = ObjectUtil.isNotEmpty(condition.getPageSize())?condition.getPageSize():10;
            if(StrUtil.isNotBlank(sql)){
                if(StrUtil.startWith(condition.getDatasourceType(),"mongodb")){
                    Query query = new Query();
                    if(!params.isEmpty()){
                        for(String key:params.keySet()){
                            query.addCriteria(Criteria.where(key).is(params.get(key)));
                        }
                    }
                    JSONObject object = JSON.parseObject(sql);
                    String collName = object.getString("collName");
                    if("secondary".equals(object.getString("dbName"))){
                        rspData.setTotal(secondaryMongoTemplate.count(query,collName));
                        query.skip(pageSize * (pageNum - 1)).limit(pageSize);
                        rspData.setRows(secondaryMongoTemplate.find(query,Map.class,collName));
                    }else {
                        rspData.setTotal(primaryMongoTemplate.count(query,collName));
                        query.skip(pageSize * (pageNum - 1)).limit(pageSize);
                        rspData.setRows(primaryMongoTemplate.find(query,Map.class,collName));
                    }
                    return rspData;
                } else {
                    //R-ZBZB-202109280789699 优化报表工具可以配置多数据源查询 王子超
                    if(StrUtil.startWith(condition.getDatasourceType(),"database")){
                        StrUtil.subAfter(condition.getDatasourceType(),"_",false);
                        DynamicDataSourceContextHolder.push(condition.getDatasourceType());
                    }else {
                        DynamicDataSourceContextHolder.push(EbikeDataBaseConstant.SLAVE);
                    }
                    PageHelper.startPage(pageNum,pageSize);
                    String dataScope = Constants.ONE.equals(condition.getIfDataFilter())?condition.getDataScope():"";
                    List<Map<String,Object>> list = reportToolMapper.selectByDynamicSql(SqlLabelParseUtil.parseSql(condition), dataScope);
                    rspData.setRows(list);
                    rspData.setTotal(new PageInfo(list).getTotal());
                    return rspData;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DynamicDataSourceContextHolder.clear();
        }
        return rspData;
    }

    @DS(EbikeDataBaseConstant.SLAVE)
    @Override
    public void exportReport(ReportCondition condition, HttpServletResponse response)throws Exception {
        ReportTool reportConfig = reportToolMapper.selectReportConfigById(condition.getReportId());
        if(reportConfig != null){
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition","attachment;filename=Report.xlsx");
            ExcelWriter writer = null;
            try {
                writer = cn.hutool.poi.excel.ExcelUtil.getBigWriter();
                List<ReportColumn> columns = JSON.parseArray(reportConfig.getReportColumns(),ReportColumn.class);
                columns = search(columns);
                // 设置表头
                for (ReportColumn column:columns ) {
                    writer.addHeaderAlias(column.getColumnKey(), column.getColumnName());
                }
                for(int i=0;i<columns.size();i++){
                    writer.setColumnWidth(i,30);
                }
                condition.setReportSql(reportConfig.getReportSql());
                condition.setPageNum(1);
                int maxPageSize = "MongoDB".equals(condition.getDatasourceType())?mongoDbMaxPageSize:oracleMaxPageSize;
                condition.setPageSize(maxPageSize);
                TableDataInfo dataInfo = this.selectByDynamicSql(condition);
                writer.write(buildExcelRow((List<Map<String, Object>>) dataInfo.getRows(),columns), true);
                int pages = (int) Math.ceil((double)dataInfo.getTotal()/maxPageSize);
                if(pages>1){
                    for(int i=2;i<=pages;i++){
                        condition.setPageNum(i);
                        writer.write(buildExcelRow((List<Map<String, Object>>) this.selectByDynamicSql(condition).getRows(),columns), true);
                    }
                }
                response.reset();
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("download.xlsx", "UTF-8"));
                response.setContentType("application/octet-stream; charset=UTF-8");
                //out为OutputStream，需要写出到的目标流
                ServletOutputStream out= response.getOutputStream();
                writer.flush(out,true);
                //此处记得关闭输出Servlet流
                IoUtil.close(out);
            }catch (Exception e) {
                log.error("导出Excel异常{}", e.getMessage(),e);
                throw new CustomException("导出Excel失败，请联系网站管理员！");
            }finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }else {
            response.getWriter().println("导出失败：报表配置不存在");
        }
    }

    @Override
    public Set<String> getCollNames(String dbName) {
        if("primary".equalsIgnoreCase(dbName)){
            return primaryMongoTemplate.getCollectionNames();
        }
        if("secondary".equalsIgnoreCase(dbName)){
            return secondaryMongoTemplate.getCollectionNames();
        }
        return new HashSet<>();
    }

    private List<Map> buildExcelRow(List<Map<String,Object>> resList, List<ReportColumn> columns){
        List<Map> oldRows = JSON.parseArray(JSON.toJSONStringWithDateFormat(resList,"yyyy-MM-dd HH:mm:ss"),Map.class);
        Map<String,Map<String,String>> formatter = getFormatter(columns);
        List<Map> rows = new ArrayList<>();
        //筛选导出的字段
        for (Map oldRow : oldRows ) {
            Map<String,String> row = new HashMap<>();
            for (ReportColumn column:columns ) {
                String key = column.getColumnKey();
                String val = oldRow.get(key)==null?"":oldRow.get(key).toString();
                if(Constants.ONE.equals(column.getIfFormat())){
                    if(Constants.ONE.equals(column.getFormatMethod())){
                        row.put(key,formatter.get(column.getFormatKey()).get(val));
                    }else if(Constants.THREE.equals(column.getFormatMethod())){
                        row.put(key,formatter.get(Constants.AREA_FORMAT).get(val));
                    }else if(Constants.FOUR.equals(column.getFormatMethod())){
                        row.put(key,formatter.get(Constants.DEPT_FORMAT).get(val));
                    }else {
                        row.put(key,val);
                    }
                }else {
                    row.put(key,val);
                }
            }
            rows.add(row);
        }
        return rows;
    }

    @DS(EbikeDataBaseConstant.SLAVE)
    private Map<String,Map<String,String>> getFormatter(List<ReportColumn> columns){
        Map<String,Map<String,String>> formatter = new HashMap<>();
        for (ReportColumn column:columns ) {
            //设置格式化方法
            if(!Constants.ONE.equals(column.getIfFormat())){
                continue;
            }
            if(Constants.ONE.equals(column.getFormatMethod()) && !formatter.containsKey(column.getFormatKey())){
                List<SysDictData> dataList = dictDataService.selectDictDataByType(column.getFormatKey());
                Map<String,String> dictMap = new HashMap<>();
                for(SysDictData dict: dataList){
                    dictMap.put(dict.getDictValue(),dict.getDictLabel());
                }
                formatter.put(column.getFormatKey(),dictMap);
            }else if(Constants.THREE.equals(column.getFormatMethod()) && !formatter.containsKey(Constants.AREA_FORMAT)){
                List<SysArea> areaList = areaService.selectAll();
                Map<String,String> areaMap = new HashMap<>();
                for(SysArea area: areaList){
                    areaMap.put(area.getAreaCode(),area.getAreaName());
                }
                formatter.put(Constants.AREA_FORMAT,areaMap);
            }else if(Constants.FOUR.equals(column.getFormatMethod()) && !formatter.containsKey(Constants.DEPT_FORMAT)){
                List<SysDept> deptList = deptService.selectAll();
                Map<String,String> deptMap = new HashMap<>();
                for(SysDept dept: deptList){
                    deptMap.put(dept.getDeptId(),dept.getDeptName());
                }
                formatter.put(Constants.DEPT_FORMAT,deptMap);
            }
        }
        return formatter;
    }

    public List<ReportColumn> search(List<ReportColumn> logsList){
        logsList.sort(Comparator.comparing(ReportColumn::getColumnSort));
        return logsList;
    }
}
