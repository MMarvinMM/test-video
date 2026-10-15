package com.chinatower.analysis.modules.statistic.service;

import cn.hutool.core.util.StrUtil;
import com.chinatower.analysis.api.entity.CustomVO;
import com.chinatower.analysis.modules.common.Constants;
import com.chinatower.analysis.modules.statistic.dao.UserStatisticDAO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户统计
 * @author lyy
 * @version 1.0
 * @date 2023/04/11 15:34
 */
@Service
@Slf4j
public class UserStatisticService extends TowerBaseController {

    @Resource
    private UserStatisticDAO userStatisticDAO;

    @Resource
    private SystemFeignClient systemFeignClient;
    /**
     * 用户总览
     *
     * @param customVO
     * @return Result
     * @throws
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id",
            industryCode="sd.industry_code", deptColumn = "sd.dept_id")
    public Result getCustomNum(CustomVO customVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        String deptId = loginUser.getUser().getDeptId();
        customVO.setDeptId(deptId);
        customVO.setLocalNode(platformCode);
        Map statisticsMap =  userStatisticDAO.getCustomNum(customVO);
        Map deptNum = userStatisticDAO.getCustomDeptNum(customVO);
        statisticsMap.put(  Constants.DEPT_NUM, deptNum.get(  Constants.DEPT_NUM)); //用户下启用组织数量
        if (MapUtils.isEmpty(statisticsMap)) {
            statisticsMap = new HashMap();
            statisticsMap.put("userTotalNum", Constants.ZERO); //用户总数
            statisticsMap.put("startUserNum", Constants.ZERO); //启用用户总数
            statisticsMap.put("stopUserNum", Constants.ZERO); //停用用户总数
            statisticsMap.put( Constants.DEPT_NUM, deptNum.get(  Constants.DEPT_NUM)); //用户下启用组织数量
        }
        return Result.data(statisticsMap);
    }

    /**
     * 用户数量按组织排行
     * @param customVO
     * @return
     */
    public TableDataInfo getCustomNumByDept(CustomVO customVO){
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNum()) && StringUtils.isNotNull(customVO.getPageSize())){
            customVO.setPageNums(customVO.getPageNum());
            customVO.setPageSizes(customVO.getPageSize());
            customVO.setPageNum(null);
            customVO.setPageSize(null);
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String deptId = loginUser.getUser().getDeptId();
        customVO.setDeptId(deptId);
        List<Map> list = userStatisticDAO.getCustomNumByDept(customVO);
        this.handleNameSort(list,Constants.GET_CUSTOM_NUM_BY_DEPT);
        long total = list.size();
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNums()) && StringUtils.isNotNull(customVO.getPageSizes())){
            int skipNum = customVO.getPageSizes() * (customVO.getPageNums() - 1);
            list= list.stream().skip(skipNum).limit(customVO.getPageSizes()).collect(Collectors.toList());
        }
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setTotal(total);
        return dataTable;
    }

    /**
     * 用户登录24H分布统计
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public List<Map> getLoginNumByHour(CustomVO customVO) {
        log.info("查询用户登录24H分布,入参{}",customVO.toString());
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> loginNumMap = userStatisticDAO.getLoginNumByHour(customVO);
        return loginNumMap;
    }

    /**
     * 用户登录时间统计
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public List<Map> getCustomNumByLoginTime(CustomVO customVO){
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> list = userStatisticDAO.getCustomNumByLoginTime(customVO);
        if (!CollectionUtils.isEmpty(list)) {
            list = list.stream().collect(Collectors.groupingBy(e -> e.get(Constants.LOGIN_DATE))).values().stream().map(d -> {
                List<String> loginTypeNames = d.stream().map(e -> e.get(Constants.LOGIN_TYPE_NAME).toString()).collect(Collectors.toList());
                Map<String, Object> smpData = new HashMap<>();
                if (!loginTypeNames.contains(Constants.WEB_MANAGE)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put(Constants.LOGIN_DATE, d.get(0).get(Constants.LOGIN_DATE));
                    data.put(Constants.LOGIN_TYPE_NAME, Constants.WEB_MANAGE);
                    data.put(Constants.LOGIN_COUNT, Constants.ZERO);
                    d.add(data);
                }
                if (!loginTypeNames.contains(Constants.APP_MANAGE)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put(Constants.LOGIN_DATE, d.get(0).get(Constants.LOGIN_DATE));
                    data.put(Constants.LOGIN_TYPE_NAME, Constants.APP_MANAGE);
                    data.put(Constants.LOGIN_COUNT, Constants.ZERO);
                    d.add(data);
                }
                if (!loginTypeNames.contains(Constants.WX_MANAGE)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put(Constants.LOGIN_DATE, d.get(0).get(Constants.LOGIN_DATE));
                    data.put(Constants.LOGIN_TYPE_NAME, Constants.WX_MANAGE);
                    data.put(Constants.LOGIN_COUNT, Constants.ZERO);
                    d.add(data);
                }
                smpData.put("loginTotalCount", d.stream().map(s -> new BigDecimal(s.get(Constants.LOGIN_COUNT).toString())).reduce(BigDecimal.ZERO, BigDecimal::add));
                smpData.put("loginNum",d.stream().sorted(Comparator.comparing(s -> s.get(Constants.LOGIN_DATE).toString())).collect(Collectors.toList()));
                return smpData;
            }).sorted(Comparator.comparing(s -> s.get("loginNum").toString())).collect(Collectors.toList());
        }
        return list;
    }

    /**
     * 用户登录次数排行
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public TableDataInfo getLoginNumRank(CustomVO customVO) {
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNum()) && StringUtils.isNotNull(customVO.getPageSize())){
            customVO.setPageNums(customVO.getPageNum());
            customVO.setPageSizes(customVO.getPageSize());
            customVO.setPageNum(null);
            customVO.setPageSize(null);
        }
        log.info("查询用户登录次数排行,入参{}",customVO.toString());
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> loginCountMap = userStatisticDAO.getLoginNumRank(customVO);
        this.handleNameSort(loginCountMap,Constants.GET_LOGIN_NUM_RANK);
        long total = loginCountMap.size();
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNums()) && StringUtils.isNotNull(customVO.getPageSizes())){
            int skipNum = customVO.getPageSizes() * (customVO.getPageNums() - 1);
            loginCountMap= loginCountMap.stream().skip(skipNum).limit(customVO.getPageSizes()).collect(Collectors.toList());
        }
        TableDataInfo dataTable = getDataTable(loginCountMap);
        dataTable.setTotal(total);
        return dataTable;
    }

    /**
     * 用户登录时长排行
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id",deptColumn = "dataTable.dept_id")
    public TableDataInfo getLoginDurationRank(CustomVO customVO) {
        log.info("查询用户登录时长排行,入参{}",customVO.toString());
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNum()) && StringUtils.isNotNull(customVO.getPageSize())){
            customVO.setPageNums(customVO.getPageNum());
            customVO.setPageSizes(customVO.getPageSize());
            customVO.setPageNum(null);
            customVO.setPageSize(null);
        }
        if (customVO.getEndType() == null){
            customVO.setEndType(0);
        }
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> loginDurationMap = userStatisticDAO.getLoginDurationRank(customVO);
        this.handleNameSort(loginDurationMap,Constants.GET_LOGIN_DURATION_RANK);
        long total = loginDurationMap.size();
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNums()) && StringUtils.isNotNull(customVO.getPageSizes())){
            int skipNum = customVO.getPageSizes() * (customVO.getPageNums() - 1);
            loginDurationMap= loginDurationMap.stream().skip(skipNum).limit(customVO.getPageSizes()).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(loginDurationMap)) {
            for (Map map : loginDurationMap) {
                long seconds = Long.parseLong(map.get(Constants.ONLINE_TIME).toString());
                long days = TimeUnit.SECONDS.toDays(seconds);
                long hours = TimeUnit.SECONDS.toHours(seconds) - (days * 24);
                long minutes = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
                if (days == 0) {
                    if (hours == 0) {
                        map.put(Constants.LOGIN_DURATION, minutes + "分");
                    } else {
                        map.put(Constants.LOGIN_DURATION, hours + "时" + minutes + "分");
                    }
                } else {
                    map.put(Constants.LOGIN_DURATION, days + "天" + hours + "时" + minutes + "分");
                }
            }
        }
        TableDataInfo dataTable = getDataTable(loginDurationMap);
        dataTable.setTotal(total);
        return dataTable;
    }

    /**
     * 处理排序
     * @param mapList
     * @param typeName
     */
    public void handleNameSort(List<Map> mapList,String typeName){
        Collections.sort(mapList, new Comparator<Map>() {
            @Override
            public int compare(Map data1, Map data2) {
                // 首先比较 deviceNum，进行数字倒序排列
                Integer num1 =0;
                Integer num2 = 0;
                if (Constants.GET_LOGIN_DURATION_RANK.equals(typeName)){
                     num1 = Integer.parseInt(String.valueOf(data1.get(Constants.ONLINE_TIME)));
                     num2 = Integer.parseInt(String.valueOf(data2.get(Constants.ONLINE_TIME)));
                }else if (Constants.GET_LOGIN_NUM_RANK.equals(typeName) || Constants.GET_LOGIN_TYPE_BY_PC_BROWER.equals(typeName) || Constants.GET_LOGIN_TYPE_BY_PC_OS.equals(typeName)
                || Constants.GET_LOGIN_TYPE_BY_APP_OS.equals(typeName) || Constants.GET_LOGIN_TYPE_BY_PHONE_MODEL.equals(typeName) || Constants.GET_LOGIN_TYP_EBY_APP_VERSION.equals(typeName)){
                    num1 = Integer.parseInt(String.valueOf(data1.get("loginCount")));
                    num2 = Integer.parseInt(String.valueOf(data2.get("loginCount")));
                }else if (Constants.GET_CUSTOM_NUM_BY_DEPT.equals(typeName)){
                    num1 = Integer.parseInt(String.valueOf(data1.get("userNum")));
                    num2 = Integer.parseInt(String.valueOf(data2.get("userNum")));
                }
                int deviceNumCompare = num2.compareTo(num1); // 倒序
                // 如果 deviceNum 相同，则继续比较 tenantName 的拼音首字母
                if (deviceNumCompare == 0) {
                    String pinyin1 = "";
                    String pinyin2 = "";
                    if (Constants.GET_LOGIN_DURATION_RANK.equals(typeName) || Constants.GET_LOGIN_NUM_RANK.equals(typeName)){
                         pinyin1 = toPinyin(String.valueOf(data1.get("loginName")));
                         pinyin2 = toPinyin(String.valueOf(data2.get("loginName")));
                    }else if (Constants.GET_LOGIN_TYPE_BY_PC_BROWER.equals(typeName)){
                        pinyin1 = toPinyin(String.valueOf(data1.get("browserName")));
                        pinyin2 = toPinyin(String.valueOf(data2.get("browserName")));
                    }else if (Constants.GET_LOGIN_TYPE_BY_PC_OS.equals(typeName)){
                        pinyin1 = toPinyin(String.valueOf(data1.get(Constants.PC_OS_NAME)));
                        pinyin2 = toPinyin(String.valueOf(data2.get(Constants.PC_OS_NAME)));
                    }else if (Constants.GET_LOGIN_TYPE_BY_APP_OS.equals(typeName)){
                        pinyin1 = toPinyin(String.valueOf(data1.get("appOsName")));
                        pinyin2 = toPinyin(String.valueOf(data2.get("appOsName")));
                    }else if (Constants.GET_LOGIN_TYPE_BY_PHONE_MODEL.equals(typeName)){
                        pinyin1 = toPinyin(String.valueOf(data1.get("phoneModelName")));
                        pinyin2 = toPinyin(String.valueOf(data2.get("phoneModelName")));
                    }else if (Constants.GET_LOGIN_TYP_EBY_APP_VERSION.equals(typeName)){
                        pinyin1 = toPinyin(String.valueOf(data1.get("versionName")));
                        pinyin2 = toPinyin(String.valueOf(data2.get("versionName")));
                    }else if (Constants.GET_CUSTOM_NUM_BY_DEPT.equals(typeName)){
                        pinyin1 = toPinyin(String.valueOf(data1.get("deptName")));
                        pinyin2 = toPinyin(String.valueOf(data2.get("deptName")));
                    }
                    deviceNumCompare = pinyin1.compareTo(pinyin2); // 按拼音首字母排序
                }

                return deviceNumCompare;
            }

            // 辅助方法，将汉字转换为拼音首字母大写形式
            private String toPinyin(String chinese) {
                StringBuilder pinyinStr = new StringBuilder();
                for (char c : chinese.toCharArray()) {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        pinyinStr.append(pinyinArray[0].charAt(0));
                    } else {
                        // 如果转换失败，直接添加原字符（这种情况较少见）
                        pinyinStr.append(c);
                    }
                }
                return pinyinStr.toString().toUpperCase();
            }
        });
    }

    /**
     * PC操作系统统计
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public TableDataInfo getLoginTypeByPcOs(CustomVO customVO) {
        log.info("查询PC操作系统,入参{}",customVO.toString());
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNum()) && StringUtils.isNotNull(customVO.getPageSize())){
            customVO.setPageNums(customVO.getPageNum());
            customVO.setPageSizes(customVO.getPageSize());
            customVO.setPageNum(null);
            customVO.setPageSize(null);
        }
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<String> typeList = new ArrayList<>();
        Result<List<SysDictDataResponse>> systemTypeList = systemFeignClient.dictDataType(Constants.SYSTEM_TYPE);
        if (systemTypeList != null && systemTypeList.getData() != null && !systemTypeList.getData().isEmpty()) {
            typeList = systemTypeList.getData().stream().map(v -> v.getDictLabel()).collect(Collectors.toList());
            customVO.setSystemTypeList(typeList);
        }
        List<Map> pcOsMap = userStatisticDAO.getLoginTypeByPcOs(customVO);
        for (SysDictDataResponse datum : systemTypeList.getData()) {
            for (Map map : pcOsMap) {
                if (String.valueOf(map.get(Constants.PC_OS_NAME)).contains(datum.getDictLabel())){
                    map.put(Constants.PC_OS_NAME,datum.getDictValue());
                }
            }
        }
        this.handleNameSort(pcOsMap,Constants.GET_LOGIN_TYPE_BY_PC_OS);
        long total = pcOsMap.size();
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNums()) && StringUtils.isNotNull(customVO.getPageSizes())){
            int skipNum = customVO.getPageSizes() * (customVO.getPageNums() - 1);
            pcOsMap= pcOsMap.stream().skip(skipNum).limit(customVO.getPageSizes()).collect(Collectors.toList());
        }

        TableDataInfo dataTable = getDataTable(pcOsMap);
        dataTable.setTotal(total);
        return dataTable;
    }

    /**
     * PC浏览器排行
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public TableDataInfo getLoginTypeByPcBrower(CustomVO customVO) {
        log.info("查询PC浏览器排行,入参{}",customVO.toString());
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNum()) && StringUtils.isNotNull(customVO.getPageSize())){
            customVO.setPageNums(customVO.getPageNum());
            customVO.setPageSizes(customVO.getPageSize());
            customVO.setPageNum(null);
            customVO.setPageSize(null);
        }
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> pcBrowerMap = userStatisticDAO.getLoginTypeByPcBrower(customVO);
        this.handleNameSort(pcBrowerMap,Constants.GET_LOGIN_TYPE_BY_PC_BROWER);
        long total = pcBrowerMap.size();
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNums()) && StringUtils.isNotNull(customVO.getPageSizes())){
            int skipNum = customVO.getPageSizes() * (customVO.getPageNums() - 1);
            pcBrowerMap= pcBrowerMap.stream().skip(skipNum).limit(customVO.getPageSizes()).collect(Collectors.toList());
        }
        TableDataInfo dataTable = getDataTable(pcBrowerMap);
        dataTable.setTotal(total);
        return dataTable;
    }

    /**
     * APP操作系统排行
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public TableDataInfo getLoginTypeByAppOs(CustomVO customVO) {
        log.info("查询APP操作系统,入参{}",customVO.toString());
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNum()) && StringUtils.isNotNull(customVO.getPageSize())){
            customVO.setPageNums(customVO.getPageNum());
            customVO.setPageSizes(customVO.getPageSize());
            customVO.setPageNum(null);
            customVO.setPageSize(null);
        }
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> appOsMap = userStatisticDAO.getLoginTypeByAppOs(customVO);
        this.handleNameSort(appOsMap,Constants.GET_LOGIN_TYPE_BY_APP_OS);
        long total = appOsMap.size();
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNums()) && StringUtils.isNotNull(customVO.getPageSizes())){
            int skipNum = customVO.getPageSizes() * (customVO.getPageNums() - 1);
            appOsMap= appOsMap.stream().skip(skipNum).limit(customVO.getPageSizes()).collect(Collectors.toList());
        }
        TableDataInfo dataTable = getDataTable(appOsMap);
        dataTable.setTotal(total);
        return dataTable;
    }

    /**
     * APP手机型号排行
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public TableDataInfo getLoginTypeByPhoneModel(CustomVO customVO) {
        log.info("查询APP手机型号排行,入参{}",customVO.toString());
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNum()) && StringUtils.isNotNull(customVO.getPageSize())){
            customVO.setPageNums(customVO.getPageNum());
            customVO.setPageSizes(customVO.getPageSize());
            customVO.setPageNum(null);
            customVO.setPageSize(null);
        }
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> phoneModelMap = userStatisticDAO.getLoginTypeByPhoneModel(customVO);
        this.handleNameSort(phoneModelMap,Constants.GET_LOGIN_TYPE_BY_PHONE_MODEL);
        long total = phoneModelMap.size();
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNums()) && StringUtils.isNotNull(customVO.getPageSizes())){
            int skipNum = customVO.getPageSizes() * (customVO.getPageNums() - 1);
            phoneModelMap= phoneModelMap.stream().skip(skipNum).limit(customVO.getPageSizes()).collect(Collectors.toList());
        }
        TableDataInfo dataTable = getDataTable(phoneModelMap);
        dataTable.setTotal(total);
        return dataTable;
    }

    /**
     * APP版本排行
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public TableDataInfo getLoginTypeByAppVersion(CustomVO customVO) {
        log.info("查询APP版本排行,入参{}",customVO.toString());
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNum()) && StringUtils.isNotNull(customVO.getPageSize())){
            customVO.setPageNums(customVO.getPageNum());
            customVO.setPageSizes(customVO.getPageSize());
            customVO.setPageNum(null);
            customVO.setPageSize(null);
        }
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> appVersionMap = userStatisticDAO.getLoginTypeByAppVersion(customVO);
        this.handleNameSort(appVersionMap,Constants.GET_LOGIN_TYP_EBY_APP_VERSION);
        long total = appVersionMap.size();
        //分页处理
        if (StringUtils.isNotNull(customVO.getPageNums()) && StringUtils.isNotNull(customVO.getPageSizes())){
            int skipNum = customVO.getPageSizes() * (customVO.getPageNums() - 1);
            appVersionMap= appVersionMap.stream().skip(skipNum).limit(customVO.getPageSizes()).collect(Collectors.toList());
        }
        TableDataInfo dataTable = getDataTable(appVersionMap);
        dataTable.setTotal(total);
        return dataTable;
    }

    /**
     * 登录成功率分析
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public List<Map> getLoginSuccessRate(CustomVO customVO) {
        log.info("查询登录成功率分析,入参{}",customVO.toString());
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> successMap = userStatisticDAO.getLoginSuccessRate(customVO);
        if (!CollectionUtils.isEmpty(successMap)) {
            //如果没有登录失败次数，补充字段
            if (successMap.stream().noneMatch(e -> Constants.ONE_STRING.equals(e.get(Constants.LOGIN_STATUS)))) {
                Map<String, Object> data = new HashMap<>();
                data.put(Constants.LOGIN_STATUS, Constants.ONE_STRING);
                data.put(Constants.LOGIN_COUNT, Constants.ZERO);
                successMap.add(data);
            }
            Map<String, String> map = new HashMap<>();
            DecimalFormat decimalFormat = new DecimalFormat("#");
            List<Map> successList = successMap.stream()
                    .filter(e -> e.get(Constants.LOGIN_STATUS).equals("0"))
                    .collect(Collectors.toList());
            Double successCount;
            if (!CollectionUtils.isEmpty(successList)) {
                successCount = Double.parseDouble(successList.get(0).get(Constants.LOGIN_COUNT).toString());
            } else {
                successCount = Double.parseDouble(String.valueOf(Constants.ZERO));
            }
            Double totalCount = successMap.stream()
                    .mapToDouble(e -> Double.parseDouble(e.get(Constants.LOGIN_COUNT).toString()))
                    .sum();
            double successRate = (double) successCount / totalCount * 100;
            map.put("successRate", decimalFormat.format(successRate) + "%");
            successMap.add(map);
        }
        return successMap;
    }

    /**
     * 登录失败原因分析
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public List<Map> getLoginFailureRate(CustomVO customVO) {
        log.info("查询登录失败原因分析,入参{}",customVO.toString());
        String userKey = SecurityUtils.getLoginUserId();
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> failCauseMap = userStatisticDAO.getLoginFailureRate(customVO);
        //如果是APP/小程序，不走下面代码
        if (StrUtil.isNotEmpty(userKey) && userKey.startsWith("web")) { // pc端用户
            if (!CollectionUtils.isEmpty(failCauseMap)) {
                BigDecimal totalFailNum = BigDecimal.ZERO;
                for (Map<String, Object> failCause : failCauseMap) {
                    BigDecimal failNum = new BigDecimal(failCause.get(Constants.FAIL_CAUSE_NUM).toString());
                    totalFailNum = totalFailNum.add(failNum);
                    failCause.put(Constants.FAIL_CAUSE_NUM, failNum);
                }
                if (totalFailNum.compareTo(BigDecimal.ZERO) != Constants.ZERO) {
                    BigDecimal finalTotalFailNum = totalFailNum;
                    failCauseMap = failCauseMap.stream()
                            .collect(Collectors.groupingBy(e -> e.get("loginCause")))
                            .values()
                            .stream().filter(s -> s.get(0).get("loginCause") != null)
                            .map(d -> {
                                Map<String, Object> smpData = d.get(0);
                                smpData.put("failRatio", d.stream()
                                        .map(s -> new BigDecimal(s.get(Constants.FAIL_CAUSE_NUM).toString())
                                                .divide(finalTotalFailNum, 4, BigDecimal.ROUND_HALF_UP)
                                                .multiply(new BigDecimal(100))
                                                .setScale(2, BigDecimal.ROUND_HALF_UP)
                                                .toString() + "%")
                                        .findFirst()
                                        .orElse("0.00%"));
                                return smpData;
                            }).sorted(Comparator.comparing((Map<String, Object> m) -> new BigDecimal(m.get(Constants.FAIL_CAUSE_NUM).toString())).reversed())
                            .collect(Collectors.toList());
                }
            }
        }
        return failCauseMap;
    }

    /**
     * APP接口：用户登录方式分析
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public List<Map> getCustomNumByLoginStyle(CustomVO customVO) {
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> styleMap =  userStatisticDAO.getCustomNumByLoginStyle(customVO);
        if (!CollectionUtils.isEmpty(styleMap)) {
            //计算总数
            Double totalCount = styleMap.stream()
                    .mapToDouble(e -> Double.parseDouble(e.get(Constants.LOGIN_COUNT).toString()))
                    .sum();
            for (Map resultMap : styleMap) {
                DecimalFormat decimalFormat = new DecimalFormat("#");
                Double styleRate = Double.parseDouble(resultMap.get(Constants.LOGIN_COUNT).toString()) / totalCount * 100;
                resultMap.put(Constants.STYLE_RATE, decimalFormat.format(styleRate) + "%");
            }
        }
        return styleMap;
    }

    /**
     * APP端操作系统分析
     * @param customVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public List<Map> getAppSystemRate(CustomVO customVO) {
        CustomVO vo = new CustomVO();
        List<String> custom = getCustom(vo);
        customVO.setUserIdList(custom);
        List<Map> appSystemMap = userStatisticDAO.getLoginTypeByAppOs(customVO);
        List<Map> appList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(appSystemMap)) {
            //计算总数、IOS总数、Android总数
            Map<String, Integer> counts = appSystemMap.stream()
                    .filter(map -> !map.get(Constants.APP_OS_NAME).toString().equals("Unknown")) //过滤掉‘Unknown’的操作系统
                    .collect(Collectors.groupingBy(
                            map -> map.get(Constants.APP_OS_NAME).toString().contains("Android") ? Constants.ANDROID : Constants.IOS,
                            Collectors.summingInt(map -> Integer.parseInt(map.get(Constants.LOGIN_COUNT).toString()))
                    ));
            int totalCount = counts.values().stream().mapToInt(Integer::intValue).sum();
            int iosCount = counts.getOrDefault(Constants.IOS, Constants.ZERO);
            int androidCount = counts.getOrDefault(Constants.ANDROID, Constants.ZERO);
            //构造结果
            DecimalFormat decimalFormat = new DecimalFormat("#");
            Map<String, Object> iosMap = new HashMap<>();
            iosMap.put(Constants.APP_OS_NAME, Constants.IOS);
            iosMap.put(Constants.LOGIN_COUNT, iosCount);
            iosMap.put(Constants.STYLE_RATE, decimalFormat.format((double) iosCount / totalCount * 100) + "%");
            Map<String, Object> andMap = new HashMap<>();
            andMap.put(Constants.APP_OS_NAME, Constants.ANDROID);
            andMap.put(Constants.LOGIN_COUNT, androidCount);
            andMap.put(Constants.STYLE_RATE, decimalFormat.format((double) androidCount / totalCount * 100) + "%");
            appList.add(iosMap);
            appList.add(andMap);
        }
        return appList;
    }


    /**
     * 查询权限下所有用户
     *
     * @param customVO
     * @return Result
     * @throws
     */
    public List<String> getCustom(CustomVO customVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        String deptId = loginUser.getUser().getDeptId();
        customVO.setDeptId(deptId);
        customVO.setLocalNode(platformCode);
        List<String> custom = userStatisticDAO.getCustom(customVO);
        return custom;
    }
}
