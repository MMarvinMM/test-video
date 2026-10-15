package com.chinatower.bdm.modules.grid.controller;

import cn.hutool.core.collection.CollUtil;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.modules.grid.service.BdmGridService;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.gis.api.entity.DistrictsVO;
import com.chinatower.gis.api.entity.GisKeyWordQueryReq;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.ISysImportService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * video-bdm
 * @Author：zl
 * @Description：网格管理接口
 * @Date： 2022/8/9
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__网格管理
 */
@Api(value = "网格管理接口-Api",tags = "网格管理菜单后端接口")
@Slf4j
@RestController
@RequestMapping("/grid")
public class BdmGridController extends TowerBaseController {
    @Resource
    BdmGridService bdmGridService;
    @Resource
    ISysImportService importService;

    /**
     * 删除网格
     * @Author：zl
     * @param gridVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "删除网格",notes = "删除网格")
    @PostMapping("/deleteInfo")
    public Result deleteGridInfo(@RequestBody GridVO gridVO) {
        Result res;
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            gridVO.setModifier(currentUser.getUser().getUserId().toString());
            res = bdmGridService.deleteGridInfo(gridVO);
        }catch (Exception e){
            log.error(Constants.DELETE_GRID_INFO_ERRO,e);
            res = Result.fail(Constants.DELETE_GRID_INFO_ERRO);
        }
        return res;
    }

    /**
     * 修改网格
     * @Author：zl
     * @param gridVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "修改网格",notes = "修改网格")
    @PostMapping("/update")
    public Result updateGridInfo(@RequestBody GridVO gridVO) {
        Result res;
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            gridVO.setModifier(currentUser.getUser().getUserId().toString());
            res = bdmGridService.updateGridInfo(gridVO);
        }catch (Exception e){
            log.error(Constants.UPDATE_GRID_INFO_ERRO,e);
            res = Result.fail(Constants.UPDATE_GRID_INFO_ERRO);
        }
        return res;
    }

    /**
     * 新增网格
     * @Author：zl
     * @param gridVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "新增网格",notes = "新增网格")
    @PostMapping("/add")
    public Result addGridInfo(@RequestBody GridVO gridVO) {
        Result res;
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            gridVO.setTenantId(currentUser.getUser().getTenantId());
            gridVO.setIndustryCode(currentUser.getUser().getIndustryCode());
            gridVO.setAppVerCode(currentUser.getUser().getAppVerCode());
            gridVO.setCreator(currentUser.getUser().getUserId().toString());
            res = bdmGridService.addGridInfo(gridVO);
        }catch (Exception e){
            res = Result.fail(Constants.ADD_GRID_INFO_ERRO);
            log.error(Constants.ADD_GRID_INFO_ERRO,e);
        }
        return res;
    }

    /**
     * 新增最高级网格
     * @Author：zl
     * @param gridVOList
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "新增最高级网格",notes = "新增最高级网格")
    @PostMapping("/addFirstGrid")
    public Result addFirstGrid(@RequestBody List<GridVO> gridVOList) {
        Result res;
        if (CollectionUtils.isEmpty(gridVOList)){
            return Result.fail(Constants.CHECK_IN_ARG);
        }
        try{
            res = bdmGridService.addFirstGrid(gridVOList);
        }catch (Exception e){
            res = Result.fail(Constants.ADD_FIRST_GRID_INFO_ERRO);
            log.error(Constants.ADD_FIRST_GRID_INFO_ERRO,e);
        }
        return res;
    }

    /**
     * 查询网格树
     * @Author：zl
     * @param vo
     * @return AjaxResult
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "查询网格树",notes = "查询网格树")
    @PostMapping(value = "/getGridTree")
    public AjaxResult getGridTree(@RequestBody BaseDataScopeEntity vo) {
        return AjaxResult.success(bdmGridService.getGridTree(vo));
    }

    /**
     * 查询当前用户网格树
     * @Author：zl
     * @param vo
     * @return AjaxResult
     * @version release_v1.6.70
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "查询当前用户网格树",notes = "查询当前用户网格树")
    @PostMapping(value = "/getGridTreeCurUser")
    public Result<List<TreeSelect>> getGridTreeCurUser(@RequestBody GridVO vo) {
        return Result.data(bdmGridService.getGridTreeCurUser(vo));
    }

    /**
     * 单条数据获取
     * @Author：zl
     * @param gridId
     * @return Result<GridVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "单条数据获取",notes = "单条数据获取")
    @GetMapping("/findOne")
    public Result<GridVO> findGridInfo(Long gridId) {
        return bdmGridService.findGridInfo(gridId);
    }

    /**
     * 大屏单条数据获取
     * @Author：wangyt
     * @param param
     * @return Result<GridVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "大屏单条数据获取",notes = "大屏单条数据获取")
    @PostMapping("/screenFindGridInfo")
    public Result<GridVO> screenFindGridInfo(@RequestBody GridInfoParam param) {
        return bdmGridService.screenFindGridInfo(param);
    }

    /**
     * 单条数据获取sdk
     * @Author：zl
     * @param param
     * @return Result<GridVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "单条数据获取sdk",notes = "单条数据获取sdk")
    @PostMapping("/findOneSdk")
    public Result<GridVO> findGridInfoSdk(@RequestBody GridInfoParam param){
        return bdmGridService.findGridInfo(param.getGridId());
    }


    /**
     * 根据父级网格获取子集网格列表数据
     * @Author：zl
     * @param gridVO
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "网格列表",notes = "网格列表")
    @PostMapping("/getGridList")
    public TableDataInfo getGridList(@RequestBody GridParam gridVO) {
        gridVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        gridVO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        startPage();
        return getDataTable(bdmGridService.getGridList(gridVO));

    }


    /**
     * 网格导入
     * @Author：zl
     * @param file
     * @param importType
     * @param fileUrl
     * @return Result<String>
     * @throws Exception
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "网格导入")
    @PostMapping("/import")
    public Result<String> importData(MultipartFile file, String importType, String fileUrl) throws Exception {
        List<GridExportVO> list = new ExcelUtil<>(GridExportVO.class).importExcel(file.getInputStream());
        if (CollUtil.isEmpty(list)) {
            return Result.fail(Constants.IMPORT_DATA_NOT_NULL);
        }
        ImportParams<GridExportVO> params = new ImportParams<>(list, importType, file.getOriginalFilename(), fileUrl);
        params.put("loginUser",SecurityUtils.getLoginUser().getUser());
        return importService.autoThreadImport(params, p -> bdmGridService.importData(p));
    }

    /**
     * 根据经纬度获取网格
     * @Author：zl
     * @param param
     * @return Result<List<GridByLngLat>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "根据经纬度获取网格",notes = "根据经纬度获取网格")
    @PostMapping("/getMessageByLngLat")
    public Result<List<GridByLngLat>> getMessageByLngLat(@RequestBody GridByLngLatParam param){
        return Result.data(bdmGridService.getMessageByLngLat(param));
    }

    /**
     * 根据经纬度获取网格 for 微创告警调用
     * @Author：zl
     * @param param
     * @return Result<List<GridByLngLat>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "根据经纬度获取网格",notes = "根据经纬度获取网格")
    @PostMapping("/getMessageByLngLatNew")
    public Result<List<GridByLngLat>> getMessageByLngLatNew(@RequestBody GridByLngLatParam param){
        return Result.data(bdmGridService.getMessageByLngLatNew(param));
    }

    /**
     * 获取租户网格图层
     * @Author：zl
     * @param gridLayerParam
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "租户网格图层",notes = "租户网格图层")
    @PostMapping("/getGridListByLayer")
    public Result getGridListByLayer(@RequestBody GridLayerParam gridLayerParam) {
        return Result.data(bdmGridService.getGridLayerList(gridLayerParam));

    }


    /**
     * 根据省市区获取租户网格图层
     * @Author：zl
     * @param gridLayerParam
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @PostMapping("/getGridListByRegion")
    public Result getGridListByRegion(@RequestBody GridLayerParam gridLayerParam) {
        return Result.data(bdmGridService.getGridListByRegion(gridLayerParam));

    }

    /**
     * 获取租户权限内网格数据
     * @Author：zl
     * @param gridAllParam
     * @return Result<List<GridVO>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "获取租户权限内网格数据",notes = "获取租户权限内网格数据")
    @PostMapping("/getGridListByTenantId")
    public Result<List<GridVO>> getGridListByTenantId(@RequestBody GridAllParam gridAllParam) {
        return Result.data(bdmGridService.getGridListByTenantId(gridAllParam));

    }

    /**
     * 智慧乡镇根据网格id查网格名称
     * @Author：zl
     * @param list
     * @return Result<List<GridVO>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "根据网格id查网格名称",notes = "根据网格id查网格名称")
    @PostMapping("/getGridNameByGridId")
    public Result<List<GridVO>> getGridNameByGridId(@RequestBody List<Long> list) {
        return Result.data(bdmGridService.getGridNameByGridId(list));

    }

    /**
     * 同步区域信息
     * @Author：zl
     * @return Result<String>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "同步区域信息",notes = "同步区域信息")
    @PostMapping("/syncAreaInfo")
    public Result syncAreaInfo(@RequestBody List<String> tenantIdList) {
        if (CollectionUtils.isEmpty(tenantIdList)){
            return Result.success(Constants.CHECK_IN_ARG);
        }
        return bdmGridService.syncAreaInfo(tenantIdList);
    }
    /**
     * 地图检索
     * @Description: 地图检索
     * @param gisKeyWordQueryReq 入参
     * @return com.chinatower.common.entity.Result
     * @Author: TangJinBo
     * @Date: 2022/12/13 17:48
     */
    @ApiOperation(value = "地图检索",notes = "地图检索")
    @PostMapping("/keyWordQuery")
    public Result keyWordQuery(@RequestBody GisKeyWordQueryReq gisKeyWordQueryReq){
        if (Objects.isNull(gisKeyWordQueryReq)) {
            return Result.success(Constants.CHECK_IN_ARG);
        }
        return bdmGridService.keyWordQuery(gisKeyWordQueryReq);
    }

    /**
     * 行政区域查询
     * @Description: 行政区域查询
     * @param gisKeyWordQueryReq 入参
     * @return com.chinatower.common.entity.Result
     * @Author: TangJinBo
     * @Date: 2022/12/13 17:48
     */
    @ApiOperation(value = "行政区域查询",notes = "行政区域查询")
    @PostMapping("/districtQuery")
    public Result<List<DistrictsVO>>  districtQuery(@RequestBody GisKeyWordQueryReq gisKeyWordQueryReq){
        if (Objects.isNull(gisKeyWordQueryReq)) {
            return Result.success(Constants.CHECK_IN_ARG);
        }
        return bdmGridService.districtQuery(gisKeyWordQueryReq);
    }


    /**
     * 网格压缩接口
     * @Author：lxt
     * @param gridVO
     * @return Result<GridVO>
     * @version release_v1.6.60
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "网格压缩接口",notes = "网格压缩接口")
    @PostMapping("/gridCompress")
    public Result<String> gridCompress(@RequestBody GridVO gridVO){
        return Result.data(bdmGridService.gridCompress(gridVO));
    }


    /**
     * 批量压缩网格
     * @Author：lxt
     * @param
     * @return Result<GridVO>
     * @version release_v1.6.60
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "批量压缩网格接口",notes = "批量压缩网格接口")
    @PostMapping("/batchGridCompress")
    public void batchGridCompress(@RequestBody GridNoPageVO gridVO){
        long startMillis = Calendar.getInstance().getTimeInMillis();
        int success = Constants.ZERO_NUM;
        int fail = Constants.ZERO_NUM;
        List<GridListVO> list = bdmGridService.getPointGridList(gridVO);
        log.info("total："+list.size());
        if(!CollectionUtils.isEmpty(list)){
            for (int i = Constants.ZERO_NUM; i < list.size(); i++) {
                GridVO tempGridVO = new  GridVO();
                tempGridVO.setGridRange(list.get(i).getGridRange());
                String finalRange = bdmGridService.gridCompress(tempGridVO);
                if(!StringUtils.isEmpty(finalRange)){
                    GridVO updateGridVO = new GridVO();
                    updateGridVO.setGridId(Long.valueOf(list.get(i).getGridId()));
                    updateGridVO.setGridRange(finalRange);
                    int num = bdmGridService.updateGridRange(updateGridVO);
                    if(num > Constants.ZERO_NUM){
                        success ++;
                    }else{
                        fail ++;
                    }
                }else{
                    log.error("失败网格id:"+list.get(i).getGridId());
                    fail ++;
                }
            }
        }
        log.info("批量压缩网格接口结束,共执行" + list.size() + "条，成功" + success + "条,失败" + fail +
                "条,耗时"+ (Calendar.getInstance().getTimeInMillis() - startMillis) +"毫秒！");
    }


    /**
     * 批量处理网格透明度，边框线条粗细 ，颜色等历史数据
     * @Author：lxt
     * @param
     * @return Result<GridVO>
     * @version release_v1.6.60
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @ApiOperation(value = "批量处理网格历史数据接口",notes = "批量处理网格历史数据接口")
    @PostMapping("/batchGridDeal")
    @Transactional(rollbackFor = Exception.class)
    public void batchGridDeal(@RequestBody GridNoPageVO gridVO){
        List<GridListVO> list = bdmGridService.getPointGridList(gridVO);
        log.info("total："+list.size());
        List<GridVO> gridVOList = new ArrayList();
        if(!CollectionUtils.isEmpty(list)){
            for (int i = Constants.ZERO_NUM; i < list.size(); i++) {
                GridListVO gridListVO = list.get(i);
                String color = gridListVO.getColor();
                String gridId = gridListVO.getGridId();
                if(!StringUtils.isEmpty(color)){
                    String[] colorArr = color.split(Constants.COMMA);
                    if(colorArr != null && colorArr.length != Constants.ZERO_NUM){
                        String borderColorLineThickness = "";
                        String borderColorTransparency = "";
                        String colorTransparency = "";
                        for (int j = Constants.ZERO_NUM; j < colorArr.length; j++) {
                            borderColorLineThickness = borderColorLineThickness + "1,";
                            borderColorTransparency = borderColorTransparency + "0.5,";
                            colorTransparency = colorTransparency + "0.5,";
                        }
                        borderColorLineThickness = borderColorLineThickness.substring(Constants.ZERO_NUM,borderColorLineThickness.length()-Constants.ONE_NUM);
                        borderColorTransparency = borderColorTransparency.substring(Constants.ZERO_NUM,borderColorTransparency.length()-Constants.ONE_NUM);
                        colorTransparency = colorTransparency.substring(Constants.ZERO_NUM,colorTransparency.length()-Constants.ONE_NUM);
                        GridVO tempGridVO = new GridVO();
                        tempGridVO.setGridId(Long.valueOf(gridId));
                        tempGridVO.setBorderColor(color);
                        tempGridVO.setBorderColorLineThickness(borderColorLineThickness);
                        tempGridVO.setBorderColorTransparency(borderColorTransparency);
                        tempGridVO.setColorTransparency(colorTransparency);
                        gridVOList.add(tempGridVO);
                    }
                }
            }
            //批量更新理网格历史数据
            bdmGridService.updateGridBatch(gridVOList);
        }
    }



    /**
     * 根据经纬度刷数据
     */
    @PostMapping("/common/brushDataBasedWithLongitudeAndLatitude")
    public String brushDataBasedWithLongitudeAndLatitude(@RequestBody GridParam gridVO) {
        return bdmGridService.brushDataBasedWithLongitudeAndLatitude(gridVO);

    }



    /**
     * 根据网格名称查询当前客户，当前行业的网格列表
     */
    @PostMapping("/findGridList")
    public Result<List<GridVO>> findGridList(@RequestBody GridNoPageVO gridNoPageVO) {
        return bdmGridService.findGridList(gridNoPageVO);
    }


}
