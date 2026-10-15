package com.chinatower.bdm.modules.gridKeeper.controller;

import cn.hutool.core.collection.CollUtil;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.modules.gridKeeper.entity.GridKeeperTTVO;
import com.chinatower.bdm.modules.gridKeeper.service.BdmGridKeeperService;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.common.utils.security.RsaUtils;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.ISysImportService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * video-bdm
 * @Author：zl
 * @Description：网格员管理接口
 * @Date： 2022/8/9
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
 */
@Api(value = "网格员相关接口-Api",tags = "网格员菜单后端接口")
@Slf4j
@RestController
@RequestMapping("/gridKeeper")
public class BdmGridKeeperController extends TowerBaseController {
    @Resource
    BdmGridKeeperService bdmGridKeeperService;
    @Resource
    ISysImportService importService;

    private final String ERROR = "参数不合法！";


    /**
     * 新增网格管理员
     * @Author：zl
     * @param gridKeeperVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "新增/编辑网格员",notes = "新增/编辑网格员")
    @PostMapping("/add")
    public Result addGridKeeperInfo(@RequestBody GridKeeperVO gridKeeperVO) {
        Result res;
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            gridKeeperVO.setTenantId(currentUser.getUser().getTenantId());
            gridKeeperVO.setCreator(currentUser.getUser().getUserId());
            gridKeeperVO.setUserId(gridKeeperVO.getStaffId());
            gridKeeperVO.setAppVerCode(currentUser.getUser().getAppVerCode());
            gridKeeperVO.setIndustryCode(currentUser.getUser().getIndustryCode());
            String phone = AESUtil.decryptBase64(gridKeeperVO.getAesPhoneNumber());
            gridKeeperVO.setKeeperPhoneNumber(phone);
            //add by liuxt 20231221 加密
            String keeperIdCardNumber = "";
            if(StringUtils.isNotEmpty(gridKeeperVO.getAesKeeperIdCardNumber())){
                keeperIdCardNumber = AESUtil.decryptBase64(gridKeeperVO.getAesKeeperIdCardNumber());
            }
            gridKeeperVO.setKeeperIdCardNumber(keeperIdCardNumber);
            res = bdmGridKeeperService.addGridKeeperInfo(gridKeeperVO);
        }catch (Exception e){
            res = Result.fail(Constants.ADD_GRID_KEEPER_ERRO);
            log.error(Constants.ADD_GRID_KEEPER_ERRO,e);
        }
        return res;
    }

    /**
     * 网格员单条数据查询
     * @Author：zl
     * @param keeperId
     * @return Result<GridKeeperVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员单条数据查询",notes = "网格员单条数据查询")
    @GetMapping("/findOne")
    public Result<GridKeeperVO> findGridKeeperInfo(Long keeperId) {
        return bdmGridKeeperService.findGridKeeperInfo(keeperId);
    }

    /**
     * 网格员单条数据查询sdk
     * @Author：zl
     * @param param
     * @return Result<GridKeeperVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员单条数据查询sdk",notes = "网格员单条数据查询sdk")
    @PostMapping("/findOneSdk")
    public Result<GridKeeperVO> findGridKeeperInfo(@RequestBody GridKeeperInfoParam param){
        return bdmGridKeeperService.findGridKeeperInfo(param.getKeeperId());
    }

    /**
     * 根据租户查询网格员列表sdk
     * @Author：zl
     * @param param
     * @return Result<List<GridKeeperVO>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "根据租户查询网格员列表sdk",notes = "根据租户查询网格员列表sdk")
    @PostMapping("/findAllSdk")
    public Result<List<GridKeeperVO>> findGridKeeperAll(@RequestBody CommonFeignEntity param){
        return bdmGridKeeperService.findGridKeeperAll(param);
    }

    /**
     * 检索网格员列表sdk
     *
     * @param param
     * @return
     */
    @ApiOperation(value = "检索网格员列表sdk", notes = "检索网格员列表sdk")
    @PostMapping("/search")
    public Result<TableDataInfo> searchGridKeeper(@RequestBody GridKeeperSearchParam param) {
        //不传分页参数默认查全部
        if (Objects.nonNull(param.getPageNum()) && Objects.nonNull(param.getPageSize())) {
            PageHelper.startPage(param.getPageNum(), param.getPageSize());
        }
        return Result.data(getDataTable(bdmGridKeeperService.searchGridKeeper(param)));
    }

    /**
     * 网格员单条数据查询
     * @Author：zl
     * @param keeperId
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员单条数据查询",notes = "网格员单条数据查询")
    @GetMapping("/findOneNoEncryption")
    public Result findGridKeeperInfoNoEncryption(Long keeperId) {
        return bdmGridKeeperService.findGridKeeperInfoNoEncryption(keeperId);
    }

    /**
     * 网格员单条数据查询sdk
     * @Author：zl
     * @param param
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员单条数据查询sdk",notes = "网格员单条数据查询")
    @PostMapping("/findOneNoEncryptionSdk")
    public Result findOneNoEncryptionSdk(@RequestBody GridKeeperInfoParam param) {
        return bdmGridKeeperService.findGridKeeperInfoNoEncryption(param.getKeeperId());
    }

    /**
     * 网格员禁用功能
     * @Author：zl
     * @param keepers
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员禁用功能",notes = "网格员禁用功能")
    @PostMapping("/stopGridKeeperInfo")
    public Result stopGridKeeperInfo(@RequestBody List<Long> keepers) {
        return bdmGridKeeperService.stopGridKeeperInfo(keepers);
    }

    /**
     * 网格员启用功能
     * @Author：zl
     * @param keepers
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员启用功能",notes = "网格员启用功能")
    @PostMapping("/openGridKeeperInfo")
    public Result openGridKeeperInfo(@RequestBody List<Long> keepers) {
        return bdmGridKeeperService.openGridKeeperInfo(keepers);
    }

    /**
     * 网格员定位关闭功能
     * @Author：zl
     * @param keepers
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员定位关闭功能",notes = "网格员定位关闭功能")
    @PostMapping("/stopGridKeeperLocal")
    public Result stopGridKeeperLocal(@RequestBody List<Long> keepers) {
        return bdmGridKeeperService.stopGridKeeperLocal(keepers);
    }

    /**
     * 网格员定位启用功能
     * @Author：zl
     * @param keepers
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员定位启用功能",notes = "网格员定位启用功能")
    @PostMapping("/openGridKeeperLocal")
    public Result openGridKeeperLocal(@RequestBody List<Long> keepers) {
        return bdmGridKeeperService.openGridKeeperLocal(keepers);
    }

    /**
     * 网格员删除
     * @Author：zl
     * @param keepers
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员删除",notes = "网格员删除")
    @PostMapping("/deleteGridKeeperInfo")
    public Result deleteGridKeeperInfo(@RequestBody List<Long> keepers) {
        return bdmGridKeeperService.deleteGridKeeperInfo(keepers);
    }

    /**
     * 网格员分页查询
     * @Author：zl
     * @param gridKeeperVO
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员分页查询",notes = "网格员分页查询")
    @PostMapping("/getList")
    public TableDataInfo getGridKeeperList(@RequestBody GridKeeperParam gridKeeperVO) {
        startPage();
        return getDataTable(bdmGridKeeperService.getGridKeeperList(gridKeeperVO));
    }

    /**
     * 根据网格id查询所属网格员
     * @Author：zl
     * @param gridId
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "根据网格id查询所属网格员",notes = "根据网格id查询所属网格员")
    @GetMapping("/getGridKeeperListByGridId")
    public Result getGridKeeperListByGridId(Long gridId){
        GridInfoParam gridVO = new GridInfoParam();
        gridVO.setGridId(gridId);
        return Result.data(bdmGridKeeperService.getGridKeeperListByGridId(gridVO));
    }

    /**
     * 网格员导入
     * @Author：zl
     * @param file
     * @param importType
     * @param fileUrl
     * @return Result<String>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员导入")
    @PostMapping("/import")
    public Result<String> importData(MultipartFile file, String importType, String fileUrl) throws Exception {
        List<GridKeeperExportVO> list = new ExcelUtil<>(GridKeeperExportVO.class).importExcel(file.getInputStream());
        if (CollUtil.isEmpty(list)) {
            return Result.fail(Constants.IMPORT_DATA_NOT_NULL);
        }
        ImportParams<GridKeeperExportVO> params = new ImportParams<>(list, importType, file.getOriginalFilename(), fileUrl);
        params.put(Constants.LOGIN_USER,SecurityUtils.getLoginUser().getUser());
        return importService.autoThreadImport(params, p -> bdmGridKeeperService.importData(p));
    }

    /**
     * 根据网格员获取所属网格
     * @Author：zl
     * @return Result<List<GridVO>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "根据网格员获取所属网格",notes = "根据网格员获取所属网格")
    @GetMapping("/getGridByKeeper")
    public Result<List<GridVO>> getGridByKeeper(){
        GridKeeperVO gridKeeperVO = new GridKeeperVO();
        return bdmGridKeeperService.getGridByKeeper(gridKeeperVO);
    }


    /**
     * 根据网格员获取所属网格(新)
     * @Author：zl
     * @return Result<List<GridVO>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "根据网格员获取所属网格(新)",notes = "根据网格员获取所属网格(新)")
    @PostMapping("/getGridByKeeperNew")
    public Result<List<GridVO>> getGridByKeeperNew(@RequestBody GridKeeperVO gridKeeperVO){
        return bdmGridKeeperService.getGridByKeeperNew(gridKeeperVO);
    }

    /**
     * 根据租户获取所属网格
     * @Author：zl
     * @return Result<List<GridVO>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "根据租户获取所属网格",notes = "根据网格员获取所属网格")
    @GetMapping("/getGridByTenantId")
    public Result<List<GridVO>> getGridByTenantId(){
        GridKeeperVO gridKeeperVO = new GridKeeperVO();
        return bdmGridKeeperService.getGridByTenantId(gridKeeperVO);
    }

    /**
     * 周边网格员搜索
     * @Author：zl
     * @param vo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "周边网格员搜索")
    @PostMapping("/getKeeperByPeriphery")
    public Result<List<GridKeeperTTVO>> getKeeperByPeriphery(@RequestBody KeeperPeripheryParam vo){
        return bdmGridKeeperService.getKeeperByPeriphery(vo);
    }

    /**
     * 租户网格员图层
     * @Author：zl
     * @param gridKeeperLayerParam
     * @return gridKeeperLayerParam
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "租户网格员图层",notes = "租户网格员图层")
    @PostMapping("/getGridKeeperByLayer")
    public Result getGridKeeperByLayer(@RequestBody GridKeeperLayerParam gridKeeperLayerParam) {
        gridKeeperLayerParam.setStatus(Constants.ZERO);
        return Result.data(bdmGridKeeperService.getGridKeeperListByLayer(gridKeeperLayerParam));
    }


    /**
     * 新租户网格员图层（一个网格员返回一条）
     * @Author：zl
     * @param gridKeeperLayerParam
     * @return gridKeeperLayerParam
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "租户网格员图层",notes = "租户网格员图层")
    @PostMapping("/getGridKeeperByLayerNew")
    public Result getGridKeeperByLayerNew(@RequestBody GridKeeperLayerParam gridKeeperLayerParam) {
        gridKeeperLayerParam.setStatus(Constants.ZERO);
        return Result.data(bdmGridKeeperService.getGridKeeperListByLayerNew(gridKeeperLayerParam));
    }

    /**
     * 查询网格员树
     * @Author：zl
     * @param vo
     * @return AjaxResult
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "查询网格员树",notes = "查询网格员树")
    @PostMapping(value = "/getGridKeeperTree")
    public AjaxResult getGridKeeperTree(@RequestBody @Validated KeeperTreeSelectParam vo) {
        return AjaxResult.success(bdmGridKeeperService.getGridKeeperTree(vo));
    }

    /**
     * 网格员打点接口
     * @Author：zl
     * @param vo
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员打点接口",notes = "网格员打点接口")
    @PostMapping(value = "/saveLngLatByKeeper")
    public Result saveLngLatByKeeper(@RequestBody @Validated SaveLngLatParam vo) {
        return bdmGridKeeperService.saveLngLatByKeeper(vo);
    }

    /**
     * 网格员批量打点接口
     * @Author：zhaozj
     * @param paramList
     * @return Result
     * @version release_v1.6.35
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员批量打点接口",notes = "网格员批量打点接口")
    @PostMapping(value = "/saveLngLatByKeeperBatch")
    public Result saveLngLatByKeeperBatch(@RequestBody @Validated List<SaveLngLatParam> paramList) {
        return bdmGridKeeperService.saveLngLatByKeeperBatch(paramList);
    }

    /**
     * 网格员退出登录
     * @Author：zl
     * @param keeperId
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员退出登录",notes = "网格员退出登录")
    @GetMapping(value = "/closeKeeper")
    public Result closeKeeper(Long keeperId) {
        return bdmGridKeeperService.closeKeeper(keeperId);
    }

    /**
     * 登录人员是否网格员判断接口
     * @Author：zl
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "登录人员是否网格员判断接口",notes = "登录人员是否网格员判断接口")
    @PostMapping(value = "/checkKeeper")
    public Result checkKeeper() {
        return bdmGridKeeperService.getKeeperByUserId(SecurityUtils.getLoginUser().getUser().getUserId());

    }

    /**
     * 网格员轨迹获取
     * @Author：zl
     * @param queryTrailsParam
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员轨迹获取",notes = "网格员轨迹获取")
    @PostMapping(value = "/getKeeperTrail")
    public Result getKeeperTrail(@RequestBody @Validated QueryTrailsParam queryTrailsParam) {
        return bdmGridKeeperService.getKeeperTrail(queryTrailsParam);
    }


    /**
     * 判断名下是否有代办项
     * @Author：vvv
     * @param vo
     * @return AjaxResult
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "判断名下是否有代办项",notes = "判断名下是否有代办项")
    @PostMapping(value = "/checkTaskByGrid")
    public Result checkTaskByGrid(@RequestBody GridVO vo) {
        return bdmGridKeeperService.checkTaskByGrid(vo);
    }

    /**
     * 判断名下是否有代办项
     * @Author：vvv
     * @param gridKeeperVO
     * @return AjaxResult
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "判断名下是否有代办项",notes = "判断名下是否有代办项")
    @PostMapping(value = "/checkGridUser")
    public Result checkTaskByGrid(@RequestBody GridKeeperVO gridKeeperVO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        gridKeeperVO.setTenantId(currentUser.getUser().getTenantId());
        gridKeeperVO.setCreator(currentUser.getUser().getUserId());
        gridKeeperVO.setUserId(gridKeeperVO.getStaffId());
        gridKeeperVO.setAppVerCode(currentUser.getUser().getAppVerCode());
        gridKeeperVO.setIndustryCode(currentUser.getUser().getIndustryCode());
        return bdmGridKeeperService.checkGridUser(gridKeeperVO);
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
        return bdmGridKeeperService.syncAreaInfo(tenantIdList);
    }

    /**
     * 根据用户ID查询其网格员ID
     * @Author：zyc
     * @param industryCode 行业编码
     * @param userId 用户id
     * @param tenantId 租户id
     * @return Result
     * @version release_v1.5.70
     * @requestID  SDK需求
     */
    @ApiOperation(value = "根据用户ID查询其网格员ID",notes = "根据用户ID查询其网格员ID")
    @GetMapping("/getGridKeeper")
    public  Result getGridKeeper(@RequestParam String industryCode,@RequestParam  String userId,@RequestParam String tenantId){
          if (StringUtils.isBlank(industryCode)||StringUtils.isBlank(userId)||StringUtils.isBlank(tenantId)){
              throw new CustomException(ERROR);
        }
          return bdmGridKeeperService.getGridKeeperById(industryCode,userId,tenantId);
    }

    /**
     * 根据经纬度查询网格员列表 （for 前端组件）
     * @return
     */
    @PostMapping("/getKeeperListByLonLat")
    public  Result<List<GridKeeperListVO>> getKeeperListByLonLat(@RequestBody GridKeeperListVO gridKeeperListVO){
        if (StringUtils.isBlank(gridKeeperListVO.getLongitude()) || StringUtils.isBlank(gridKeeperListVO.getLatitude())){
            throw new CustomException(ERROR);
        }
        return Result.data(bdmGridKeeperService.getKeeperListByLonLat(gridKeeperListVO));
    }

    /**
     * 根据经纬度查询网格员列表 （for 前端组件）
     * @return
     */
    @PostMapping("/getKeeperListByRegion")
    public  Result<List<GridKeeperListVO>> getKeeperListByRegion(@RequestBody GridKeeperListVO gridKeeperListVO){
        if (CollectionUtils.isEmpty(gridKeeperListVO.getRegionList())){
            throw new CustomException(ERROR);
        }
        return Result.data(bdmGridKeeperService.getKeeperListByLonLat(gridKeeperListVO));
    }

}
