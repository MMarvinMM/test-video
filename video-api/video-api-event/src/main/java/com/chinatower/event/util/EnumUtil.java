package com.chinatower.event.util;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: EnumUtil.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/24 11:02
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public class EnumUtil {

    /**
     * 通过角色获取到对应告警状态
     */
    public enum OrderStatusByRole {
        PENDING_JUDGE(998947L, "1"),
        PENDING_DISPATCH(998943L, "2"),
        PENDING_CLAIM(998945L, "3"),
        PENDING_HANDLE(998946L, "5");

        private Long code;

        private String name;

        OrderStatusByRole(Long code, String name) {
            this.code = code;
            this.name = name;
        }

        public Long getCode() {
            return code;
        }

        // 普通方法
        public static String getName(Long code) {
            for (OrderStatusByRole c : OrderStatusByRole.values()) {
                if (c.getCode().longValue() == code.longValue()) {
                    return c.name;
                }
            }
            return null;
        }
    }

    /**
     * 系统默认角色枚举
     */
    public enum BaseRole {
        SUPER_ADMIN(1L, "超级管理员"),
        JUDGE_MAN(4L, "研判人员"),
        DISPATCH_MAN(5L, "调度人员"),
        HANDLE_MAN(6L, "处置人员"),
        CHECK_MAN(7L, "核实人员"),
        COMPANY_LEADER(998948L, "公司领导"),
        DEPARTMENT_LEADER(998948L, "部门领导");

        private Long code;

        private String name;

        private BaseRole(Long code, String name) {
            this.code = code;
            this.name = name;
        }

        public Long getCode() {
            return code;
        }

        public void setCode(Long code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum FlowNode {
        MISINFORMATION("0", "假警完结"),
        PENDING_JUDGE("1", "研判环节"),
        PENDING_DISPATCH("2", "调度环节"),
        PENDING_HANDLE("4", "处置环节"),
        PENDING_CHECK("5", "核实环节"),
        END("6", "已完结"),
        DUPLICATION("7", "重复告警完结"),
        SCHEDULING_COMMAND("8", "指挥调度");
        private String code;

        private String name;

        private FlowNode(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // 普通方法
        public static String getName(String code) {
            for (FlowNode c : FlowNode.values()) {
                if (c.getCode().equals(code)) {
                    return c.name;
                }
            }
            return null;
        }
    }

    /**
     * 告警等级
     */
    public enum AlarmLevel {
        GENERALLY("01", "一般"),
        URGENT("02", "紧急"),
        SERIOUS("03", "严重"),
        VERYSERIOUS("04", "非常严重");

        private String code;

        private String name;

        private AlarmLevel(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 告警等级
     */
    public enum WarningSource {
        AI("1", "AI上报"),
        PEOPLE("2", "人工上报"),
        ONEKEY("3", "一键告警"),
        THIRD("11", "第三方告警"),
        TRADE("7", "行业告警");

        private String code;

        private String name;

        private WarningSource(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static int ONE_MINUTES = 60;

    static int ONE_HOUR = ONE_MINUTES * ONE_MINUTES;

    static int ONE_DAY = 24 * ONE_MINUTES * ONE_MINUTES;

    /**
     * 时间单位转秒
     */
    public enum TimeUnit {
        SECOND("s", 1),
        MINUTES("M", ONE_MINUTES),
        HOUR("h", ONE_HOUR),
        DAY("d", ONE_DAY);

        private String s;

        private int i;

        TimeUnit(String s, int i) {
            this.s = s;
            this.i = i;
        }

        public String getCode() {
            return s;
        }

        public static int getSecond(String s) {
            for (TimeUnit c : TimeUnit.values()) {
                if (c.getCode().equals(s)) {
                    return c.i;
                }
            }
            return 1;
        }
    }

    /**
     * 排序
     */
    public enum Order {
        ASC("asc"),
        DESC("desc");

        private String name;

        Order(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        // 普通方法
        public static boolean exist(String name) {
            for (Order c : Order.values()) {
                if (c.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 告警等级
     */
    public enum AlarmSoundType {
        DEFAULT("0", "默认声音"),
        URGENT("1", "其他声音");

        private String code;

        private String name;

        private AlarmSoundType(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 告警等级
     */
    public enum AlarmSoundStatusCd {
        enable("0", "启用"),
        Disable("1", "停用");

        private String code;

        private String name;

        private AlarmSoundStatusCd(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum AlarmSecondJudge {
        RESUT_0("0", "否"),
        RESUT_1("1", "是");

        private String code;

        private String name;

        private AlarmSecondJudge(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }



    public enum AlarmSecondJudgeResult {
        RESUT_0("0", "假"),
        RESUT_1("1", "真"),
        RESUT_2("2", "算法未分析");

        private String code;

        private String name;

        private AlarmSecondJudgeResult(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }


}
