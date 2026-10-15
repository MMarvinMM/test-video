package com.chinatower.event.mapper;

import com.chinatower.common.core.dto.AlarmTypeClass;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 告警类型参数mapper
 * @author chmod
 * @version 1.0
 * @date 2022/3/11 14:15
 */
@Mapper
public interface AlarmTypeClassMapper {
    /**
     * @Description 删除
     * @author wjd
     * @Date 15:24 2021/7/7
     * @Param [typeClassId]
     **/
    int deleteByPrimaryKey(String typeClassId);

    /**
     * @Description 新增
     * @author wjd
     * @Date 15:24 2021/7/7
     * @Param [record]
     **/
    int insert(@Param("list") List<AlarmTypeClass> list);
    /**
     * @Description 部分新增
     * @author wjd
     * @Date 15:24 2021/7/7
     * @Param [record]
     **/
    int insertSelective(AlarmTypeClass record);

    /**
     * @Description 查询详情
     * @author wjd
     * @Date 15:25 2021/7/7
     * @Param [typeClassId]
     **/
    AlarmTypeClass selectByPrimaryKey(String typeClassId);

    /**
     * @Description 部分更新
     * @author wjd
     * @Date 15:25 2021/7/7
     * @Param [record]
     **/
    int updateByPrimaryKeySelective(AlarmTypeClass record);

    /**
     * @Description 更新
     * @author wjd
     * @Date 15:25 2021/7/7
     * @Param [record]
     **/
    int updateByPrimaryKey(AlarmTypeClass record);

    /**
     * @Description 获取数量
     * @author wjd
     * @Date 15:25 2021/7/7
     * @Param [body]
     **/
    int getCount(@Param("body") AlarmTypeClassDTO body);

    /**
     * @Description 根据分类ID删除
     * @author wjd
     * @Date 15:25 2021/7/7
     * @Param [alarmTypeClassId]
     **/
    int deleteByTypeClassId(String alarmTypeClassId);

    /**
     * @Description 删除未分配分类
     * @author wjd
     * @Date 15:38 2021/7/7
     * @Param []
     **/
    int deleteByZero();

    int deletes(@Param("ids") List<String> ids);

    int deleteTypes(@Param("ids") List<String> ids);

    int deleteTimes(@Param("ids") List<String> ids);

    int deleteParam(@Param("ids") List<String> ids);

    List<AlarmTypeClass> findOrderAlarmListByTenant(@Param("body") AlarmTypeClassDTO body);

    int insertAlarmTypeClass(AlarmTypeClass alarmTypeClass);

    int insertAlarmTypeClassAndNoticeMod(AlarmTypeClass alarmTypeClass);

    List<String> findAlarmTypeClassIdExist(AlarmTypeClass alarmTypeClass);
}
