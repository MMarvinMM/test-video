package com.chinatower.platform.project.numtwin.mapper;

import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.project.numtwin.entity.NumTwinsConfigDto;
import com.chinatower.platform.project.numtwin.entity.NumTwinsPriorityDto;
import com.chinatower.platform.project.numtwin.entity.NumTwinsUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/12 11:15
 */

@Mapper
public interface NumTwinsMapper {

    /**
     * 保存数字孪生配置
     * @return
     */
    Integer save(NumTwinsConfigDto dto);

    /**
     * 修改数字孪生配置
     * @return
     */
    Integer update(NumTwinsConfigDto dto);

    /**
     * 根据租户行业获取配置详情
     * @param dto
     * @return
     */
    NumTwinsConfigDto getInfo(NumTwinsConfigDto dto);

    /**
     * 保存优先级配置
     * @return
     */
    Integer saveList(@Param("list") List<NumTwinsPriorityDto> list);

    /**
     * 修改配置
     * @return
     */
    Integer updateList(@Param("list") List<NumTwinsPriorityDto> list,@Param("updateUser") String updateUser);

    /**
     * 修改配置
     * @return
     */
    Integer deleteUserList(@Param("list") List<String> userId,@Param("configId") String configId);

    /**
     * 获取租户行业下所有用户
     * @param tenantId
     * @return
     */
    List<NumTwinsPriorityDto> getAllUserId(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 查询用户优先级列表
     * @param req
     * @return
     */
    List<NumTwinsUser> getNumTwinsUserList(NumTwinsUser req);

    /**
     * 查询用户优先级列表
     * @return
     */
    List<SysDept> selectDeptList(@Param("tenantId") String tenantId,@Param("industryCode") String industryCode);
}
