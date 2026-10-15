package com.chinatower.device.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.ChooseLocationResDto;
import com.chinatower.device.dto.ChooseLocationThreeDto;
import com.chinatower.device.dto.ChooseLocationTwoDto;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ChooseLocationController {


    private final String EXT = "xls,xlsx";


    private final String ERROR_MSG = "导入失败，请核对模板及填写数据";

    private final String IMPORT_MSG = "导入失败";

    private final Double MAX_LON = 180.0;

    private final Double MIN_LON = -180.0;

    private final Double MAX_LAT = 90.0;

    private final Double MIN_LAT = -90.0;

    public static void main(String[] args) {
        boolean integer = NumberUtil.isInteger("1231.123");
        System.out.println(integer);
    }

    /**
     *
     * @param file 文件
     * @param importType
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "选址导入")
    @PostMapping("/location/import")
    public Result<ChooseLocationResDto> importData(MultipartFile file, int importType) throws Exception {
        // 获取文件后缀名
        String fileExtension = FileUtil.extName(file.getOriginalFilename());

        List<ChooseLocationThreeDto> successList = new ArrayList<>();

        List<Integer> errList = new ArrayList<>();

        if (null != fileExtension) {
            if (!EXT.contains(fileExtension)) {
                throw new CustomException("仅允许导入“xls”或“xlsx”格式文件。");
            }
        }
        List<ChooseLocationThreeDto> list;
        if (2 == importType) {
            list = new ExcelUtil<>(ChooseLocationThreeDto.class).importExcel(file.getInputStream());
            log.info("++:{}", JSON.toJSONString(list));
            list.forEach(item -> {
                if (item.getHeight() != null) {
                    throw new CustomException(IMPORT_MSG);
                }
            });
        } else {
            list = new ExcelUtil<>(ChooseLocationThreeDto.class).importExcel(file.getInputStream());
        }
        if (CollUtil.isEmpty(list)) {
            throw new CustomException("导入数据不能为空");
        }
        if (list.size() > 100) {
            throw new CustomException("导入数据不可超过100条");
        }
        for (int i = 0; i < list.size(); i++) {
            ChooseLocationThreeDto item = list.get(i);

            String err = null;

            if (item.getLon() == null || item.getLat() == null || item.getRadius() == null) {
                err = ERROR_MSG;
            }

            if (3 == importType) {
                if (item.getLon() == null || item.getLat() == null || item.getRadius() == null|| item.getHeight() == null) {
                    err = ERROR_MSG;
                }
                if (null == item.getHeight() || item.getHeight() <= 0 || item.getHeight() > 1000) {
                    err = ERROR_MSG;
                } else {
                    item.setHeight(Double.parseDouble(NumberUtil.round(item.getHeight(), 2).toString()));
                }
            }
            if (null != item.getLon()) {
                item.setLon(Double.parseDouble(NumberUtil.round(item.getLon(), 6).toString()));
                if (item.getLon() < MIN_LON || item.getLon() > MAX_LON) {
                    err = ERROR_MSG;
                }
            }
            if (null != item.getLat()) {
                item.setLat(Double.parseDouble(NumberUtil.round(item.getLat(), 6).toString()));
                if (item.getLat() < MIN_LAT || item.getLat() > MAX_LAT) {
                    err = ERROR_MSG;
                }
            }
            if (null == item.getRadius() || !NumberUtil.isInteger(item.getRadius().toString()) || item.getRadius() <= 0 || item.getRadius() > 50000) {
                err = ERROR_MSG;
            }
            if (null != err) {
                errList.add(i + 2);
            } else {
                successList.add(item);
            }
        }
        if (successList.isEmpty()) {
            throw new CustomException(IMPORT_MSG);
        }
        ChooseLocationResDto chooseLocationResDto = new ChooseLocationResDto();
        chooseLocationResDto.setSuccessList(successList);
        chooseLocationResDto.setErrList(errList);
        return Result.data(chooseLocationResDto);
    }
}
