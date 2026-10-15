package com.chinatower.device.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.service.IVideoCheckpointDeviceMgmtInfoService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceExportVO;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * video-device
 * @Author：zl
 * @Description：卡口管理接口
 * @Date： 2022/8/10
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__卡口管理
 */
@RestController
@Slf4j
@Api(value = "卡口设备相关信息查询模块",tags = "卡口设备相关信息查询模块")
public class CheckpointDeviceMgmtController extends TowerBaseController {
    
    @Autowired
    private IVideoCheckpointDeviceMgmtInfoService videoCheckpointDeviceMgmtInfoService;

    private static final int EXCEL_EXPORT_LIMIT = 5000;

    /**
     * 分页查询卡口设备信息
     * 根据条件分页查询卡口设备信息  增加industryCode appVerCode
     * @Author：zl
     * @param checkpointDeviceInfoVO
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @PostMapping("/video/device/queryCheckpointDeviceInfoPage")
    public TableDataInfo queryCheckpointDeviceInfoPage(@RequestBody CheckpointDeviceInfoVO checkpointDeviceInfoVO) {
        List<CheckpointDeviceInfoVO> list = videoCheckpointDeviceMgmtInfoService.queryCheckpointDeviceInfoPage(checkpointDeviceInfoVO);
        return getDataTable(list);
    }

    /**
     * 新增卡口设备信息
     * 功能描述：新增卡口设备信息  增加industryCode appVerCode
     *
     * @Author：zl
     * @param channelCodeList
     * @return Result<String>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @PostMapping("/video/device/addCheckpointDeviceInfo")
    public Result<String> addCheckpointDeviceInfo(@RequestBody List<String> channelCodeList) {
        int num = videoCheckpointDeviceMgmtInfoService.insertCheckpointDeviceInfo(channelCodeList);
        if(num > -1){
            return Result.success("新增卡口设备成功!");
        }else{
            return Result.fail("新增卡口设备失败,无对应设备通道!");
        }

    }

    /**
     * 删除卡口设备相关信息
     * @Author：zl
     * @param idList
     * @return Result<String>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @PostMapping("/video/device/removeCheckpointDeviceInfo")
    public Result<String> removeCheckpointDeviceInfo(@RequestBody List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw new CustomException("非法的卡口设备通道编码");
        }
        CheckpointDeviceInfoVO checkpointDeviceInfoVO = new CheckpointDeviceInfoVO();
        checkpointDeviceInfoVO.setIds(idList);
        //根据idList查询出设备编码list
        List<CheckpointDeviceInfoVO> checkpointDeviceInfoVOList = videoCheckpointDeviceMgmtInfoService.queryCheckpointDeviceInfo(checkpointDeviceInfoVO);
        if(!CollectionUtils.isEmpty(checkpointDeviceInfoVOList)){
            List<String> deviceCodeList = checkpointDeviceInfoVOList.stream().map(CheckpointDeviceInfoVO::getDeviceCode).collect(Collectors.toList());
            checkpointDeviceInfoVO.setDeviceCodeList(deviceCodeList);
        }
        if(!CollectionUtils.isEmpty(checkpointDeviceInfoVO.getDeviceCodeList())){
            checkpointDeviceInfoVO.setIds(null);
            videoCheckpointDeviceMgmtInfoService.deleteCheckpointDeviceInfoBatch(checkpointDeviceInfoVO);
        }else{//防止删除整个表数据
            throw new CustomException("要删除的卡口设备编码不能为空");
        }
        return Result.success("删除卡口设备成功!");
    }

    /**
     * 查询租户设备通道树
     *
     * @Author：zl
     * @return Result<List<TreeSelect>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @PostMapping(value = "/video/device/getCheckpointDeviceTree")
    public Result<List<TreeSelect>> getCheckpointDeviceTree(){
        CheckpointDeviceInfoVO vo = new CheckpointDeviceInfoVO();
        return videoCheckpointDeviceMgmtInfoService.getCheckpointDeviceTree(vo);
    }


}
