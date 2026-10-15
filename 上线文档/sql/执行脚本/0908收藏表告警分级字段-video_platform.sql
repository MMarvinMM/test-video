-- ============================================================
-- 业务库 video_platform
-- 我的收藏表增加告警分级字段（与告警月表口径一致）
--
-- 用途：收藏列表返回 AI 风险等级/场景类型，支持场景筛选与 AI 排序
-- 说明：重复执行 ADD COLUMN 会报错，按环境是否已有列决定是否执行
-- ============================================================

ALTER TABLE `vm_warning_order_collection_t`
    ADD COLUMN risk_level tinyint NULL COMMENT 'AI风险等级' AFTER judge_result,
    ADD COLUMN judgment_basis text NULL COMMENT '研判依据(AI风险等级分析)' AFTER risk_level,
    ADD COLUMN scene_code varchar(32) NULL COMMENT '场景编码(Dify回调映射:smoke/fire/smokeFire)' AFTER judgment_basis,
    ADD COLUMN scene_template varchar(64) NULL COMMENT '场景模板(来自配置表)' AFTER scene_code,
    ADD COLUMN ai_judge_status tinyint NULL COMMENT 'AI研判状态:0未研判/1研判中/2研判完成/3研判失败' AFTER scene_template,
    ADD COLUMN agent_extend_field text NULL COMMENT '智能体扩展字段(JSON驼峰)' AFTER ai_judge_status;
