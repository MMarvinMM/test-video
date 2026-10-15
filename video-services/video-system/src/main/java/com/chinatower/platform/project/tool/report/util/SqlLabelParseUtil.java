package com.chinatower.platform.project.tool.report.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.chinatower.platform.project.tool.report.domain.ReportCondition;

import java.util.Date;
import java.util.Map;

/**
 * 自定义sql语句标签解析工具
 * @author WANGZIC
 */
public class SqlLabelParseUtil {

    /** 参数替换 替换后的参数带 ‘’ */
    private static final String[] PARAM = {"#{","}"};

    /** 常量替换 替换后的不带 ‘’ */
    private static final String[] CONSTANT = {"${","}"};

    /** 变量替换 替换后的参数不带 */
    private static final String[] VARIABLE = {
            "$yyyyMM$","$yyyy-MM$","$yyyyMMdd$","$yyyy-MM-dd$",
            "#yyyyMM#","#yyyy-MM#","#yyyyMMdd#","#yyyy-MM-dd#"
    };

    /** 判断标签 */
    private static final String[] IF_LABEL = {"[?","?]"};


    public static String parseSql(ReportCondition condition){
        String sql = condition.getReportSql();
        // 参数替换
        sql = paramReplace(sql,condition.getParams());

        sql = variableReplace(sql);

        sql = constantReplace(sql,condition.getParams());
        // 自定义判断标签解析
        sql = ifLabelParse(sql);

        return sql;
    }

    /**
     * 变量替换
     * @param sql
     * @return
     */
    private static String variableReplace(String sql){
        for(String str: VARIABLE){
            while (sql.contains(str)){
                sql = sql.replace(str, DateUtil.format(new Date(),str.substring(1,str.length()-1)));
            }
        }
        return sql;
    }

    /**
     * 常量替换
     * @param sql
     * @param params
     * @return
     */
    private static String constantReplace(String sql, Map<String, Object> params){
        if(sql.contains(CONSTANT[0])){
            StringBuilder builder = new StringBuilder();
            String sqltmp = sql;
            while (sqltmp.contains(CONSTANT[0])){
                int startIndex = sqltmp.indexOf(CONSTANT[0]);
                int endIndex = sqltmp.indexOf(CONSTANT[1]);
                String paramKey =sqltmp.substring(startIndex+2,endIndex);
                if(StrUtil.isNotBlank(MapUtil.getStr(params,paramKey))){
                    builder.append(" ").append(sqltmp, 0, startIndex).append(params.get(paramKey));
                }else {
                    builder.append(" ").append(sqltmp, 0, endIndex+1);
                }
                sqltmp = sqltmp.substring(endIndex+1);
            }
            sql = builder.append(" ").append(sqltmp).toString();
        }
        return sql;
    }

    /**
     * 参数替换
     * @param sql
     * @param params
     * @return
     */
    private static String paramReplace(String sql, Map<String, Object> params){
        if(sql.contains(PARAM[0])){
            StringBuilder builder = new StringBuilder();
            String sqltmp = sql;
            while (sqltmp.contains(PARAM[0])){
                int startIndex = sqltmp.indexOf(PARAM[0]);
                int endIndex = sqltmp.indexOf(PARAM[1]);
                String paramKey =sqltmp.substring(startIndex+2,endIndex);
                if(StrUtil.isNotBlank(MapUtil.getStr(params,paramKey))){
                    builder.append(" ").append(sqltmp, 0, startIndex).append(" '")
                            .append(params.get(paramKey)).append("' ");
                }else {
                    builder.append(" ").append(sqltmp, 0, endIndex+1);
                }
                sqltmp = sqltmp.substring(endIndex+1);
            }
            sql = builder.append(" ").append(sqltmp).toString();
        }
        return sql;
    }

    private static String ifLabelParse(String sql){
        StringBuilder builder = new StringBuilder();
        String sqltmp = sql;
        while (sqltmp.contains(IF_LABEL[0])){
            int startIndex = sqltmp.indexOf(IF_LABEL[0]);
            int endIndex = sqltmp.indexOf(IF_LABEL[1]);
            String paramIf =sqltmp.substring(startIndex+2,endIndex);
            builder.append(" ").append(sqltmp, 0, startIndex);
            if(!paramIf.contains(PARAM[0])){
                builder.append(" ").append(paramIf);
            }
            sqltmp = sqltmp.substring(endIndex+2);
        }
        return builder.append(" ").append(sqltmp).toString();
    }

//    public static void main(String[] args) {
//        Map<String,String> param = new HashMap<>();
//        param.put(Constants.SQL,"select * from sys_user where 1=1 and create_by = #{creator} [? and update_by = #{modifier}?] [? and create_time = #{createTime} ?] ");
//        param.put("creator","admin");
//        param.put("modifier","admin");
//        System.out.println(parseSql(param));
//    }
}
