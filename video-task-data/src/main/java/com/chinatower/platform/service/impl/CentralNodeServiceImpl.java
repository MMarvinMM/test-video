package com.chinatower.platform.service.impl;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.file.request.SysFileRequest;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.dao.LocalRequest;
import com.chinatower.platform.dao.LocalizationAlarmDetailedInfoDAO;
import com.chinatower.platform.dao.AlarmSyncStatusMongoDAO;
import com.chinatower.platform.easy.ExcelInterceptListener;
import com.chinatower.platform.easy.ExcelOperaceptListener;
import com.chinatower.platform.easy.ExcelWarningListener;
import com.chinatower.platform.easy.dto.InterceptOrderEntityVo;
import com.chinatower.platform.easy.dto.OperateOrderEntityVo;
import com.chinatower.platform.easy.dto.WarningOrderEntityVo;
import com.chinatower.platform.elasticsearch.repository.InteceptRepository;
import com.chinatower.platform.elasticsearch.repository.OperateRepository;
import com.chinatower.platform.elasticsearch.repository.WarningRepository;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.modules.InterceptOrderEntity;
import com.chinatower.platform.modules.OperateOrderEntity;
import com.chinatower.platform.modules.WarningOrderEntity;
import com.chinatower.platform.modules.AlarmSyncStatusEntity;
import com.chinatower.platform.modules.AlarmSyncFailItem;
import com.chinatower.platform.modules.AlarmSyncEsErrorCollector;
import com.chinatower.platform.service.CentralNodeService;
import com.chinatower.platform.service.AlarmSyncFailNotifyService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2023/10/31 9:54
 */

@Slf4j
@Service
public class CentralNodeServiceImpl implements CentralNodeService {

    @Autowired
    private LocalizationAlarmDetailedInfoDAO localizationAlarmDetailedInfoDAO;

    @Autowired
    private WarningRepository warningRepository;

    @Autowired
    private OperateRepository operateRepository;

    @Autowired
    private InteceptRepository inteceptRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private AlarmSyncStatusMongoDAO alarmSyncStatusMongoDAO;

    @Autowired
    private AlarmSyncFailNotifyService alarmSyncFailNotifyService;

    @Value("${path.temp}")
    private String path;
    @Value("${ofs.filepath}")
    private String filePath;

    /**
     * 将上传到ofs上的各下沉节点工单数据同步到es
     * @param param
     */
    @Override
    public void pushEsWarningOrderByOfs(String param) {
        LocalRequest request = null;
        if(!StringUtils.isEmpty(param)){
            request = JSONObject.parseObject(param,LocalRequest.class);
        }
        //所有垚查询的节点
        List<String> allPlatformCodeList = new ArrayList<>();
        //所有的父节点
        List<String> parentCodeList = new ArrayList<>();
        if(request != null && !StringUtils.isEmpty(request.getColonyCode())){
            allPlatformCodeList.add(request.getColonyCode());
        }else {
            //获取当前节点
            SysConfig sysConfig = new SysConfig();
            sysConfig.setConfigKey(Constants.PLATFORM_CODE);
            AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
            if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))){
                SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")),SysConfig.class);
                if(sysConfigInfo != null){
                    parentCodeList.add(sysConfigInfo.getConfigValue());
                    boolean flag = true;
                    int i = 1;
                    while (i<=5){
                        i++;
                        parentCodeList=this.getAllplatformCode(parentCodeList);
                        if(!CollectionUtils.isEmpty(parentCodeList)){
                            allPlatformCodeList.addAll(parentCodeList);
                        }else{
                            i = 6;
                        }
                    }
                }else{
                    log.info("pushEsWarningOrderByOfs-当前环境未配置属地化节点:{}",JSON.toJSONString(result));
                    return;
                }
            }else{
                log.info("pushEsWarningOrderByOfs-获取当前节点失败:{}",JSON.toJSONString(result));
                return;
            }
        }
        //所有属地化数据整合
        List<WarningOrderEntity> AllOrderList = new ArrayList<>();
        List<OperateOrderEntity> AllLOperateist = new ArrayList<>();
        List<InterceptOrderEntity> AllLInterceptist = new ArrayList<>();
        log.info("pushEsWarningOrderByOfs-所有需要查询的属地化节点:{}",JSON.toJSONString(allPlatformCodeList));
        //查询所有下沉节点上传至ofs的文件
        //List<PlatformDataConfigDto> configList = localizationAlarmDetailedInfoDAO.getAllSwitchData(allPlatformCodeList);
        if(!CollectionUtils.isEmpty(allPlatformCodeList)){
            // 拉取日期：默认前一天，可通过 param.dateString 指定
            String pullDate;
            if (request == null || StringUtils.isEmpty(request.getDateString())) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DATE, -1);
                pullDate = DateUtil.format(cal.getTime(), "yyyyMMdd");
            } else {
                pullDate = request.getDateString();
            }
            // 本次同步失败明细，任务结束后聚合发送一条短信
            List<AlarmSyncFailItem> failList = new ArrayList<>();
            //读取文件获取所有工单
            for (String platformCode : allPlatformCodeList) {
                String dateString = platformCode + "_" + pullDate;
                try {
                    //工单表读取
                    String orderOfsUrl = "/"+platformCode+filePath+"/"+dateString+"_"+ Constants.WARN_ORDER_TEBLE+Constants.XLSX_SUFFIX;
                    String orderRealUrl = this.getRealUrl(platformCode,orderOfsUrl);
                    log.info("pushEsWarningOrderByOfs-orderRealUrl-platformCode:{},{}",orderRealUrl,platformCode);
                    AlarmSyncEsErrorCollector warningErrorCollector = new AlarmSyncEsErrorCollector();
                    if(!StringUtils.isEmpty(orderRealUrl) && !Constants.PARAM_NULL.equals(orderRealUrl)){
                        EasyExcel.read(new URL(orderRealUrl).openStream(), WarningOrderEntityVo.class,
                                new ExcelWarningListener(warningRepository, warningErrorCollector)).sheet().doRead();
                    }else{
                        log.info("pushEsWarningOrderByOfs工单没有数据同步-platformCode:{}",platformCode);
                    }
                    saveSyncStatusAfterRead(platformCode, pullDate, Constants.SYNC_TYPE_WARNING_ORDER, warningErrorCollector, failList);
                }catch (Exception e){
                    log.info("pushEsWarningOrderByOfs入参orderOfs:{}", JSON.toJSONString(platformCode));
                    handleReadException(platformCode, pullDate, Constants.SYNC_TYPE_WARNING_ORDER, "工单", e, failList);
                }
                try {
                    //运营研判表读取
                    String operateOfsUrl = "/"+platformCode+filePath+"/"+dateString+"_"+ Constants.WARN_ALARM_OPERATE+Constants.XLSX_SUFFIX;
                    String operateRealUrl = this.getRealUrl(platformCode,operateOfsUrl);
                    log.info("pushEsWarningOrderByOfs-operateRealUrl-platformCode:{},{}",operateRealUrl,platformCode);
                    AlarmSyncEsErrorCollector operateErrorCollector = new AlarmSyncEsErrorCollector();
                    if(!StringUtils.isEmpty(operateRealUrl) && !Constants.PARAM_NULL.equals(operateRealUrl)){
                        EasyExcel.read(new URL(operateRealUrl).openStream(), OperateOrderEntityVo.class,
                                new ExcelOperaceptListener(operateRepository, operateErrorCollector)).sheet().doRead();
                    }else{
                        log.info("pushEsWarningOrderByOfs运营研判工单没有数据同步-platformCode:{}",platformCode);
                    }
                    saveSyncStatusAfterRead(platformCode, pullDate, Constants.SYNC_TYPE_OPERATE, operateErrorCollector, failList);
                }catch (Exception e){
                    log.info("pushEsWarningOrderByOfs入参-operateOfs:{}", JSON.toJSONString(platformCode));
                    handleReadException(platformCode, pullDate, Constants.SYNC_TYPE_OPERATE, "运营研判", e, failList);
                }

                try {
                    //回收站读取
                    String interceptOfsUrl ="/"+platformCode+filePath+"/"+dateString+"_"+  Constants.WARN_ALARM_INTERCEPT+Constants.XLSX_SUFFIX;
                    String interceptRealUrl = this.getRealUrl(platformCode,interceptOfsUrl);
                    log.info("pushEsWarningOrderByOfs-interceptRealUrl-platformCode:{},{}",interceptRealUrl,platformCode);
                    AlarmSyncEsErrorCollector interceptErrorCollector = new AlarmSyncEsErrorCollector();
                    if(!StringUtils.isEmpty(interceptRealUrl) && !Constants.PARAM_NULL.equals(interceptRealUrl)){
                        EasyExcel.read(new URL(interceptRealUrl).openStream(), InterceptOrderEntityVo.class,
                                new ExcelInterceptListener(inteceptRepository, interceptErrorCollector)).sheet().doRead();
                    }else{
                        log.info("pushEsWarningOrderByOfs回收站工单没有数据同步-platformCode:{}",platformCode);
                    }
                    saveSyncStatusAfterRead(platformCode, pullDate, Constants.SYNC_TYPE_INTERCEPT, interceptErrorCollector, failList);
                }catch (Exception e){
                    log.info("pushEsWarningOrderByOfs入参-interceptOfs:{}", JSON.toJSONString(platformCode));
                    handleReadException(platformCode, pullDate, Constants.SYNC_TYPE_INTERCEPT, "回收站", e, failList);
                }
            }
            if (!CollectionUtils.isEmpty(failList)) {
                alarmSyncFailNotifyService.sendFailNotifyBatch(pullDate, failList);
            }
        }
        //将读取得工单上传至ES
       /* if(!CollectionUtils.isEmpty(AllOrderList)){
            for (WarningOrderEntity vo:AllOrderList) {
                if(!StringUtils.isEmpty(vo.getFlowNodeTime())){
                    vo.setFlowNodeTimeDate(DateUtil.parseDateTime(vo.getFlowNodeTime()));
                }
                if(!StringUtils.isEmpty(vo.getAlarmTime())){
                    vo.setAlarmTimeDate(DateUtil.parseDateTime(vo.getAlarmTime()));
                }
                if(!StringUtils.isEmpty(vo.getCompConfidenceLevel())){
                    vo.setConfidenceLevelMax(vo.getCompConfidenceLevel());
                }
            }
            log.info("CentralNodeServiceImpl-pushEsWarningOrderByOfs读取到的工单数量:{}",JSON.toJSONString(AllOrderList.size()));
            List<List<WarningOrderEntity>> orderList = Lists.partition(AllOrderList,5000);
            orderList.forEach(item ->{
                List<String> orderIdList = item.stream().map(WarningOrderEntity::getWarningOrderId).collect(Collectors.toList());
                QueryBuilder query = QueryBuilders.boolQuery();
                query = ((BoolQueryBuilder) query).must(QueryBuilders.termsQuery(Constants.PARAM_WARN_ORDER_ID, orderIdList));
                //删除包含此次新增数据中的历史数据，查询最新数据
                try{
                    warningRepository.deleteAll(warningRepository.search(query));
                }catch (Exception e){
                    log.info("CentralNodeServiceImpl-删除工单失败");
                }
                warningRepository.saveAll(item);
            });
        }

        //将读取得运营研判上传ES
        if(!CollectionUtils.isEmpty(AllLOperateist)){
            for (OperateOrderEntity vo:AllLOperateist) {
                if(!StringUtils.isEmpty(vo.getModifierTime())){
                    vo.setModifierTimeDate(DateUtil.parseDateTime(vo.getModifierTime()));
                }
                if(!StringUtils.isEmpty(vo.getAlarmTime())){
                    vo.setAlarmTimeDate(DateUtil.parseDateTime(vo.getAlarmTime()));
                }
                if(!StringUtils.isEmpty(vo.getCompConfidenceLevel())){
                    vo.setConfidenceLevelMax(vo.getCompConfidenceLevel());
                }
            }
            log.info("CentralNodeServiceImpl-pushEsWarningOrderByOfs读取到的运营研判数量:{}",JSON.toJSONString(AllLOperateist.size()));
            List<List<OperateOrderEntity>> operateList = Lists.partition(AllLOperateist,5000);
            operateList.forEach(item ->{
                List<String> orderIdList = item.stream().map(OperateOrderEntity::getWarningOrderId).collect(Collectors.toList());
                QueryBuilder query = QueryBuilders.boolQuery();
                query = ((BoolQueryBuilder) query).must(QueryBuilders.termsQuery(Constants.PARAM_WARN_ORDER_ID, orderIdList));
                //删除包含此次新增数据中的历史数据，查询最新数据
                try{
                    operateRepository.deleteAll(operateRepository.search(query));
                }catch (Exception e){
                    log.info("CentralNodeServiceImpl-删除运营研判单失败");
                }
                operateRepository.saveAll(item);
            });
        }
        //将读取得回收站数据上传ES
        if(!CollectionUtils.isEmpty(AllLInterceptist)){
            for (InterceptOrderEntity vo:AllLInterceptist) {
                if(!StringUtils.isEmpty(vo.getAlarmTime())){
                    vo.setAlarmTimeDate(DateUtil.parseDateTime(vo.getAlarmTime()));
                }
                if(!StringUtils.isEmpty(vo.getCompConfidenceLevel())){
                    vo.setConfidenceLevelMax(vo.getCompConfidenceLevel());
                }
            }
            log.info("CentralNodeServiceImpl-pushEsWarningOrderByOfs读取到的回收站数量:{}",JSON.toJSONString(AllLInterceptist.size()));
            List<List<InterceptOrderEntity>> interceptList = Lists.partition(AllLInterceptist,5000);
            interceptList.forEach(item ->{
                List<String> orderIdList = item.stream().map(InterceptOrderEntity::getWarningOrderId).collect(Collectors.toList());
                QueryBuilder query = QueryBuilders.boolQuery();
                query = ((BoolQueryBuilder) query).must(QueryBuilders.termsQuery(Constants.PARAM_WARN_ORDER_ID, orderIdList));
                //删除包含此次新增数据中的历史数据，查询最新数据
                try{
                    inteceptRepository.deleteAll(inteceptRepository.search(query));
                }catch (Exception e){
                    log.info("CentralNodeServiceImpl-删除回收站单失败");
                }
                inteceptRepository.saveAll(item);
            });
        }
*/

    }

    /**
     * Excel 读取完成后，根据 ES 写入结果保存同步状态
     */
    private void saveSyncStatusAfterRead(String platformCode, String pullDate, int syncType,
                                         AlarmSyncEsErrorCollector errorCollector, List<AlarmSyncFailItem> failList) {
        if (errorCollector.hasError()) {
            saveAlarmSyncStatus(platformCode, pullDate, syncType, false, errorCollector.getError(), failList);
            return;
        }
        saveAlarmSyncStatus(platformCode, pullDate, syncType, true, null, failList);
    }

    /**
     * 保存属地告警同步状态到 Mongo
     *
     * @param platformCode 属地编码
     * @param pullDate     拉取日期，格式 yyyyMMdd
     * @param syncType     同步类型：1-工单，2-运营研判，3-回收站
     * @param syncSuccess  同步是否成功
     * @param error        失败时的异常
     * @param failList     失败明细收集列表
     */
    private void saveAlarmSyncStatus(String platformCode, String pullDate, int syncType, boolean syncSuccess,
                                   Exception error, List<AlarmSyncFailItem> failList) {
        try {
            AlarmSyncStatusEntity statusEntity = new AlarmSyncStatusEntity();
            statusEntity.setCreateTime(new Date());
            statusEntity.setPlatformCode(platformCode);
            statusEntity.setPullDate(pullDate);
            statusEntity.setSyncType(syncType);
            statusEntity.setSyncStatus(syncSuccess ? Constants.SYNC_STATUS_SUCCESS : Constants.SYNC_STATUS_FAIL);
            alarmSyncStatusMongoDAO.saveSyncStatus(statusEntity);
            if (!syncSuccess) {
                failList.add(new AlarmSyncFailItem(platformCode, pullDate, syncType, error == null ? null : error.getMessage()));
            }
        } catch (Exception e) {
            log.error("保存属地告警同步状态失败, platformCode:{}, pullDate:{}, syncType:{}", platformCode, pullDate, syncType, e);
        }
    }

    /**
     * 处理 Excel 读取异常：无数据场景记成功，其他异常记失败并通知
     */
    private void handleReadException(String platformCode, String pullDate, int syncType,
                                     String syncTypeName, Exception error, List<AlarmSyncFailItem> failList) {
        if (isIgnorableReadException(error)) {
            log.info("pushEsWarningOrderByOfs{}无数据同步-platformCode:{}, reason:{}",
                    syncTypeName, platformCode, error.getMessage());
            saveAlarmSyncStatus(platformCode, pullDate, syncType, true, null, failList);
            return;
        }
        log.error("pushEsWarningOrderByOfs-{}读取异常:", syncTypeName, error);
        saveAlarmSyncStatus(platformCode, pullDate, syncType, false, error, failList);
    }

    /**
     * 判断是否为可忽略的无数据读取异常（不计入同步失败）
     */
    private boolean isIgnorableReadException(Exception error) {
        return isFileNotExistsException(error) || isEmptyExcelException(error);
    }

    /**
     * 判断是否为 Excel 无 sheet 异常（空文件等场景）
     */
    private boolean isEmptyExcelException(Exception error) {
        if (error == null) {
            return false;
        }
        Throwable current = error;
        while (current != null) {
            String message = current.getMessage();
            if (!StringUtils.isEmpty(message)
                    && message.toLowerCase().contains(Constants.EXCEL_NO_SHEET_MSG)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    /**
     * 判断异常是否为文件不存在（无数据同步场景）
     */
    private boolean isFileNotExistsException(Exception error) {
        if (error == null) {
            return false;
        }
        Throwable current = error;
        while (current != null) {
            if (current instanceof FileNotFoundException) {
                return true;
            }
            String message = current.getMessage();
            if (!StringUtils.isEmpty(message)) {
                String lowerMsg = message.toLowerCase();
                if (lowerMsg.contains("404")
                        || lowerMsg.contains("文件不存在")
                        || lowerMsg.contains("no such file")
                        || lowerMsg.contains("nosuchkey")
                        || lowerMsg.contains("object not found")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    /**
     * 获取所有节点编码
     * @param parentCodeList
     * @return
     */
    private List<String> getAllplatformCode(List<String> parentCodeList){
        return localizationAlarmDetailedInfoDAO.getPlatformCodeList(parentCodeList);
    }

    /**
     * 获取真实的属地ofs地址
     * @param platformCode
     * @param fileName
     * @return
     */
    public String getRealUrl(String platformCode,String fileName){
        log.info("CentralNodeServiceImplgetRealUrl-req:{},{}",platformCode,fileName);
        SysFileRequest request = new SysFileRequest();
        request.setColonyCode(platformCode);
        request.setFileName(fileName);
        request.setUseOfs(true);
        Result<String> result1 =  fileFeignClient.getFileUrlByFileName(request);
        log.info("CentralNodeServiceImplgetRealUrl-resp:{}",JSON.toJSONString(result1));
        if(result1 != null){
            return String.valueOf(result1.getData());
        }
        return null;
    }

    /**
     * 将excel文件数据读取并封装List
     * @param
     * @param
     * @throws IOException
     */
    public MultipartFile getDataByExcel(String realUrl) throws Exception {
        return getFileTwo(realUrl);
    }

    private MultipartFile getFileTwo(String url) throws IOException {
        /*String randomFileName = "new_url_" + new SecureRandom().nextInt(100000);
        Path tempFile = Files.createTempFile(randomFileName, ".xlsx");*/
        String randomFileName = "new_url_" + new SecureRandom().nextInt(100000);
        Path directoryPath = Paths.get(path);
        Path filePath = directoryPath.resolve(randomFileName+".xlsx");
        Files.createDirectories(directoryPath);
        try (BufferedInputStream inStream = new BufferedInputStream(new URL(url).openStream());
             BufferedOutputStream os = new BufferedOutputStream(Files.newOutputStream(filePath, StandardOpenOption.CREATE))) {

            byte[] buffer = new byte[16 * 1024]; // Increase the buffer size to 16KB
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        } catch (Exception e) {
           log.error("getFileTwo异常",e);
        }
        return new CommonsMultipartFile(creatFileItem(filePath.toFile(),filePath));
    }



    public static FileItem creatFileItem(File file,Path path) throws IOException {

        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory(16, null);
        FileItem fileItem = diskFileItemFactory.createItem("textField", "application/zip", true, file.getName());

        int bytesRead = 0;
        byte[] buffer = new byte[16 * 1024];
        OutputStream outputStream = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            outputStream = fileItem.getOutputStream();
            while ((bytesRead = fileInputStream.read(buffer, 0, 16 * 1024)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            Files.copy(file.toPath(), outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            outputStream.close();
            fileInputStream.close();
            Files.deleteIfExists(path);
        }
        return fileItem;
    }

    /**
     * File转化为FileItem
     * @param file
     * @return
     */
    private static FileItem createFileItem(File file) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem("textField", "text/plain", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }



}
