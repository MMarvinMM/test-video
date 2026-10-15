package com.chinatower.video.modules.recordDownload.controller;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.mongodb.request.Page;
import com.chinatower.video.api.entity.RecordDownloadDTO;
import com.chinatower.video.api.entity.RecordDownloadVO;
import com.chinatower.video.modules.recordDownload.service.IRecordDownloadService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.chinatower.common.core.utils.StringUtils.getDataTable;

/**
 * 录像下载 控制器
 */
@RestController
@RequestMapping("/recordDownload")
public class RecordDownloadController {


    @Resource
    IRecordDownloadService recordDownloadService;

    /**
     * 批量保存
     * @param list list
     * @return String
     */
    @PostMapping("/batchSave")
    Result<String> batchSave(@RequestBody List<RecordDownloadDTO> list){
        recordDownloadService.batchSave(list);
        return Result.success("新增成功");
    }

    /**
     * 查看下载完成列表
     * @param recordDownload recordDownload
     * @return 列表
     */
    @PostMapping("/list")
    Result<TableDataInfo> list(@RequestBody RecordDownloadVO recordDownload) {
        Page<RecordDownloadDTO> page = recordDownloadService.list(recordDownload);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(page.getList());
        rspData.setMsg("查询成功");
        rspData.setTotal(page.getCount());
        return Result.data(rspData);
    }

    /**
     * 下载完成删除
     * @param download id
     * @return String
     */
    @PostMapping("/delete")
    Result<String> delete(@RequestBody RecordDownloadDTO download) {
        recordDownloadService.delete(download.getId());
        return Result.success("删除成功");
    }
}
