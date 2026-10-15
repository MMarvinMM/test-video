package com.chinatower.video.modules.arButton.mapper;

import com.chinatower.video.modules.arButton.entity.ArButtonAttachInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @program: video-platform
 * @ClassName: ButtonAttachMapper
 * @description: AR大屏，配置按钮附件持久层
 * @author: fengty
 * @create: 2024-04-07 15:29
 */
@Repository
public interface ButtonAttachMapper {

    void insert(ArButtonAttachInfo attachInfo);

    ArButtonAttachInfo selectById(int id);

    List<ArButtonAttachInfo> selectByCondition(ArButtonAttachInfo attachInfo);

    int selectCountByCondition(ArButtonAttachInfo attachInfo);

    void batchInsert(List<ArButtonAttachInfo> buttonAttachInfoList);

    int updateButtonAttachByButtonId(ArButtonAttachInfo attachInfo);

    int batchLogicDelete(@Param("deleteIds") List<Long> deleteIds, @Param("modifier") String modifier);
}
