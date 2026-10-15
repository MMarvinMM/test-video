package com.chinatower.airline.enums;

import java.util.*;


/**
 * 任务执行错误类型枚举类
 */
public enum TaskErrorReasonTypeEnum {

    TASK_EXECUTION_EXCEPTION("任务执行异常", Arrays.asList(
            "飞行任务下发失败",
            "航线文件格式不兼容",
            "飞行任务下发失败，机场无法获取航线",
            "机场传输精准复拍航线失败",
            "航线文件解析失败",
            "离场航线下发失败",
            "进离场航线下发失败",
            "机场系统运行异常，重新下发任务",
            "启动任务失败，查询不到大疆航线",
            "启动任务失败，获取参数失败",
            "飞行任务执行超时",
            "查询不到对应的任务架次",
            "航线执行失败",
            "启动任务失败，无法获取基站状态",
            "启动任务失败，基站状态非空闲",
            "启动任务失败，没有收到基站请求任务资源",
            "任务开启指令下发失败：机巢没有连接",
            "启动任务失败，查询不到nestUuid"
    )),

    AIRPORT_SYSTEM_EXCEPTION("机场系统异常", Arrays.asList(
            "飞行器初始化失败",
            "机场传输航线至飞行器失败",
            "飞行器起飞前准备超时",
            "机场系统异常，无法获取结果",
            "飞行器起飞前准备失败",
            "机场系统异常，飞行任务执行失败",
            "飞行器 RTK 收敛失败",
            "飞行器 RTK 定位异常",
            "机场通信异常",
            "机场通信异常",
            "机场系统异常",
            "任务失败，重启机场",
            "机场位置未收敛",
            "飞行器 RTK 收敛失败",
            "机场未检测到飞行器",
            "飞行器 RTK 收敛位置过远",
            "机场系统错误",
            "机场急停按钮被按下",
            "云端与机场通信异常",
            "机场固件版本过低",
            "机场正在初始化",
            "机场未准备完成",
            "飞行器卫星定位信号差",
            "飞行器挡位错误",
            "飞行器返航点未设置",
            "飞行器通信异常",
            "机巢不在线",
            "当前基站状态不为待机",
            "基站状态离线",
            "当前基站正在执行任务",
            "该基站有批量自动任务运行",
            "该基站有批量自动任务暂停中",
            "当前基站状态为{0}",
            "自动装载失败",
            "机巢任务构建失败",
            "获取机巢数据出错",
            "当前状态不允许执行任务",
            "当前天线未打开",
            "获取基站状态,参数错误",
            "获取无人机拍照/录像源类型失败,基站离线",
            "获取基站状态失败,基站离线",
            "等待两个机场都空闲",
            "机场位置未收敛",
            "起飞机场与降落机场部署距离超出限制",
            "降落机场检查问题",
            "基站离线"
    )),

    UAV_EXCEPTION("无人机异常", Arrays.asList(
            "飞行器不在停机坪正中间或朝向不正确",
            "RTK收敛超时，用户手动取消",
            "机场获取飞行器控制权失败",
            "飞行器电池循环次数过高",
            "飞行器固件版本不匹配",
            "飞行器飞行高度过低",
            "飞行器触发避障",
            "飞行器接近限飞区自动返航",
            "飞行器起飞失败",
            "飞行器起飞前准备失败",
            "飞行器轨迹规划失败",
            "目标点位于禁飞区域或障碍物内",
            "飞行器航线飞行过程中失联",
            "飞行器未悬停",
            "用户使用 B 控操控飞行器起桨",
            "飞行器无法起飞",
            "飞行器起飞失败",
            "飞行器 GPS 定位信号差",
            "飞行器定位失效",
            "飞行器数据更新失败",
            "飞行器无法到达目标降落机场",
            "飞机不在舱内",
            "飞机未连接",
            "无人机未连接",
            "获取无人机拍照/录像源类型失败,参数异常",
            "遥控器未连接"
    )),

    WEATHER_REASON("天气原因", Arrays.asList(
            "任务过程中遇到大风紧急返航",
            "天气原因，温度低且降雨",
            "气象检查不通过",
            "气象站数据不正常",
            "风速过大",
            "气象检查正常",
            "获取气象数据出错",
            "未获得气象站相关信息",
            "风速过大",
            "当前正在下雨"
    )),

    NO_FLY_ZONE_HEIGHT("限飞区/限高问题", Arrays.asList(
            "线规划高度超过限高",
            "起点或终点位于限远区域",
            "触碰自定义飞行区边界",
            "航线穿过限飞区",
            "飞行器接近限飞区自动返航",
            "触碰自定义飞行区边界",
            "目标点位于禁飞区域或障碍物内",
            "已触碰自定义飞行区边界",
            "降落机场位于自定义禁飞区等",
            "目标降落机场突破限高区限高",
            "目标降落机场突破飞行器限高",
            "目标机场处于地理鸟笼作业区域外"
    )),

    BATTERY_POWER_EXCEPTION("电池/电量异常", Arrays.asList(
            "飞行器电池循环次数过高",
            "飞行器电量低于30%",
            "飞行器低电量返航",
            "获取电池信息异常",
            "机巢电池有空缺",
            "无法知道预备使用电池电量",
            "预备使用电池充电次数超过150次",
            "预备使用电池剩余量低",
            "电池电量低，请谨慎飞行",
            "电池充电次数",
            "获取电池信息异常",
            "没有获取充电次数",
            "预备使用电池充电次数超过限制",
            "电池循环次数超过",
            "电池状态未知"
    )),

    USER_OPERATION_INTERVENTION("用户操作干预", Arrays.asList(
            "RTK收敛超时，用户手动取消",
            "机场急停按钮被按下",
            "用户使用 B 控操控飞行器起桨",
            "任务被手动打断或异常终止",
            "自动任务已取消",
            "手动计划，没有确认"
    )),

    OTHER("其他", Arrays.asList(
            "降落机场和起飞机场标定信息不一致",
            "设备部署位置不佳",
            "查询不到航线信息",
            "可拍照数少于航线拍照数",
            "没有获取到可拍照数",
            "可录制视频时长少于航线录制时长",
            "剩余空间足够",
            "没有获取到可录制时长",
            "检查不通过",
            "剩余空间不足",
            "不具备降落引导的条件"
    ));

    private final String description;
    private final List<String> errorReasons;

    // 反向映射，用于快速查找
    private static final Map<String, TaskErrorReasonTypeEnum> ERROR_REASON_MAP = new HashMap<>();

    static {
        for (TaskErrorReasonTypeEnum errorType : values()) {
            for (String reason : errorType.getErrorReasons()) {
                ERROR_REASON_MAP.put(reason, errorType);
            }
        }
    }

    TaskErrorReasonTypeEnum(String description, List<String> errorReasons) {
        this.description = description;
        this.errorReasons = errorReasons;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getErrorReasons() {
        return errorReasons;
    }

    /**
     * 根据错误原因获取错误类型
     */
    public static TaskErrorReasonTypeEnum getByErrorReason(String errorReason) {
        return ERROR_REASON_MAP.getOrDefault(errorReason, OTHER);
    }

    /**
     * 获取所有错误原因列表
     */
    public static List<String> getAllErrorReasons() {
        return new ArrayList<>(ERROR_REASON_MAP.keySet());
    }
}