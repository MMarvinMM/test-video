package com.chinatower.video.modules.videorecordplan.dto;

import java.util.Date;

public class TimeNode {
    //时间的long值
    private final long time;
    //开始时间1，结束时间-1
    private final long direct;
    //累积和
    private long sum = 0;

    public TimeNode(Date dateTime, long direct) {
        this.time = dateTime.getTime();
        this.direct = direct;
    }

    public long getDirect() {
        return direct;
    }

    public long getTime() {
        return time;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

}