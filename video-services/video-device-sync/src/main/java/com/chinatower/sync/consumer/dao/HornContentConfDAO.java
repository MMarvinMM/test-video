package com.chinatower.sync.consumer.dao;

import com.chinatower.sync.consumer.item.HornTxtFileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author willem
 * @date 2024/10/21
 * @description
 */
@Mapper
public interface HornContentConfDAO {
    //根据广播平台audioID更新音频上传后的url信息
    void updateAudioInfoByAudioId(@Param("params") List<Map<String,String>> params);


    void updateTxtInfoByTxtIdBatch(@Param("params") List<HornTxtFileVO> params);

}
