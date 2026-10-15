package com.chinatower.bdm.modules.layer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LayerTypeStyleModifyDeleteDto implements Serializable {

    private List<LayerTypeStyle> addList;

    private List<LayerTypeStyle> updateList;

    private List<LayerTypeStyle> deleteList;

}
