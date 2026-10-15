package com.chinatower.outdata.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class RadarMsg {

    @Data
    public static class Req<T> {
        @NotBlank(message = "请求入参不可以为空")
        private T reqBody;
    }

    @Data
    public static class TargetReq {
        /**
         * 租户id
         */
        private String tenantId;
        /**
         * 行业编码
         */
        private String industryCode;
    }

    @Data
    public static class TargetResponse {
        // 目标ID: 目标唯一id
        private String id;
        // 短编号: 显示短号
        private Integer displayId;
        // 船名: AIS船名
        private String vesselName;
        // 船舶MMSI: 船舶9位码
        private Integer mmsi;
        // 呼号: AIS呼号
        private String callSign;
        // IMO编号: AIS的IMO编号
        private Integer imo;
        // 纬度: 单位为度
        private Double latitude;
        // 经度: 单位为度
        private Double longitude;
        // 目标航向: 单位为°
        private Float course;
        // 目标艏向: 单位为°
        private Float heading;
        // 目标速度: 单位为节
        private Float speed;
        // 目标长度: 单位为米
        private Integer len;
        // 目标宽度: 单位为米
        private Integer wid;
        // 最大长度: 单位为米
        private Integer maxLen;
        // 单位为米: 单位为米
        private Integer minLen;
        // 船舶类型: 1渔船 2货船 3客船 4油船 5公务船（执法船） 6海警船 7高速船 8作业船 9其他（雷达和其他类型的AIS） 10拖船
        private Integer shiptype;
        // 目标类型: 1雷达目标 2A类AIS目标 3A类AIS目标 4雷达目标与A类AIS目标的融合 5雷达目标与B类AIS目标的融合 6北斗目标 7AIS助航目标 8雷达目标与助航目标的融合 9虚拟助航目标 10雷达目标与虚拟助航目标的融合 11其他未知（预留）
        private String sClass;
        // 目标状态: 1代表正常 2代表预测 3代表丢失
        private Integer state;
        // 时间戳: 目标的时间戳
        private Long lastTm;
        // 接收时间: 同一批次数据接收时间
        private Long receiveTime;
        // 数据状态: 新增、删除和更新
        private String status;
        // 省份: 雷达站点所属省份
        private String province;
        // 数据质量: 可不用
        private Integer quality;
        // 更新周期: 单位为秒
        private Integer period;
        // AIS厂商: AIS设备商
        private String vendorId;
        // 国籍: 船舶国籍
        private String nationality;
        // 持续时长
        private Long duration;
        // 移动距离
        private Long  displacement;
        // 接续之前 的id
        private String  idLink;
        private Long updateTimestamp;
        private String source;
        private String sourceType;
        private String accessNode;
    }

    @FeignClient(contextId = "messageService",
            value = ServiceNameConstants.OUTDATA_SERVICE)
    public interface MessageService {
        @PostMapping("/radarMsg/register")
        Result<Boolean> register(@RequestBody Req<String> req);

        @PostMapping("/radarMsg/showCurrentTimeTargetList")
        Result<List<TargetResponse>> showCurrentTimeTargetList(@RequestBody TargetReq req);
    }
}
