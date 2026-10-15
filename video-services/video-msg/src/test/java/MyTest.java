import com.alibaba.fastjson.JSON;
import com.chinatower.platform.MsgApplication;
import com.chinatower.platform.common.sms.ItowerSmsUtil;
import com.chinatower.platform.entity.msg.BsJpushRecord;
import com.chinatower.platform.project.service.CommonSmsDbService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * @Description:
 * @Author: zs
 * @Date: 2021/01/26 16:46
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MsgApplication.class)
public class MyTest {


    @Autowired
    private ItowerSmsUtil itowerSmsUtil;
    @Resource
    private CommonSmsDbService commonSmsDbService;

    @Test
    public void test3() {
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("code", "123456");
//        Map<String,String> map2= itowerSmsUtil.send(JSON.toJSONString(map), "15801333885", "whlp_mscl");
//        System.out.println(JSON.toJSONString(map2));

        BsJpushRecord bsJpushRecord=new BsJpushRecord();
        bsJpushRecord.setAlias("123321");
        bsJpushRecord.setMsgContent("短信内容");
        bsJpushRecord.setExtrasparam("额外信息");
        bsJpushRecord.setMsgTitle("通知标题");
        bsJpushRecord.setNotificationTitle("标题1");
        commonSmsDbService.saveJpushRecord(bsJpushRecord,"1");

    }


}
