package com.chinatower.platform.mapper;

import com.chinatower.bdm.api.entity.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BmAttendanceExportMapper {

    List<BmAttendanceListVO> getList(BmAttendanceParam bmAttendanceParam);

    List<GridKeeperRefVO> getGridNamesByKeeper(BmAttendanceHistoryListVO vo);

    List<GridKeeperRefVO> getGridNamesByKeeperBatch(List<String> list);

    List<BmAttendanceHistoryListVO> getHistoryList(BmAttendanceHistoryParam bmAttendanceHistoryParam);


}
