package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.event.dataobject.WarningOrderCollectionDO;
import com.chinatower.platform.dto.AlarmOrderDTO;
import com.chinatower.platform.enums.TableEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 告警信息收藏表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-27
 */
@DS(TableEnum.PLATFORM)
public interface IWarningOrderCollectionMapper extends BaseMapper<WarningOrderCollectionDO> {

    /**
     * 查询收藏数据
     * @param dto
     * @return
     */
    List<WarningOrderListVO> qryWarningOrderCollection(@Param("param") AlarmOrderDTO dto);

}
