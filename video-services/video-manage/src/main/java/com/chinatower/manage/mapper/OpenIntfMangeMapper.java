package com.chinatower.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.manage.dataobject.open.OpenIntfInfoVO;
import com.chinatower.manage.dataobject.open.OpenIntfReqInfoVO;
import com.chinatower.manage.dataobject.open.OpenManageInfoVO;
import com.chinatower.manage.dto.open.OpenManageInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lmz
 * @version 1.0
 * @date 2022年6月30日18:58:45
 */
@Mapper
public interface OpenIntfMangeMapper extends BaseMapper<OpenManageInfoVO> {

    List<OpenManageInfoVO> queryOpenApiPlatformInfo(@Param("model") OpenManageInfoModel model);

    List<OpenManageInfoVO> queryOpenSdkPlatformInfo(@Param("model") OpenManageInfoModel model);

    OpenManageInfoVO getOpenApiManageInfo(@Param("openIntfId")Long openIntfId);

    OpenManageInfoVO getOpenSdkManageInfo(@Param("openIntfId")Long openIntfId);

    OpenIntfInfoVO qryOpenApiIntfExtendInfo(@Param("openIntfId")Long openIntfId);

    OpenIntfInfoVO qryOpenSdkIntfExtendInfo(@Param("openIntfId")Long openIntfId);

    List<OpenIntfReqInfoVO> queryOpenSdkIntfReqInfoListBySdkIntfId(@Param("openIntfId")Long openIntfId);

    List<OpenIntfReqInfoVO>  selectOpenApiIntfIdByApiIntfId(@Param("openIntfId")Long openIntfId);



}
