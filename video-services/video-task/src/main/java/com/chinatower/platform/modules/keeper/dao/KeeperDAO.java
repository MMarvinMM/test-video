package com.chinatower.platform.modules.keeper.dao;

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
public interface KeeperDAO {

    /**
     * 获取巡查配置数据
     * @return
     */
    List<InspectionTaskVO> getInspectionConfList();

    /**
     * 插入巡查任务
     * @param list
     * @return
     */
    int insertInspectionTask(List<InspectionTaskVO> list);

    /**
     * 更新巡查配置
     * @return
     */
    int updateNoRepeatConfStatus();

    /**
     * 更新签到
     * @return
     */
    int updateStatusIn();

    /**
     * 更新签退
     * @return
     */
    int updateStatusOut();

    /**
     * 插入考勤表
     * @return
     */
    int insertAttendanceLine();

    /**
     * 查询网格员信息
     * @param weekDay
     * @return
     */
    List<BmAttendanceLineTaskVO> getKeeperInfo(String weekDay);

    /**
     * 查询网格员考勤信息
     * @param list
     */
    void insertAttendanceLineInfo(List<BmAttendanceLineTaskVO> list);

    /**
     * 查询巡查配置列表
     * @param format
     * @return
     */
    List<InspectionTaskVO> getInspectionConfListTest(@Param("format") String format);

    /**
     * 修改巡查完成状态
     * @return
     */
    int updateRepeatConfStatus();

    /**
     * 查询字典项
     * @param dictType
     * @param week
     * @return
     */
    String selectDictDataByType(@Param("dictType") String dictType,@Param("week") String week);

    /**
     * 更新巡查信息下发状态
     * @param taskIdList
     * @return
     */
    int updateTaskWatchStatus(List<String> taskIdList);

    /**
     * 根据网格员ID查询网格员信息
     * @param keeperList
     * @return
     */
    List<KeeperVO> getUserIdByKeerperList(List<String> keeperList);
}
