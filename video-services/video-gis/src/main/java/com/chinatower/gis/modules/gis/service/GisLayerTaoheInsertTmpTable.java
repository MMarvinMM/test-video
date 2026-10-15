package com.chinatower.gis.modules.gis.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.chinatower.gis.modules.gis.entity.layer.*;
import com.chinatower.gis.modules.gis.mapper.LayerTaoheAnalysisLayersInsertMapper;
import com.chinatower.gis.modules.gis.mapper.LayerTaoheMapper;
import com.chinatower.platform.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.PGCopyOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import static com.chinatower.gis.modules.gis.service.GisLayerTaoheService.globalMaxSaveDay;
import static com.chinatower.gis.modules.gis.service.PgService.XZTB_TMP_FIELDS;
import static com.chinatower.gis.utils.LayerTaoheUtil.getNewTmpTbName;

@Service
@Slf4j
public class GisLayerTaoheInsertTmpTable {

    @Autowired
    private LayerTaoheMapper layerTaoheMapper;

    @Autowired
    private LayerTaoheAnalysisLayersInsertMapper layerTaoheAnalysisLayersInsertMapper;

    @Resource
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    /**
     * 套合明细的分页结果总数
     *
     * @param analysisLayerInfos 套合分析的有效图层
     * @param guid 本次分析的 guid
     * @param userID 用户ID
     * @param serialName 分组名称对象
     * @param wkt 几何图形
     * @param distance 缓冲区半径
     * @param overArea 输入多边形的面积，单位平方米
     */
    @Async("threadPoolTaskExecutor")
    public void asyncTaoheAnalysisLayersInsert(AnalysisLayerInfos analysisLayerInfos, String guid, String userID, SerialName serialName, String wkt, double distance, double overArea) {

        StopWatch st = new StopWatch("另起线程 asyncTaoheAnalysisLayersInsert");

        st.start("asyncTaoheAnalysisLayersInsert.TaoheAnalysisLayers");
        analysisLayerInfos.TaoheAnalysisLayersDetail(serialName, wkt, distance, overArea, layerTaoheMapper);
        st.stop();

        if (serialName.newTableName == null) {
            st.start("getNewTmpTbName all");
            serialName.newTableName = getNewTmpTbName("all");
            st.stop();
            st.start("layerTaoheAnalysisLayersInsertMapper.insertTmpMngInfo");
            layerTaoheAnalysisLayersInsertMapper.insertTmpMngInfo(guid, userID, serialName.newTableName, "all", overArea);
            st.stop();
            st.start("layerTaoheAnalysisLayersInsertMapper.insertXZTBTableTmp1");
            layerTaoheAnalysisLayersInsertMapper.insertXZTBTableTmp1(serialName.newTableName);
            st.stop();
            st.start("layerTaoheAnalysisLayersInsertMapper.insertXZTBTableTmp2");
            layerTaoheAnalysisLayersInsertMapper.insertXZTBTableTmp2(serialName.newTableName);
            st.stop();
            st.start("layerTaoheAnalysisLayersInsertMapper.insertXZTBTableTmp3");
            layerTaoheAnalysisLayersInsertMapper.insertXZTBTableTmp3(serialName.newTableName);
            st.stop();
            st.start("layerTaoheAnalysisLayersInsertMapper.insertXZTBTableTmp4");
            layerTaoheAnalysisLayersInsertMapper.insertXZTBTableTmp4(serialName.newTableName);
            st.stop();
        }

        st.start("准备写入环境");
        String newTableName = serialName.newTableName;

        DataSource postgres = dynamicRoutingDataSource.getDataSource("postgres");
        DataOutputStream os = null;
        Connection hikariConnection = null;
        try {
            hikariConnection = postgres.getConnection();
            PGConnection pgConnection = hikariConnection.unwrap(org.postgresql.PGConnection.class);
            StringBuilder copySqlIn = new StringBuilder();
            copySqlIn.append("COPY ").append(newTableName).append(" (");
            copySqlIn.append(XZTB_TMP_FIELDS);
            copySqlIn.append(") FROM STDIN BINARY");
            os = new DataOutputStream(new PGCopyOutputStream(pgConnection, copySqlIn.toString()));
            byte[] headerSignature = {'P', 'G', 'C', 'O', 'P', 'Y', '\n', (byte) 0xFF, '\r', '\n', '\0'};
            os.write(headerSignature);
            os.writeInt(0);
            os.writeInt(0);
            int coll = 5;
            st.stop();

            for(AnalysisLayerInfo analysisLayerInfo: analysisLayerInfos) {
                st.start("流写入 " + analysisLayerInfo.getLayerTypeSimple());
                insertTmpTableByArr(analysisLayerInfo.getSpots(), os, coll);
                st.stop();
            }

            st.start("完成写入");
            os.writeShort(-1);
        } catch (Exception e) {
            log.error("copy 入库失败", e);
            throw new CustomException("保存图层数据错我");
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                    hikariConnection.close();
                }
            } catch (Exception e) {
                log.error("关闭jdbc 错我", e);
            }
        }
        st.stop();

        st.start("统计 allcount 数值");
        TableRowCountList tbs = analysisLayerInfos.calTableRowCounts();
        st.stop();
        String tbsStr = JSON.toJSONString(tbs);

        st.start("更新临时表管理表的 allcount");
        layerTaoheAnalysisLayersInsertMapper.updateThmanageAllcount(guid, tbsStr);
        st.stop();

        st.start("deleteTaoheTmpTable");
        deleteTaoheTmpTable();
        st.stop();

        System.out.println(st.prettyPrint());
    }

    /**
     * 套合明细的分页结果总数
     *
     * @param xztbCals zxtb对象
     * @param os DataOutputStream 对象
     * @param coll 字段数量
     */
    private static void insertTmpTableByArr(List<List<XztbCal>> xztbCals, DataOutputStream os, int coll) throws IOException {
        for (List<XztbCal> arr : xztbCals) {
            for (XztbCal xztbCal : arr) {
                os.writeShort(coll);

                // bsm, coverarea, coverratio, dlmc, reftablename
                byte[] bsm = xztbCal.getBsm().getBytes();
                os.writeInt(bsm.length);
                os.write(bsm);

                os.writeInt(8);
                os.writeDouble(xztbCal.getCalArea());

                os.writeInt(8);
                os.writeDouble(xztbCal.getCoverRatio());

                byte[] dlmc = xztbCal.getSerial().getBytes();
                os.writeInt(dlmc.length);
                os.write(dlmc);

                byte[] refTableName = xztbCal.getTbName().getBytes();
                os.writeInt(refTableName.length);
                os.write(refTableName);
            }
        }
    }

    /**
     * 批量删除临时表
     */
    private void deleteTaoheTmpTable() {
        int maxSaveDay = globalMaxSaveDay;
        StopWatch st = new StopWatch("删除临时表中的超期表");
        st.start("查询临时表中的超期表");
        String inString = String.format("createtime < (now()::timestamp + '-%d day')", maxSaveDay);
        List<String> tmpTableNames = layerTaoheAnalysisLayersInsertMapper.selectNotDeleteTmpTableName(inString);
        int size = tmpTableNames.size();
        st.stop();
        if(size > 0) {
            st.start("组装批量删除临时表的SQL");
            StringBuilder sbUpdateTableNames = new StringBuilder();
            for (int i=0; i<size; i++) {
                String tableName = tmpTableNames.get(i);
                if(i == 0) {
                    sbUpdateTableNames.append(String.format("'%s'", tableName));
                    layerTaoheAnalysisLayersInsertMapper.dropTable(tableName);
                } else {
                    sbUpdateTableNames.append(String.format(" ,'%s'", tableName));
                    layerTaoheAnalysisLayersInsertMapper.dropTable(tableName);
                }
            }
            st.stop();
            st.start("批量删除临时表");
            st.stop();

            String updateTableNames = sbUpdateTableNames.toString();
            st.start("更新临时表管理表");
            layerTaoheAnalysisLayersInsertMapper.updateTmpTables(updateTableNames);
            st.stop();
        }

        System.out.println(st.prettyPrint());
    }
}
