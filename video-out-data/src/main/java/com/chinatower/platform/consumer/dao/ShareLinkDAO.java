package com.chinatower.platform.consumer.dao;

import com.chinatower.platform.consumer.item.ShareLinkVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShareLinkDAO {

    ShareLinkVO getShareLinkInfoByShortUrl(@Param("shortUrl")String shortUrl);

    int save(@Param("entity") ShareLinkVO entity);
}
