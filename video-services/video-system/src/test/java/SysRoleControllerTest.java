import com.chinatower.platform.CompAdminApplication;
import com.chinatower.platform.common.constant.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.service.ISysUserService;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CompAdminApplication.class)
@Slf4j
public class SysRoleControllerTest {

    @Autowired
    private ISysUserService userService;

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

    @Autowired
    private StringEncryptor stringEncryptor;

    @Test
    public void jasypt() {
        //加密字段
        //针对3456进行加密
        String encrypt = stringEncryptor.encrypt("3456");
        System.out.println("加密后内容:" + encrypt);
        System.out.println("aaaaaaa");
        System.out.println("解密"+stringEncryptor.decrypt("O44lgog7ycWqd8eSqPsI2Sa+gKnsgDDtfQo55xSCCBA="));
    }

}

