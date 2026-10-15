package com.chinatower.platform.auth.project.mapper;

import com.chinatower.platform.auth.project.entity.SsoSecretSyncRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * SSO登录相关 Mapper
 */
@Mapper
public interface SsoLoginMapper {

    int insertSecretSyncRecord(SsoSecretSyncRecord record);

    SsoSecretSyncRecord selectLatestSecretSync();
}
