package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ProcessCommonWordsDTO
 * @Deacription 事件处理常用语
 * @Author lyy
 * @Date 2023/01/29 15:02
 * @Version 1.0
 **/
@Data
@TableName("vm_process_common_words_t")
public class ProcessCommonWordsDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 常用语编码
     */
    @TableId(value = "words_id", type = IdType.AUTO)
    private Long wordsId;

    /**
     * 初始化常用语标识
     * 1-该事件已研判为真警，请尽快处理；2-该事件已判断为误报；
     * 3-该事件分派不合理，请重新转派；4-该事件已处置完成，请核实；5-该事件已操作回退，请再次派发
     */
    private String initWordsFlag;

    /**
     * 常用语内容
     */
    private String content;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 行业编码
     */
    private String industryCode;

}
