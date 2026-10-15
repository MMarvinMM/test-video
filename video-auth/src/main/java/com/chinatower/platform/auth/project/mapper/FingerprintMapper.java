package com.chinatower.platform.auth.project.mapper;

import com.chinatower.platform.auth.project.entity.FingerprintVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 指纹登录Mapper接口
 *
 * @author system
 * @date 2026-05-11
 */
public interface FingerprintMapper {


    /**
     * 插入或更新指纹登录开关状态
     *
     * @param vo 指纹登录实体
     * @return 影响行数
     */
    int upsertFingerprintEnabled(FingerprintVO vo);

    /**
     * 查询指纹登录完整信息（开关状态、开启时间）
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 指纹登录实体
     */
    FingerprintVO selectFingerprintInfo(@Param("userId") String userId,
                                        @Param("deviceId") String deviceId);

    /**
     * 更新指纹开启时间
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 影响行数
     */
    int updateEnabledTime(@Param("userId") String userId, @Param("deviceId") String deviceId);


}