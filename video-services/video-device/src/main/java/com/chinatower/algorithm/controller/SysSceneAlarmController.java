package com.chinatower.algorithm.controller;

import com.chinatower.algorithm.dataobject.*;
import com.chinatower.algorithm.repository.ISysSceneAlarmRepository;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 场景告警关系表管理
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@RestController
@Slf4j
@Api(value = "场景告警关系表管理", tags = "场景告警关系表管理")
@RequestMapping("/video/algorithm/sceneAlarm")
public class SysSceneAlarmController {

    @Resource
    private ISysSceneAlarmRepository sysSceneAlarmRepository;

    private static final String SCENE_ID_NOT_EMPTY = "场景ID不能为空";
    private static final String QUERY_ERROR = "查询失败：";

    /**
     * 查询场景告警关系表列表（树状结构，支持分页和alarmType列表）
     *
     * @param queryDTO 查询条件
     * @return 结果
     */
    @PostMapping("/list")
    @ApiOperation("查询场景告警关系表列表（树状结构）")
    public TableDataInfo list(@RequestBody SysSceneAlarmQueryDTO queryDTO) {
        try {
            // 查询分页后的数据（内存分页）
            return sysSceneAlarmRepository.selectSysSceneAlarmTreeList(queryDTO);
        } catch (Exception e) {
            log.error("查询场景告警关系表列表异常", e);
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(500);
            return dataTable;
        }
    }


    /**
     * 新增场景告警关系表
     *
     * @param addDTO 场景告警关系表新增信息
     * @return 结果
     */
    @PostMapping("/add")
    @ApiOperation("新增场景告警关系表")
    public Result<String> add(@RequestBody SysSceneAlarmAddDTO addDTO) {
        try {
            if (StringUtils.isEmpty(addDTO.getSceneName())) {
                return Result.fail("场景名称不能为空");
            }
            if (addDTO.getAlarmTypeList() == null || addDTO.getAlarmTypeList().isEmpty()) {
                return Result.fail("告警类型列表不能为空");
            }

            int result = sysSceneAlarmRepository.insertSysSceneAlarmBatch(addDTO);
            if (result > 0) {
                return Result.success("新增成功");
            } else {
                return Result.fail("新增失败");
            }
        } catch (Exception e) {
            log.error("新增场景告警关系表异常", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 修改场景告警关系表
     *
     * @param updateDTO 场景告警关系表修改信息
     * @return 结果
     */
    @PostMapping("/update")
    @ApiOperation("修改场景告警关系表")
    public Result<String> update(@RequestBody SysSceneAlarmUpdateDTO updateDTO) {
        try {
            if (updateDTO.getSceneId() == null) {
                return Result.fail(SCENE_ID_NOT_EMPTY);
            }

            // 验证数据是否存在
            SysSceneConfigDO existEntity = sysSceneAlarmRepository.selectSysSceneAlarmById(updateDTO.getSceneId());
            if (existEntity == null) {
                return Result.fail("数据不存在");
            }

            // 检查场景是否在算法配置表中使用
            Boolean isInUse = sysSceneAlarmRepository.checkSceneInAlgorithmConfig(updateDTO.getSceneId());
            if (isInUse) {
                return Result.fail("请停用算法任务后再进行修改");
            }

            int result = sysSceneAlarmRepository.updateSysSceneAlarmBatch(updateDTO);
            if (result > 0) {
                return Result.success("修改成功");
            } else {
                return Result.fail("修改失败");
            }
        } catch (Exception e) {
            log.error("修改场景告警关系表异常", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 根据sceneId删除场景告警关系表（逻辑删除）
     *
     * @param sceneId 场景ID
     * @return 结果
     */
    @GetMapping("/delete/{sceneId}")
    public Result<String> delete(@PathVariable("sceneId") String sceneId) {
        try {
            if (StringUtils.isEmpty(sceneId)) {
                return Result.fail(SCENE_ID_NOT_EMPTY);
            }

            // 检查场景是否在算法配置表中使用
            Boolean isInUse = sysSceneAlarmRepository.checkSceneInAlgorithmConfig(sceneId);
            if (isInUse) {
                return Result.fail("请停用算法任务后再进行删除");
            }
            
            // 根据sceneId删除
            int result = sysSceneAlarmRepository.deleteSysSceneAlarmBySceneId(sceneId);
            if (result > 0) {
                return Result.success("删除成功");
            } else {
                return Result.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除场景告警关系表异常", e);
            return Result.fail("删除失败：" + e.getMessage());
        }
    }


    /**
     * 根据告警名称查询某客户行业下的告警类型
     *
     * @param queryDTO 查询参数，包含alarmTypeName（可选）
     * @return 告警类型列表
     */
    @PostMapping("/alarmTypes")
    @ApiOperation("根据告警名称查询某客户行业下的告警类型")
    public Result<List<AlarmTypeVO>> getAlarmTypes(@RequestBody(required = false) AlarmTypeQueryDTO queryDTO) {
        try {
            
            List<AlarmTypeVO> list = sysSceneAlarmRepository.selectAlarmTypesByName(queryDTO);
            return Result.data(list);
        } catch (Exception e) {
            log.error("查询告警类型异常", e);
            return Result.fail(QUERY_ERROR + e.getMessage());
        }
    }

    /**
     * 根据场景名称查询某客户行业下的场景
     *
     * @param queryDTO 查询参数，包含sceneName（可选）
     * @return 场景列表
     */
    @PostMapping("/scenes")
    @ApiOperation("根据场景名称查询某客户行业下的场景")
    public Result<List<SceneVO>> getScenes(@RequestBody(required = false) SceneQueryDTO queryDTO) {
        try {
            List<SceneVO> list = sysSceneAlarmRepository.selectScenesByName(queryDTO);
            return Result.data(list);
        } catch (Exception e) {
            log.error("查询场景异常", e);
            return Result.fail(QUERY_ERROR + e.getMessage());
        }
    }

    /**
     * 查询当前用户包含的大模型算法列表
     *
     * @return 大模型算法列表
     */
    @GetMapping("/hasModelAlgorithm")
    @ApiOperation("查询当前用户包含的大模型算法列表")
    public Result<List<ModelAlgorithmVO>> hasModelAlgorithm() {
        try {
            List<ModelAlgorithmVO> modelAlgorithmList = sysSceneAlarmRepository.getModelAlgorithms();
            return Result.data(modelAlgorithmList);
        } catch (Exception e) {
            log.error("查询大模型算法列表异常", e);
            return Result.fail(QUERY_ERROR + e.getMessage());
        }
    }

    /**
     * 根据算法编码查询场景告警树状结构
     *
     * @param queryDTO 查询参数，包含algorithmCode
     * @return 场景告警树状结构列表
     */
    @PostMapping("/treeByAlgoCode")
    @ApiOperation("根据算法编码查询场景告警树状结构")
    public Result<List<SceneTreeVO>> treeByAlgoCode(@RequestBody(required = false) SceneAlarmQueryDTO queryDTO) {
        try {
            List<SceneTreeVO> treeList = sysSceneAlarmRepository.treeByAlgoCode(queryDTO);
            return Result.data(treeList);
        } catch (Exception e) {
            log.error("查询场景告警树状结构异常", e);
            return Result.fail(QUERY_ERROR + e.getMessage());
        }
    }

    /**
     * 判断场景ID是否在算法配置表的scene_code字段中
     *
     * @param queryDTO 查询参数，包含sceneId
     * @return 场景是否在算法配置中
     */
    @PostMapping("/checkSceneInAlgorithmConfig")
    @ApiOperation("判断场景ID是否在算法配置表的scene_code字段中")
    public Result<SceneInAlgorithmConfigVO> checkSceneInAlgorithmConfig(@RequestBody SceneInAlgorithmConfigQueryDTO queryDTO) {
        try {
            if (queryDTO == null || StringUtils.isEmpty(queryDTO.getSceneId())) {
                return Result.fail(SCENE_ID_NOT_EMPTY);
            }

            SceneInAlgorithmConfigVO vo = sysSceneAlarmRepository.checkSceneInAlgorithmConfigBySceneId(queryDTO);
            return Result.data(vo);
        } catch (Exception e) {
            log.error("判断场景是否在算法配置中异常", e);
            return Result.fail(QUERY_ERROR + e.getMessage());
        }
    }

    /**
     * 根据 tenantId、industryCode 和 algorithmCode 判断 sys_scene_config 表中是否有有效数据，
     * 如果没有，则根据 vm_algorithm_info_t 表中的 model_config 字段生成数据
     *
     * @param queryDTO 查询参数，包含 tenantId、industryCode、algorithmCode
     * @return 生成的数据数量
     */
    @PostMapping("/batchByAlgorithmCode")
    @ApiOperation("根据 model_config 生成场景告警配置")
    public Result<Integer> generateFromModelConfig(@RequestBody GenerateSceneAlarmFromModelConfigDTO queryDTO) {
        try {
            if (queryDTO == null) {
                return Result.fail("参数不能为空");
            }
            if (StringUtils.isEmpty(queryDTO.getAlgorithmCode())) {
                return Result.fail("算法编码不能为空");
            }

            int count = sysSceneAlarmRepository.generateSceneAlarmFromModelConfig(
                    queryDTO.getAlgorithmCode()
            );
            return Result.data(count);
        } catch (Exception e) {
            log.error("根据 model_config 生成场景告警配置异常", e);
            return Result.fail("生成失败：" + e.getMessage());
        }
    }
}

