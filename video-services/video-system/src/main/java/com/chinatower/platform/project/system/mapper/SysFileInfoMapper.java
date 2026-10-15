package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.SysFileInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文件信息Mapper接口
 *
 * @author wangzic
 * @date 2020-04-03
 */
public interface SysFileInfoMapper
{
//    /**
//     * 查询文件信息
//     *
//     * @param fileId 文件信息ID
//     * @return 文件信息
//     */
//    SysFileInfo selectSysFileInfoById(String fileId);
//
//    /**
//     * 查询文件信息列表
//     *
//     * @param sysFileInfo 文件信息
//     * @return 文件信息集合
//     */
//    List<SysFileInfo> selectSysFileInfoList(SysFileInfo sysFileInfo);
//
//    /**
//     * 新增文件信息
//     *
//     * @param sysFileInfo 文件信息
//     * @return 结果
//     */
//    int insertSysFileInfo(SysFileInfo sysFileInfo);
//
//    /**
//     * 修改文件信息
//     *
//     * @param sysFileInfo 文件信息
//     * @return 结果
//     */
//    int updateSysFileInfo(SysFileInfo sysFileInfo);
//
//    /**
//     * 删除文件信息
//     *
//     * @param fileId 文件信息ID
//     * @return 结果
//     */
//    int deleteSysFileInfoById(String fileId);
//
//    /**
//     * 批量删除文件信息
//     *
//     * @param fileIds 需要删除的数据ID
//     * @return 结果
//     */
//    int deleteSysFileInfoByIds(String[] fileIds);
//
//
//    /**
//     * 批量查询文件信息
//     * @param fileIds
//     * @return
//     */
//    List<SysFileInfo> selectSysFileInfoByIds(String[] fileIds);
//
//
//    List<SysFileInfo> selectToEncryptFile();
//
//    @Select("select count(1) from SYS_FILE_INFO")
//    int selectCount();
//
//    @Select(" select tt.* from (" +
//            " SELECT t.FILE_ID,t.FILE_URL,rownum AS rowno FROM SYS_FILE_INFO t WHERE ROWNUM <= #{pageNumber} * #{pageSize} " +
//            " )  tt where  tt.rowno>(#{pageNumber}-1)*#{pageSize}")
//    List<SysFileInfo> selectPageInfo(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);


}
