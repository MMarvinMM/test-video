package com.chinatower.platform.controller;


import com.chinatower.common.core.dto.CheckpointAlarmDTO;
import com.chinatower.common.core.vo.CheckpointAlarmExportVO;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.service.ICheckpointAlarmRepository;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * video-export
 * @Author：zl
 * @Description：出入口管理接口
 * @Date： 2022/8/10
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__出入口管理
 */
@RestController
@Slf4j
@RequestMapping(value = "/alarm/checkpoint")
public class CheckpointAlarmController extends TowerBaseController {

    @Autowired
    private ICheckpointAlarmRepository checkpointAlarmRepository;

    private static final int EXCEL_EXPORT_LIMIT = 5000;


    /**
     *
     * 导出车辆进出记录
     * 功能描述 导出车辆进出记录
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 13:34
     * @version:
     */
    @ApiOperation(value = "导出查询车辆进出记录")
    @PostMapping("/exportCheckpointAlarmList")
    public Result<String> exportCheckpointAlarmList(@RequestBody CheckpointAlarmDTO dto, HttpServletResponse response) {
        List<CheckpointAlarmExportVO> list = checkpointAlarmRepository.exportCheckpointAlarmList(dto);
        int total = list.size();
        if(total > EXCEL_EXPORT_LIMIT){
            return Result.fail("数据量过多，请按查询条件导出!");
        } else {
            byte[] data =new ExcelUtil<>(CheckpointAlarmExportVO.class).exportBytes(list);
            FileUtils.download(response, data);
            return Result.success("导出车辆进出记录成功!");
        }
    }
}
