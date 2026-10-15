package com.chinatower.receive.aijudge;

/**
 * 外部 AI 告警分级研判响应结果
 */
public class AiJudgeResult {

    /** 是否研判成功 */
    private boolean success;

    /** 告警工单号 */
    private String alarmId;

    /** 风险等级（独立字段） */
    private String riskLevel;

    /** 场景编码（独立字段） */
    private String sceneCode;

    /** 智能体扩展字段 JSON（驼峰） */
    private String agentExtendField;

    /** 失败错误码 */
    private String errorCode;

    /** 失败错误信息 */
    private String errorMsg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getSceneCode() {
        return sceneCode;
    }

    public void setSceneCode(String sceneCode) {
        this.sceneCode = sceneCode;
    }

    public String getAgentExtendField() {
        return agentExtendField;
    }

    public void setAgentExtendField(String agentExtendField) {
        this.agentExtendField = agentExtendField;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
