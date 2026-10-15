package com.chinatower.system.api.model;

import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
public class OrderGatewayRspDataVO implements Serializable {

    /**
     * 订购关系编码
     */

    @NotEmpty(message = "订购关系编码不能为空")
    private String orderCode;

    /**
     * 续订原订单号
     */
    private String oldOrderCode;

    /**
     * 订购关系变更状态：
     * 0：变更
     * 1：新增订购
     * 2：订单续订、启用（订购失效或停用后此状态才可生效）
     * 3：退订（退订后不可恢复）
     * 4：停用（欠费停用） 、失效
     */
    private int status;

    /**
     * 客户信息
     */
    @Valid
    private Customer customer = new Customer();

    /**
     * 套餐列表
     */
    @Valid
    private List<OrderPackage> packages = Lists.newArrayList();

    /**
     * 生效时间
     */
    private String validTime;
    /**
     * 失效时间
     */
    private String invalidTime;
    /**
     * 账号
     */
    private String subscriberCode;
    /**
     * 用户名
     */
    private String subscriberName;

    /**
     * 业务归属组织
     */
    private String businessOrg;

    /**
     * 订购时间
     */
    private String orderTime;


    /**
     * 套餐列表
     */
    @Data
    public static class OrderPackage {
        /**
         * 套餐编码
         */
        private String packageCode;
        /**
         * 套餐名称
         */
        private String packageName;
        /**
         * 套餐描述
         */
        private String description;
        /**
         * 应用版本
         */
        private Application application;
        /**
         * 算法信息
         */
        private List<AlgoTreeVO> algorithms;
        /**
         * 设备及通道
         */
        private List<DeviceTreeVO> devices;
        /**
         * 存储时长
         */
        private String storageTime;
        /**
         * 物联设备
         */
        private List<DeviceTreeVO> iotDevices;
        /**
         * 雷达设备
         */
        private List<DeviceTreeVO> radarDevices;
        /**
         * 无人机设备
         */
        private List<DeviceTreeVO> uavDevices;
        /**
         * 喇叭设备
         */
        private List<DeviceTreeVO> hornDevices;
        /**
         * 执法记录仪设备
         */
        private List<DeviceTreeVO> recorderDevices;
    }

    /**
     * 客户信息
     */
    @Data
    public static class Customer {
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

        /**
         * 账号
         */
        private String admin;
        /**
         * 密码
         */
        private String password;
        /**
         * 所属区域（与注册所属地市相同）
         */
        private String admArea;
        /**
         * 客户手机号
         */
        private String phoneNum;

    }


    /**
     * 应用版本
     */
    @Data
    public static class Application {
        /**
         * 应用编码
         */
        private String appCode;
        /**
         * 应用版本
         */
        private String productName;
        /**
         * 版本编码
         */
        private String productCode;
        /**
         * 应用能力
         */
        private List<String> capabilities;
    }


}
