package com.chinatower.common.core.dto;

import com.chinatower.common.core.domain.OrderAlgorithmConfigDO;
import com.chinatower.common.core.domain.OrderDeviceHisDO;
import com.chinatower.common.core.domain.OrderUavDeviceHisDO;
import com.chinatower.common.core.domain.OrderUavNestDeviceHisDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Auther: yuexuebin
 * @Date: 2021/5/17 16:26
 * @Description:
 * 同步网关对象
 */
@Data
public class OrderGatewayDTO implements Serializable {
    /** 订购关系编码 */

    @NotEmpty(message = "订购关系编码不能为空")
    private String orderCode;

    /**
     * 续订原订单号
     */
    private String oldOrderCode;

    /**
     * 受理节点
     */
    private String acceptNode;

    /**
     * 只处理订单用户信息
     */
    private Boolean handleOrderUserInfoOnly;

    /**
     * 是否是 中心节点但不是受理节点
     */
    private Boolean isCenterButNotAcceptNode;

    /**
     * 当前节点编码
     */
    private String currentLocationCode;

    /**
     * 本次订单迁移标识次数
     */
    private int orderMoveSign;

    /**
     * 旧受理节点
     */
    private String oldAcceptNode;

    /**
     *订购关系变更状态：
     * 0：变更
     * 1：新增订购
     * 2：订单续订、启用（订购失效或停用后此状态才可生效）
     * 3：退订（退订后不可恢复）
     * 4：停用（欠费停用） 、失效
     */
    private int status;

    /** 客户信息 */
    @Valid
    private Customer customer;

    /** 套餐列表 */
    @Valid
    private List<OrderPackages> packages;

    /**
     * 订购关系类型：
     * 1-非精准转非精准订单（vm_order_algorithm_config 无记录且 algorithmConfigList 为空）
     * 2-非精准转精准订单（vm_order_algorithm_config 无记录且 algorithmConfigList 不为空）
     * 3-精准转非精准订单（vm_order_algorithm_config 有记录且 algorithmConfigList 为空）
     * 4-精准转精准订单（vm_order_algorithm_config 有记录且 algorithmConfigList 不为空）
     */
    private Integer orderType;

    /**
     * 算法配置信息
     */
    private List<AlgorithmConfigInfo> algorithmConfigList;

    /** 生效时间 */
    private String validTime;
    /** 失效时间 */
    private String invalidTime;
    /** 账号 */
    private String subscriberCode;
    /** 用户名 */
    private String subscriberName;

    /** 业务归属组织 */
    private String businessOrg;

    /** 订购时间 */
    private String orderTime;

    /**
     * 变更减少的设备列表，用于后续事务外处理逻辑
     */
    private List<OrderDeviceHisDO> reduceDeviceList;

    /**
     * 算法配置减少的设备信息
     */
    private List<OrderDeviceHisDO> reduceAlgorithmConfigDeviceList;

    /**
     * 算法配置减少的无人机设备信息
     */
    private List<OrderUavDeviceHisDO> reduceAlgorithmConfigUavDeviceList;

    /**
     * 变更减少的无人机设备列表，用于后续事务外处理逻辑
     */
    private List<OrderUavDeviceHisDO> reduceUavDeviceList;

    /**
     * 变更增加的摄像机设备信息，用于同步大屏配置使用 key -> industryCode
     */
    private Map<String, List<Device>> addDeviceMap;

    /**
     * 变更增加的物联设备信息，用于同步大屏配置使用 key -> industryCode
     */
    private Map<String, List<OrderGatewayDTO.SensorDevice>> addSensorDeviceMap;

    /**
     * 变更增加的无人机设备信息，用于同步大屏配置使用 key -> industryCode
     */
    private Map<String, List<OrderGatewayDTO.Device>> addUavDeviceMap;

    /**
     * 变更增加的广播设备信息，用于同步大屏配置使用 key -> industryCode
     */
    private Map<String, List<OrderGatewayDTO.SensorDevice>> addHornDeviceMap;

    /**
     * 变更增加的执法记录仪设备信息，用于同步大屏配置使用 key -> industryCode
     */
    private Map<String, List<OrderGatewayDTO.Device>> addRecordDeviceMap;

    /** 套餐列表 */
    @Data
    public static class OrderPackages{
        /** 套餐编码 */
        private String packageCode;
        /** 套餐名称 */
        private String packageName;
        /** 套餐描述 */
        private String description;
        /** 应用版本 */
        private Application  application;
        /** 监控服务 */
        private List<Service> services;
        /** 算法信息 */
        private List<Algorithm> algorithms;
        /** 设备及通道 */
        private List<Device> devices;
        /** 传感器设备 */
        private List<SensorDevice> sensorDevice;
        /**
         * 无人机设备
         */
        private List<Device> uavDevice;
        /**
         * 无人机机巢设备
         */
        private List<Device> nestDevice;
        /**
         * 喇叭设备
         */
        private List<SensorDevice> hornDevice;
        /**
         * 执法记录仪
         */
        private List<Device> recorderDevice;
        /**
         * 能源设备
         */
        private List<EnergyDevice> energyDevice;

        /** 存储时长 */
        private String storageTime;
    }
    /** 客户信息 */
    @Data
    public static class Customer{
        /**
         * 业务客户编码
         */
        @NotEmpty(message = "业务客户编码不能为空")
        private String customerCode;

        /**
         * 客户名称
         */
        @NotEmpty(message = "业务客户名称不能为空")
        private String customerName;

        /**
         * 注册地所属省份
         */
        private String cusRegistryProvince;

        /**
         * 注册地所属地市
         */
        private String cusRegistryCity;

        /** 账号 */
        private String admin;
        /** 密码 */
        private String password;
        /** 所属区域（与注册所属地市相同） */
        private String admArea;
        /** 客户手机号 */
        private String phoneNum;

    }


    /** 应用版本 */
    @Data
    public static class  Application {
        /** 应用编码 */
        private String appCode;
        /** 应用版本 */
        private String productName;
        /** 版本编码 */
        private String productCode;
        /** 应用能力 */
        private List<String> capabilities;
    }

    /** 监控服务 */
    @Data
    public static class Service {
        /** 监控编码 */
        private String serviceCode;
        /** 监控服务 */
        private String serviceName;
        /** 有效行政区域 */
        private String areaCodes;

    }

    /** 算法信息 */
    @Data
    public static class Algorithm{
        /** 算法编码 */
        private String algorithmCode;
        /** 算法名称 */
        private String algorithmName;
    }

    /** 设备及通道 */
    @Setter
    @Getter
    public static class Device implements Serializable {
        /** 设备编码 */
        private String deviceCode;
        /** 设备名称 */
        private String devName;

        /**
         * 接入节点
         */
        private String accessNode;

        /** 设备状态 */
        private Integer status;
        /** 设备类型 */
        private Long categoryCode;
        /** 站址编码 */
        private String siteCode;
        private String siteName;
        /** 公共库站址编码 */
        private String publicSiteCode;
        /** 归属区域 */
        private String areaCode;
        /** 归属行政区域 */
        private String admCode;
        /** 站址地址 */
        private String location;
        /** 经度 */
        private String longitude;
        /** 纬度 */
        private String latitude;
        /** 水平范围最小值 */
        private String horizonMin;
        /** 水平范围最大值 */
        private String horizonMax;
        /** 垂直范围从最小值 */
        private String verticalMin;
        /** 垂直范围从最大值 */
        private String verticalMax;
        /** 视角场最小值 */
        private String visualMin;
        /** 视角场最大值 */
        private String visualMax;

        /** 挂高 */
        private String height;
        /** 设备通道 */
        private List<DeviceChannels> channels;


    }

    @Data
    public static class DeviceChannels implements Serializable {
        /** 通道编码 */
        private String channelCode;
        /** 通道名称 */
        private String channelName;
        /** 通道状态 */
        private Integer status;
        /** 可设置最大看守位数量 */
        private Integer maxGuard;
        /** 可设置最大预置点数量 */
        private Integer maxPreset;
        /** 可设置最大巡航路径数量 */
        private Integer maxPatrol;
        /** 可视范围 */
        private String visualRange;
        /** 水平辐射角度 */
        private String horizRange;
        /** 俯仰辐射角度 */
        private String vertiRange;
        /** 码流类型 */
        private Integer streamType;


    }

    /** 传感器设备 */
    @Setter
    @Getter
    public static class SensorDevice implements Serializable {
        /** 设备编码 */
        private String deviceCode;
        /** 设备名称 */
        private String devName;
        /**
         * 接入节点
         */
        private String accessNode;
        /** 设备状态 */
        private Integer status;
        /** 设备类型 */
        private Integer categoryCode;
        /** 站址编码 */
        private String siteCode;
        private String siteName;
        /** 公共库站址编码 */
        private String publicSiteCode;
        /** 归属区域 */
        private String areaCode;
        /** 归属行政区域 */
        private String admCode;
        /** 站址地址 */
        private String location;
        /** 经度 */
        private String longitude;
        /** 纬度 */
        private String latitude;
        /** 挂高 */
        private String height;
        /**
         * 设备类型，运管录入，各系统与运管同步
         */
        private String deviceType;
    }

    @Setter
    @Getter
    public static class EnergyDevice implements Serializable {
        /** 设备编码 */
        private String deviceCode;
        /** 设备名称 */
        private String devName;
        /**
         * 接入节点
         */
        private String accessNode;
        /** 设备状态 */
        private Integer status;
        /** 设备类型 */
        private Integer categoryCode;
        /** 站址编码 */
        private String siteCode;
        private String siteName;
        /** 公共库站址编码 */
        private String provinceCode;
        /** 归属区域 */
        private String cityCode;
        /** 归属行政区域 */
        private String countyCode;
        /** 站址地址 */
        private String crewProvinceCode;
        /** 经度 */
        private String crewCityCode;
        /** 纬度 */
        private String crewCountyCode;
    }

    @Data
    public static class AlgorithmConfigInfo implements Serializable {
        /**
         * 算法编码
         */
        private String algorithmCode;
        /**
         * 算法类型 1-摄像机算法配置 2-无人机算法配置
         */
        private String algorithmType;
        /**
         * 设备编码
         */
        private String deviceCode;
    }

    public boolean checkParam() {
        OrderGatewayDTO.Application application;
        if (this.getCustomer() == null || StringUtils.isEmpty(this.getCustomer().getCustomerCode())
                || org.apache.commons.collections4.CollectionUtils.size(this.getPackages()) != 1
                || (application = this.getPackages().get(0).getApplication()) == null
                || StringUtils.isEmpty(application.getAppCode())
                || StringUtils.isEmpty(application.getProductCode())) {
            return false;
        }
        return true;
    }

}
