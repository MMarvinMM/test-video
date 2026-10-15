import com.chinatower.platform.CompAdminApplication;
import com.chinatower.platform.common.constant.Constants;
import com.chinatower.platform.constants.FeignConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.service.ISysUserService;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CompAdminApplication.class)
@Slf4j
public class SysUserControllerTest {


    @Autowired
    private ISysUserService userService;

    @Test
    public void testRedis(){
        boolean test = RedisUtil.setIfAbsent("test", "1", Long.valueOf(10), TimeUnit.SECONDS, RedisDBType.PRIMARY);
        System.out.println("第一次设置："+test);
        boolean test2 = RedisUtil.setIfAbsent("test", "1", Long.valueOf(10), TimeUnit.SECONDS, RedisDBType.PRIMARY);
        System.out.println("第二次设置："+test2);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean test3 = RedisUtil.setIfAbsent("test", "1", Long.valueOf(10), TimeUnit.SECONDS, RedisDBType.PRIMARY);
        System.out.println("第三次设置："+test3);
    }

    private void login(String userId){
        SysUser sysUser = userService.selectUserById(userId);
        LoginUser loginUser = new LoginUser();
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setUser(sysUser);
        RedisUtil.set(Constants.LOGIN_TOKEN_KEY + sysUser.getUserId(), loginUser, RedisDBType.PRIMARY);

    }

    @Test
    public void testSelectUserByName(){
        SysUser lycs = userService.selectUserWithPwdByUserName("lycs");

    }
}

