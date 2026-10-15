package com.chinatower.platform.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.analysis.api.AnalysisExportAPI;
import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.StatisticsExportParam;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.constants.Constants;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.vo.SysFileInfo;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.DateUtils;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.service.StatisticWordService;
import com.chinatower.platform.utils.CommonUtils;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatisticWordServiceImpl implements StatisticWordService {

    @Resource
    private AnalysisExportAPI analysisExportAPI;

    @Resource
    private CommonUtils fileTool;
    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;


    private static void wordTextReplace(XWPFDocument document,StatisticsExportParam param,StatisticsVO statisticsVO){
        //1.告警总览概述
        Map EventSheet1 = param.getEventSheet1();
        //2.告警时间统计
        List<Map> EventSheet2 = param.getEventSheet2();
        //2.告警时间压制统计
        List<Map> EventSheet22 = param.getEventSheet22();
        //3.摄像机告警数量统计
        List<Map> EventSheet3 = param.getEventSheet3();
        //4.告警来源统计
        List<Map> EventSheet4 = param.getEventSheet4();
        //4.告警来源准确率统计
        List<Map> EventSheet44 = param.getEventSheet44();
        //5.告警区域统计
        List<Map> EventSheet5 = param.getEventSheet5();
        //5.告警区域准确率统计
        List<Map> EventSheet55 = param.getEventSheet55();
        //6.告警类型统计统计
        List<Map> EventSheet6 = param.getEventSheet6();
        //6.告警类型统计准确率统计
        List<Map> EventSheet66 = param.getEventSheet66();
        //7.告警24小时统计
        List<Map> EventSheet7 = param.getEventSheet7();
        //8.告警区域完结率排行 8->10
        List<Map> EventSheet8 = param.getEventSheet10();
        //9.组织完结率排行
        List<Map> EventSheet9 = param.getEventSheet9();
        //10.物联设备告警统计
        List<Map> EventSheet10 = param.getEventSheet11();
        //11.网格完结率统计
        List<Map> EventSheet11 = param.getEventSheet12();
        Date date = new Date();
        //拼接 时间范围
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日");
        //导出时间
        String exportTime = outputFormat.format(date);
        String startTime = outputFormat.format(DateUtils.fomatDate(statisticsVO.getQueryStartTime()));
        String endTime = outputFormat.format(DateUtils.fomatDate(statisticsVO.getQueryEndTime()));
        String time = startTime.equals(endTime)?startTime:startTime+"至"+endTime;
        // 获取模板中所有的段落
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            // 获取段落中所有的文本块
            List<XWPFRun> runs = paragraph.getRuns();
            StringBuilder sb = new StringBuilder();
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text != null) {
                    sb.append(text);
                }
            }

            String fullText = sb.toString();
            String tableTitle = "";
            // 如果文本块包含要替换的文本
            // 替换文本
            //总体概述
            if (fullText.contains("{{exportTime}}")) {
                fullText = fullText.replace("{{exportTime}}", exportTime);
            }
            if (fullText.contains("{{startEndTime}}")) {
                fullText = fullText.replace("{{startEndTime}}", time);
            }
            if (fullText.contains("{{allNum}}")) {
                fullText = fullText.replace("{{allNum}}", String.valueOf(EventSheet1.get(Constants.ALL_NUM)));
            }
            if (fullText.contains("{{suppressNum}}")) {
                fullText = fullText.replace("{{suppressNum}}", String.valueOf(EventSheet1.get("suppressNum")));
            }
            if (fullText.contains("{{orderCompleteNum}}")) {
                fullText = fullText.replace("{{orderCompleteNum}}", String.valueOf(EventSheet1.get(Constants.ORDERCOMPLETE_NUM)));
            }
            if (fullText.contains("{{processNum}}")) {
                fullText = fullText.replace("{{processNum}}", String.valueOf(EventSheet1.get(Constants.PROCESS_NUM)));
            }
            if (fullText.contains("{{completionRate}}")) {
                fullText = fullText.replace("{{completionRate}}", "0".equals(String.valueOf(EventSheet1.get(Constants.ALL_NUM)))?"-":EventSheet1.get(Constants.COMPLETION_RATE)+"%");
            }
            if (fullText.contains("{{realNum}}")) {
                fullText = fullText.replace("{{realNum}}", String.valueOf(EventSheet1.get("realNum")));
            }
            if (fullText.contains("{{falseNum}}")) {
                fullText = fullText.replace("{{falseNum}}", String.valueOf(EventSheet1.get("falseNum")));
            }
            if (fullText.contains("{{repeatCompleteNum}}")) {
                fullText = fullText.replace("{{repeatCompleteNum}}", String.valueOf(EventSheet1.get("repeatCompleteNum")));
            }
            //时间统计
            List<Map>   map2 = EventSheet2.stream()
                    .sorted(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get("num")))).reversed()
                            .thenComparing(Comparator.comparing((Map m) -> LocalDate.parse(String.valueOf(m.get(Constants.ASSIGNED_DATE)))))
                    ).collect(Collectors.toList());

            List<Map>  map22 = EventSheet22.stream()
                    .sorted(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get("num")))).reversed()
                            .thenComparing(Comparator.comparing((Map m) -> LocalDate.parse(String.valueOf(m.get(Constants.ASSIGNED_DATE)))))
                    ).collect(Collectors.toList());
            if (fullText.contains("{{table1}}")) {
                tableTitle = fullText.replace("{{table1}}", "表1 告警时间统计");
                fullText ="";
            }
            if (fullText.contains("{{numMaxTime1}}")) {
                fullText = fullText.replace("{{numMaxTime1}}", String.valueOf(map2.get(0).get(Constants.ASSIGNED_DATE)));
            }
            if (fullText.contains("{{num1}}")) {
                fullText = fullText.replace("{{num1}}", String.valueOf(map2.get(0).get("num")));
            }

            if (fullText.contains("{{suppressNumMaxTime1}}")) {
                fullText = fullText.replace("{{suppressNumMaxTime1}}", String.valueOf(map22.get(0).get(Constants.ASSIGNED_DATE)));
            }

            if (fullText.contains("{{suppressNum1}}")) {
                fullText = fullText.replace("{{suppressNum1}}", String.valueOf(map22.get(0).get("num")));
            }
            //摄像机统计
            if (fullText.contains("{{table2}}")) {
                tableTitle = fullText.replace("{{table2}}", "表2 摄像机告警统计");
                fullText ="";
            }
            if (fullText.contains("{{deviceMaxName2}}")) {
                fullText = fullText.replace("{{deviceMaxName2}}", EventSheet3!= null && EventSheet3.size()>0 &&null!=EventSheet3.get(0).get(Constants.PARAM_DEVICE_NAME)?String.valueOf(EventSheet3.get(0).get(Constants.PARAM_DEVICE_NAME)):"-");
            }
            if (fullText.contains("{{allMaxNum2}}")) {
                fullText = fullText.replace("{{allMaxNum2}}", EventSheet3!= null && EventSheet3.size()>0 &&null!=EventSheet3.get(0).get(Constants.ALL_NUM)?String.valueOf(EventSheet3.get(0).get(Constants.ALL_NUM)):"-");
            }
            if (fullText.contains("{{deviceMinName2}}")) {
                fullText = fullText.replace("{{deviceMinName2}}", EventSheet3!= null && EventSheet3.size()>0 &&null!=EventSheet3.get(EventSheet3.size()-1).get(Constants.PARAM_DEVICE_NAME)?String.valueOf(EventSheet3.get(EventSheet3.size()-1).get(Constants.PARAM_DEVICE_NAME)):"-");
            }
            if (fullText.contains("{{allMinNum2}}")) {
                fullText = fullText.replace("{{allMinNum2}}", EventSheet3!= null && EventSheet3.size()>0 &&null!=EventSheet3.get(EventSheet3.size()-1).get(Constants.ALL_NUM)?String.valueOf(EventSheet3.get(EventSheet3.size()-1).get(Constants.ALL_NUM)):"-");
            }
            //摄像机统计
            if (fullText.contains("{{table33}}")) {
                tableTitle = fullText.replace("{{table33}}", "表3 物联设备告警统计");
                fullText ="";
            }
            if (fullText.contains("{{deviceMaxName33}}")) {
                fullText = fullText.replace("{{deviceMaxName33}}", EventSheet10!= null && EventSheet10.size()>0 &&null!=EventSheet10.get(0).get(Constants.PARAM_DEVICE_NAME)?String.valueOf(EventSheet10.get(0).get(Constants.PARAM_DEVICE_NAME)):"-");
            }
            if (fullText.contains("{{allMaxNum33}}")) {
                fullText = fullText.replace("{{allMaxNum33}}", EventSheet10!= null && EventSheet10.size()>0 &&null!=EventSheet10.get(0).get(Constants.ALL_NUM)?String.valueOf(EventSheet10.get(0).get(Constants.ALL_NUM)):"-");
            }
            if (fullText.contains("{{deviceMinName33}}")) {
                fullText = fullText.replace("{{deviceMinName33}}", EventSheet10!= null && EventSheet10.size()>0 &&null!=EventSheet10.get(EventSheet10.size()-1).get(Constants.PARAM_DEVICE_NAME)?String.valueOf(EventSheet10.get(EventSheet10.size()-1).get(Constants.PARAM_DEVICE_NAME)):"-");
            }
            if (fullText.contains("{{allMinNum33}}")) {
                fullText = fullText.replace("{{allMinNum33}}", EventSheet10!= null && EventSheet10.size()>0 &&null!=EventSheet10.get(EventSheet10.size()-1).get(Constants.ALL_NUM)?String.valueOf(EventSheet10.get(EventSheet10.size()-1).get(Constants.ALL_NUM)):"-");
            }
            //告警来源统计
            if (fullText.contains("{{table3}}")) {
                tableTitle = fullText.replace("{{table3}}", "表4 告警来源统计");
                fullText ="";
            }
            if (fullText.contains("{{numMaxWarningSourceName3}}")) {
                fullText = fullText.replace("{{numMaxWarningSourceName3}}", EventSheet4!= null && EventSheet4.size()>0 &&null!=EventSheet4.get(0).get(Constants.PARAM_WARNING_SOURCE_NAME)?String.valueOf(EventSheet4.get(0).get(Constants.PARAM_WARNING_SOURCE_NAME)):"-");
            }
            if (fullText.contains("{{num3}}")) {
                fullText = fullText.replace("{{num3}}", EventSheet4!= null && EventSheet4.size()>0 &&null!=EventSheet4.get(0).get("num")?String.valueOf(EventSheet4.get(0).get("num")):"-");
            }
            if (fullText.contains("{{rateMaxWarningSourceName3}}")) {
                fullText = fullText.replace("{{rateMaxWarningSourceName3}}", EventSheet44!= null && EventSheet44.size()>0 &&null!=EventSheet44.get(0).get("sourceName")?String.valueOf(EventSheet44.get(0).get("sourceName")):"-");
            }
            if (fullText.contains("{{rate3}}")) {
                fullText = fullText.replace("{{rate3}}", EventSheet44!= null && EventSheet44.size()>0 &&null!=EventSheet44.get(0).get(Constants.ACCURACY)?EventSheet44.get(0).get(Constants.ACCURACY)+"%":"-");
            }
            //告警区域统计
            if (fullText.contains("{{table4}}")) {
                tableTitle = fullText.replace("{{table4}}", "表5 告警区域统计");
                fullText ="";
            }
            if (fullText.contains("{{numMaxRegionName4}}")) {
                fullText = fullText.replace("{{numMaxRegionName4}}", EventSheet5!= null && EventSheet5.size()>0 &&null!=EventSheet5.get(0).get(Constants.REGION_NAME)?String.valueOf(EventSheet5.get(0).get(Constants.REGION_NAME)):"-");
            }
            if (fullText.contains("{{num4}}")) {
                fullText = fullText.replace("{{num4}}", EventSheet5!= null && EventSheet5.size()>0 &&null!=EventSheet5.get(0).get("num")?String.valueOf(EventSheet5.get(0).get("num")):"-");
            }
            if (fullText.contains("{{rateMaxRegionName4}}")) {
                fullText = fullText.replace("{{rateMaxRegionName4}}", EventSheet55!= null && EventSheet55.size()>0 &&null!=EventSheet55.get(0).get(Constants.REGION_NAME)?String.valueOf(EventSheet55.get(0).get(Constants.REGION_NAME)):"-");
            }
            if (fullText.contains("{{rate4}}")) {
                fullText = fullText.replace("{{rate4}}", EventSheet55!= null && EventSheet55.size()>0 &&null!=EventSheet55.get(0).get(Constants.ACCURACY)?EventSheet55.get(0).get(Constants.ACCURACY)+"%":"-");
            }
            //告警类型统计
            if (fullText.contains("{{table5}}")) {
                tableTitle = fullText.replace("{{table5}}", "表6 告警类型统计");
                fullText ="";
            }
            if (fullText.contains("{{numMaxTypeName5}}")) {
                fullText = fullText.replace("{{numMaxTypeName5}}", EventSheet6!= null && EventSheet6.size()>0 &&null!=EventSheet6.get(0).get(Constants.TYPE_NAME)?String.valueOf(EventSheet6.get(0).get(Constants.TYPE_NAME)):"-");
            }
            if (fullText.contains("{{num5}}")) {
                fullText = fullText.replace("{{num5}}", EventSheet6!= null && EventSheet6.size()>0 &&null!=EventSheet6.get(0).get("num")?String.valueOf(EventSheet6.get(0).get("num")):"-");
            }
            if (fullText.contains("{{rateMaxTypeName5}}")) {
                fullText = fullText.replace("{{rateMaxTypeName5}}", EventSheet66!= null && EventSheet66.size()>0 &&null!=EventSheet66.get(0).get(Constants.TYPE_NAME)?String.valueOf(EventSheet66.get(0).get(Constants.TYPE_NAME)):"-");
            }
            if (fullText.contains("{{rate5}}")) {
                fullText = fullText.replace("{{rate5}}", EventSheet66!= null && EventSheet66.size()>0 &&null!=EventSheet66.get(0).get(Constants.ACCURACY)?EventSheet66.get(0).get(Constants.ACCURACY)+"%":"-");
            }
            //告警24小时统计
            List<Map> map7 = EventSheet7.stream()
                    .sorted(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get("num")))).reversed()
                            .thenComparing(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get(Constants.PARAM_LABEL)).split(":")[0])))
                    ).collect(Collectors.toList());
            List<Map> map77 = EventSheet7.stream()
                    .sorted(Comparator.comparing((Map m) -> Double.parseDouble(String.valueOf(m.get(Constants.ACCURACY)))).reversed()
                            .thenComparing(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get(Constants.PARAM_LABEL)).split(":")[0])))
                    ).collect(Collectors.toList());
            if (fullText.contains("{{table6}}")) {
                tableTitle = fullText.replace("{{table6}}", "表7 24小时准确率分布");
                fullText ="";
            }
            if (fullText.contains("{{numMaxTime6}}")) {
                fullText = fullText.replace("{{numMaxTime6}}", String.valueOf(map7.get(0).get(Constants.PARAM_LABEL)));
            }
            if (fullText.contains("{{num6}}")) {
                fullText = fullText.replace("{{num6}}", String.valueOf(map7.get(0).get("num")));
            }
            if (fullText.contains("{{rateMaxTime6}}")) {
                fullText = fullText.replace("{{rateMaxTime6}}", String.valueOf(map77.get(0).get(Constants.PARAM_LABEL)));
            }
            if (fullText.contains("{{rate6}}")) {
                fullText = fullText.replace("{{rate6}}", map77.get(0).get(Constants.ACCURACY)+"%");
            }
            //告警区域完结率统计
            List<Map> map8 =null;
            if ( EventSheet8!= null && EventSheet8.size()>0){

                map8= EventSheet8.stream()
                        .sorted(Comparator.comparing((Map m) -> Double.parseDouble(String.valueOf(m.get(Constants.COMPLETION_RATES)))).reversed()
                                .thenComparing(Comparator.comparing((Map m) -> Long.parseLong("null".equals(String.valueOf(m.get("provinceId")))?"0":String.valueOf(m.get("provinceId")))))
                                .thenComparing(Comparator.comparing((Map m) -> Long.parseLong("null".equals(String.valueOf(m.get("cityId")))?"0":String.valueOf(m.get("cityId")))))
                                .thenComparing(Comparator.comparing((Map m) -> Long.parseLong("null".equals(String.valueOf(m.get("countyId")))?"0":String.valueOf(m.get("countyId")))))
                                .thenComparing(Comparator.comparing((Map m) -> Long.parseLong("null".equals(String.valueOf(m.get("townId")))?"0":String.valueOf(m.get("townId")))))
                        ).collect(Collectors.toList());
            }
            if (fullText.contains("{{table7}}")) {
                tableTitle = fullText.replace("{{table7}}", "表8 区域完结率");
                fullText ="";
            }
            if (fullText.contains("{{rateMaxRegionName7}}")) {
                fullText = fullText.replace("{{rateMaxRegionName7}}", EventSheet8!= null && EventSheet8.size()>0 &&null!=map8.get(0).get(Constants.REGION_NAME)?String.valueOf(map8.get(0).get(Constants.REGION_NAME)):"-");
            }
            if (fullText.contains("{{rate7}}")) {
                fullText = fullText.replace("{{rate7}}", EventSheet8!= null && EventSheet8.size()>0 &&null!=map8.get(0).get(Constants.COMPLETION_RATE)?map8.get(0).get(Constants.COMPLETION_RATE)+"%":"-");
            }
            //组织完结率统计
            List<Map> map9 =null;
            if ( EventSheet9!= null && EventSheet9.size()>0){
                map9 = EventSheet9.stream()
                        .sorted(Comparator.comparing((Map m) -> Double.parseDouble(String.valueOf(m.get(Constants.COMPLETION_RATES)))).reversed()
                                .thenComparing(Comparator.comparing((Map m) -> String.valueOf(m.get("deptId"))))
                        ).collect(Collectors.toList());
            }
            if (fullText.contains("{{table8}}")) {
                tableTitle = fullText.replace("{{table8}}", "表9 组织完结率");
                fullText ="";
            }
            if (fullText.contains("{{rateMaxDeptName8}}")) {
                fullText = fullText.replace("{{rateMaxDeptName8}}", EventSheet9!= null && EventSheet9.size()>0 &&null!=map9.get(0).get(Constants.DEPT_NAME)?String.valueOf(map9.get(0).get(Constants.DEPT_NAME)):"-");
            }
            if (fullText.contains("{{rate8}}")) {
                fullText = fullText.replace("{{rate8}}", EventSheet9!= null && EventSheet9.size()>0 &&null!=map9.get(0).get(Constants.COMPLETION_RATE)?map9.get(0).get(Constants.COMPLETION_RATE)+"%":"-");
            }

            //网格完结率统计
            List<Map> map10 =null;
            if ( EventSheet11!= null && EventSheet11.size()>0){
                map10  = EventSheet11.stream().sorted(Comparator.comparing((Map m) ->Double.parseDouble(String.valueOf(m.get(Constants.COMPLETION_RATES)))).reversed().thenComparing((Map m) ->Integer.parseInt(String.valueOf(m.get("gridLevel")))).thenComparing((Map m) -> m.get("gridName"), Collator.getInstance(Locale.CHINA))).collect(Collectors.toList());
            }
            if (fullText.contains("{{table9}}")) {
                tableTitle = fullText.replace("{{table9}}", "表10网格完结率");
                fullText ="";
            }
            if (fullText.contains("{{rateMaxGridName9}}")) {
                fullText = fullText.replace("{{rateMaxGridName9}}", EventSheet11!= null && EventSheet11.size()>0 &&null!=map10.get(0).get(Constants.GRID_NAME)?String.valueOf(map10.get(0).get(Constants.GRID_NAME)):"-");
            }
            if (fullText.contains("{{rate9}}")) {
                fullText = fullText.replace("{{rate9}}", EventSheet11!= null && EventSheet11.size()>0 &&null!=map10.get(0).get(Constants.COMPLETION_RATE)?map10.get(0).get(Constants.COMPLETION_RATE)+"%":"-");
            }

            // 删除原始段落中的所有运行
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
            if (!StringUtil.isEmpty(fullText)){
                // 创建一个新运行，并将更新后的文本添加到该运行中
                XWPFRun newRun = paragraph.createRun();
                newRun.setText(fullText, 0);
                newRun.setFontFamily("宋体");
            }else {
                // 创建一个新运行，并将更新后的文本添加到该运行中
                XWPFRun newRun1 = paragraph.createRun();
                newRun1.setText(tableTitle, 0);
                newRun1.setFontSize(11);
                newRun1.setFontFamily("黑体");
            }
        }



        // 填充表格1数据
        XWPFTable table = document.getTables().get(0);
        // 删除表格的第二行
        table.removeRow(1);
        if(null != EventSheet2 && EventSheet2.size()>0 && null != EventSheet22 && EventSheet22.size()>0){
            for (int i = 0; i < EventSheet2.size(); i++) {
                XWPFTableRow row = table.createRow();
                tableNormalFormat(table,row,0, String.valueOf(i+1));
                tableNormalFormat(table,row,1, String.valueOf(EventSheet2.get(i).get(Constants.ASSIGNED_DATE)));
                tableNormalFormat(table,row,2,String.valueOf(EventSheet2.get(i).get("num")));
                tableNormalFormat(table,row,3,String.valueOf(EventSheet22.get(i).get("num")));
        }
    }

        // 填充表格2数据
        XWPFTable table2 = document.getTables().get(1);
        table2.setTableAlignment(TableRowAlign.CENTER);
        if(null != EventSheet3 && EventSheet3.size()>0){
            // 删除表格的第二行
            table2.removeRow(1);
            for (int i = 0; i < EventSheet3.size(); i++) {
                XWPFTableRow row = table2.createRow();
                tableNormalFormat(table2,row,0, String.valueOf(i+1));
                tableNormalFormat(table2,row,1, String.valueOf(EventSheet3.get(i).get("deviceCode")));
                tableNormalFormat(table2,row,2,null==EventSheet3.get(i).get(Constants.PARAM_DEVICE_NAME)?"-":String.valueOf(EventSheet3.get(i).get(Constants.PARAM_DEVICE_NAME)));
                tableNormalFormat(table2,row,3,String.valueOf(EventSheet3.get(i).get(Constants.ALL_NUM)));
                tableNormalFormat(table2,row,4,null==EventSheet3.get(i).get(Constants.ACCURACY)?"-":EventSheet3.get(i).get(Constants.ACCURACY)+"%");
                tableNormalFormat(table2,row,5,EventSheet3.get(i).get(Constants.COMPLETION_RATE)+"%");
                tableNormalFormat(table2,row,6,String.valueOf(EventSheet3.get(i).get("suppressNum")));
            }
        }

        // 填充表格33数据
        XWPFTable table33 = document.getTables().get(2);
        table33.setTableAlignment(TableRowAlign.CENTER);
        if(null != EventSheet10 && EventSheet10.size()>0){
            // 删除表格的第二行
            table33.removeRow(1);
            for (int i = 0; i < EventSheet10.size(); i++) {
                XWPFTableRow row = table33.createRow();
                tableNormalFormat(table33,row,0, String.valueOf(i+1));
                tableNormalFormat(table33,row,1, String.valueOf(EventSheet10.get(i).get("deviceCode")));
                tableNormalFormat(table33,row,2,null==EventSheet10.get(i).get(Constants.PARAM_DEVICE_NAME)?"-":String.valueOf(EventSheet10.get(i).get(Constants.PARAM_DEVICE_NAME)));
                tableNormalFormat(table33,row,3,String.valueOf(EventSheet10.get(i).get(Constants.ALL_NUM)));
                tableNormalFormat(table33,row,4,null==EventSheet10.get(i).get(Constants.ACCURACY)?"-":EventSheet10.get(i).get(Constants.ACCURACY)+"%");
                tableNormalFormat(table33,row,5,EventSheet10.get(i).get(Constants.COMPLETION_RATE)+"%");
            }
        }

        // 填充表格3数据
        String[] sourceStr = new String[]{"1","2","3","4","8","9","10","11"};
        String[] array = EventSheet4.stream().map(t -> String.valueOf(t.get(Constants.PARAM_WARNING_SOURCE))).toArray(String[]::new);
        List<String> list1 = new ArrayList<>(Arrays.asList(sourceStr));
        List<String> list2 = new ArrayList<>(Arrays.asList(array));
        list1.removeAll(list2);
        String[] strings = list1.toArray(new String[0]);
        for (String s : strings) {
            Map maps = new HashMap<>();
            switch (s){
                case "1":
                    maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"AI告警");
                    maps.put(Constants.NUM,"0");
                    EventSheet4.add(maps);
                    break;
                case "2":
                    maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"人工上报");
                    maps.put(Constants.NUM,"0");
                    EventSheet4.add(maps);
                    break;
                case "3":
                    maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"一键告警");
                    maps.put(Constants.NUM,"0");
                    EventSheet4.add(maps);
                    break;
                case "4":
                    maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"摄像机告警");
                    maps.put(Constants.NUM,"0");
                    EventSheet4.add(maps);
                    break;
                case "8":
                    maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"物联告警");
                    maps.put(Constants.NUM,"0");
                    EventSheet4.add(maps);
                    break;
                case "9":
                    maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"卫星告警");
                    maps.put(Constants.NUM,"0");
                    EventSheet4.add(maps);
                    break;
                case "10":
                    maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"雷达告警");
                    maps.put(Constants.NUM,"0");
                    EventSheet4.add(maps);
                    break;
                case "11":
                 maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"第三方告警");
                 maps.put(Constants.NUM,"0");
                 EventSheet4.add(maps);
                 break;
                default:
                    break;
            }
        }
        XWPFTable table3 = document.getTables().get(3);
        // 删除表格的第二行
        table3.removeRow(1);
            for (int i = 0; i < EventSheet4.size(); i++) {
                XWPFTableRow row = table3.createRow();
                tableNormalFormat(table3,row,0, String.valueOf(i+1));
                tableNormalFormat(table3,row,1, String.valueOf(EventSheet4.get(i).get(Constants.PARAM_WARNING_SOURCE_NAME)));
                tableNormalFormat(table3,row,2,String.valueOf(EventSheet4.get(i).get("num")));
                tableNormalFormat(table3,row,3,null==EventSheet4.get(i).get(Constants.ACCURACY)  ?"-":EventSheet4.get(i).get(Constants.ACCURACY)+"%");

            }


        // 填充表格4数据
        XWPFTable table4 = document.getTables().get(4);
        if(null != EventSheet5 && EventSheet5.size()>0){
            // 删除表格的第二行
            table4.removeRow(1);
            for (int i = 0; i < EventSheet5.size(); i++) {
                XWPFTableRow row = table4.createRow();
                tableNormalFormat(table4,row,0, String.valueOf(i+1));
                tableNormalFormat(table4,row,1,null==EventSheet5.get(i).get(Constants.REGION_NAME)?"-":String.valueOf(EventSheet5.get(i).get(Constants.REGION_NAME)));
                tableNormalFormat(table4,row,2,String.valueOf(EventSheet5.get(i).get("num")));
                tableNormalFormat(table4,row,3,null==EventSheet5.get(i).get(Constants.ACCURACY)?"-":EventSheet5.get(i).get(Constants.ACCURACY)+"%");
            }
        }

        // 填充表格5数据
        XWPFTable table5 = document.getTables().get(5);
        if(null != EventSheet6 && EventSheet6.size()>0){
            // 删除表格的第二行
            table5.removeRow(1);
            for (int i = 0; i < EventSheet6.size(); i++) {
                XWPFTableRow row = table5.createRow();
                tableNormalFormat(table5,row,0, String.valueOf(i+1));
                tableNormalFormat(table5,row,1,null==EventSheet6.get(i).get(Constants.TYPE_NAME)?"-":String.valueOf(EventSheet6.get(i).get(Constants.TYPE_NAME)));
                tableNormalFormat(table5,row,2,String.valueOf(EventSheet6.get(i).get("num")));
                tableNormalFormat(table5,row,3,null==EventSheet6.get(i).get(Constants.ACCURACY)?"-":EventSheet6.get(i).get(Constants.ACCURACY)+"%");
            }
        }

        // 填充表格6数据
        XWPFTable table6 = document.getTables().get(6);
        if(null != EventSheet7 && EventSheet7.size()>0){
            // 删除表格的第二行
            table6.removeRow(1);
            for (int i = 0; i < EventSheet7.size(); i++) {
                XWPFTableRow row = table6.createRow();
                tableNormalFormat(table6,row,0, String.valueOf(i+1));
                tableNormalFormat(table6,row,1,String.valueOf(EventSheet7.get(i).get(Constants.PARAM_LABEL)));
                tableNormalFormat(table6,row,2,String.valueOf(EventSheet7.get(i).get("num")));
                tableNormalFormat(table6,row,3,EventSheet7.get(i).get(Constants.ACCURACY)+"%");
            }
        }

        // 填充表格7数据
        XWPFTable table7 = document.getTables().get(7);
        if(null != EventSheet8 && EventSheet8.size()>0){
            // 删除表格的第二行
            table7.removeRow(1);
            for (int i = 0; i < EventSheet8.size(); i++) {
                XWPFTableRow row = table7.createRow();
                tableNormalFormat(table7,row,0, String.valueOf(i+1));
                tableNormalFormat(table7,row,1,String.valueOf(EventSheet8.get(i).get(Constants.REGION_NAME)));
                tableNormalFormat(table7,row,2,String.valueOf(EventSheet8.get(i).get(Constants.ALL_NUM)));
                tableNormalFormat(table7,row,3,null==EventSheet8.get(i).get(Constants.COMPLETION_RATE)?"-":EventSheet8.get(i).get(Constants.COMPLETION_RATE)+"%");
                tableNormalFormat(table7,row,4,String.valueOf(Math.round(Double.parseDouble(String.valueOf(EventSheet8.get(i).get(Constants.PROCESS_NUM))))));
                tableNormalFormat(table7,row,5,String.valueOf(Math.round(Double.parseDouble(String.valueOf(EventSheet8.get(i).get(Constants.ACCEPTANCE_NUM))))));
                tableNormalFormat(table7,row,6,String.valueOf(EventSheet8.get(i).get(Constants.JUDGR_NUM)));
                tableNormalFormat(table7,row,7,String.valueOf(EventSheet8.get(i).get(Constants.DISPATCH_NUM)));
                tableNormalFormat(table7,row,8,String.valueOf(EventSheet8.get(i).get(Constants.HANDLE_NUM)));
                tableNormalFormat(table7,row,9,String.valueOf(EventSheet8.get(i).get(Constants.APPROVE_NUM)));
                tableNormalFormat(table7,row,10,String.valueOf(EventSheet8.get(i).get(Constants.ORDERCOMPLETE_NUM)));
                tableNormalFormat(table7,row,11,String.valueOf(EventSheet8.get(i).get(Constants.AVG_TIME_DAY)));
            }
        }

        // 填充表格8数据
        XWPFTable table8 = document.getTables().get(8);
        if(null != EventSheet9 && EventSheet9.size()>0){
            // 删除表格的第二行
            table8.removeRow(1);
            for (int i = 0; i < EventSheet9.size(); i++) {
                XWPFTableRow row = table8.createRow();
                tableNormalFormat(table8,row,0, String.valueOf(i+1));
                tableNormalFormat(table8,row,1,String.valueOf(EventSheet9.get(i).get(Constants.DEPT_NAME)));
                tableNormalFormat(table8,row,2,String.valueOf(EventSheet9.get(i).get(Constants.ALL_NUM)));
                tableNormalFormat(table8,row,3,null==EventSheet9.get(i).get(Constants.COMPLETION_RATE)?"-":EventSheet9.get(i).get(Constants.COMPLETION_RATE)+"%");
                tableNormalFormat(table8,row,4,String.valueOf(Math.round(Double.parseDouble(String.valueOf(EventSheet9.get(i).get(Constants.PROCESS_NUM))))));
                tableNormalFormat(table8,row,5,String.valueOf(Math.round(Double.parseDouble(String.valueOf(EventSheet9.get(i).get(Constants.ACCEPTANCE_NUM))))));
                tableNormalFormat(table8,row,6,String.valueOf(EventSheet9.get(i).get(Constants.JUDGR_NUM)));
                tableNormalFormat(table8,row,7,String.valueOf(EventSheet9.get(i).get(Constants.DISPATCH_NUM)));
                tableNormalFormat(table8,row,8,String.valueOf(EventSheet9.get(i).get(Constants.HANDLE_NUM)));
                tableNormalFormat(table8,row,9,String.valueOf(EventSheet9.get(i).get(Constants.APPROVE_NUM)));
                tableNormalFormat(table8,row,10,String.valueOf(EventSheet9.get(i).get(Constants.ORDERCOMPLETE_NUM)));
                tableNormalFormat(table8,row,11,String.valueOf(EventSheet9.get(i).get(Constants.AVG_TIME_DAY)));
            }
        }


        // 填充表格9数据
        XWPFTable table9 = document.getTables().get(9);
        if(null != EventSheet11 && EventSheet11.size()>0){
            // 删除表格的第二行
            table9.removeRow(1);
            for (int i = 0; i < EventSheet11.size(); i++) {
                XWPFTableRow row = table9.createRow();
                tableNormalFormat(table9,row,0, String.valueOf(i+1));
                tableNormalFormat(table9,row,1,String.valueOf(EventSheet11.get(i).get(Constants.GRID_NAME)));
                tableNormalFormat(table9,row,2,String.valueOf(EventSheet11.get(i).get(Constants.ALL_NUM)));
                tableNormalFormat(table9,row,3,null==EventSheet11.get(i).get(Constants.COMPLETION_RATE)?"-":EventSheet11.get(i).get(Constants.COMPLETION_RATE)+"%");
                tableNormalFormat(table9,row,4,String.valueOf(Math.round(Double.parseDouble(String.valueOf(EventSheet11.get(i).get(Constants.PROCESS_NUM))))));
                tableNormalFormat(table9,row,5,String.valueOf(Math.round(Double.parseDouble(String.valueOf(EventSheet11.get(i).get(Constants.ACCEPTANCE_NUM))))));
                tableNormalFormat(table9,row,6,String.valueOf(EventSheet11.get(i).get(Constants.JUDGR_NUM)));
                tableNormalFormat(table9,row,7,String.valueOf(EventSheet11.get(i).get(Constants.DISPATCH_NUM)));
                tableNormalFormat(table9,row,8,String.valueOf(EventSheet11.get(i).get(Constants.HANDLE_NUM)));
                tableNormalFormat(table9,row,9,String.valueOf(EventSheet11.get(i).get(Constants.APPROVE_NUM)));
                tableNormalFormat(table9,row,10,String.valueOf(EventSheet11.get(i).get(Constants.ORDERCOMPLETE_NUM)));
                tableNormalFormat(table9,row,11,String.valueOf(EventSheet11.get(i).get(Constants.AVG_TIME_DAY)));
            }
        }


    }

    /**
     * 方案 G V2.0：在报告末尾追加【九、下游任务统计】章节与表格（模板无占位时仍可输出）。
     */
    private static void appendSubSceneChapterNine(XWPFDocument document, StatisticsExportParam param) {
        List<Map> rankList = param.getEventSheetSubScene();
        List<Map> accuracyList = param.getEventSheetSubSceneAccuracy();

        XWPFParagraph title = document.createParagraph();
        title.setStyle("Heading1");
        XWPFRun titleRun = title.createRun();
        titleRun.setText("九、下游任务统计");
        titleRun.setBold(true);
        titleRun.setFontSize(14);
        titleRun.setFontFamily("宋体");

        String maxName = "-";
        String maxNum = "-";
        String maxAccName = "-";
        String maxAcc = "-";
        if (rankList != null && !rankList.isEmpty()) {
            Object nameObj = rankList.get(0).get(Constants.TYPE_NAME);
            Object numObj = rankList.get(0).get("num");
            maxName = nameObj == null ? "-" : String.valueOf(nameObj);
            maxNum = numObj == null ? "-" : String.valueOf(numObj);
        }
        if (accuracyList != null && !accuracyList.isEmpty()) {
            Object nameObj = accuracyList.get(0).get(Constants.TYPE_NAME);
            Object accObj = accuracyList.get(0).get(Constants.ACCURACY);
            maxAccName = nameObj == null ? "-" : String.valueOf(nameObj);
            maxAcc = accObj == null ? "-" : accObj + "%";
        }

        XWPFParagraph summary = document.createParagraph();
        XWPFRun summaryRun = summary.createRun();
        summaryRun.setText("本周期下游任务告警量最高为「" + maxName + "」（" + maxNum
                + "），准确率最高为「" + maxAccName + "」（" + maxAcc + "）。");
        summaryRun.setFontSize(12);
        summaryRun.setFontFamily("宋体");

        XWPFParagraph tableTitle = document.createParagraph();
        XWPFRun tableTitleRun = tableTitle.createRun();
        tableTitleRun.setText("表 下游任务统计");
        tableTitleRun.setBold(true);
        tableTitleRun.setFontFamily("宋体");

        XWPFTable table = document.createTable(1, 4);
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setText("序号");
        header.getCell(1).setText("下游任务名称");
        header.getCell(2).setText("告警数量");
        header.getCell(3).setText("准确率");
        if (rankList != null) {
            // 准确率按 typeName 映射，便于同行展示
            Map<String, Object> accuracyByName = new HashMap<>();
            if (accuracyList != null) {
                for (Map accRow : accuracyList) {
                    Object key = accRow.get(Constants.TYPE_NAME);
                    if (key != null) {
                        accuracyByName.put(String.valueOf(key), accRow.get(Constants.ACCURACY));
                    }
                }
            }
            for (int i = 0; i < rankList.size(); i++) {
                Map rowData = rankList.get(i);
                XWPFTableRow row = table.createRow();
                String typeName = rowData.get(Constants.TYPE_NAME) == null
                        ? "-" : String.valueOf(rowData.get(Constants.TYPE_NAME));
                Object acc = accuracyByName.get(typeName);
                row.getCell(0).setText(String.valueOf(i + 1));
                row.getCell(1).setText(typeName);
                row.getCell(2).setText(rowData.get("num") == null ? "-" : String.valueOf(rowData.get("num")));
                row.getCell(3).setText(acc == null ? "-" : acc + "%");
            }
        }
    }

    @Override
    public void exportEventWordStatistics(StatisticsVO statisticsVO, HttpServletResponse response) {
        statisticsVO.setPageNum(null);
        statisticsVO.setPageSize(null);
        statisticsVO.setPageNums(null);
        statisticsVO.setPageSizes(null);
        AnalysisVO analysisVO = new AnalysisVO();
        BeanUtils.copyProperties(statisticsVO, analysisVO);
        StatisticsExportParam param = new StatisticsExportParam();
        ByteArrayOutputStream bos = null;

        SysConfig sysConfig = fileTool.selectConfigByKey("tenant_event_template");
        if(ObjectUtil.isNull(sysConfig)){
            throw new CustomException("未找到该系统参数配置：tenant_event_template");
        }
        String fileUrl = fileUrlEncryptUtil.dealObsFileUrl(sysConfig.getConfigValue());
        // 读入Word模板
        // 1. 获取当前类的 ClassLoader 实例
        XWPFDocument document = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(fileUrl);
            inputStream = url.openStream();
            document = new XWPFDocument(inputStream);
            //异步获取数据
            List<Integer> paramList = Arrays.asList(1,2,22,3,33,4,44,5,55,6,66,17,18,7,8,9,10,11);
            ThreadParallelUtil.run(paramList, o->{
                this.eventMethodAll(statisticsVO,analysisVO,o,param);
                return null;
            }, ThreadParallelUtil.ConfigFactory.EVENT_EXPORT_CODE);
            //处理数据
            this.wordTextReplace(document,param,statisticsVO);
            // 方案 G V2.0：模板外追加【九、下游任务统计】（不改动类型章节六）
            appendSubSceneChapterNine(document, param);
            bos = new ByteArrayOutputStream();
            document.write(bos);
            byte[] bytes = bos.toByteArray();
            FileUtils.download(response, bytes, "");
        } catch (IOException e) {
            log.error(Constants.CHECK_EXPORT_FAIL,e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException var21) {
                    log.error(Constants.CHECK_EXPORT_FAIL,var21);
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException var21) {
                    log.error(Constants.CHECK_EXPORT_FAIL,var21);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException var20) {
                    log.error(Constants.CHECK_EXPORT_FAIL,var20);
                }
            }
        }
    }

    /**
     * 添加单元格普通格式的文本内容  宋体12
     */
    private static void tableNormalFormat(XWPFTable table,XWPFTableRow row, int i ,String value) {
        // 获取表格的第一行
        XWPFTableRow firstRow = table.getRow(1);
        //第一行样式赋值其他单元格
        row.getCell(i).getCTTc().addNewTcPr().set(firstRow.getCell(i).getCTTc().getTcPr());
        // 关闭表格的自动调整大小功能
        CTTblLayoutType tableLayout = table.getCTTbl().getTblPr().addNewTblLayout();
        tableLayout.setType(STTblLayoutType.FIXED);
        XWPFRun run1 = row.getCell(i).addParagraph().createRun();
        row.getCell(i).removeParagraph(0);
        row.getCell(i).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFParagraph paragraph = row.getCell(i).getParagraphs().get(0);
        // 获取段落的CTPPr对象（CTPPr表示段落的属性）
        CTPPr ppr = paragraph.getCTP().getPPr();
        if (ppr == null) {
            ppr = paragraph.getCTP().addNewPPr();
        }
        // 创建一个新的缩进属性对象
        CTInd indentation = ppr.addNewInd();
        // 设置缩进距离为1
        indentation.setFirstLineChars(BigInteger.ONE);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        // 设置行距
        CTSpacing spacing = paragraph.getCTP().addNewPPr().addNewSpacing();
        spacing.setLineRule(STLineSpacingRule.AUTO);
        spacing.setLine(BigInteger.valueOf(240)); // 单倍行距为1（240磅）
        run1.setText(value);
        run1.setFontSize(11);
        run1.setFontFamily("宋体");
    }

    private void eventMethodAll(StatisticsVO statisticsVO,AnalysisVO analysisVO,Integer num,StatisticsExportParam param){
        Map map = new HashMap();
        List<Map> listMap = new ArrayList<>();
        AnalysisVO vo = new AnalysisVO();
        switch (num){
            case 1:
                Result numAll = analysisExportAPI.getNumAll(statisticsVO);
                map = (Map) numAll.getData();
                param.setEventSheet1(map);
                break;
            case 2:
                //告警日期折线图
                Result eventNumListApp = analysisExportAPI.getEventNumListApp(analysisVO);
                listMap  = (List<Map>) eventNumListApp.getData();
                param.setEventSheet2(listMap);
                break;
            case 22:
                //告警压制折线图
                Result interceptNumApp = analysisExportAPI.getEventInterceptNumApp(analysisVO);
                listMap = (List<Map>) interceptNumApp.getData();
                param.setEventSheet22(listMap);
                break;
            case 3:
                //摄像机告警数量统计
                TableDataInfo deviceNumAll = analysisExportAPI.getDeviceNumAll(statisticsVO);
                listMap = (List<Map>) deviceNumAll.getRows();
                param.setEventSheet3(listMap);
                break;
            case 4:
                //告警来源排行
                TableDataInfo alarmNumSource = analysisExportAPI.getEventAlarmNumSource(statisticsVO);
                listMap  = (List<Map>) alarmNumSource.getRows();
                param.setEventSheet4(listMap);
                break;
            case 44:
                //告警来源准确率排行
                TableDataInfo sourceAccuracy = analysisExportAPI.getEvenSourceAccuracy(analysisVO);
                listMap  = (List<Map>) sourceAccuracy.getRows();
                param.setEventSheet44(listMap);
                break;
            case 5:
                BeanUtils.copyProperties(analysisVO, vo);
                //赋值纬度标识
                vo.setAreaBoxNum(analysisVO.getAreaSelect());
                //告警区域排行
                TableDataInfo alarmNumArea = analysisExportAPI.getEventNumByArea(vo);
                listMap = (List<Map>) alarmNumArea.getRows();
                param.setEventSheet5(listMap);
                break;
            case 55:
                BeanUtils.copyProperties(analysisVO, vo);
                //赋值纬度标识
                vo.setAreaBoxNum(analysisVO.getAreaSelect());
                //告警区域准确率排行
                TableDataInfo alarmNumAreaRate = analysisExportAPI.getEvenAreaAccuracy(vo);
                listMap  = (List<Map>) alarmNumAreaRate.getRows();
                param.setEventSheet55(listMap);
                break;
            case 6:
                //告警类型排行
                TableDataInfo typeYearMonthApp = analysisExportAPI.getDevTypeYearMonthApp(statisticsVO);
                listMap  = (List<Map>) typeYearMonthApp.getRows();
                param.setEventSheet6(listMap);
                break;
            case 66:
                //告警类型准确率排行
                TableDataInfo evenTypeAccuracy = analysisExportAPI.getEvenTypeAccuracy(analysisVO);
                listMap  = (List<Map>) evenTypeAccuracy.getRows();
                param.setEventSheet66(listMap);
                break;
            case 17:
                // 下游任务排行（方案 G V2.0）
                StatisticsVO subSceneRankVo = new StatisticsVO();
                BeanUtils.copyProperties(statisticsVO, subSceneRankVo);
                subSceneRankVo.setStatDimension("SUB_TASK");
                TableDataInfo subSceneRank = analysisExportAPI.getDevTypeYearMonthApp(subSceneRankVo);
                listMap = (List<Map>) subSceneRank.getRows();
                param.setEventSheetSubScene(listMap);
                break;
            case 18:
                // 下游任务准确率排行（方案 G V2.0）
                AnalysisVO subSceneAccuracyVo = new AnalysisVO();
                BeanUtils.copyProperties(analysisVO, subSceneAccuracyVo);
                subSceneAccuracyVo.setStatDimension("SUB_TASK");
                TableDataInfo subSceneAccuracy = analysisExportAPI.getEvenTypeAccuracy(subSceneAccuracyVo);
                listMap = (List<Map>) subSceneAccuracy.getRows();
                param.setEventSheetSubSceneAccuracy(listMap);
                break;
            case 7:
                //告警24h折线图
                Result eventNumListAppHour = analysisExportAPI.getEventNumByHour(analysisVO);
                listMap  = (List<Map>) eventNumListAppHour.getData();
                param.setEventSheet7(listMap);
                break;
            case 8:
                BeanUtils.copyProperties(analysisVO, vo);
                //赋值纬度标识
                vo.setAreaBoxNum(analysisVO.getAreaCompletionSelect());
                //行政区域完结率统计
                TableDataInfo evenAreaCompletion = analysisExportAPI.getEvenAreaCompletion(vo);
                listMap = (List<Map>) evenAreaCompletion.getRows();
                param.setEventSheet10(listMap);
                break;
            case 9:
                //组织完结率统计
                TableDataInfo deptCompletion = analysisExportAPI.getEvenDeptCompletion(analysisVO);
                listMap  = (List<Map>) deptCompletion.getRows();
                param.setEventSheet9(listMap);
                break;
            case 10:
                //物联设备告警统计
                TableDataInfo iotDeviceNumAll = analysisExportAPI.getIotDeviceNumAll(statisticsVO);
                listMap  = (List<Map>) iotDeviceNumAll.getRows();
                param.setEventSheet11(listMap);
                break;
            case 11:
                //网格完结率统计
                TableDataInfo gridCompletion = analysisExportAPI.getEvenGridCompletion(analysisVO);
                listMap  = (List<Map>) gridCompletion.getRows();
                param.setEventSheet12(listMap);
                break;
            default: break;
        }
    }


}
