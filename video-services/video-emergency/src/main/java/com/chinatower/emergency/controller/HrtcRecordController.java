package com.chinatower.emergency.controller;


import com.chinatower.common.entity.Result;
import com.chinatower.emergency.dto.HrtcRecordVo;
import com.chinatower.emergency.service.IHrtcRecordService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.AuthObjDataVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * video-emergency
 * 融合通信hrtc 记录表
 */
@RestController
public class HrtcRecordController {

    @Resource
    IHrtcRecordService hrtcRecordService;

    @Resource
    SystemFeignClient systemFeignClient;
    /**
     * 记录融合通信相关数据
     * 会商 记录会商id，发起人id，开始时间
     * 对讲 记录对讲id，静音人id
     * @param list
     * @return
     */
    @PostMapping("/htrc/record/addBatch")
    public Result<String> addBatch(@RequestBody List<HrtcRecordVo> list) {
        hrtcRecordService.addBatch(list);
        return Result.success("请求成功");
    }

    @PostMapping("/htrc/record/update")
    public Result<String> update(@RequestBody HrtcRecordVo vo) {
        hrtcRecordService.update(vo);
        return Result.success("请求成功");
    }

    /**
     * 根据id查询记录信息
     * @param vo
     * @return
     */
    @PostMapping("/htrc/record/findById")
    public Result<HrtcRecordVo> findById(@RequestBody HrtcRecordVo vo) {
        return Result.data(hrtcRecordService.findById(vo));
    }


    /**
     * 根据id删除记录信息
     * @param vo
     * @return
     */
    @PostMapping("/htrc/record/delete")
    public Result<String> delete(@RequestBody HrtcRecordVo vo) {
        hrtcRecordService.delete(vo);
        return Result.success("请求成功");
    }

    @PostMapping("/test")
    public Result<AuthObjDataVO> test() throws Exception {
        return systemFeignClient.getDataScopeObj();
    }
}
