package com.chinatower.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.vo.CommunicationVo;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.event.api.CommunicationService;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author WLF
 * @description
 * @date 2024/10/21 11:21
 */
@RestController
@RequestMapping("/communicationExport")
@Slf4j
public class CommunicationExportController {
    @Resource
    private CommunicationService communicationService;

    /**
     * 导出融合通信数据
     * @param communicationVo
     * @param response
     * @throws Exception
     */
    @PostMapping("/exportInfoList")
    public void export(@RequestBody CommunicationVo communicationVo, HttpServletResponse response) throws Exception {
        TableDataInfo tableDataInfo = communicationService.queryList(communicationVo);
        List<Map> list = (List<Map>) tableDataInfo.getRows();
        JSONArray array = JSON.parseArray(JSONArray.toJSONString(list));
        List<CommunicationVo> communicationVos =array.toJavaList(CommunicationVo.class);
        if (communicationVos.size() > 5000) {
            throw new CustomException("导出数据量最多5000条，请按照查询条件导出");
        }
        byte[] data = new ExcelUtil<>(CommunicationVo.class).exportBytes(communicationVos);
        FileUtils.download(response, data);
    }
}
