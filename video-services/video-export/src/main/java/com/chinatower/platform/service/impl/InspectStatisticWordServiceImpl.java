package com.chinatower.platform.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.analysis.api.entity.StatisticsExportParam;
import com.chinatower.bdm.api.InspectStatisticsAPI;
import com.chinatower.bdm.api.entity.InspectStatisticsVo;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.constants.Constants;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.DateUtils;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.InspectExportWordStatisticsService;
import com.chinatower.platform.utils.CommonUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
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
public class InspectStatisticWordServiceImpl implements InspectExportWordStatisticsService {

    @Resource
    private InspectStatisticsAPI inspectStatisticsAPI;

    @Resource
    private CommonUtils fileTool;
    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;


    @Override
    public void exportWordInspectStatistics(InspectStatisticsVo inspectStatisticsVo, HttpServletResponse response) {
        inspectStatisticsVo.setPageNum(null);
        inspectStatisticsVo.setPageSize(null);
        StatisticsExportParam param = new StatisticsExportParam();
        ByteArrayOutputStream bos = null;
        SysConfig sysConfig = fileTool.selectConfigByKey("tenant_inspect_template");
        if(ObjectUtil.isNull(sysConfig)){
            throw new CustomException("未找到该系统参数配置：tenant_inspect_template");
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
            List<Integer> paramList = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16);
            ThreadParallelUtil.run(paramList, o->{
                this.inspectMethodAll(inspectStatisticsVo,o,param);
                return null;
            }, ThreadParallelUtil.ConfigFactory.EVENT_EXPORT_CODE);
            //处理数据
            this.wordTextReplace(document,param,inspectStatisticsVo);
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

    private static void wordTextReplace(XWPFDocument document,StatisticsExportParam param,InspectStatisticsVo inspectStatisticsVo) {
        //1.巡查总览概述
        Map inspectSheet1 = param.getInspectSheet1();
        //2.网格层级数量统计
        List<Map> inspectSheet2 = param.getInspectSheet2();
        //3.网格员性别统计
        Map inspectSheet3 = param.getInspectSheet3();
        //3.网格员年龄分布统计
        Map inspectSheet33 = param.getInspectSheet4();
        //4.网格统计
        List<Map> inspectSheet4 = param.getInspectSheet5();
        //5.网格出勤率排行统计
        List<Map> inspectSheet5 = param.getInspectSheet6();
        //6.网格长出勤率排行统计
        List<Map> inspectSheet6 = param.getInspectSheet7();
        //7.普通网格员出勤率排行统计
        List<Map> inspectSheet7 = param.getInspectSheet8();
        //8.网格打卡状态统计
        List<Map> inspectSheet8 = param.getInspectSheet9();
        //9.网格员打卡状态统计
        List<Map> inspectSheet9 = param.getInspectSheet10();
        //10.网格巡查任务数量排行统计
        List<Map> inspectSheet10 = param.getInspectSheet11();
        //11.巡查任务反馈端统计
        List<Map> inspectSheet11 = param.getInspectSheet12();
        //11.巡查反馈24小时分布统计
        List<Map> inspectSheet12 = param.getInspectSheet13();
        //12.巡查任务按日期统计
        List<Map> inspectSheet13 = param.getInspectSheet14();
        //13.巡查人任务数量排行统计
        List<Map> inspectSheet14 = param.getInspectSheet15();
        //14.网格任务完成率排行统计
        List<Map> inspectSheet15 = param.getInspectSheet16();
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String tenantName = loginUser.getUser().getTenantInfo().getTenantName();
        Date date = new Date();
        //拼接 时间范围
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日");
        //导出时间
        String exportTime = outputFormat.format(date);
        String startTime = outputFormat.format(DateUtils.fomatDate(inspectStatisticsVo.getQueryStartTime()));
        String endTime = outputFormat.format(DateUtils.fomatDate(inspectStatisticsVo.getQueryEndTime()));
        String time = startTime.equals(endTime) ? startTime : startTime + "至" + endTime;
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
            if (fullText.contains("{{userName}}")) {
                fullText = fullText.replace("{{userName}}", tenantName);
            }
            if (fullText.contains("{{gridNum}}")) {
                fullText = fullText.replace("{{gridNum}}", String.valueOf(inspectSheet1.get(Constants.GRID_NUM)));
            }
            if (fullText.contains("{{gridMarkNum}}")) {
                fullText = fullText.replace("{{gridMarkNum}}", String.valueOf(inspectSheet1.get("gridMarkNum")));
            }
            if (fullText.contains("{{gridNoMarkNum}}")) {
                fullText = fullText.replace("{{gridNoMarkNum}}", String.valueOf(inspectSheet1.get("gridNoMarkNum")));
            }
            if (fullText.contains("{{gridKeeperNum}}")) {
                fullText = fullText.replace("{{gridKeeperNum}}", String.valueOf(inspectSheet1.get(Constants.GRID_KEEPER_NUM)));
            }
            if (fullText.contains("{{gridKeeperOneNum}}")) {
                fullText = fullText.replace("{{gridKeeperOneNum}}", String.valueOf(inspectSheet1.get("gridKeeperOneNum")));
            }
            if (fullText.contains("{{gridKeeperTwoNum}}")) {
                fullText = fullText.replace("{{gridKeeperTwoNum}}", String.valueOf(inspectSheet1.get("gridKeeperTwoNum")));
            }
            if (fullText.contains("{{inspectionTaskNum}}")) {
                fullText = fullText.replace("{{inspectionTaskNum}}", String.valueOf(inspectSheet1.get(Constants.INSPECTION_TASK_NUM)));
            }
            if (fullText.contains("{{taskCompletedNum}}")) {
                fullText = fullText.replace("{{taskCompletedNum}}", String.valueOf(inspectSheet1.get(Constants.TASK_COMPLETED_NUM)));
            }
            if (fullText.contains("{{taskIncompleteNum}}")) {
                fullText = fullText.replace("{{taskIncompleteNum}}", String.valueOf(inspectSheet1.get(Constants.TASK_INCOMPLETE_NUM)));
            }
            if (fullText.contains("{{attendanceRate}}")) {
                fullText = fullText.replace("{{attendanceRate}}", "0".equals(String.valueOf(inspectSheet1.get("workDay"))) ? Constants.SEP : inspectSheet1.get(Constants.ATTENDANCE_RATE) + Constants.PER_CENT);
            }
            if (fullText.contains("{{normalCount}}")) {
                fullText = fullText.replace("{{normalCount}}", String.valueOf(inspectSheet1.get("normalCount")));
            }
            if (fullText.contains("{{noNormalCount}}")) {
                fullText = fullText.replace("{{noNormalCount}}", String.valueOf(inspectSheet1.get("noNormalCount")));
            }
            //1.网格层级数量统计
            if (fullText.contains("{{table1}}")) {
                tableTitle = fullText.replace("{{table1}}", "表1 网格层级数量统计");
                fullText = "";
            }
            if (fullText.contains("{{gridLevelNum1}}")) {
                fullText = fullText.replace("{{gridLevelNum1}}", String.valueOf(inspectSheet2.size()));
            }
            //2.网格员年龄分布统计
            //处理年龄数据为list
            List<Map> list33 = new ArrayList<>();
            if (null != inspectSheet33) {
                Map map1 = new HashMap();
                map1.put("age", "20岁以下");
                map1.put("num", inspectSheet33.get("lessTwenty"));
                map1.put("rate", inspectSheet33.get("lessTwentyRate"));
                list33.add(map1);
                Map map2 = new HashMap();
                map2.put("age", "21-30岁");
                map2.put("num", inspectSheet33.get("twentyToThirty"));
                map2.put("rate", inspectSheet33.get("twentyToThirtyRate"));
                list33.add(map2);
                Map map3 = new HashMap();
                map3.put("age", "31岁-40岁");
                map3.put("num", inspectSheet33.get("thirtyToForty"));
                map3.put("rate", inspectSheet33.get("thirtyToFortyRate"));
                list33.add(map3);
                Map map4 = new HashMap();
                map4.put("age", "41岁-50岁");
                map4.put("num", inspectSheet33.get("fortyToFifty"));
                map4.put("rate", inspectSheet33.get("fortyToFiftyRate"));
                list33.add(map4);
                Map map5 = new HashMap();
                map5.put("age", "50岁以上");
                map5.put("num", inspectSheet33.get("moreFifty"));
                map5.put("rate", inspectSheet33.get("moreFiftyRate"));
                list33.add(map5);
                Map map6 = new HashMap();
                map6.put("age", "未知");
                map6.put("num", inspectSheet33.get(Constants.UNKNOWN_NUM));
                map6.put("rate", inspectSheet33.get(Constants.UNKNOWN_RATE));
                list33.add(map6);
                list33 = list33.stream().sorted(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get("num")))).reversed()).collect(Collectors.toList());
            }

            if (fullText.contains("{{table2}}")) {
                tableTitle = fullText.replace("{{table2}}", "表2 网格员年龄分布统计");
                fullText = "";
            }
            if (fullText.contains("{{gridKeeperTotalNum2}}")) {
                fullText = fullText.replace("{{gridKeeperTotalNum2}}", String.valueOf(inspectSheet1.get(Constants.GRID_KEEPER_NUM)));
            }
            if (fullText.contains("{{gridKeeperManNum2}}")) {
                fullText = fullText.replace("{{gridKeeperManNum2}}", String.valueOf(inspectSheet3.get("gridKeeperManNum")));
            }
            if (fullText.contains("{{manRate2}}")) {
                fullText = fullText.replace("{{manRate2}}", inspectSheet3.get("manRate") + Constants.PER_CENT);
            }
            if (fullText.contains("{{gridKeeperWomanNum2}}")) {
                fullText = fullText.replace("{{gridKeeperWomanNum2}}", String.valueOf(inspectSheet3.get("gridKeeperWomanNum")));
            }
            if (fullText.contains("{{womanRate2}}")) {
                fullText = fullText.replace("{{womanRate2}}", inspectSheet3.get("womanRate") + Constants.PER_CENT);
            }
            if (fullText.contains("{{unknownNum2}}")) {
                fullText = fullText.replace("{{unknownNum2}}", String.valueOf(inspectSheet3.get(Constants.UNKNOWN_NUM)));
            }
            if (fullText.contains("{{unknownRate2}}")) {
                fullText = fullText.replace("{{unknownRate2}}", inspectSheet3.get(Constants.UNKNOWN_RATE) + Constants.PER_CENT);
            }
            if (fullText.contains("{{numMaxAge2}}")) {
                fullText = fullText.replace("{{numMaxAge2}}", String.valueOf(list33.get(0).get("age")));
            }
            if (fullText.contains("{{gridKeeperNum2}}")) {
                fullText = fullText.replace("{{gridKeeperNum2}}", String.valueOf(list33.get(0).get("num")));
            }
            if (fullText.contains("{{rate2}}")) {
                fullText = fullText.replace("{{rate2}}", list33.get(0).get("rate") + Constants.PER_CENT);
            }

            //3.网格统计
            List<Map> map4 = null;
            if (inspectSheet4 != null && inspectSheet4.size() > 0) {
                map4 = inspectSheet4.stream()
                        .sorted(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get(Constants.GRID_KEEPER_NUM)))).reversed()
                                .thenComparing(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get(Constants.GRID_LEVEL)))))
                        ).collect(Collectors.toList());
            }
            if (fullText.contains("{{table3}}")) {
                tableTitle = fullText.replace("{{table3}}", "表3 网格统计");
                fullText = "";
            }
            if (fullText.contains("{{gridName3}}")) {
                fullText = fullText.replace("{{gridName3}}", inspectSheet4 != null && inspectSheet4.size() > 0 ? String.valueOf(map4.get(0).get(Constants.GRID_NAME)) : Constants.SEP);
            }
            if (fullText.contains("{{gridLeve3}}")) {
                fullText = fullText.replace("{{gridLeve3}}", inspectSheet4 != null && inspectSheet4.size() > 0 ? String.valueOf(map4.get(0).get(Constants.GRID_LEVEL_NAME)) : Constants.SEP);
            }
            if (fullText.contains("{{pointStatus3}}")) {
                fullText = fullText.replace("{{pointStatus3}}", inspectSheet4 != null && inspectSheet4.size() > 0 ? ("1".equals(String.valueOf(map4.get(0).get("pointStatus"))) ? "已标绘" : "未标绘") : Constants.SEP);
            }
            if (fullText.contains("{{keeperName3}}")) {
                fullText = fullText.replace("{{keeperName3}}", inspectSheet4 != null && inspectSheet4.size() > 0 && "" != String.valueOf(map4.get(0).get(Constants.KEEPER_NAME)) ? String.valueOf(map4.get(0).get(Constants.KEEPER_NAME)) : Constants.SEP);
            }
            if (fullText.contains("{{gridKeeperNum3}}")) {
                fullText = fullText.replace("{{gridKeeperNum3}}", inspectSheet4 != null && inspectSheet4.size() > 0 ? String.valueOf(map4.get(0).get(Constants.GRID_KEEPER_NUM)) : Constants.SEP);
            }
            //4.网格出勤率排行统计
            if (fullText.contains("{{table4}}")) {
                tableTitle = fullText.replace("{{table4}}", "表4 网格出勤率排行统计");
                fullText = "";
            }
            if (fullText.contains("{{gridName4}}")) {
                fullText = fullText.replace("{{gridName4}}", inspectSheet5 != null && inspectSheet5.size() > 0 ? String.valueOf(inspectSheet5.get(0).get(Constants.GRID_NAME)) : Constants.SEP);
            }
            if (fullText.contains("{{attendanceRate4}}")) {
                fullText = fullText.replace("{{attendanceRate4}}", inspectSheet5 != null && inspectSheet5.size() > 0 && null != inspectSheet5.get(0).get(Constants.ATTENDANCE_RATE) ? inspectSheet5.get(0).get(Constants.ATTENDANCE_RATE) + Constants.PER_CENT : Constants.SEP);
            }
            //5.网格长出勤率排行统计
            if (fullText.contains("{{table5}}")) {
                tableTitle = fullText.replace("{{table5}}", "表5 网格长出勤率排行统计");
                fullText = "";
            }
            if (fullText.contains("{{keeperName5}}")) {
                fullText = fullText.replace("{{keeperName5}}", inspectSheet6 != null && inspectSheet6.size() > 0 ? String.valueOf(inspectSheet6.get(0).get(Constants.KEEPER_NAME)) : Constants.SEP);
            }
            if (fullText.contains("{{attendanceRate5}}")) {
                fullText = fullText.replace("{{attendanceRate5}}", inspectSheet6 != null && inspectSheet6.size() > 0 && null != inspectSheet6.get(0).get(Constants.ATTENDANCE_RATE) ? inspectSheet6.get(0).get(Constants.ATTENDANCE_RATE) + Constants.PER_CENT : Constants.SEP);
            }
            //6.网格员出勤率排行统计
            if (fullText.contains("{{table6}}")) {
                tableTitle = fullText.replace("{{table6}}", "表6 普通网格员出勤率排行统计");
                fullText = "";
            }
            if (fullText.contains("{{keeperName6}}")) {
                fullText = fullText.replace("{{keeperName6}}", inspectSheet7 != null && inspectSheet7.size() > 0 ? String.valueOf(inspectSheet7.get(0).get(Constants.KEEPER_NAME)) : Constants.SEP);
            }
            if (fullText.contains("{{attendanceRate6}}")) {
                fullText = fullText.replace("{{attendanceRate6}}", inspectSheet7 != null && inspectSheet7.size() > 0 && null != inspectSheet7.get(0).get(Constants.ATTENDANCE_RATE) ? inspectSheet7.get(0).get(Constants.ATTENDANCE_RATE) + Constants.PER_CENT : Constants.SEP);
            }
            //7.网格打卡状态统计
            List<Map> map8 = null;
            List<Map> map88 = null;
            if (inspectSheet8 != null && inspectSheet8.size() > 0) {
                map8 = inspectSheet8.stream()
                        .sorted(Comparator.comparing((Map m) -> Long.parseLong(String.valueOf(m.get(Constants.IN_NUM)))).reversed()
                                .thenComparing(Comparator.comparing((Map m) -> Long.parseLong(String.valueOf(m.get(Constants.GRID_LEVEL)))))
                                .thenComparing(Comparator.comparing((Map m) -> Double.parseDouble(String.valueOf(m.get(Constants.ATTENDANCE_RATES)))).reversed())
                        ).collect(Collectors.toList());
                map88 = inspectSheet8.stream()
                        .sorted(Comparator.comparing((Map m) -> Long.parseLong(String.valueOf(m.get(Constants.OUT_NUM)))).reversed()
                                .thenComparing(Comparator.comparing((Map m) -> Long.parseLong(String.valueOf(m.get(Constants.GRID_LEVEL)))))
                                .thenComparing(Comparator.comparing((Map m) -> Double.parseDouble(String.valueOf(m.get(Constants.ATTENDANCE_RATES)))).reversed())
                        ).collect(Collectors.toList());

            }
            if (fullText.contains("{{table7}}")) {
                tableTitle = fullText.replace("{{table7}}", "表7 网格打卡状态统计");
                fullText = "";
            }
            if (fullText.contains("{{inMaxGridName7}}")) {
                fullText = fullText.replace("{{inMaxGridName7}}", inspectSheet8 != null && inspectSheet8.size() > 0 ? String.valueOf(map8.get(0).get(Constants.GRID_NAME)) : Constants.SEP);
            }
            if (fullText.contains("{{inNum7}}")) {
                fullText = fullText.replace("{{inNum7}}", inspectSheet8 != null && inspectSheet8.size() > 0 ? String.valueOf(map8.get(0).get(Constants.IN_NUM)) : Constants.SEP);
            }
            if (fullText.contains("{{outMaxGridName7}}")) {
                fullText = fullText.replace("{{outMaxGridName7}}", inspectSheet8 != null && inspectSheet8.size() > 0 ? String.valueOf(map88.get(0).get(Constants.GRID_NAME)) : Constants.SEP);
            }
            if (fullText.contains("{{outNum7}}")) {
                fullText = fullText.replace("{{outNum7}}", inspectSheet8 != null && inspectSheet8.size() > 0 ? String.valueOf(map88.get(0).get(Constants.OUT_NUM)) : Constants.SEP);
            }
            //8.网格员打卡状态统计
            List<Map> map9 = null;
            List<Map> map99 = null;
            if (inspectSheet9 != null && inspectSheet9.size() > 0) {
                map9 = inspectSheet9.stream()
                        .sorted(Comparator.comparing((Map m) -> Long.parseLong(String.valueOf(m.get(Constants.IN_NUM)))).reversed()
                                .thenComparing(Comparator.comparing((Map m) -> Double.parseDouble(String.valueOf(m.get(Constants.ATTENDANCE_RATES)))).reversed())
                                .thenComparing((Map m) -> m.get(Constants.KEEPER_NAME), Collator.getInstance(Locale.CHINA))
                        ).collect(Collectors.toList());
                map99 = inspectSheet9.stream()
                        .sorted(Comparator.comparing((Map m) -> Long.parseLong(String.valueOf(m.get(Constants.OUT_NUM)))).reversed()
                                .thenComparing(Comparator.comparing((Map m) -> Double.parseDouble(String.valueOf(m.get(Constants.ATTENDANCE_RATES)))).reversed())
                                .thenComparing((Map m) -> m.get(Constants.KEEPER_NAME), Collator.getInstance(Locale.CHINA))
                        ).collect(Collectors.toList());

            }
            if (fullText.contains("{{table8}}")) {
                tableTitle = fullText.replace("{{table8}}", "表8 网格员打卡状态统计");
                fullText = "";
            }
            if (fullText.contains("{{inMaxKeeperName8}}")) {
                fullText = fullText.replace("{{inMaxKeeperName8}}", inspectSheet9 != null && inspectSheet9.size() > 0 ? String.valueOf(map9.get(0).get(Constants.KEEPER_NAME)) : Constants.SEP);
            }
            if (fullText.contains("{{inNum8}}")) {
                fullText = fullText.replace("{{inNum8}}", inspectSheet9 != null && inspectSheet9.size() > 0 ? String.valueOf(map9.get(0).get(Constants.IN_NUM)) : Constants.SEP);
            }
            if (fullText.contains("{{outMaxKeeperName8}}")) {
                fullText = fullText.replace("{{outMaxKeeperName8}}", inspectSheet9 != null && inspectSheet9.size() > 0 ? String.valueOf(map99.get(0).get(Constants.KEEPER_NAME)) : Constants.SEP);
            }
            if (fullText.contains("{{outNum8}}")) {
                fullText = fullText.replace("{{outNum8}}", inspectSheet9 != null && inspectSheet9.size() > 0 ? String.valueOf(map99.get(0).get(Constants.OUT_NUM)) : Constants.SEP);
            }
            //9.网格巡查任务数量排行统计
            if (fullText.contains("{{table9}}")) {
                tableTitle = fullText.replace("{{table9}}", "表9 网格巡查任务数量排行统计");
                fullText = "";
            }
            if (fullText.contains("{{gridName9}}")) {
                fullText = fullText.replace("{{gridName9}}", inspectSheet10 != null && inspectSheet10.size() > 0 ? String.valueOf(inspectSheet10.get(0).get(Constants.GRID_NAME)) : Constants.SEP);
            }
            if (fullText.contains("{{inspectionTaskNum9}}")) {
                fullText = fullText.replace("{{inspectionTaskNum9}}", inspectSheet10 != null && inspectSheet10.size() > 0 ? String.valueOf(inspectSheet10.get(0).get(Constants.INSPECTION_TASK_NUM)) : Constants.SEP);
            }
            if (fullText.contains("{{taskIncompleteNum9}}")) {
                fullText = fullText.replace("{{taskIncompleteNum9}}", inspectSheet10 != null && inspectSheet10.size() > 0 ? String.valueOf(inspectSheet10.get(0).get(Constants.TASK_INCOMPLETE_NUM)) : Constants.SEP);
            }
            if (fullText.contains("{{taskCompletedNum9}}")) {
                fullText = fullText.replace("{{taskCompletedNum9}}", inspectSheet10 != null && inspectSheet10.size() > 0 ? String.valueOf(inspectSheet10.get(0).get(Constants.TASK_COMPLETED_NUM)) : Constants.SEP);
            }
            //10.巡查任务24小时分布统计
            List<Map> map12 = null;
            if (inspectSheet12 != null && inspectSheet12.size() > 0) {
                map12 = inspectSheet12.stream()
                        .sorted(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get(Constants.INSPECTION_TASK_BACK_NUM)))).reversed()
                                .thenComparing(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get(Constants.PARAM_LABEL)).split(":")[0])))
                        ).collect(Collectors.toList());
            }
                if (fullText.contains("{{table10}}")) {
                    tableTitle = fullText.replace("{{table10}}", "表10 巡查任务24小时分布统计");
                    fullText = "";
                }
                if (fullText.contains("{{inspectionTaskNumApp10}}")) {
                    fullText = fullText.replace("{{inspectionTaskNumApp10}}", inspectSheet11 != null && inspectSheet11.size() > 0 ? String.valueOf(inspectSheet11.get(0).get("inspectionTaskNumApp")) : Constants.SEP);
                }
                if (fullText.contains("{{appRate10}}")) {
                    fullText = fullText.replace("{{appRate10}}", inspectSheet11 != null && inspectSheet11.size() > 0 ? inspectSheet11.get(0).get("appRate") + Constants.PER_CENT : Constants.SEP);
                }
                if (fullText.contains("{{inspectionTaskNumPc10}}")) {
                    fullText = fullText.replace("{{inspectionTaskNumPc10}}", inspectSheet11 != null && inspectSheet11.size() > 0 ? String.valueOf(inspectSheet11.get(0).get("inspectionTaskNumPc")) : Constants.SEP);
                }
                if (fullText.contains("{{pcRate10}}")) {
                    fullText = fullText.replace("{{pcRate10}}", inspectSheet11 != null && inspectSheet11.size() > 0 ? inspectSheet11.get(0).get("pcRate") + Constants.PER_CENT : Constants.SEP);
                }
                if (fullText.contains("{{label10}}")) {
                    fullText = fullText.replace("{{label10}}", inspectSheet12 != null && inspectSheet12.size() > 0 ? String.valueOf(map12.get(0).get(Constants.PARAM_LABEL)) : Constants.SEP);
                }

                if (fullText.contains("{{inspectionTaskBackNum10}}")) {
                    fullText = fullText.replace("{{inspectionTaskBackNum10}}", inspectSheet12 != null && inspectSheet12.size() > 0 ? String.valueOf(map12.get(0).get(Constants.INSPECTION_TASK_BACK_NUM)) : Constants.SEP);
                }
                //11.巡查任务按日期统计
                List<Map> map13 = null;
                if (inspectSheet13 != null && inspectSheet13.size() > 0) {
                    map13 = inspectSheet13.stream()
                            .sorted(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get(Constants.INSPECTION_TASK_NUM)))).reversed()
                                    .thenComparing(Comparator.comparing((Map m) -> LocalDate.parse(String.valueOf(m.get(Constants.ASSIGNED_DATE)))))
                            ).collect(Collectors.toList());
                }
                if (fullText.contains("{{table11}}")) {
                    tableTitle = fullText.replace("{{table11}}", "表11 巡查任务按日期统计");
                    fullText = "";
                }
                if (fullText.contains("{{assignedDate11}}")) {
                    fullText = fullText.replace("{{assignedDate11}}", inspectSheet13 != null && inspectSheet13.size() > 0 ? String.valueOf(map13.get(0).get(Constants.ASSIGNED_DATE)) : Constants.SEP);
                }
                if (fullText.contains("{{inspectionTaskNum11}}")) {
                    fullText = fullText.replace("{{inspectionTaskNum11}}", inspectSheet13 != null && inspectSheet13.size() > 0 ? String.valueOf(map13.get(0).get(Constants.INSPECTION_TASK_NUM)) : Constants.SEP);
                }
                if (fullText.contains("{{taskCompletedNum11}}")) {
                    fullText = fullText.replace("{{taskCompletedNum11}}", inspectSheet13 != null && inspectSheet13.size() > 0 ? String.valueOf(map13.get(0).get(Constants.TASK_COMPLETED_NUM)) : Constants.SEP);
                }
                if (fullText.contains("{{taskIncompleteNum11}}")) {
                    fullText = fullText.replace("{{taskIncompleteNum11}}", inspectSheet13 != null && inspectSheet13.size() > 0 ? String.valueOf(map13.get(0).get(Constants.TASK_INCOMPLETE_NUM)) : Constants.SEP);
                }
                //12.巡查人任务数量排行统计
                List<Map> map14 = null;
                if (inspectSheet14 != null && inspectSheet14.size() > 0) {
                    map14 = inspectSheet14.stream()
                            .sorted(Comparator.comparing((Map m) -> Double.parseDouble(String.valueOf(m.get("completedRates")))).reversed()
                                    .thenComparing(Comparator.comparing((Map m) -> String.valueOf(m.get(Constants.KEEPER_NAME)), Collator.getInstance(Locale.CHINA)))
                            ).collect(Collectors.toList());
                }
                if (fullText.contains("{{table12}}")) {
                    tableTitle = fullText.replace("{{table12}}", "表12 巡查人任务数量排行统计");
                    fullText = "";
                }
                if (fullText.contains("{{numMaxKeeperName12}}")) {
                    fullText = fullText.replace("{{numMaxKeeperName12}}", inspectSheet14 != null && inspectSheet14.size() > 0 ? String.valueOf(inspectSheet14.get(0).get(Constants.KEEPER_NAME)) : Constants.SEP);
                }
                if (fullText.contains("{{inspectionTaskNum12}}")) {
                    fullText = fullText.replace("{{inspectionTaskNum12}}", inspectSheet14 != null && inspectSheet14.size() > 0 ? String.valueOf(inspectSheet14.get(0).get(Constants.INSPECTION_TASK_NUM)) : Constants.SEP);
                }
                if (fullText.contains("{{taskCompletedNum12}}")) {
                    fullText = fullText.replace("{{taskCompletedNum12}}", inspectSheet14 != null && inspectSheet14.size() > 0 ? String.valueOf(inspectSheet14.get(0).get(Constants.TASK_COMPLETED_NUM)) : Constants.SEP);
                }
                if (fullText.contains("{{taskIncompleteNum12}}")) {
                    fullText = fullText.replace("{{taskIncompleteNum12}}", inspectSheet14 != null && inspectSheet14.size() > 0 ? String.valueOf(inspectSheet14.get(0).get(Constants.TASK_INCOMPLETE_NUM)) : Constants.SEP);
                }
                if (fullText.contains("{{rateMaxKeeperName12}}")) {
                    fullText = fullText.replace("{{rateMaxKeeperName12}}", inspectSheet14 != null && inspectSheet14.size() > 0 ? String.valueOf(map14.get(0).get(Constants.KEEPER_NAME)) : Constants.SEP);
                }

                if (fullText.contains("{{completedRate12}}")) {
                    fullText = fullText.replace("{{completedRate12}}", inspectSheet14 != null && inspectSheet14.size() > 0 ? map14.get(0).get(Constants.COMPLETED_RATE) + Constants.PER_CENT : Constants.SEP);
                }
                //13.网格任务完成率排行统计
                if (fullText.contains("{{table13}}")) {
                    tableTitle = fullText.replace("{{table13}}", "表13 网格任务完成率排行统计");
                    fullText = "";
                }
                if (fullText.contains("{{gridName13}}")) {
                    fullText = fullText.replace("{{gridName13}}", inspectSheet15 != null && inspectSheet15.size() > 0 ? String.valueOf(inspectSheet15.get(0).get(Constants.GRID_NAME)) : Constants.SEP);
                }
                if (fullText.contains("{{taskCompletedRate13}}")) {
                    fullText = fullText.replace("{{taskCompletedRate13}}", inspectSheet15 != null && inspectSheet15.size() > 0 && null != inspectSheet15.get(0).get(Constants.TASK_COMPLETED_RATE) ? inspectSheet15.get(0).get(Constants.TASK_COMPLETED_RATE) + Constants.PER_CENT : Constants.SEP);
                }

                // 删除原始段落中的所有运行
                while (paragraph.getRuns().size() > 0) {
                    paragraph.removeRun(0);
                }
                if (!StringUtil.isEmpty(fullText)) {
                    // 创建一个新运行，并将更新后的文本添加到该运行中
                    XWPFRun newRun = paragraph.createRun();
                    newRun.setText(fullText, 0);
                    newRun.setFontFamily("宋体");
                } else {
                    // 创建一个新运行，并将更新后的文本添加到该运行中
                    XWPFRun newRun1 = paragraph.createRun();
                    newRun1.setText(tableTitle, 0);
                    newRun1.setFontSize(11);
                    newRun1.setFontFamily("黑体");
                }
            }
                // 填充表格1数据
                XWPFTable table = document.getTables().get(0);
                if (null != inspectSheet2 && inspectSheet2.size() > 0) {
                    // 删除表格的第二行
                    table.removeRow(1);
                    for (int i = 0; i < inspectSheet2.size(); i++) {
                        XWPFTableRow row = table.createRow();
                        tableNormalFormat(table, row, 0, String.valueOf(i + 1));
                        tableNormalFormat(table, row, 1, String.valueOf(inspectSheet2.get(i).get(Constants.GRID_LEVEL_NAME)));
                        tableNormalFormat(table, row, 2, String.valueOf(inspectSheet2.get(i).get(Constants.GRID_NUM)));
                    }
                }

                // 填充表格2数据
                XWPFTable table2 = document.getTables().get(1);
                if (null != inspectSheet33 && inspectSheet33.size() > 0) {
                    // 删除表格的第二行
                    table2.removeRow(1);
                        List<Map> list33 = new ArrayList<>();
                        Map map1 = new HashMap();
                        map1.put("age","20岁以下");
                        map1.put("num",inspectSheet33.get("lessTwenty"));
                        map1.put("rate",inspectSheet33.get("lessTwentyRate"));
                        list33.add(map1);
                        Map map2 = new HashMap();
                        map2.put("age","21-30岁");
                        map2.put("num",inspectSheet33.get("twentyToThirty"));
                        map2.put("rate",inspectSheet33.get("twentyToThirtyRate"));
                        list33.add(map2);
                        Map map3 = new HashMap();
                        map3.put("age","31岁-40岁");
                        map3.put("num",inspectSheet33.get("thirtyToForty"));
                        map3.put("rate",inspectSheet33.get("thirtyToFortyRate"));
                        list33.add(map3);
                        Map map4 = new HashMap();
                        map4.put("age","41岁-50岁");
                        map4.put("num",inspectSheet33.get("fortyToFifty"));
                        map4.put("rate",inspectSheet33.get("fortyToFiftyRate"));
                        list33.add(map4);
                        Map map5 = new HashMap();
                        map5.put("age","50岁以上");
                        map5.put("num",inspectSheet33.get("moreFifty"));
                        map5.put("rate",inspectSheet33.get("moreFiftyRate"));
                        list33.add(map5);
                        Map map6 = new HashMap();
                        map6.put("age","未知");
                        map6.put("num",inspectSheet33.get(Constants.UNKNOWN_NUM));
                        map6.put("rate",inspectSheet33.get(Constants.UNKNOWN_RATE));
                        list33.add(map6);
                    for (int i = 0; i < list33.size(); i++) {
                        XWPFTableRow row = table2.createRow();
                        tableNormalFormat(table2, row, 0, String.valueOf(i + 1));
                        tableNormalFormat(table2, row, 1, String.valueOf(list33.get(i).get("age")));
                        tableNormalFormat(table2, row, 2, String.valueOf(list33.get(i).get(Constants.NUM)));
                        tableNormalFormat(table2, row, 3, list33.get(i).get("rate")+Constants.PER_CENT);
                    }
                }

                // 填充表格3数据
                XWPFTable table3 = document.getTables().get(2);
                // 删除表格的第二行
                table3.removeRow(1);
                for (int i = 0; i < inspectSheet4.size(); i++) {
                    XWPFTableRow row = table3.createRow();
                    tableNormalFormat(table3, row, 0, String.valueOf(i + 1));
                    tableNormalFormat(table3, row, 1, String.valueOf(inspectSheet4.get(i).get(Constants.GRID_LEVEL_NAME)));
                    tableNormalFormat(table3, row, 2, String.valueOf(inspectSheet4.get(i).get(Constants.GRID_NAME)));
                    tableNormalFormat(table3, row, 3, "0".equals(String.valueOf(inspectSheet4.get(i).get("pointStatus")))?"未标绘":"已标绘");
                    tableNormalFormat(table3, row, 4, "".equals(String.valueOf(inspectSheet4.get(i).get(Constants.KEEPER_NAME)))?Constants.SEP:String.valueOf(inspectSheet4.get(i).get(Constants.KEEPER_NAME)));
                    tableNormalFormat(table3, row, 5, String.valueOf(inspectSheet4.get(i).get(Constants.GRID_KEEPER_NUM)));
                }


                // 填充表格4数据
                XWPFTable table4 = document.getTables().get(3);
                if (null != inspectSheet5 && inspectSheet5.size() > 0) {
                    // 删除表格的第二行
                    table4.removeRow(1);
                    for (int i = 0; i < inspectSheet5.size(); i++) {
                        XWPFTableRow row = table4.createRow();
                        tableNormalFormat(table4, row, 0, String.valueOf(i + 1));
                        tableNormalFormat(table4, row, 1, String.valueOf(inspectSheet5.get(i).get(Constants.GRID_NAME)));
                        tableNormalFormat(table4, row, 2, null == inspectSheet5.get(i).get(Constants.ATTENDANCE_RATE) ? Constants.SEP : inspectSheet5.get(i).get(Constants.ATTENDANCE_RATE) + Constants.PER_CENT);
                    }
                }

                // 填充表格5数据
                XWPFTable table5 = document.getTables().get(4);
                if (null != inspectSheet6 && inspectSheet6.size() > 0) {
                    // 删除表格的第二行
                    table5.removeRow(1);
                    for (int i = 0; i < inspectSheet6.size(); i++) {
                        XWPFTableRow row = table5.createRow();
                        tableNormalFormat(table5, row, 0, String.valueOf(i + 1));
                        tableNormalFormat(table5, row, 1, String.valueOf(inspectSheet6.get(i).get(Constants.KEEPER_NAME)));
                        tableNormalFormat(table5, row, 2, null == inspectSheet6.get(i).get(Constants.ATTENDANCE_RATE) ? Constants.SEP : inspectSheet6.get(i).get(Constants.ATTENDANCE_RATE) + Constants.PER_CENT);
                    }
                }

                // 填充表格6数据
                XWPFTable table6 = document.getTables().get(5);
                 if (null != inspectSheet7 && inspectSheet7.size() > 0) {
                     // 删除表格的第二行
                     table6.removeRow(1);
                     for (int i = 0; i < inspectSheet7.size(); i++) {
                         XWPFTableRow row = table6.createRow();
                         tableNormalFormat(table6, row, 0, String.valueOf(i + 1));
                         tableNormalFormat(table6, row, 1, String.valueOf(inspectSheet7.get(i).get(Constants.KEEPER_NAME)));
                         tableNormalFormat(table6, row, 2, null == inspectSheet7.get(i).get(Constants.ATTENDANCE_RATE) ? Constants.SEP : inspectSheet7.get(i).get(Constants.ATTENDANCE_RATE) + Constants.PER_CENT);
                     }
                 }

                // 填充表格7数据
                XWPFTable table7 = document.getTables().get(6);
                if (null != inspectSheet8 && inspectSheet8.size() > 0) {
                    // 删除表格的第二行
                    table7.removeRow(1);
                    for (int i = 0; i < inspectSheet8.size(); i++) {
                        XWPFTableRow row = table7.createRow();
                        tableNormalFormat(table7, row, 0, String.valueOf(i + 1));
                        tableNormalFormat(table7, row, 1, String.valueOf(inspectSheet8.get(i).get(Constants.GRID_LEVEL_NAME)));
                        tableNormalFormat(table7, row, 2, String.valueOf(inspectSheet8.get(i).get(Constants.GRID_NAME)));
                        tableNormalFormat(table7, row, 3, null == inspectSheet8.get(i).get(Constants.ATTENDANCE_RATE) ? Constants.SEP : inspectSheet8.get(i).get(Constants.ATTENDANCE_RATE) + Constants.PER_CENT);
                        tableNormalFormat(table7, row, 4, String.valueOf(inspectSheet8.get(i).get(Constants.IN_NUM)));
                        tableNormalFormat(table7, row, 5, String.valueOf(inspectSheet8.get(i).get("lateNum")));
                        tableNormalFormat(table7, row, 6, String.valueOf(inspectSheet8.get(i).get("noInNum")));
                        tableNormalFormat(table7, row, 7, String.valueOf(inspectSheet8.get(i).get(Constants.OUT_NUM)));
                        tableNormalFormat(table7, row, 8, String.valueOf(inspectSheet8.get(i).get("earlyNum")));
                        tableNormalFormat(table7, row, 9, String.valueOf(inspectSheet8.get(i).get("noOutNum")));
                    }
                }

                // 填充表格8数据
                XWPFTable table8 = document.getTables().get(7);
                if (null != inspectSheet9 && inspectSheet9.size() > 0) {
                    // 删除表格的第二行
                    table8.removeRow(1);
                    for (int i = 0; i < inspectSheet9.size(); i++) {
                        XWPFTableRow row = table8.createRow();
                        tableNormalFormat(table8, row, 0, String.valueOf(i + 1));
                        tableNormalFormat(table8, row, 1, String.valueOf(inspectSheet9.get(i).get(Constants.GRID_LEVEL_NAME)));
                        tableNormalFormat(table8, row, 2, String.valueOf(inspectSheet9.get(i).get(Constants.GRID_NAME)));
                        tableNormalFormat(table8, row, 3, String.valueOf(inspectSheet9.get(i).get(Constants.KEEPER_NAME)));
                        tableNormalFormat(table8, row, 4, String.valueOf(inspectSheet9.get(i).get(Constants.LOGIN_NAME)));
                        tableNormalFormat(table8, row, 5, null == inspectSheet9.get(i).get(Constants.ATTENDANCE_RATE) ? Constants.SEP : inspectSheet9.get(i).get(Constants.ATTENDANCE_RATE) + Constants.PER_CENT);
                        tableNormalFormat(table8, row, 6, String.valueOf(inspectSheet9.get(i).get(Constants.IN_NUM)));
                        tableNormalFormat(table8, row, 7, String.valueOf(inspectSheet9.get(i).get("lateNum")));
                        tableNormalFormat(table8, row, 8, String.valueOf(inspectSheet9.get(i).get("noInNum")));
                        tableNormalFormat(table8, row, 9, String.valueOf(inspectSheet9.get(i).get(Constants.OUT_NUM)));
                        tableNormalFormat(table8, row, 10, String.valueOf(inspectSheet9.get(i).get("earlyNum")));
                        tableNormalFormat(table8, row, 11, String.valueOf(inspectSheet9.get(i).get("noOutNum")));
                    }
                }
               // 填充表格9数据
               XWPFTable table9 = document.getTables().get(8);
               if (null != inspectSheet10 && inspectSheet10.size() > 0) {
                   // 删除表格的第二行
                   table9.removeRow(1);
                   for (int i = 0; i < inspectSheet10.size(); i++) {
                       XWPFTableRow row = table9.createRow();
                       tableNormalFormat(table9, row, 0, String.valueOf(i + 1));
                       tableNormalFormat(table9, row, 1, String.valueOf(inspectSheet10.get(i).get(Constants.GRID_NAME)));
                       tableNormalFormat(table9, row, 2, String.valueOf(inspectSheet10.get(i).get(Constants.INSPECTION_TASK_NUM)));
                       tableNormalFormat(table9, row, 3, String.valueOf(inspectSheet10.get(i).get(Constants.TASK_COMPLETED_NUM)));
                       tableNormalFormat(table9, row, 4, String.valueOf(inspectSheet10.get(i).get(Constants.TASK_INCOMPLETE_NUM)));
                   }
               }
              // 填充表格10数据
              XWPFTable table10 = document.getTables().get(9);
              if (null != inspectSheet12 && inspectSheet12.size() > 0) {
                  // 删除表格的第二行
                  table10.removeRow(1);
                  for (int i = 0; i < inspectSheet12.size(); i++) {
                      XWPFTableRow row = table10.createRow();
                      tableNormalFormat(table10, row, 0, String.valueOf(i + 1));
                      tableNormalFormat(table10, row, 1, String.valueOf(inspectSheet12.get(i).get(Constants.PARAM_LABEL)));
                      tableNormalFormat(table10, row, 2, String.valueOf(inspectSheet12.get(i).get(Constants.INSPECTION_TASK_BACK_NUM)));
                  }
              }

              // 填充表格11数据
              XWPFTable table11 = document.getTables().get(10);
              if (null != inspectSheet13 && inspectSheet13.size() > 0) {
                  // 删除表格的第二行
                  table11.removeRow(1);
                  for (int i = 0; i < inspectSheet13.size(); i++) {
                      XWPFTableRow row = table11.createRow();
                      tableNormalFormat(table11, row, 0, String.valueOf(i + 1));
                      tableNormalFormat(table11, row, 1, String.valueOf(inspectSheet13.get(i).get(Constants.ASSIGNED_DATE)));
                      tableNormalFormat(table11, row, 2, String.valueOf(inspectSheet13.get(i).get(Constants.INSPECTION_TASK_NUM)));
                      tableNormalFormat(table11, row, 3, String.valueOf(inspectSheet13.get(i).get(Constants.TASK_COMPLETED_NUM)));
                      tableNormalFormat(table11, row, 4, String.valueOf(inspectSheet13.get(i).get(Constants.TASK_INCOMPLETE_NUM)));
                  }
              }

              // 填充表格12数据
              XWPFTable table12 = document.getTables().get(11);
              if (null != inspectSheet14 && inspectSheet14.size() > 0) {
                  // 删除表格的第二行
                  table12.removeRow(1);
                  for (int i = 0; i < inspectSheet14.size(); i++) {
                      XWPFTableRow row = table12.createRow();
                      tableNormalFormat(table12, row, 0, String.valueOf(i + 1));
                      tableNormalFormat(table12, row, 1, String.valueOf(inspectSheet14.get(i).get(Constants.KEEPER_NAME)));
                      tableNormalFormat(table12, row, 2, String.valueOf(inspectSheet14.get(i).get(Constants.INSPECTION_TASK_NUM)));
                      tableNormalFormat(table12, row, 3, String.valueOf(inspectSheet14.get(i).get(Constants.TASK_COMPLETED_NUM)));
                      tableNormalFormat(table12, row, 4, String.valueOf(inspectSheet14.get(i).get(Constants.TASK_INCOMPLETE_NUM)));
                      tableNormalFormat(table12, row, 5, null == inspectSheet14.get(i).get(Constants.COMPLETED_RATE) ? Constants.SEP : inspectSheet14.get(i).get(Constants.COMPLETED_RATE) + Constants.PER_CENT);
                  }
              }

              // 填充表格13数据
              XWPFTable table13 = document.getTables().get(12);
              if (null != inspectSheet15 && inspectSheet15.size() > 0) {
                // 删除表格的第二行
                table13.removeRow(1);
                for (int i = 0; i < inspectSheet15.size(); i++) {
                    XWPFTableRow row = table13.createRow();
                    tableNormalFormat(table13, row, 0, String.valueOf(i + 1));
                    tableNormalFormat(table13, row, 1, String.valueOf(inspectSheet15.get(i).get(Constants.GRID_NAME)));
                    tableNormalFormat(table13, row, 2, null == inspectSheet15.get(i).get(Constants.TASK_COMPLETED_RATE) ? Constants.SEP : inspectSheet15.get(i).get(Constants.TASK_COMPLETED_RATE) + Constants.PER_CENT);
                }
              }

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
                param.setInspectSheet7((List<Map>) gridKeeperAttendance.getRows());
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


    /**
             * 添加单元格普通格式的文本内容  宋体12
             */
            private static void tableNormalFormat (XWPFTable table, XWPFTableRow row,int i, String value){
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


        }

