package com.chinatower.emergency.controller;


import com.chinatower.common.entity.Result;
import com.chinatower.emergency.dto.EmergencyDeptDTO;
import com.chinatower.emergency.service.EmergencyDeptService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * video-emergency
 * @author Liqi
 */
@RestController
@Slf4j
@RequestMapping("/emergency/emergencyDept")
public class EmergencyDeptController {

    @Autowired
    private EmergencyDeptService emergencyDeptService;

    /**
     * 功能描述：新增组别
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月28日16:58:32
     */
    @PostMapping("/addEmergencyGroup")
    public Result addEmergencyGroup(@RequestBody EmergencyDeptDTO dto) {
        return emergencyDeptService.addEmergencyGroup(dto);
    }


    @PostMapping("/generateInitialDept")
    public void generateInitialDept(@RequestBody EmergencyDeptDTO dto) {
        emergencyDeptService.generateInitialDept(dto.getEventCode());
    }

    /**
     * 功能描述：删除组别/删除组织
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月28日16:58:32
     */
    @PostMapping("/delEmergencyDept")
    public Result<String> delEmergencyDept(@RequestBody EmergencyDeptDTO dto) {
        return Result.data(emergencyDeptService.delEmergencyDept(dto));
    }


    /**
     * 功能描述：查询组别
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月28日16:58:32
     */
    @PostMapping("/getEmergencyGroup")
    public Result getEmergencyGroup(@RequestBody EmergencyDeptDTO dto) {
        return Result.data(PageInfo.of(emergencyDeptService.getEmergencyGroup(dto)));
    }

    /**
     * 功能描述：新增组织
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月28日16:58:32
     */
    @PostMapping("/addEmergencyDept")
    public Result<String> addEmergencyDept(@RequestBody EmergencyDeptDTO dto) {
        return emergencyDeptService.addEmergencyDept(dto);
    }



    /**
     * 功能描述：获取组织树
     *
     * @return Result
     * @author Liqi
     * @date 2024年10月28日16:58:32
     */
    @PostMapping("/getEmergencyOrganTree")
    public Result getEmergencyOrganTree(@RequestBody EmergencyDeptDTO dto) {
        return Result.data(emergencyDeptService.getEmergencyOrganTree(dto));
    }

}
