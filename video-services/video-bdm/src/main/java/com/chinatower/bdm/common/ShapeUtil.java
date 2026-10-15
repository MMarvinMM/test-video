package com.chinatower.bdm.common;



import com.chinatower.platform.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Slf4j
@SuppressWarnings("all")
public class ShapeUtil {

    public static final String DEF_GEOM_KEY = "the_geom";

    public static final String DEF_ENCODE = "UTF-8";
    public static final String SHP = ".shp";
    public static final String PATH = "\\";

//    public static void main1(String[] args) {
//        List<Map<String, Object>> list = readShpGis("D:\\BaiduNetdiskDownload\\星环-SHP入库测试数据\\shpout\\building2g.shp");
//        System.out.println(JSON.toJSONString(list));
//    }

//    public static void main(String[] args) {
//        File shpFile = new File("D:\\chinatower\\pg\\A图层\\mapreceive\\mapreceive.shp");
//
//        try {
//
//            Map<String, Object> params = new HashMap<>();
//            params.put("url", shpFile.toURI().toURL());
//            params.put("memory mapped buffer", true);
//            ShapefileDataStore shapefileDataStore = (ShapefileDataStore) DataStoreFinder.getDataStore(params);
//            Charset charset = Charset.forName("UTF-8");
//            shapefileDataStore.setCharset(charset);
//            FeatureSource featuresource = shapefileDataStore.getFeatureSource(shapefileDataStore.getTypeNames()[0]); //默认是文件名称
//            SimpleFeatureCollection simpleFeatureCollection = (SimpleFeatureCollection) featuresource.getFeatures(); //读取要素
//            SimpleFeatureIterator simpleFeatureIterator = simpleFeatureCollection.features();
//
//            int totalCount = 0;
//            int count = 0;
//            long time = System.currentTimeMillis();
//            while (simpleFeatureIterator.hasNext()){
//                count++;
//                SimpleFeature feature = simpleFeatureIterator.next();
//                Map<String,Object> dataMap = new HashMap<>();
//                for (Property p : feature.getProperties()) {
////                    log.info(p.getName().toString().toLowerCase(Locale.ROOT) + " == " + p.getValue());
//                    dataMap.put(p.getName().toString().toLowerCase(Locale.ROOT), p.getValue().toString());
//                    if ("id".equals(p.getName().toString().toLowerCase(Locale.ROOT)) && "1061".equals(p.getValue().toString())){
//                        System.out.println(JSON.toJSONString(dataMap));
//                    }
//                    if (!p.getType().getBinding().getName().equals(MultiPolygon.class.getName())) {
////                        properties.put(p.getName().toString().toLowerCase(Locale.ROOT), p.getValue().toString());
//                    }
//                }
////                System.out.println(JSON.toJSONString(dataMap));
////                if (count > 5){
////                    break;
////                }
//            }
////            System.out.println(System.currentTimeMillis() - time);
//            System.out.println(count);
//            simpleFeatureIterator.close();
//            shpFile.exists();
//            shapefileDataStore.dispose();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    /**
     *
     * @param zipPath zip路径
     * @param descDir 解压路径
     * @return 解压文件
     */
    @SuppressWarnings("rawtypes")
    public static boolean decompressZip(String zipPath, String descDir) {
        File zipFile = new File(zipPath);
        boolean flag = false;
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = null;
        try {
            zip = new ZipFile(zipFile, Charset.forName("gbk"));//防止中文目录，乱码
            for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                //指定解压后的文件夹+当前zip文件的名称
                String outPath = (descDir + zipEntryName).replace("/", File.separator);
                //判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
                if (!file.exists()) {
                    file.mkdirs();
                }
                //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                //保存文件路径信息(可利用md5.zip名称的唯一性，来判断是否已经解压)
                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[2048];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
            }
            flag = true;
            //必须关闭，要不然这个zip文件一直被占用着，要删删不掉，改名也不可以，移动也不行，整多了，系统还崩了。
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 读取shape文件，默认编码时utf-8
     *
     * @param shpPath shape文件地址，如：D:\data\tmp\test.shp
     * @return shape文件解析后的数据集合
     */
    public static List<Map<String, Object>> readShp(String shpPath) {
        return readShp(shpPath, DEF_ENCODE);
    }

    /**
     *
     * @param shpPath
     * @return 获得文件编码
     */
    public static Integer getSrs(String shpPath){
        DataStore ds = null;
        Integer epsg = 0;
        try {
            if (StringUtils.isEmpty(shpPath)) {
                throw new CustomException("shp文件的路径不能为空，shpPath: " + shpPath);
            }
            //加载shape文件
            File file = new File(shpPath);
            //ShapefileDataStore sds = new ShapefileDataStore("");
            Map<String, Object> map = new HashMap<>();
            map.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
            map.put(ShapefileDataStoreFactory.DBFCHARSET.key, Charset.forName(DEF_ENCODE));
            //加载数据源
            ds = DataStoreFinder.getDataStore(map);

            //获取要素源
            FeatureSource<SimpleFeatureType, SimpleFeature> fs = ds.getFeatureSource(ds.getTypeNames()[0]);
            FeatureCollection<SimpleFeatureType, SimpleFeature> collections = fs.getFeatures();
            SimpleFeatureType schema = collections.getSchema();
            CoordinateReferenceSystem srs = schema.getCoordinateReferenceSystem();
            epsg = CRS.lookupEpsgCode(srs, true);
            return epsg;
        }catch (Exception e){
            log.error("解析文件地址错误 ",e);
        }finally {
            if (ds != null){
                ds.dispose();
            }
        }
        return epsg;
    }

    /**
     * 读取shape文件
     *
     * @param shpPath shape文件地址，如：D:\data\tmp\test.shp
     * @param encode  文件编码
     * @return shape文件解析后的数据集合
     */
    public static List<Map<String, Object>> readShp(String shpPath, String encode) {
        //申明返回结果
        List<Map<String, Object>> list = new ArrayList<>();
        DataStore ds = null;
        FeatureIterator<SimpleFeature> features = null;
        try {
            if (StringUtils.isEmpty(shpPath)) {
                throw new CustomException("shp文件的路径不能为空，shpPath: " + shpPath);
            }

            if (StringUtils.isEmpty(encode)) {
                throw new CustomException("shp文件的编码不能为空，encode: " + encode);
            }
            //加载shape文件
            File file = new File(shpPath);
            Map<String, Object> map = new HashMap<>();
            map.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
            map.put(ShapefileDataStoreFactory.DBFCHARSET.key, Charset.forName(encode));
            //加载数据源
            ds = DataStoreFinder.getDataStore(map);

            //获取要素源
            FeatureSource<SimpleFeatureType, SimpleFeature> fs = ds.getFeatureSource(ds.getTypeNames()[0]);
            FeatureCollection<SimpleFeatureType, SimpleFeature> collections = fs.getFeatures();
            features = collections.features();
            SimpleFeatureType schema = collections.getSchema();
            CoordinateReferenceSystem srs = schema.getCoordinateReferenceSystem();
            Integer epsg = CRS.lookupEpsgCode(srs, true);
            if (epsg == null || epsg != 4326){
                throw new CustomException("上传文件地图数据编码不是4326，请重新上传。");
            }
            //循环获取feature
//            while (features.hasNext()) {
//                SimpleFeature feature = features.next();
//
//                Map<String, Object> dataMap = new HashMap<>();
//                dataMap.put("id",feature.getID());
//                log.info("解析ID = {}",feature.getID());
//                Map<String, Object> propertiesMap = new HashMap<>();
//                for (Property p : feature.getProperties()) {
//                    if (p.getType().getBinding().getName().equals(MultiPolygon.class.getName())) {
//                        MultiPolygon m = (MultiPolygon) p.getValue();
//                        Coordinate[] coordinates = m.getCoordinates();
//                        StringBuilder sb = new StringBuilder();
//                        sb.append("[[[");
//                        for (int i = 0; i < coordinates.length; i++) {
//                            Coordinate c = coordinates[i];
//                            if (i == coordinates.length - 1){
//                                sb.append("[" + c.getX() + "," + c.getY()+"]");
//                            }else {
//                                sb.append("[" + c.getX() + "," + c.getY()+"],");
//                            }
//
//                        }
//                        sb.append("]]]");
//                        dataMap.put("coordinates", sb.toString());
//                    }else {
//                        propertiesMap.put(p.getName().toString(), p.getValue());
//                    }
//                }
//                dataMap.put("properties", JSON.toJSONString(propertiesMap));
//                list.add(dataMap);
//            }
        } catch (CustomException e){
            throw e;
        }catch (Exception e) {
            log.error("解析错误",e);
            throw new CustomException("上传文件不符合规则，请重新上传。");
        }finally {
            if (features != null){
                features.close();
            }
            if (ds != null){
                ds.dispose();
            }
        }
        return list;
    }

    /**
     * 构建Feature模板
     *
     * @param geomType 图形信息类型 Geometry类型，如Point.class、Polygon.class等
     * @param data     图形信息具体的属性
     * @return featureType
     */
    public static SimpleFeatureType builderFeatureType(Class<?> geomType, Map<String, ?> data) {
        //定义图形信息和属性信息
        SimpleFeatureTypeBuilder ftb = new SimpleFeatureTypeBuilder();
        ftb.setCRS(DefaultGeographicCRS.WGS84);
        ftb.setName("shapefile");

        ftb.add(DEF_GEOM_KEY, geomType);

        if (MapUtils.isNotEmpty(data)) {
            for (String key : data.keySet()) {
                if (Objects.nonNull(data.get(key))) {
                    ftb.add(key.toUpperCase(), data.get(key).getClass());
                }
            }
        }

        return ftb.buildFeatureType();
    }

    /**
     * 压缩shape文件
     *
     * @param shpPath shape文件路径（包含shape文件名称）
     */
    public static void zipShapeFile(String shpPath) {
        try {
            File shpFile = new File(shpPath);
            String shpRoot = shpFile.getParentFile().getPath();
            String shpName = shpFile.getName().substring(0, shpFile.getName().lastIndexOf("."));

            String zipPath = shpRoot + File.separator + shpName + ".zip";
            File zipFile = new File(zipPath);
            InputStream input = null;
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            // zip的名称为
            zipOut.setComment(shpName);
            String[] shpFiles = new String[]{
                    shpRoot + File.separator + shpName + ".dbf",
                    shpRoot + File.separator + shpName + ".prj",
                    shpRoot + File.separator + shpName + ".shp",
                    shpRoot + File.separator + shpName + ".shx",
                    shpRoot + File.separator + shpName + ".fix"
            };

            for (int i = 0; i < shpFiles.length; i++) {
                File file = new File(shpFiles[i]);
                input = new FileInputStream(file);
                zipOut.putNextEntry(new ZipEntry(file.getName()));
                int temp = 0;
                while ((temp = input.read()) != -1) {
                    zipOut.write(temp);
                }
                input.close();
            }
            zipOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}