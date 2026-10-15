package com.chinatower.video.modules.artags.dataobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessDictType implements Serializable {

    private Integer id;

    private String typeName;

    private Integer parentId;

    private List<BusinessDictType> children;

    private Integer dictType;

}
