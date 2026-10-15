package com.chinatower.order.util;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.order.mapper.SysUserMapper;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * 随机密码
 */
@Component
public class PasswordUtil {

    @Autowired
    private SysUserMapper userMapper;

    private static final String UPPERCASE_CHARACTERS = "ABCDEFGHJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARACTERS = "abcdefghijkmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "~!@#%^&*?_-";
    private static final String SYS_USER_INITPASSWORD= "sys.user.initPassword";
    public static final String DEPENDENCE_MANAGE = "dependence_manage";

    /**
     * 生成随机密码（规则：随机生成的8-16个字符，包含大写字母、小写字母、数字及特殊字符，四种字符的组合。支持字母“a-z，A-Z”、数字“0-9”及特殊字符“~!@#$%^&*?_-”）
     * @return
     */
    public String generatePassword() {
        //如果是属地化环境，直接返回参数配置默认密码；其它环境正常使用随机密码
        SysConfig sysConfig = selectConfigByKey(DEPENDENCE_MANAGE);  //0-属地化、1-云化中心、2-下沉节点
        if (sysConfig != null && Constants.ZERO.equals(sysConfig.getConfigValue())) {
            return selectConfigValueByKey(SYS_USER_INITPASSWORD);
        }
        SecureRandom random = new SecureRandom();
        StringBuilder passwordBuilder = new StringBuilder();

        // 随机选择密码长度（在8到16之间）
        //int passwordLength = getRandomNumberInRange(8, 16);
        // 需求调整为固定8位密码长度
        int passwordLength = 8;

        // 确保至少包含一个大写字母、一个小写字母、一个数字和一个特殊字符
        passwordBuilder.append(getRandomCharacter(UPPERCASE_CHARACTERS));
        passwordBuilder.append(getRandomCharacter(LOWERCASE_CHARACTERS));
        passwordBuilder.append(getRandomCharacter(NUMBERS));
        passwordBuilder.append(getRandomCharacter(SPECIAL_CHARACTERS));

        // 生成剩余的密码字符
        for (int i = 4; i < passwordLength; i++) {
            String characters = UPPERCASE_CHARACTERS + LOWERCASE_CHARACTERS + NUMBERS + SPECIAL_CHARACTERS;
            passwordBuilder.append(getRandomCharacter(characters));
        }

        // 将密码字符进行随机排序
        String password = passwordBuilder.toString();
        char[] passwordArray = password.toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int randomIndex = random.nextInt(passwordArray.length);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[randomIndex];
            passwordArray[randomIndex] = temp;
        }

        return new String(passwordArray);
    }

    private String selectConfigValueByKey(String configKey) {
        SysConfig config = selectConfigByKey(configKey);
        return ObjectUtil.isNull(config) ? "" : config.getConfigValue();
    }

    private SysConfig selectConfigByKey(String configKey) {
        SysConfig config = RedisUtil.get(SYS_CONFIG_KEY+configKey, SysConfig.class, RedisDBType.PRIMARY);
        if(ObjectUtil.isNull(config)){
            config = new SysConfig();
            config.setConfigKey(configKey);
            config = userMapper.selectConfig(config);
            if(ObjectUtil.isNotNull(config)){
                RedisUtil.set(SYS_CONFIG_KEY+configKey,config, RedisDBType.PRIMARY);
            }
        }
        return config;
    }

    private static char getRandomCharacter(String characters) {
        SecureRandom random = new SecureRandom();
        int randomIndex = random.nextInt(characters.length());
        return characters.charAt(randomIndex);
    }

    private static int getRandomNumberInRange(int min, int max) {
        SecureRandom random = new SecureRandom();
        return random.nextInt((max - min) + 1) + min;
    }
}
