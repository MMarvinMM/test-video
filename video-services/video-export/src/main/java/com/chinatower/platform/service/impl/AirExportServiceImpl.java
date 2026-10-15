package com.chinatower.platform.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.device.api.IAirStatisticsService;
import com.chinatower.device.entity.dto.StatisticalAnalysisReq;
import com.chinatower.device.entity.vo.ExportStatisticalAnalysisReq;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.event.api.model.DownLoadUrlsReq;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.AirExportService;
import com.chinatower.platform.utils.DownloadUtil;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author WLF
 * @description
 * @date 2024/11/6 14:06
 */
@Service
@Slf4j
public class AirExportServiceImpl implements AirExportService {
    @Resource
    private DownloadUtil downloadUtil;
    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Resource
    private IAirStatisticsService iAirStatisticsService;

    private static String DEVICE_NAME = "deviceName";
    private static String TOTAL_COUNT = "totalCount";

    /**
     * 通过地址下载
     *
     * @param response
     * @param request
     * @param req
     */
    @Override
    public void downloadByUrls(HttpServletResponse response, HttpServletRequest request, DownLoadUrlsReq req) {
        // 获取当前的用户
        String tenantName = "";
        // req.getName()有就是铁塔视角，没有则自行判断
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 铁塔视角
        if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            tenantName = "中国铁塔";
        } else {
            // 客户视角判断是否为铁塔切租户
            boolean switchFlag = SecurityUtils.isSwitch();
            //客户视角切换：已切换-sql后面拼接租户ID,未切换-
            if (switchFlag) {
                //已切换，水印设置为铁塔
                tenantName = "中国铁塔";
            } else {
                // 未切换，水印为租户名称
                tenantName = loginUser.getUser().getTenantInfo().getTenantName();
            }
        }
        // 获取原始的图像和视频 URL 列表，并进行空指针判断
        List<String> imageUrls = req.getPhotoUrls();
        List<String> videoUrlList = req.getVideoUrls();
        if (ObjectUtil.isEmpty(req.getPhotoUrls()) &&  ObjectUtil.isEmpty(req.getVideoUrls())) {
            throw new CustomException("下载图片或者视频url集合不能为空");
        }
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }
        if (videoUrlList == null) {
            videoUrlList = new ArrayList<>();
        }
        // 将视频 URL 列表添加到图像 URL 列表中，创建一个包含所有 URL 的新列表
        List<String> urls = new ArrayList<>(imageUrls);
        urls.addAll(videoUrlList);
        // 调用 OBS 加密方法获取加密后的路径
        Map<String, String> urlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(urls);
        // 使用 forEach 打印出 urlMap 中的所有 key 和 value
        urlMap.forEach((key, value) -> {
            log.info("Original URL: {}, Encrypted URL: {}", key, value);
        });
        imageUrls.replaceAll(originalUrl -> Optional.ofNullable(urlMap.get(originalUrl)).filter(newUrl -> !newUrl.isEmpty()).orElse(originalUrl));
        videoUrlList.replaceAll(originalUrl -> Optional.ofNullable(urlMap.get(originalUrl)).filter(newUrl -> !newUrl.isEmpty()).orElse(originalUrl));
        req.setPhotoUrls(imageUrls);
        req.setVideoUrls(videoUrlList);
        try {
            if ("1".equals(req.getIsZip())) {
                log.info("压缩包imageUrls大小:{},videoUrlList大小:{}", imageUrls.size(), videoUrlList.size());
                downloadUtil.downLoadByUrlsZip(req, tenantName, response);
            }else {
                log.info("单个imageUrls大小:{},videoUrlList大小:{}", imageUrls.size(), videoUrlList.size());
                downloadUtil.downLoadByUrls(req, tenantName, response);
            }
        } catch (Exception e) {
            throw new CustomException("下载图片视频出错");
        }
    }

    /**
     * @param kmzUrl
     * @param response
     */
    @Override
    public void downloadKmz(String kmzUrl, HttpServletResponse response) {
        InputStream input = null;
        OutputStream output = null;
        if (StringUtils.isEmpty(kmzUrl)) {
            throw new CustomException("kmzUrl不能为空");
        }
        String fileUrl = fileUrlEncryptUtil.dealObsFileUrl(kmzUrl);
        log.info("kmzAccessUrl:{}", fileUrl);
        try {
            // 打开连接
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            // 设置请求超时为5秒
            connection.setConnectTimeout(5 * 1000);
            // 读取数据流并保存到本地
            input = connection.getInputStream();
            byte[] data = new byte[1024];
            int len;
            output = response.getOutputStream();
            while ((len = input.read(data)) != -1) {
                output.write(data, 0, len);
            }
        } catch (IOException e) {
            log.error("Error downloading KMZ file", e);
            // 你可以选择在前端显示一个错误信息
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // 关闭流
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error("Error closing input stream", e);
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    log.error("Error closing output stream", e);
                }
            }
        }
    }

    /**
     * 通过地址下载
     *
     * @param response
     * @param request
     */
    @Override
    public void downloadByUrlsDemo(HttpServletResponse response, HttpServletRequest request) {
        // 获取当前的用户
        String tenantName = "";
        tenantName = "中国铁塔";
        downloadUtil.downLoadDemo(tenantName, response);
    }

    @Override
    public void exportStatisticalAnalysis(StatisticalAnalysisReq statisticalAnalysisReq, HttpServletResponse response) {
        try {
            // 查询数据
            ExportStatisticalAnalysisReq exportData = iAirStatisticsService.queryExportStatisticalAnalysis(statisticalAnalysisReq);

            // 设置响应头
            String fileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "飞行管理大屏统计分析清单.xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 导出Excel
            exportToExcel(exportData, response.getOutputStream());

        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    public void exportToExcel(ExportStatisticalAnalysisReq exportData, OutputStream outputStream) throws Exception {
        // 创建Workbook
        Workbook workbook = new XSSFWorkbook();

        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);

        // Sheet 1: 统计总览
        createStatisticsOverviewSheet(workbook, exportData.getStatisticsOverviewList(), headerStyle);

        // Sheet 2: 飞行距离统计排名
        createFlightDistanceRankSheet(workbook, exportData.getFlightDistanceTopList(), headerStyle);

        // Sheet 3: 飞行时长排名
        createFlightDurationRankSheet(workbook, exportData.getFlightDurationTopList(), headerStyle);

        // Sheet 4: 失败原因排行
        createFailCauseRankSheet(workbook, exportData.getFailCauseTopList(), headerStyle);

        // Sheet 5: 计划统计排名
        createPlanStatisticsRankSheet(workbook, exportData.getPlanUavFlyCountTopList(), headerStyle);

        // Sheet 6: 任务状态统计
        createTaskStatusStatisticsSheet(workbook, exportData.getTaskStatusStatisticsTopList(), headerStyle);

        // Sheet 7: 飞行趋势统计
        createFlightTrendSheet(workbook, exportData.getFlyTrendList(), headerStyle);

        // Sheet 8: 起飞时刻统计
        createTakeoffTimeSheet(workbook, exportData.getFlyTimeStatisticalList(), headerStyle);

        // Sheet 9: 飞行任务明细表
        createFlightTaskDetailSheet(workbook, exportData.getTaskDetailList(), headerStyle);

        // Sheet 10: 飞行类别统计
        createFlightCategorySheet(workbook, exportData.getFlyTypeStatisticsList(), headerStyle);

        // 写入输出流
        workbook.write(outputStream);
        workbook.close();
    }


    // Sheet 1: 统计总览
    private void createStatisticsOverviewSheet(Workbook workbook, List<Map> dataList,
                                               CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("统计总览");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"无人机使用设备数（个）", "已执行飞行计划（个）", "飞行次数（次）", "飞行距离（m）", "飞行时长（min）"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        if (dataList != null && !dataList.isEmpty()) {
            Map data = dataList.get(0);
            Row dataRow = sheet.createRow(1);

            dataRow.createCell(0).setCellValue(getStringValue(data.get("uavUsageCount")));
            dataRow.createCell(1).setCellValue(getStringValue(data.get("executedPlanCount")));
            dataRow.createCell(2).setCellValue(getStringValue(data.get("flightCount")));
            dataRow.createCell(3).setCellValue(getStringValue(data.get("totalDistance")));
            dataRow.createCell(4).setCellValue(getStringValue(data.get("totalDuration")));
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 2: 飞行距离统计排名
    private void createFlightDistanceRankSheet(Workbook workbook, List<Map> dataList,
                                               CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("飞行距离统计排名");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"序号", "设备名称", "飞行距离（m）"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            int index = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(index++);
                dataRow.createCell(1).setCellValue(getStringValue(data.get(DEVICE_NAME)));
                dataRow.createCell(2).setCellValue(getStringValue(data.get("airMileage")));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 3: 飞行时长排名
    private void createFlightDurationRankSheet(Workbook workbook, List<Map> dataList,
                                               CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("飞行时长排名");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"序号", "设备名称", "飞行时长"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            int index = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(index++);
                dataRow.createCell(1).setCellValue(getStringValue(data.get(DEVICE_NAME)));
                dataRow.createCell(2).setCellValue(getStringValue(data.get("flyTime")));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 4: 失败原因排行
    private void createFailCauseRankSheet(Workbook workbook, List<Map> dataList,
                                          CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("失败原因排行");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"序号", "失败原因", "次数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            int index = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(index++);
                dataRow.createCell(1).setCellValue(getStringValue(data.get("errorReason")));
                dataRow.createCell(2).setCellValue(getStringValue(data.get(TOTAL_COUNT)));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 5: 计划统计排名
    private void createPlanStatisticsRankSheet(Workbook workbook, List<Map> dataList,
                                               CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("计划统计排名");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"排行", "无人机名称", "计划名称", "飞行次数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            int index = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(index++);
                dataRow.createCell(1).setCellValue(getStringValue(data.get(DEVICE_NAME)));
                dataRow.createCell(2).setCellValue(getStringValue(data.get("planName")));
                dataRow.createCell(3).setCellValue(getStringValue(data.get("flyCount")));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 6: 任务状态统计
    private void createTaskStatusStatisticsSheet(Workbook workbook, List<Map> dataList,
                                                 CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("任务状态统计");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"任务状态", "数量", "占比"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(getStringValue(data.get("taskType")));
                dataRow.createCell(1).setCellValue(getStringValue(data.get("taskCount")));
                dataRow.createCell(2).setCellValue(getStringValue(data.get("taskRate")));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 7: 飞行趋势统计
    private void createFlightTrendSheet(Workbook workbook, List<Map> dataList,
                                        CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("飞行趋势统计");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"时间", "飞行次数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(getStringValue(data.get("showTime")));
                dataRow.createCell(1).setCellValue(getStringValue(data.get(TOTAL_COUNT)));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 8: 起飞时刻统计
    private void createTakeoffTimeSheet(Workbook workbook, List<Map> dataList,
                                        CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("起飞时刻统计");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"起飞时刻", "飞行次数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(getStringValue(data.get("showTime")));
                dataRow.createCell(1).setCellValue(getStringValue(data.get(TOTAL_COUNT)));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 9: 飞行任务明细表
    private void createFlightTaskDetailSheet(Workbook workbook, List<Map> dataList,
                                             CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("飞行任务明细表");

        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "序号", "设备名称", "设备编码", "飞行开始时间（时分秒）", "飞行结束时间（时分秒）",
                "计划名称", "任务状态", "执行时间", "飞行类型", "航线名称", "飞行次数",
                "飞行时长", "飞行里程（m）"
        };

        //设置动态表头
        int algorithmCount = 0;
        if (!CollectionUtils.isEmpty(dataList)){
            Map firstItem = dataList.get(0);
            int dataSize = firstItem.size();
            int headerSize = headers.length;

            if (dataSize > headerSize) {
                int diff = dataSize - headerSize;
                algorithmCount = diff / 3; // 每个算法占用3列：名称、编码、告警数量

                List<String> newHeaders = new ArrayList<>(Arrays.asList(headers));
                for (int i = 1; i <= algorithmCount; i++) {
                    newHeaders.add("算法" + i + "名称");
                    newHeaders.add("算法" + i + "编码");
                    newHeaders.add("算法" + i + "告警数量");
                }
                headers = newHeaders.toArray(new String[0]);
            }
        }


        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            int index = 1;
            for (Map<String, Object> data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                int colIndex = 0;

                // 填充固定列数据
                dataRow.createCell(colIndex++).setCellValue(index++);
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get(DEVICE_NAME)));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("uavCode")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flyStartTime")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flyEndTime")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("planName")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("taskStatus")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("realityTime")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flightMainType")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flightRouteName")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flyCount")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("flyTime")));
                dataRow.createCell(colIndex++).setCellValue(getStringValue(data.get("accumulateMileage")));

                // 动态填充算法相关列数据
                for (int i = 1; i <= algorithmCount; i++) {
                    String algorithmName = getStringValue(data.get("alarmBody" + i));
                    String algorithmCode = getStringValue(data.get("warningTypeId" + i));
                    String alarmCount = getStringValue(data.get("alarmNum" + i ));

                    // 检查是否有算法数据
                    if (!algorithmName.isEmpty() || !algorithmCode.isEmpty() || !alarmCount.isEmpty()) {
                        dataRow.createCell(colIndex++).setCellValue(algorithmName);
                        dataRow.createCell(colIndex++).setCellValue(algorithmCode);
                        dataRow.createCell(colIndex++).setCellValue(alarmCount);
                    } else {
                        // 如果没有算法数据，跳过这3列
                        dataRow.createCell(colIndex++).setCellValue("");
                        dataRow.createCell(colIndex++).setCellValue("");
                        dataRow.createCell(colIndex++).setCellValue("");
                    }
                }
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    // Sheet 10: 飞行类别统计
    private void createFlightCategorySheet(Workbook workbook, List<Map> dataList,
                                           CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("飞行类别统计");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"飞行类别", "数量", "占比"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (dataList != null && !dataList.isEmpty()) {
            int rowNum = 1;
            for (Map data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(getStringValue(data.get("flightMainType")));
                dataRow.createCell(1).setCellValue(getStringValue(data.get(TOTAL_COUNT)));
                dataRow.createCell(2).setCellValue(getStringValue(data.get("taskRate")));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 25);
        }
    }

    private String getStringValue(Object obj) {
        if (obj == null) {
            return "";
        }
        return String.valueOf(obj);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
