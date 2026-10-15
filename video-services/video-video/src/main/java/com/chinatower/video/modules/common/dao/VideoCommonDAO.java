package com.chinatower.video.modules.common.dao;

import com.chinatower.platform.entity.devicemgmt.vo.DeviceOptAuthVO;
import com.chinatower.video.api.entity.BaseErrorRecordVO;
import com.chinatower.video.api.entity.VideoBaseReq;
import com.chinatower.video.modules.videocontrol.dto.SysUserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: VideoCommonDAO
 * @Description: 视频控制公共DAO
 * @Author: zhujun
 * @Date: 2022-01-13
 **/
@Mapper
public interface VideoCommonDAO {

    /**
     * 获取接口路径
     *
     * @param interfaceCode
     * @return
     */
    String getInterfaceUrl(@Param("interfaceCode") String interfaceCode);

    /**
     * 获取有效的租用设备通道
     * @return
     */
    List<Map<String, String>> queryOrderDevice(@Param("param") VideoBaseReq param);

    List<Map<String, String>> queryOrderDeviceByTableName(@Param("param") VideoBaseReq param, @Param ("tableName") String tableName);

    List<Map<String, String>> queryOrderUavDevice(@Param("param") VideoBaseReq param);
    List<Map<String, String>> queryOrderDeviceNoAuth(@Param("param") VideoBaseReq param);

    List<Map<String, String>> queryOrderUavDeviceNoAuth(@Param("param") VideoBaseReq param);
    List<Map<String, String>> queryOrderDeviceByTableNameNoAuth(@Param("param") VideoBaseReq param, @Param ("tableName") String tableName);

    String getErrorMsg(@Param("dictValue") String dictValue);

    /**
     * 功能描述: 获取用户名称--脱敏处理
     * @param: userId
     * @return: String
     * @auther: zhaozj
     * @date: 2022/11/23
     * @version: release_v1.5.30
     */
    SysUserDTO getUserName(SysUserDTO sysUserDTO);

    List<SysUserDTO> getUserIdBatch(List<String> userNameList);

    int saveBaseErrorRecord(BaseErrorRecordVO vo);


    List<DeviceOptAuthVO> queryDeviceOptAuthInfo(DeviceOptAuthVO deviceOptAuthVO);

    SysUserDTO getUserInfo(SysUserDTO sysUserDTO);

    String getDeptInfo(List<String> deptList,String industryCode);
}
