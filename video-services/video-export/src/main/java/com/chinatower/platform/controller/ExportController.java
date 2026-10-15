package com.chinatower.platform.controller;

import com.chinatower.controller.TowerBaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * video-export
 * @Description: 导出接口统一处理控制层
 * @Author: luo_ji
 * @CreateDate: 2022/12/19 16:46
 * @Version: 1.0
 */
@RefreshScope
@RestController
@RequiredArgsConstructor
@RequestMapping("export")
public class ExportController extends TowerBaseController {




}
