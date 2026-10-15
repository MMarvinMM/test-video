package com.chinatower.manage.mapper;

import com.chinatower.manage.dto.ScreenDataConfigCountVO;
import com.chinatower.manage.dto.ScreenDataConfigParticipationVO;
import com.chinatower.manage.dto.ScreenDataConfigVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScreenDataConfigMapper {

    List<ScreenDataConfigCountVO> selectAllScreenData(ScreenDataConfigCountVO screenDataConfigCount);

    int deleteByScreenDataConfigId(Long screenDataConfigId);

    int insertScreenDataConfigCount(ScreenDataConfigCountVO screenDataConfigCount);

    int updateScreenDataConfigCount(ScreenDataConfigCountVO screenDataConfigCount);

    int batchUpdateScreenDataConfigCount(List<ScreenDataConfigCountVO> openIsCustomizeScreenDataConfigList);

    int batchUpdateScreenDataConfigAlarmCount(List<ScreenDataConfigCountVO> openIsCustomizeScreenDataConfigList);

    int batchInsertScreenDataConfig(List<ScreenDataConfigVO> insertList);

    int batchDeleteScreenDataConfig(List<ScreenDataConfigVO> deleteList);

    List<ScreenDataConfigParticipationVO> selectScreenDataConfigByConfigId(Long screenDataConfigId);

    List<ScreenDataConfigParticipationVO> selectAreaIdByType(List<String> codeList, String flag);

    List<ScreenDataConfigParticipationVO> selectAreaCodeByType(List<String> codeList, String flag);

    List<ScreenDataConfigCountVO> selectOpenIsCustomizeScreenDataConfig(List<ScreenDataConfigVO> screenDataConfigList);

    ScreenDataConfigVO selectScreenDataConfigCountById(Long id);

    ScreenDataConfigVO selectScreenDataConfigCountByIdAndType(String screenId, String screenType, String tenantId, String industryCode, String appVerCode);

    List<ScreenDataConfigCountVO> selectOpenIsCustomizeScreenDataConfigTieta(List<ScreenDataConfigVO> tietaList);
}
