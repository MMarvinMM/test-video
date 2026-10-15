package com.chinatower.bdm.modules.bmattendance.dao;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.bmattendance.entity.BmAttendanceLineBdmVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface BmAttendanceDAO {

    List<BmAttendanceListVO> getList(BmAttendanceParam bmAttendanceParam);

    /**
     * 根据网格员id查询归属的网格名称，用,拼接
     * @param vo
     * @return
     */
    List<GridKeeperRefVO> getGridNamesByKeeper(BmAttendanceHistoryListVO vo);

    List<GridKeeperRefVO> getGridNamesByKeeperBatch(List<String> list);


    List<BmAttendanceHistoryListVO> getHistoryList(BmAttendanceHistoryParam bmAttendanceHistoryParam);

    /**
     * 新增考勤设置
     * @param vo
     * @return
     */
    int insertSetData(BmAttendanceSetVO vo);

    /**
     * 修改考勤设置
     * @param vo
     * @return
     */
    int updateSetData(BmAttendanceSetVO vo);

    /**
     * 根据租户id、行业编码查询考勤设置
     * @param bmAttendanceSetVO
     * @return
     */
    BmAttendanceSetVO findOneSetData(BmAttendanceSetVO bmAttendanceSetVO);

    /**
     * 查询单日考勤
     * @param vo
     * @return
     */
    BmAttendanceLineVO findOneByKeeper(BmAttendanceLineVO vo);

    /**
     * 签到、签退
     * @param vo
     * @return
     */
    int updateSignData(BmAttendanceLineVO vo);

    AttendanceStatisticsVO getAttendanceStatistics(AttendanceStatisticsParam vo);

    AttendanceCurrentMonthStatisticsVO getCurrentMonthStatistics(AttendanceStatisticsParam vo);

    BmAttendanceLineVO getCurrentDayLine(AttendanceStatisticsParam vo);

    BmAttendanceUserVO getAttendanceUserInfo(BmAttendanceUserVO vo);

    List<BmAttendanceUserVO> getAttendanceUserGridInfo(BmAttendanceUserVO vo);

    List<BmAttendanceLineBdmVO> getKeeperInfo(String weekDay,String keeperId);

    String selectDictDataByType(@Param("dictType") String dictType, @Param("week") String week);

    void insertAttendanceLineInfo(List<BmAttendanceLineBdmVO> list);

    List<AttendanceStatisticsDetailVO> getAttendanceStatisticsDetail(AttendanceStatisticsParam vo);

    List<String> getAllLineList(AttendanceStatisticsParam param);

}
