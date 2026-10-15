package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.platform.entity.msg.BsJpushRecord;
import lombok.Data;

@Data
public class MessageManageVO extends BsJpushRecord {

    private String flag;//标识0一键已读 1单条已读（极光）2单条已读（公告）

    private Long noticeId;//公告id

}
