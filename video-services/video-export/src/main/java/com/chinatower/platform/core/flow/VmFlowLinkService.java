package com.chinatower.platform.core.flow;

import cn.hutool.core.collection.CollectionUtil;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.*;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.IAlarmFlowConfigMapper;
import com.chinatower.platform.mapper.IVmFlowLinkIndustryConfigMapper;
import com.chinatower.platform.repository.IFlowLinkAttrRepository;
import com.chinatower.platform.repository.IFlowTemplateRepository;
import com.chinatower.platform.repository.ISysService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 告警流程配置相关查询，状态流转功能service
 * @Author lyy
 * @Date 2022/12/13
 * @Version release_v1.5.50
 **/
@Component("vmFlowLinkService")
@Slf4j
public class VmFlowLinkService {
    @Autowired
    private IFlowTemplateRepository flowTemplateRepository;
    @Autowired
    private IFlowLinkAttrRepository flowLinkAttrRepository;
    @Autowired
    private IAlarmFlowConfigMapper alarmFlowConfigMapper;
    @Autowired
    private IVmFlowLinkIndustryConfigMapper flowLinkIndustryConfigMapper;
    @Autowired
    private ISysService sysService;

    /**
     * 获取流程环节信息
     * @param model
     * @return
     */
    public List<FlowModel> getFlowLink(AlarmFlowConfigModel model){
        //先查告警流程配置信息 关联attr
        List<FlowModel> flowModelList = alarmFlowConfigMapper.queryFlowLink(model);
        if(CollectionUtil.isEmpty(flowModelList)){
            flowModelList = flowLinkIndustryConfigMapper.queryFlowLinkByFlowId(model);
        }
        //没有则使用行业版本初始化流程配置节点属性
        if(CollectionUtil.isEmpty(flowModelList)){
            model.setId(model.getTemplateId() != null?model.getTemplateId():model.getId());
            flowModelList = flowLinkIndustryConfigMapper.queryFlowLinkIndustryConfig(model);
            //行业默认流程名字拼接1
            for (FlowModel flowModel:flowModelList) {
                if(Constants.DEFAULT_FLOW.contains(String.valueOf(flowModel.getLinkId()))){
                    flowModel.setLinkName(flowModel.getLinkName()+"1");
                }
            }
            if(CollectionUtil.isEmpty(flowModelList)){
                VmFlowTemplateModel flowTemplateModel = new VmFlowTemplateModel();
                flowTemplateModel.setInitSource(model.getWarningSource());
                flowTemplateModel.setFlowType("1");//默认流程
                FlowAlarmTemplateDto flowAlarmTemplateDto = flowTemplateRepository.selectFlowTemplate(flowTemplateModel);
                if(flowAlarmTemplateDto != null){
                    AlarmFlowConfigModel newModel = new AlarmFlowConfigModel();
                    newModel.setLinkId(model.getLinkId());
                    newModel.setLinkList(model.getLinkList());
                    newModel.setId(flowAlarmTemplateDto.getTemplateId());
                    flowModelList = flowLinkIndustryConfigMapper.queryFlowLinkIndustryConfig(newModel);
                    //行业默认流程名字拼接1
                    for (FlowModel flowModel:flowModelList) {
                        if(Constants.DEFAULT_FLOW.contains(String.valueOf(flowModel.getLinkId()))){
                            flowModel.setLinkName(flowModel.getLinkName()+"1");
                        }
                    }
                }
            }
        }
        return flowModelList;
    }

    /**
     * 功能说明：根据用户id和角色id查询流程环节信息
     *
     * @author: zhang.xionghui
     * @param:
     * @return:
     * @date: 2022/8/10 14:21
     * @requestID  需求单号及需求名称
     */
    public String getFlowLinkAttrByUser(SysUser user, List<String> roleIdList){
        String flowLinkIds = "";
        String userId = sysService.getLoginUserId();  //获取当前登录用户id(切换后取对应账号的id)
        user.setUserId(userId);
        //根据用户id查环节信息
        List<VmFlowLinkAttrDto> flowLinkUserList = flowLinkAttrRepository.getFlowLinkByUserId(userId);
        List<VmFlowLinkAttrDto> flowLinkRoleList = new ArrayList<>();
        if(roleIdList!=null && !roleIdList.isEmpty()){
            Map roleParam = new HashMap();
            roleParam.put("roleIdList",roleIdList);
            roleParam.put("tenantId",user.getTenantId());
            roleParam.put("industryCode",user.getIndustryCode());
            roleParam.put("appVerCode",user.getAppVerCode());
            flowLinkRoleList = flowLinkAttrRepository.getFlowLinkByRoleIdList(roleParam);
        }
//        for (Long role:roleIds) {
//            //根据角色id查环节信息
//            List<VmFlowLinkAttrDto> flowLinkList = flowLinkAttrRepository.getFlowLinkByRoleId(role,currentUser.getUser().getAppVerCode(),currentUser.getUser().getIndustryCode());
//            if(!CollectionUtil.isEmpty(flowLinkList)){
//                flowLinkRoleList.addAll(flowLinkList);
//            }
//        }

        //汇总数据
        flowLinkUserList.addAll(flowLinkRoleList);
        if(!CollectionUtil.isEmpty(flowLinkUserList)) {
            //去重处理加拼接处理
            flowLinkIds = flowLinkUserList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                    new TreeSet<>(Comparator.comparing(o -> o.getAlarmFlowId() + "-" + o.getLinkId()))), ArrayList::new))
                    .stream().map(item -> item.getAlarmFlowId() + "-" + item.getLinkId()).collect(Collectors.joining(Constants.COMMA));
        }
        return flowLinkIds;
    }
}
