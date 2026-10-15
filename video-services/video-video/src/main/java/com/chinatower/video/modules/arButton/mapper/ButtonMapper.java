package com.chinatower.video.modules.arButton.mapper;

import com.chinatower.video.modules.arButton.entity.ArButtonInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: ButtonMapper
 * @description: AR大屏，配置按钮mapper
 * @author: fengty
 * @create: 2024-04-07 11:05
 */
@Repository
public interface ButtonMapper {

    int insert(ArButtonInfo buttonInfo);

    int updateById(ArButtonInfo buttonInfo);

    ArButtonInfo selectById(Long id);


    int selectCountByConditions(ArButtonInfo buttonInfo);

    List<ArButtonInfo> pageArButtonInfo(ArButtonInfo buttonInfo);
}
