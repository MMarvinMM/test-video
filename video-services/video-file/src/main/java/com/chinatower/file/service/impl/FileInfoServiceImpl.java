package com.chinatower.file.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.chinatower.file.vo.SysFileInfo;
import com.chinatower.file.mapper.SysFileInfoMapper;
import com.chinatower.file.service.FileInfoService;
import com.chinatower.file.tool.FileInfoObsUrlDeal;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileInfoServiceImpl implements FileInfoService {

    @Autowired
    private SysFileInfoMapper sysFileInfoMapper;

    @Autowired
    private FileInfoObsUrlDeal fileInfoObsUrlDeal;


    //视联上传文档的创建人
    private static final String VIDEO_CREATE_BY = "video";

    /**
     * 获取序列id
     *
     * @param name   序列名
     * @param number 数目
     * @return 序列id
     */
    @Override
    public String getSeqByName(String name, String number) {
        List result = new ArrayList<String>();
        for (int i = 0; i < Integer.parseInt(number); i++) {
            result.add(i);
        }
        List resultData = sysFileInfoMapper.getSeqByNameList(name,result);
        String str = String.join(",", resultData);
        return str;
    }
}
