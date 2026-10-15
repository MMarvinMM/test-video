package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.service.ObsClientService;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.project.system.domain.SysAlarmType;
import com.chinatower.platform.project.system.domain.vo.SysAlarmTypeVO;
import com.chinatower.platform.project.system.service.ISysAlarmTypeTTViewService;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * video-system
 *
 */
@Slf4j
@RestController
@RequestMapping("/ttviewNew/system/alarmType")
public class SysAlarmTypeTTViewController extends TowerBaseController {

    @Autowired
    private ISysAlarmTypeTTViewService sysAlarmTypeTTViewService;

    @Autowired
    private ObsClientService obsClientService;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 查询所有的告警类型
     * @param sysAlarmTypeVO
     * @return
     */
    @PostMapping("/list")
    @DataScope(tenantColumn = "u.tenantor")
    public TableDataInfo getAlarmsTypeList(@RequestBody SysAlarmTypeVO sysAlarmTypeVO) {
        PageHelper.startPage(sysAlarmTypeVO.getPageNum(), sysAlarmTypeVO.getPageSize());
        List<SysAlarmType> sysAlarmTypeList = sysAlarmTypeTTViewService.getAlarmsTypeList(sysAlarmTypeVO);
        //整改for循环 modify by liuxt 20240806
        if(!CollectionUtils.isEmpty(sysAlarmTypeList)){
            Set<String> fileUrls = sysAlarmTypeList.stream().map(SysAlarmType::getIcon).collect(Collectors.toSet());
            if(!fileUrls.isEmpty()){
                // 处理Obs文件URL加密返回
                Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(fileUrls));
                String fileUrl = "";
                for (SysAlarmType f : sysAlarmTypeList) {
                    fileUrl = f.getIcon();
                    if (!StringUtils.isEmpty(fileUrl)) {
                        f.setIcon(result.get(fileUrl));
                    }
                }
            }
        }
        return getDataTable(sysAlarmTypeList);
    }

    /**
     * 新增告警类型
     * @param sysAlarmTypeVOList
     * @return
     */
    @PostMapping("/addList")
    public Result addAlarmsTypeList(@RequestBody List<SysAlarmTypeVO> sysAlarmTypeVOList) {
        return sysAlarmTypeTTViewService.addAlarmsTypeList(sysAlarmTypeVOList);
    }

    /**
     * 修改告警类型
     * @param sysAlarmTypeVO
     * @return
     */
    @PostMapping("/updateAlarmType")
    public Result updateAlarmsType(@RequestBody SysAlarmTypeVO sysAlarmTypeVO) {
        return sysAlarmTypeTTViewService.updateAlarmsType(sysAlarmTypeVO);
    }

    /**
     * 删除告警类型   0正常 1删除
     * @param sysAlarmTypeVO
     * @return
     */
    @PostMapping("/removeAlarmType")
    public Result removeAlarmType(@RequestBody SysAlarmTypeVO sysAlarmTypeVO) {
        if (sysAlarmTypeVO.getAlarmTypeId() == null) {
            Result.fail("告警类型ID不能为空");
        }
        sysAlarmTypeTTViewService.removeAlarmType(sysAlarmTypeVO);
        return Result.success("删除摄像机型号成功");
    }

    /**
     * 上传告警类型图标svg图片接口
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<SysAlarmTypeVO> uploadAlarmIcon(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }
        SysAlarmTypeVO sysAlarmTypeVO = new SysAlarmTypeVO();
        try {
            //上传图片
            String url = obsClientService.uploadFile(file);
            sysAlarmTypeVO.setIcon(url);
        } catch (Exception e) {
            log.info("========上传告警类型图标失败", e);
            return Result.fail("上传失败");
        }
        return Result.data(sysAlarmTypeVO);
    }


    /**
     * 查询名称重复
     * @param sysAlarmTypeVO
     * @return
     */
    @PostMapping("/checkAlarmsName")
    public Result checkAlarmsName(@RequestBody SysAlarmTypeVO sysAlarmTypeVO) {
        if (sysAlarmTypeVO.getTypeName() == null) {
            Result.fail("告警名称不能为空");
        }
        return sysAlarmTypeTTViewService.checkAlarmsName(sysAlarmTypeVO);
    }

}
