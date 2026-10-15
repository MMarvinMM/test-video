package com.chinatower.platform.common.cmpp;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.platform.common.config.SmsTemplateConf;
import com.chinatower.platform.entity.msg.BsSmsRecord;
import com.chinatower.platform.project.service.SmsService;
import com.huawei.insa2.comm.cmpp.message.CMPPDeliverMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitRepMessage;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.TypeConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Component
public class AiPaiSmsCompont {


    @Value("${AIPaiCMPPhost}")
    private String host;
    @Value("${AIPaiCMPPport}")
    private String port;
    @Value("${AIPaiCMPPsourceAddr}")
    private String sourceAddr;
    @Value("${AIPaiCMPPsharedSecret}")
    private String sharedSecret;
    @Value("${AIPaiCMPPheartbeatInterval}")
    private int heartbeatInterval;
    @Value("${AIPaiCMPPreconnectInterval}")
    private int reconnectInterval;
    @Value("${AIPaiCMPPheartbeatNoresponseout}")
    private int heartbeatNoresponseout;
    @Value("${AIPaiCMPPtransactionTimeout}")
    private int transactionTimeout;
    @Value("${AIPaiCMPPversion}")
    private int version;
    @Value("${AIPaiCMPPdebug}")
    private Boolean debug;
    //R-ZBZB-202110160795568 添加动态参数
    @Value("${AIPaiTerminalId}")
    private String terminalId;

    @Autowired
    private SmsService smsService;
    @Autowired
    private SmsTemplateConf smsTemplateConf;

    AiPaiSmsSendProxy smProxy;

    private static String RETURN_UNICODE_SC = "UnicodeBigUnmarked";
    private static String RETURN_UNICODE_TEST = "GB2312";

    private final static String MSG_CONTENT = "msgContent";

    private final static String MESSAGE = "message";
    private static Args args = new Args();

    @PostConstruct
    public void init() {
        try {
            args.set("host", host);
            args.set("port", port);
            args.set("source-addr", sourceAddr);
            args.set("shared-secret", sharedSecret);
            args.set("heartbeat-interval", heartbeatInterval);
            args.set("reconnect-interval", reconnectInterval);
            args.set("heartbeat-noresponseout", heartbeatNoresponseout);
            args.set("transaction-timeout", transactionTimeout);
            args.set("version", version);
            args.set("debug", debug);
            smProxy = new AiPaiSmsSendProxy(args, this); //CMPP3.0的话就是  SMProxy30 smProxy = new SMProxy30(args1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //发送短信
    public Map<String, String> sendCmppMsg(String phone, String msgContent) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put(MSG_CONTENT, msgContent);
        try {
            String[] dest_Terminal_Id = {phone};
            byte[] msg_Content = msgContent.getBytes("GB2312");
            CMPPSubmitMessage submitMsg = new CMPPSubmitMessage(
                    1,//@pk_Total 相同msg_Id消息总条数,短短信这里是1
                    1,//@pk_Number 相同msg_Id的消息序号
                    1,//@registered_Delivery 是否要求返回状态报告
                    1,//@msg_Level  信息级别
                    "",// @service_Id 业务类型 用户自定义 用来分类查询
                    2,//@fee_UserType 0对目的终端计费；1对源终端计费；2对SP计费;
                    "",//@fee_Terminal_Id 被计费用户的号码
                    0,//@tp_Pid GSM协议类型 一般文本的时候设0,铃声图片设1
                    0,//@tp_Udhi GSM协议类型 0不包含头部信息 1包含头部信息
                    15,//@msg_Fmt 消息格式
                    "125115",//@msg_Src 消息内容来源 6位的企业代码,这里需修改
                    "02",// @fee_Type 资费类别 一般为02：按条计信息费
                    "0",//@fee_Code 资费代码(以分为单位)
                    null,//@valid_Time 存活有效期
                    null,//@at_Time 定时发送时间
                    terminalId,//@src_Terminal_Id 移动所提供的服务代码  此处需修改
                    dest_Terminal_Id,//@dest_Terminal_Id 接收业务的MSISDN号码,就是接收短信的手机号,String数组
                    msg_Content,//@msg_Content 消息内容 byte[],发送的消息内容,需要转化为byte[]
                    "" //预留
            );
            log.info("AiPaiSmProxy" + smProxy);
            CMPPSubmitRepMessage sub = (CMPPSubmitRepMessage) smProxy.send(submitMsg);//这里的smProxy就是第2点中用单例创建的smProxy对象
            log.info("send AIPaiMessage cmpp result =====" + JSONObject.toJSONString(sub));
            //网关生成的唯一标识
            long msgId = TypeConvert.byte2long(sub.getMsgId());
            log.info("发送号码：{} result：{} msgId：{}", phone, sub.getResult(), msgId);
            map.put("msgId", String.valueOf(msgId));
            if (sub.getResult() == 0) {
                //发送成功
                map.put("code", "1");
                map.put(MESSAGE, "send msg success");
                log.info("send msg ok ");
            } else {
                map.put("code", "0");
                map.put(MESSAGE, "send msg error");
            }
        } catch (Exception e) {
            map.put("code", "0");
            map.put(MESSAGE, "send msg error");
            log.error(e.getMessage(), e);
        }
        return map;
    }


    /**
     * 发送短信验证码
     *
     * @param phone
     * @return
     * @throws Exception
     */
    public Map<String, String> sendVerificationCode(String phone, String msgCode, String template) {
//        String identifyingCode = CommonUtil.getRandom6();
        String msgContent = StrUtil.isEmpty(template) ? smsTemplateConf.getMsgTemplate() : template;
        msgContent = msgContent.replace("code", msgCode);
        Map resultMap = sendCmppMsg(phone, msgContent);
        resultMap.put(MSG_CONTENT, msgContent);
        return resultMap;
    }

    /**
     * 发送短信
     *
     * @param phone
     * @return
     */
    public Map<String, String> sendSmsMessage(String phone, String content) {
//        String msgContent = "【中国铁塔】"+content +"（若非本人操作，请忽略本短信）";
        Map resultMap = sendCmppMsg(phone, content);
        resultMap.put(MSG_CONTENT, content);
        return resultMap;
    }


    //接收短信
    public void processRecvDeliverMsg(CMPPDeliverMessage deliverMsg) {
        log.info("++++++++++++++++=++加入回调++++++++++++++++++++++++++++++++++++++");
        log.info("deliverMsg===={}", JSON.toJSONString(deliverMsg));
        if (deliverMsg.getRegisteredDeliver() == 0) {
            //上行短信
            try {
                //字节
                int msgFmt = deliverMsg.getMsgFmt();
                //上行号码
                String srcTerminalId = esc0(deliverMsg.getSrcterminalId());
                //上行短信内容
                String msgContent;
                if (deliverMsg.getMsgFmt() == 8) {
                    msgContent = esc0(new String(deliverMsg.getMsgContent(), RETURN_UNICODE_SC));
                } else {
                    msgContent = esc0(new String(deliverMsg.getMsgContent(), RETURN_UNICODE_TEST));
                }
                log.info("AiPai return message===={}", msgContent);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            //发送短信后的回执状态
            try {
                //发送号码
                String srcTerminalId = esc0(deliverMsg.getSrcterminalId());   //发送手机号
                //回执状态
                String stat = esc0(deliverMsg.getStat());  //DELIVRD 成功  其他失败UNDELIV
                //statusMsgId 和 sendMessage类回调的msgId 一致
                long statusMsgId = TypeConvert.byte2long(deliverMsg.getStatusMsgId());
                BsSmsRecord bsSmsRecord = new BsSmsRecord();
                bsSmsRecord.setMsgId(String.valueOf(statusMsgId));
                bsSmsRecord.setSendStat(stat);
                smsService.updateSmsRecordStatByMsgId(bsSmsRecord);
                log.info("return messageId===={}", statusMsgId);
                log.info("return messageStat===={}", stat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void onTerminate() {
        log.error("网关重新连接");
        smProxy.close();
        init();
    }


    public List<byte[]> getLongByte(String message) {
        List<byte[]> list = new ArrayList<byte[]>();
        try {
            //转换为byte[]
            byte[] messageUCS2 = message.getBytes("UnicodeBigUnmarked");
            // 长短信长度
            int messageUCS2Len = messageUCS2.length;
            int maxMessageLen = 140;
            // 长短信发送
            if (messageUCS2Len > maxMessageLen) {
                //int tpUdhi = 1;
                //长消息是1.短消息是0
                //int msgFmt = 0x08;//长消息不能用GBK // 长短信分为多少条发送
                int messageUCS2Count = messageUCS2Len / (maxMessageLen - 6) + 1;
                byte[] tp_udhiHead = new byte[6];
                SecureRandom random = new SecureRandom();
                //随机填充tp_udhiHead[3] 标识这批短信
                random.nextBytes(tp_udhiHead);
                tp_udhiHead[0] = 0x05;
                tp_udhiHead[1] = 0x00;
                tp_udhiHead[2] = 0x03;
//				tp_udhiHead[3] = 0x0A;
                tp_udhiHead[4] = (byte) messageUCS2Count;
                // 默认为第一条
                tp_udhiHead[5] = 0x01;
                for (int i = 0; i < messageUCS2Count; i++) {
                    tp_udhiHead[5] = (byte) (i + 1);
                    byte[] msgContent;
                    // 不为最后一条
                    if (i != messageUCS2Count - 1) {
                        msgContent = byteAdd(tp_udhiHead, messageUCS2, i * (maxMessageLen - 6), (i + 1) * (maxMessageLen - 6));
                        list.add(msgContent);
                    } else {
                        msgContent = byteAdd(tp_udhiHead, messageUCS2, i * (maxMessageLen - 6), messageUCS2Len);
                        list.add(msgContent);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public byte[] byteAdd(byte[] tpUdhiHead, byte[] messageUCS2, int i, int j) {
        byte[] msgb = new byte[j - i + 6];
        System.arraycopy(tpUdhiHead, 0, msgb, 0, 6);
        System.arraycopy(messageUCS2, i, msgb, 6, j - i);
        return msgb;
    }


    public String esc0(String s) {
        if (s == null || s.length() == 0) {
            s = "";
            return s;
        } else {
            int i = s.indexOf('\0');
            if (i > 0) {
                s = s.substring(0, i);
            } else {
                s = s.replace("\0", "");
            }
        }
        return s;
    }

    public static void main(String[] args) {

        String str = "{\"bytes\":\"AQmIgwLsnE30ioWtMTA2OTExNTUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADE\n" +
                "1ODAxMzMzODg1AAAAAAAAAAAAAAE8d7jCAAABMuVNTzowMDkAMjEwNzE1MTQxMjIxMDcxNTE0MTIxNTgwMTMzMzg4NQAAAAAAAAAAAAAB\n" +
                "CYiEAAAAAAAAAAA=\",\"commandId\":5,\"destTerminalId\":\"15801333885\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u\n" +
                "0000\\u0000\\u0000\",\"destnationId\":\"10691155\",\"doneTime\":1626329528671,\"msgFmt\":0,\"msgId\":\"AuycTfSKha0=\",\"m\n" +
                "sgLength\":60,\"registeredDeliver\":1,\"reserve\":\"\",\"sMSCSequence\":17401988,\"sequenceId\":17401987,\"serviceId\"\n" +
                ":\"\",\"srcterminalId\":\"15801333885\",\"stat\":\"MO:009\",\"statusMsgId\":\"d7jCAAABMuU=\",\"submitTime\":1626329528671\n" +
                ",\"tpPid\":0,\"tpUdhi\":0}";


    }

}
