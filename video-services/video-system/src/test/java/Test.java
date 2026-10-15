import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.utils.SecurityUtils;

public class Test {

    public static void main(String[] args){
        System.out.println("18996145560".replaceAll("(\\d{3})\\d*(\\d{4})", "$1****$2"));
        System.out.println( SecurityUtils.decryptPassword("F5DD7CF74EB6FB0A12734F9518EE324A"));
    }
}
