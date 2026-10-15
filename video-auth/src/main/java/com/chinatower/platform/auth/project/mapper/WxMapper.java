package com.chinatower.platform.auth.project.mapper;

import com.chinatower.auth.api.entity.WxColonyCodeVo;
import com.chinatower.platform.auth.model.WxLoginVo;
import com.chinatower.platform.model.dto.CascadeConfigSyncVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @title
 * @description 公众号业务持久层
 * @author zhujun
 * @updateTime 2022/11/15
 * @throws
 */
public interface WxMapper {

    Map<String, String> loginByWx(@Param("unionId") String unionId);

    Map<String, String> getIndustryCodeByWx(WxLoginVo vo);

    List<Map<String, String>> getBandUserByWx(WxLoginVo vo);

    String getReceiveMsgState(WxLoginVo vo);

    int releaseBindUser(WxLoginVo vo);

    int bindUser(WxLoginVo vo);

    int updateBindUser(WxLoginVo vo);

    int getWxUserCount(WxLoginVo vo);

    int insertWxUser(WxLoginVo vo);

    int updateWxUser(WxLoginVo vo);

    int updateUnSubscribe(@Param("openId") String openId, @Param("status") String status);

    int updateIndustryCode(WxLoginVo vo);

    List<Map<String,String>> findUserByUserId(String userId);

    void invalidUser(String userId);

    int wxUpdateMsgControl(WxLoginVo vo);

    void colonyBind(WxColonyCodeVo vo);

    int selectColonyUser(WxColonyCodeVo vo);

    void update(WxColonyCodeVo vo);

    String selectColonyCodeByUnionId(@Param("unionId")String unionId);

    CascadeConfigSyncVO findCascadeByCode(@Param("platformCode") String platformCode);

    String getOpenIdByUnionId(String unionId);
}
