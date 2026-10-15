package com.chinatower.platform.modules.gis.service;

import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.platform.modules.gis.dao.LayerDAO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class LayerService {
    @Resource
    private LayerDAO layerDAO;
    @Resource
    private GisLayerAPI gisLayerAPI;
    /**
     *
     * @return 创建“其他类型”数据类型的表信息
     */
    @XxlJob("createQttypeInfo")
    public ReturnT<String> createQtTypeInfo(String param) {
        return createQtTypeInfoDo();
    }

    /**
     *
     * @return 矿产过期时间计算
     */
    @XxlJob("layerKuangChanExpireCalculate")
    public ReturnT<String> tewcExpireCalculate(String param) {
        gisLayerAPI.updateExpire();
        return ReturnT.SUCCESS;
    }


    public ReturnT<String> createQtTypeInfoDo() {

        /* 复原表的sql
         * delete from sys_layer_type;
         * insert into sys_layer_type select * from sys_layer_type_20240124145239603;
         * delete from sys_layer_type_fields;
         * insert into sys_layer_type_fields select * from sys_layer_type_fields_20240124145239603;
         * */

        StopWatch st = new StopWatch("创建“其他类型”数据类型的表信息");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String formattedDateTime = now.format(formatter);
        String typetableback = "sys_layer_type_" + formattedDateTime;
        String typefieldstableback = "sys_layer_type_fields_" + formattedDateTime;

//        String sql = "";
//        sql = String.format("create table %s as select * from sys_layer_type;", typetableback); // 备份 sys_layer_type
        st.start("备份 sys_layer_type 为 " + typetableback);
//        layerDAO.executeSql(sql);
        layerDAO.executeTypetableback(typetableback);
        st.stop();

//        sql = String.format("create table %s as select * from sys_layer_type_fields;", typefieldstableback); // 备份 sys_layer_type_fields
        st.start("备份 sys_layer_type_fields 为 " + typefieldstableback);
        layerDAO.executeTypefieldstableback(typefieldstableback);
        st.stop();

//        sql = "SELECT DISTINCT abbreviation\n" +
//                "FROM\n" +
//                "    sys_layer_type\n" +
//                "WHERE\n" +
//                "    abbreviation NOT IN ('xztb' , 'yjjbnt', 'stbhhx', 'jsydgzq', 'tdbp', 'slzy')\n" +
//                "ORDER BY abbreviation;";        // 查询其他类型

//        StringBuilder sbSql = new StringBuilder();
        List<String> abbs = layerDAO.selectAbbreviation();
//        st.start("组装更新sql语句");
//        for (int i=0; i<abbs.size(); i++) {
//            String abb = abbs.get(i);
//
//            System.out.println("生成 " + abb + " 的sql语句");
//            // 其他类型地块	qtlxdk
//            sbSql.append(String.format("update sys_layer_type set \n" +
//                    "\tadd_other_field_values_flag = 1,\t\t-- 是否记录未填写的字段\n" +
//                    "\tsuper_type_name = 'qt',\t\t\t\t\t-- 父类类型名称\n" +
//                    "\tinitial_type_name = null,\t\t\t\t-- 祖类类型名称\n" +
//                    "\tnot_covered_label = null,\t-- 未覆盖显示的标签名（套合）\n" +
//                    "\tcovered_label = null,\t\t\t-- 覆盖显示的标签名（套合）\n" +
//                    "\tcovered_field_code = null,\t\t\t\t-- 覆盖分类字段code（套合）\n" +
//                    "\tunique_field_code = 'bsm',\t\t\t\t-- 唯一值的字段名（套合）\n" +
//                    "\tarea_ref = null,\t\t\t\t-- 业务面积字段名\n" +
//                    "\tlabel = 'bsm',\t\t\t\t\t\t\t-- 属性显示名称（套合）\n" +
//                    "\ttbbh_ref = 'bsm'\t\t\t\t\t\t-- 指定图斑编号的替代字段\n" +
//                    "where abbreviation = '%s';", abb));
//            // 删除该类型数据
//            sbSql.append(String.format("delete from sys_layer_type_fields where abbreviation = '%s';", abb));
//            // 1	标识码	BSM	character varying		50		>0	O	支持模糊查询
//            sbSql.append(String.format("INSERT INTO sys_layer_type_fields \n" +
//                    "\t(id, abbreviation, code, name, remark, type, chart_length, not_null_flag, filter_type, filter_index, identify_index, result_index, delete_flag) VALUES (\n" +
//                    "\tUUID(),\t\t\t\t-- id\n" +
//                    "\t'%s',\t\t\t-- abbreviation 图层类型简称\n" +
//                    "\t'bsm',\t\t\t\t-- code 字段代码\n" +
//                    "\t'标识码',\t\t\t-- name 字段显示名称\n" +
//                    "\t'标识码字段信息，值来自于“地块编号”字段',\t-- remark 备注，说明\n" +
//                    "\t3,\t\t\t\t\t-- type 类型 1:int 2:double 3:varchart 4:timestamp\n" +
//                    "\t50,\t\t\t\t\t-- chart_length 字符类型长度\n" +
//                    "\t1,  \t\t\t\t-- not_null_flag 是否是不能为空的字段（0 代表 false，也就可以为空; 1 代表 true，不能为空)'\n" +
//                    "\t0,  \t\t\t\t-- filter_type 筛选类型（0文本，1下拉，默认：2空）\n" +
//                    "\t1,  \t\t\t\t-- filter_index 属性查询功能的顺序号（默认为0，起始为1）\n" +
//                    "\t1,\t\t\t\t\t-- identify_index 识别功能的顺序号\n" +
//                    "\t1,\t\t\t\t\t-- result_index 结果列表的顺序号\n" +
//                    "\t0\t\t\t\t\t-- delete_flag 删除状态（0 代表 false，未删除; 1 代表 true，已删除)\n" +
//                    ");", abb));
//            // 2	要素代码	ysdm	character varying		20			O	支持模糊查询
//            sbSql.append(String.format("INSERT INTO sys_layer_type_fields \n" +
//                    "\t(id, abbreviation, code, name, remark, type, chart_length, not_null_flag, filter_type, filter_index, identify_index, result_index, delete_flag) VALUES (\n" +
//                    "\tUUID(),\t\t\t\t-- id\n" +
//                    "\t'%s',\t\t\t-- abbreviation 图层类型简称\n" +
//                    "\t'ysdm',\t\t\t\t-- code 字段代码\n" +
//                    "\t'要素代码',\t\t\t-- name 字段显示名称\n" +
//                    "\t'要素代码字段信息',\t-- remark 备注，说明\n" +
//                    "\t3,\t\t\t\t\t-- type 类型 1:int 2:double 3:varchart 4:timestamp\n" +
//                    "\t20,\t\t\t\t\t-- chart_length 字符类型长度\n" +
//                    "\t0,  \t\t\t\t-- not_null_flag 是否是不能为空的字段（0 代表 false，也就可以为空; 1 代表 true，不能为空)'\n" +
//                    "\t0,  \t\t\t\t-- filter_type 筛选类型（0文本，1下拉，默认：2空）\n" +
//                    "\t2,  \t\t\t\t-- filter_index 属性查询功能的顺序号（默认为0，起始为1）\n" +
//                    "\t0,\t\t\t\t\t-- identify_index 识别功能的顺序号\n" +
//                    "\t2,\t\t\t\t\t-- result_index 结果列表的顺序号\n" +
//                    "\t0\t\t\t\t\t-- delete_flag 删除状态（0 代表 false，未删除; 1 代表 true，已删除)\n" +
//                    ");", abb));
//            // 3	行政区代码	xzqdm	character varying		50			O	支持模糊查询
//            sbSql.append(String.format("INSERT INTO sys_layer_type_fields \n" +
//                    "\t(id, abbreviation, code, name, remark, type, chart_length, not_null_flag, filter_type, filter_index, identify_index, result_index, delete_flag) VALUES (\n" +
//                    "\tUUID(),\t\t\t\t-- id\n" +
//                    "\t'%s',\t\t\t-- abbreviation 图层类型简称\n" +
//                    "\t'xzqdm',\t\t\t\t-- code 字段代码\n" +
//                    "\t'行政区代码',\t\t\t-- name 字段显示名称\n" +
//                    "\t'行政区代码字段信息',\t-- remark 备注，说明\n" +
//                    "\t3,\t\t\t\t\t-- type 类型 1:int 2:double 3:varchart 4:timestamp\n" +
//                    "\t50,\t\t\t\t\t-- chart_length 字符类型长度\n" +
//                    "\t0,  \t\t\t\t-- not_null_flag 是否是不能为空的字段（0 代表 false，也就可以为空; 1 代表 true，不能为空)'\n" +
//                    "\t0,  \t\t\t\t-- filter_type 筛选类型（0文本，1下拉，默认：2空）\n" +
//                    "\t3,  \t\t\t\t-- filter_index 属性查询功能的顺序号（默认为0，起始为1）\n" +
//                    "\t0,\t\t\t\t\t-- identify_index 识别功能的顺序号\n" +
//                    "\t3,\t\t\t\t\t-- result_index 结果列表的顺序号\n" +
//                    "\t0\t\t\t\t\t-- delete_flag 删除状态（0 代表 false，未删除; 1 代表 true，已删除)\n" +
//                    ");", abb));
//            // 4	属性json字符串	properties	text					M
//            sbSql.append(String.format("INSERT INTO sys_layer_type_fields \n" +
//                    "\t(id, abbreviation, code, name, remark, type, chart_length, not_null_flag, filter_type, filter_index, identify_index, result_index, delete_flag) VALUES (\n" +
//                    "\tUUID(),\t\t\t\t-- id\n" +
//                    "\t'%s',\t\t\t-- abbreviation 图层类型简称\n" +
//                    "\t'properties',\t\t\t\t-- code 字段代码\n" +
//                    "\t'属性json字符串',\t\t\t-- name 字段显示名称\n" +
//                    "\t'属性json字符串字段信息，固有字段，不支持类型编辑，列举出来仅为结果列表排序使用',\t-- remark 备注，说明\n" +
//                    "\t0,\t\t\t\t\t-- type 类型 1:int 2:double 3:varchart 4:timestamp\n" +
//                    "\t0,\t\t\t\t\t-- chart_length 字符类型长度\n" +
//                    "\t0,  \t\t\t\t-- not_null_flag 是否是不能为空的字段（0 代表 false，也就可以为空; 1 代表 true，不能为空)'\n" +
//                    "\t0,  \t\t\t\t-- filter_type 筛选类型（0文本，1下拉，默认：2空）\n" +
//                    "\t0,  \t\t\t\t-- filter_index 属性查询功能的顺序号（默认为0，起始为1）\n" +
//                    "\t0,\t\t\t\t\t-- identify_index 识别功能的顺序号\n" +
//                    "\t4,\t\t\t\t\t-- result_index 结果列表的顺序号\n" +
//                    "\t0\t\t\t\t\t-- delete_flag 删除状态（0 代表 false，未删除; 1 代表 true，已删除)\n" +
//                    ");", abb));
//        }
//
//        String sqls = sbSql.toString();
//        st.stop();

        st.start("批量插入临时表");
//        layerDAO.insertSql(sqls);
        layerDAO.insertAbbreviation1(abbs);
        layerDAO.insertAbbreviation2(abbs);
        layerDAO.insertAbbreviation3(abbs);
        layerDAO.insertAbbreviation4(abbs);
        layerDAO.insertAbbreviation5(abbs);
        layerDAO.insertAbbreviation6(abbs);
        st.stop();

        System.out.println(st.prettyPrint());
//        return Result.success("创建'其他类型'数据类型的表信息成功！");
        return  ReturnT.SUCCESS;
    }

}
