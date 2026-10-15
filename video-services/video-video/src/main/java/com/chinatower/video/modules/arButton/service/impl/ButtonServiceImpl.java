package com.chinatower.video.modules.arButton.service.impl;

import com.chinatower.common.core.domain.R;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.modules.arButton.entity.ArButtonAttachInfo;
import com.chinatower.video.modules.arButton.entity.ArButtonDeviceRelation;
import com.chinatower.video.modules.arButton.entity.ArButtonInfo;
import com.chinatower.video.modules.arButton.handler.ArButtonHandler;
import com.chinatower.video.modules.arButton.mapper.ArbuttonDeviceRelationMapper;
import com.chinatower.video.modules.arButton.mapper.ButtonAttachMapper;
import com.chinatower.video.modules.arButton.mapper.ButtonMapper;
import com.chinatower.video.modules.arButton.service.ButtonService;
import com.chinatower.video.modules.constants.ArConstants;
import com.chinatower.video.modules.constants.IndErrorMsgConstants;
import com.chinatower.video.modules.device.entity.DeviceDTO;
import com.chinatower.video.modules.device.mapper.DeviceMapper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: video-platform
 * @ClassName: ButtonServiceImpl
 * @description: AR大屏，配置按钮服务层实现
 * @author: fengty
 * @create: 2024-04-07 11:04
 */

@Service
@Slf4j
public class ButtonServiceImpl implements ButtonService {


    @Autowired
    private ButtonMapper buttonMapper;

    @Autowired
    ButtonAttachMapper buttonAttachMapper;

    @Resource
    FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    ArbuttonDeviceRelationMapper arbuttonDeviceRelationMapper;

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    ArButtonHandler arButtonHandler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addButton(ArButtonInfo buttonInfo) throws Exception {
        // 为了兼容老大屏增加
        if(null == buttonInfo.getDeviceRange()) {
            buttonInfo.setDeviceRange(ArConstants.DICT_ALL);
        }
        // 返回体map successKeyName， msgKeyName ， successKeyName true为成功， false为失败
        Map<String, Object> returnMap = new HashMap<>();
        String successKeyName = ArConstants.SUCCESS_KEY;
        String msgKeyName = ArConstants.MSG_KEY;

        if (buttonInfo.getAttachList().size() > 9) {
            returnMap.put(successKeyName, false);
            returnMap.put(msgKeyName, IndErrorMsgConstants.AR_ATTACH_OVER_NUMBER);
            return returnMap;
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 由于共通方法中有状态查询，重名校验是不区分状态的，所以查询之前把状态置空
        Integer enableStatusCache = buttonInfo.getEnableStatus();
        buttonInfo.setEnableStatus(null);

        // 查询 同一节点同一客户同一行业应用下的按钮名称是否有重复
        int count = arButtonHandler.getTheSameName(buttonInfo, user);
        if (count != 0) {
            returnMap.put(successKeyName, false);
            returnMap.put(msgKeyName, IndErrorMsgConstants.AR_BUTTON_NAME_NOT_ONLY);
            return returnMap;
        }
        // 查询后再将原始值set回去
        buttonInfo.setEnableStatus(enableStatusCache != null ? enableStatusCache : 2);
        log.info("添加ar大屏按钮入参:{}", buttonInfo);
        try {
            buttonInfo.setCreator(user.getUserName());
            int buttonCount = buttonMapper.insert(buttonInfo);
            if (buttonCount == 1) {
                List<ArButtonAttachInfo> buttonAttachInfoList = buttonInfo.getAttachList();
                buttonAttachInfoList.forEach(arButtonAttachInfo -> {
                    arButtonAttachInfo.setDeleteFlag(0);
                    arButtonAttachInfo.setCreator(user.getUserName());
                    arButtonAttachInfo.setButtonId(buttonInfo.getId());
                });
                buttonAttachMapper.batchInsert(buttonAttachInfoList);
            }
            // 20245-5-29 增加逻辑，如果应用范围不等于3, 则添加摄像机和配置的关系到关系表中 start
            batchInsertRelationByButton(buttonInfo);
            // end
            log.info("配置按钮：{}, 添加了{}个附件", buttonInfo.getButtonName(), buttonInfo.getAttachList().size());
            returnMap.put(successKeyName, true);
            returnMap.put(msgKeyName, "添加成功！");
            return returnMap;
        } catch (Exception e) {
            log.error("ar大屏配置增加报错：{}", e);
            throw new Exception(IndErrorMsgConstants.AR_BUTTON_ADD_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteButton(Long id) throws Exception {
        // 返回体map successKeyName， msgKeyName ， successKeyName true为成功， false为失败
        Map<String, Object> returnMap = new HashMap<>();
        String successKeyName = ArConstants.SUCCESS_KEY;
        String msgKeyName = ArConstants.MSG_KEY;

        ArButtonInfo buttonInfo = new ArButtonInfo();
        SysUser user = SecurityUtils.getLoginUser().getUser();

        try {
            buttonInfo.setId(id);
            buttonInfo.setModifier(user.getUserName());
            buttonInfo.setDeleteFlag(1);
            int updateCount = buttonMapper.updateById(buttonInfo);
            if (updateCount == 1) {
                ArButtonAttachInfo attachInfo = new ArButtonAttachInfo();
                attachInfo.setButtonId(id);
                attachInfo.setDeleteFlag(1);
                attachInfo.setModifier(user.getUserName());
                buttonAttachMapper.updateButtonAttachByButtonId(attachInfo);
            }
            // 2024-5-29 增加逻辑，逻辑删除按钮配置时物理删除关系表数据 start
            ArButtonDeviceRelation arButtonDeviceRelation = new ArButtonDeviceRelation();
            arButtonDeviceRelation.setIndustryCode(user.getIndustryCode());
            arButtonDeviceRelation.setAcceptNode(user.getPlatformCode());
            arButtonDeviceRelation.setTenantId(user.getTenantId());
            arButtonDeviceRelation.setButtonId(id);
            arbuttonDeviceRelationMapper.deleteByCondition(arButtonDeviceRelation);
            // end
            returnMap.put(successKeyName, true);
            returnMap.put(msgKeyName, "删除成功！");
            return returnMap;
        } catch (Exception e) {
            log.error("删除ar大屏配置出错",e);
            throw new Exception(IndErrorMsgConstants.AR_BUTTON_DELETET_ONLY);
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, Object> updateButton(ArButtonInfo buttonInfo) throws Exception {
        // 返回体map successKeyName， msgKeyName ， successKeyName true为成功， false为失败
        Map<String, Object> returnMap = new HashMap<>();
        String successKeyName = ArConstants.SUCCESS_KEY;
        String msgKeyName = ArConstants.MSG_KEY;
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 由于共通方法中有状态查询，重名校验是不区分状态的，所以查询之前把状态置空
        ArButtonInfo buttonInfoFromDB = buttonMapper.selectById(buttonInfo.getId());
        buttonInfo.setEnableStatus(null);
        // 查询 同一节点同一客户同一行业应用下的按钮名称是否有重复
        int count = arButtonHandler.getTheSameName(buttonInfo, user);
        if (count > 0) {
            returnMap.put(successKeyName, false);
            returnMap.put(msgKeyName, IndErrorMsgConstants.AR_BUTTON_NAME_NOT_ONLY);
            return returnMap;
        }
        buttonInfo.setEnableStatus(buttonInfoFromDB.getEnableStatus());
        log.info("编辑ar大屏按钮入参:{}", buttonInfo.toString());
        try {
            buttonInfo.setModifier(user.getUserName());
            int buttonCount = buttonMapper.updateById(buttonInfo);
            if (buttonCount == 1) {
                // 前端传过来的等待删除的附件列表
                List<ArButtonAttachInfo> deleteAttaches = buttonInfo.getDeleteAttachList();
                if (!CollectionUtils.isEmpty(deleteAttaches)) {
                    ArButtonAttachInfo paramAttach = new ArButtonAttachInfo();
                    paramAttach.setDeleteFlag(0);
                    paramAttach.setButtonId(buttonInfo.getId());
                    // 从待删除的list整理出id装载到此list
                    List<Long> deleteIds = new ArrayList<>();
                    deleteAttaches.forEach(attachInfo ->
                            deleteIds.add(attachInfo.getId())
                    );
                    buttonAttachMapper.batchLogicDelete(deleteIds, user.getUserName());
                }
                if (!CollectionUtils.isEmpty(buttonInfo.getAttachList())) {
                    ArButtonAttachInfo paramArbuttonAttach = new ArButtonAttachInfo();
                    paramArbuttonAttach.setButtonId(buttonInfo.getId());
                    paramArbuttonAttach.setDeleteFlag(0);
                    int countAttach = buttonAttachMapper.selectCountByCondition(paramArbuttonAttach);

                    if (countAttach + buttonInfo.getAttachList().size() > 9) {
                        returnMap.put(successKeyName, false);
                        returnMap.put(msgKeyName, IndErrorMsgConstants.AR_ATTACH_OVER_NUMBER);
                        return returnMap;
                    }

                    List<ArButtonAttachInfo> buttonAttachInfoList = buttonInfo.getAttachList();
                    buttonAttachInfoList.forEach(arButtonAttachInfo -> {
                        arButtonAttachInfo.setDeleteFlag(0);
                        arButtonAttachInfo.setCreator(user.getUserName());
                        arButtonAttachInfo.setButtonId(buttonInfo.getId());
                    });
                    buttonAttachMapper.batchInsert(buttonAttachInfoList);
                }
                // 2024-5-29 增加逻辑，如果范围为当前摄像机或选中摄像机，逻辑删除按钮配置时物理删除关系表数据然后重新添加 start
                // buttonInfo.getDeviceRange()为空的情况是老大屏进来的请求
                if(null != buttonInfo.getDeviceRange()) {
                    ArButtonDeviceRelation arButtonDeviceRelation = new ArButtonDeviceRelation();
                    arButtonDeviceRelation.setIndustryCode(user.getIndustryCode());
                    arButtonDeviceRelation.setAcceptNode(user.getPlatformCode());
                    arButtonDeviceRelation.setTenantId(user.getTenantId());
                    arButtonDeviceRelation.setButtonId(buttonInfo.getId());
                    arbuttonDeviceRelationMapper.deleteByCondition(arButtonDeviceRelation);
                    batchInsertRelationByButton(buttonInfo);
                }
            }
            // end
            if(buttonInfoFromDB.getEnableStatus() == 1) {
                arButtonHandler.handleEnableButtonIsOverLimit(buttonInfo, buttonInfoFromDB, user, returnMap);
            }
            log.info("配置按钮：{}, 添加了{}个附件", buttonInfo.getButtonName(), buttonInfo.getAttachList().size());
            returnMap.put(successKeyName, true);
            returnMap.put(msgKeyName, "修改成功！");
            return returnMap;
        } catch (RuntimeException runtimeException) {
            log.error("编辑配置按钮时报错:{}", runtimeException);
            throw new Exception(IndErrorMsgConstants.AR_ATTACH_UPDATE_ERROR);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ArButtonInfo getButtonById(Long id) {
        try {
            ArButtonInfo arButtonInfo = buttonMapper.selectById(id);
            ArButtonAttachInfo attachInfoParam = new ArButtonAttachInfo();
            attachInfoParam.setDeleteFlag(0);
            attachInfoParam.setButtonId(id);
            List<ArButtonAttachInfo> listAttach = buttonAttachMapper.selectByCondition(attachInfoParam);

            // 组装路径解密方法参数
            List<String> pathList = new ArrayList<>();
            for (ArButtonAttachInfo attachInfo : listAttach) {
                String attachPath = attachInfo.getAttachPath();
                if (StringUtils.isNotEmpty(attachPath)) {
                    pathList.add(attachPath);
                }
            }
            // 路径解密
            Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(pathList);
            // 再将路径set回去
            for (ArButtonAttachInfo attachInfo : listAttach) {
                String encryptUrl = stringStringMap.get(attachInfo.getAttachPath());
                attachInfo.setAttachPath(encryptUrl);
            }
            arButtonInfo.setAttachList(listAttach);

            if (arButtonInfo.getDeviceRange() != ArConstants.DICT_ALL) {
                ArButtonDeviceRelation relationParam = new ArButtonDeviceRelation();
                relationParam.setTenantId(arButtonInfo.getTenantId());
                relationParam.setButtonId(id);
                relationParam.setAcceptNode(arButtonInfo.getAcceptNode());
                relationParam.setIndustryCode(arButtonInfo.getIndustryCode());
                relationParam.setEnableStatus(arButtonInfo.getEnableStatus());
                List<ArButtonDeviceRelation> relations = arbuttonDeviceRelationMapper.selectListByCondition(relationParam);
                if (!CollectionUtils.isEmpty(relations)) {
                    List<String> deviceCodes = new ArrayList<>();
                    for (ArButtonDeviceRelation relation : relations) {
                        deviceCodes.add(relation.getDeviceCode());
                    }
                    arButtonInfo.setDeviceCodes(deviceCodes);
                }
            }
            return arButtonInfo;
        } catch (Exception e) {
            log.error("查询详情报错：{}", e);
        }
        return null;
    }

    @Override
    public int getCountByConditions(ArButtonInfo buttonInfo) {
        return buttonMapper.selectCountByConditions(buttonInfo);
    }

    @Override
    public List<ArButtonInfo> pageArButtonInfo(ArButtonInfo buttonInfo) {
        PageHelper.startPage(buttonInfo.getPageNum(), buttonInfo.getPageSize());
        SysUser user = SecurityUtils.getLoginUser().getUser();
        buttonInfo.setTenantId(user.getTenantId());
        buttonInfo.setIndustryCode(user.getIndustryCode());
        buttonInfo.setAcceptNode(user.getPlatformCode());
        buttonInfo.setDeleteFlag(0);
        return buttonMapper.pageArButtonInfo(buttonInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> turnButtonStatus(ArButtonInfo buttonInfo) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        // 查询出待启用的按钮（数据库中的原始按钮）
        ArButtonInfo originalButton = buttonMapper.selectById(buttonInfo.getId());
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            buttonInfo.setDeviceRange(originalButton.getDeviceRange());
            buttonInfo.setId(originalButton.getId());
            buttonInfo.setModifier(user.getUserName());
            buttonInfo.setTurnTime(new Date().toString());
            int successCount = buttonMapper.updateById(buttonInfo);
            if (buttonInfo.getEnableStatus() == 1) { // 启用按钮
                updateRelations(originalButton, buttonInfo.getEnableStatus());
                arButtonHandler.handleEnableButtonIsOverLimit(buttonInfo, originalButton, user, returnMap);
            } else { // 停用按钮
                if (originalButton.getDeviceRange() != ArConstants.DICT_ALL) {
                    updateRelations(originalButton, buttonInfo.getEnableStatus());
                }
            }
            if (successCount == 1) {
                returnMap.put(ArConstants.SUCCESS_KEY, true);
                returnMap.put(ArConstants.MSG_KEY, "操作成功");
            } else {
                returnMap.put(ArConstants.SUCCESS_KEY, false);
                returnMap.put(ArConstants.MSG_KEY, IndErrorMsgConstants.AR_BUTTON_TURN_ERROR);
            }
        } catch (RuntimeException runtimeException){
          log.error("起停用报错：{}", runtimeException);
          throw new Exception(IndErrorMsgConstants.AR_BUTTON_TURN_ERROR);
        } catch (Exception e) {
            throw new Exception(returnMap.get(ArConstants.MSG_KEY).toString());
        }
        return returnMap;
    }



    @Override
    public List<ArButtonInfo> getArButtonInfoByDeviceCode(String deviceCode) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        ArButtonDeviceRelation relationParam = new ArButtonDeviceRelation();
        relationParam.setDeviceCode(deviceCode);
        relationParam.setIndustryCode(user.getIndustryCode());
        relationParam.setAcceptNode(user.getPlatformCode());
        relationParam.setTenantId(user.getTenantId());
        relationParam.setEnableStatus(1);
        List<ArButtonDeviceRelation> relations = arbuttonDeviceRelationMapper.selectListByCondition(relationParam);


        ArButtonInfo arButtonInfoParam = new ArButtonInfo();
        arButtonInfoParam.setIndustryCode(user.getIndustryCode());
        arButtonInfoParam.setAcceptNode(user.getPlatformCode());
        arButtonInfoParam.setTenantId(user.getTenantId());
        arButtonInfoParam.setEnableStatus(1);
        arButtonInfoParam.setDeviceRange(ArConstants.DICT_ALL);
        // 查询范围为“全部”的按钮配置
        List<ArButtonInfo> allRangeButtons = buttonMapper.pageArButtonInfo(arButtonInfoParam);
        // 查询范围为“当前”或者指定的按钮配置
        // 封装查询参数
        if(!CollectionUtils.isEmpty(relations)) {
            List<Long> ids = new ArrayList<>();
            for (ArButtonDeviceRelation relation : relations) {
                ids.add(relation.getButtonId());
            }
            if(!CollectionUtils.isEmpty(ids)) {
                arButtonInfoParam.setDeviceRange(ArConstants.DICT_DESIGNATED);
                arButtonInfoParam.setIds(ids);
                List<ArButtonInfo> relationButton = buttonMapper.pageArButtonInfo(arButtonInfoParam);
                if (!CollectionUtils.isEmpty(relationButton)) {
                    allRangeButtons.addAll(relationButton);
                }
            }
        }
        allRangeButtons.sort(Comparator.comparing(ArButtonInfo::getTurnTime));
        return allRangeButtons;
    }

    /**
     * 根据配置基本信息批量新增配置与设备的关系
     * @param buttonInfo
     */
    public void batchInsertRelationByButton (ArButtonInfo buttonInfo) {
        if (buttonInfo.getDeviceRange() != ArConstants.DICT_ALL) {
            List<String> deviceCodes = buttonInfo.getDeviceCodes();
            List<ArButtonDeviceRelation> relations = new ArrayList<>();
            for (String deviceCode : deviceCodes) {
                ArButtonDeviceRelation relation = new ArButtonDeviceRelation();
                relation.setDeviceCode(deviceCode);
                relation.setButtonId(buttonInfo.getId());
                relation.setAcceptNode(buttonInfo.getAcceptNode());
                relation.setTenantId(buttonInfo.getTenantId());
                relation.setIndustryCode(buttonInfo.getIndustryCode());
                relation.setEnableStatus(buttonInfo.getEnableStatus());
                relations.add(relation);
            }
            arbuttonDeviceRelationMapper.batchInsert(relations);
        }
    }

    /**
     * 更新关系表的启用状态 根据按钮主键更新所有
     * @param originalButton
     */
    public void updateRelations (ArButtonInfo originalButton, int enableStatus) {
        ArButtonDeviceRelation relationUpdateParam = new ArButtonDeviceRelation();
        relationUpdateParam.setButtonId(originalButton.getId());
        relationUpdateParam.setIndustryCode(originalButton.getIndustryCode());
        relationUpdateParam.setAcceptNode(originalButton.getAcceptNode());
        relationUpdateParam.setTenantId(originalButton.getTenantId());
        relationUpdateParam.setEnableStatus(enableStatus);
        arbuttonDeviceRelationMapper.updateByButtonId(relationUpdateParam);
    }

}
