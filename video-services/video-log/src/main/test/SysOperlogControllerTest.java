import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.text.UUID;
import com.chinatower.common.entity.CommonLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.TowerLogApplication;
import com.chinatower.platform.common.constant.Constants;
import com.chinatower.platform.constants.FeignConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.service.ISysUserService;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = TowerLogApplication.class)
@Slf4j
public class SysOperlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void add(){
        String userId = 1;
        //构造登录信息
        CommonLog log = new CommonLog();
        try {
            log.setBusinessType(BusinessType.OTHER.ordinal());
            log.setDeptName("测试部门");
            log.setMethod("list");
            log.setTitle("单元测试");
            log.setOperId(System.currentTimeMillis());
            log.setOperIp("127.0.0.1");
            log.setOperParam("test");
            log.setOperUrl("/provider/log/set");
            log.setTraceId(UUID.randomUUID().toString());
            log.setStatus(1);
            log.setCreateTime(new Date());
            log.setCreateBy("admin");
            mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST,"/provider/log/set").header(FeignConstants.HEADER_USER_KEY,userId).content(JSON.toJSONString(log)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("true"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void list(){
        String content = "?pageNum=1&pageSize=10&title=测";
        try {
            mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,"/provider/sysLog/list")
                            .queryParam("pageNum","0").queryParam("pageSize","10").queryParam("title","测"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("200"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
