package com.chinatower.platform.ecmwf.service;

import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.outdata.api.entity.EcmwfHeader;
import com.chinatower.outdata.api.entity.EcmwfInfoParam;
import com.chinatower.outdata.api.entity.EcmwfVO;
import com.chinatower.platform.ecmwf.constants.Constants;
import com.chinatower.platform.ecmwf.domain.SysEcmwf;
import com.chinatower.platform.ecmwf.mapper.SysEcmwfMapper;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ISysEcmwfService {
    @Resource
    private SysEcmwfMapper sysEcmwfMapper;

    public void readCsv(String filePath) {
        try {
            //获取导入文件路径
            //List<SysEcmwfConf> pathList = sysEcmwfMapper.getEcmwfConf();
            //for (SysEcmwfConf cataPath : pathList) {
            SysEcmwf updateSysEcmwf = new SysEcmwf();
           // String[] parts = filePath.split("=");
            //String filePathTmp =  parts[1].replace("%3A",":").replace("%5C","\\").replace("%2F","/");
            System.out.print("读取文件路径："+filePath);
            List<File> filelist = getFileList(filePath, Constants.FILETYPE);

            BigDecimal minData = new BigDecimal(0);
            BigDecimal maxData = new BigDecimal(0);
            for (File path : filelist) {
                DataInputStream in = new DataInputStream(new FileInputStream(path));
                CSVReader csvReader = new CSVReader(new InputStreamReader(in, "UTF-8"), CSVParser.DEFAULT_SEPARATOR,
                        CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER, 0);
                   try{

                    String[] strs;
                    BigDecimal bigd;
                    List list = new ArrayList<>();
                    int i = 1;
                    while ((strs = csvReader.readNext()) != null) {
                        if (i >= Constants.STARTLINE && i <= Constants.ENDLINE) {
                            for (int j = 0; j <= 900; ++j) {
                                //System.out.println("j:"+j);
                                if (j >= Constants.STARTCOL && j <= Constants.ENDCOL) {
                                    //初始化最大最小值
                                    if(i==Constants.STARTLINE && j== Constants.STARTCOL)
                                    {
                                        minData= new BigDecimal(strs[j - 1].toString());
                                        maxData= new BigDecimal(strs[j - 1].toString());
                                    }
                                    bigd = new BigDecimal(strs[j - 1].toString());
                                    //System.out.println(bigd);
                                    list.add(bigd);
                                    if (maxData.compareTo(bigd) ==-1)  {
                                        maxData = bigd;
                                    }
                                    if (minData.compareTo(bigd) == 1)  {
                                        minData = bigd;
                                    }
                                }
                            }

                        }
                        i = i + 1;
                        //System.out.println("i:"+i);
                    }
                    System.out.println("maxData:"+maxData);
                    System.out.println("minData:"+minData);
                    //获取文件名称
                    String fileName = path.toString().substring(path.toString().lastIndexOf("/") + 1);
                    //获取参数类型、时间、步长
                    String[] filenameStr = fileName.split("_");
                    String param = filenameStr[0];
                    String inDate = filenameStr[1];
                    String step = filenameStr[2].substring(0, filenameStr[2].indexOf("."));
                       System.out.println("ecmwf_type:"+param);
                    System.out.println("inDate:"+inDate);
                    //将步长转换为起始时间
                    Date date = new SimpleDateFormat("yyyyMMdd").parse(inDate);

                    System.out.println("date:"+date);
                    Calendar rightNow = Calendar.getInstance();
                    rightNow.setTime(date);
                    rightNow.add(Calendar.HOUR, Integer.parseInt(step) - 3);
                    Date beginDate = rightNow.getTime();
                    System.out.println("beginDate:"+beginDate);
                    rightNow.setTime(date);
                    rightNow.add(Calendar.HOUR, Integer.parseInt(step));
                    Date endDate = rightNow.getTime();
                    System.out.println("endDate:"+endDate);
                    //设置参数
                    SysEcmwf sysEcmwf = new SysEcmwf();
                    sysEcmwf.setBeginTime(beginDate);
                    sysEcmwf.setEndTime(endDate);
                    sysEcmwf.setEcmwfPath(path.toString());
                    sysEcmwf.setEcmwfType(param);
                    sysEcmwf.setEcmwfStr(list.toString());
                    sysEcmwf.setDataTime(inDate.substring(0,4)+"-"+inDate.substring(4,6)+"-"+inDate.substring(6,8)+" 00:00:00");
                    sysEcmwf.setBeginLat(Constants.BEGIN_LAT);
                    sysEcmwf.setBeginLon(Constants.BEGIN_LON);
                    sysEcmwf.setEndLon(Constants.END_LON);
                    sysEcmwf.setEndLat(Constants.END_LAT);
                    sysEcmwf.setBeginLon(Constants.BEGIN_LON);
                    sysEcmwf.setGrib(Constants.GRIB);
                    sysEcmwf.setLongthNum(Constants.LONGTH_NUM);
                    sysEcmwf.setHeightNum(Constants.HEIGHT_NUM);
                    if(param.equals("sp")){
                        sysEcmwf.setGribUnit(Constants.SP_UNIT);
                    }else if(param.equals("tp")){
                        sysEcmwf.setGribUnit(Constants.TP_UNIT);
                    }else if(param.equals("2t")){
                        sysEcmwf.setGribUnit(Constants.T_UNIT);
                    }else if(param.equals("10u")){
                        sysEcmwf.setGribUnit(Constants.U_UNIT);
                    }else if(param.equals("10v")){
                        sysEcmwf.setGribUnit(Constants.V_UNIT);
                    }else if(param.equals("r")){
                        sysEcmwf.setGribUnit(Constants.R_UNIT);
                    }
                    sysEcmwf.setMaxData(maxData.toString());
                    sysEcmwf.setMinData(minData.toString());
                    //写表
                    sysEcmwfMapper.insert(sysEcmwf);
                    updateSysEcmwf.setDataTime(inDate.substring(0,4)+"-"+inDate.substring(4,6)+"-"+inDate.substring(6,8)+" 00:00:00");
                    updateSysEcmwf.setEcmwfType(param);
                    csvReader.close();

                } catch(Exception e){e.printStackTrace();
                       csvReader.close();
                }
            }

            //删除非当前入库的数据
            sysEcmwfMapper.delete(updateSysEcmwf);
            //}
            //sysEcmwfMapper.delete();
        } catch(Exception e){e.printStackTrace();

        }

    }
    /**
     * 通过文件夹路径收集指定类型文件
     *
     * @param strPath  文件路径
     * @param fileType 文件类型
     * @return
     */

    public  List<File> getFileList(String strPath, String fileType) {
        List<File> filelist = new ArrayList<>();
        File dir = new File(strPath);
        // 该文件目录下文件全部放入数组
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                // 判断是文件还是文件夹
                if (files[i].isDirectory()) {
                    // 获取文件绝对路径
                    getFileList(files[i].getAbsolutePath(), fileType);
                    // 判断文件名是否以.XXX结尾
                } else if (fileName.endsWith(fileType)) {
                    String strFileName = files[i].getAbsolutePath();
                    filelist.add(files[i]);
                } else {
                    continue;
                }
            }

        }
        System.out.println(filelist.toString());
        return filelist;
    }

    public EcmwfInfoParam getEcmwf(EcmwfVO vo) {


        System.out.println("queryData:"+vo.getQueryDate());
        List<EcmwfHeader> list = sysEcmwfMapper.getEcmwf(vo);

        if(list.size()>0) {
            EcmwfInfoParam param = new EcmwfInfoParam();

            param.setData(list.get(0).getEcmwfStr());
            list.get(0).setEcmwfStr("");
            param.setHeader(list.get(0));
            return param;
        }else{
            List<EcmwfHeader> lastList = sysEcmwfMapper.getEcmwfLastInfo(vo);

            if(lastList.size()>0) {
                EcmwfInfoParam param = new EcmwfInfoParam();

                param.setData(lastList.get(0).getEcmwfStr());
                lastList.get(0).setEcmwfStr("");
                param.setHeader(lastList.get(0));
                return param;
            }else {
                EcmwfInfoParam param = new EcmwfInfoParam();
                return param;
            }
        }
    }

    /**
     * 每天获取ecmwf气象数据。
     * 例如每天10：30获取气象数据
     * @param param 执行shell脚本,入库csv文件位置，以","分割。
     * @return
     */
    @XxlJob("runGetEcmwfInfoHandler")
    public ReturnT<String> runGetEcmwfInfoHandler(String param) {
        String flag = "0";
        String[] methodParams = param.split(",");
        log.info("定时任务获取气象数据===start==={}", DateUtils.getTime());
        log.info("param:" + param);
        log.info("执行命令..[{}]", methodParams[0]);
        log.info("文件路径...[{}]", methodParams[1]);
        try {
            String[] arguments = new String[]{methodParams[0]};
            Process process = Runtime.getRuntime().exec(arguments);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));

            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            //java代码中的process.waitFor()返回值为0表示我们调用python脚本成功，
            //返回值为1表示调用python脚本失败，这和我们通常意义上见到的0与1定义正好相反
            int re = process.waitFor();
            log.info("定时任务获取气象数据===end===返回值{}" + re);

            if (re == 0) {
                log.info("入库开始：");
                this.readCsv(methodParams[1]);
                log.info("入库结束：");
                flag = "1";
            } else {
                flag = "0";

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(flag.equals("1")){
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
            }
    }




}
