package com.chinatower.platform.enums;

/**
 * 功能说明:
 *
 * @author 李峥
 * @date
 * @requestID 需求单号及需求名称
 */
public class VideoSystemEnums {

    /**
     * 用户类型枚举
     */
    public enum UserType {
        TOWER_USER("铁塔用户", "0"), TENANT_USER("租户用户", "2");
        // 成员变量
        private String name;
        private String index;
        // 构造方法
        private UserType(String name, String index) {
            this.name = name;
            this.index = index;
        }
        // 普通方法
        public static String getName(String index) {
            for (UserType c : UserType.values()) {
                if (c.getIndex().equals(index)) {
                    return c.name;
                }
            }
            return null;
        }
        // get set 方法
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getIndex() {
            return index;
        }
        public void setIndex(String index) {
            this.index = index;
        }
    }

    /**
     * 菜单适用范围枚举
     */
    public enum MenuApply {
        COMM_MENU("通用菜单", "0"), PC_MENU("PC端菜单", "1"),APP_MENU("APP端菜单", "2"),WX_MENU("小程序菜单", "3");
        // 成员变量
        private String name;
        private String index;
        // 构造方法
        private MenuApply(String name, String index) {
            this.name = name;
            this.index = index;
        }
        // 普通方法
        public static String getName(String index) {
            for (MenuApply c : MenuApply.values()) {
                if (c.getIndex().equals(index)) {
                    return c.name;
                }
            }
            return null;
        }
        // get set 方法
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getIndex() {
            return index;
        }
        public void setIndex(String index) {
            this.index = index;
        }
    }

    /**
     * 角色类型枚举
     */
    public enum RoleType {
        TOWER_ROLE("铁塔角色", "0"), PUBLIC_ROLE("公共角色", "1"),TENANT_USER("行业应用角色", "2");
        // 成员变量
        private String name;
        private String index;
        // 构造方法
        private RoleType(String name, String index) {
            this.name = name;
            this.index = index;
        }
        // 普通方法
        public static String getName(String index) {
            for (RoleType c : RoleType.values()) {
                if (c.getIndex().equals(index)) {
                    return c.name;
                }
            }
            return null;
        }
        // get set 方法
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getIndex() {
            return index;
        }
        public void setIndex(String index) {
            this.index = index;
        }
    }

    /**
     * 是否枚举
     */
    public enum IsOrNotType {
        IS("是", "1"), NOT("否", "0");
        // 成员变量
        private String name;
        private String index;
        // 构造方法
        private IsOrNotType(String name, String index) {
            this.name = name;
            this.index = index;
        }
        // 普通方法
        public static String getName(String index) {
            for (UserType c : UserType.values()) {
                if (c.getIndex().equals(index)) {
                    return c.name;
                }
            }
            return null;
        }
        // get set 方法
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getIndex() {
            return index;
        }
        public void setIndex(String index) {
            this.index = index;
        }
    }

    /**
     * 区域级别
     */
    public enum AreaLevel {
        PROVINCE("省", "2"), CITY("市", "3"), COUNTY("区县", "4");
        // 成员变量
        private String name;
        private String index;
        // 构造方法
        private AreaLevel(String name, String index) {
            this.name = name;
            this.index = index;
        }
        // 普通方法
        public static String getName(String index) {
            for (AreaLevel c : AreaLevel.values()) {
                if (c.getIndex().equals(index)) {
                    return c.name;
                }
            }
            return null;
        }
        // get set 方法
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getIndex() {
            return index;
        }
        public void setIndex(String index) {
            this.index = index;
        }
    }

    /**
     * 直辖市行政区域对应部门
     */
    public enum DirectCityArea2Dept {
        BEIJING("110000", "110100"), TIANJIN("120000", "120100"),SHANGHAI("310000", "310100"),CHONGQING("500000", "500100");
        // 成员变量
        private String name;
        private String index;
        // 构造方法
        private DirectCityArea2Dept(String name, String index) {
            this.name = name;
            this.index = index;
        }
        // 普通方法
        public static String getName(String index) {
            for (DirectCityArea2Dept c : DirectCityArea2Dept.values()) {
                if (c.getIndex().equals(index)) {
                    return c.name;
                }
            }
            return null;
        }
        // get set 方法
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getIndex() {
            return index;
        }
        public void setIndex(String index) {
            this.index = index;
        }
    }
}
