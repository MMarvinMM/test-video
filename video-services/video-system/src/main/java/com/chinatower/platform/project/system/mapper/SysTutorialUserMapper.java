package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.SysTutorialUser;
import com.chinatower.platform.project.system.domain.vo.TutorialIndustryVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description 用户教程
 * @author luo_ji
 * @date 2022-12-15
 */
@Mapper
@Repository
public interface SysTutorialUserMapper {

    /**
     * 新增
     * @author luo_ji
     * @date 2022/12/15
     **/
    int insert(SysTutorialUser sysTutorialUser);

    /**
     * 刪除
     * @author luo_ji
     * @date 2022/12/15
     **/
    int delete(Long tutorialId);

    /**
     * 更新
     * @author luo_ji
     * @date 2022/12/15
     **/
    int update(SysTutorialUser sysTutorialUser);

    /**
     * 查询 根据主键 id 查询
     * @author luo_ji
     * @date 2022/12/15
     **/
    SysTutorialUser load(Long tutorialId);

    /**
     * 查询 分页查询
     * @author luo_ji
     * @date 2022/12/15
     **/
    List<SysTutorialUser> pageList(SysTutorialUser sysTutorialUser);


    String checkRepeat(SysTutorialUser sysTutorialUser);

    /**
     * 查询所属行业接口
     * @param tutorialIndustryVO
     * @return
     */
    List<TutorialIndustryVO> getIndustryList(TutorialIndustryVO tutorialIndustryVO);

}