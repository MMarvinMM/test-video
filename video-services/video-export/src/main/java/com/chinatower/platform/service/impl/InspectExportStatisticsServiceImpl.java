package com.chinatower.platform.service.impl;

import com.chinatower.analysis.api.entity.*;
import com.chinatower.bdm.api.InspectStatisticsAPI;
import com.chinatower.bdm.api.entity.InspectStatisticsVo;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.constants.Constants;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.service.InspectExportStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InspectExportStatisticsServiceImpl implements InspectExportStatisticsService {

    @Resource
    private InspectStatisticsAPI inspectStatisticsAPI;

    @Override
    public void exportInspectStatistics(InspectStatisticsVo inspectStatisticsVo, HttpServletResponse response) {
        inspectStatisticsVo.setPageNum(null);
        inspectStatisticsVo.setPageSize(null);
        StatisticsExportParam param = new StatisticsExportParam();
        List<Integer> paramList = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16);
        ThreadParallelUtil.run(paramList,o->{
            this.inspectMethodAll(inspectStatisticsVo,o,param);
            return null;
        }, ThreadParallelUtil.ConfigFactory.EVENT_EXPORT_CODE);
        ByteArrayOutputStream bos = null;
        XSSFWorkbook wb = new XSSFWorkbook();
        CellStyle cellStyle = this.exportValueStyle(wb);
        try {

                    //巡查总览
                    wb = createInspectSheet1(wb, inspectStatisticsVo,param,cellStyle);

                    //网格层级数量排行
                    wb = createInspectSheet2(wb, inspectStatisticsVo,param,cellStyle);

                    //网格员性别统计
                    wb = createInspectSheet3(wb, inspectStatisticsVo,param,cellStyle);

                    //网格员年龄分布
                    wb = createInspectSheet4(wb, inspectStatisticsVo,param,cellStyle);

                    //网格统计
                    wb = createInspectSheet5(wb, inspectStatisticsVo,param,cellStyle);

                    //网格出勤率排行
                    wb = createInspectSheet6(wb, inspectStatisticsVo,param,cellStyle);

                    //网格长出勤率排行
                    wb = createInspectSheet7(wb, inspectStatisticsVo,param,cellStyle);

                    //普通网格员出勤率排行
                    wb = createInspectSheet8(wb, inspectStatisticsVo,param,cellStyle);

                    //网格打卡状态统计
                    wb = createInspectSheet9(wb, inspectStatisticsVo,param,cellStyle);

                    //网格员打卡状态统计
                    wb = createInspectSheet10(wb, inspectStatisticsVo,param,cellStyle);

                    //网格巡查任务数量排行
                    wb = createInspectSheet11(wb, inspectStatisticsVo,param,cellStyle);

                    //巡查任务反馈端统计
                    wb = createInspectSheet12(wb, inspectStatisticsVo,param,cellStyle);

                    //巡查反馈24小时分布
                    wb = createInspectSheet13(wb, inspectStatisticsVo,param,cellStyle);

                    //巡查任务按日期统计
                    wb = createInspectSheet14(wb, inspectStatisticsVo,param,cellStyle);

                    //巡查人任务数量排行
                    wb = createInspectSheet15(wb, inspectStatisticsVo,param,cellStyle);

                    //网格任务完成率排行
                    wb = createInspectSheet16(wb, inspectStatisticsVo,param,cellStyle);

            bos = new ByteArrayOutputStream();
            wb.write(bos);
            byte[] bytes = bos.toByteArray();
            FileUtils.download(response, bytes, "");
        } catch (IOException e) {
            log.error(Constants.CHECK_EXPORT_FAIL,e);
        } finally {
            if (wb != null) {
                try {
                    wb.close();
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



    private XSSFWorkbook createInspectSheet1(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("巡查总览");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET1.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET1[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //巡查统计总览
        Map map = param.getInspectSheet1();
        if(null != map){
            //创建单元格
            XSSFRow rowi = sheet.createRow(1);
            XSSFCell gridNum=rowi.createCell(0);
            XSSFCell gridMarkNum=rowi.createCell(1);
            XSSFCell gridNoMarkNum=rowi.createCell(2);
            XSSFCell gridKeeperNum=rowi.createCell(3);
            XSSFCell gridKeeperOneNum=rowi.createCell(4);
            XSSFCell gridKeeperTwoNum=rowi.createCell(5);
            XSSFCell inspectionTaskNum=rowi.createCell(6);
            XSSFCell taskCompletedNum=rowi.createCell(7);
            XSSFCell taskIncompleteNum=rowi.createCell(8);
            XSSFCell attendanceRate=rowi.createCell(9);
            XSSFCell normalCount=rowi.createCell(10);
            XSSFCell noNormalCount=rowi.createCell(11);

            //赋值字体样式
            gridNum.setCellStyle(cellStyle);
            gridMarkNum.setCellStyle(cellStyle);
            gridNoMarkNum.setCellStyle(cellStyle);
            gridKeeperNum.setCellStyle(cellStyle);
            gridKeeperOneNum.setCellStyle(cellStyle);
            gridKeeperTwoNum.setCellStyle(cellStyle);
            inspectionTaskNum.setCellStyle(cellStyle);
            taskCompletedNum.setCellStyle(cellStyle);
            taskIncompleteNum.setCellStyle(cellStyle);
            attendanceRate.setCellStyle(cellStyle);
            normalCount.setCellStyle(cellStyle);
            noNormalCount.setCellStyle(cellStyle);


            //单元格赋值
            gridNum.setCellValue(String.valueOf(map.get("gridNum")));
            gridMarkNum.setCellValue(String.valueOf(map.get("gridMarkNum")));
            gridNoMarkNum.setCellValue(String.valueOf(map.get("gridNoMarkNum")));
            gridKeeperNum.setCellValue(String.valueOf(map.get("gridKeeperNum")));
            gridKeeperOneNum.setCellValue(String.valueOf(map.get("gridKeeperOneNum")));
            gridKeeperTwoNum.setCellValue(String.valueOf(map.get("gridKeeperTwoNum")));
            inspectionTaskNum.setCellValue(String.valueOf(map.get(Constants.INSPECTION_TASK_NUM)));
            taskCompletedNum.setCellValue(String.valueOf(map.get(Constants.TASK_COMPLETED_NUM)));
            taskIncompleteNum.setCellValue(String.valueOf(map.get(Constants.TASK_INCOMPLETE_NUM)));
            attendanceRate.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(map.get(Constants.ATTENDANCE_RATE)))?Constants.SEP:(map.get(Constants.ATTENDANCE_RATE))+Constants.PER_CENT);
            normalCount.setCellValue(String.valueOf(map.get("normalCount")));
            noNormalCount.setCellValue(String.valueOf(map.get("noNormalCount")));
        }

        return wb;
    }



    private XSSFWorkbook createInspectSheet2(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("网格层级数量排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET2.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET2[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //网格层级数量排行
        List<Map> map = param.getInspectSheet2();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell gridLevel=rowi.createCell(0);
                XSSFCell gridNum=rowi.createCell(1);

                //赋值字体样式
                gridLevel.setCellStyle(cellStyle);
                gridNum.setCellStyle(cellStyle);

                //单元格赋值
                gridLevel.setCellValue(String.valueOf(map.get(i).get(Constants.GRID_LEVEL_NAME)));
                gridNum.setCellValue(String.valueOf(map.get(i).get("gridNum")));
            }
        }
        return wb;
    }

    private XSSFWorkbook createInspectSheet3(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("网格员性别统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET3.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET3[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //网格性别统计
        Map map = param.getInspectSheet3();
        if(null != map){
            List<Map> list = new ArrayList<>();
            Map map1 = new HashMap();
            map1.put(Constants.GENDER,"男性");
            map1.put("num",map.get("gridKeeperManNum"));
            map1.put("rate",map.get("manRate"));
            list.add(map1);
            Map map2 = new HashMap();
            map2.put(Constants.GENDER,"女性");
            map2.put("num",map.get("gridKeeperWomanNum"));
            map2.put("rate",map.get("womanRate"));
            list.add(map2);
            Map map3 = new HashMap();
            map3.put(Constants.GENDER,"未知");
            map3.put("num",map.get("unknownNum"));
            map3.put("rate",map.get("unknownRate"));
            list.add(map3);
            list = list.stream().sorted(Comparator.comparing((Map m) ->Integer.parseInt(String.valueOf(m.get("num")))).reversed()).collect(Collectors.toList());
            if(null != list && list.size()>0){
                for (int i = 0; i < list.size(); i++) {
                    //创建单元格
                    XSSFRow rowi = sheet.createRow(i+1);
                    XSSFCell gender=rowi.createCell(0);
                    XSSFCell num=rowi.createCell(1);
                    XSSFCell rate=rowi.createCell(2);

                    //赋值字体样式
                    gender.setCellStyle(cellStyle);
                    num.setCellStyle(cellStyle);
                    rate.setCellStyle(cellStyle);

                    //单元格赋值
                    gender.setCellValue(String.valueOf(list.get(i).get(Constants.GENDER)));
                    num.setCellValue(String.valueOf(list.get(i).get("num")));
                    rate.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(list.get(i).get("rate")))?Constants.SEP:(list.get(i).get("rate"))+Constants.PER_CENT);

                }
            }
        }

        return wb;
    }

    private XSSFWorkbook createInspectSheet4(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("网格员年龄分布");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET4.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET4[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //网格员年龄分布
        Map map = param.getInspectSheet4();
        if(null != map){
            List<Map> list = new ArrayList<>();
            Map map1 = new HashMap();
            map1.put("age","20岁以下");
            map1.put("num",map.get("lessTwenty"));
            map1.put("rate",map.get("lessTwentyRate"));
            list.add(map1);
            Map map2 = new HashMap();
            map2.put("age","21-30岁");
            map2.put("num",map.get("twentyToThirty"));
            map2.put("rate",map.get("twentyToThirtyRate"));
            list.add(map2);
            Map map3 = new HashMap();
            map3.put("age","31岁-40岁");
            map3.put("num",map.get("thirtyToForty"));
            map3.put("rate",map.get("thirtyToFortyRate"));
            list.add(map3);
            Map map4 = new HashMap();
            map4.put("age","41岁-50岁");
            map4.put("num",map.get("fortyToFifty"));
            map4.put("rate",map.get("fortyToFiftyRate"));
            list.add(map4);
            Map map5 = new HashMap();
            map5.put("age","50岁以上");
            map5.put("num",map.get("moreFifty"));
            map5.put("rate",map.get("moreFiftyRate"));
            list.add(map5);
            Map map6 = new HashMap();
            map6.put("age","未知");
            map6.put("num",map.get("unknownNum"));
            map6.put("rate",map.get("unknownRate"));
            list.add(map6);
            if(null != list && list.size()>0){
                for (int i = 0; i < list.size(); i++) {
                    //创建单元格
                    XSSFRow rowi = sheet.createRow(i+1);
                    XSSFCell age=rowi.createCell(0);
                    XSSFCell num=rowi.createCell(1);
                    XSSFCell rate=rowi.createCell(2);

                    //赋值字体样式
                    age.setCellStyle(cellStyle);
                    num.setCellStyle(cellStyle);
                    rate.setCellStyle(cellStyle);

                    //单元格赋值
                    age.setCellValue(String.valueOf(list.get(i).get("age")));
                    num.setCellValue(String.valueOf(list.get(i).get("num")));
                    rate.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(list.get(i).get("rate")))?Constants.SEP:(list.get(i).get("rate"))+Constants.PER_CENT);

                }
            }
        }

        return wb;
    }

    private XSSFWorkbook createInspectSheet5(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("网格统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET5.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET5[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //网格统计
        List<Map> map = param.getInspectSheet5();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell gridLevel=rowi.createCell(0);
                XSSFCell gridName=rowi.createCell(1);
                XSSFCell pointStatus=rowi.createCell(2);
                XSSFCell keeperName=rowi.createCell(3);
                XSSFCell gridKeeperNum=rowi.createCell(4);

                //赋值字体样式
                gridLevel.setCellStyle(cellStyle);
                gridName.setCellStyle(cellStyle);
                pointStatus.setCellStyle(cellStyle);
                keeperName.setCellStyle(cellStyle);
                gridKeeperNum.setCellStyle(cellStyle);

                //单元格赋值
                gridLevel.setCellValue(String.valueOf(map.get(i).get(Constants.GRID_LEVEL_NAME)));
                gridName.setCellValue(String.valueOf(map.get(i).get(Constants.GRID_NAME)));
                pointStatus.setCellValue("0".equals(String.valueOf(map.get(i).get("pointStatus")))?"未标绘":"已标绘");
                keeperName.setCellValue(String.valueOf(map.get(i).get(Constants.KEEPER_NAME)));
                gridKeeperNum.setCellValue(String.valueOf(map.get(i).get("gridKeeperNum")));
            }
        }
        return wb;
    }

    private XSSFWorkbook createInspectSheet6(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("网格出勤率排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET6.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET6[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //网格出勤率排行
        List<Map> map = param.getInspectSheet6();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell gridName=rowi.createCell(0);
                XSSFCell attendanceRate=rowi.createCell(1);

                //赋值字体样式
                gridName.setCellStyle(cellStyle);
                attendanceRate.setCellStyle(cellStyle);

                //单元格赋值
                gridName.setCellValue(String.valueOf(map.get(i).get(Constants.GRID_NAME)));
                attendanceRate.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(map.get(i).get(Constants.ATTENDANCE_RATE)))?Constants.SEP:(map.get(i).get(Constants.ATTENDANCE_RATE))+Constants.PER_CENT);

            }
        }
        return wb;
    }

    private XSSFWorkbook createInspectSheet7(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("网格长出勤率排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET7.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET7[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //网格长出勤率排行
        List<Map> map = param.getInspectSheet7();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell keeperName=rowi.createCell(0);
                XSSFCell attendanceRate=rowi.createCell(1);

                //赋值字体样式
                keeperName.setCellStyle(cellStyle);
                attendanceRate.setCellStyle(cellStyle);

                //单元格赋值
                keeperName.setCellValue(String.valueOf(map.get(i).get(Constants.KEEPER_NAME)));
                attendanceRate.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(map.get(i).get(Constants.ATTENDANCE_RATE)))?Constants.SEP:(map.get(i).get(Constants.ATTENDANCE_RATE))+Constants.PER_CENT);

            }
        }
        return wb;
    }

    private XSSFWorkbook createInspectSheet8(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("普通网格员出勤率排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET8.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET8[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //普通网格员出勤率排行
        List<Map> map = param.getInspectSheet8();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell keeperName=rowi.createCell(0);
                XSSFCell attendanceRate=rowi.createCell(1);

                //赋值字体样式
                keeperName.setCellStyle(cellStyle);
                attendanceRate.setCellStyle(cellStyle);

                //单元格赋值
                keeperName.setCellValue(String.valueOf(map.get(i).get(Constants.KEEPER_NAME)));
                attendanceRate.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(map.get(i).get(Constants.ATTENDANCE_RATE)))?Constants.SEP:(map.get(i).get(Constants.ATTENDANCE_RATE))+Constants.PER_CENT);

            }
        }
        return wb;
    }

    private XSSFWorkbook createInspectSheet9(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("网格打卡状态统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET9.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET9[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //网格打卡状态统计
        List<Map> map = param.getInspectSheet9();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell gridLevel=rowi.createCell(0);
                XSSFCell gridName=rowi.createCell(1);
                XSSFCell attendanceRate=rowi.createCell(2);
                XSSFCell inNum=rowi.createCell(3);
                XSSFCell lateNum =rowi.createCell(4);
                XSSFCell noInNum=rowi.createCell(5);
                XSSFCell outNum=rowi.createCell(6);
                XSSFCell earlyNum=rowi.createCell(7);
                XSSFCell noOutNum=rowi.createCell(8);

                //赋值字体样式
                gridLevel.setCellStyle(cellStyle);
                gridName.setCellStyle(cellStyle);
                attendanceRate.setCellStyle(cellStyle);
                inNum.setCellStyle(cellStyle);
                lateNum.setCellStyle(cellStyle);
                noInNum.setCellStyle(cellStyle);
                outNum.setCellStyle(cellStyle);
                earlyNum.setCellStyle(cellStyle);
                noOutNum.setCellStyle(cellStyle);

                //单元格赋值
                gridLevel.setCellValue(String.valueOf(map.get(i).get(Constants.GRID_LEVEL_NAME)));
                gridName.setCellValue(String.valueOf(map.get(i).get(Constants.GRID_NAME)));
                attendanceRate.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(map.get(i).get(Constants.ATTENDANCE_RATE)))?Constants.SEP:(map.get(i).get(Constants.ATTENDANCE_RATE))+Constants.PER_CENT);
                inNum.setCellValue(String.valueOf(map.get(i).get("inNum")));
                lateNum.setCellValue(String.valueOf(map.get(i).get("lateNum")));
                noInNum.setCellValue(String.valueOf(map.get(i).get("noInNum")));
                outNum.setCellValue(String.valueOf(map.get(i).get("outNum")));
                earlyNum.setCellValue(String.valueOf(map.get(i).get("earlyNum")));
                noOutNum.setCellValue(String.valueOf(map.get(i).get("noOutNum")));

            }
        }
        return wb;
    }

    private XSSFWorkbook createInspectSheet10(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("网格员打卡状态统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET10.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET10[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //网格员打卡状态统计
        List<Map> map = param.getInspectSheet10();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell gridLevel=rowi.createCell(0);
                XSSFCell gridName=rowi.createCell(1);
                XSSFCell keeperName=rowi.createCell(2);
                XSSFCell loginName=rowi.createCell(3);
                XSSFCell attendanceRate=rowi.createCell(4);
                XSSFCell inNum=rowi.createCell(5);
                XSSFCell lateNum =rowi.createCell(6);
                XSSFCell noInNum=rowi.createCell(7);
                XSSFCell outNum=rowi.createCell(8);
                XSSFCell earlyNum=rowi.createCell(9);
                XSSFCell noOutNum=rowi.createCell(10);

                //赋值字体样式
                gridLevel.setCellStyle(cellStyle);
                gridName.setCellStyle(cellStyle);
                keeperName.setCellStyle(cellStyle);
                loginName.setCellStyle(cellStyle);
                attendanceRate.setCellStyle(cellStyle);
                inNum.setCellStyle(cellStyle);
                lateNum.setCellStyle(cellStyle);
                noInNum.setCellStyle(cellStyle);
                outNum.setCellStyle(cellStyle);
                earlyNum.setCellStyle(cellStyle);
                noOutNum.setCellStyle(cellStyle);

                //单元格赋值
                gridLevel.setCellValue(String.valueOf(map.get(i).get(Constants.GRID_LEVEL_NAME)));
                gridName.setCellValue(String.valueOf(map.get(i).get(Constants.GRID_NAME)));
                keeperName.setCellValue(String.valueOf(map.get(i).get(Constants.KEEPER_NAME)));
                loginName.setCellValue(String.valueOf(map.get(i).get("loginName")));
                attendanceRate.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(map.get(i).get(Constants.ATTENDANCE_RATE)))?Constants.SEP:(map.get(i).get(Constants.ATTENDANCE_RATE))+Constants.PER_CENT);
                inNum.setCellValue(String.valueOf(map.get(i).get("inNum")));
                lateNum.setCellValue(String.valueOf(map.get(i).get("lateNum")));
                noInNum.setCellValue(String.valueOf(map.get(i).get("noInNum")));
                outNum.setCellValue(String.valueOf(map.get(i).get("outNum")));
                earlyNum.setCellValue(String.valueOf(map.get(i).get("earlyNum")));
                noOutNum.setCellValue(String.valueOf(map.get(i).get("noOutNum")));

            }
        }
        return wb;
    }

    private XSSFWorkbook createInspectSheet11(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("网格巡查任务数量排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET11.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET11[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //网格巡查任务数量排行
        List<Map> map = param.getInspectSheet11();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell gridName=rowi.createCell(0);
                XSSFCell inspectionTaskNum=rowi.createCell(1);
                XSSFCell taskCompletedNum=rowi.createCell(2);
                XSSFCell taskIncompleteNum=rowi.createCell(3);

                //赋值字体样式
                gridName.setCellStyle(cellStyle);
                inspectionTaskNum.setCellStyle(cellStyle);
                taskCompletedNum.setCellStyle(cellStyle);
                taskIncompleteNum.setCellStyle(cellStyle);

                //单元格赋值
                gridName.setCellValue(String.valueOf(map.get(i).get(Constants.GRID_NAME)));
                inspectionTaskNum.setCellValue(String.valueOf(map.get(i).get(Constants.INSPECTION_TASK_NUM)));
                taskCompletedNum.setCellValue(String.valueOf(map.get(i).get(Constants.TASK_COMPLETED_NUM)));
                taskIncompleteNum.setCellValue(String.valueOf(map.get(i).get(Constants.TASK_INCOMPLETE_NUM)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createInspectSheet12(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("巡查任务反馈端统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET12.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET12[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //巡查任务反馈端统计
        List<Map> map = param.getInspectSheet12();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell backName=rowi.createCell(0);
                XSSFCell num=rowi.createCell(1);
                XSSFCell rate=rowi.createCell(2);
                XSSFRow row2 = sheet.createRow(i+2);
                XSSFCell backName2=row2.createCell(0);
                XSSFCell num2=row2.createCell(1);
                XSSFCell rate2=row2.createCell(2);

                //赋值字体样式
                backName.setCellStyle(cellStyle);
                num.setCellStyle(cellStyle);
                rate.setCellStyle(cellStyle);
                backName2.setCellStyle(cellStyle);
                num2.setCellStyle(cellStyle);
                rate2.setCellStyle(cellStyle);

                //单元格赋值
                backName.setCellValue("PC");
                num.setCellValue(String.valueOf(map.get(i).get("inspectionTaskNumPc")));
                rate.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(map.get(i).get("pcRate")))?Constants.SEP:(map.get(i).get("pcRate"))+Constants.PER_CENT);
                backName2.setCellValue("APP");
                num2.setCellValue(String.valueOf(map.get(i).get("inspectionTaskNumApp")));
                rate2.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(map.get(i).get("appRate")))?Constants.SEP:(map.get(i).get("appRate"))+Constants.PER_CENT);

            }
        }
        return wb;
    }
    private XSSFWorkbook createInspectSheet13(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("巡查反馈24小时分布");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET13.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET13[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //巡查反馈24小时分布
        List<Map> map = param.getInspectSheet13();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell label=rowi.createCell(0);
                XSSFCell num=rowi.createCell(1);

                //赋值字体样式
                label.setCellStyle(cellStyle);
                num.setCellStyle(cellStyle);

                //单元格赋值
                label.setCellValue(String.valueOf(map.get(i).get("label")));
                num.setCellValue(String.valueOf(map.get(i).get("inspectionTaskBackNum")));
            }
        }
        return wb;
    }

    private XSSFWorkbook createInspectSheet14(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("巡查任务按日期统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET14.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET14[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //巡查任务按日期统计
        List<Map> map = param.getInspectSheet14();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell assignedDate=rowi.createCell(0);
                XSSFCell inspectionTaskNum=rowi.createCell(1);
                XSSFCell taskCompletedNum=rowi.createCell(2);
                XSSFCell taskIncompleteNum=rowi.createCell(3);
                //赋值字体样式
                assignedDate.setCellStyle(cellStyle);
                inspectionTaskNum.setCellStyle(cellStyle);
                taskCompletedNum.setCellStyle(cellStyle);
                taskIncompleteNum.setCellStyle(cellStyle);
                //单元格赋值
                assignedDate.setCellValue(String.valueOf(map.get(i).get("assignedDate")));
                inspectionTaskNum.setCellValue(String.valueOf(map.get(i).get(Constants.INSPECTION_TASK_NUM)));
                taskCompletedNum.setCellValue(String.valueOf(map.get(i).get(Constants.TASK_COMPLETED_NUM)));
                taskIncompleteNum.setCellValue(String.valueOf(map.get(i).get(Constants.TASK_INCOMPLETE_NUM)));

            }
        }
        return wb;
    }

    private XSSFWorkbook createInspectSheet15(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("巡查人任务数量排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET15.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET15[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //巡查人任务数量排行
        List<Map> map = param.getInspectSheet15();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell keeperName=rowi.createCell(0);
                XSSFCell inspectionTaskNum=rowi.createCell(1);
                XSSFCell taskCompletedNum=rowi.createCell(2);
                XSSFCell taskIncompleteNum=rowi.createCell(3);
                XSSFCell completedRate=rowi.createCell(4);
                //赋值字体样式
                keeperName.setCellStyle(cellStyle);
                inspectionTaskNum.setCellStyle(cellStyle);
                taskCompletedNum.setCellStyle(cellStyle);
                taskIncompleteNum.setCellStyle(cellStyle);
                completedRate.setCellStyle(cellStyle);
                //单元格赋值
                keeperName.setCellValue(String.valueOf(map.get(i).get(Constants.KEEPER_NAME)));
                inspectionTaskNum.setCellValue(String.valueOf(map.get(i).get(Constants.INSPECTION_TASK_NUM)));
                taskCompletedNum.setCellValue(String.valueOf(map.get(i).get(Constants.TASK_COMPLETED_NUM)));
                taskIncompleteNum.setCellValue(String.valueOf(map.get(i).get(Constants.TASK_INCOMPLETE_NUM)));
                completedRate.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(map.get(i).get("completedRate")))?Constants.SEP:(map.get(i).get("completedRate"))+Constants.PER_CENT);
            }
        }
        return wb;
    }

    private XSSFWorkbook createInspectSheet16(XSSFWorkbook wb,InspectStatisticsVo inspectStatisticsVo,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("网格任务完成率排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.INSPECT_SHEET16.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.INSPECT_SHEET16[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //网格任务完成率排行
        List<Map> map = param.getInspectSheet16();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell gridName=rowi.createCell(0);
                XSSFCell taskCompletedRate=rowi.createCell(1);

                //赋值字体样式
                gridName.setCellStyle(cellStyle);
                taskCompletedRate.setCellStyle(cellStyle);

                //单元格赋值
                gridName.setCellValue(String.valueOf(map.get(i).get(Constants.GRID_NAME)));
                taskCompletedRate.setCellValue(Constants.PARAM_NULL.equals(String.valueOf(map.get(i).get("taskCompletedRate"))) ? Constants.SEP : (map.get(i).get("taskCompletedRate"))+Constants.PER_CENT);
            }
        }
        return wb;
    }

    private void inspectMethodAll(InspectStatisticsVo inspectStatisticsVo,Integer num,StatisticsExportParam param){
        Map map = new HashMap();
        List<Map> listMap = new ArrayList<>();
        switch (num){
            case 1:
                //巡查总览
                Result overview = inspectStatisticsAPI.inspectOverview(inspectStatisticsVo);
                map = (Map) overview.getData();
                param.setInspectSheet1(map);
                break;
            case 2:
                //网格层级数量排行
                TableDataInfo gridLevel = inspectStatisticsAPI.getGridLevel(inspectStatisticsVo);
                listMap  = (List<Map>) gridLevel.getRows();
                param.setInspectSheet2(listMap);
                break;
            case 3:
                //网格员性别统计
                Result keeperGender = inspectStatisticsAPI.getGridKeeperGender(inspectStatisticsVo);
                map = (Map) keeperGender.getData();
                param.setInspectSheet3(map);
                break;
            case 4:
                //网格员年龄分布
                Result gridKeeperAge = inspectStatisticsAPI.getGridKeeperAge(inspectStatisticsVo);
                map = (Map) gridKeeperAge.getData();
                param.setInspectSheet4(map);
                break;
            case 5:
                //网格统计
                TableDataInfo gridStatistics = inspectStatisticsAPI.getGridStatistics(inspectStatisticsVo);
                listMap  = (List<Map>) gridStatistics.getRows();
                param.setInspectSheet5(listMap);
                break;
            case 6:
                //网格出勤率排行
                TableDataInfo gridAttendance = inspectStatisticsAPI.getGridAttendance(inspectStatisticsVo);
                listMap  = (List<Map>) gridAttendance.getRows();
                param.setInspectSheet6(listMap);
                break;
            case 7:
                //网格长出勤率排行
                inspectStatisticsVo.setKeeperType("1");
                TableDataInfo gridKeeperAttendance = inspectStatisticsAPI.getGridKeeperAttendance(inspectStatisticsVo);
                listMap  = (List<Map>) gridKeeperAttendance.getRows();
                param.setInspectSheet7(listMap);
                break;
            case 8:
                //网格员出勤率排行
                TableDataInfo keeperAttendance = inspectStatisticsAPI.getGridKeeperAttendanceByTwo(inspectStatisticsVo);
                param.setInspectSheet8((List<Map>) keeperAttendance.getRows());
                break;
            case 9:
                //网格打卡状态统计
                TableDataInfo gridClockState = inspectStatisticsAPI.getGridClockState(inspectStatisticsVo);
                listMap  = (List<Map>) gridClockState.getRows();
                param.setInspectSheet9(listMap);
                break;
            case 10:
                //网格员打卡状态统计
                TableDataInfo gridKeeperClockState = inspectStatisticsAPI.getGridKeeperClockState(inspectStatisticsVo);
                listMap  = (List<Map>) gridKeeperClockState.getRows();
                param.setInspectSheet10(listMap);
                break;
            case 11:
                //网格巡查任务数量排行
                TableDataInfo gridInspectTask = inspectStatisticsAPI.getGridInspectTask(inspectStatisticsVo);
                listMap  = (List<Map>) gridInspectTask.getRows();
                param.setInspectSheet11(listMap);
                break;
            case 12:
                //巡查任务反馈端统计
                TableDataInfo gridInspectTaskBack = inspectStatisticsAPI.getGridInspectTaskBack(inspectStatisticsVo);
                listMap  = (List<Map>) gridInspectTaskBack.getRows();
                param.setInspectSheet12(listMap);
                break;
            case 13:
                //巡查反馈24小时分布
                TableDataInfo taskBackByHour = inspectStatisticsAPI.getGridInspectTaskBackByHour(inspectStatisticsVo);
                listMap  = (List<Map>) taskBackByHour.getRows();
                param.setInspectSheet13(listMap);
                break;
            case 14:
                //巡查任务按日期统计
                TableDataInfo inspectTaskByDay = inspectStatisticsAPI.getGridInspectTaskByDay(inspectStatisticsVo);
                listMap  = (List<Map>) inspectTaskByDay.getRows();
                param.setInspectSheet14(listMap);
                break;
            case 15:
                //巡查人任务数量排行
                TableDataInfo taskByPerson = inspectStatisticsAPI.getGridInspectTaskByPerson(inspectStatisticsVo);
                listMap  = (List<Map>) taskByPerson.getRows();
                param.setInspectSheet15(listMap);
                break;
            case 16:
                //网格任务完成率排行
                TableDataInfo taskCompletionRate = inspectStatisticsAPI.getGridInspectTaskCompletionRate(inspectStatisticsVo);
                listMap  = (List<Map>) taskCompletionRate.getRows();
                param.setInspectSheet16(listMap);
                break;
            default: break;
        }
    }


    //设置字体样式,设置单元格样式表头
    private CellStyle exportFontAndTableStyle(XSSFWorkbook wb) {
        // 设置字体样式
        Font font = wb.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeightInPoints((short) 10); // 设置字体大小为10号
        font.setColor(IndexedColors.WHITE.getIndex());
        // 设置单元格样式
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);  // 文字居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    //设置导出值字体样式
    private CellStyle exportValueStyle(XSSFWorkbook wb) {
        // 设置字体样式
        Font font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10); // 设置字体大小为10号
        // 设置单元格样式
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);  // 文字居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    //设置自适应宽度
    private void exportWidthStyle(XSSFSheet sheet,CellStyle style) {
        // 获取表头行
        XSSFRow headerRow = sheet.getRow(0);
        // 将样式应用到所有表头单元格
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            XSSFCell cell = headerRow.getCell(i);
            cell.setCellStyle(style);
            // 获取单元格中的文本
            String headerText = cell.getStringCellValue();
            int textWidth =0;
            if (headerText.length() == 2){
                // 计算所需的宽度
                textWidth = headerText.length() * 256 * 6;
            }else {
                // 计算所需的宽度
                textWidth = headerText.length() * 256 * 3;
            }
            // 设置单元格宽度
            sheet.setColumnWidth(i, textWidth);
        }
    }

}
