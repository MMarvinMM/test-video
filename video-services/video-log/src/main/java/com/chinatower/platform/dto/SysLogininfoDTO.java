package com.chinatower.platform.dto;

import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.SysLogininfoEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author iwhalecloud
 */
@Data
public class SysLogininfoDTO extends BaseMongoDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "LOG_SysLogininfor";

    /** 用户账号 */
    private String userName;

    /** 登录IP地址 */
    private String ipaddr;

    /** 登录状态 0成功 1失败 */
    private String status;

    /** 登录端类型 */
    private String clientType;

    /** 手机号码 */
    private String phonenumber;

    /** 操作系统 */
    private String os;

    /** 访问时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date beginTime;

    /** 访问时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endTime;

    private Integer pageNo;

    private Integer pageSize;

    private String orderByColumn;

    @Pattern(regexp = "asc|desc",message = "排序规则不合法")
    private String isAsc;

    /** 客户名称 */
    private String userType;

    /** 铁塔组织id */
    private String deptId;

    /** 客户id */
    private String tenantIdOrName;


    @Override
    public String getCollectionName() {
        if(this.collectionName == null){
            this.collectionName = TABLE_NAME;
        }
        return this.collectionName;
    }

    public QueryEntity getQueryEntity(){
        QueryEntity query = new QueryEntity(SysLogininfoEntity.class);
        if(StringUtils.isNotBlank(ipaddr)){
            query.regex("ipaddr", Constants.REGEX_START+this.escapeSpecialWord(ipaddr.trim())+Constants.REGEX_END);
        }
        if(StringUtils.isNotBlank(phonenumber)){
            query.regex("phonenumber",Constants.REGEX_START+this.escapeSpecialWord(phonenumber.trim())+Constants.REGEX_END);
        }
        if(StringUtils.isNotBlank(os)){
            query.regex("os",Constants.REGEX_START+this.escapeSpecialWord(os.trim())+Constants.REGEX_END);
        }
        if(StringUtils.isNotBlank(status)){
            query.eq("status",status);
        }
        if(StringUtils.isNotBlank(userName)){
            query.regex("userName",Constants.REGEX_START+this.escapeSpecialWord(userName.trim())+Constants.REGEX_END);
        }
        if(beginTime != null && endTime != null){
            query.between("loginTime",  DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd 00:00:00",beginTime)),DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd 23:59:59",endTime)));
        }
        if(StringUtils.isNotBlank(clientType)){
            query.regex("clientType",clientType.trim()+Constants.REGEX_END);
        }

        //add by liuxt 20231115
        if(StringUtils.isNotBlank(userType)){
            query.eq("userType",userType);
        }

        if(StringUtils.isNotBlank(tenantIdOrName)){
            query.eq("tenantId",tenantIdOrName);
        }

        if(StringUtils.isNotBlank(deptId)){
            query.eq("deptId",deptId);
        }
        return query;
    }

    private String escapeSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public SortEntity getSortEntity(){
        SortEntity sort = new SortEntity();
        if(StringUtils.isNotBlank(orderByColumn)){
            if(StringUtils.isNotBlank(isAsc)){
                if(isAsc.equalsIgnoreCase("desc")){
                    sort.desc(orderByColumn);
                }else{
                    sort.asc(orderByColumn);
                }
            }else{
                sort.desc(orderByColumn);
            }
        }else{
            sort.desc("loginTime");
        }
        return sort;
    }
}
