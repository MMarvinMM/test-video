package com.chinatower.platform.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.enums.ClientType;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.sql.SqlUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.api.LogFeign;
import com.chinatower.platform.api.dto.SysLogininfoDTO;
import com.chinatower.platform.api.dto.SysOperLogDTO;
import com.chinatower.platform.api.entity.SysLogininfoEntity;
import com.chinatower.platform.api.entity.SysOperLogEntity;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.EbikeDataBaseConstant;

import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysDictType;
import com.chinatower.platform.entity.export.SysConfigDto;
import com.chinatower.platform.entity.export.SysFileInfo;
import com.chinatower.platform.mapper.SysAreaMapper;
import com.chinatower.platform.service.AdminExportService;
import com.chinatower.platform.utils.ExportModuleEnums;
import com.chinatower.platform.utils.SqlUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Range;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * video-export
 * 功能说明: admin模块导出功能类
 *
 * @author 李峥
 * @date 2021-12-03 16:44
 * @requestID
 */
@RefreshScope
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("admin")
public class AdminExportController extends TowerBaseController {
    //每次最大查询条数
    @Value("${page.maxPageSize:5000}")
    private int maxPageSize;
    //最大条数超过后异步处理导出
    @Value("${export.maxPageSize:10000}")
    public int exportMaxSize;

    @Value("${maxPageSize.oracle:5000}")
    private Integer maxPageSizeOracle;

    @Autowired
    private LogFeign logFeign;

    private final AdminExportService adminExportService;

    /**
     * 导出区域信息列表
     * @param sysArea
     */
    @PostMapping("/sysArea")
    public void exportSysArea(@RequestBody SysArea sysArea) {
        PageMethod.startPage(1, maxPageSize);
        List<SysArea> list = adminExportService.selectSysAreaList(sysArea);
        PageInfo<SysArea> pageInfo = new PageInfo<>(list);
        /*
         *  R-ZBZB-202109280789715 web-admin拆分改造 渠道信息列表导出改造
         *  导出条数超过一定数量，则走export逻辑
         *  20210930
         *  卿燚
         * */
        if (pageInfo.getTotal() > exportMaxSize) {
            SqlUtils.getSql(SysAreaMapper.class.getName() + ".selectSysAreaList",
                    sysArea,
                    ExportModuleEnums.EXPORT_SYS_AREA.getKey(),
                    SysArea.class.getName(),
                    null, EbikeDataBaseConstant.EBIKE_ADMIN);
        } else {
            if (pageInfo.getPages() > 1) {
                for (int i = 2; i <= pageInfo.getPages(); i++) {
                    PageMethod.startPage(i, maxPageSize);
                    list.addAll(adminExportService.selectSysAreaList(sysArea));
                }
            }
            byte[] data = new ExcelUtil<>(SysArea.class).exportBytes(list);
            FileUtils.download(getResponse(), data);
        }
    }

    /**
     * 导出字典类型
     * @param dictType
     * @param response
     */
    @ApiOperation(value = "导出字典类型")
    @TowerLog(title = "字典类型", businessType = BusinessType.EXPORT)
    @PostMapping("/system/dict/type/export")
    public void export(@RequestBody SysDictType dictType, HttpServletResponse response)
    {
        List<SysDictType> list = adminExportService.selectDictTypeList(dictType);
        byte[] data =new ExcelUtil<>(SysDictType.class).exportBytes(list);
        FileUtils.download(response, data);
    }

    /**
     * 导出参数管理
     * @param config
     * @param response
     */
    @ApiOperation(value = "导出参数管理")
    @TowerLog(title = "参数管理", businessType = BusinessType.EXPORT)
    @PostMapping("/system/config/export")
    public void export(@RequestBody SysConfigDto config, HttpServletResponse response) {
        List<SysConfigDto> list = adminExportService.exportConfigList(config);
        byte[] data =new ExcelUtil<>(SysConfigDto.class).exportBytes(list);
        FileUtils.download(response, data);
    }

    /**
     * 导出登陆日志
     * @param logininfor
     * @param response
     */
    @ApiOperation(value = "导出登陆日志")
    @TowerLog(title = "登陆日志", businessType = BusinessType.EXPORT)
    @PostMapping("/monitor/logininfor/export")
    public void exportLoginInfo(@RequestBody SysLogininfoDTO logininfor, HttpServletResponse response) {
        List<SysLogininfoEntity> result = new ArrayList<>();
        Result<List<SysLogininfoEntity>> resultDto = logFeign.exportList(logininfor);
        if (resultDto != null && resultDto.getCode() == ResultCode.SUCCESS.getCode()){
            List<SysLogininfoEntity> list = resultDto.getData();
            list.forEach(dto -> {
                if (StringUtils.isNotEmpty(dto.getOsVersion())){
                    dto.setOs(dto.getOs() + " " + dto.getOsVersion());
                }
                if (ClientType.WX_MANAGE.getClientType().equals(dto.getClientType())
                        ||ClientType.APP_MANAGE.getClientType().equals(dto.getClientType())){
                    dto.setBrowser("");
                    dto.setBrowserVersion("");
                }
                if (!ClientType.APP_MANAGE.getClientType().equals(dto.getClientType())){
                    dto.setAppVersion("");
                }
            });
            copy(result,list);
            byte[] data =new ExcelUtil<>(SysLogininfoEntity.class).exportBytes(result);
            FileUtils.download(response, data);
        }else {
            if(resultDto != null){
                throw new CustomException(resultDto.getMsg());
            }else {
                throw new CustomException("导出失败");
            }

        }


    }

    private void copy(List<SysLogininfoEntity> result , List<SysLogininfoEntity> list){
        if(null == list || list.isEmpty()){
            return;
        }
        JSON.parseArray(JSON.toJSONString(list)).forEach(s->{
            SysLogininfoEntity e = JSON.parseObject(s.toString(), SysLogininfoEntity.class);
            result.add(e);
        });
    }

    /**
     * 导出操作日志
     * @param operLog
     * @param response
     */
    @ApiOperation(value = "导出操作日志")
    @TowerLog(title = "操作日志", businessType = BusinessType.EXPORT)
    @PostMapping("/monitor/operlog/export")
    public void exportOperLog(@RequestBody SysOperLogDTO operLog, HttpServletResponse response) {
        log.info("导出操作日志operLog开始:{}", operLog);
        Result<List<SysOperLogEntity>> resultDto = logFeign.exportList(operLog);
        log.info("导出操作日志operLog结束");
        if (resultDto != null && resultDto.getCode() == ResultCode.SUCCESS.getCode()){
            List<SysOperLogEntity> list = resultDto.getData();
            byte[] data =new ExcelUtil<>(SysOperLogEntity.class).exportBytes(list);
            FileUtils.download(response, data);
        }else {
            if(resultDto != null){
                throw new CustomException(resultDto.getMsg());
            }else {
                throw new CustomException("导出失败");
            }
        }
    }

}
