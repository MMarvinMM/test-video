package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.platform.framework.web.domain.SystemBaseEntity;

/**
 * 参数配置表 sys_config
 *
 * @author iwhalecloud
 */
public class MsgConfig extends SystemBaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 行业版本编码*/
    private String appVerCode;

    /** 租户ID */
    private String flag;



    public String getAppVerCode() {
        return appVerCode;
    }

    public void setAppVerCode(String appVerCode) {
        this.appVerCode = appVerCode;
    }



    public String getFlag()
    {
        return flag;
    }

    public void setFlag(String flag)
    {
        this.flag = flag;
    }


}
