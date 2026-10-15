package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.comparator.ChineseComparator;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysIndustryConf;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.project.system.domain.vo.AssociateDemoAccountVO;
import com.chinatower.platform.project.system.domain.vo.IndustryQueryVO;
import com.chinatower.platform.project.system.domain.vo.IndustrySelectVO;
import com.chinatower.platform.project.system.mapper.SysIndustryConfMapper;
import com.chinatower.platform.project.system.service.ISysDictDataService;
import com.chinatower.platform.project.system.service.ISysIndustyService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.IndustryConfDto;
import com.chinatower.system.api.model.IndustryTypeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能说明:
 * @param:
 * @return:
 * @author: 王梦迪
 * @date: 2022/3/18 9:16
 * @requestID  需求单号及需求名称
 */
@Slf4j
@Service
public class SysIndustyServiceImpl implements ISysIndustyService {

    @Autowired
    private SysIndustryConfMapper sysIndustryConfMapper;

    @Autowired
    private FileUrlEncryptUtil obsFileUrlEncryptDeal;

    @Resource
    ISysDictDataService sysDictDataService;

    private final String TOWER_TYPE = "0";

    private final String TOWER_CHOOSE_TENANT_ID = "tower_choose_tenant_id";
    private final String TOWER_CHOOSE_INDUSTRY_NAME = "tower_choose_industry_name";

    @Override
    public List<SysIndustryConf> selectIndustryConf() {
        String acceptNode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        return sysIndustryConfMapper.selectIndustryConf(acceptNode);
    }

    @Override
    public List<SysIndustryViewRel> selectIndustryViewRel(String industryCode) {
        return sysIndustryConfMapper.selectIndustryViewRel(industryCode);
    }
    @Override
    public List<SysIndustryViewRel> selectAllIndustryViewRel() {
        return sysIndustryConfMapper.selectAllIndustryViewRel();
    }

    @Override
    public List<IndustrySelectVO> selectIndustryByUserId(String userId) {
        return sysIndustryConfMapper.selectIndustryByUserId(userId,SecurityUtils.getLoginUser().getUser().getPlatformCode());
    }

    @Override
    public List<IndustryTypeDto> allByUserPc(){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //根据用户登录类型获取用户信息
        boolean associateFlag = false;
        Map<String, String> industryUsername = new HashMap<>();
        String username = getUserNameByAssociateDemoAccount(user);
        if(StringUtils.isEmpty(username)){
            username = this.getUserName(user);
            //获取不同行业配置的用户名
            industryUsername = getIndustryConfigUsername(user);
        }else{
            associateFlag = true;
        }
        String platformCode = user.getPlatformCode();
        List<IndustryTypeDto> typeList = sysIndustryConfMapper.allByUserPc(username,platformCode);

        List<String> urls = new ArrayList<>();
        List<IndustryConfDto> confList = new ArrayList<>();
        //批量加密url
        for (IndustryTypeDto industryTypeDto : typeList) {
            //禁用这种写法，userName会报错
//            industryTypeDto.getList().forEach(item -> {
//                setObsUrlList(item, urls);
//                //设置username
//                if (TOWER_TYPE.equals(user.getUserType())) {
//                    item.setUsername(industryUsername.getOrDefault(item.getIndustryCode(), username));
//                } else {
//                    //设置username
//                    item.setUsername(username);
//                }
//            });
            for (int i = 0; i < industryTypeDto.getList().size(); i++) {
                setObsUrlList(industryTypeDto.getList().get(i), urls);
                //设置username
                if (TOWER_TYPE.equals(user.getUserType()) && !associateFlag) {
                    industryTypeDto.getList().get(i).setUsername(industryUsername.getOrDefault(industryTypeDto.getList().get(i).getIndustryCode(), username));
                } else {
                    //设置username
                    industryTypeDto.getList().get(i).setUsername(username);
                }
            }
            confList.addAll(industryTypeDto.getList());
        }
        //url加密
        this.dealObsFileUrlBatch(confList,urls,platformCode);
        return typeList;
    }
    @Override
    public List<IndustryConfDto> allByUser() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //根据用户登录类型获取用户信息
        boolean associateFlag = false;
        Map<String, String> industryUsername = new HashMap<>();
        String username = getUserNameByAssociateDemoAccount(user);
        if(StringUtils.isEmpty(username)){
            username = this.getUserName(user);
            //获取不同行业配置的用户名
            industryUsername = getIndustryConfigUsername(user);
        }else{
            associateFlag = true;
        }

        String platformCode = user.getPlatformCode();

        List<IndustryConfDto> list = sysIndustryConfMapper.allByUser(username,platformCode);
        //排序
        list.sort(Comparator.comparing(IndustryConfDto::getProductSeq)
                .thenComparing(IndustryConfDto::getIndustryName, new ChineseComparator()));
        List<String> urls = new ArrayList<>();

        //批量加密url
//        list.forEach(item -> {
//            setObsUrlList(item, urls);
//            if (TOWER_TYPE.equals(user.getUserType())) {
//                item.setUsername(industryUsername.getOrDefault(item.getIndustryCode(), username));
//            } else {
//                //设置username
//                item.setUsername(username);
//            }
//
//        });

        for (int i = 0; i < list.size(); i++) {
            setObsUrlList(list.get(i), urls);
            //设置username
            if (TOWER_TYPE.equals(user.getUserType()) && !associateFlag) {
                list.get(i).setUsername(industryUsername.getOrDefault(list.get(i).getIndustryCode(), username));
            } else {
                //设置username
                list.get(i).setUsername(username);
            }
        }

        //url加密
        this.dealObsFileUrlBatch(list,urls,platformCode);
        return list;
    }

    private Map<String, String> getIndustryConfigUsername(SysUser user) {
        Map<String, String> industryUsername = new HashMap<>();
        if (TOWER_TYPE.equals(user.getUserType())) {
            List<SysDictData> sysDictData = sysDictDataService.selectDictDataByType(TOWER_CHOOSE_INDUSTRY_NAME);
            if (!sysDictData.isEmpty()) {
                for (SysDictData sysDictDatum : sysDictData) {
                    industryUsername.put(sysDictDatum.getDictLabel(), sysDictDatum.getDictValue());
                }
            }
        }
        return industryUsername;
    }

    private String getUserName(SysUser user){
        String username = "";
        if (TOWER_TYPE.equals(user.getUserType())) {
            List<SysDictData> sysDictData = sysDictDataService.selectDictDataByType(TOWER_CHOOSE_TENANT_ID);
            String tenantId = sysDictData.isEmpty() ? null : sysDictData.get(0).getDictValue();
            //查询用户名
            if (StringUtils.isNotEmpty(tenantId)) {
                username = sysIndustryConfMapper.findUsernameByTenantId(tenantId);
            } else {
                throw new CustomException("当前铁塔账号未配置登陆选择行业用户", 400);
            }
        } else {
            username = user.getUserName();
        }
        return username;
    }

    /**
     *
     * @param user
     * @return
     */
    private String getUserNameByAssociateDemoAccount(SysUser user){
        String username = "";
        if (TOWER_TYPE.equals(user.getUserType())) {
            //根据登录用户id，先查询【sys_associate_demo_account_user_rel_t】关联演示账号与用户关系表
            String associateId = "";
            AssociateDemoAccountVO associateDemoAccountVO = new AssociateDemoAccountVO();
            associateDemoAccountVO.setUserId(user.getUserId());
            List<AssociateDemoAccountVO> userAssociateIdList = sysIndustryConfMapper.queryAssociateDemoAccountUserRelList(associateDemoAccountVO);
            if(!CollectionUtils.isEmpty(userAssociateIdList)){
                //如果不为空，取出id，查出【sys_associate_demo_account_t】关联演示账号表对应的客户id
                associateId = userAssociateIdList.get(0).getAssociateId();
            }else{
                //如果为空，根据登录用户的组织id，查询【sys_associate_demo_account_dept_rel_t】关联演示账号与组织关系表
                associateDemoAccountVO.setDeptId(user.getDeptId());
                List<AssociateDemoAccountVO> deptAssociateIdList = sysIndustryConfMapper.queryAssociateDemoAccountDeptRelList(associateDemoAccountVO);
                if(!CollectionUtils.isEmpty(deptAssociateIdList)){
                    associateId = deptAssociateIdList.get(0).getAssociateId();
                }
            }
            if(StringUtils.isNotEmpty(associateId)){
                associateDemoAccountVO.setId(associateId);
                String tenantId = sysIndustryConfMapper.getAssociateDemoAccountInfo(associateDemoAccountVO);
                //查询用户名
                if (StringUtils.isNotEmpty(tenantId)) {
                    username = sysIndustryConfMapper.findUsernameByTenantId(tenantId);
                } else {
                    throw new CustomException("当前铁塔账号未配置登陆选择行业用户", 400);
                }
            }
        }else {
            username = user.getUserName();
        }
        return username;
    }

    private void dealObsFileUrlBatch(List<IndustryConfDto> list, List<String> urls, String platformCode){
        Map<String, String> urlMap;
        try {
            log.info("请求文件加密开始");
            // 当前节点编码
            String currentColonyCode = platformCode;
            String centerColonyCode = "10000000001";
            // 当前是中心节点，按照正常查询
            if (StringUtils.equals(currentColonyCode, centerColonyCode)) {
                urlMap = obsFileUrlEncryptDeal.dealObsFileUrlBatch(new ArrayList<>(urls));
            }
            // 当前不是中心节点，需要查询中心节点的ofs
            else {
                urlMap = obsFileUrlEncryptDeal.dealObsFileUrlBatch(urls, true, centerColonyCode);
            }

            log.info("文件加密批量加密:入参:{},出参:{}", JSON.toJSONString(urls), JSON.toJSONString(urlMap));
            list.forEach(item -> getObsUrlEncrypt(urlMap, item));
            log.info("文件加密结束");
        } catch (Exception e) {
            log.error("批量obs加密异常", e);
        }
    }

    @Override
    public IndustryConfDto detailByIndustryCode(String industryCode) {
        IndustryConfDto industryConfDto = sysIndustryConfMapper.detailByIndustryCode(industryCode,SecurityUtils.getLoginUser().getUser().getPlatformCode());
        List<String> urls = new ArrayList<>();
        //批量加密url
        setObsUrlList(industryConfDto, urls);
        Map<String, String> urlMap;
        try {
            log.info("请求文件加密开始");
            urlMap = obsFileUrlEncryptDeal.dealObsFileUrlBatch(new ArrayList<>(urls));
            log.info("文件加密批量加密:入参:{},出参:{}", JSON.toJSONString(urls), JSON.toJSONString(urlMap));
            //加密后的数据放回原对象
            getObsUrlEncrypt(urlMap, industryConfDto);
            log.info("文件加密结束");
        } catch (Exception e) {
            log.error("getDeviceImage批量obs加密异常", e);
        }

        return null;
    }

    @Override
    public List<IndustrySelectVO> queryIndustryByKeyWord(IndustryQueryVO industryQueryVO) {
        String acceptNode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        industryQueryVO.setAcceptNode(acceptNode);
        return sysIndustryConfMapper.queryIndustryByKeyWord(industryQueryVO);
    }

    @Override
    public List<IndustrySelectVO> queryAppVerCodeByKeyWord(IndustryQueryVO industryQueryVO) {
        String acceptNode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        industryQueryVO.setAcceptNode(acceptNode);
        return sysIndustryConfMapper.queryAppVerCodeByKeyWord(industryQueryVO);
    }

    @Override
    public List<IndustrySelectVO> initIndustryByKeyWord(IndustryQueryVO industryQueryVO) {
        String acceptNode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        industryQueryVO.setAcceptNode(acceptNode);
        return sysIndustryConfMapper.initIndustryByKeyWord(industryQueryVO);
    }

    @Override
    public Integer allByUserCount() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //根据用户登录类型获取用户信息
        String username = getUserNameByAssociateDemoAccount(user);
        if(StringUtils.isEmpty(username)){
            username = this.getUserName(user);
        }
        String platformCode = user.getPlatformCode();
        return sysIndustryConfMapper.allByUserCount(username,platformCode);
    }

    /**
     * 构建obs批量加密请求入参
     */
    private void setObsUrlList(IndustryConfDto industryConfDto, List<String> urls) {
        urls.add(industryConfDto.getImgUrl());
        urls.add(industryConfDto.getMobileAppIconUrl());
        urls.add(industryConfDto.getLoginDisplayImgUrl());
        if (StringUtils.isNotEmpty(industryConfDto.getProductBriefInfo())) {
            String[] split = industryConfDto.getProductBriefInfo().split(",");
            urls.addAll(Arrays.asList(split));
        }
    }

    /**
     * 加密后替换
     */
    private void getObsUrlEncrypt(Map<String, String> urlMap, IndustryConfDto item) {
        item.setImgUrl(urlMap.get(item.getImgUrl()));
        item.setMobileAppIconUrl(urlMap.get(item.getMobileAppIconUrl()));
        item.setLoginDisplayImgUrl(urlMap.get(item.getLoginDisplayImgUrl()));
        if (StringUtils.isNotEmpty(item.getProductBriefInfo())) {
            String[] split = item.getProductBriefInfo().split(",");
            List<String> productBriefInfoByEncryptList = new ArrayList<>();
            for (String s : split) {
                String s1 = urlMap.get(s);
                productBriefInfoByEncryptList.add(s1);
            }
            String productBriefInfoByEncrypt = CollUtil.join(productBriefInfoByEncryptList, ",");
            item.setProductBriefInfo(productBriefInfoByEncrypt);
        }
    }

}
