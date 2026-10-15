package com.chinatower.platform.ecmwf.mapper;

import com.chinatower.outdata.api.entity.EcmwfHeader;
import com.chinatower.outdata.api.entity.EcmwfVO;
import com.chinatower.platform.ecmwf.domain.SysEcmwf;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysEcmwfMapper {

    int delete(SysEcmwf vo);
    int insert(SysEcmwf vo);
    List<EcmwfHeader> getEcmwf(EcmwfVO vo);
    List<EcmwfHeader> getEcmwfLastInfo(EcmwfVO vo);

}
