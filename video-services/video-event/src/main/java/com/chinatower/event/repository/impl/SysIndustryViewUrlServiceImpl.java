package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.dto.SysLinkViewUrlModel;
import com.chinatower.event.mapper.SysIndustryViewUrlMapper;
import com.chinatower.event.repository.ISysIndustryViewUrlService;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 行业大屏关联业务接口实现
 * @author chmod
 * @version 1.0
 * @date 2022/2/10 09:10
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class SysIndustryViewUrlServiceImpl implements ISysIndustryViewUrlService {
    @Autowired
    private SysIndustryViewUrlMapper sysIndustryViewUrlMapper;

    @Override
    public List<SysLinkViewUrlModel> querySysLinkViewUrlAll() {
        return sysIndustryViewUrlMapper.querySysLinkViewUrlAll();
    }
}
