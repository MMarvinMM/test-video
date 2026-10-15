package com.chinatower.platform.utils;

import com.github.pagehelper.util.StringUtil;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 功能说明 : 转化sql 加参数
 * @author 杨洪洲
 * @date 9/29/21
 * @requestID R-ZBZB-202109280789713 web-admin拆分改造
 */
@Component
public class SqlInterceptor {
    private static ThreadLocal<SimpleDateFormat> dateTimeFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };


    /**
     * 功能说明 : 获取完整的sql语句
     * @author 杨洪洲
     * @date 9/29/21
     * @throws
     * @param configuration
     * @param boundSql
     * @return
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    public String getSql(Configuration configuration, BoundSql boundSql) {
        // 输入sql字符串空判断
        String sql = boundSql.getSql();
        if (StringUtil.isEmpty(sql)) {
            return "";
        }
        return formatSql(sql, configuration, boundSql);
    }

    /**
     * 功能说明 : 将占位符替换成参数值
     * @author 杨洪洲
     * @date 9/29/21
     * @throws
     * @param sql
     * @param configuration
     * @param boundSql
     * @return
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    private String formatSql(String sql, Configuration configuration, BoundSql boundSql) {

        //美化sql
        sql = beautifySql(sql);

        //填充占位符, 目前基本不用mybatis存储过程调用,故此处不做考虑
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        List<String> parameters = new ArrayList<>();
        if (parameterMappings != null) {
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    //  参数值
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    //  获取参数名称
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 获取参数值
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        // 如果是单个值则直接赋值
                        value = parameterObject;
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }

                    if (value instanceof Number) {
                        parameters.add(String.valueOf(value));
                    } else {
                        StringBuilder builder = new StringBuilder();
                        builder.append("'");
                        if (value instanceof Date) {
                            builder.append(dateTimeFormatter.get().format((Date) value));
                        } else if (value instanceof String) {
                            builder.append(value);
                        }
                        builder.append("'");
                        parameters.add(builder.toString());
                    }
                }
            }
        }

        for (String value : parameters) {
            sql = sql.replaceFirst("\\?", value);
        }
        return sql;
    }


    /**
     * 功能说明 : 美化sql
     * @author 杨洪洲
     * @date 9/29/21
     * @throws
     * @param sql
     * @return
     * @requestID R-ZBZB-202109280789713 web-admin拆分改造
     */
    public static String beautifySql(String sql) {
        sql = sql.replaceAll("[\\s\n ]+", " ");
        return sql;
    }
}



