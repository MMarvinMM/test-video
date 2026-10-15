package com.chinatower.platform.project.system.service;

import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.system.api.request.notice.SysNotice;
import com.chinatower.platform.project.system.domain.dto.NoticeQueryDTO;
import com.chinatower.platform.project.system.domain.vo.GetNoticeReqVo;

import java.util.List;

/**
 * 公告 服务层
 *
 * @author chinatower
 */
public interface ISysNoticeService
{
    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
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
     * 删除公告信息
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
     * 查询当前用户待阅公告列表
     * @param user 当前用户信息
     * @return 待阅公告列表
     */
    List<SysNotice> selectToReadNoticeByUser(SysUser user);

    /**
     * 查询当前用户待阅公告列表
     * @paramo vo
     * @return 待阅公告列表
     */
    List<SysNotice> selectAppToReadNoticeByUser(GetNoticeReqVo vo);

    /**
     * 查询当前用户公告列表
     * @param vo
     * @return 公告列表
     */
    List<SysNotice> selectAppNoticeByUser(GetNoticeReqVo vo);

    /**
     * 插入已读记录
     * @param noticeId 已读公告ID
     * @param userId 用户ID
     * @return 插入结果
     */
    int insertNoticeRead(Long noticeId, String userId);

    /**
     * 查询未读公告记录
     * @param vo
     * @return 插入结果
     */
    int countNoticeReadRecordByUserId(GetNoticeReqVo vo);

    void updateStatus(NoticeQueryDTO dto);

    /**
     * 批量插入已读记录
     * @param vo
     * @return 插入结果
     */
    int insertNoticeReadBatch(GetNoticeReqVo vo);

    /**
     * 查询当前用户公告列表数量
     * @param vo
     * @return 公告列表数量
     */
    Integer selectAppNoticeByUserCount(GetNoticeReqVo vo);


}
