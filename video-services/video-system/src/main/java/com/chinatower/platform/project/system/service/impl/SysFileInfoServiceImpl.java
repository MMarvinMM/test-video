package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.project.system.domain.SysFileInfo;
import com.chinatower.platform.project.system.mapper.SysFileInfoMapper;
import com.chinatower.platform.project.system.service.ISysFileInfoService;
import com.chinatower.platform.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件信息Service业务层处理
 *
 * @author wangzic
 * @date 2020-04-03
 */
@Service
public class SysFileInfoServiceImpl implements ISysFileInfoService {
//    @Autowired
//    private SysFileInfoMapper sysFileInfoMapper;
//    //视联上传文档的创建人
//    private static final String VIDEO_CREATE_BY = "video";
//    @Autowired
//    private FileUrlEncryptUtil fileUrlEncryptUtil;
//
//    /**
//     * 查询文件信息
//     *
//     * @param fileId 文件信息ID
//     * @return 文件信息
//     */
//    @Override
//    public SysFileInfo selectSysFileInfoById(String fileId) {
//        SysFileInfo sysFileInfo = sysFileInfoMapper.selectSysFileInfoById(fileId);
//        if(sysFileInfo!=null ){
//            sysFileInfo.setFileUrl(fileUrlEncryptUtil.dealObsFileUrl(AESUtil.decryptHttp(sysFileInfo.getFileUrl())));
//            // 处理Obs文件URL加密返回
////            obsFileUrlEncryptDeal.dealObsFileUrl(sysFileInfo);
//        }
//
//        return sysFileInfo;
//    }
//
//    /**
//     * 查询文件信息列表
//     *
//     * @param sysFileInfo 文件信息
//     * @return 文件信息
//     */
//    @Override
//    public List<SysFileInfo> selectSysFileInfoList(SysFileInfo sysFileInfo) {
//        List<SysFileInfo> sysFileInfos = sysFileInfoMapper.selectSysFileInfoList(sysFileInfo);
//        sysFileInfos.forEach(info-> info.setFileUrl(AESUtil.decryptHttp(info.getFileUrl())));
//        return sysFileInfos;
//    }
//
//    /**
//     * 新增文件信息
//     *
//     * @param sysFileInfo 文件信息
//     * @return 结果
//     */
//    @Override
//    public int insertSysFileInfo(SysFileInfo sysFileInfo) {
//        if(StrUtil.isBlank(sysFileInfo.getFileId())){
//            sysFileInfo.setFileId(IdUtil.simpleUUID());
//        }
//        sysFileInfo.setCreateTime(new Date());
//        sysFileInfo.setCreator(SecurityUtils.getUsername());
//        if(SecurityUtils.getLoginUser() != null){
//            sysFileInfo.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
//        }
//
//        sysFileInfo.setFileUrl(AESUtil.encryptHttp(sysFileInfo.getFileUrl()));
//        return sysFileInfoMapper.insertSysFileInfo(sysFileInfo);
//    }
//
//    /**
//     * 修改文件信息
//     *
//     * @param sysFileInfo 文件信息
//     * @return 结果
//     */
//    @Override
//    public int updateSysFileInfo(SysFileInfo sysFileInfo) {
//        sysFileInfo.setUpdateTime(new Date());
//        sysFileInfo.setFileUrl(AESUtil.encryptHttp(sysFileInfo.getFileUrl()));
//        return sysFileInfoMapper.updateSysFileInfo(sysFileInfo);
//    }
//
//    @Override
//    public List<SysFileInfo> selectSysFileInfoByIds(String[] fileIds) {
//        List<SysFileInfo> sysFileInfos =sysFileInfoMapper.selectSysFileInfoByIds(fileIds);
//        sysFileInfos.forEach(info->info.setFileUrl(AESUtil.decryptHttp(info.getFileUrl())));
//        // 处理Obs文件URL加密返回
//        Set<String> collect = sysFileInfos.stream().filter(e-> StringUtils.isNotEmpty(e.getFileUrl())).map(SysFileInfo::getFileUrl).collect(Collectors.toSet());
//        Map<String, String> urlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(collect));
//        for (SysFileInfo sysFileInfo : sysFileInfos){
//            if (StringUtils.isNotEmpty(sysFileInfo.getFileUrl())){
//                sysFileInfo.setFileUrl(urlMap.get(sysFileInfo.getFileUrl()));
//            }
//        }
//        return sysFileInfos;
//    }
//
//    @Override
//    public List<SysFileInfo> selectToEncryptFile() {
//        return sysFileInfoMapper.selectToEncryptFile();
//    }
//
//    /**
//     * 批量删除文件信息
//     *
//     * @param fileIds 需要删除的文件信息ID
//     * @return 结果
//     */
//    @Override
//    public int deleteSysFileInfoByIds(String[] fileIds) {
//        return sysFileInfoMapper.deleteSysFileInfoByIds(fileIds);
//    }
//
//    /**
//     * 删除文件信息信息
//     *
//     * @param fileId 文件信息ID
//     * @return 结果
//     */
//    @Override
//    public int deleteSysFileInfoById(String fileId) {
//        return sysFileInfoMapper.deleteSysFileInfoById(fileId);
//    }
//
//    /**
//     * 新增文件信息,视联使用
//     * @auth 李峥
//     * @param sysFileInfo 文件信息
//     * @return 结果
//     */
//    @Override
//    public boolean insertSysFileInfo2Video(SysFileInfo sysFileInfo) {
//        if(StrUtil.isBlank(sysFileInfo.getFileId())){
//            sysFileInfo.setFileId(IdUtil.simpleUUID());
//        }
//        sysFileInfo.setCreateTime(new Date());
//        sysFileInfo.setCreator(VIDEO_CREATE_BY);
//        sysFileInfo.setFileUrl(AESUtil.encryptHttp(sysFileInfo.getFileUrl()));
//        int i = sysFileInfoMapper.insertSysFileInfo(sysFileInfo);
//        if (i > 0){
//            return true;
//        }
//        return false;
//    }
}

