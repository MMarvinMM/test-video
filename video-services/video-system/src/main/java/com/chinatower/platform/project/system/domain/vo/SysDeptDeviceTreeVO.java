package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.system.api.model.SysArea;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author qq
 */
@Data
@NoArgsConstructor
@ApiModel(value = "设备树")
public class SysDeptDeviceTreeVO implements Serializable {
    private String id;
    private String label;
    private String pid;
    private List<SysDeptDeviceTreeVO> children;
    private List<SysDeptDeviceVO> devices;

    public SysDeptDeviceTreeVO(SysArea area) {
        this.id = area.getAreaCode();
        this.label = area.getAreaName();
        this.pid = area.getAreaParentCode();
        this.children = new ArrayList<>();
        this.devices = new ArrayList<>();
    }

    public static List<SysDeptDeviceTreeVO> completeTop(List<SysDeptDeviceTreeVO> children) {
        if (CollectionUtils.isEmpty(children)) {
            return new ArrayList<>();
        }
        boolean has = false;
        String topId = "100000";
        for (SysDeptDeviceTreeVO child : children) {
            if (topId.equals(child.getId())) {
                has = true;
                break;
            }
        }
        if (has) {
            return children;
        }
        SysDeptDeviceTreeVO vo = new SysDeptDeviceTreeVO();
        vo.setDevices(new ArrayList<>());
        vo.setChildren(children);
        vo.setId(topId);
        vo.setPid("0");
        vo.setLabel("全国");
        return Collections.singletonList(vo);
    }
}
