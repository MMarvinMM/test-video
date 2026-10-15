package com.chinatower.common.core.mongo;

import com.chinatower.platform.constants.Constants;
import org.springframework.data.mongodb.core.query.Query;

public enum IndexStrategy {
    INTECEPTER {
        @Override
        public void withHint(Query query) {
            String queryString = query.getQueryObject().toString();
            if (queryString.contains("warning_order_id")) {
                query.withHint("warning_order_id");
            } else {
                if (queryString.contains(Constants.INDEXFIELD_ALARM_TIME) && queryString.contains("is_second_judge")) {
                    query.withHint("alarm_time,is_second_judge,warning_source");
                } else if (queryString.contains(Constants.INDEXFIELD_ALARM_TIME) && queryString.contains("device_code")) {
                    query.withHint("alarm_time,device_code,judge_result");
                } else if (queryString.contains(Constants.INDEXFIELD_ALARM_TIME) && queryString.contains("tenant_id")) {
                    query.withHint("tenant_id");
                } else if (queryString.contains(Constants.INDEXFIELD_ALARM_TIME) && queryString.contains("warning_type_id")) {
                    query.withHint("alarm_time,warning_type_id,judge_result");
                } else if (queryString.contains(Constants.INDEXFIELD_ALARM_TIME) && queryString.contains("province_code")
                        && queryString.contains("city_code") && queryString.contains("county_code")) {
                    query.withHint("alarm_time,province_code,city_code,county_code,judge_result");
                } else {
                    query.withHint("alarm_time,judge_result");
                }
            }
        }
    };

    public abstract void withHint(Query query);
}
