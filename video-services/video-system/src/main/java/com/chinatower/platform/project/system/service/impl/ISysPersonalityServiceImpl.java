package com.chinatower.platform.project.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.domain.vo.SysInfoSetVO;
import com.chinatower.platform.project.system.enums.SysInfoSetVOType;
import com.chinatower.platform.project.system.mapper.SysPersonalityMapper;
import com.chinatower.platform.project.system.service.ISysConfigService;
import com.chinatower.platform.project.system.service.ISysPersonalityService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chinatower.platform.constants.Constants.PLATFORM_CODE;

/**
 * @ClassName ISysPersonalityServiceImpl
 * @Description 个性化配置服务实现类
 * @Author D
 * @Date 2022/2/23 17:00
 */
@Service
@Transactional
@Slf4j
public class ISysPersonalityServiceImpl implements ISysPersonalityService {

    private static final String CONFIG_TYPE = "1";
    private static final String THEME_TYPE = "2";
    private static final String DEFAULT_LOGO_IMG_URL = "default_logo_img_url";
    private static final String DEFAULT_APP_IMG_URL = "default_app_img_url";
    private static final String DEFAULT_WEB_IMG_URL = "default_web_img_url";
    private static final String DEFAULT_WEB_NAME = "default_web_name";

    private static final String PLATFORM_NAME = "platformName";

    @Autowired
    private SysPersonalityMapper sysPersonalityMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 组装查询条件
     *
     * @param sysInfoSetVO
     */
    private void setSysInfoSetVO(SysInfoSetVO sysInfoSetVO, String type) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        sysInfoSetVO.setTenantId(null);
        sysInfoSetVO.setCreator(null);
        sysInfoSetVO.setDeptId(null);
        sysInfoSetVO.setIndustryCode(null);
        sysInfoSetVO.setType(type);
        if (SysInfoSetVOType.ZERO.getType().equals(type)) {
            sysInfoSetVO.setDataScope(null);
        }
        if (SysInfoSetVOType.ONE.getType().equals(type)) {
            sysInfoSetVO.setCreator(user.getUserName());
            sysInfoSetVO.setIndustryCode(user.getIndustryCode());
            sysInfoSetVO.setAppVerCode(user.getAppVerCode());
            sysInfoSetVO.setTenantId(user.getTenantId());
        }
        if (SysInfoSetVOType.THREE.getType().equals(type)) {
            sysInfoSetVO.setDeptId(user.getDeptId());
            sysInfoSetVO.setIndustryCode(user.getIndustryCode());
            sysInfoSetVO.setAppVerCode(user.getAppVerCode());
            sysInfoSetVO.setTenantId(user.getTenantId());
        }
        if (SysInfoSetVOType.FOUR.getType().equals(type)) {
            sysInfoSetVO.setIndustryCode(user.getIndustryCode());
            sysInfoSetVO.setAppVerCode(user.getAppVerCode());
            sysInfoSetVO.setTenantId(user.getTenantId());
        }
    }

    /**
     * 获取个性化配置
     *
     * @param sysInfoSetVO
     * @return
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code", appVerCode = "app_ver_code")
    public List<SysInfoSetVO> getInfoSetList(SysInfoSetVO sysInfoSetVO) {
        //根据登录人查询是否有个性化配置
        List<SysInfoSetVO> list = new ArrayList<>();
        //不传 本人＞本组织＞本客户 > 默认
        if (StringUtils.isEmpty(sysInfoSetVO.getType())) {
            sysInfoSetVO.setIsDefault(1);
            SysInfoSetVOType voType = SysInfoSetVOType.ONE;
            while (voType != null && (list == null || list.isEmpty())){
                setSysInfoSetVO(sysInfoSetVO, voType.getType());
                list = sysPersonalityMapper.getInfoSetList(sysInfoSetVO);
                voType = SysInfoSetVOType.get(voType.getNextType());
            }
        } else {
            //根据所传查询
            SysInfoSetVOType voType = SysInfoSetVOType.get(sysInfoSetVO.getType());
            if (voType == null){
                throw new CustomException("配置归属参数错误");
            }
            setSysInfoSetVO(sysInfoSetVO, sysInfoSetVO.getType());
            list = sysPersonalityMapper.getInfoSetList(sysInfoSetVO);
        }
        if (list.isEmpty()) {
            //配置了返回结果,没有配置取默认
            //没有配置取默认值
            setSysInfoSetVO(sysInfoSetVO,SysInfoSetVOType.ZERO.getType());
            List<SysInfoSetVO> list2 = sysPersonalityMapper.getInfoSetList(sysInfoSetVO);
            if (sysInfoSetVO.getIsDefault() == 1 && (list == null || list.isEmpty())) {
                list = list2;
            } else if (sysInfoSetVO.getIsDefault() == 1){
                Set<String> setNames = list.stream().map(SysInfoSetVO::getSetName).collect(Collectors.toSet());
                for (SysInfoSetVO vo : list2) {
                    if (!setNames.contains(vo.getSetName())) {
                        list.add(vo);
                    }
                }
            }
            //判断如果是取默认值，就取行业版本中配置的banner标题
            if (sysInfoSetVO.getIsDefault() == 1) {
                SysUser user = SecurityUtils.getLoginUser().getUser();
                String platformCode = configService.selectConfigValueByKey(PLATFORM_CODE);
                String bannerTitle = sysPersonalityMapper.findBannerTitle(user.getIndustryCode(), user.getAppVerCode(), platformCode);
                if (StringUtil.isNotEmpty(bannerTitle)) {
                    list.forEach(item -> {
                        if (PLATFORM_NAME.equals(item.getSetName())) {
                            log.info("查询到行业版本中配置的banner");
                            item.setSetValue(bannerTitle);
                        }
                    });
                } else {
                    log.info("未查到行业版本中配置的bannerTitle,故使用默认 中国铁塔视联平台");
                }
            }
        }


        try {
            if (list != null) {
                list.forEach(f -> {
                    if ("logo".equalsIgnoreCase(f.getSetName())) {
                        String s = f.getSetValue();
                        if (StringUtils.isEmpty(s)) {
                            s = configService.selectConfigValueByKey(DEFAULT_LOGO_IMG_URL);
                        }
                        f.setSetValue(fileUrlEncryptUtil.dealObsFileUrl(s));
                    } else if ("app".equalsIgnoreCase(f.getSetName())) {
                        String s = f.getSetValue();
                        if (StringUtils.isEmpty(s)) {
                            s = configService.selectConfigValueByKey(DEFAULT_APP_IMG_URL);
                        }
                        f.setSetValue(fileUrlEncryptUtil.dealObsFileUrl(s));
                    } else if ("labelIcon".equalsIgnoreCase(f.getSetName())) {
                        String s = f.getSetValue();
                        if (StringUtils.isEmpty(s)) {
                            s = configService.selectConfigValueByKey(DEFAULT_WEB_IMG_URL);
                        }
                        f.setSetValue(fileUrlEncryptUtil.dealObsFileUrl(s));
                    } else if ("labelName".equalsIgnoreCase(f.getSetName())) {
                        String s = f.getSetValue();
                        if (StringUtils.isEmpty(s)) {
                            s = configService.selectConfigValueByKey(DEFAULT_WEB_NAME);
                        }
                        f.setSetValue(s);
                    }
                });
            }
        } catch (Exception e) {
            log.error("处理错误", e);
        }
        return list;
    }

    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code", appVerCode = "app_ver_code")
    public List<SysInfoSetVO> getThemeInfoByLoginUser(SysInfoSetVO vo) {
        vo.setType(THEME_TYPE);
        return sysPersonalityMapper.getInfoSetList(vo);
    }

    @Override
    public int saveSets(List<SysInfoSetVO> voList) {
        return saveSetsByType(voList, CONFIG_TYPE);
    }


    @Override
    public int saveSetsByType(List<SysInfoSetVO> voList, String type) {
        if (voList == null || voList.isEmpty()) {
            throw new CustomException("参数不能为空");
        }
        SysInfoSetVOType voType = SysInfoSetVOType.get(type);
        if (voType == null) {
            throw new CustomException("归属配置不能为空");
        }
        /*获取当前用户*/
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();

        List<SysInfoSetVO> list = new ArrayList<>();
        voList.forEach(v -> {
            //如果保存的本组织 且组织ID有多个
            v.setIndustryCode(user.getIndustryCode());
            v.setAppVerCode(user.getAppVerCode());
            v.setSetId(IdWorker.getIdStr());
            v.setType(type);
            v.setTenantId(tenantId);
            v.setDeptId(user.getDeptId());
            list.add(v);
        });
        //如果租户id为空,说明是铁塔人员进行的配置
        if (StringUtils.isEmpty(tenantId)) {
            //根据用户删除个性化配置
            sysPersonalityMapper.delete(user.getUserName(), type, user.getAppVerCode(), user.getIndustryCode());
        } else {
            //如果租户id不为空,说明是租户人员进行的配置
            //根据租户删除个性化配置
//            sysPersonalityMapper.deleteByTenantId(tenantId, type, user.getAppVerCode(), user.getIndustryCode());
            sysPersonalityMapper.deleteBySysInfoSetVO(SysInfoSetVOType.ONE.getType(), user.getUserName(), tenantId, null, user.getIndustryCode(), null);
            //如果保存本组织 需要清楚组织的
            if (SysInfoSetVOType.THREE.getType().equals(type) || SysInfoSetVOType.FOUR.getType().equals(type)) {
                sysPersonalityMapper.deleteBySysInfoSetVO(SysInfoSetVOType.THREE.getType(), null, tenantId, null, user.getIndustryCode(), user.getDeptId());
            }
            if (SysInfoSetVOType.FOUR.getType().equals(type)) {
                sysPersonalityMapper.deleteBySysInfoSetVO(SysInfoSetVOType.FOUR.getType(), null, tenantId, null, user.getIndustryCode(), null);
            }
        }
        //新增个性化配置
        return sysPersonalityMapper.saveSets(list, user.getUserName());
    }

    @Override
    public void reset(String tenantId,String type) {

        String logoImgUrl = configService.selectConfigValueByKey(DEFAULT_LOGO_IMG_URL);
        String appImgUrl = configService.selectConfigValueByKey(DEFAULT_APP_IMG_URL);
        String webImgUrl = configService.selectConfigValueByKey(DEFAULT_WEB_IMG_URL);
        String webName = configService.selectConfigValueByKey(DEFAULT_WEB_NAME);

        List<SysInfoSetVO> list = new ArrayList<>();
        SysUser user = SecurityUtils.getLoginUser().getUser();

        String platformCode = configService.selectConfigValueByKey(PLATFORM_CODE);

        String bannerTitle = sysPersonalityMapper.findBannerTitle(user.getIndustryCode(), user.getAppVerCode(), platformCode);
        if (StringUtil.isNotEmpty(bannerTitle)) {
            this.setDefault(list, PLATFORM_NAME, bannerTitle);
        } else {
            this.setDefault(list, PLATFORM_NAME, "中国铁塔视联平台");
            log.info("未查到行业版本中配置的bannerTitle,故使用默认 中国铁塔视联平台");
        }

        //TODO 值
        this.setDefault(list, "logo", logoImgUrl);
        this.setDefault(list, "app", appImgUrl);
        this.setDefault(list, "labelIcon", webImgUrl);
        this.setDefault(list, "labelName", webName);
        saveSetsByType(list,SysInfoSetVOType.FOUR.getType());
    }
    private void setDefault(List<SysInfoSetVO> list,String setName, String value){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        SysInfoSetVO infoSetVO = new SysInfoSetVO();
        infoSetVO.setSetName(setName);
        infoSetVO.setSetValue(value);
        infoSetVO.setSetId(IdWorker.getIdStr());
        infoSetVO.setCreator(user.getUserName());
        infoSetVO.setCreateTime(new Date());
        list.add(infoSetVO);
    }

    private void saveOrUpdateValue(String tenantId, String type, String setName, String value) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        SysInfoSetVO infoSetVO = this.sysPersonalityMapper.selectOne(tenantId, type, setName, user.getAppVerCode(), user.getIndustryCode());

        if (infoSetVO == null) {
            infoSetVO = new SysInfoSetVO();
            infoSetVO.setTenantId(tenantId);
            infoSetVO.setType(type);
            infoSetVO.setSetName(setName);
            infoSetVO.setSetValue(value);
            infoSetVO.setSetId(IdWorker.getIdStr());
            infoSetVO.setCreator(user.getUserName());
            infoSetVO.setCreateTime(new Date());
            infoSetVO.setIndustryCode(user.getIndustryCode());
            infoSetVO.setAppVerCode(user.getAppVerCode());
            if (SysInfoSetVOType.THREE.getType().equals(type)){
                infoSetVO.setDeptId(user.getDeptId());
            }
            sysPersonalityMapper.insert(infoSetVO);
        } else {
            infoSetVO.setSetValue(value);
            sysPersonalityMapper.updateById(infoSetVO);
        }
    }

}
