package com.chinatower.message.hms.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.message.hms.dto.HmsCodeAlarmTypeItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HMS 告警 code -> 告警类型解析
 * <p>
 * 优先从 classpath 下 {@code hms-code-alarm-type-map.json} 解析；
 * 同一 code 在 JSON 中出现多条时，按数组顺序<strong>后出现的覆盖先出现的</strong>，即取文件中最后一条的告警类型。
 * 未命中时兜底：hmsMsg，再兜底 hmsCode。
 */
@Component
@Slf4j
public class UavHmsAlarmTypeResolver {

    private static final String MAP_RESOURCE = "hms-code-alarm-type-map.json";

    private Map<String, String> codeToAlarmType = Collections.emptyMap();

    @PostConstruct
    public void loadMap() {
        try {
            ClassPathResource resource = new ClassPathResource(MAP_RESOURCE);
            if (!resource.exists()) {
                log.warn("未找到 {}，HMS 告警类型将使用 hmsMsg/hmsCode 兜底", MAP_RESOURCE);
                return;
            }
            String json = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            List<HmsCodeAlarmTypeItem> list = JSON.parseArray(json, HmsCodeAlarmTypeItem.class);
            if (list == null || list.isEmpty()) {
                log.warn("{} 内容为空", MAP_RESOURCE);
                return;
            }
            // LinkedHashMap 按遍历顺序写入，同 key 后写覆盖先写 → 文件中最后一条生效
            Map<String, String> map = new LinkedHashMap<>();
            for (HmsCodeAlarmTypeItem item : list) {
                if (item == null) {
                    continue;
                }
                String key = item.getCode();
                String type = item.getAlarmType();
                if (StrUtil.isNotBlank(key) && StrUtil.isNotBlank(type)) {
                    map.put(key, type);
                }
            }
            this.codeToAlarmType = Collections.unmodifiableMap(map);
            log.info("已加载 HMS 告警类型映射：{} 条（同 code 取 JSON 中最后一条）", map.size());
        } catch (Exception e) {
            log.error("加载 {} 失败", MAP_RESOURCE, e);
            this.codeToAlarmType = Collections.emptyMap();
        }
    }

    /**
     * @param hmsCode 告警 code
     */
    public String resolveType(String hmsCode) {
        if (StrUtil.isNotBlank(hmsCode)) {
            String fromMap = codeToAlarmType.get(hmsCode);
            if (StrUtil.isNotBlank(fromMap)) {
                return fromMap;
            }
        }
        return "";
    }

}
