package com.chinatower.platform.project.system.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.URLDecoder;
import com.chinatower.common.core.utils.sql.SqlUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.common.utils.security.RsaUtils;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.service.ISysUserWhiteListService;
import com.chinatower.platform.service.ISysImportService;
import com.chinatower.platform.utils.AESUtil;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.SysUserWhiteListVO;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * video-system
 *
 */
@Api(tags = "登录账号验证白名单")
@Slf4j
@RestController
@RequestMapping("/system/whiteList")
public class SysUserWhiteListController extends TowerBaseController {
    @Resource
    private ISysUserWhiteListService iSysUserWhiteListService;
    @Resource
    ISysImportService importService;

    /**
     * 根据登录名获取用户详细信息
     */
    @ApiOperation(value = "根据登录名获取用户详细信息",notes = "根据登录名获取用户详细信息")
    @GetMapping("/getUser")
    public Result<SysUserWhiteListVO> getUser(String userName,String decryptFlag){
        log.info("根据登录名获取用户详细信息入参：用户名：{}解密标志：{}", userName, decryptFlag);
        //处理加密产生的空格问题
        userName = URLDecoder.decode(userName, StandardCharsets.UTF_8)
                .replace(" ", "+");
        Result<SysUserWhiteListVO> result;
        if(StringUtils.isNotBlank(decryptFlag) && "1".equals(decryptFlag)){
            //用户名解密
            String loginUserName = com.chinatower.platform.common.utils.security.AESUtil.decryptBase64(userName);
            result = iSysUserWhiteListService.getUser(loginUserName);
        }else{
            result = iSysUserWhiteListService.getUser(userName);
        }
        //手机号加密
        SysUserWhiteListVO vo;
        if (Objects.nonNull(vo = result.getData())) {
            String phoneNumber = vo.getPhoneNumber();
            String encryptPhone = AESUtil.encrypt(phoneNumber);
            vo.setPhoneNumber(null);
            vo.setAesPhoneNumber(encryptPhone);
            //用户名加密
            vo.setUserName( com.chinatower.platform.common.utils.security.AESUtil.encryptBase64((vo.getUserName())));
        }
        return result;
    }

    /**
     * 新增白名单
     */
    @ApiOperation(value = "新增白名单",notes = "新增白名单")
    @TowerLog(title = "白名单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public Result add(@RequestBody SysUserWhiteListVO vo){
        LoginUser currentUser = SecurityUtils.getLoginUser();
        vo.setCreateBy(currentUser.getUser().getUserId());
        return iSysUserWhiteListService.add(vo);
    }

    /**
     * 删除白名单
     */
    @ApiOperation(value = "删除白名单",notes = "删除白名单")
    @TowerLog(title = "白名单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public Result delete(@RequestBody List<String> ids){
        return iSysUserWhiteListService.delete(ids);
    }

    /**
     * 白名单列表
     */
    @ApiOperation(value = "列表",notes = "列表")
    @GetMapping("/list")
    public TableDataInfo list(SysUserWhiteListVO vo){

        //如果排序不为空
        if (StringUtils.isNotEmpty(vo.getOrderByColumn())){
            //且排序不符合校验,置空过滤
            if (!SqlUtil.isValidOrderBySql(vo.getOrderByColumn())) {
                vo.setOrderByColumn(null);
            }
        }
        //排序列字段入参合法性检查
        getAllFields(SysUserWhiteListVO.class,vo);

        PageHelper.startPage(vo.getPageNum(), vo.getPageSize(), vo.getOrderBy());
        return getDataTable(iSysUserWhiteListService.list(vo));
    }

    /**
     * 白名单导入
     */
    @ApiOperation(value = "白名单导入")
    @TowerLog(title = "白名单", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public Result<String> importData(@RequestParam("file") MultipartFile file, @RequestParam("importType") String importType, @RequestParam("fileUrl")String fileUrl) throws Exception {
        List<SysUserWhiteListVO> list = new ExcelUtil<>(SysUserWhiteListVO.class).importExcel(file.getInputStream());
        if (CollectionUtil.isEmpty(list)) {
            return Result.fail("导入数据为空");
        }
        list = list.stream().filter(a-> a != null).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(list)) {
            return Result.fail("导入数据为空");
        }
        ImportParams<SysUserWhiteListVO> params = new ImportParams<>(list, importType, file.getOriginalFilename(), fileUrl);
        params.put("loginUser",SecurityUtils.getLoginUser().getUser());
        return importService.autoThreadImport(params, p -> iSysUserWhiteListService.importData(p));
    }

    /**
     * 获取全部账号
     */
    @ApiOperation(value = "获取全部账号",notes = "获取全部账号")
    @GetMapping("/getUserList")
    public Result<List<String>> getUserList(){
        return iSysUserWhiteListService.getUserList();
    }

    //排序列字段入参合法性检查
    public static void getAllFields(Class<?> clazz,SysUserWhiteListVO vo) {
        List<Field> fieldList = Lists.newArrayList();
        while (clazz != null){
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        List<String> stringList = new ArrayList<>();
        for (Field Field : fieldList) {
            stringList.add(Field.getName());
        }
        String orderByColumn = vo.getOrderByColumn();
        if(StringUtils.isNotEmpty(orderByColumn)){
            if(!stringList.contains(orderByColumn)){
                vo.setOrderByColumn(null);
            }
        }
    }
}
