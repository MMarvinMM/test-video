package com.chinatower.platform.modules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮件收件人信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailReceiverInfoDto {

    /**
     * 收件人邮箱，多个以英文逗号分隔
     */
    private String receiveEmails;

    /**
     * 抄送人邮箱，多个以英文逗号分隔
     */
    private String ccEmails;
}
