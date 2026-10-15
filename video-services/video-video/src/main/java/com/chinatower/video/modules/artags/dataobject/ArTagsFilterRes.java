package com.chinatower.video.modules.artags.dataobject;

import java.util.List;

import com.chinatower.video.modules.artags.dataobject.ArTagsInfo;

import lombok.Data;

@Data
public class ArTagsFilterRes {
	
	private Integer tagsType;
	
	private List<ArTagsInfo> data;
	
}
