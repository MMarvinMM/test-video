package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 巡航路径预置点位关系表
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-22
 */
@Getter
@Setter
@TableName("vm_patrol_preset_rela_t")
public class PatrolPresetRelaDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 巡航路径id
     */
    private Long patrolId;

    /**
     * 预置位id
     */
    private Integer presetId;

    /**
     * 停留时间
     */
    private Integer staySeconds;

    /**
     * 巡航速度
     */
    private Integer speed;

    /**
     * 预置位排序
     */
    private Integer sort;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date creatorDate;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date modifierDate;

    /**
     * 删除标识0正常 1删除
     */
    private String deleteFlag;


}
