package com.chinatower.video.api;

import java.util.List;

import com.chinatower.common.page.TableDataInfo;
import com.chinatower.video.modules.artags.dataobject.*;
import com.chinatower.video.modules.query.entity.ArTagFilterVO;
import com.chinatower.video.modules.query.entity.ArTagMoveRes;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.ArTagsInfoReq;

/**
 * @ClassName: ArTagsInfoCustomAPI
 * @Description: 查询功能相关接口
 * @Author: songcl
 * @Date: 2023/4/15
 **/
public interface ArTagsInfoCustomAPI {

    @PostMapping("/arTagsInfo/addOrUpdate")
    Result<Long> addOrUpdate(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq);

    @PostMapping("/arTagsInfo/listTree")
    Result<List<ArTagTree>> listTree(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq);

    @PostMapping("/arTagsInfo/list")
    Result<List<ArDeviceInfo>> list(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq);
    @PostMapping("/arTagsInfo/pageList")
    Result<TableDataInfo> pageList(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq);

    @PostMapping("/arTagsInfo/delete")
    Result<String> delete(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq);

    @PostMapping("/arTagsInfo/addMark")
    Result<String> addMark(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq);

    @PostMapping("/arTagsInfo/addMarks")
    Result<String> addMarks(@Validated @RequestBody List<ArTagsInfoReq> arTagsInfoReqList);

    @PostMapping("/arTagsInfo/delMark")
    Result<String> delMark(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq);

    @PostMapping("/arTagsInfo/delMarks")
    Result<String> delMarks(@Validated @RequestBody List<ArTagsInfoReq> arTagsInfoReqList);

    @PostMapping("/arTagsInfo/weatherInfoList")
    Result weatherInfoList(ArDeviceDTO arDeviceDTO);

    @ApiOperation(value = "查询AR标签类型树", notes = "查询AR标签类型树")
    @GetMapping("/arTagsInfo/arTagsTypeTree")
    Result<List<BusinessDictType>> arTagsTypeTree();

    @ApiOperation(value = "查询事件类型树", notes = "查询AR标签类型树")
    @GetMapping("/arTagsInfo/arTagsTypeTreeForEvent/{dictType}")
    Result<List<BusinessDictType>> arTagsTypeTreeForEvent(@PathVariable("dictType") Integer dictType);

    @PostMapping("/arTagsInfo/batchAddLabelIcon")
    Result<String> batchAddLabelIcon(@RequestBody ArLabelIconGallery arLabelIconGallery);

    @PostMapping("/arTagsInfo/getLabelIconList")
    Result<List<ArLabelIconGallery>> getLabelIconList(@RequestBody ArLabelIconGallery arLabelIconGallery);

    @PostMapping("/arTagsInfo/delLabelIcon")
    Result<String> delLabelIcon(@RequestBody ArLabelIconGallery arLabelIconGallery);

    @PostMapping("/arTagsInfo/checkLabelIcon")
    Result<Boolean> checkLabelIcon(@RequestBody ArLabelIconGallery arLabelIconGallery);

    @PostMapping("/arTagsInfo/listForCruise")
    Result<List<ArTagMoveRes>> listForCruise(@RequestBody ArTagFilterVO arTagFilterVO);

    @PostMapping("/arTagsInfo/addMapTags")
    Result<String> addMapTags(@Validated @RequestBody List<ArTagsInfoReq> arTagsInfoReqList);

    @PostMapping("/arTagsInfo/queryMapAllTags")
    Result<List<ArTagMoveRes>> queryMapAllTags(@RequestBody ArTagFilterVO tagFilterVO);

    @PostMapping(" /arTagsInfo/delMapTags")
    Result<String> delMapTags(@Validated @RequestBody List<ArTagsInfoReq> arTagsInfoReqList);

    @PostMapping(" /arTagsInfo/queryTagLocation")
    Result<List<TagLocationVO>> queryTagLocation(@RequestBody TagLocationVO arTagsInfoReq) ;

}
