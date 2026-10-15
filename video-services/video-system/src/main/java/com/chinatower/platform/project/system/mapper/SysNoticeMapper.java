package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.system.api.request.notice.SysNotice;
import com.chinatower.platform.project.system.domain.dto.NoticeQueryDTO;
import com.chinatower.platform.project.system.domain.vo.GetNoticeReqVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 通知公告表 数据层
 *
 * @author chinatower
 */
public interface SysNoticeMapper
{
    /**
     * 查询公告信息
     *
     * @param noticeId     公告ID
     * @param platformCode
     * @return 公告信息
     */
    public SysNotice selectNoticeById(NoticeQueryDTO noticeId);

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<SysNotice> getNoticeList(SysNotice notice);

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    public int insertNotice(SysNotice notice);

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    public int updateNotice(SysNotice notice);

    /**
     * 批量删除公告
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    public int deleteNoticeById(NoticeQueryDTO noticeId);

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    public int deleteNoticeByIds(NoticeQueryDTO noticeIds);

    /**
     * 查询PC端当前用户待阅公告列表
     *
     * @param user 当前用户信息
     * @return 待阅公告列表
     */
    List<SysNotice> selectToReadNoticeByUser(SysUser user);

    /**
     * 查询App端当前用户待阅公告列表
     *
     * @param vo
     * @return 待阅公告列表
     */
    List<SysNotice> selectAppToReadNoticeByUser(GetNoticeReqVo vo);
    /**
     * 查询App端当前用户公告列表
     *
     * @param vo
     * @return 待阅公告列表
     */
    List<SysNotice> selectAppNoticeByUser(GetNoticeReqVo vo);


    /**
     * 插入已读记录
     *
     * @param noticeId 已读公告ID
     * @param userId   用户ID
     * @return 插入结果
     */
    int insertNoticeRead(@Param("noticeId") Long noticeId,@Param("userId") String userId);

    /**
     * 插入已读记录
     *
     * @param noticeId 已读公告ID
     * @param userId   用户ID
     * @return 插入结果
     */
    int countNoticeReadRecord(@Param("noticeId") Long noticeId,@Param("userId") String userId);


    /**
     * 查询已读记录
     *
     * @param vo
     * @return 插入结果
     */
    int countNoticeReadRecordByUserId(GetNoticeReqVo vo);


    @Select("select count(1) from SYS_NOTICE")
    int selectCount();

    @Select(" select tt.* from (" +
            " SELECT t.notice_id, t.NOTICE_BANNER,rownum AS rowno FROM sys_notice t WHERE ROWNUM <= #{pageNumber} * #{pageSize} " +
            " )  tt where  tt.rowno>(#{pageNumber}-1)*#{pageSize}")
    List<SysNotice> selectPageInfo(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);

    void updateStatus(NoticeQueryDTO dto);


    /**
     * 插入已读记录
     *
     * @param vo
     * @return 插入结果
     */
    int insertNoticeReadBatch(GetNoticeReqVo vo);

}
