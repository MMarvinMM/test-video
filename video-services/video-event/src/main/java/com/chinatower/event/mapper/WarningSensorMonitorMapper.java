package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.WarningSensorMonitorVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 告警事件关联物联设备检测因子信息 Mapper 接口
 * </p>
 *
 * @author luo.zhenghong
 * @since 2022-07-25
 */
@Mapper
@DS(EbikeDataBaseConstant.PLATFORM)
public interface WarningSensorMonitorMapper {

    /**
     * 创建联物联设备检测因子信息表
     * @param tableNewName
     * @param tableOldName
     * @return
     */
    int createNewTable(@Param("tableNewName") String tableNewName, @Param("tableOldName") String tableOldName);

    /**
     * 批量插入联物联设备检测因子信息数据
     * @return
     */
    int batchInsert(@Param("tableName") String tableName, @Param("list") List<WarningSensorMonitorVO> list);

}
