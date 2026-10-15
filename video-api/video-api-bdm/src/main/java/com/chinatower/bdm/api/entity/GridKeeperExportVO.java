package com.chinatower.bdm.api.entity;

import com.chinatower.common.annotation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 导入导出网格员参数
 */
@Data
@ToString
public class GridKeeperExportVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Excel(name = "责任网格(必填)")
    private String gridName;
    @Excel(name = "姓名(必填)")
    private String keeperName;
    @Excel(name = "账号(必填)")
    private String loginName;
    @Excel(name = "性别",readConverterExp = "0=男,1=女")
    private String keeperGender;
    @Excel(name = "手机号码(必填)")
    private String keeperPhoneNumber;
    @Excel(name = "邮箱")
    private String keeperEmail;
    @Excel(name = "身份证号")
    private String keeperIdCardNumber;
    @Excel(name = "出生日期",dateFormat = "yyyy-MM-dd")
    private String keeperBirthDate;
    @Excel(name = "网格员类型(必填，网格长/网格员)",readConverterExp = "1=网格长,2=网格员")
    private String keeperType;
    @Excel(name = "是否开启定位(必填)",readConverterExp = "0=是,1=否")
    private String locationFlag;
    @Excel(name = "选用日期(必填)",dateFormat = "yyyy-MM-dd")
    private String entryTime;
    @Excel(name = "微信号")
    private String wechatNumber;
    @Excel(name = "政治面貌",readConverterExp = "1=党员")
    private String party;
//    @Excel(name = "职业格言")
//    private String keeperMotto;
//    @Excel(name = "个人简历")
//    private String personalProfile;
    @Excel(name = "工作职责")
    private String operatingDuty;

    /**
     * 网格编码集合
     */
    private String[] gridIds;
}
