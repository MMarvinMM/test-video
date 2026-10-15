package com.chinatower.emergency.dto;


import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.chinatower.system.api.model.DeptUserGroupVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskInfoVO extends CommonFeignEntity {
    private String eventCode;
    private String digitalId;
    private Integer stageId;
    private String digitalTaskId;
    private String digitalStageId;
    private Integer taskId;
    private String taskName;
    private String taskStartDate;
    private String taskStopDate;
    //任务状态 0:未开始,1:执行中,2:已中止,3:已结束
    private Integer taskStatus;
    private Integer taskProgress;
    private String taskDetail;
    //责任人姓名拼接串（逗号分隔）
    private String nickNames;
    private String userNames;
    private String userIds;
    private List<Integer> feedbackIdList;
    private List<EmergencyAttachVO> imgUrlList;
    private List<EmergencyAttachVO> videoUrlList;
    private List<EmergencyAttachVO> fileUrlList;
    //责任人组织用户树
    private List<DeptUserGroupVO> deptUserTree;
    //责任人用户列表
    private List<EmergencySysUser> userList;
}
