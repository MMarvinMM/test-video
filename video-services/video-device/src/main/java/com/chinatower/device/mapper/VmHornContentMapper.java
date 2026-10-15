package com.chinatower.device.mapper;

import com.chinatower.device.entity.dto.VmHornContentConfDTO;
import com.chinatower.device.entity.dto.VmHornContentConfRequestListDTO;
import com.chinatower.device.entity.dto.VmHornContentRequestDTO;
import com.chinatower.device.entity.vo.VmHornContentVO;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author willem
 * @date 2024/10/9
 */
@Mapper
public interface VmHornContentMapper {
    /**
     * 校验标题是否存在
     *
     * @param param 包含标题信息的参数对象
     * @return 如果标题已存在，则返回该标题对应的ID；否则返回null。
     */
    Integer titleExist(VmHornContentVO param);

    /**
     * 添加广播内容
     *
     * @param param 广播内容DTO对象
     * @return 添加成功的广播内容ID
     */
    Integer add(VmHornContentVO param);

    /**
     * 修改广播内容
     *
     * @param param 广播内容参数
     * @return 修改成功的记录数
     */
    Integer update(VmHornContentVO param);

    /**
     * 审核广播内容
     *
     * @param param 广播内容配置DTO
     * @return 审核结果，可能为null
     */
    Integer audit(VmHornContentVO param);

    /**
     * 删除广播内容
     *
     * @param param 广播内容配置DTO
     * @return 删除的广播内容ID列表
     */
    Integer delete(@Param("param") VmHornContentConfDTO param, @Param("resourceIds") List<String> resourceIds);

    /**
     * 获取广播内容列表
     *
     * @param param 广播内容请求列表参数
     * @return 广播内容列表
     */
    List<VmHornContentConfDTO> list(VmHornContentConfRequestListDTO param);

    /**
     * 根据类型获取广播内容列表
     *
     * @param param 广播请求参数
     * @return 广播内容列表
     */
    List<VmHornContentConfDTO> getListByContentType(VmHornContentRequestDTO param);

    /**
     * 根据资源id获取广播内容
     * @param id
     * @return
     */
    VmHornContentConfDTO getHornContentByResourceId(Long id);

    /**
     * 根据资源id查询资源类型
     * @param resourceIds
     * @return
     */
    List<VmHornContentConfDTO> selectContentTypeByResourceIds(List<String> resourceIds);

    /**
     * 根据任务ID列表查询虚拟机横幅配置信息
     *
     * @param taskIds 任务ID列表
     * @return 虚拟机横幅配置信息列表
     */
    List<VmHornContentConfDTO> selectByTaskIds(@Param("taskIds") List<Long> taskIds);

    /**
     * 校验内容是否被任务关联
     *
     * @param param 内容id
     * @return 如果标题已存在，则返回该标题对应的ID；否则返回null。
     */
    Integer contentHasRel(VmHornContentConfDTO param);

    /**
     * 查询广播内容详情
     * @param hornContentConfDTO
     * @return
     */
    VmHornContentConfDTO detail(VmHornContentConfRequestListDTO hornContentConfDTO);

    /**
     * 计算未审核内容数量
     *
     * @param idList 内容ID列表
     * @return 未审核内容数量
     */
    int countNotApprovedContent(@Param("list") List<Long> idList);
}
