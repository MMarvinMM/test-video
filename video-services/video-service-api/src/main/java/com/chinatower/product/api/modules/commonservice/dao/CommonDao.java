package com.chinatower.product.api.modules.commonservice.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @ClassName: CommonDao
 * @Description:
 * @Author: lijiwei
 * @Date: 2021-05-31 16:16
 **/
public interface CommonDao {

    List<String> getOrderCode(@Param("customerCode") String customerCode, @Param("deviceCode") String deviceCode);
}
