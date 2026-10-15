package com.chinatower.platform.project.system.service.impl;

import com.chinatower.common.entity.Result;
import com.chinatower.common.utils.SqlUtil;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.platform.project.system.domain.vo.MiniProgramUpgradeInfoVO;
import com.chinatower.platform.project.system.domain.vo.WgtUpgradeInfoVO;
import com.chinatower.platform.project.system.mapper.WgtUpgradeInfoMapper;
import com.chinatower.platform.project.system.service.IWgtUpgradeInfoService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 功能说明: Wgt版本管理Service业务层处理
 * @param:
 * @return:
 * @author: lzt
 * @date: 2024/1/31
 */
@Slf4j
@Service
public class WgtUpgradeInfoServiceImpl implements IWgtUpgradeInfoService {

    @Resource
    private WgtUpgradeInfoMapper wgtUpgradeInfoMapper;


    @Override
    public String queryColonyVersion(String colonyCode) {
        return wgtUpgradeInfoMapper.queryColonyVersion(colonyCode);
    }

    @Override
    public WgtUpgradeInfoVO queryWgtUpgradeInfo(String colonyVersion, Integer type) {
        return  wgtUpgradeInfoMapper.queryWgtUpgradeInfo(colonyVersion, type);
    }

    @Override
    public List<WgtUpgradeInfoVO> queryWgtList(WgtUpgradeInfoVO wgtUpgradeInfoVO) {
        String[] orderByColumnStr = {"colonyVersion", "wgtStatus", "upgradeUrl","wgtVersion","isForceUpgrade","upgradeDescribe","creator","createTime","modifier","updateTime"};
        List<String> variableList = Arrays.asList(orderByColumnStr);
        if (!variableList.contains(wgtUpgradeInfoVO.getOrderByColumn())) {
            wgtUpgradeInfoVO.setOrderByColumn("");
        }
        String orderByStr ="";
        if(StringUtils.isNotEmpty(wgtUpgradeInfoVO.getOrderByColumn()) && "wgtStatus".equals(wgtUpgradeInfoVO.getOrderByColumn())){
            orderByStr = "CASE WHEN upgrade_url IS NULL THEN '0' ELSE '1' END " + wgtUpgradeInfoVO.getIsAsc();
        }else{
            orderByStr = wgtUpgradeInfoVO.getOrderBy();
        }
        PageHelper.startPage(wgtUpgradeInfoVO.getPageNum(), wgtUpgradeInfoVO.getPageSize(),orderByStr);
        // 先根据分页查询出父级数据
        wgtUpgradeInfoVO.setQueryType(0);
        List<WgtUpgradeInfoVO> parentList = wgtUpgradeInfoMapper.queryWgtList(wgtUpgradeInfoVO);
        if (CollectionUtils.isEmpty(parentList)) {
            return new ArrayList<>();
        }
        // 2. 收集父级ID
        List<Long> parentIds = parentList.stream()
                .map(WgtUpgradeInfoVO::getId)
                .collect(Collectors.toList());

        // 3. 根据父级ID查询子级数据
        WgtUpgradeInfoVO tempWgtUpgradeInfoVO = new WgtUpgradeInfoVO();
        tempWgtUpgradeInfoVO.setParentIds(parentIds);
        List<WgtUpgradeInfoVO> childrenList = wgtUpgradeInfoMapper.queryWgtList(tempWgtUpgradeInfoVO);
        // 4. 将子级数据放入对应的父级
        Map<Long, List<WgtUpgradeInfoVO>> childrenMap = childrenList.stream()
                .filter(child -> child.getParentId() != null)
                .collect(Collectors.groupingBy(WgtUpgradeInfoVO::getParentId));
        // 设置子节点列表
        for (WgtUpgradeInfoVO parent : parentList) {
            List<WgtUpgradeInfoVO> children = childrenMap.get(parent.getId());
            parent.setChildren(children != null ? children : new ArrayList<>());
        }
        return parentList;
    }



    @Override
    public int updateWgt(WgtUpgradeInfoVO wgtUpgradeInfoVO) {
        wgtUpgradeInfoVO.setUpdateTime(new Date());
        wgtUpgradeInfoVO.setModifier(SecurityUtils.getUsername());
        return wgtUpgradeInfoMapper.updateWgt(wgtUpgradeInfoVO);
    }

    @Override
    public WgtUpgradeInfoVO getDownloadUrl(WgtUpgradeInfoVO wgtUpgradeInfoVO) {
        return wgtUpgradeInfoMapper.getDownloadUrl(wgtUpgradeInfoVO);
    }


    @Override
    public MiniProgramUpgradeInfoVO queryMiniProgramUpgradeInfo(String colonyVersion) {
        return  wgtUpgradeInfoMapper.queryMiniProgramUpgradeInfo(colonyVersion);
    }


    @Override
    public List<MiniProgramUpgradeInfoVO> queryMiniProgramList(MiniProgramUpgradeInfoVO miniProgramUpgradeInfoVO) {
//        String[] orderByColumnStr = {"createTime","updateTime"};
//        List<String> variableList = Arrays.asList(orderByColumnStr);
//        if (!variableList.contains(miniProgramUpgradeInfoVO.getOrderByColumn())) {
//            miniProgramUpgradeInfoVO.setOrderByColumn("");
//        }
//        PageHelper.startPage(miniProgramUpgradeInfoVO.getPageNum(), miniProgramUpgradeInfoVO.getPageSize(),miniProgramUpgradeInfoVO.getOrderBy());
        return wgtUpgradeInfoMapper.queryMiniProgramList(miniProgramUpgradeInfoVO);
    }

    @Override
    public int updateMiniProgram(MiniProgramUpgradeInfoVO miniProgramUpgradeInfoVO) {
        miniProgramUpgradeInfoVO.setModifier(SecurityUtils.getOperator());
        return wgtUpgradeInfoMapper.updateMiniProgram(miniProgramUpgradeInfoVO);
    }


    @Override
    public Result<String> addChildWgt(WgtUpgradeInfoVO wgtUpgradeInfoVO) {
        wgtUpgradeInfoVO.setCreator(SecurityUtils.getOperator());
        WgtUpgradeInfoVO tempWgtUpgradeInfoVO = new WgtUpgradeInfoVO();
        tempWgtUpgradeInfoVO.setParentId(wgtUpgradeInfoVO.getParentId());
        tempWgtUpgradeInfoVO.setPlatformCode(wgtUpgradeInfoVO.getPlatformCode());
        List<WgtUpgradeInfoVO> list = wgtUpgradeInfoMapper.queryWgtList(tempWgtUpgradeInfoVO);
        if(!CollectionUtils.isEmpty(list)){
            return Result.fail("子节点已存在");
        }
        wgtUpgradeInfoMapper.addChildWgt(wgtUpgradeInfoVO);
        return Result.success("新增子节点成功");
    }





}
