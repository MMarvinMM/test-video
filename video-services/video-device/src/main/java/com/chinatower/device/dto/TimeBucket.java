package com.chinatower.device.dto;
import java.util.*;

public class TimeBucket {

    private final Date start;

    private final Date end;

    public TimeBucket(Date start, Date end) {
        if (start.after(end)) {
            throw new IllegalArgumentException("时间段无效(开始日期需要小于结束日期)");
        }
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

}