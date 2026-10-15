package com.chinatower.emergency.controller;


import com.chinatower.bdm.api.entity.TreeSelectByBdm;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.emergency.dto.Constant;
import com.chinatower.emergency.dto.EmergencyResourceVO;
import com.chinatower.emergency.service.EmergencyResourceService;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * video-emergency
 * 应急资源
 *
 * @author liuxt
 * @date 2024/10/28
 * @requestID
 */
@Slf4j
@RestController
@RequestMapping("/emergency")
public class EmergencyResourceController extends TowerBaseController {



    @Resource
    private EmergencyResourceService emergencyResourceService;


    /**
     * 应急资源树
     * @author liuxt
     * @param vo
     * @return 应急资源树
     * @throws
     * @requestID     *
     */
    @ApiOperation(value = "应急资源树",notes = "应急资源树")
    @PostMapping(value = "/getEmergencyResourceTree")
    public AjaxResult getEmergencyResourceTree(@RequestBody EmergencyResourceVO vo) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        vo.setTenantId(currentUser.getUser().getTenantId());
        vo.setIndustryCode(currentUser.getUser().getIndustryCode());
//        vo.setAppVerCode(currentUser.getUser().getAppVerCode());
        List<TreeSelectByBdm> treeSelectByBdmList = emergencyResourceService.getEmergencyResourceTree(vo);
        return AjaxResult.success(treeSelectByBdmList);
    }


    /**
     * 资源调派
     * @author liuxt
     * @param emergencyResourceVO
     * @return 新增结果
     * @throws
     * @requestID
     */
    @PostMapping(value = "/addEmergencyResource")
    @Transactional(rollbackFor = Exception.class)
    public Result add(@RequestBody EmergencyResourceVO emergencyResourceVO) {
        List<EmergencyResourceVO> list = new ArrayList<>();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //先删除再新增
        emergencyResourceVO.setTenantId(loginUser.getUser().getTenantId());
        emergencyResourceVO.setIndustryCode(loginUser.getUser().getIndustryCode());
        emergencyResourceService.deleteEmergencyResource(emergencyResourceVO);
        if(!CollectionUtils.isEmpty(emergencyResourceVO.getResourceIdList())){
            for (int i = 0; i < emergencyResourceVO.getResourceIdList().size(); i++) {
                EmergencyResourceVO tmpEmergencyResourceVO = new EmergencyResourceVO();
                tmpEmergencyResourceVO.setEventCode(emergencyResourceVO.getEventCode());
                tmpEmergencyResourceVO.setStageId(emergencyResourceVO.getStageId());
                tmpEmergencyResourceVO.setTaskId(emergencyResourceVO.getTaskId());
                tmpEmergencyResourceVO.setResourceId(emergencyResourceVO.getResourceIdList().get(i));
                tmpEmergencyResourceVO.setTenantId(loginUser.getUser().getTenantId());
                tmpEmergencyResourceVO.setIndustryCode(loginUser.getUser().getIndustryCode());
                tmpEmergencyResourceVO.setAppVerCode(loginUser.getUser().getAppVerCode());
                tmpEmergencyResourceVO.setCreator(SecurityUtils.getOperator());
                list.add(tmpEmergencyResourceVO);
            }
        }
        return Result.data(emergencyResourceService.addEmergencyResource(list));
    }

}
