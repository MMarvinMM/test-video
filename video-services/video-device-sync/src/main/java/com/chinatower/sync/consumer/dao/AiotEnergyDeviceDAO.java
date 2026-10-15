package com.chinatower.sync.consumer.dao;

import com.chinatower.sync.consumer.item.AiotEnergyDeviceReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiotEnergyDeviceDAO {

    List<String> queryDeviceCodeList(@Param("list") List<String> list);

    int insertDeviceBatch(@Param("list")List<AiotEnergyDeviceReport> list);

    int updateDeviceBatch(@Param("list")List<AiotEnergyDeviceReport> list);

    List<String> queryValidOrderDeviceCodeList();

    List<String> getAllDeviceCodeList();
}
