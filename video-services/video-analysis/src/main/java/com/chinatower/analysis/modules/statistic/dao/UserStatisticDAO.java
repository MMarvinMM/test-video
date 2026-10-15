package com.chinatower.analysis.modules.statistic.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.CustomVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserStatisticDAO {

    /**
     * 用户总览
     * @param customVO
     * @return
     */
    @DS("slave")
    Map getCustomNum(CustomVO customVO);

    /**
     * 用户总览
     * @param customVO
     * @return
     */
    @DS("slave")
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,tenantColumn="dataTable.tenant_id",
            industryCode="sd.industry_code", deptColumn = "sd.dept_id")
    List<String> getCustom(CustomVO customVO);

    /**
     * 用户总览
     * @param customVO
     * @return
     */
    @DS("slave")
    Map getCustomDeptNum(CustomVO customVO);

    /**
     * 用户数量按组织排行
     * @param customVO
     * @return
     */
    List<Map> getCustomNumByDept(CustomVO customVO);

    /**
     * 用户登录24小时分布
     * @param customVO
     * @return
     */
    List<Map> getLoginNumByHour(CustomVO customVO);

    /**
     * 用户登录时间统计
     * @param customVO
     * @return
     */
    List<Map> getCustomNumByLoginTime(CustomVO customVO);

    /**
     * 用户登录次数排行
     * @param customVO
     * @return
     */
    List<Map> getLoginNumRank(CustomVO customVO);

    /**
     * 用户登录时长排行
     * @param customVO
     * @return
     */
    List<Map> getLoginDurationRank(CustomVO customVO);

    /**
     * PC操作系统
     * @param customVO
     * @return
     */
    List<Map> getLoginTypeByPcOs(CustomVO customVO);

    /**
     * PC浏览器排行
     * @param customVO
     * @return
     */
    List<Map> getLoginTypeByPcBrower(CustomVO customVO);

    /**
     * APP操作系统
     * @param customVO
     * @return
     */
    List<Map> getLoginTypeByAppOs(CustomVO customVO);

    /**
     * APP手机型号排行
     * @param customVO
     * @return
     */
    List<Map> getLoginTypeByPhoneModel(CustomVO customVO);

    /**
     * APP版本排行
     * @param customVO
     * @return
     */
    List<Map> getLoginTypeByAppVersion(CustomVO customVO);

    /**
     * 用户登录成功率分析
     * @param customVO
     * @return
     */
    List<Map> getLoginSuccessRate(CustomVO customVO);

    /**
     * 用户登录失败原因分析
     * @param customVO
     * @return
     */
    List<Map> getLoginFailureRate(CustomVO customVO);

    /**
     * APP调用：用户登录方式分析
     * @param customVO
     * @return
     */
    List<Map> getCustomNumByLoginStyle(CustomVO customVO);
}
