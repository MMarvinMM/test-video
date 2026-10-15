package com.chinatower.platform.auth.project.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.auth.project.entity.dto.StatisticsUserOnlineTimeDTO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysLogininfor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统访问日志情况信息 数据层
 *
 * @author bruce
 */
@Mapper
public interface SysLogininforMapper
{
    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(SysLogininfor logininfor);

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    public int deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     *
     * @return 结果
     */
    public int cleanLogininfor();


    /**
     * 查询用户历史在线时长
     */
    @DS(EbikeDataBaseConstant.STATISTIC)
    StatisticsUserOnlineTimeDTO selectUserOnlineTime(@Param("userId") String userId, @Param("loginTime") String loginTime);

    /**
     * 新增&修改用户登录时长
     * @param onlineTimeDTO
     * @return
     */
    @DS(EbikeDataBaseConstant.STATISTIC)
    int addUserOnlineTime(StatisticsUserOnlineTimeDTO onlineTimeDTO);
    @DS(EbikeDataBaseConstant.STATISTIC)
    int updateUserOnlineTime(StatisticsUserOnlineTimeDTO onlineTimeDTO);

    StatisticsUserOnlineTimeDTO selectUserLoginInfo(String userId);
}
