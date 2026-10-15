package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.platform.project.system.domain.WhiteIp;
import lombok.Data;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: WhiteIpVO
 * @description: 登录IP白名单前端传输对象
 * @author: fengty
 * @create: 2024-04-29 15:49
 */
@Data
public class WhiteIpVO {

    private List<WhiteIp> insertWhiteIps;

    private List<WhiteIp> deleteWhiteIps;

    private List<WhiteIp> updateWhiteIps;

}
