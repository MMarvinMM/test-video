package com.chinatower.event.dto.ThreadDto;

import com.chinatower.common.entity.Result;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.Data;

import java.util.List;
@Data
public class DictOrEnumDto {

    private List<SysDictDataResponse> importanceLevelEnum;
    private Result<List<SysDictDataResponse>> dictData;
    private Result<List<SysDictDataResponse>> suppressDictData;
    private List<SysDictDataResponse> warningSourceEnum;
}
