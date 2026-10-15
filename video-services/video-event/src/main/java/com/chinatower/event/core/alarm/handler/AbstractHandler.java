package com.chinatower.event.core.alarm.handler;


import com.chinatower.event.core.alarm.AlarmStrategy;
import com.chinatower.event.dto.ReceiveAlarmDTO;

/**
 * @Description 核心处理逻辑抽象类
 * @Author guyuefei
 * @Date 2022/7/12
 * @Version release_v1.5
 **/
public abstract class AbstractHandler {

    /**
     * 定义下一个执行器
     */
    private AbstractHandler next;

    private AlarmStrategy alarmStrategy;

    /**
     * 赋值下一个执行器
     * @param next
     */
    public void setNext(AbstractHandler next) {
        this.next = next;
    }

    /**
     * 获取下一个执行器
     * @return
     */
    public AbstractHandler getNext() {
        return next;
    }

    /**
     * 执行逻辑
     * @param receiveAlarmDTO
     * @return
     */
    public abstract boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception;

    /**
     * 执行下一个处理器逻辑
     * @param receiveAlarmDTO
     * @return
     */
    protected boolean excuteNextHandler(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        if(this.getNext() != null){
            return this.getNext().excute(receiveAlarmDTO);
        }
        return true;
    }

    /**
     * 定义执行器的名称
     * @param
     * @return
     */
    public String getHandlerName(){
        return this.getClass().getName();
    }


    /**
     * 赋值下一个执行器
     * @param alarmStrategy
     */
    public AbstractHandler setAlarmStrategy(AlarmStrategy alarmStrategy) {
        this.alarmStrategy = alarmStrategy;
        return this;
    }

    /**
     * 获取下一个执行器
     * @return
     */
    public AlarmStrategy getAlarmStrategy() {
        return alarmStrategy;
    }
    

}
