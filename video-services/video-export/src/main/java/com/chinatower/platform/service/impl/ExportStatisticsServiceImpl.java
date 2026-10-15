package com.chinatower.platform.service.impl;
import com.chinatower.analysis.api.AnalysisExportAPI;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.constants.Constants;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.SysUserMapper;
import com.chinatower.platform.service.ExportStatisticsService;
import com.chinatower.platform.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExportStatisticsServiceImpl implements ExportStatisticsService {
    @Resource
    private StatisticService statisticService;
    @Resource
    private AnalysisExportAPI analysisExportAPI;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public void exportEventStatistics(StatisticsVO statisticsVO, HttpServletResponse response) {
        statisticsVO.setPageNum(null);
        statisticsVO.setPageSize(null);
        statisticsVO.setPageNums(null);
        statisticsVO.setPageSizes(null);
        AnalysisVO analysisVO = new AnalysisVO();
        BeanUtils.copyProperties(statisticsVO, analysisVO);
        StatisticsExportParam param = new StatisticsExportParam();
        // 17=下游任务排行，18=下游任务准确率排行
        List<Integer> paramList = Arrays.asList(1,2,22,3,17,4,5,6,66,7,8,9,10,11,18,12,13,14,15,16);
        ThreadParallelUtil.run(paramList,o->{
            this.eventMethodAll(statisticsVO,analysisVO,o,param);
            return null;
        }, ThreadParallelUtil.ConfigFactory.EVENT_EXPORT_CODE);
        ByteArrayOutputStream bos = null;
        XSSFWorkbook wb = new XSSFWorkbook();
        CellStyle cellStyle = this.exportValueStyle(wb);
        try {

                    //告警总览
                    wb = createEventSheet1(wb, statisticsVO,param,cellStyle);

                    //告警时间统计
                    wb = createEventSheet2(wb, analysisVO,param,cellStyle);

                   //告警类型排行
                   wb = createEventSheet3(wb, statisticsVO,param,cellStyle);

                   //下游任务排行（方案 G V2.0，类型产物之后新增）
                   wb = createEventSheetSubScene(wb, param, cellStyle);

                   //告警来源排行
                   wb = createEventSheet4(wb, statisticsVO,param,cellStyle);

                   //告警区域排行
                   wb = createEventSheet5(wb, analysisVO,param,cellStyle);

                    //告警24小时分布
                   wb = createEventSheet6(wb, analysisVO,param,cellStyle);

                   //摄像机告警统计
                   wb = createEventSheet7(wb, statisticsVO,param,cellStyle);

                   //物联告警统计
                   wb = createEventSheet15(wb, statisticsVO,param,cellStyle);

                   //告警准确率统计
                   wb = createEventSheet8(wb, statisticsVO,param,cellStyle);

                   //告警区域准确率排行
                   wb = createEventSheet9(wb, analysisVO,param,cellStyle);

                   //告警来源准确率排行
                   wb = createEventSheet10(wb, analysisVO,param,cellStyle);

                   //告警类型准确率排行
                   wb = createEventSheet11(wb, analysisVO,param,cellStyle);

                   //下游任务准确率排行（方案 G V2.0，类型准确率之后新增）
                   wb = createEventSheetSubSceneAccuracy(wb, param, cellStyle);

                   //告警准确率24小时分布
                   wb = createEventSheet12(wb, analysisVO,param,cellStyle);

                   //行政区域完结率统计
                   wb = createEventSheet13(wb, analysisVO,param,cellStyle);

                   //组织完结率统计
                   wb = createEventSheet14(wb, analysisVO,param,cellStyle);

                   //网格完结率统计
                   wb = createEventSheet16(wb, analysisVO,param,cellStyle);


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

    @Override
    public void exportDeviceStatistics(AnalysisVO analysisVO, HttpServletResponse response) {
        analysisVO.setPageNum(null);
        analysisVO.setPageSize(null);
        StatisticsExportParam param = new StatisticsExportParam();
        List<Integer> paramList = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14);
        ThreadParallelUtil.run(paramList,o->{
            this.deviceMethodAll(null,analysisVO,o,param);
            return null;
        }, ThreadParallelUtil.ConfigFactory.DEVICE_EXPORT_CODE);
        ByteArrayOutputStream bos = null;
        XSSFWorkbook wb = new XSSFWorkbook();
        CellStyle cellStyle = this.exportValueStyle(wb);
        try {
            //摄像机总览
            wb = createDeviceSheet1(wb, analysisVO,param,cellStyle);

            //摄像机数量区域排行
            wb = createDeviceSheet2(wb, analysisVO,param,cellStyle);

            //摄像机订购算法排行
            wb = createDeviceSheet3(wb, analysisVO,param,cellStyle);

            //通道算法任务排行
            wb = createDeviceSheet4(wb, analysisVO,param,cellStyle);

            //物联设备总览
            wb = createDeviceSheet5(wb, analysisVO,param,cellStyle);

            //物联设备数量区域排行
            wb = createDeviceSheet6(wb, analysisVO,param,cellStyle);

            //无人机总览
            wb = createDeviceSheet7(wb, analysisVO,param,cellStyle);

            //无人机数量区域排行
            wb = createDeviceSheet8(wb, analysisVO,param,cellStyle);

            //雷达总览
            wb = createDeviceSheet9(wb, analysisVO,param,cellStyle);

            //雷达数量区域排行
            wb = createDeviceSheet10(wb, analysisVO,param,cellStyle);

            //无人机总览
            wb = createDeviceSheet11(wb, analysisVO,param,cellStyle);

            //无人机数量区域排行
            wb = createDeviceSheet12(wb, analysisVO,param,cellStyle);

            //执法记录仪总览
            wb = createDeviceSheet13(wb, analysisVO,param,cellStyle);

            //执法记录仪数量区域排行
            wb = createDeviceSheet14(wb, analysisVO,param,cellStyle);
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

    @Override
    public void exportUserStatistics(CustomVO customVO, HttpServletResponse response) {
        customVO.setPageNum(null);
        customVO.setPageSize(null);
        StatisticsExportParam param = new StatisticsExportParam();
        List<Integer> paramList = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13);
        ThreadParallelUtil.run(paramList,o->{
            this.userMethodAll(customVO,o,param);
            return null;
        }, ThreadParallelUtil.ConfigFactory.USER_EXPORT_CODE);
        ByteArrayOutputStream bos = null;
        XSSFWorkbook wb = new XSSFWorkbook();
        CellStyle cellStyle = this.exportValueStyle(wb);
        try {
            //用户总览
            wb = createUserSheet1(wb, customVO,param,cellStyle);

            //用户登录时间统计
            wb = createUserSheet2(wb, customVO,param,cellStyle);

            //用户登录次数
            wb = createUserSheet3(wb, customVO,param,cellStyle);

            //用户登录时长 PC
            wb = createUserSheet13(wb, customVO,param,cellStyle);

            //用户登录时长 APP
            wb = createUserSheet14(wb, customVO,param,cellStyle);

            //用户登录时长 小程序
            wb = createUserSheet15(wb, customVO,param,cellStyle);

            //用户登录24小时分布
            wb = createUserSheet4(wb, customVO,param,cellStyle);

            //PC操作系统
            wb = createUserSheet5(wb, customVO,param,cellStyle);

            //PC浏览器排行
            wb = createUserSheet6(wb, customVO,param,cellStyle);

            //APP操作系统
            wb = createUserSheet7(wb, customVO,param,cellStyle);


            //APP手机型号排行
            wb = createUserSheet8(wb, customVO,param,cellStyle);


            //APP版本排行
            wb = createUserSheet9(wb, customVO,param,cellStyle);


            //用户登录成功率分析
            wb = createUserSheet10(wb, customVO,param,cellStyle);

            //用户登录失败原因分析
            wb = createUserSheet11(wb, customVO,param,cellStyle);

            //用户数量组织排行
            wb = createUserSheet12(wb, customVO,param,cellStyle);
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

    private XSSFWorkbook createEventSheet1(XSSFWorkbook wb, StatisticsVO statisticsVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("告警总览");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET1.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET1[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        Map map = param.getEventSheet1();

        if(null != map){
            //创建单元格
            XSSFRow rowi = sheet.createRow(1);
            XSSFCell cellNum=rowi.createCell(0);
            XSSFCell cellReal=rowi.createCell(1);
            XSSFCell cellRepeat=rowi.createCell(2);
            XSSFCell cellFalse=rowi.createCell(3);
            XSSFCell cellAccuracy=rowi.createCell(4);
            XSSFCell cellProcess=rowi.createCell(5);
            XSSFCell cellAcceptance=rowi.createCell(6);
            XSSFCell cellJudge=rowi.createCell(7);
            XSSFCell cellDispatch=rowi.createCell(8);
            XSSFCell cellHandle=rowi.createCell(9);
            XSSFCell cellApprove=rowi.createCell(10);
            XSSFCell cellComplete=rowi.createCell(11);
            XSSFCell cellCompletionRate=rowi.createCell(12);
            XSSFCell cellSuppress=rowi.createCell(13);

            //赋值字体样式
            cellNum.setCellStyle(cellStyle);
            cellReal.setCellStyle(cellStyle);
            cellRepeat.setCellStyle(cellStyle);
            cellFalse.setCellStyle(cellStyle);
            cellAccuracy.setCellStyle(cellStyle);
            cellProcess.setCellStyle(cellStyle);
            cellAcceptance.setCellStyle(cellStyle);
            cellJudge.setCellStyle(cellStyle);
            cellDispatch.setCellStyle(cellStyle);
            cellHandle.setCellStyle(cellStyle);
            cellApprove.setCellStyle(cellStyle);
            cellComplete.setCellStyle(cellStyle);
            cellCompletionRate.setCellStyle(cellStyle);
            cellSuppress.setCellStyle(cellStyle);


            //单元格赋值
            cellNum.setCellValue(String.valueOf(map.get(Constants.ALL_NUM)));
            cellReal.setCellValue(String.valueOf(map.get(Constants.REAL_NUM)));
            cellRepeat.setCellValue(String.valueOf(map.get(Constants.REPEATCOMPLETE_NUM)));
            cellFalse.setCellValue(String.valueOf(map.get(Constants.FALSE_NUM)));
            cellAccuracy.setCellValue("null".equals(String.valueOf(map.get(Constants.ACCURACY)))?"-":(map.get(Constants.ACCURACY))+"%");
            cellProcess.setCellValue(String.valueOf(map.get(Constants.PROCESS_NUM)));
            cellAcceptance.setCellValue(String.valueOf(map.get(Constants.ACCEPTANCE_NUM)));
            cellJudge.setCellValue(String.valueOf(map.get(Constants.JUDGR_NUM)));
            cellDispatch.setCellValue(String.valueOf(map.get(Constants.DISPATCH_NUM)));
            cellHandle.setCellValue(String.valueOf(map.get(Constants.HANDLE_NUM)));
            cellApprove.setCellValue(String.valueOf(map.get(Constants.APPROVE_NUM)));
            cellComplete.setCellValue(String.valueOf(map.get(Constants.ORDERCOMPLETE_NUM)));
            cellCompletionRate.setCellValue("0".equals(String.valueOf(map.get(Constants.ALL_NUM)))?"-":(map.get(Constants.COMPLETION_RATE))+"%");
            cellSuppress.setCellValue(String.valueOf(map.get(Constants.SUPPRESS_NUM)));
        }

        return wb;
    }

    private XSSFWorkbook createEventSheet2(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("告警时间统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET2.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET2[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //告警日期折线图
        List<Map> map = param.getEventSheet2();
        //告警压制折线图
        List<Map> map1 = param.getEventSheet22();
        if(null != map && map.size()>0 && null != map1 && map1.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellDate=rowi.createCell(0);
                XSSFCell cellNum=rowi.createCell(1);
                XSSFCell cellSuppress=rowi.createCell(2);

                //赋值字体样式
                cellDate.setCellStyle(cellStyle);
                cellNum.setCellStyle(cellStyle);
                cellSuppress.setCellStyle(cellStyle);

                //单元格赋值
                cellDate.setCellValue(String.valueOf(map.get(i).get(Constants.ASSIGNED_DATE)));
                cellNum.setCellValue(String.valueOf(map.get(i).get(Constants.NUM)));
                cellSuppress.setCellValue(String.valueOf(map1.get(i).get(Constants.NUM)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createEventSheet3(XSSFWorkbook wb, StatisticsVO statisticsVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("告警类型排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET3.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET3[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //告警类型排行
        List<Map> map = param.getEventSheet3();
        if(null != map && map.size()>0){
            Integer sum = map.stream().mapToInt((x) -> Integer.parseInt(String.valueOf(x.get("num")))).sum();
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellTypeName=rowi.createCell(0);
                XSSFCell cellNum=rowi.createCell(1);
                XSSFCell cellRate=rowi.createCell(2);

                //赋值字体样式
                cellTypeName.setCellStyle(cellStyle);
                cellNum.setCellStyle(cellStyle);
                cellRate.setCellStyle(cellStyle);

                //单元格赋值
                cellTypeName.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.TYPE_NAME)))?"":String.valueOf(map.get(i).get(Constants.TYPE_NAME)));
                cellNum.setCellValue(String.valueOf(map.get(i).get(Constants.NUM)));
                BigDecimal num = new BigDecimal(String.valueOf(map.get(i).get(Constants.NUM)));
                String rate = num.divide(new BigDecimal(sum), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP)+"%";
                cellRate.setCellValue(rate);
            }
        }
        return wb;
    }


    /**
     * 方案 G V2.0：下游任务排行 sheet（紧随告警类型排行）
     */
    private XSSFWorkbook createEventSheetSubScene(XSSFWorkbook wb, StatisticsExportParam param, CellStyle cellStyle) {
        XSSFSheet sheet = wb.createSheet("下游任务排行");
        sheet.setColumnWidth(0, 256 * 14);
        sheet.setColumnWidth(1, 256 * 18);
        CellStyle style = this.exportFontAndTableStyle(wb);
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET_SUB_SCENE.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET_SUB_SCENE[i]);
        }
        this.exportWidthStyle(sheet, style);
        List<Map> map = param.getEventSheetSubScene();
        if (null != map && map.size() > 0) {
            Integer sum = map.stream().mapToInt((x) -> Integer.parseInt(String.valueOf(x.get("num")))).sum();
            for (int i = 0; i < map.size(); i++) {
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellTypeName = rowi.createCell(0);
                XSSFCell cellNum = rowi.createCell(1);
                XSSFCell cellRate = rowi.createCell(2);
                cellTypeName.setCellStyle(cellStyle);
                cellNum.setCellStyle(cellStyle);
                cellRate.setCellStyle(cellStyle);
                cellTypeName.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.TYPE_NAME)))
                        ? "" : String.valueOf(map.get(i).get(Constants.TYPE_NAME)));
                cellNum.setCellValue(String.valueOf(map.get(i).get(Constants.NUM)));
                BigDecimal num = new BigDecimal(String.valueOf(map.get(i).get(Constants.NUM)));
                String rate = num.divide(new BigDecimal(sum), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
                cellRate.setCellValue(rate);
            }
        }
        return wb;
    }

    /**
     * 方案 G V2.0：下游任务准确率排行 sheet（紧随告警类型准确率排行）
     */
    private XSSFWorkbook createEventSheetSubSceneAccuracy(XSSFWorkbook wb, StatisticsExportParam param, CellStyle cellStyle) {
        XSSFSheet sheet = wb.createSheet("下游任务准确率排行");
        sheet.setColumnWidth(0, 256 * 14);
        sheet.setColumnWidth(1, 256 * 18);
        CellStyle style = this.exportFontAndTableStyle(wb);
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET_SUB_SCENE_ACCURACY.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET_SUB_SCENE_ACCURACY[i]);
        }
        this.exportWidthStyle(sheet, style);
        List<Map> map = param.getEventSheetSubSceneAccuracy();
        if (null != map && map.size() > 0) {
            for (int i = 0; i < map.size(); i++) {
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellAreaName = rowi.createCell(0);
                XSSFCell cellAccuracy = rowi.createCell(1);
                cellAreaName.setCellStyle(cellStyle);
                cellAccuracy.setCellStyle(cellStyle);
                cellAreaName.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.TYPE_NAME)))
                        ? "" : String.valueOf(map.get(i).get(Constants.TYPE_NAME)));
                cellAccuracy.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.ACCURACY)))
                        ? "-" : (map.get(i).get(Constants.ACCURACY)) + "%");
            }
        }
        return wb;
    }

    private XSSFWorkbook createEventSheet4(XSSFWorkbook wb, StatisticsVO statisticsVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("告警来源统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET4.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET4[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //告警来源排行
        List<Map> map = param.getEventSheet4();
        if(null != map && map.size()>0){
            Integer sum = map.stream().mapToInt((x) -> Integer.parseInt(String.valueOf(x.get("num")))).sum();
            String[] sourceStr = new String[]{"1","2","3","4","8","9","10","11"};
            String[] array = map.stream().map(t -> String.valueOf(t.get(Constants.PARAM_WARNING_SOURCE))).toArray(String[]::new);
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
                        map.add(maps);
                        break;
                    case "2":
                        maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"人工上报");
                        maps.put(Constants.NUM,"0");
                        map.add(maps);
                        break;
                    case "3":
                        maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"一键告警");
                        maps.put(Constants.NUM,"0");
                        map.add(maps);
                        break;
                    case "4":
                        maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"摄像机告警");
                        maps.put(Constants.NUM,"0");
                        map.add(maps);
                        break;
                    case "8":
                        maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"物联告警");
                        maps.put(Constants.NUM,"0");
                        map.add(maps);
                        break;
                    case "9":
                        maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"卫星告警");
                        maps.put(Constants.NUM,"0");
                        map.add(maps);
                        break;
                    case "10":
                        maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"雷达告警");
                        maps.put(Constants.NUM,"0");
                        map.add(maps);
                        break;
                    case "11":
                        maps.put(Constants.PARAM_WARNING_SOURCE_NAME,"第三方告警");
                        maps.put(Constants.NUM,"0");
                        map.add(maps);
                        break;
                    default:
                        break;
                }
            }
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellSourceName=rowi.createCell(0);
                XSSFCell cellNum=rowi.createCell(1);
                XSSFCell cellRate=rowi.createCell(2);

                //赋值字体样式
                cellSourceName.setCellStyle(cellStyle);
                cellNum.setCellStyle(cellStyle);
                cellRate.setCellStyle(cellStyle);

                //单元格赋值
                cellSourceName.setCellValue(String.valueOf(map.get(i).get(Constants.PARAM_WARNING_SOURCE_NAME)));
                cellNum.setCellValue(String.valueOf(map.get(i).get(Constants.NUM)));
                BigDecimal num = new BigDecimal(String.valueOf(map.get(i).get(Constants.NUM)));
                String rate="";
                if (num.compareTo(BigDecimal.ZERO) == 0){
                    rate = "0.00";
                }else {
                    rate = num.divide(new BigDecimal(sum), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP)+"%";
                }
                cellRate.setCellValue(rate);
            }
        }
        return wb;
    }


    private XSSFWorkbook createEventSheet5(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("告警区域排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET5.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET5[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //告警区域排行
        List<Map> map = param.getEventSheet5();
        if(null != map && map.size()>0){
            Integer sum = map.stream().mapToInt((x) -> Integer.parseInt(String.valueOf(x.get("num")))).sum();
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellSourceName=rowi.createCell(0);
                XSSFCell cellNum=rowi.createCell(1);
                XSSFCell cellRate=rowi.createCell(2);

                //赋值字体样式
                cellSourceName.setCellStyle(cellStyle);
                cellNum.setCellStyle(cellStyle);
                cellRate.setCellStyle(cellStyle);

                //单元格赋值
                cellSourceName.setCellValue(String.valueOf(map.get(i).get(Constants.REGION_NAME)));
                cellNum.setCellValue(String.valueOf(map.get(i).get(Constants.NUM)));
                BigDecimal num = new BigDecimal(String.valueOf(map.get(i).get(Constants.NUM)));
                String rate = num.divide(new BigDecimal(sum), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP)+"%";
                cellRate.setCellValue(rate);
            }
        }
        return wb;
    }

    private XSSFWorkbook createEventSheet6(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("告警24小时分布");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET6.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET6[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //告警24h折线图
        List<Map> map = param.getEventSheet6();
        //告警压制24h折线图
        List<Map> map1 = param.getEventSheet66();
        if(null != map && map.size()>0 && null != map1 && map1.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellDate=rowi.createCell(0);
                XSSFCell cellNum=rowi.createCell(1);
                XSSFCell cellSuppress=rowi.createCell(2);

                //赋值字体样式
                cellDate.setCellStyle(cellStyle);
                cellNum.setCellStyle(cellStyle);
                cellSuppress.setCellStyle(cellStyle);

                //单元格赋值
                cellDate.setCellValue(String.valueOf(map.get(i).get(Constants.PARAM_LABEL)));
                cellNum.setCellValue(String.valueOf(map.get(i).get(Constants.NUM)));
                cellSuppress.setCellValue(String.valueOf(map1.get(i).get(Constants.NUM)));
            }
        }
        return wb;
    }


    private XSSFWorkbook createEventSheet7(XSSFWorkbook wb, StatisticsVO statisticsVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("摄像机告警统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET7.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET7[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //摄像机告警数量统计
        List<Map> map = param.getEventSheet7();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellDeviceCode=rowi.createCell(0);
                XSSFCell cellDeviceName=rowi.createCell(1);
                XSSFCell cellNum=rowi.createCell(2);
                XSSFCell cellAvgNum=rowi.createCell(3);
                XSSFCell cellReal=rowi.createCell(4);
                XSSFCell cellRepeat=rowi.createCell(5);
                XSSFCell cellFalse=rowi.createCell(6);
                XSSFCell cellAccuracy=rowi.createCell(7);
                XSSFCell cellCompletionRate=rowi.createCell(8);
                XSSFCell cellProcess=rowi.createCell(9);
                XSSFCell cellAcceptance=rowi.createCell(10);
                XSSFCell cellJudge=rowi.createCell(11);
                XSSFCell cellDispatch=rowi.createCell(12);
                XSSFCell cellHandle=rowi.createCell(13);
                XSSFCell cellApprove=rowi.createCell(14);
                XSSFCell cellComplete=rowi.createCell(15);
                XSSFCell cellSuppress=rowi.createCell(16);

                //赋值字体样式
                cellDeviceCode.setCellStyle(cellStyle);
                cellDeviceName.setCellStyle(cellStyle);
                cellNum.setCellStyle(cellStyle);
                cellAvgNum.setCellStyle(cellStyle);
                cellReal.setCellStyle(cellStyle);
                cellRepeat.setCellStyle(cellStyle);
                cellFalse.setCellStyle(cellStyle);
                cellAccuracy.setCellStyle(cellStyle);
                cellCompletionRate.setCellStyle(cellStyle);
                cellProcess.setCellStyle(cellStyle);
                cellAcceptance.setCellStyle(cellStyle);
                cellJudge.setCellStyle(cellStyle);
                cellDispatch.setCellStyle(cellStyle);
                cellHandle.setCellStyle(cellStyle);
                cellApprove.setCellStyle(cellStyle);
                cellComplete.setCellStyle(cellStyle);
                cellSuppress.setCellStyle(cellStyle);


                //单元格赋值
                cellDeviceCode.setCellValue(String.valueOf(map.get(i).get(Constants.PARAM_DEVICE_CODE)));
                cellDeviceName.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.PARAM_DEVICE_NAME)))?"":String.valueOf(map.get(i).get(Constants.PARAM_DEVICE_NAME)));
                cellNum.setCellValue(String.valueOf(map.get(i).get(Constants.ALL_NUM)));
                cellAvgNum.setCellValue(String.valueOf(map.get(i).get(Constants.AVG_NUM)));
                cellReal.setCellValue(String.valueOf(map.get(i).get(Constants.REAL_NUM)));
                cellRepeat.setCellValue(String.valueOf(map.get(i).get(Constants.REPEATCOMPLETE_NUM)));
                cellFalse.setCellValue(String.valueOf(map.get(i).get(Constants.FALSE_NUM)));
                cellAccuracy.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.ACCURACY)))?"-":(map.get(i).get(Constants.ACCURACY))+"%");
                cellCompletionRate.setCellValue(map.get(i).get(Constants.COMPLETION_RATE)+"%");
                cellProcess.setCellValue(String.valueOf(map.get(i).get(Constants.PROCESS_NUM)));
                cellAcceptance.setCellValue(String.valueOf(map.get(i).get(Constants.ACCEPTANCE_NUM)));
                cellJudge.setCellValue(String.valueOf(map.get(i).get(Constants.JUDGR_NUM)));
                cellDispatch.setCellValue(String.valueOf(map.get(i).get(Constants.DISPATCH_NUM)));
                cellHandle.setCellValue(String.valueOf(map.get(i).get(Constants.HANDLE_NUM)));
                cellApprove.setCellValue(String.valueOf(map.get(i).get(Constants.APPROVE_NUM)));
                cellComplete.setCellValue(String.valueOf(map.get(i).get(Constants.ORDERCOMPLETE_NUM)));
                cellSuppress.setCellValue(String.valueOf(map.get(i).get(Constants.SUPPRESS_NUM)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createEventSheet8(XSSFWorkbook wb, StatisticsVO statisticsVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("告警准确率统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET8.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET8[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //告警准确率分析
        Map map = param.getEventSheet8();
        if(null != map){
            //真重误总量
            int sum = Integer.parseInt(String.valueOf(map.get(Constants.REAL_NUM))) + Integer.parseInt(String.valueOf(map.get(Constants.FALSE_NUM))) + Integer.parseInt(String.valueOf(map.get(Constants.REPEATCOMPLETE_NUM)));
            //真重总量
            int sums = Integer.parseInt(String.valueOf(map.get(Constants.REAL_NUM))) + Integer.parseInt(String.valueOf(map.get(Constants.REPEATCOMPLETE_NUM)));

            //创建单元格
                XSSFRow rowi = sheet.createRow(1);
                XSSFCell cellReal=rowi.createCell(0);
                XSSFCell cellRealRate=rowi.createCell(1);
                XSSFCell cellFalse=rowi.createCell(2);
                XSSFCell cellFalseRate=rowi.createCell(3);
                XSSFCell cellRepeat=rowi.createCell(4);
                XSSFCell cellRepeatRate=rowi.createCell(5);
                XSSFCell cellAccuracy=rowi.createCell(6);

                //赋值字体样式
                cellReal.setCellStyle(cellStyle);
                cellRealRate.setCellStyle(cellStyle);
                cellFalse.setCellStyle(cellStyle);
                cellFalseRate.setCellStyle(cellStyle);
                cellRepeat.setCellStyle(cellStyle);
                cellRepeatRate.setCellStyle(cellStyle);
                cellAccuracy.setCellStyle(cellStyle);

                //单元格赋值
                cellReal.setCellValue(String.valueOf(map.get(Constants.REAL_NUM)));
                cellRepeat.setCellValue(String.valueOf(map.get(Constants.REPEATCOMPLETE_NUM)));
                cellFalse.setCellValue(String.valueOf(map.get(Constants.FALSE_NUM)));
                String accuracy = sum==0?Constants.ZERO_ZERO_STRING+"%":new BigDecimal(sums).divide(new BigDecimal(sum), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP)+"%";
                String realRate = sum==0?Constants.ZERO_ZERO_STRING+"%":new BigDecimal(String.valueOf(map.get(Constants.REAL_NUM))).divide(new BigDecimal(sum), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP)+"%";
                String falseRate = sum==0?Constants.ZERO_ZERO_STRING+"%":new BigDecimal(String.valueOf(map.get(Constants.FALSE_NUM))).divide(new BigDecimal(sum), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP)+"%";
                String repeatRate = sum==0?Constants.ZERO_ZERO_STRING+"%":new BigDecimal(String.valueOf(map.get(Constants.REPEATCOMPLETE_NUM))).divide(new BigDecimal(sum), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP)+"%";
                cellAccuracy.setCellValue(accuracy);
                cellRealRate.setCellValue(realRate);
                cellRepeatRate.setCellValue(repeatRate);
                cellFalseRate.setCellValue(falseRate);
        }
        return wb;
    }

    private XSSFWorkbook createEventSheet9(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("告警区域准确率排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET9.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET9[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //告警区域排行
        List<Map> map = param.getEventSheet9();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellAreaName=rowi.createCell(0);
                XSSFCell cellAccuracy=rowi.createCell(1);

                //赋值字体样式
                cellAreaName.setCellStyle(cellStyle);
                cellAccuracy.setCellStyle(cellStyle);

                //单元格赋值
                cellAreaName.setCellValue(String.valueOf(map.get(i).get(Constants.REGION_NAME)));
                cellAccuracy.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.ACCURACY)))?"-":(map.get(i).get(Constants.ACCURACY))+"%");
            }
        }
        return wb;
    }

    private XSSFWorkbook createEventSheet10(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("告警来源准确率排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET10.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET10[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //告警来源准确率排行
        List<Map> map = param.getEventSheet10();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellAreaName=rowi.createCell(0);
                XSSFCell cellAccuracy=rowi.createCell(1);

                //赋值字体样式
                cellAreaName.setCellStyle(cellStyle);
                cellAccuracy.setCellStyle(cellStyle);

                //单元格赋值
                cellAreaName.setCellValue(String.valueOf(map.get(i).get(Constants.SOURCE_NAME)));
                cellAccuracy.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.ACCURACY)))?"-":(map.get(i).get(Constants.ACCURACY))+"%");
            }
        }
        return wb;
    }

    private XSSFWorkbook createEventSheet11(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("告警类型准确率排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET11.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET11[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //告警类型准确率排行
        List<Map> map = param.getEventSheet11();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellAreaName=rowi.createCell(0);
                XSSFCell cellAccuracy=rowi.createCell(1);

                //赋值字体样式
                cellAreaName.setCellStyle(cellStyle);
                cellAccuracy.setCellStyle(cellStyle);

                //单元格赋值
                cellAreaName.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.TYPE_NAME)))?"":String.valueOf(map.get(i).get(Constants.TYPE_NAME)));
                cellAccuracy.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.ACCURACY)))?"-":(map.get(i).get(Constants.ACCURACY))+"%");
            }
        }
        return wb;
    }


    private XSSFWorkbook createEventSheet12(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("告警准确率24小时分布");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET12.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET12[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //告警准确率24小时分布
        List<Map> map = param.getEventSheet12();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellDate=rowi.createCell(0);
                XSSFCell cellAccuracy=rowi.createCell(1);

                //赋值字体样式
                cellDate.setCellStyle(cellStyle);
                cellAccuracy.setCellStyle(cellStyle);

                //单元格赋值
                cellDate.setCellValue(String.valueOf(map.get(i).get(Constants.PARAM_LABEL)));
                cellAccuracy.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.ACCURACY)))?"":(map.get(i).get(Constants.ACCURACY))+"%");
            }
        }
        return wb;
    }

    private XSSFWorkbook createEventSheet13(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("行政区域完结率统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET13.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET13[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //行政区域完结率统计
        List<Map> map = param.getEventSheet13();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellAreaName=rowi.createCell(0);
                XSSFCell cellNum=rowi.createCell(1);
                XSSFCell cellCompletionRate=rowi.createCell(2);
                XSSFCell cellProcess=rowi.createCell(3);
                XSSFCell cellAcceptance=rowi.createCell(4);
                XSSFCell cellJudge=rowi.createCell(5);
                XSSFCell cellDispatch=rowi.createCell(6);
                XSSFCell cellHandle=rowi.createCell(7);
                XSSFCell cellApprove=rowi.createCell(8);
                XSSFCell cellComplete=rowi.createCell(9);
                XSSFCell cellAvgTime=rowi.createCell(10);

                //赋值字体样式
                cellAreaName.setCellStyle(cellStyle);
                cellNum.setCellStyle(cellStyle);
                cellCompletionRate.setCellStyle(cellStyle);
                cellProcess.setCellStyle(cellStyle);
                cellAcceptance.setCellStyle(cellStyle);
                cellJudge.setCellStyle(cellStyle);
                cellDispatch.setCellStyle(cellStyle);
                cellHandle.setCellStyle(cellStyle);
                cellApprove.setCellStyle(cellStyle);
                cellComplete.setCellStyle(cellStyle);
                cellAvgTime.setCellStyle(cellStyle);

                //单元格赋值
                cellAreaName.setCellValue(String.valueOf(map.get(i).get(Constants.REGION_NAME)));
                cellNum.setCellValue(String.valueOf(map.get(i).get(Constants.ALL_NUM)));
                cellCompletionRate.setCellValue(map.get(i).get(Constants.COMPLETION_RATE)+"%");
                cellProcess.setCellValue(String.valueOf(Math.round(Double.parseDouble(String.valueOf(map.get(i).get(Constants.PROCESS_NUM))))));
                cellAcceptance.setCellValue(String.valueOf(Math.round(Double.parseDouble(String.valueOf(map.get(i).get(Constants.ACCEPTANCE_NUM))))));
                cellJudge.setCellValue(String.valueOf(map.get(i).get(Constants.JUDGR_NUM)));
                cellDispatch.setCellValue(String.valueOf(map.get(i).get(Constants.DISPATCH_NUM)));
                cellHandle.setCellValue(String.valueOf(map.get(i).get(Constants.HANDLE_NUM)));
                cellApprove.setCellValue(String.valueOf(map.get(i).get(Constants.APPROVE_NUM)));
                cellComplete.setCellValue(String.valueOf(map.get(i).get(Constants.ORDERCOMPLETE_NUM)));
                cellAvgTime.setCellValue(String.valueOf(map.get(i).get(Constants.AVG_TIME_DAY)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createEventSheet14(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("组织完结率统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET14.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET14[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //组织完结率统计
        List<Map> map = param.getEventSheet14();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellDeptName=rowi.createCell(0);
                XSSFCell cellNum=rowi.createCell(1);
                XSSFCell cellCompletionRate=rowi.createCell(2);
                XSSFCell cellProcess=rowi.createCell(3);
                XSSFCell cellAcceptance=rowi.createCell(4);
                XSSFCell cellJudge=rowi.createCell(5);
                XSSFCell cellDispatch=rowi.createCell(6);
                XSSFCell cellHandle=rowi.createCell(7);
                XSSFCell cellApprove=rowi.createCell(8);
                XSSFCell cellComplete=rowi.createCell(9);
                XSSFCell cellAvgTime=rowi.createCell(10);

                //赋值字体样式
                cellDeptName.setCellStyle(cellStyle);
                cellNum.setCellStyle(cellStyle);
                cellCompletionRate.setCellStyle(cellStyle);
                cellProcess.setCellStyle(cellStyle);
                cellAcceptance.setCellStyle(cellStyle);
                cellJudge.setCellStyle(cellStyle);
                cellDispatch.setCellStyle(cellStyle);
                cellHandle.setCellStyle(cellStyle);
                cellApprove.setCellStyle(cellStyle);
                cellComplete.setCellStyle(cellStyle);
                cellAvgTime.setCellStyle(cellStyle);

                //单元格赋值
                cellDeptName.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.DEPT_NAME)))?"":String.valueOf(map.get(i).get(Constants.DEPT_NAME)));
                cellNum.setCellValue(String.valueOf(map.get(i).get(Constants.ALL_NUM)));
                cellCompletionRate.setCellValue(map.get(i).get(Constants.COMPLETION_RATE)+"%");
                cellProcess.setCellValue(String.valueOf(Math.round(Double.parseDouble(String.valueOf(map.get(i).get(Constants.PROCESS_NUM))))));
                cellAcceptance.setCellValue(String.valueOf(Math.round(Double.parseDouble(String.valueOf(map.get(i).get(Constants.ACCEPTANCE_NUM))))));
                cellJudge.setCellValue(String.valueOf(map.get(i).get(Constants.JUDGR_NUM)));
                cellDispatch.setCellValue(String.valueOf(map.get(i).get(Constants.DISPATCH_NUM)));
                cellHandle.setCellValue(String.valueOf(map.get(i).get(Constants.HANDLE_NUM)));
                cellApprove.setCellValue(String.valueOf(map.get(i).get(Constants.APPROVE_NUM)));
                cellComplete.setCellValue(String.valueOf(map.get(i).get(Constants.ORDERCOMPLETE_NUM)));
                cellAvgTime.setCellValue(String.valueOf(map.get(i).get(Constants.AVG_TIME_DAY)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createEventSheet15(XSSFWorkbook wb, StatisticsVO statisticsVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("物联设备告警统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET15.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET15[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //物联告警数量统计
        List<Map> map = param.getEventSheet15();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellDeviceCode=rowi.createCell(0);
                XSSFCell cellDeviceName=rowi.createCell(1);
                XSSFCell cellNum=rowi.createCell(2);
                XSSFCell cellAvgNum=rowi.createCell(3);
                XSSFCell cellReal=rowi.createCell(4);
                XSSFCell cellRepeat=rowi.createCell(5);
                XSSFCell cellFalse=rowi.createCell(6);
                XSSFCell cellAccuracy=rowi.createCell(7);
                XSSFCell cellCompletionRate=rowi.createCell(8);
                XSSFCell cellProcess=rowi.createCell(9);
                XSSFCell cellAcceptance=rowi.createCell(10);
                XSSFCell cellJudge=rowi.createCell(11);
                XSSFCell cellDispatch=rowi.createCell(12);
                XSSFCell cellHandle=rowi.createCell(13);
                XSSFCell cellApprove=rowi.createCell(14);
                XSSFCell cellComplete=rowi.createCell(15);

                //赋值字体样式
                cellDeviceCode.setCellStyle(cellStyle);
                cellDeviceName.setCellStyle(cellStyle);
                cellNum.setCellStyle(cellStyle);
                cellAvgNum.setCellStyle(cellStyle);
                cellReal.setCellStyle(cellStyle);
                cellRepeat.setCellStyle(cellStyle);
                cellFalse.setCellStyle(cellStyle);
                cellAccuracy.setCellStyle(cellStyle);
                cellCompletionRate.setCellStyle(cellStyle);
                cellProcess.setCellStyle(cellStyle);
                cellAcceptance.setCellStyle(cellStyle);
                cellJudge.setCellStyle(cellStyle);
                cellDispatch.setCellStyle(cellStyle);
                cellHandle.setCellStyle(cellStyle);
                cellApprove.setCellStyle(cellStyle);
                cellComplete.setCellStyle(cellStyle);


                //单元格赋值
                cellDeviceCode.setCellValue(String.valueOf(map.get(i).get(Constants.PARAM_DEVICE_CODE)));
                cellDeviceName.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.PARAM_DEVICE_NAME)))?"":String.valueOf(map.get(i).get(Constants.PARAM_DEVICE_NAME)));
                cellNum.setCellValue(String.valueOf(map.get(i).get(Constants.ALL_NUM)));
                cellAvgNum.setCellValue(String.valueOf(map.get(i).get(Constants.AVG_NUM)));
                cellReal.setCellValue(String.valueOf(map.get(i).get(Constants.REAL_NUM)));
                cellRepeat.setCellValue(String.valueOf(map.get(i).get(Constants.REPEATCOMPLETE_NUM)));
                cellFalse.setCellValue(String.valueOf(map.get(i).get(Constants.FALSE_NUM)));
                cellAccuracy.setCellValue("null".equals(String.valueOf(map.get(i).get(Constants.ACCURACY)))?"-":(map.get(i).get(Constants.ACCURACY))+"%");
                cellCompletionRate.setCellValue(map.get(i).get(Constants.COMPLETION_RATE)+"%");
                cellProcess.setCellValue(String.valueOf(map.get(i).get(Constants.PROCESS_NUM)));
                cellAcceptance.setCellValue(String.valueOf(map.get(i).get(Constants.ACCEPTANCE_NUM)));
                cellJudge.setCellValue(String.valueOf(map.get(i).get(Constants.JUDGR_NUM)));
                cellDispatch.setCellValue(String.valueOf(map.get(i).get(Constants.DISPATCH_NUM)));
                cellHandle.setCellValue(String.valueOf(map.get(i).get(Constants.HANDLE_NUM)));
                cellApprove.setCellValue(String.valueOf(map.get(i).get(Constants.APPROVE_NUM)));
                cellComplete.setCellValue(String.valueOf(map.get(i).get(Constants.ORDERCOMPLETE_NUM)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createEventSheet16(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("网格完结率统计");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.EVENT_SHEET16.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.EVENT_SHEET16[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //网格完结率统计
        List<Map> map = param.getEventSheet16();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellGridName=rowi.createCell(0);
                XSSFCell cellNum=rowi.createCell(1);
                XSSFCell cellCompletionRate=rowi.createCell(2);
                XSSFCell cellProcess=rowi.createCell(3);
                XSSFCell cellAcceptance=rowi.createCell(4);
                XSSFCell cellJudge=rowi.createCell(5);
                XSSFCell cellDispatch=rowi.createCell(6);
                XSSFCell cellHandle=rowi.createCell(7);
                XSSFCell cellApprove=rowi.createCell(8);
                XSSFCell cellComplete=rowi.createCell(9);
                XSSFCell cellAvgTime=rowi.createCell(10);

                //赋值字体样式
                cellGridName.setCellStyle(cellStyle);
                cellNum.setCellStyle(cellStyle);
                cellCompletionRate.setCellStyle(cellStyle);
                cellProcess.setCellStyle(cellStyle);
                cellAcceptance.setCellStyle(cellStyle);
                cellJudge.setCellStyle(cellStyle);
                cellDispatch.setCellStyle(cellStyle);
                cellHandle.setCellStyle(cellStyle);
                cellApprove.setCellStyle(cellStyle);
                cellComplete.setCellStyle(cellStyle);
                cellAvgTime.setCellStyle(cellStyle);

                //单元格赋值
                cellGridName.setCellValue("null".equals(String.valueOf(map.get(i).get("gridName")))?"":String.valueOf(map.get(i).get("gridName")));
                cellNum.setCellValue(String.valueOf(map.get(i).get(Constants.ALL_NUM)));
                cellCompletionRate.setCellValue(map.get(i).get(Constants.COMPLETION_RATE)+"%");
                cellProcess.setCellValue(String.valueOf(Math.round(Double.parseDouble(String.valueOf(map.get(i).get(Constants.PROCESS_NUM))))));
                cellAcceptance.setCellValue(String.valueOf(Math.round(Double.parseDouble(String.valueOf(map.get(i).get(Constants.ACCEPTANCE_NUM))))));
                cellJudge.setCellValue(String.valueOf(map.get(i).get(Constants.JUDGR_NUM)));
                cellDispatch.setCellValue(String.valueOf(map.get(i).get(Constants.DISPATCH_NUM)));
                cellHandle.setCellValue(String.valueOf(map.get(i).get(Constants.HANDLE_NUM)));
                cellApprove.setCellValue(String.valueOf(map.get(i).get(Constants.APPROVE_NUM)));
                cellComplete.setCellValue(String.valueOf(map.get(i).get(Constants.ORDERCOMPLETE_NUM)));
                cellAvgTime.setCellValue(String.valueOf(map.get(i).get(Constants.AVG_TIME_DAY)));
            }
        }
        return wb;
    }



    private XSSFWorkbook createDeviceSheet1(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("摄像机总览");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET1.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET1[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        DeviceStatisticsVO map = param.getDeviceSheet1();

        if(null != map){
                //创建单元格
                XSSFRow rowi = sheet.createRow(1);
                XSSFCell cellDevNum = rowi.createCell(0);
                XSSFCell cellDevOnlineNum = rowi.createCell(1);
                XSSFCell cellDevOfflineNum = rowi.createCell(2);
                XSSFCell cellDevOnlinePer = rowi.createCell(3);
                XSSFCell cellChannelNum = rowi.createCell(4);
                XSSFCell cellChannelOnlineNum = rowi.createCell(5);
                XSSFCell cellChannelOfflineNum = rowi.createCell(6);
                XSSFCell cellChannelOnlinePer = rowi.createCell(7);
                XSSFCell cellDisAlgorithmTaskNum = rowi.createCell(8);
                XSSFCell cellUpAlgorithmTaskNum = rowi.createCell(9);

                //赋值字体样式
                cellDevNum.setCellStyle(cellStyle);
                cellDevOnlineNum.setCellStyle(cellStyle);
                cellDevOfflineNum.setCellStyle(cellStyle);
                cellDevOnlinePer.setCellStyle(cellStyle);
                cellChannelOnlineNum.setCellStyle(cellStyle);
                cellChannelOfflineNum.setCellStyle(cellStyle);
                cellChannelOnlinePer.setCellStyle(cellStyle);
                cellDisAlgorithmTaskNum.setCellStyle(cellStyle);
                cellUpAlgorithmTaskNum.setCellStyle(cellStyle);

                //单元格赋值
                cellDevNum.setCellValue(String.valueOf(map.getDeviceCountTotal()));
                cellDevOnlineNum.setCellValue(String.valueOf(map.getDevCountOnline()));
                cellDevOfflineNum.setCellValue(String.valueOf(map.getDevCountOffline()));
                cellDevOnlinePer.setCellValue(map.getDeviceCountTotal()<=0?"-":Math.round(Double.parseDouble(String.valueOf(map.getOnlinePercentageDev())) * 100) + "%");
                cellChannelNum.setCellValue(String.valueOf(map.getChannelCountTotal()));
                cellChannelOnlineNum.setCellValue(String.valueOf(map.getChannelCountOnline()));
                cellChannelOfflineNum.setCellValue(String.valueOf(map.getChannelCountOffline()));
                cellChannelOnlinePer.setCellValue(map.getChannelCountTotal()<=0?"-":Math.round(Double.parseDouble(String.valueOf(map.getOnlinePercentage())) * 100) + "%");
                cellDisAlgorithmTaskNum.setCellValue(String.valueOf(map.getDisAlgorithmTaskNum()));
                cellUpAlgorithmTaskNum.setCellValue(String.valueOf(map.getUpAlgorithmTaskNum()));
                //赋值样式,根据最长值自适应宽度
                this.exportWidthStyleMax(sheet,1,Constants.DEVICE_SHEET1.length);
        }

        return wb;
    }

    private XSSFWorkbook createDeviceSheet2(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("摄像机数量区域排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET2.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET2[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //摄像机数量区域排行
        List<Map> map = param.getDeviceSheet2();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellareaName=rowi.createCell(0);
                XSSFCell cellDeviceNum=rowi.createCell(1);

                //赋值字体样式
                cellareaName.setCellStyle(cellStyle);
                cellDeviceNum.setCellStyle(cellStyle);

                //单元格赋值
                cellareaName.setCellValue(String.valueOf(map.get(i).get(Constants.REGION_NAME)));
                cellDeviceNum.setCellValue(String.valueOf(map.get(i).get(Constants.DEVICE_NUM)));
            }
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,map.size(),Constants.DEVICE_SHEET2.length);
        }
        return wb;
    }

    private XSSFWorkbook createDeviceSheet3(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("摄像机订购算法排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET3.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET3[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //摄像机订购算法排行
        List<Map> map = param.getDeviceSheet3();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellDeviceCode=rowi.createCell(0);
                XSSFCell cellDeviceName=rowi.createCell(1);
                XSSFCell cellAlgorithmNum=rowi.createCell(2);

                //赋值字体样式
                cellDeviceCode.setCellStyle(cellStyle);
                cellDeviceName.setCellStyle(cellStyle);
                cellAlgorithmNum.setCellStyle(cellStyle);

                //单元格赋值
                cellDeviceCode.setCellValue(String.valueOf(map.get(i).get(Constants.PARAM_DEVICE_CODE)));
                cellDeviceName.setCellValue(String.valueOf(map.get(i).get(Constants.PARAM_DEVICE_NAME)));
                cellAlgorithmNum.setCellValue(String.valueOf(map.get(i).get(Constants.ALGORITHM_NUM)));
            }
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,map.size(),Constants.DEVICE_SHEET3.length);
        }
        return wb;
    }

    private XSSFWorkbook createDeviceSheet4(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("通道算法任务排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET4.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET4[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //通道算法任务排行
        List<Map<String, String>> map = param.getDeviceSheet4();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellDeviceCode=rowi.createCell(0);
                XSSFCell cellDeviceName=rowi.createCell(1);
                XSSFCell cellChannelCode=rowi.createCell(2);
                XSSFCell cellChannelName=rowi.createCell(3);
                XSSFCell cellForeAlgorithmTaskNum=rowi.createCell(4);
                XSSFCell cellAfterAlgorithmTaskNum=rowi.createCell(5);
                XSSFCell cellStartForeAlgorithmTaskNum=rowi.createCell(6);
                XSSFCell cellStartAfterAlgorithmTaskNum=rowi.createCell(7);

                //赋值字体样式
                cellDeviceCode.setCellStyle(cellStyle);
                cellDeviceName.setCellStyle(cellStyle);
                cellChannelCode.setCellStyle(cellStyle);
                cellChannelName.setCellStyle(cellStyle);
                cellForeAlgorithmTaskNum.setCellStyle(cellStyle);
                cellAfterAlgorithmTaskNum.setCellStyle(cellStyle);
                cellStartForeAlgorithmTaskNum.setCellStyle(cellStyle);
                cellStartAfterAlgorithmTaskNum.setCellStyle(cellStyle);

                //单元格赋值
                cellDeviceCode.setCellValue(String.valueOf(map.get(i).get(Constants.PARAM_DEVICE_CODE)));
                cellDeviceName.setCellValue(String.valueOf(map.get(i).get(Constants.PARAM_DEVICE_NAME)));
                cellChannelCode.setCellValue(String.valueOf(map.get(i).get(Constants.PARAM_CHANNEL_CODE)));
                cellChannelName.setCellValue(String.valueOf(map.get(i).get(Constants.PARAM_CHANNEL_NAME)));
                cellForeAlgorithmTaskNum.setCellValue(String.valueOf(map.get(i).get(Constants.FORE_ALGORITHM_TASK_NUM)));
                cellAfterAlgorithmTaskNum.setCellValue(String.valueOf(map.get(i).get(Constants.AFTER_ALGORITHM_TASK_NUM)));
                cellStartForeAlgorithmTaskNum.setCellValue(String.valueOf(map.get(i).get(Constants.START_FOREALGORITHM_TASK_NUM)));
                cellStartAfterAlgorithmTaskNum.setCellValue(String.valueOf(map.get(i).get(Constants.START_AFTERALGORITHM_TASK_NUM)));
            }
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,map.size(),Constants.DEVICE_SHEET4.length);
        }
        return wb;
    }

    private XSSFWorkbook createDeviceSheet5(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("物联设备总览");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET5.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET5[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        IotDeviceStatisticsVo map = param.getDeviceSheet5();

        if(null != map){
            //创建单元格
            XSSFRow rowi = sheet.createRow(1);
            XSSFCell cellDevNum = rowi.createCell(0);
            XSSFCell cellDevOnlineNum = rowi.createCell(1);
            XSSFCell cellDevOfflineNum = rowi.createCell(2);
            XSSFCell cellDevOnlinePer = rowi.createCell(3);

            //赋值字体样式
            cellDevNum.setCellStyle(cellStyle);
            cellDevOnlineNum.setCellStyle(cellStyle);
            cellDevOfflineNum.setCellStyle(cellStyle);
            cellDevOnlinePer.setCellStyle(cellStyle);

            //单元格赋值
            cellDevNum.setCellValue(String.valueOf(map.getIotDeviceCount()));
            cellDevOnlineNum.setCellValue(String.valueOf(map.getIotDeviceOnlineCount()));
            cellDevOfflineNum.setCellValue(String.valueOf(map.getIotDeviceOfflineCount()));
            cellDevOnlinePer.setCellValue(map.getIotDeviceCount()<=0?"-":Math.round(Double.parseDouble(String.valueOf(map.getIotDeviceOnlinePercent())) * 100) + "%");
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,1,Constants.DEVICE_SHEET5.length);
        }

        return wb;
    }

    private XSSFWorkbook createDeviceSheet6(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("物联设备数量区域排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET6.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET6[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //物联设备数量区域排行
        List<Map> map = param.getDeviceSheet6();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellareaName=rowi.createCell(0);
                XSSFCell cellDeviceNum=rowi.createCell(1);

                //赋值字体样式
                cellareaName.setCellStyle(cellStyle);
                cellDeviceNum.setCellStyle(cellStyle);

                //单元格赋值
                cellareaName.setCellValue(String.valueOf(map.get(i).get(Constants.REGION_NAME)));
                cellDeviceNum.setCellValue(String.valueOf(map.get(i).get(Constants.DEVICE_NUM)));
            }
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,map.size(),Constants.DEVICE_SHEET6.length);
        }
        return wb;
    }

    private XSSFWorkbook createDeviceSheet7(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("无人机总览");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET7.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET7[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        UavDeviceStatisticsVo map = param.getDeviceSheet7();

        if(null != map){
            //创建单元格
            XSSFRow rowi = sheet.createRow(1);
            XSSFCell cellDevNum = rowi.createCell(0);
            XSSFCell cellDevOnlineNum = rowi.createCell(1);
            XSSFCell cellDevOfflineNum = rowi.createCell(2);
            XSSFCell cellDevOnlinePer = rowi.createCell(3);
            XSSFCell cellChannelNum = rowi.createCell(4);
            XSSFCell cellChannelOnlineNum = rowi.createCell(5);
            XSSFCell cellChannelOfflineNum = rowi.createCell(6);
            XSSFCell cellChannelOnlinePer = rowi.createCell(7);

            //赋值字体样式
            cellDevNum.setCellStyle(cellStyle);
            cellDevOnlineNum.setCellStyle(cellStyle);
            cellDevOfflineNum.setCellStyle(cellStyle);
            cellDevOnlinePer.setCellStyle(cellStyle);
            cellChannelNum.setCellStyle(cellStyle);
            cellChannelOnlineNum.setCellStyle(cellStyle);
            cellChannelOfflineNum.setCellStyle(cellStyle);
            cellChannelOnlinePer.setCellStyle(cellStyle);

            //单元格赋值
            cellDevNum.setCellValue(String.valueOf(map.getUavDeviceCount()));
            cellDevOnlineNum.setCellValue(String.valueOf(map.getUavDeviceOnlineCount()));
            cellDevOfflineNum.setCellValue(String.valueOf(map.getUavDeviceOfflineCount()));
            cellDevOnlinePer.setCellValue(map.getUavDeviceCount()<=0?"-":Math.round(Double.parseDouble(String.valueOf(map.getUavDeviceOnlinePercent())) * 100) + "%");
            cellChannelNum.setCellValue(String.valueOf(map.getUavNestDeviceCount()));
            cellChannelOnlineNum.setCellValue(String.valueOf(map.getUavNestDeviceOnlineCount()));
            cellChannelOfflineNum.setCellValue(String.valueOf(map.getUavNestDeviceOfflineCount()));
            cellChannelOnlinePer.setCellValue(map.getUavNestDeviceCount()<=0?"-":Math.round(Double.parseDouble(String.valueOf(map.getUavNestDeviceOnlinePercent())) * 100) + "%");
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,1,Constants.DEVICE_SHEET7.length);
        }

        return wb;
    }

    private XSSFWorkbook createDeviceSheet8(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("无人机数量区域排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET8.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET8[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //无人机数量区域排行
        List<Map> map = param.getDeviceSheet8();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellareaName=rowi.createCell(0);
                XSSFCell cellDeviceNum=rowi.createCell(1);

                //赋值字体样式
                cellareaName.setCellStyle(cellStyle);
                cellDeviceNum.setCellStyle(cellStyle);

                //单元格赋值
                cellareaName.setCellValue(String.valueOf(map.get(i).get(Constants.REGION_NAME)));
                cellDeviceNum.setCellValue(String.valueOf(map.get(i).get(Constants.DEVICE_NUM)));
            }
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,map.size(),Constants.DEVICE_SHEET8.length);
        }
        return wb;
    }

    private XSSFWorkbook createDeviceSheet9(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("雷达总览");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET9.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET9[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        RadarDeviceStatisticsVo map = param.getDeviceSheet9();

        if(null != map){
            //创建单元格
            XSSFRow rowi = sheet.createRow(1);
            XSSFCell cellDevNum = rowi.createCell(0);
            XSSFCell cellDevOnlineNum = rowi.createCell(1);
            XSSFCell cellDevOfflineNum = rowi.createCell(2);
            XSSFCell cellDevOnlinePer = rowi.createCell(3);

            //赋值字体样式
            cellDevNum.setCellStyle(cellStyle);
            cellDevOnlineNum.setCellStyle(cellStyle);
            cellDevOfflineNum.setCellStyle(cellStyle);
            cellDevOnlinePer.setCellStyle(cellStyle);

            //单元格赋值
            cellDevNum.setCellValue(String.valueOf(map.getRadarDeviceCount()));
            cellDevOnlineNum.setCellValue(String.valueOf(map.getRadarDeviceOnlineCount()));
            cellDevOfflineNum.setCellValue(String.valueOf(map.getRadarDeviceOfflineCount()));
            cellDevOnlinePer.setCellValue(map.getRadarDeviceCount()<=0?"-":Math.round(Double.parseDouble(String.valueOf(map.getRadarDeviceOnlinePercent())) * 100) + "%");
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,1,Constants.DEVICE_SHEET9.length);
        }

        return wb;
    }

    private XSSFWorkbook createDeviceSheet10(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("雷达数量区域排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET10.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET10[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //雷达数量区域排行
        List<Map> map = param.getDeviceSheet10();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellareaName=rowi.createCell(0);
                XSSFCell cellDeviceNum=rowi.createCell(1);

                //赋值字体样式
                cellareaName.setCellStyle(cellStyle);
                cellDeviceNum.setCellStyle(cellStyle);

                //单元格赋值
                cellareaName.setCellValue(String.valueOf(map.get(i).get(Constants.REGION_NAME)));
                cellDeviceNum.setCellValue(String.valueOf(map.get(i).get(Constants.DEVICE_NUM)));
            }
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,map.size(),Constants.DEVICE_SHEET10.length);
        }
        return wb;
    }

    private XSSFWorkbook createDeviceSheet11(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("喇叭总览");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET11.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET11[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        HornDeviceStatisticsVo map = param.getDeviceSheet11();

        if(null != map){
            //创建单元格
            XSSFRow rowi = sheet.createRow(1);
            XSSFCell cellDevNum = rowi.createCell(0);
            XSSFCell cellDevWorkNum = rowi.createCell(1);
            XSSFCell cellDevOfflineNum = rowi.createCell(2);
            XSSFCell cellDevFreeNum = rowi.createCell(3);
            XSSFCell cellDevFailNum = rowi.createCell(4);
            XSSFCell cellDevNoConnectNum = rowi.createCell(5);
            XSSFCell cellDevOnlinePer = rowi.createCell(6);

            //赋值字体样式
            cellDevNum.setCellStyle(cellStyle);
            cellDevWorkNum.setCellStyle(cellStyle);
            cellDevOfflineNum.setCellStyle(cellStyle);
            cellDevFreeNum.setCellStyle(cellStyle);
            cellDevFailNum.setCellStyle(cellStyle);
            cellDevNoConnectNum.setCellStyle(cellStyle);
            cellDevOnlinePer.setCellStyle(cellStyle);

            //单元格赋值
            cellDevNum.setCellValue(String.valueOf(map.getHornDeviceCount()));
            cellDevWorkNum.setCellValue(String.valueOf(map.getHornDeviceWorkCount()));
            cellDevOfflineNum.setCellValue(String.valueOf(map.getHornDeviceOfflineCount()));
            cellDevFreeNum.setCellValue(String.valueOf(map.getHornDeviceFreeCount()));
            cellDevFailNum.setCellValue(String.valueOf(map.getHornDeviceFailCount()));
            cellDevNoConnectNum.setCellValue(String.valueOf(map.getHornDeviceNoConnectCount()));
            cellDevOnlinePer.setCellValue(map.getHornDeviceCount()<=0?"-":Math.round(Double.parseDouble(String.valueOf(map.getHornDeviceOnlinePercent())) * 100) + "%");
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,1,Constants.DEVICE_SHEET11.length);
        }

        return wb;
    }

    private XSSFWorkbook createDeviceSheet12(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("喇叭数量区域排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET12.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET12[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //喇叭数量区域排行
        List<Map> map = param.getDeviceSheet12();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellareaName=rowi.createCell(0);
                XSSFCell cellDeviceNum=rowi.createCell(1);

                //赋值字体样式
                cellareaName.setCellStyle(cellStyle);
                cellDeviceNum.setCellStyle(cellStyle);

                //单元格赋值
                cellareaName.setCellValue(String.valueOf(map.get(i).get(Constants.REGION_NAME)));
                cellDeviceNum.setCellValue(String.valueOf(map.get(i).get(Constants.DEVICE_NUM)));
            }
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,map.size(),Constants.DEVICE_SHEET12.length);
        }
        return wb;
    }

    private XSSFWorkbook createDeviceSheet13(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("执法记录仪总览");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET13.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET13[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        DeviceStatisticsVO map = param.getDeviceSheet13();

        if(null != map){
            //创建单元格
            XSSFRow rowi = sheet.createRow(1);
            XSSFCell cellDevNum = rowi.createCell(0);
            XSSFCell cellDevOnlineNum = rowi.createCell(1);
            XSSFCell cellDevOfflineNum = rowi.createCell(2);
            XSSFCell cellDevNoConnectNum = rowi.createCell(3);
            XSSFCell cellDevOnlinePer = rowi.createCell(4);
            XSSFCell cellChannelNum = rowi.createCell(5);
            XSSFCell cellChannelOnlineNum = rowi.createCell(6);
            XSSFCell cellChannelOfflineNum = rowi.createCell(7);
            XSSFCell cellChannelOnlinePer = rowi.createCell(8);

            //赋值字体样式
            cellDevNum.setCellStyle(cellStyle);
            cellDevOnlineNum.setCellStyle(cellStyle);
            cellDevOfflineNum.setCellStyle(cellStyle);
            cellDevNoConnectNum.setCellStyle(cellStyle);
            cellDevOnlinePer.setCellStyle(cellStyle);
            cellChannelNum.setCellStyle(cellStyle);
            cellChannelOnlineNum.setCellStyle(cellStyle);
            cellChannelOfflineNum.setCellStyle(cellStyle);
            cellChannelOnlinePer.setCellStyle(cellStyle);


            //单元格赋值
            cellDevNum.setCellValue(String.valueOf(map.getDeviceCountTotal()));
            cellDevOnlineNum.setCellValue(String.valueOf(map.getDevCountOnline()));
            cellDevOfflineNum.setCellValue(String.valueOf(map.getDevCountOffline()));
            cellDevNoConnectNum.setCellValue(String.valueOf(map.getDeviceNoConnectCount()));
            cellDevOnlinePer.setCellValue(map.getDeviceCountTotal()<=0?"-":Math.round(Double.parseDouble(String.valueOf(map.getOnlinePercentageDev())) * 100) + "%");
            cellChannelNum.setCellValue(String.valueOf(map.getChannelCountTotal()));
            cellChannelOnlineNum.setCellValue(String.valueOf(map.getChannelCountOnline()));
            cellChannelOfflineNum.setCellValue(String.valueOf(map.getChannelCountOffline()));
            cellChannelOnlinePer.setCellValue(map.getChannelCountTotal()<=0?"-":Math.round(Double.parseDouble(String.valueOf(map.getOnlinePercentage())) * 100) + "%");
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,1,Constants.DEVICE_SHEET13.length);
        }

        return wb;
    }

    //根据最长值自适应宽度
    private void exportWidthStyleMax(XSSFSheet sheet,int cellSize,int rowSize) {

        // 获取表头行
        XSSFRow headerRow = sheet.getRow(0);
        int[] widthArray = new int[rowSize];
        // 将样式应用到所有表头单元格
        int textWidth =0;
        for (int j = 0; j <= cellSize; j++) {
            XSSFRow row = sheet.getRow(j);
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                XSSFCell cell = row.getCell(i);
                // 获取单元格中的文本
                String headerText = cell.getStringCellValue();
                if (headerText.length() > widthArray[i]){
                    widthArray[i] = headerText.length();
                }

            }


        }
        List<String> stringList = Arrays.asList("客户ID","摄像机编号","通道编号");
        for (int i = 0; i < widthArray.length; i++) {
            String value = headerRow.getCell(i).getStringCellValue();
            if (stringList.contains(value)){
                textWidth = widthArray[i] * 281;
            }else {
                textWidth = widthArray[i] * 256 * 2;
            }

            // 设置单元格宽度
            sheet.setColumnWidth(i, textWidth);
        }



    }

    private XSSFWorkbook createDeviceSheet14(XSSFWorkbook wb, AnalysisVO analysisVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("执法记录仪数量区域排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.DEVICE_SHEET14.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.DEVICE_SHEET14[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //执法记录仪数量区域排行
        List<Map> map = param.getDeviceSheet14();
        if(null != map && map.size()>0){
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i+1);
                XSSFCell cellareaName=rowi.createCell(0);
                XSSFCell cellDeviceNum=rowi.createCell(1);

                //赋值字体样式
                cellareaName.setCellStyle(cellStyle);
                cellDeviceNum.setCellStyle(cellStyle);

                //单元格赋值
                cellareaName.setCellValue(String.valueOf(map.get(i).get(Constants.REGION_NAME)));
                cellDeviceNum.setCellValue(String.valueOf(map.get(i).get(Constants.DEVICE_NUM)));
            }
            //赋值样式,根据最长值自适应宽度
            this.exportWidthStyleMax(sheet,map.size(),Constants.DEVICE_SHEET14.length);
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet1(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("用户总览");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET1.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET1[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //用户总览
        Map map = param.getUserSheet1();
        if(null != map){
                //创建单元格
                XSSFRow rowi = sheet.createRow(1);
                XSSFCell cellUserNum=rowi.createCell(0);
                XSSFCell cellStartUserNum=rowi.createCell(1);
                XSSFCell cellStopUserNum=rowi.createCell(2);
                XSSFCell cellDeptNum=rowi.createCell(3);

                //赋值字体样式
                cellUserNum.setCellStyle(cellStyle);
                cellStartUserNum.setCellStyle(cellStyle);
                cellStopUserNum.setCellStyle(cellStyle);
                cellDeptNum.setCellStyle(cellStyle);

                //单元格赋值
                cellUserNum.setCellValue(String.valueOf(map.get(Constants.USER_TOTAL_NUM)));
                cellStartUserNum.setCellValue(String.valueOf(map.get(Constants.START_USER_NUM)));
                cellStopUserNum.setCellValue(String.valueOf(map.get(Constants.STOP_USER_NUM)));
                cellDeptNum.setCellValue(String.valueOf(map.get("deptNum")));
            }
        return wb;
    }

    private XSSFWorkbook createUserSheet2(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("用户登录按时间统计");
        sheet.setColumnWidth(0,256*14);//获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        sheet.setColumnWidth(1,256*18);

        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET2.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET2[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //用户登录时间统计
        List<Map> map = param.getUserSheet2();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellLoginDate = rowi.createCell(0);
                XSSFCell cellLoginNum = rowi.createCell(1);
                XSSFCell cellPCLoginNum = rowi.createCell(2);
                XSSFCell cellAPPLoginNum = rowi.createCell(3);
                XSSFCell cellWXLoginNum = rowi.createCell(4);

                //赋值字体样式
                cellLoginDate.setCellStyle(cellStyle);
                cellLoginNum.setCellStyle(cellStyle);
                cellPCLoginNum.setCellStyle(cellStyle);
                cellAPPLoginNum.setCellStyle(cellStyle);
                cellWXLoginNum.setCellStyle(cellStyle);

                cellLoginNum.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_TOTAL_COUNT)));
                for (Map map1 : (List<Map>)map.get(i).get("loginNum")) {
                //单元格赋值
                    cellLoginDate.setCellValue(String.valueOf(map1.get(Constants.LOGIN_DATE)));
                    if ("web_manage".equals(map1.get(Constants.LOGIN_TYPE_NAME))){
                        cellPCLoginNum.setCellValue(String.valueOf(map1.get(Constants.LOGIN_COUNT)));
                    }else if ("app_manage".equals(map1.get(Constants.LOGIN_TYPE_NAME))){
                        cellAPPLoginNum.setCellValue(String.valueOf(map1.get(Constants.LOGIN_COUNT)));
                    }else if ("wx_manage".equals(map1.get(Constants.LOGIN_TYPE_NAME))){
                        cellWXLoginNum.setCellValue(String.valueOf(map1.get(Constants.LOGIN_COUNT)));
                    }
                }
            }
        }
        return wb;
    }


    private XSSFWorkbook createUserSheet3(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("用户登录次数排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET3.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET3[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //用户登录次数及登录时长
        List<Map> map = param.getUserSheet3();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellLoginUserName = rowi.createCell(0);
                XSSFCell cellLoginName = rowi.createCell(1);
                XSSFCell cellLoginNum = rowi.createCell(2);

                //赋值字体样式
                cellLoginUserName.setCellStyle(cellStyle);
                cellLoginName.setCellStyle(cellStyle);
                cellLoginNum.setCellStyle(cellStyle);

                //单元格赋值
                cellLoginUserName.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_USER_NAME)));
                cellLoginName.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_NAME)));
                cellLoginNum.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet4(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("用户登录24小时分布");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET4.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET4[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //用户登录24小时分布
        List<Map> map = param.getUserSheet4();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellDate = rowi.createCell(0);
                XSSFCell cellLoginNum = rowi.createCell(1);

                //赋值字体样式
                cellDate.setCellStyle(cellStyle);
                cellLoginNum.setCellStyle(cellStyle);

                //单元格赋值
                cellDate.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_TIME_SLOT)));
                cellLoginNum.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet5(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("PC操作系统");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET5.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET5[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //PC操作系统
        List<Map> map = param.getUserSheet5();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellName = rowi.createCell(0);
                XSSFCell cellLoginNum = rowi.createCell(1);

                //赋值字体样式
                cellName.setCellStyle(cellStyle);
                cellLoginNum.setCellStyle(cellStyle);

                //单元格赋值
                cellName.setCellValue(String.valueOf(map.get(i).get(Constants.PC_OS_NAME)));
                cellLoginNum.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet6(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("PC浏览器排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET6.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET6[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //PC浏览器排行
        List<Map> map = param.getUserSheet6();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellName = rowi.createCell(0);
                XSSFCell cellLoginNum = rowi.createCell(1);

                //赋值字体样式
                cellName.setCellStyle(cellStyle);
                cellLoginNum.setCellStyle(cellStyle);

                //单元格赋值
                cellName.setCellValue(String.valueOf(map.get(i).get(Constants.BROWSER_NAME)));
                cellLoginNum.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet7(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("APP操作系统");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET7.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET7[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //APP操作系统
        List<Map> map = param.getUserSheet7();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellName = rowi.createCell(0);
                XSSFCell cellLoginNum = rowi.createCell(1);

                //赋值字体样式
                cellName.setCellStyle(cellStyle);
                cellLoginNum.setCellStyle(cellStyle);

                //单元格赋值
                cellName.setCellValue(String.valueOf(map.get(i).get(Constants.APP_OS_NAME)));
                cellLoginNum.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet9(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("APP版本排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET9.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET9[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //APP版本排行
        List<Map> map = param.getUserSheet9();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellName = rowi.createCell(0);
                XSSFCell cellLoginNum = rowi.createCell(1);

                //赋值字体样式
                cellName.setCellStyle(cellStyle);
                cellLoginNum.setCellStyle(cellStyle);

                //单元格赋值
                cellName.setCellValue(String.valueOf(map.get(i).get(Constants.VERSION_NAME)));
                cellLoginNum.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet8(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("APP手机型号排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET8.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET8[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //APP手机型号排行
        List<Map> map = param.getUserSheet8();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellName = rowi.createCell(0);
                XSSFCell cellLoginNum = rowi.createCell(1);

                //赋值字体样式
                cellName.setCellStyle(cellStyle);
                cellLoginNum.setCellStyle(cellStyle);

                //单元格赋值
                cellName.setCellValue(String.valueOf(map.get(i).get(Constants.PHONE_MODEL_NAME)));
                cellLoginNum.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet10(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("用户登录成功率分析");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET10.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET10[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //用户登录成功率分析
        List<Map> map = param.getUserSheet10();
        if(null != map && map.size()>0) {
            //创建单元格
            XSSFRow rowi = sheet.createRow(1);
            XSSFCell cellLoginSuccessNum = rowi.createCell(0);
            XSSFCell cellLoginFailNum = rowi.createCell(1);
            XSSFCell cellSuccessRate = rowi.createCell(2);

            //赋值字体样式
            cellLoginSuccessNum.setCellStyle(cellStyle);
            cellLoginFailNum.setCellStyle(cellStyle);
            cellSuccessRate.setCellStyle(cellStyle);

            for (int i = 0; i < map.size(); i++) {
                //单元格赋值
                if ("0".equals(map.get(i).get(Constants.LOGIN_STATUS))){
                    cellLoginSuccessNum.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_COUNT)));
                }else if ("1".equals(map.get(i).get(Constants.LOGIN_STATUS))){
                    cellLoginFailNum.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_COUNT)));
                }else {
                    cellSuccessRate.setCellValue(String.valueOf(map.get(i).get(Constants.SUCCESS_RATE)));
                }
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet11(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("用户登录失败原因分析");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET11.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET11[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //用户登录失败原因分析
        List<Map> map = param.getUserSheet11();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellLoginCause = rowi.createCell(0);
                XSSFCell cellFailCauseNum = rowi.createCell(1);
                XSSFCell cellFailRate = rowi.createCell(2);

                //赋值字体样式
                cellLoginCause.setCellStyle(cellStyle);
                cellFailCauseNum.setCellStyle(cellStyle);
                cellFailRate.setCellStyle(cellStyle);

                //单元格赋值
                cellLoginCause.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_CAUSE)));
                cellFailCauseNum.setCellValue(String.valueOf(map.get(i).get(Constants.FAIL_CAUSE_NUM)));
                cellFailRate.setCellValue(String.valueOf(map.get(i).get(Constants.FAIL_RATIO)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet12(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("用户数量按组织排行");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET12.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET12[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //用户数量按组织排行
        List<Map> map = param.getUserSheet12();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellDeptName = rowi.createCell(0);
                XSSFCell cellTenantNum = rowi.createCell(1);

                //赋值字体样式
                cellDeptName.setCellStyle(cellStyle);
                cellTenantNum.setCellStyle(cellStyle);

                //单元格赋值
                cellDeptName.setCellValue(String.valueOf(map.get(i).get(Constants.DEPT_NAME)));
                cellTenantNum.setCellValue(String.valueOf(Math.round(Double.parseDouble(String.valueOf(map.get(i).get("userNum")))))
                );
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet13(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("用户登录时长排行（PC）");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET13.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET13[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //用户登录时长
        List<Map> map = param.getUserSheet13();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellLoginUserName = rowi.createCell(0);
                XSSFCell cellLoginName = rowi.createCell(1);
                XSSFCell cellLoginTime = rowi.createCell(2);

                //赋值字体样式
                cellLoginUserName.setCellStyle(cellStyle);
                cellLoginName.setCellStyle(cellStyle);
                cellLoginTime.setCellStyle(cellStyle);

                //单元格赋值
                cellLoginUserName.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_USER_NAME)));
                cellLoginName.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_NAME)));
                cellLoginTime.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_DURATION)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet14(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("用户登录时长排行（APP）");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET13.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET13[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //用户登录时长
        List<Map> map = param.getUserSheet14();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellLoginUserName = rowi.createCell(0);
                XSSFCell cellLoginName = rowi.createCell(1);
                XSSFCell cellLoginTime = rowi.createCell(2);

                //赋值字体样式
                cellLoginUserName.setCellStyle(cellStyle);
                cellLoginName.setCellStyle(cellStyle);
                cellLoginTime.setCellStyle(cellStyle);

                //单元格赋值
                cellLoginUserName.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_USER_NAME)));
                cellLoginName.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_NAME)));
                cellLoginTime.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_DURATION)));
            }
        }
        return wb;
    }

    private XSSFWorkbook createUserSheet15(XSSFWorkbook wb, CustomVO customVO,StatisticsExportParam param,CellStyle cellStyle){
        //创建表格标题
        XSSFSheet sheet = wb.createSheet("用户登录时长排行（小程序）");
        sheet.setColumnWidth(0,256*14);
        sheet.setColumnWidth(1,256*18);
        //获取字体,单元格样式
        CellStyle style = this.exportFontAndTableStyle(wb);
        //创建表格头赋值名称
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < Constants.USER_SHEET13.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(Constants.USER_SHEET13[i]);
        }
        //赋值样式,自适应宽度
        this.exportWidthStyle(sheet,style);
        //用户登录时长
        List<Map> map = param.getUserSheet15();
        if(null != map && map.size()>0) {
            for (int i = 0; i < map.size(); i++) {
                //创建单元格
                XSSFRow rowi = sheet.createRow(i + 1);
                XSSFCell cellLoginUserName = rowi.createCell(0);
                XSSFCell cellLoginName = rowi.createCell(1);
                XSSFCell cellLoginTime = rowi.createCell(2);

                //赋值字体样式
                cellLoginUserName.setCellStyle(cellStyle);
                cellLoginName.setCellStyle(cellStyle);
                cellLoginTime.setCellStyle(cellStyle);

                //单元格赋值
                cellLoginUserName.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_USER_NAME)));
                cellLoginName.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_NAME)));
                cellLoginTime.setCellValue(String.valueOf(map.get(i).get(Constants.LOGIN_DURATION)));
            }
        }
        return wb;
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
                //告警类型排行
                TableDataInfo typeYearMonthApp = analysisExportAPI.getDevTypeYearMonthApp(statisticsVO);
                listMap  = (List<Map>) typeYearMonthApp.getRows();
                param.setEventSheet3(listMap);
                break;
            case 17:
                // 下游任务排行（独立副本，避免并行污染 TYPE 维度）
                StatisticsVO subSceneRankVo = new StatisticsVO();
                BeanUtils.copyProperties(statisticsVO, subSceneRankVo);
                subSceneRankVo.setStatDimension("SUB_TASK");
                TableDataInfo subSceneRank = analysisExportAPI.getDevTypeYearMonthApp(subSceneRankVo);
                listMap = (List<Map>) subSceneRank.getRows();
                param.setEventSheetSubScene(listMap);
                break;
            case 4:
                //告警来源排行
                TableDataInfo alarmNumSource = analysisExportAPI.getEventAlarmNumSource(statisticsVO);
                listMap  = (List<Map>) alarmNumSource.getRows();
                param.setEventSheet4(listMap);
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
            case 6:
                //告警24h折线图
                Result eventNumListAppHour = analysisExportAPI.getEventNumByHour(analysisVO);
                listMap  = (List<Map>) eventNumListAppHour.getData();
                param.setEventSheet6(listMap);
                break;
            case 66:
                //告警压制24h折线图
                Result interceptNumAppHour = analysisExportAPI.getEventNumByInterceptHour(analysisVO);
                listMap  = (List<Map>) interceptNumAppHour.getData();
                param.setEventSheet66(listMap);
                break;
            case 7:
                //摄像机告警数量统计
                TableDataInfo deviceNumAll = analysisExportAPI.getDeviceNumAll(statisticsVO);
                listMap = (List<Map>) deviceNumAll.getRows();
                param.setEventSheet7(listMap);
                break;
            case 8:
                //告警准确率分析
                Result evenAccuracy = analysisExportAPI.getEvenAccuracy(statisticsVO);
                map  = (Map) evenAccuracy.getData();
                param.setEventSheet8(map);
                break;
            case 9:
                BeanUtils.copyProperties(analysisVO, vo);
                //赋值纬度标识
                vo.setAreaBoxNum(analysisVO.getAreaAccuracySelect());
                //告警区域准确率排行
                TableDataInfo alarmNumAreaRate = analysisExportAPI.getEvenAreaAccuracy(vo);
                listMap  = (List<Map>) alarmNumAreaRate.getRows();
                param.setEventSheet9(listMap);
                break;
            case 10:
                //告警来源准确率排行
                TableDataInfo sourceAccuracy = analysisExportAPI.getEvenSourceAccuracy(analysisVO);
                listMap  = (List<Map>) sourceAccuracy.getRows();
                param.setEventSheet10(listMap);
                break;
            case 11:
                //告警类型准确率排行
                TableDataInfo evenTypeAccuracy = analysisExportAPI.getEvenTypeAccuracy(analysisVO);
                listMap  = (List<Map>) evenTypeAccuracy.getRows();
                param.setEventSheet11(listMap);
                break;
            case 18:
                // 下游任务准确率排行
                AnalysisVO subSceneAccuracyVo = new AnalysisVO();
                BeanUtils.copyProperties(analysisVO, subSceneAccuracyVo);
                subSceneAccuracyVo.setStatDimension("SUB_TASK");
                TableDataInfo subSceneAccuracy = analysisExportAPI.getEvenTypeAccuracy(subSceneAccuracyVo);
                listMap = (List<Map>) subSceneAccuracy.getRows();
                param.setEventSheetSubSceneAccuracy(listMap);
                break;
            case 12:
                //告警准确率24小时分布
                Result eventNumByHour = analysisExportAPI.getEventNumByHour(analysisVO);
                listMap  = (List<Map>) eventNumByHour.getData();
                param.setEventSheet12(listMap);
                break;
            case 13:
                BeanUtils.copyProperties(analysisVO, vo);
                //赋值纬度标识
                vo.setAreaBoxNum(analysisVO.getAreaCompletionSelect());
                //行政区域完结率统计
                TableDataInfo evenAreaCompletion = analysisExportAPI.getEvenAreaCompletion(vo);
                listMap = (List<Map>) evenAreaCompletion.getRows();
                param.setEventSheet13(listMap);
                break;
            case 14:
                //组织完结率统计
                TableDataInfo deptCompletion = analysisExportAPI.getEvenDeptCompletion(analysisVO);
                listMap  = (List<Map>) deptCompletion.getRows();
                param.setEventSheet14(listMap);
                break;

            case 15:
                //物联设备告警统计
                TableDataInfo iotDeviceNumAll = analysisExportAPI.getIotDeviceNumAll(statisticsVO);
                listMap  = (List<Map>) iotDeviceNumAll.getRows();
                param.setEventSheet15(listMap);
                break;
            case 16:
                //网格完结率统计
                TableDataInfo gridCompletion = analysisExportAPI.getEvenGridCompletion(analysisVO);
                listMap  = (List<Map>) gridCompletion.getRows();
                param.setEventSheet16(listMap);
                break;
            default: break;
        }
    }


    private void deviceMethodAll(StatisticsVO statisticsVO,AnalysisVO analysisVO,Integer num,StatisticsExportParam param){
        DeviceStatisticsVO map = new DeviceStatisticsVO();
        List<Map> listMap = new ArrayList<>();
        IotDeviceStatisticsVo iotMap = new IotDeviceStatisticsVo();
        UavDeviceStatisticsVo uavMap = new UavDeviceStatisticsVo();
        RadarDeviceStatisticsVo radarMap = new RadarDeviceStatisticsVo();
        HornDeviceStatisticsVo hornMap = new HornDeviceStatisticsVo();
        List<Map<String, String>> listMaps = new ArrayList<Map<String, String>>();
        switch (num){
            case 1:
                Result<DeviceStatisticsVO> deviceStatistics = analysisExportAPI.getDeviceStatistics(analysisVO);
                map =  deviceStatistics.getData();
                param.setDeviceSheet1(map);
                break;
            case 2:
                //摄像机数量区域排行
                TableDataInfo cameraNumByArea = analysisExportAPI.getCameraNumByArea(analysisVO);
                listMap  = (List<Map>) cameraNumByArea.getRows();
                param.setDeviceSheet2(listMap);
                break;
            case 3:
                //摄像机订购算法排行
                TableDataInfo algorithmRank = analysisExportAPI.getOrderAlgorithmRank(analysisVO);
                listMap  = (List<Map>) algorithmRank.getRows();
                param.setDeviceSheet3(listMap);
                break;
            case 4:
                //通道算法任务排行
                listMaps = statisticService.getAllAlgorithmTaskRank(analysisVO);
                param.setDeviceSheet4(listMaps);
                break;
            case 5:
                //物联设备总览
                Result<IotDeviceStatisticsVo> iotDeviceStatistics = analysisExportAPI.getIotDeviceStatistics(analysisVO);
                iotMap =  iotDeviceStatistics.getData();
                param.setDeviceSheet5(iotMap);
                break;
            case 6:
                //物联设备数量区域排行
                TableDataInfo iotDeviceStatisticsByArea = analysisExportAPI.getIotDeviceStatisticsByArea(analysisVO);
                listMap  = (List<Map>) iotDeviceStatisticsByArea.getRows();
                param.setDeviceSheet6(listMap);
                break;
            case 7:
                //无人机设备总览
                Result<UavDeviceStatisticsVo> uavDeviceStatistics = analysisExportAPI.getUavDeviceStatistics(analysisVO);
                uavMap =  uavDeviceStatistics.getData();
                param.setDeviceSheet7(uavMap);
                break;
            case 8:
                //无人机设备数量区域排行
                TableDataInfo uavDeviceStatisticsByArea = analysisExportAPI.getUavDeviceStatisticsByArea(analysisVO);
                listMap  = (List<Map>) uavDeviceStatisticsByArea.getRows();
                param.setDeviceSheet8(listMap);
                break;
            case 9:
                //雷达设备总览
                Result<RadarDeviceStatisticsVo> radarDeviceStatistics = analysisExportAPI.getRadarDeviceStatistics(analysisVO);
                radarMap =  radarDeviceStatistics.getData();
                param.setDeviceSheet9(radarMap);
                break;
            case 10:
                //雷达设备数量区域排行
                TableDataInfo radarDeviceStatisticsByArea = analysisExportAPI.getRadarDeviceStatisticsByArea(analysisVO);
                listMap  = (List<Map>) radarDeviceStatisticsByArea.getRows();
                param.setDeviceSheet10(listMap);
                break;
            case 11:
                //大喇叭设备总览
                Result<HornDeviceStatisticsVo> hornDeviceStatistics = analysisExportAPI.getHornDeviceStatistics(analysisVO);
                hornMap =  hornDeviceStatistics.getData();
                param.setDeviceSheet11(hornMap);
                break;
            case 12:
                //大喇叭数量区域排行
                TableDataInfo hornDeviceStatisticsByArea = analysisExportAPI.getHornDeviceStatisticsByArea(analysisVO);
                listMap  = (List<Map>) hornDeviceStatisticsByArea.getRows();
                param.setDeviceSheet12(listMap);
                break;
            case 13:
                //执法记录仪设备总览
                Result<DeviceStatisticsVO> recorderDeviceStatistics = analysisExportAPI.getRecorderDeviceStatistics(analysisVO);
                map =  recorderDeviceStatistics.getData();
                param.setDeviceSheet13(map);
                break;
            case 14:
                //大喇叭数量区域排行
                TableDataInfo recorderDeviceStatisticsByArea = analysisExportAPI.getRecorderDeviceStatisticsByArea(analysisVO);
                listMap  = (List<Map>) recorderDeviceStatisticsByArea.getRows();
                param.setDeviceSheet14(listMap);
                break;
            default: break;
        }
    }


    private void userMethodAll(CustomVO customVO,Integer num,StatisticsExportParam param){
        Map map = new HashMap();
        List<Map> listMap = new ArrayList<>();
        switch (num){
            case 1:
                //用户总览
                Result customNum = analysisExportAPI.getCustomNum(customVO);
                map  = (Map) customNum.getData();
                param.setUserSheet1(map);
                break;
            case 2:
                //用户登录时间统计
                TableDataInfo customNumByLoginTime = analysisExportAPI.getCustomNumByLoginTime(customVO);
                listMap  = (List<Map>) customNumByLoginTime.getRows();
                param.setUserSheet2(listMap);
                break;
            case 3:
                //用户登录次数
                TableDataInfo loginNumRank = analysisExportAPI.getLoginNumRank(customVO);
                listMap  = (List<Map>) loginNumRank.getRows();
                param.setUserSheet3(listMap);
                break;
            case 4:
                //用户登录24小时分布
                TableDataInfo loginNumByHour = analysisExportAPI.getLoginNumByHour(customVO);
                listMap = (List<Map>) loginNumByHour.getRows();
                param.setUserSheet4(listMap);
                break;
            case 5:
                //PC操作系统
                TableDataInfo loginNumPCOs = analysisExportAPI.getLoginTypeByPcOs(customVO);
                listMap  = (List<Map>) loginNumPCOs.getRows();
                param.setUserSheet5(listMap);
                break;
            case 6:
                //PC浏览器排行
                TableDataInfo loginNumPCBrower = analysisExportAPI.getLoginTypeByPcBrower(customVO);
                listMap  = (List<Map>) loginNumPCBrower.getRows();
                param.setUserSheet6(listMap);
                break;
            case 7:
                //APP操作系统
                TableDataInfo loginNumAppOs = analysisExportAPI.getLoginTypeByAppOs(customVO);
                listMap  = (List<Map>) loginNumAppOs.getRows();
                param.setUserSheet7(listMap);
                break;
            case 8:
                //APP手机型号排行
                TableDataInfo loginNumPhoneModel = analysisExportAPI.getLoginTypeByPhoneModel(customVO);
                listMap  = (List<Map>) loginNumPhoneModel.getRows();
                param.setUserSheet8(listMap);
                break;
            case 9:
                //APP版本排行
                TableDataInfo loginNumAppVersion = analysisExportAPI.getLoginTypeByAppVersion(customVO);
                listMap  = (List<Map>) loginNumAppVersion.getRows();
                param.setUserSheet9(listMap);
                break;
            case 10:
                //用户登录成功率分析
                TableDataInfo loginSuccessRate = analysisExportAPI.getLoginSuccessRate(customVO);
                listMap  = (List<Map>) loginSuccessRate.getRows();
                param.setUserSheet10(listMap);
                break;
            case 11:
                //用户登录失败原因分析
                TableDataInfo loginFailRate = analysisExportAPI.getLoginFailureRate(customVO);
                listMap  = (List<Map>) loginFailRate.getRows();
                param.setUserSheet11(listMap);
                break;
            case 12:
                //用户数量组织排行
                TableDataInfo customNumByDept = analysisExportAPI.getCustomNumByDept(customVO);
                listMap  = (List<Map>) customNumByDept.getRows();
                param.setUserSheet12(listMap);
                break;
            case 13:
                //PC用户登录时长
                customVO.setEndType(1);
                TableDataInfo durationRankPc = analysisExportAPI.getLoginDurationRank(customVO);
                listMap  = (List<Map>) durationRankPc.getRows();
                handleName(listMap);
                param.setUserSheet13(listMap);
                //APP用户登录时长
                customVO.setEndType(2);
                TableDataInfo durationRankApp = analysisExportAPI.getLoginDurationRank(customVO);
                listMap  = (List<Map>) durationRankApp.getRows();
                handleName(listMap);
                param.setUserSheet14(listMap);
                //小程序用户登录时长
                customVO.setEndType(3);
                TableDataInfo durationRankWx = analysisExportAPI.getLoginDurationRank(customVO);
                listMap  = (List<Map>) durationRankWx.getRows();
                handleName(listMap);
                param.setUserSheet15(listMap);
                break;
            default: break;
        }
    }

    public void handleName(List<Map> mapList){
        List<String> userIdList = mapList.stream().map(a -> String.valueOf(a.get("userId"))).collect(Collectors.toList());
        SysUser users = new SysUser();
        List<SysUser> userList = sysUserMapper.selectUser(users);
        if (!CollectionUtils.isEmpty(mapList)) {
            for (Map map : mapList) {
                //登录名，租户名为空赋值
                for (SysUser user : userList) {
                    if (map.get("userId").equals(user.getUserId())){
                        map.put(Constants.TENANT_NAME,user.getTenantName());
                        map.put("loginUserName",user.getUserName());
                    }
                }
            }
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
