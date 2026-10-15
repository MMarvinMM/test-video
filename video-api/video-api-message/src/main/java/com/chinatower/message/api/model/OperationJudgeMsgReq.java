package com.chinatower.message.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationJudgeMsgReq {


    private List<UserInfo> userInfoList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo{
        /**
         * 用户ID
         */
        private String userId;
        /**
         * 行业编码,铁塔默认999999
         */
        private String industryCode = "999999";
    }

    public static OperationJudgeMsgReq fromUserIds(List<String> userIds) {
        return OperationJudgeMsgReq.builder()
                .userInfoList(userIds.stream().map(userId -> UserInfo.builder().userId(userId).build()).collect(Collectors.toList()))
                .build();
    }

}
