package com.chinatower.platform.project.system.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.IIndustryService;
import com.chinatower.manage.api.dto.SysIndustryConfigInfoModel;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.mapper.*;
import com.chinatower.platform.project.system.service.IScreenMenuConfService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.ScreenMenuConfVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.*;
import java.util.stream.Collectors;


/**
 * 大屏菜单配置业务层处理
 *
 * @author chinatower
 */
@Slf4j
@Service
public class ScreenMenuConfServiceImpl implements IScreenMenuConfService {
    @Autowired
    private ScreenMenuConfMapper screenMenuConfMapper;

    @Autowired
    private IIndustryService iIndustryService;



    @Override
    public List<ScreenMenuConfVO> getScreenMenuConf(ScreenMenuConfVO screenMenuConfVO) {
        List<ScreenMenuConfVO> returnScreenMenuConfVOList = new ArrayList<>();//要返回给前端的列表
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //调浩鲸接口查当前客户，行业版本下的全部大屏配置信息
        List<SysIndustryConfigInfoModel> sysIndustryConfigInfoModelDataList = new ArrayList<>();
        SysIndustryConfigInfoModel param = new SysIndustryConfigInfoModel();
        param.setIndustryCode(user.getIndustryCode());
        param.setAppVerCode(user.getAppVerCode());
        param.setTenantId(user.getTenantId());
        log.info("获取全部大屏配置信息接口入参:" + JSONObject.toJSONString(param));
        Result<List<SysIndustryConfigInfoModel>> industryConfiglist = iIndustryService.queryIndustryConfigInfo(param);
        if (industryConfiglist != null && !CollectionUtils.isEmpty(industryConfiglist.getData())) {
            sysIndustryConfigInfoModelDataList = industryConfiglist.getData();
            log.info("queryIndustryConfigInfo 接口, param:{}, result:{}",
                    JSON.toJSONString(param), JSON.toJSONString(sysIndustryConfigInfoModelDataList));
            screenMenuConfVO.setTenantId(user.getTenantId());
            screenMenuConfVO.setIndustryCode(user.getIndustryCode());
            screenMenuConfVO.setAppVerCode(user.getAppVerCode());
            List<ScreenMenuConfVO> list = screenMenuConfMapper.getScreenMenuConf(screenMenuConfVO);
            int curSort = screenMenuConfMapper.getMaxSort(screenMenuConfVO);
            if(!CollectionUtils.isEmpty(list)){//配了个性化
                list = foreachIndustryList(list,sysIndustryConfigInfoModelDataList,curSort);//遍历浩鲸返回的全部大屏配置信息 确定返回的列表要添加哪些大屏
                list = foreachList(list,sysIndustryConfigInfoModelDataList);//遍历个性化的大屏配置信息 确定返回的列表要删除哪些大屏
                Map<String, List<ScreenMenuConfVO>> groupedItems = list.stream()
                        .collect(Collectors.groupingBy(ScreenMenuConfVO::getGroupId, LinkedHashMap::new, Collectors.toList()));
                for (Map.Entry<String, List<ScreenMenuConfVO>> entry : groupedItems.entrySet()) {//转换为前端需要的格式
                    List<ScreenMenuConfVO> groupList = entry.getValue();
                    List<ScreenMenuConfVO> tempList = new ArrayList<>();
                    groupList.stream().forEach(item -> {
                        ScreenMenuConfVO tempScreenMenuConfVO = new ScreenMenuConfVO();
                        tempScreenMenuConfVO.setIndustryViewId(item.getIndustryViewId());
                        tempScreenMenuConfVO.setIndustryViewType(item.getIndustryViewType());
                        tempScreenMenuConfVO.setViewType(item.getViewType());
                        tempScreenMenuConfVO.setSort(item.getSort());
                        tempScreenMenuConfVO.setIndustryViewName(item.getIndustryViewName());
                        tempList.add(tempScreenMenuConfVO);
                    });
                    ScreenMenuConfVO firstScreenMenuConfVO = groupList.get(Constants.ZERO_NUM);//取分组后的第一条大屏菜单配置
                    firstScreenMenuConfVO.setIndustryViewList(tempList);
                    firstScreenMenuConfVO.setIndustryViewId(null);
                    firstScreenMenuConfVO.setIndustryViewType(null);
                    firstScreenMenuConfVO.setViewType(null);
                    firstScreenMenuConfVO.setSort(null);
                    returnScreenMenuConfVOList.add(firstScreenMenuConfVO);
                }
            }else{//没配个性化
                int sort = 0;
                for (int i = 0; i < sysIndustryConfigInfoModelDataList.size(); i++) {//转换为前端需要的格式
                    SysIndustryConfigInfoModel sysIndustryConfigInfoModel = sysIndustryConfigInfoModelDataList.get(i);
                    ScreenMenuConfVO initScreenMenuConfVO = new ScreenMenuConfVO();
                    List<ScreenMenuConfVO> tempList = new ArrayList<>();
                    ScreenMenuConfVO tempScreenMenuConfVO = new ScreenMenuConfVO();
                    tempScreenMenuConfVO.setIndustryViewId(sysIndustryConfigInfoModel.getIndustryViewId().toString());
                    tempScreenMenuConfVO.setIndustryViewType(sysIndustryConfigInfoModel.getIndustryViewType());
                    tempScreenMenuConfVO.setViewType(sysIndustryConfigInfoModel.getViewType());
                    tempScreenMenuConfVO.setIndustryViewName(sysIndustryConfigInfoModel.getIndustryViewName());
                    tempScreenMenuConfVO.setSort(sort);
                    tempList.add(tempScreenMenuConfVO);
                    initScreenMenuConfVO.setIndustryViewList(tempList);
                    initScreenMenuConfVO.setTabName(sysIndustryConfigInfoModel.getIndustryViewName());
                    initScreenMenuConfVO.setAttr(Constants.ONE);
                    initScreenMenuConfVO.setGroupId(IdUtils.simpleUUID());
                    returnScreenMenuConfVOList.add(initScreenMenuConfVO);
                    sort = sort + Constants.ONE_NUM;
                }
            }
        }
        return returnScreenMenuConfVOList;
    }


    /**
     * 功能说明: 保存大屏菜单配置
     * @author liuxt
     * @param screenMenuConfVOList
     * @return 新增结果
     * @throws
     * @throws
     * @requestID       *
     */
    @Override
    public int saveRoleScreenRef(List<ScreenMenuConfVO> screenMenuConfVOList) {
        int num = 0;
        if(!CollectionUtils.isEmpty(screenMenuConfVOList)){
            ScreenMenuConfVO screenMenuConfVO = new ScreenMenuConfVO();
            SysUser user = SecurityUtils.getLoginUser().getUser();
            screenMenuConfVO.setTenantId(user.getTenantId());
            screenMenuConfVO.setIndustryCode(user.getIndustryCode());
            screenMenuConfVO.setAppVerCode(user.getAppVerCode());
            screenMenuConfMapper.deleteScreenMenuConf(screenMenuConfVO);
            List<ScreenMenuConfVO> returnScreenMenuConfVOList = new ArrayList<>();
            int sort = 0;
            for (int i = 0; i < screenMenuConfVOList.size(); i++) {
                ScreenMenuConfVO vo = screenMenuConfVOList.get(i);
                List<ScreenMenuConfVO> industryViewList = vo.getIndustryViewList();
                for (int j = 0; j < industryViewList.size(); j++) {
                    ScreenMenuConfVO industryView = industryViewList.get(j);
                    ScreenMenuConfVO tempScreenMenuConfVO = new ScreenMenuConfVO();
                    tempScreenMenuConfVO.setGroupId(vo.getGroupId());
                    tempScreenMenuConfVO.setAttr(vo.getAttr());
                    tempScreenMenuConfVO.setTabName(vo.getTabName());
                    tempScreenMenuConfVO.setIndustryViewId(industryView.getIndustryViewId());
                    tempScreenMenuConfVO.setIndustryViewType(industryView.getIndustryViewType());
                    tempScreenMenuConfVO.setViewType(industryView.getViewType());
                    tempScreenMenuConfVO.setSort(sort);
                    tempScreenMenuConfVO.setTenantId(user.getTenantId());
                    tempScreenMenuConfVO.setIndustryCode(user.getIndustryCode());
                    tempScreenMenuConfVO.setAppVerCode(user.getAppVerCode());
                    tempScreenMenuConfVO.setCreator(SecurityUtils.getOperator());
                    returnScreenMenuConfVOList.add(tempScreenMenuConfVO);
                    sort = sort + 1;
                }
            }
            num = screenMenuConfMapper.insertScreenMenuConf(returnScreenMenuConfVOList);
        }
        return num;
    }


    /**
     * 功能说明: 遍历浩鲸返回的全部大屏配置信息 确定返回的列表要添加哪些大屏
     * @author liuxt
     * @return 新增结果
     * @throws
     * @throws
     * @requestID       *
     */
    public List<ScreenMenuConfVO> foreachIndustryList(List<ScreenMenuConfVO> list,List<SysIndustryConfigInfoModel> sysIndustryConfigInfoModelDataList,int curSort) {
        for (int i = 0; i < sysIndustryConfigInfoModelDataList.size(); i++) {//遍历浩鲸返回的全部大屏配置信息 确定返回的列表要添加哪些大屏
            SysIndustryConfigInfoModel sysIndustryConfigInfoModel = sysIndustryConfigInfoModelDataList.get(i);
            String industryViewId = sysIndustryConfigInfoModel.getIndustryViewId().toString();
            Integer industryViewType = sysIndustryConfigInfoModel.getIndustryViewType();
            Integer viewType = sysIndustryConfigInfoModel.getViewType();
            boolean flag = false;
            for (int j = 0; j < list.size(); j++) {//遍历配了个性化的全部大屏配置信息
                ScreenMenuConfVO screenMenuConf = list.get(j);
                if(industryViewId.equals(screenMenuConf.getIndustryViewId()) && Objects.equals(industryViewType, screenMenuConf.getIndustryViewType()) && Objects.equals(viewType, screenMenuConf.getViewType())){
                    flag = true;
                    break;
                }
            }
            if(!flag){//浩鲸返回的有这个大屏，但是个性化没这个大屏，说明浩鲸新加的，添加到列表最后一行
                ScreenMenuConfVO tempScreenMenuConfVO = new ScreenMenuConfVO();
                tempScreenMenuConfVO.setAttr(Constants.ONE);
                tempScreenMenuConfVO.setTabName(sysIndustryConfigInfoModel.getIndustryViewName());
                tempScreenMenuConfVO.setIndustryViewId(industryViewId);
                tempScreenMenuConfVO.setIndustryViewType(industryViewType);
                tempScreenMenuConfVO.setViewType(viewType);
                tempScreenMenuConfVO.setIndustryViewName(sysIndustryConfigInfoModel.getIndustryViewName());
                tempScreenMenuConfVO.setGroupId(IdUtils.simpleUUID());
                curSort = curSort + Constants.ONE_NUM;
                tempScreenMenuConfVO.setSort(curSort);
                list.add(tempScreenMenuConfVO);
            }
        }
        return list;
    }

    /**
     * 功能说明: 遍历个性化的大屏配置信息 确定返回的列表要删除哪些大屏
     * @author liuxt
     * @return 新增结果
     * @throws
     * @throws
     * @requestID       *
     */
    public List<ScreenMenuConfVO> foreachList(List<ScreenMenuConfVO> list,List<SysIndustryConfigInfoModel> sysIndustryConfigInfoModelDataList) {
        Iterator<ScreenMenuConfVO> iterator = list.iterator();//遍历个性化的大屏配置信息 确定返回的列表要删除哪些大屏
        while (iterator.hasNext()) {
            ScreenMenuConfVO curScreenMenuConfVO = iterator.next();
            boolean flag = false;
            for (int j = 0; j < sysIndustryConfigInfoModelDataList.size(); j++) {//遍历浩鲸返回的全部大屏配置信息
                SysIndustryConfigInfoModel sysIndustryConfigInfoModel = sysIndustryConfigInfoModelDataList.get(j);
                String industryViewId = sysIndustryConfigInfoModel.getIndustryViewId().toString();
                Integer industryViewType = sysIndustryConfigInfoModel.getIndustryViewType();
                Integer viewType = sysIndustryConfigInfoModel.getViewType();
                if(industryViewId.equals(curScreenMenuConfVO.getIndustryViewId()) && Objects.equals(industryViewType, curScreenMenuConfVO.getIndustryViewType()) && Objects.equals(viewType, curScreenMenuConfVO.getViewType())){
                    curScreenMenuConfVO.setIndustryViewName(sysIndustryConfigInfoModel.getIndustryViewName());//给前端返回大屏名称
                    flag = true;
                    break;
                }
            }
            if(!flag){//个性化有这个大屏，但是浩鲸返回的没这个大屏，说明浩鲸删了，从列表中移除这个大屏
                iterator.remove();
            }
        }
        return list;
    }



}
