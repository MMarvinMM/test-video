package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
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
public class ProcessCommonWordsDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 常用语编码
     * @required true
     */
    private Long wordsId;

    /**
     * 初始化常用语标识
     * 1-该事件已研判为真警，请尽快处理；2-该事件已判断为误报；
     * 3-该事件分派不合理，请重新转派；4-该事件已处置完成，请核实；5-该事件已操作回退，请再次派发
     * @required true
     */
    private String initWordsFlag;

    /**
     * 常用语内容
     * @required true
     */
    private String content;

    /**
     * 常用语标识集合
     * @required false
     */
    private List<String> initWordsFlagList;

}
