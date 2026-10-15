//package com.chinatower.gis.utils;
//
//import com.alibaba.fastjson.JSON;
//import com.chinatower.gis.modules.gis.entity.LayerInfo;
//import com.chinatower.platform.common.exception.CustomException;
//import com.mysql.cj.jdbc.MysqlDataSource;
//import lombok.extern.slf4j.Slf4j;
//
//import java.lang.reflect.Field;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Slf4j
//public class PGUtils {
//
//    public static void main(String[] args)throws Exception {
//
//        List<LayerInfo> sql = sql("select * from layer_info", LayerInfo.class);
//        System.out.println(JSON.toJSONString(sql));
//    }
//
//    /**
//     * @return 获取数据库连接
//     */
//    public static Connection getPgsqlConnection() throws SQLException {
//        // 数据库连接参数
//        String url = "jdbc:postgresql://10.43.86.38:5432/gtgis"; // 按照您的数据库名称和设置更改
//        String username = "gtgis"; // 按照您的PostgreSQL用户名更改
//        String password = "CFVTjmmoSXN56Rol"; // 按照您的PostgreSQL密码更改
//        // 创建连接
//        try {
//            Connection connection = DriverManager.getConnection(url, username, password);
//            // 连接成功后，您可以执行SQL查询、插入、更新等操作。
//            return connection;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        //2.得到Connection
//        return null;
//    }
//
//    public static <T> List<T> sql(String formatSql, Class<T> cls)throws Exception{
//        Connection conn = null;
//        PreparedStatement ps = null;
//        ResultSet resultSet = null;
//        try {
//            conn = getPgsqlConnection();
//            log.info("执行sql [ {} ]", formatSql);
//            ps = conn.prepareStatement(formatSql);
//            resultSet = ps.executeQuery();
//
//            return getListData(resultSet,cls);
//        } catch (Exception e) {
//            log.error("数据转换报错",e);
//            throw new CustomException("sql异常：" + formatSql);
//        } finally {
//            if (resultSet != null) {
//                resultSet.close();
//                ps.close();
//                conn.close();
//            }
//        }
//    }
//
//    public static Object[][] getMultiResult(String sql)throws Exception{
//
////        System.out.printf("sql：%s \n", sql);
//        Connection con = null;
//        Object result[][] = null;
//        Statement stat = null;
//        ResultSet rs = null;
//        try {
//            con = getPgsqlConnection();
//            if (con == null) {
//                throw new RuntimeException("Failure of database connection");
//            }
//            stat = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
//                    ResultSet.CONCUR_READ_ONLY);
//            rs = stat.executeQuery(sql);
//            if (rs != null && rs.next()) {
//                /**
//                 * 计算行列数
//                 */
//                ResultSetMetaData rsmd = rs.getMetaData();
//                int columnCount = rsmd.getColumnCount();
//                rs.last();
//                int rowCountng = rs.getRow();
//                rs.first();
//                result = new Object[rowCountng][columnCount];
//                for (int i = 0; i < rowCountng; i++) {
//                    for (int j = 0; j < columnCount; j++) {
//                        result[i][j] = rs.getObject(j + 1);
//                    }
//                    rs.next();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//            throw new RuntimeException(e);
//        } finally {
//            rs.close();
//            stat.close();
//            con.close();
//        }
//        return result;
//    }
//
//
//    public static double getNumberResult(String sql)throws Exception{
//
////        System.out.printf("sql：%s \n", sql);
//
//        Connection con = null;
//        double result = 0.0;
//        Statement stat = null;
//        ResultSet rs = null;
//        try {
//            con = getPgsqlConnection();
//            if (con == null) {
//                throw new RuntimeException("Failure of database connection");
//            }
//            stat = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
//                    ResultSet.CONCUR_READ_ONLY);
//            rs = stat.executeQuery(sql);
//            if (rs.last()) {
//                result = Double.parseDouble(rs.getObject(1).toString());
//            }
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//            throw new RuntimeException(e);
//        } finally {
//            rs.close();
//            stat.close();
//            con.close();
//        }
//        return result;
//    }
//
//    public static String getStringResult(String sql)throws Exception{
//
////        System.out.printf("sql：%s \n", sql);
//
//        Connection con = null;
//        String result = null;
//        Statement stat = null;
//        ResultSet rs = null;
//        try {
//            con = getPgsqlConnection();
//            if (con == null) {
//                throw new RuntimeException("Failure of database connection");
//            }
//            stat = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
//                    ResultSet.CONCUR_READ_ONLY);
//            rs = stat.executeQuery(sql);
//            if (rs.last()) {
//                result = rs.getObject(1).toString();
//            }
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//            throw new RuntimeException(e);
//        } finally {
//            rs.close();
//            stat.close();
//            con.close();
//        }
//        return result;
//    }
//
//    public static Object getObjectResult(String sql)throws Exception{
//        Connection con = null;
//        Object result = "";
//        Statement stat = null;
//        ResultSet rs = null;
//        try {
//            con = getPgsqlConnection();
//            if (con == null) {
//                throw new RuntimeException("Failure of database connection");
//            }
//            stat = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
//                    ResultSet.CONCUR_READ_ONLY);
//            rs = stat.executeQuery(sql);
//            if (rs.last()) {
//                result = rs.getObject(1);
//            }
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//            throw new RuntimeException(e);
//        } finally {
//            rs.close();
//            stat.close();
//            con.close();
//        }
//        return result;
//    }
//
//    public static int executeUpdate(String sql) throws SQLException {
////        System.out.printf("sql：%s \n", sql);
//
//        Connection con = null;
//        Statement stat = null;
//        int count = -1;
//        try {
//            con = getPgsqlConnection();
//            if (con == null) {
//                throw new RuntimeException("Failure of database connection");
//            }
//            stat = con.createStatement();
//            count =stat.executeUpdate(sql);
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//        } finally {
//            stat.close();
//            con.close();
//        }
//        return count;
//    }
//
//    /**
//     * @param rs  sql执行结果
//     * @param <T> 泛型
//     * @return 组装数据
//     */
//    public static  <T> List<T> getListData(ResultSet rs, Class<T> cls) throws Exception {
//        List<T> ts = new ArrayList<>();
//        ResultSetMetaData rsmd = rs.getMetaData();
//        while (rs.next()) {
//            T t = cls.newInstance();
//            for (int i = 0; i < rsmd.getColumnCount(); i++) {
//                String colName = rsmd.getColumnName(i + 1);// 获取列名
//                Object value = rs.getObject(colName);//获取列对应的值。
//                if (i == 0) {
//                    log.info("属性名称 = {} -- 属性值 = {}", colName, value);
//                }
//                try {
//                    Field field = t.getClass().getDeclaredField(toCamelCase(colName.toLowerCase(Locale.ROOT)));
//                    field.setAccessible(true);
//                    field.set(t, value);
//                } catch (NoSuchFieldException e) {
//                    log.info("字段 {} 不存在", colName);
//                }
//
//            }
//            log.info("实体 [ {} ] 数据 {}", cls.getSimpleName(), JSON.toJSONString(t));
//            ts.add(t);
//        }
//        return ts;
//    }
//
//
//    /**
//     * 驼峰式命名法
//     */
//    public static String toCamelCase(String str) {
//        str = str.toLowerCase();
//        Pattern compile = Pattern.compile("_[a-z]|_[0-9]");
//        Matcher matcher = compile.matcher(str);
//        StringBuffer sb = new StringBuffer();
//        while (matcher.find()) {
//            matcher.appendReplacement(sb, matcher.group(0).toUpperCase().replace("_", ""));
//        }
//        matcher.appendTail(sb);
//        return sb.toString();
//    }
//}
