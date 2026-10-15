package com.chinatower.platform.modules.user.dao;

import com.chinatower.platform.modules.keeper.entity.BmAttendanceLineTaskVO;
import com.chinatower.platform.modules.keeper.entity.InspectionTaskVO;
import com.chinatower.platform.modules.keeper.entity.KeeperVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 网格员考勤、巡查定时
 */
@Mapper
public interface UserDAO {


    /**
     * 用户停用
     * @return
     */
    int updateUserStatus();


}
