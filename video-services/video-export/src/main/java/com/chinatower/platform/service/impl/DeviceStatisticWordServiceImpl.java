package com.chinatower.platform.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.analysis.api.AnalysisExportAPI;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.api.entity.dto.PointRankDto;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.constants.Constants;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.vo.SysFileInfo;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.DeviceStatisticWordService;
import com.chinatower.platform.service.StatisticService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chinatower.platform.utils.CommonUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class DeviceStatisticWordServiceImpl implements DeviceStatisticWordService {

    @Resource
    private AnalysisExportAPI analysisExportAPI;
    @Resource
    private StatisticService statisticService;
    @Resource
    private CommonUtils fileTool;
    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    private static void wordTextReplace(XWPFDocument document,StatisticsExportParam param,AnalysisVO analysisVO){
        //1.设备统计总览概述
        DeviceStatisticsVO DeviceSheet1 = param.getDeviceSheet1();
        //2.摄像机数量区域排行
        List<Map> DeviceSheet2 = param.getDeviceSheet2();
        //3.摄像机订购算法排行
        List<Map> DeviceSheet3 = param.getDeviceSheet3();
        //4.通道算法任务数量
        List<Map<String, String>> DeviceSheet4 = param.getDeviceSheet4();
        //5.物联设备总览
        IotDeviceStatisticsVo DeviceSheet5 = param.getDeviceSheet5();
        //6.物联设备数量区域排行
        List<Map> DeviceSheet6 = param.getDeviceSheet6();
        //7.无人机设备总览
        UavDeviceStatisticsVo DeviceSheet7 = param.getDeviceSheet7();
        //8.无人机数量区域排行
        List<Map> DeviceSheet8 = param.getDeviceSheet8();
        //9.雷达设备总览
        RadarDeviceStatisticsVo DeviceSheet9 = param.getDeviceSheet9();
        //10.雷达数量区域排行
        List<Map> DeviceSheet10 = param.getDeviceSheet10();
        //11.喇叭设备总览
        HornDeviceStatisticsVo DeviceSheet11 = param.getDeviceSheet11();
        //12.喇叭数量区域排行
        List<Map> DeviceSheet12 = param.getDeviceSheet12();
        //11.执法记录仪设备总览
        DeviceStatisticsVO DeviceSheet13 = param.getDeviceSheet13();
        //12.执法记录仪数量区域排行
        List<Map> DeviceSheet14 = param.getDeviceSheet14();
        Date date = new Date();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日");
        //导出时间
        String exportTime = outputFormat.format(date);
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String tenantName = loginUser.getUser().getTenantInfo().getTenantName();
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
            if (fullText.contains("{{tenantName}}")) {
                fullText = fullText.replace("{{tenantName}}", tenantName);
            }
            if (fullText.contains("{{deviceCountTotal}}")) {
                fullText = fullText.replace("{{deviceCountTotal}}", String.valueOf(DeviceSheet1.getDeviceCountTotal()));
            }
            if (fullText.contains("{{devCountOnline}}")) {
                fullText = fullText.replace("{{devCountOnline}}", String.valueOf(DeviceSheet1.getDevCountOnline()));
            }
            if (fullText.contains("{{devCountOffline}}")) {
                fullText = fullText.replace("{{devCountOffline}}", String.valueOf(DeviceSheet1.getDevCountOffline()));
            }
            if (fullText.contains("{{onlinePercentageDev}}")) {
                fullText = fullText.replace("{{onlinePercentageDev}}",DeviceSheet1.getDeviceCountTotal()<=0?"-":Math.round(Double.parseDouble(String.valueOf(DeviceSheet1.getOnlinePercentageDev())) * 100) + "%");
            }
            if (fullText.contains("{{channelCountTotal}}")) {
                fullText = fullText.replace("{{channelCountTotal}}", String.valueOf(DeviceSheet1.getChannelCountTotal()));
            }
            if (fullText.contains("{{channelCountOnline}}")) {
                fullText = fullText.replace("{{channelCountOnline}}", String.valueOf(DeviceSheet1.getChannelCountOnline()));
            }
            if (fullText.contains("{{channelCountOffline}}")) {
                fullText = fullText.replace("{{channelCountOffline}}", String.valueOf(DeviceSheet1.getChannelCountOffline()));
            }
            if (fullText.contains("{{onlinePercentage}}")) {
                fullText = fullText.replace("{{onlinePercentage}}",DeviceSheet1.getChannelCountTotal()<=0?"-":Math.round(Double.parseDouble(String.valueOf(DeviceSheet1.getOnlinePercentage())) * 100) + "%");
            }
            if (fullText.contains("{{disAlgorithmTaskNum}}")) {
                fullText = fullText.replace("{{disAlgorithmTaskNum}}", String.valueOf(DeviceSheet1.getDisAlgorithmTaskNum()));
            }
            if (fullText.contains("{{upAlgorithmTaskNum}}")) {
                fullText = fullText.replace("{{upAlgorithmTaskNum}}", String.valueOf(DeviceSheet1.getUpAlgorithmTaskNum()));
            }
            //物联
            if (fullText.contains("{{iotDeviceCount}}")) {
                fullText = fullText.replace("{{iotDeviceCount}}", String.valueOf(DeviceSheet5.getIotDeviceCount()));
            }
            if (fullText.contains("{{iotDeviceOnlineCount}}")) {
                fullText = fullText.replace("{{iotDeviceOnlineCount}}", String.valueOf(DeviceSheet5.getIotDeviceOnlineCount()));
            }
            if (fullText.contains("{{iotDeviceOfflineCount}}")) {
                fullText = fullText.replace("{{iotDeviceOfflineCount}}", String.valueOf(DeviceSheet5.getIotDeviceOfflineCount()));
            }
            if (fullText.contains("{{iotDeviceOnlinePercent}}")) {
                fullText = fullText.replace("{{iotDeviceOnlinePercent}}",DeviceSheet5.getIotDeviceCount()<=0?"-":Math.round(Double.parseDouble(String.valueOf(DeviceSheet5.getIotDeviceOnlinePercent())) * 100) + "%");
            }
            //无人机
            if (fullText.contains("{{uavDeviceCount}}")) {
                fullText = fullText.replace("{{uavDeviceCount}}", String.valueOf(DeviceSheet7.getUavDeviceCount()));
            }
            if (fullText.contains("{{uavDeviceOnlineCount}}")) {
                fullText = fullText.replace("{{uavDeviceOnlineCount}}", String.valueOf(DeviceSheet7.getUavDeviceOnlineCount()));
            }
            if (fullText.contains("{{uavDeviceOfflineCount}}")) {
                fullText = fullText.replace("{{uavDeviceOfflineCount}}", String.valueOf(DeviceSheet7.getUavDeviceOfflineCount()));
            }
            if (fullText.contains("{{uavDeviceOnlinePercent}}")) {
                fullText = fullText.replace("{{uavDeviceOnlinePercent}}",DeviceSheet7.getUavDeviceCount()<=0?"-":Math.round(Double.parseDouble(String.valueOf(DeviceSheet7.getUavDeviceOnlinePercent())) * 100) + "%");
            }
            if (fullText.contains("{{uavNestDeviceCount}}")) {
                fullText = fullText.replace("{{uavNestDeviceCount}}", String.valueOf(DeviceSheet7.getUavNestDeviceCount()));
            }
            if (fullText.contains("{{uavNestDeviceOnlineCount}}")) {
                fullText = fullText.replace("{{uavNestDeviceOnlineCount}}", String.valueOf(DeviceSheet7.getUavNestDeviceOnlineCount()));
            }
            if (fullText.contains("{{uavNestDeviceOfflineCount}}")) {
                fullText = fullText.replace("{{uavNestDeviceOfflineCount}}", String.valueOf(DeviceSheet7.getUavNestDeviceOfflineCount()));
            }
            if (fullText.contains("{{uavNestDeviceOnlinePercent}}")) {
                fullText = fullText.replace("{{uavNestDeviceOnlinePercent}}",DeviceSheet7.getUavNestDeviceCount()<=0?"-":Math.round(Double.parseDouble(String.valueOf(DeviceSheet7.getUavNestDeviceOnlinePercent())) * 100) + "%");
            }
            //雷达
            if (fullText.contains("{{radarDeviceCount}}")) {
                fullText = fullText.replace("{{radarDeviceCount}}", String.valueOf(DeviceSheet9.getRadarDeviceCount()));
            }
            if (fullText.contains("{{radarDeviceOnlineCount}}")) {
                fullText = fullText.replace("{{radarDeviceOnlineCount}}", String.valueOf(DeviceSheet9.getRadarDeviceOnlineCount()));
            }
            if (fullText.contains("{{radarDeviceOfflineCount}}")) {
                fullText = fullText.replace("{{radarDeviceOfflineCount}}", String.valueOf(DeviceSheet9.getRadarDeviceOfflineCount()));
            }
            if (fullText.contains("{{radarDeviceOnlinePercent}}")) {
                fullText = fullText.replace("{{radarDeviceOnlinePercent}}",DeviceSheet9.getRadarDeviceCount()<=0?"-":Math.round(Double.parseDouble(String.valueOf(DeviceSheet9.getRadarDeviceOnlinePercent())) * 100) + "%");
            }
            //喇叭
            if (fullText.contains("{{hornDeviceCount}}")) {
                fullText = fullText.replace("{{hornDeviceCount}}", String.valueOf(DeviceSheet11.getHornDeviceCount()));
            }
            if (fullText.contains("{{hornDeviceWorkCount}}")) {
                fullText = fullText.replace("{{hornDeviceWorkCount}}", String.valueOf(DeviceSheet11.getHornDeviceWorkCount()));
            }
            if (fullText.contains("{{hornDeviceOfflineCount}}")) {
                fullText = fullText.replace("{{hornDeviceOfflineCount}}", String.valueOf(DeviceSheet11.getHornDeviceOfflineCount()));
            }
            if (fullText.contains("{{hornDeviceFreeCount}}")) {
                fullText = fullText.replace("{{hornDeviceFreeCount}}", String.valueOf(DeviceSheet11.getHornDeviceFreeCount()));
            }
            if (fullText.contains("{{hornDeviceFailCount}}")) {
                fullText = fullText.replace("{{hornDeviceFailCount}}", String.valueOf(DeviceSheet11.getHornDeviceFailCount()));
            }
            if (fullText.contains("{{hornDeviceNoConnectCount}}")) {
                fullText = fullText.replace("{{hornDeviceNoConnectCount}}", String.valueOf(DeviceSheet11.getHornDeviceNoConnectCount()));
            }
            if (fullText.contains("{{hornDeviceOnlinePercent}}")) {
                fullText = fullText.replace("{{hornDeviceOnlinePercent}}",DeviceSheet11.getHornDeviceCount()<=0?"-":Math.round(Double.parseDouble(String.valueOf(DeviceSheet11.getHornDeviceOnlinePercent())) * 100) + "%");
            }
            //执法记录仪
            if (fullText.contains("{{recorderDeviceCount}}")) {
                fullText = fullText.replace("{{recorderDeviceCount}}", String.valueOf(DeviceSheet13.getDeviceCountTotal()));
            }
            if (fullText.contains("{{recorderDevCountOnline}}")) {
                fullText = fullText.replace("{{recorderDevCountOnline}}", String.valueOf(DeviceSheet13.getDevCountOnline()));
            }
            if (fullText.contains("{{recorderDevCountOffline}}")) {
                fullText = fullText.replace("{{recorderDevCountOffline}}", String.valueOf(DeviceSheet13.getDevCountOffline()));
            }
            if (fullText.contains("{{recorderDeviceNoConnectCount}}")) {
                fullText = fullText.replace("{{recorderDeviceNoConnectCount}}", String.valueOf(DeviceSheet13.getDeviceNoConnectCount()));
            }
            if (fullText.contains("{{recorderOnlinePercentageDev}}")) {
                fullText = fullText.replace("{{recorderOnlinePercentageDev}}",DeviceSheet13.getDeviceCountTotal()<=0?"-":Math.round(Double.parseDouble(String.valueOf(DeviceSheet13.getOnlinePercentageDev())) * 100) + "%");
            }
            if (fullText.contains("{{recorderChannelCountTotal}}")) {
                fullText = fullText.replace("{{recorderChannelCountTotal}}", String.valueOf(DeviceSheet13.getChannelCountTotal()));
            }
            if (fullText.contains("{{recorderChannelCountOnline}}")) {
                fullText = fullText.replace("{{recorderChannelCountOnline}}", String.valueOf(DeviceSheet13.getChannelCountOnline()));
            }
            if (fullText.contains("{{recorderChannelCountOffline}}")) {
                fullText = fullText.replace("{{recorderChannelCountOffline}}", String.valueOf(DeviceSheet13.getChannelCountOffline()));
            }
            if (fullText.contains("{{recorderOnlinePercentage}}")) {
                fullText = fullText.replace("{{recorderOnlinePercentage}}",DeviceSheet13.getChannelCountTotal()<=0?"-":Math.round(Double.parseDouble(String.valueOf(DeviceSheet13.getOnlinePercentage())) * 100) + "%");
            }
            //一、摄像机数量区域统计
            if (fullText.contains("{{table1}}")) {
                tableTitle = fullText.replace("{{table1}}", "表1 摄像机数量区域排行");
                fullText ="";
            }
            if (fullText.contains("{{regionName}}")) {
                fullText = fullText.replace("{{regionName}}",DeviceSheet2!= null && DeviceSheet2.size()>0?String.valueOf(DeviceSheet2.get(0).get(Constants.REGION_NAME)):"-");
            }
            //二、摄像机订购算法排行
            Integer sum2 = DeviceSheet3.stream().mapToInt((x) -> Integer.parseInt(String.valueOf(x.get("algorithmNum")))).sum();
            if (fullText.contains("{{table2}}")) {
                tableTitle = fullText.replace("{{table2}}", "表2 摄像机订购算法排行");
                fullText ="";
            }
            if (fullText.contains("{{algorithmNum2}}")) {
                fullText = fullText.replace("{{algorithmNum2}}",DeviceSheet3!= null && DeviceSheet3.size()>0? String.valueOf(sum2):"-");
            }
            //三、通道算法任务数量
                Integer foreTaskNum = 0;
                Integer afterTaskNum = 0;
                Integer strForeTaskNum = 0;
                Integer strAfterTaskNum = 0;
                Integer taskNum = 0 ;
                Integer strTaskNum = 0;
            if (DeviceSheet4!= null && DeviceSheet4.size()>0){
                //前端任务
                foreTaskNum = DeviceSheet4.stream().mapToInt((x) -> Integer.parseInt(String.valueOf(x.get("foreAlgorithmTaskNum")))).sum();
                //后端任务
                afterTaskNum = DeviceSheet4.stream().mapToInt((x) -> Integer.parseInt(String.valueOf(x.get("afterAlgorithmTaskNum")))).sum();
                //前端启用任务
                strForeTaskNum = DeviceSheet4.stream().mapToInt((x) -> Integer.parseInt(String.valueOf(x.get("startForeAlgorithmTaskNum")))).sum();
                //后端启用任务
                strAfterTaskNum = DeviceSheet4.stream().mapToInt((x) -> Integer.parseInt(String.valueOf(x.get("startAfterAlgorithmTaskNum")))).sum();
                //前后端任务
                taskNum = foreTaskNum+afterTaskNum;
                //前后端启用任务
                strTaskNum = strForeTaskNum+strAfterTaskNum;
            }
            if (fullText.contains("{{table3}}")) {
                tableTitle = fullText.replace("{{table3}}", "表3 通道算法任务数量");
                fullText ="";
            }
            if (fullText.contains("{{algorithmTaskNum3}}")) {
                fullText = fullText.replace("{{algorithmTaskNum3}}",DeviceSheet3!= null && DeviceSheet3.size()>0? String.valueOf(taskNum):"-");
            }
            if (fullText.contains("{{foreAlgorithmTaskNum3}}")) {
                fullText = fullText.replace("{{foreAlgorithmTaskNum3}}",DeviceSheet3!= null && DeviceSheet3.size()>0? String.valueOf(foreTaskNum):"-");
            }
            if (fullText.contains("{{afterAlgorithmTaskNum3}}")) {
                fullText = fullText.replace("{{afterAlgorithmTaskNum3}}",DeviceSheet3!= null && DeviceSheet3.size()>0? String.valueOf(afterTaskNum):"-");
            }
            if (fullText.contains("{{startAlgorithmTaskNum3}}")) {
                fullText = fullText.replace("{{startAlgorithmTaskNum3}}",DeviceSheet3!= null && DeviceSheet3.size()>0? String.valueOf(strTaskNum):"-");
            }
            if (fullText.contains("{{startForeAlgorithmTaskNum3}}")) {
                fullText = fullText.replace("{{startForeAlgorithmTaskNum3}}",DeviceSheet3!= null && DeviceSheet3.size()>0? String.valueOf(strForeTaskNum):"-");
            }
            if (fullText.contains("{{startAfterAlgorithmTaskNum3}}")) {
                fullText = fullText.replace("{{startAfterAlgorithmTaskNum3}}",DeviceSheet3!= null && DeviceSheet3.size()>0? String.valueOf(strAfterTaskNum):"-");
            }
            //三、物联数量区域统计
            if (fullText.contains("{{table4}}")) {
                tableTitle = fullText.replace("{{table4}}", "表4 物联设备数量区域排行");
                fullText ="";
            }
            if (fullText.contains("{{regionName4}}")) {
                fullText = fullText.replace("{{regionName4}}",DeviceSheet6!= null && DeviceSheet6.size()>0?String.valueOf(DeviceSheet6.get(0).get(Constants.REGION_NAME)):"-");
            }
            //四、无人机数量区域统计
            if (fullText.contains("{{table5}}")) {
                tableTitle = fullText.replace("{{table5}}", "表5  无人机数量区域排行");
                fullText ="";
            }
            if (fullText.contains("{{regionName5}}")) {
                fullText = fullText.replace("{{regionName5}}",DeviceSheet8!= null && DeviceSheet8.size()>0?String.valueOf(DeviceSheet8.get(0).get(Constants.REGION_NAME)):"-");
            }
            //五、雷达数量区域统计
            if (fullText.contains("{{table6}}")) {
                tableTitle = fullText.replace("{{table6}}", "表6  雷达数量区域排行");
                fullText ="";
            }
            if (fullText.contains("{{regionName6}}")) {
                fullText = fullText.replace("{{regionName6}}",DeviceSheet10!= null && DeviceSheet10.size()>0?String.valueOf(DeviceSheet10.get(0).get(Constants.REGION_NAME)):"-");
            }
            //六、喇叭数量区域统计
            if (fullText.contains("{{table7}}")) {
                tableTitle = fullText.replace("{{table7}}", "表7  喇叭数量区域排行");
                fullText ="";
            }
            if (fullText.contains("{{regionName7}}")) {
                fullText = fullText.replace("{{regionName7}}",DeviceSheet12!= null && DeviceSheet12.size()>0?String.valueOf(DeviceSheet12.get(0).get(Constants.REGION_NAME)):"-");
            }
            //七、执法记录仪数量区域统计
            if (fullText.contains("{{table8}}")) {
                tableTitle = fullText.replace("{{table8}}", "表8执法记录仪数量区域排行");
                fullText ="";
            }
            if (fullText.contains("{{regionName8}}")) {
                fullText = fullText.replace("{{regionName8}}",DeviceSheet14!= null && DeviceSheet14.size()>0?String.valueOf(DeviceSheet14.get(0).get(Constants.REGION_NAME)):"-");
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
        if(null != DeviceSheet2  && DeviceSheet2.size()>0){
            // 删除表格的第二行
            table.removeRow(1);
            for (int i = 0; i < DeviceSheet2.size(); i++) {
                XWPFTableRow row = table.createRow();
                tableNormalFormat(table,row,0, String.valueOf(i+1));
                tableNormalFormat(table,row,1, String.valueOf(DeviceSheet2.get(i).get(Constants.REGION_NAME)));
                tableNormalFormat(table,row,2,String.valueOf(DeviceSheet2.get(i).get(Constants.DEVICE_NUM)));
        }
    }

        // 填充表格2数据
        XWPFTable table2 = document.getTables().get(1);
        if(null != DeviceSheet3  && DeviceSheet3.size()>0){
            // 删除表格的第二行
            table2.removeRow(1);
            for (int i = 0; i < DeviceSheet3.size(); i++) {
                XWPFTableRow row = table2.createRow();
                tableNormalFormat(table2,row,0, String.valueOf(i+1));
                tableNormalFormat(table2,row,1, String.valueOf(DeviceSheet3.get(i).get("deviceCode")));
                tableNormalFormat(table2,row,2,String.valueOf(DeviceSheet3.get(i).get("deviceName")));
                tableNormalFormat(table2,row,3,String.valueOf(DeviceSheet3.get(i).get("algorithmNum")));
            }
        }

        // 填充表格3数据
        XWPFTable table3 = document.getTables().get(2);
        if(null != DeviceSheet4  && DeviceSheet4.size()>0){
            // 删除表格的第二行
            table3.removeRow(1);
            for (int i = 0; i < DeviceSheet4.size(); i++) {
                XWPFTableRow row = table3.createRow();
                tableNormalFormat(table3,row,0, String.valueOf(i+1));
                tableNormalFormat(table3,row,1, String.valueOf(DeviceSheet4.get(i).get("deviceName")));
                tableNormalFormat(table3,row,2,String.valueOf(DeviceSheet4.get(i).get("channelName")));
                tableNormalFormat(table3,row,3,String.valueOf(DeviceSheet4.get(i).get("taskNum")));
                tableNormalFormat(table3,row,4,String.valueOf(DeviceSheet4.get(i).get("foreAlgorithmTaskNum")));
                tableNormalFormat(table3,row,5,String.valueOf(DeviceSheet4.get(i).get("afterAlgorithmTaskNum")));
                tableNormalFormat(table3,row,6,String.valueOf(DeviceSheet4.get(i).get("strTaskNum")));
                tableNormalFormat(table3,row,7,String.valueOf(DeviceSheet4.get(i).get("startForeAlgorithmTaskNum")));
                tableNormalFormat(table3,row,8,String.valueOf(DeviceSheet4.get(i).get("startAfterAlgorithmTaskNum")));
            }
        }

        // 填充表格4数据
        XWPFTable table4 = document.getTables().get(3);
        if(null != DeviceSheet6  && DeviceSheet6.size()>0){
            // 删除表格的第二行
            table4.removeRow(1);
            for (int i = 0; i < DeviceSheet6.size(); i++) {
                XWPFTableRow row = table4.createRow();
                tableNormalFormat(table4,row,0, String.valueOf(i+1));
                tableNormalFormat(table4,row,1, String.valueOf(DeviceSheet6.get(i).get(Constants.REGION_NAME)));
                tableNormalFormat(table4,row,2,String.valueOf(DeviceSheet6.get(i).get(Constants.DEVICE_NUM)));
            }
        }

        // 填充表格5数据
        XWPFTable table5 = document.getTables().get(4);
        if(null != DeviceSheet8  && DeviceSheet8.size()>0){
            // 删除表格的第二行
            table5.removeRow(1);
            for (int i = 0; i < DeviceSheet8.size(); i++) {
                XWPFTableRow row = table5.createRow();
                tableNormalFormat(table5,row,0, String.valueOf(i+1));
                tableNormalFormat(table5,row,1, String.valueOf(DeviceSheet8.get(i).get(Constants.REGION_NAME)));
                tableNormalFormat(table5,row,2,String.valueOf(DeviceSheet8.get(i).get(Constants.DEVICE_NUM)));
            }
        }

        // 填充表格6数据
        XWPFTable table6 = document.getTables().get(5);
        if(null != DeviceSheet10  && DeviceSheet10.size()>0){
            // 删除表格的第二行
            table6.removeRow(1);
            for (int i = 0; i < DeviceSheet10.size(); i++) {
                XWPFTableRow row = table6.createRow();
                tableNormalFormat(table6,row,0, String.valueOf(i+1));
                tableNormalFormat(table6,row,1, String.valueOf(DeviceSheet10.get(i).get(Constants.REGION_NAME)));
                tableNormalFormat(table6,row,2,String.valueOf(DeviceSheet10.get(i).get(Constants.DEVICE_NUM)));
            }
        }

        // 填充表格7数据
        XWPFTable table7 = document.getTables().get(6);
        if(null != DeviceSheet12  && DeviceSheet12.size()>0){
            // 删除表格的第二行
            table7.removeRow(1);
            for (int i = 0; i < DeviceSheet12.size(); i++) {
                XWPFTableRow row = table7.createRow();
                tableNormalFormat(table7,row,0, String.valueOf(i+1));
                tableNormalFormat(table7,row,1, String.valueOf(DeviceSheet12.get(i).get(Constants.REGION_NAME)));
                tableNormalFormat(table7,row,2,String.valueOf(DeviceSheet12.get(i).get(Constants.DEVICE_NUM)));
            }
        }

        // 填充表格8数据
        XWPFTable table8 = document.getTables().get(7);
        if(null != DeviceSheet14  && DeviceSheet14.size()>0){
            // 删除表格的第二行
            table8.removeRow(1);
            for (int i = 0; i < DeviceSheet14.size(); i++) {
                XWPFTableRow row = table8.createRow();
                tableNormalFormat(table7,row,0, String.valueOf(i+1));
                tableNormalFormat(table7,row,1, String.valueOf(DeviceSheet14.get(i).get(Constants.REGION_NAME)));
                tableNormalFormat(table7,row,2,String.valueOf(DeviceSheet14.get(i).get(Constants.DEVICE_NUM)));
            }
        }

    }

    @Override
    public void exportDeviceWordStatistics(AnalysisVO analysisVO, HttpServletResponse response) {
        analysisVO.setPageNum(null);
        analysisVO.setPageSize(null);
        ByteArrayOutputStream bos = null;
        PointRankVo pointRankVo = new PointRankVo();
        BeanUtils.copyProperties(analysisVO, pointRankVo);
        StatisticsExportParam param = new StatisticsExportParam();
        // 读入Word模板
        // 1. 获取模版
        SysConfig sysConfig = fileTool.selectConfigByKey("tenant_device_template");
        if(ObjectUtil.isNull(sysConfig)){
            throw new CustomException("未找到该系统参数配置：tenant_device_template");
        }
        String fileUrl = fileUrlEncryptUtil.dealObsFileUrl(sysConfig.getConfigValue());
        InputStream inputStream =null;
        XWPFDocument document = null;
        try {
            URL url = new URL(fileUrl);
            inputStream = url.openStream();
            document = new XWPFDocument(inputStream);
            //异步获取数据
            List<Integer> paramList = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14);
            ThreadParallelUtil.run(paramList,o->{
                this.deviceMethodAll(pointRankVo,analysisVO,o,param);
                return null;
            }, ThreadParallelUtil.ConfigFactory.DEVICE_EXPORT_CODE);
            //处理数据
            this.wordTextReplace(document,param,analysisVO);
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
     * 添加单元格普通格式的文本内容  宋体10
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

    private void deviceMethodAll(PointRankVo pointRankVo,AnalysisVO analysisVO,Integer num,StatisticsExportParam param){
        DeviceStatisticsVO map = new DeviceStatisticsVO();
        List<PointRankDto> maps = new ArrayList<>();
        List<Map> listMap = new ArrayList<>();
        List<Map<String, String>> listMaps = new ArrayList<Map<String, String>>();
        IotDeviceStatisticsVo iotMap = new IotDeviceStatisticsVo();
        UavDeviceStatisticsVo uavMap = new UavDeviceStatisticsVo();
        RadarDeviceStatisticsVo radarMap = new RadarDeviceStatisticsVo();
        HornDeviceStatisticsVo hornMap = new HornDeviceStatisticsVo();
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
                TableDataInfo orderAlgorithmRank = analysisExportAPI.getOrderAlgorithmRank(analysisVO);
                listMap = (List<Map>) orderAlgorithmRank.getRows();
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


}
