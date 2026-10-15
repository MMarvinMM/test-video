package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.WhiteIp;
import com.github.pagehelper.Page;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: WhiteIpMapper
 * @description: 用户登录IP白名单数据传输接口
 * @author: fengty
 * @create: 2024-04-29 13:26
 */
public interface WhiteIpMapper {
    Integer insertIps(List<WhiteIp> whiteIpList);

    Integer updateIps(List<WhiteIp> whiteIpList);

    Page<WhiteIp> selectIps(WhiteIp whiteIp);
}
