package com.chinatower.platform.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.analysis.api.AnalysisExportAPI;
import com.chinatower.analysis.api.entity.CustomVO;
import com.chinatower.analysis.api.entity.StatisticsExportParam;
import com.chinatower.common.core.utils.StringUtils;
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
import com.chinatower.platform.service.ExportStatisticsService;
import com.chinatower.platform.service.StatisticService;
import com.chinatower.platform.service.UserStatisticWordService;
import com.chinatower.platform.utils.CommonUtils;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserStatisticWordServiceImpl implements UserStatisticWordService {

    @Resource
    private AnalysisExportAPI analysisExportAPI;

    @Resource
    private StatisticService statisticService;

    @Resource
    private CommonUtils fileTool;
    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Resource
    private ExportStatisticsService exportStatisticsService;

    @Override
    public void exportUserWordStatistics(CustomVO customVO, HttpServletResponse response) {
        customVO.setPageNum(null);
        customVO.setPageNum(null);
        StatisticsExportParam param = new StatisticsExportParam();
        ByteArrayOutputStream bos = null;
        // 读入Word模板
        // 1. 获取模板
        SysConfig sysConfig = fileTool.selectConfigByKey("tenant_user_template");
        if(ObjectUtil.isNull(sysConfig)){
            throw new CustomException("未找到该系统参数配置：tenant_user_template");
        }
        String fileUrl = fileUrlEncryptUtil.dealObsFileUrl(sysConfig.getConfigValue());
        InputStream inputStream =null;
        XWPFDocument document = null;
        try {
            URL url = new URL(fileUrl);
            inputStream = url.openStream();
            document = new XWPFDocument(inputStream);
            List<Integer> paramList = Arrays.asList(1,2,3,33,4,5,6,7,8,9,10);
            ThreadParallelUtil.run(paramList, o->{
                this.userMethodAll(customVO,o,param);
                return null;
            }, ThreadParallelUtil.ConfigFactory.USER_EXPORT_CODE);
            this.wordTextReplace(document,param,customVO);
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
     * 填充文档内容
     * @param document
     * @param param
     * @param customVO
     */
    private void wordTextReplace(XWPFDocument document, StatisticsExportParam param, CustomVO customVO) {
        //1、用户统计总览
        Map userSheet1 = param.getUserSheet1();
        //2、用户登录次数按时间统计
        List<Map> userSheet2 = param.getUserSheet2();
        //3、用户登录次数排行
        List<Map> userSheet3 = param.getUserSheet3();
        //4、用户登录24小时统计
        List<Map> userSheet4 = param.getUserSheet4();
        //5、PC操作系统统计
        List<Map> userSheet5 = param.getUserSheet5();
        //6、pc浏览器统计
        List<Map> userSheet6 = param.getUserSheet6();
        //7、app操作系统统计
        List<Map> userSheet7 = param.getUserSheet7();
        //8、app手机型号统计
        List<Map> userSheet8 = param.getUserSheet8();
        //9、app版本统计
        List<Map> userSheet9 = param.getUserSheet9();
        //10、用户数量按组织排行
        List<Map> userSheet10 = param.getUserSheet10();
        //11、用户登录时长排行 PC
        List<Map> userSheet11 = param.getUserSheet11();
        //12、用户登录时长排行 APP
        List<Map> userSheet12 = param.getUserSheet12();
        //13、用户登录时长排行 小程序
        List<Map> userSheet13 = param.getUserSheet13();
        //拼接 时间范围
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日");
        //导出时间
        String exportTime = outputFormat.format(new Date());
        String startTime = outputFormat.format(DateUtils.fomatDate(customVO.getQueryStartTime()));
        String endTime = outputFormat.format(DateUtils.fomatDate(customVO.getQueryEndTime()));
        String time = startTime.equals(endTime) ? startTime : startTime+"至"+endTime;
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
            String fullText = sb.toString(); //获取文档内容
            String tableText = ""; //获取每个表格上方表的描述
            //1、先对文档中的表格名进行统一替换，以便对其设置样式
            if (fullText.contains("{{table1}}")) {
                tableText = fullText.replace("{{table1}}",  Constants.USER_TABLE1);
                fullText = "";
            }
            if (fullText.contains("{{table2}}")) {
                tableText = fullText.replace("{{table2}}", Constants.USER_TABLE2);
                fullText = "";
            }
            if (fullText.contains("{{table3}}")) {
                tableText = fullText.replace("{{table3}}", Constants.USER_TABLE3);
                fullText = "";
            }
            if (fullText.contains("{{table4}}")) {
                tableText = fullText.replace("{{table4}}", Constants.USER_TABLE4);
                fullText = "";
            }
            if (fullText.contains("{{table5}}")) {
                tableText = fullText.replace("{{table5}}", Constants.USER_TABLE5);
                fullText = "";
            }
            if (fullText.contains("{{table6}}")) {
                tableText = fullText.replace("{{table6}}", Constants.USER_TABLE6);
                fullText = "";
            }
            if (fullText.contains("{{table7}}")) {
                tableText = fullText.replace("{{table7}}", Constants.USER_TABLE7);
                fullText = "";
            }
            if (fullText.contains("{{table8}}")) {
                tableText = fullText.replace("{{table8}}", Constants.USER_TABLE8);
                fullText = "";
            }
            if (fullText.contains("{{table9}}")) {
                tableText = fullText.replace("{{table9}}", Constants.USER_TABLE9);
                fullText = "";
            }
            if (fullText.contains("{{table10}}")) {
                tableText = fullText.replace("{{table10}}", Constants.USER_TABLE10);
                fullText = "";
            }
            if (fullText.contains("{{table11}}")) {
                tableText = fullText.replace("{{table11}}", Constants.USER_TABLE11);
                fullText = "";
            }
            if (fullText.contains("{{table12}}")) {
                tableText = fullText.replace("{{table12}}", Constants.USER_TABLE12);
                fullText = "";
            }
            //如果文本块包含要替换的文本,进行文本替换
            //用户统计报告 --开头
            if (fullText.contains("{{exportTime}}")) {
                fullText = fullText.replace("{{exportTime}}", exportTime);
            }
            //用户统计报告 --第一段 总览部分
            if (fullText.contains("{{exportEndTime}}")) {
                fullText = fullText.replace("{{exportEndTime}}", exportTime);
            }
            if (fullText.contains("{{tenantName}}")) {
                fullText = fullText.replace("{{tenantName}}", String.valueOf(userSheet1.get("tenantName")));
            }
            if (fullText.contains("{{userTotalNum}}")) {
                fullText = fullText.replace("{{userTotalNum}}", String.valueOf(userSheet1.get("userTotalNum")));
            }
            if (fullText.contains("{{startUserNum}}")) {
                fullText = fullText.replace("{{startUserNum}}", String.valueOf(userSheet1.get("startUserNum")));
            }
            if (fullText.contains("{{stopUserNum}}")) {
                fullText = fullText.replace("{{stopUserNum}}", String.valueOf(userSheet1.get("stopUserNum")));
            }
            if (fullText.contains("{{deptNum}}")) {
                fullText = fullText.replace("{{deptNum}}", String.valueOf(userSheet1.get("deptNum")));
            }

            //用户统计报告 --第二段 用户登录按时间统计
            if (fullText.contains("{{startEndTime}}")) {
                fullText = fullText.replace("{{startEndTime}}", time);
            }
            if (userSheet2 != null && userSheet2.size() > 0) {
                Integer loginTotalCount = 0;
                Integer webLoginCount = 0;
                Integer appLoginCount = 0;
                Integer wxLoginCount = 0;
                for (int i = 0; i< userSheet2.size(); i++) {
                    loginTotalCount += Integer.parseInt(userSheet2.get(i).get(Constants.LOGIN_TOTAL_COUNT).toString());
                    Object loginNumObj = userSheet2.get(i).get("loginNum");
                    if (loginNumObj instanceof List) {
                        for (Map map1 : (List<Map>)loginNumObj) {
                            String loginType = (String) map1.get(Constants.LOGIN_TYPE_NAME);
                            Integer loginCount = Integer.parseInt(map1.get(Constants.LOGIN_COUNT).toString());
                            switch (loginType) {
                                case "web_manage":
                                    webLoginCount += loginCount;
                                    break;
                                case "app_manage":
                                    appLoginCount += loginCount;
                                    break;
                                case "wx_manage":
                                    wxLoginCount += loginCount;
                                    break;
                                default: break;
                            }
                        }
                    }
                }
                if (fullText.contains("{{loginTotalCount}}")) {
                    fullText = fullText.replace("{{loginTotalCount}}", userSheet2!= null && userSheet2.size()>0 && loginTotalCount != 0 ?String.valueOf(loginTotalCount) : Constants.ZERO);
                }
                if (fullText.contains("{{appLoginCount}}")) {
                    fullText = fullText.replace("{{appLoginCount}}", userSheet2!= null && userSheet2.size()>0 && appLoginCount != 0 ? String.valueOf(appLoginCount) : Constants.ZERO);
                }
                if (fullText.contains("{{wxLoginCount}}")) {
                    fullText = fullText.replace("{{wxLoginCount}}", userSheet2!= null && userSheet2.size()>0 && wxLoginCount != 0 ? String.valueOf(wxLoginCount) : Constants.ZERO);
                }
                if (fullText.contains("{{webLoginCount}}")) {
                    fullText = fullText.replace("{{webLoginCount}}", userSheet2!= null && userSheet2.size()>0 && webLoginCount != 0 ? String.valueOf(webLoginCount) : Constants.ZERO);
                }
            }
            //用户统计报告 --第三段 用户登录统计
            if (fullText.contains("{{loginMostUserName}}")) {
                fullText = fullText.replace("{{loginMostUserName}}", userSheet3!= null && userSheet3.size()>0 && !StringUtils.isEmpty(userSheet3.get(0).get(Constants.LOGIN_NAME).toString())?String.valueOf(userSheet3.get(0).get(Constants.LOGIN_NAME)):Constants.SEP);
            }
            if (fullText.contains("{{maxLoginCount}}")) {
                fullText = fullText.replace("{{maxLoginCount}}", userSheet3!= null && userSheet3.size()>0 && !StringUtils.isEmpty(userSheet3.get(0).get(Constants.LOGIN_COUNT).toString())?String.valueOf(userSheet3.get(0).get(Constants.LOGIN_COUNT)):Constants.SEP);
            }
            //用户统计报告 --第四段 用户登录时长统计
            if (fullText.contains("{{loginMaxUserNamePc}}")) {
                fullText = fullText.replace("{{loginMaxUserNamePc}}", userSheet11!= null && userSheet11.size()>0 && !StringUtils.isEmpty(userSheet11.get(0).get(Constants.LOGIN_NAME).toString())?String.valueOf(userSheet11.get(0).get(Constants.LOGIN_NAME)):Constants.SEP);
            }
            if (fullText.contains("{{loginMaxTimePc}}")) {
                fullText = fullText.replace("{{loginMaxTimePc}}", userSheet11!= null && userSheet11.size()>0 && !StringUtils.isEmpty(userSheet11.get(0).get(Constants.LOGIN_DURATION).toString())?String.valueOf(userSheet11.get(0).get(Constants.LOGIN_DURATION)):Constants.SEP);
            }
            if (fullText.contains("{{loginMaxUserNameApp}}")) {
                fullText = fullText.replace("{{loginMaxUserNameApp}}", userSheet12!= null && userSheet12.size()>0 && !StringUtils.isEmpty(userSheet12.get(0).get(Constants.LOGIN_NAME).toString())?String.valueOf(userSheet12.get(0).get(Constants.LOGIN_NAME)):Constants.SEP);
            }
            if (fullText.contains("{{loginMaxTimeApp}}")) {
                fullText = fullText.replace("{{loginMaxTimeApp}}", userSheet12!= null && userSheet12.size()>0 && !StringUtils.isEmpty(userSheet12.get(0).get(Constants.LOGIN_DURATION).toString())?String.valueOf(userSheet12.get(0).get(Constants.LOGIN_DURATION)):Constants.SEP);
            }
            if (fullText.contains("{{loginMaxUserNameWx}}")) {
                fullText = fullText.replace("{{loginMaxUserNameWx}}", userSheet13!= null && userSheet13.size()>0 && !StringUtils.isEmpty(userSheet13.get(0).get(Constants.LOGIN_NAME).toString())?String.valueOf(userSheet13.get(0).get(Constants.LOGIN_NAME)):Constants.SEP);
            }
            if (fullText.contains("{{loginMaxTimeWx}}")) {
                fullText = fullText.replace("{{loginMaxTimeWx}}", userSheet13!= null && userSheet13.size()>0 && !StringUtils.isEmpty(userSheet13.get(0).get(Constants.LOGIN_DURATION).toString())?String.valueOf(userSheet13.get(0).get(Constants.LOGIN_DURATION)):Constants.SEP);
            }
            //用户统计报告 --第四段 用户登录24小时统计
            if (userSheet4!= null && userSheet4.size()>0) {
                List<Map> userTTSheet6Map = userSheet4.stream()
                        .sorted(Comparator.comparing((Map m) -> Integer.parseInt(String.valueOf(m.get(Constants.LOGIN_COUNT)))).reversed()
                                .thenComparing((Map m) -> String.valueOf(m.get(Constants.LOGIN_TIME_SLOT)))
                        ).collect(Collectors.toList());
                if (fullText.contains("{{loginTimeSlot}}")) {
                    fullText = fullText.replace("{{loginTimeSlot}}", userTTSheet6Map!= null && userTTSheet6Map.size()>0 && !StringUtils.isEmpty(userTTSheet6Map.get(0).get(Constants.LOGIN_TIME_SLOT).toString())?String.valueOf(userTTSheet6Map.get(0).get(Constants.LOGIN_TIME_SLOT)):Constants.SEP);
                }
                if (fullText.contains("{{loginCount}}")) {
                    fullText = fullText.replace("{{loginCount}}", userTTSheet6Map!= null && userTTSheet6Map.size()>0 && !StringUtils.isEmpty(userTTSheet6Map.get(0).get(Constants.LOGIN_COUNT).toString())?String.valueOf(userTTSheet6Map.get(0).get(Constants.LOGIN_COUNT)):Constants.SEP);
                }
            }
            //用户统计报告 --第五段 pc操作系统
            if (fullText.contains("{{pcOsName}}")) {
                fullText = fullText.replace("{{pcOsName}}", userSheet5!= null && userSheet5.size()>0 && !StringUtils.isEmpty(userSheet5.get(0).get(Constants.PC_OS_NAME).toString())?String.valueOf(userSheet5.get(0).get(Constants.PC_OS_NAME)):Constants.SEP);
            }
            //用户统计报告 --第六段 pc浏览器
            if (fullText.contains("{{pcBrowserName}}")) {
                fullText = fullText.replace("{{pcBrowserName}}", userSheet6!= null && userSheet6.size()>0 && !StringUtils.isEmpty(userSheet6.get(0).get(Constants.BROWSER_NAME).toString())?String.valueOf(userSheet6.get(0).get(Constants.BROWSER_NAME)):Constants.SEP);
            }
            //用户统计报告 --第七段 app操作系统
            if (fullText.contains("{{appOsName}}")) {
                fullText = fullText.replace("{{appOsName}}", userSheet7!= null && userSheet7.size()>0 && !StringUtils.isEmpty(userSheet7.get(0).get(Constants.APP_OS_NAME).toString())?String.valueOf(userSheet7.get(0).get(Constants.APP_OS_NAME)):Constants.SEP);
            }
            //用户统计报告 --第八段 app手机型号
            if (fullText.contains("{{phoneModelName}}")) {
                fullText = fullText.replace("{{phoneModelName}}", userSheet8!= null && userSheet8.size()>0 && !StringUtils.isEmpty(userSheet8.get(0).get(Constants.PHONE_MODEL_NAME).toString())?String.valueOf(userSheet8.get(0).get(Constants.PHONE_MODEL_NAME)):Constants.SEP);
            }
            //用户统计报告 --第九段 app版本
            if (fullText.contains("{{appVersionName}}")) {
                fullText = fullText.replace("{{appVersionName}}", userSheet9!= null && userSheet9.size()>0 && !StringUtils.isEmpty(userSheet9.get(0).get(Constants.VERSION_NAME).toString())?String.valueOf(userSheet9.get(0).get(Constants.VERSION_NAME)):Constants.SEP);
            }
            //用户统计报告 --第十段 表用户数量按组织排行
            if (fullText.contains("{{deptName9}}")) {
                fullText = fullText.replace("{{deptName9}}", userSheet10!= null && userSheet10.size()>0 && !StringUtils.isEmpty(userSheet10.get(0).get(Constants.DEPT_NAME).toString())?String.valueOf(userSheet10.get(0).get(Constants.DEPT_NAME)):Constants.SEP);
            }
            // 删除原始段落中的所有运行
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }
            // 创建一个新运行，并将更新后的文本添加到该运行中
            if (!StringUtils.isEmpty(fullText)) {
                //设置文档内容字体样式
                XWPFRun newRun = paragraph.createRun();
                newRun.setText(fullText, 0);
                newRun.setFontFamily("宋体");
                if (fullText.equals("用户统计报告")) {
                    //设置标题字体样式
                    newRun.setBold(true); //字体加粗
                    newRun.setFontSize(22);  //对应字体大小 二号
                } else {
                    newRun.setFontSize(12);  //对应字体大小 小四
                }
            } else {
                //设置表格描述字体样式
                XWPFRun newRun1 = paragraph.createRun();
                newRun1.setText(tableText, 0);
                newRun1.setFontSize(11);
                newRun1.setFontFamily("黑体");
            }
        }
        // 填充表格1数据：用户登录按时间统计
        XWPFTable table1 = document.getTables().get(0);
        if (userSheet2 != null && userSheet2.size() > 0) {
            //删除表格第二行默认行
            table1.removeRow(1);
            for (int i = 0; i < userSheet2.size(); i++) {
                XWPFTableRow row = table1.createRow();
                tableNormalFormat(table1, row,0, String.valueOf(i+1));
                tableNormalFormat(table1, row,2, String.valueOf(userSheet2.get(i).get(Constants.LOGIN_TOTAL_COUNT)));
                Object loginNumObj = userSheet2.get(i).get("loginNum");
                if (loginNumObj instanceof List) {
                    for (Map map1 : (List<Map>)loginNumObj) {
                        tableNormalFormat(table1, row,1, String.valueOf(map1.get(Constants.LOGIN_DATE)));
                        String loginType = (String) map1.get(Constants.LOGIN_TYPE_NAME);
                        switch (loginType) {
                            case "web_manage":
                                tableNormalFormat(table1, row,5, String.valueOf(map1.get(Constants.LOGIN_COUNT)));
                                break;
                            case "app_manage":
                                tableNormalFormat(table1, row,3, String.valueOf(map1.get(Constants.LOGIN_COUNT)));
                                break;
                            case "wx_manage":
                                tableNormalFormat(table1, row,4, String.valueOf(map1.get(Constants.LOGIN_COUNT)));
                                break;
                            default: break;
                        }
                    }
                }
            }
        }
        // 填充表格2数据：用户登录统计
        XWPFTable table2 = document.getTables().get(1);
        if (userSheet3 != null && userSheet3.size() > 0) {
            //删除表格第二行默认行
            table2.removeRow(1);
            for (int i = 0; i < userSheet3.size(); i++) {
                XWPFTableRow row = table2.createRow();
                tableNormalFormat(table2, row,0, String.valueOf(i+1));
                tableNormalFormat(table2, row,1, String.valueOf(userSheet3.get(i).get(Constants.LOGIN_USER_NAME))); //用户账号
                tableNormalFormat(table2, row,2, String.valueOf(userSheet3.get(i).get(Constants.LOGIN_NAME))); //用户名称
                tableNormalFormat(table2, row,3, String.valueOf(userSheet3.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        // 填充表格3数据：用户登录统计
        XWPFTable table3 = document.getTables().get(2);
        if (userSheet11 != null && userSheet11.size() > 0) {
            //删除表格第二行默认行
            table3.removeRow(1);
            for (int i = 0; i < userSheet11.size(); i++) {
                XWPFTableRow row = table3.createRow();
                tableNormalFormat(table3, row,0, String.valueOf(i+1));
                tableNormalFormat(table3, row,1, String.valueOf(userSheet11.get(i).get(Constants.LOGIN_USER_NAME))); //用户账号
                tableNormalFormat(table3, row,2, String.valueOf(userSheet11.get(i).get(Constants.LOGIN_NAME))); //用户名称
                tableNormalFormat(table3, row,3, String.valueOf(userSheet11.get(i).get(Constants.LOGIN_DURATION)));
            }
        }
        // 填充表格4数据：用户登录统计
        XWPFTable table4 = document.getTables().get(3);
        if (userSheet12 != null && userSheet12.size() > 0) {
            //删除表格第二行默认行
            table4.removeRow(1);
            for (int i = 0; i < userSheet12.size(); i++) {
                XWPFTableRow row = table4.createRow();
                tableNormalFormat(table4, row,0, String.valueOf(i+1));
                tableNormalFormat(table4, row,1, String.valueOf(userSheet12.get(i).get(Constants.LOGIN_USER_NAME))); //用户账号
                tableNormalFormat(table4, row,2, String.valueOf(userSheet12.get(i).get(Constants.LOGIN_NAME))); //用户名称
                tableNormalFormat(table4, row,3, String.valueOf(userSheet12.get(i).get(Constants.LOGIN_DURATION)));
            }
        }
        // 填充表格5数据：用户登录统计
        XWPFTable table5 = document.getTables().get(4);
        if (userSheet13 != null && userSheet13.size() > 0) {
            //删除表格第二行默认行
            table5.removeRow(1);
            for (int i = 0; i < userSheet13.size(); i++) {
                XWPFTableRow row = table5.createRow();
                tableNormalFormat(table5, row,0, String.valueOf(i+1));
                tableNormalFormat(table5, row,1, String.valueOf(userSheet13.get(i).get(Constants.LOGIN_USER_NAME))); //用户账号
                tableNormalFormat(table5, row,2, String.valueOf(userSheet13.get(i).get(Constants.LOGIN_NAME))); //用户名称
                tableNormalFormat(table5, row,3, String.valueOf(userSheet13.get(i).get(Constants.LOGIN_DURATION)));
            }
        }
        // 填充表格6数据：用户登录24小时分布
        XWPFTable table6 = document.getTables().get(5);
        if (userSheet4 != null && userSheet4.size() > 0) {
            //删除表格第二行默认行
            table6.removeRow(1);
            for (int i = 0; i < userSheet4.size(); i++) {
                XWPFTableRow row = table6.createRow();
                tableNormalFormat(table6, row,0, String.valueOf(i+1));
                tableNormalFormat(table6, row,1, String.valueOf(userSheet4.get(i).get(Constants.LOGIN_TIME_SLOT)));
                tableNormalFormat(table6, row,2, String.valueOf(userSheet4.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        // 填充表格7数据：PC操作系统排行
        XWPFTable table7 = document.getTables().get(6);
        if (userSheet5 != null && userSheet5.size() > 0) {
            //删除表格第二行默认行
            table7.removeRow(1);
            for (int i = 0; i < userSheet5.size(); i++) {
                XWPFTableRow row = table7.createRow();
                tableNormalFormat(table7, row,0, String.valueOf(i+1));
                tableNormalFormat(table7, row,1, String.valueOf(userSheet5.get(i).get(Constants.PC_OS_NAME)));
                tableNormalFormat(table7, row,2, String.valueOf(userSheet5.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        // 填充表格8数据：PC浏览器排行
        XWPFTable table8 = document.getTables().get(7);
        if (userSheet6 != null && userSheet6.size() > 0) {
            //删除表格第二行默认行
            table8.removeRow(1);
            for (int i = 0; i < userSheet6.size(); i++) {
                XWPFTableRow row = table8.createRow();
                tableNormalFormat(table8, row,0, String.valueOf(i+1));
                tableNormalFormat(table8, row,1, String.valueOf(userSheet6.get(i).get(Constants.BROWSER_NAME)));
                tableNormalFormat(table8, row,2, String.valueOf(userSheet6.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        // 填充表格9数据：APP操作系统排行
        XWPFTable table9 = document.getTables().get(8);
        if (userSheet7 != null && userSheet7.size() > 0) {
            //删除表格第二行默认行
            table9.removeRow(1);
            for (int i = 0; i < userSheet7.size(); i++) {
                XWPFTableRow row = table9.createRow();
                tableNormalFormat(table9, row,0, String.valueOf(i+1));
                tableNormalFormat(table9, row,1, String.valueOf(userSheet7.get(i).get(Constants.APP_OS_NAME)));
                tableNormalFormat(table9, row,2, String.valueOf(userSheet7.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        // 填充表格10数据：APP手机型号排行
        XWPFTable table10 = document.getTables().get(9);
        if (userSheet8 != null && userSheet8.size() > 0) {
            //删除表格第二行默认行
            table10.removeRow(1);
            for (int i = 0; i < userSheet8.size(); i++) {
                XWPFTableRow row = table10.createRow();
                tableNormalFormat(table10, row,0, String.valueOf(i+1));
                tableNormalFormat(table10, row,1, String.valueOf(userSheet8.get(i).get(Constants.PHONE_MODEL_NAME)));
                tableNormalFormat(table10, row,2, String.valueOf(userSheet8.get(i).get(Constants.LOGIN_COUNT)));
            }
        }
        // 填充表格11数据：APP版本排行
        XWPFTable table11 = document.getTables().get(10);
        if (userSheet9 != null && userSheet9.size() > 0) {
            //删除表格第二行默认行
            table11.removeRow(1);
            for (int i = 0; i < userSheet9.size(); i++) {
                XWPFTableRow row = table11.createRow();
                tableNormalFormat(table11, row,0, String.valueOf(i+1));
                tableNormalFormat(table11, row,1, String.valueOf(userSheet9.get(i).get(Constants.VERSION_NAME)));
                tableNormalFormat(table11, row,2, String.valueOf(userSheet9.get(i).get(Constants.LOGIN_COUNT)));
            }
        }

        // 填充表格12数据：用户数量按组织排行
        XWPFTable table12 = document.getTables().get(11);
        if (userSheet10 != null && userSheet10.size() > 0) {
            //删除表格第二行默认行
            table12.removeRow(1);
            for (int i = 0; i < userSheet10.size(); i++) {
                XWPFTableRow row = table12.createRow();
                tableNormalFormat(table12, row,0, String.valueOf(i+1));
                tableNormalFormat(table12, row,1, String.valueOf(userSheet10.get(i).get(Constants.DEPT_NAME)));
                tableNormalFormat(table12, row,2,  String.valueOf(Math.round(Double.parseDouble(String.valueOf(userSheet10.get(i).get("userNum"))))));

            }
        }
    }

    /**
     * 设置单元格文本内容样式
     * 字体：宋体 10 居中 自动换行（不影响表头）
     */
    private static void tableNormalFormat(XWPFTable table, XWPFTableRow row, int i, String value) {
        // 1、设置自动换行
        XWPFTableRow firstRow = table.getRow(1); //获取表格的第一行
        //第一行样式赋值其他单元格
        row.getCell(i).getCTTc().addNewTcPr().set(firstRow.getCell(i).getCTTc().getTcPr());
        // 关闭表格的自动调整大小功能
        CTTblLayoutType tableLayout = table.getCTTbl().getTblPr().addNewTblLayout();
        tableLayout.setType(STTblLayoutType.FIXED);
        // 2、设置字体居中
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
        // 3、设置字体样式
        run1.setText(value);
        run1.setFontSize(11);
        run1.setFontFamily("宋体");
    }

    /**
     * 异步获取统计数据
     * @param customVO
     * @param num
     * @param param
     */
    private void userMethodAll(CustomVO customVO, Integer num, StatisticsExportParam param) {
        Map<String, Object> map = new HashMap<>();
        List<Map> listMap = new ArrayList<>();
        switch (num){
            case 1:
                //用户总览 --客户名称、用户数量、启用用户数、停用用户数
                Result numAll = analysisExportAPI.getCustomNum(customVO);
                map = (Map) numAll.getData();
                param.setUserSheet1(map);
                break;
            case 2:
                //用户登录次数按时间统计 --用户登录总次数、app登录次数、小程序/公众号登录次数、pc登录次数
                TableDataInfo customNumByLoginTime = analysisExportAPI.getCustomNumByLoginTime(customVO);
                listMap = (List<Map>) customNumByLoginTime.getRows();
                param.setUserSheet2(listMap);
                break;
            case 3:
                //用户登录次数排行 --登录次数最多的用户姓名、最大登录次数
                TableDataInfo loginNumList = analysisExportAPI.getLoginNumRank(customVO);
                listMap  = (List<Map>) loginNumList.getRows();
                param.setUserSheet3(listMap);
                break;
            case 33:
                //PC用户登录时长
                customVO.setEndType(1);
                TableDataInfo durationRankPc = analysisExportAPI.getLoginDurationRank(customVO);
                listMap  = (List<Map>) durationRankPc.getRows();
                exportStatisticsService.handleName(listMap);
                param.setUserSheet11(listMap);
                //APP用户登录时长
                customVO.setEndType(2);
                TableDataInfo durationRankApp = analysisExportAPI.getLoginDurationRank(customVO);
                listMap  = (List<Map>) durationRankApp.getRows();
                exportStatisticsService.handleName(listMap);
                param.setUserSheet12(listMap);
                //小程序用户登录时长
                customVO.setEndType(3);
                TableDataInfo durationRankWx = analysisExportAPI.getLoginDurationRank(customVO);
                listMap  = (List<Map>) durationRankWx.getRows();
                exportStatisticsService.handleName(listMap);
                param.setUserSheet13(listMap);
                break;
            case 4:
                //用户登录24小时统计 --登录次数最多的时间段、登录次数
                TableDataInfo loginNumByHourList = analysisExportAPI.getLoginNumByHour(customVO);
                listMap  = (List<Map>) loginNumByHourList.getRows();
                param.setUserSheet4(listMap);
                break;
            case 5:
                //PC操作系统统计 --PC操作系统
                TableDataInfo pcBrowerList = analysisExportAPI.getLoginTypeByPcOs(customVO);
                listMap  = (List<Map>) pcBrowerList.getRows();
                param.setUserSheet5(listMap);
                break;
            case 6:
                //pc浏览器统计 --PC浏览器
                TableDataInfo pcOsList = analysisExportAPI.getLoginTypeByPcBrower(customVO);
                listMap  = (List<Map>) pcOsList.getRows();
                param.setUserSheet6(listMap);
                break;
            case 7:
                //app操作系统统计 --APP操作系统
                TableDataInfo appOsList = analysisExportAPI.getLoginTypeByAppOs(customVO);
                listMap  = (List<Map>) appOsList.getRows();
                param.setUserSheet7(listMap);
                break;
            case 8:
                //app手机型号统计 --手机型号
                TableDataInfo phoneModelList = analysisExportAPI.getLoginTypeByPhoneModel(customVO);
                listMap  = (List<Map>) phoneModelList.getRows();
                param.setUserSheet8(listMap);
                break;
            case 9:
                //app版本统计 --APP版本
                TableDataInfo appVersionList = analysisExportAPI.getLoginTypeByAppVersion(customVO);
                listMap  = (List<Map>) appVersionList.getRows();
                param.setUserSheet9(listMap);
                break;
            case 10:
                //用户数量组织排行
                TableDataInfo customNumByDept = analysisExportAPI.getCustomNumByDept(customVO);
                listMap  = (List<Map>) customNumByDept.getRows();
                param.setUserSheet10(listMap);
                break;
            default: break;
        }
    }
}
