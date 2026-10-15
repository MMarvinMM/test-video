package com.chinatower.common.core.serializer;

import com.alibaba.fastjson.serializer.ArraySerializer;
import com.alibaba.fastjson.serializer.ToStringSerializer;

/**
 * @author qq on 2023/2/22
 */
public class FastJsonLongArrayToStringSerializer extends ArraySerializer {
    public FastJsonLongArrayToStringSerializer() {
        super(Long.class, new ToStringSerializer());
    }
}
