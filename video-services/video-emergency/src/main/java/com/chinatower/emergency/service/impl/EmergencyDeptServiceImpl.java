package com.chinatower.emergency.service.impl;


import com.alibaba.nacos.common.utils.CollectionUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.emergency.dto.EmergencyDeptDTO;
import com.chinatower.emergency.dto.EmergencyGroupVO;
import com.chinatower.emergency.dto.EmergencyUserDTO;
import com.chinatower.emergency.mapper.EmergencyDeptMapper;
import com.chinatower.emergency.service.EmergencyDeptService;
import com.chinatower.system.api.model.DeptUserGroupVO;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Liqi
 */
@Service
@Slf4j
public class EmergencyDeptServiceImpl extends BaseEventInfoService implements EmergencyDeptService {

    @Autowired
    private EmergencyDeptMapper emergencyDeptMapper;

    private final static String ZERO_STR = "0";

    private final static String ONE_STR = "1";

    private final static String TWO_STR = "2";

    /**
     * 生成初始组别
     *
     * @param eventCode
     */
    @Override
    public void generateInitialDept(String eventCode) {
        List<String> deptNameList = Arrays.asList("指挥官", "中心组", "计划组", "行动组");
        EmergencyDeptDTO emergencyDeptDTO = new EmergencyDeptDTO();
        setUserInfo(emergencyDeptDTO, true);
        emergencyDeptDTO.setDeptLevel(1);
        emergencyDeptDTO.setAncestors(ZERO_STR);
        emergencyDeptDTO.setEventCode(eventCode);
        emergencyDeptDTO.setDeptNameList(deptNameList);
        emergencyDeptMapper.generatInitialDept(emergencyDeptDTO);
    }

    @Override
    @Transactional
    public void clearDeptUserInfo(List<String> eventCodes) {
        List<String> idList = emergencyDeptMapper.getIdList(eventCodes);
        if(CollectionUtils.isNotEmpty(idList) && !idList.isEmpty()){
            emergencyDeptMapper.clearDeptInfo(idList);
            emergencyDeptMapper.clearUserInfo(idList);
        }
    }

    @Override
    public Result addEmergencyGroup(EmergencyDeptDTO dto) {
        setUserInfo(dto, true);
        dto.setParentId(ZERO_STR);
        dto.setDeptLevel(1);
        dto.setAncestors(ZERO_STR);
        dto.setCommanderFlag(0);
        //判定有无重复组别
        List<EmergencyDeptDTO> emergencyGroup = emergencyDeptMapper.getEmergencyGroup(dto);
        if (CollectionUtils.isNotEmpty(emergencyGroup) && !emergencyGroup.isEmpty()) {
            return Result.fail("当前组别已存在");
        } else {
            emergencyDeptMapper.addEmergencyGroup(dto);
            return Result.data("增加组别成功");
        }
    }

    @Override
    public String delEmergencyDept(EmergencyDeptDTO dto) {
        setUserInfo(dto, true);
        emergencyDeptMapper.delEmergencyGroup(dto);
        return "删除成功";
    }

    @Override
    public List<EmergencyDeptDTO> getEmergencyGroup(EmergencyDeptDTO dto) {
        setUserInfo(dto, true);
        dto.setParentId(ZERO_STR);
        List<EmergencyDeptDTO> emergencyGroup;
        if (null != dto.getPageSize() && null != dto.getPageNum()) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        emergencyGroup = emergencyDeptMapper.getEmergencyGroup(dto);
        return emergencyGroup;
    }

    @Override
    public Result addEmergencyDept(EmergencyDeptDTO dto) {
        setUserInfo(dto, true);
        dto.setId(dto.getTopDeptId());
        //取当前组别及组别下所有组织名称
        List<EmergencyGroupVO> emergencyDeptList = emergencyDeptMapper.getEmergencyDeptList(dto);
        Set<String> emergencyGroupName = emergencyDeptList.stream().map(EmergencyGroupVO::getEmessageName).collect(Collectors.toSet());
        //判定有无重复组织
        if (CollectionUtils.isNotEmpty(emergencyGroupName) && emergencyGroupName.contains(dto.getEmergencyDeptName())) {
            return Result.fail("当前组别已存在此组织");
        } else {
            EmergencyDeptDTO emergencyDeptDTO = emergencyDeptMapper.getEmergencyDeptById(dto.getParentId());
            dto.setDeptLevel(emergencyDeptDTO.getDeptLevel() + 1);
            dto.setAncestors(emergencyDeptDTO.getAncestors() + "," + dto.getParentId());
            dto.setEventCode(emergencyDeptDTO.getEventCode());
            dto.setCommanderFlag(0);
            emergencyDeptMapper.addEmergencyGroup(dto);
            return Result.data("增加组织成功");
        }
    }

    @Override
    public Object getEmergencyOrganTree(EmergencyDeptDTO dto) {
        setUserInfo(dto, true);
        //取出所有组织
        List<EmergencyGroupVO> emergencyDeptList = emergencyDeptMapper.getEmergencyDeptList(dto);
        //判断是否需要返回用户信息
        if (dto.getFlag() == 1) {
            //取出所有组织ID
            Set<String> deptId = emergencyDeptList.stream().map(EmergencyGroupVO::getMessageId).collect(Collectors.toSet());
            //取出所有用户
            List<EmergencyUserDTO> emergencyUserList = emergencyDeptMapper.getEmergencyUserList(deptId);
            if ("3".equals(dto.getCorrespondenceFlag())) {
                for (EmergencyUserDTO emergencyUserDTO : emergencyUserList) {
                    EmergencyGroupVO dispatchDeskGroup = getEmergencyGroupVO(emergencyUserDTO);
                    emergencyDeptList.add(dispatchDeskGroup);
                }
            } else {
                //判断号码,加入List中
                for (EmergencyUserDTO emergencyUserDTO : emergencyUserList) {
                    if (StringUtils.isNotBlank(emergencyUserDTO.getMobileTerminalNum()) && StringUtils.isNotBlank(emergencyUserDTO.getDispatchDeskNum())) {
                        EmergencyGroupVO dispatchDeskGroup = getEmergencyGroupVO(emergencyUserDTO);
                        //调度台
                        dispatchDeskGroup.setNumberMarking(ONE_STR);
                        dispatchDeskGroup.setCorrespondenceNumber(emergencyUserDTO.getDispatchDeskNum());
                        emergencyDeptList.add(dispatchDeskGroup);
                        EmergencyGroupVO mobileGroup = getEmergencyGroupVO(emergencyUserDTO);
                        //移动端
                        mobileGroup.setCorrespondenceNumber(emergencyUserDTO.getMobileTerminalNum());
                        mobileGroup.setNumberMarking(TWO_STR);
                        emergencyDeptList.add(mobileGroup);
                    } else {
                        EmergencyGroupVO emergencyGroupVO = getEmergencyGroupVO(emergencyUserDTO);
                        if (StringUtils.isNotBlank(emergencyUserDTO.getMobileTerminalNum())) {
                            emergencyGroupVO.setCorrespondenceNumber(emergencyUserDTO.getMobileTerminalNum());
                            emergencyGroupVO.setNumberMarking(TWO_STR);
                            emergencyDeptList.add(emergencyGroupVO);
                        } else if (StringUtils.isNotBlank(emergencyUserDTO.getDispatchDeskNum())) {
                            emergencyGroupVO.setCorrespondenceNumber(emergencyUserDTO.getDispatchDeskNum());
                            emergencyGroupVO.setNumberMarking(ONE_STR);
                            emergencyDeptList.add(emergencyGroupVO);
                        } else if (!"1".equals(dto.getCorrespondenceFlag())) {
                            emergencyDeptList.add(emergencyGroupVO);
                        }
                    }
                }
            }
        }
        //增加给前端用唯一标识
        for (EmergencyGroupVO emergencyGroupVO : emergencyDeptList) {
            emergencyGroupVO.setSpecialId(emergencyGroupVO.getEmessageParentId() + "-" + emergencyGroupVO.getMessageId()
                    + "-" + (StringUtils.isNotBlank(emergencyGroupVO.getNumberMarking()) ? emergencyGroupVO.getNumberMarking() : "0"));
        }
        //排序先组织，后用户，先调度台后移动端
        Comparator<EmergencyGroupVO> comparator = Comparator.comparing(EmergencyGroupVO::getInformationFlag, Comparator.reverseOrder());
        List<EmergencyGroupVO> collect = emergencyDeptList.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        //生成树形结构
        return streamToTree(collect, ZERO_STR);
    }

    private static EmergencyGroupVO getEmergencyGroupVO(EmergencyUserDTO emergencyUserDTO) {
        EmergencyGroupVO emergencyGroupVO1 = new EmergencyGroupVO();
        emergencyGroupVO1.setMessageId(emergencyUserDTO.getUserId());
        emergencyGroupVO1.setEmessageName(emergencyUserDTO.getNickName());
        emergencyGroupVO1.setEmessageParentId(emergencyUserDTO.getEmergencyDeptId());
        emergencyGroupVO1.setInformationFlag(2);
        return emergencyGroupVO1;
    }

    public static List<EmergencyGroupVO> streamToTree(List<EmergencyGroupVO> treeList, String parentId) {
        List<EmergencyGroupVO> list = treeList.stream()
                // 过滤父节点
                .filter(parent -> parent.getEmessageParentId().equals(parentId))
                // 把父节点children递归赋值成为子节点
                .peek(child -> child.setChild(streamToTree(treeList, child.getMessageId()))).collect(Collectors.toList());
        return list;
    }


}
