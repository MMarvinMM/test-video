package com.chinatower.platform.project.monitor.controller;

import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.framework.web.domain.Server;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-system
 * 服务器监控
 *
 * @author chinatower
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController extends TowerBaseController
{
    @GetMapping()
    public AjaxResult getInfo() throws Exception
    {
        Server server = new Server();
        server.copyTo();
        return AjaxResult.success(server);
    }
}
