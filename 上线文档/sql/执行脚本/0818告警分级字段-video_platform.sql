-- ============================================================
-- 业务库 video_platform
-- 告警分级：当月告警月表增加 AI 研判字段（最终上线脚本）
--
-- 执行前请将表名 vm_warning_order_t_202608 替换为实际上线当月表名
--
-- 说明：
--   1) 仅改当月表；下月月表由月底建表任务从当月 DDL 自动复制
--   2) 老月表无需加字段，video-event 配置 ai.judge.column-since-month 后自动兼容
--   3) 腾空间：user_record / org_record / role_id → TEXT（避免 1118）
--      group_id 保持原类型，不动索引
--   4) 本脚本执行一次即可（重复执行 ADD COLUMN 会报错）
--   5) 字段含义：
--      scene_template：来自 vm_ai_judge_scene_rel，上报命中配置时写入
--      scene_code：Dify 回调 sceneCode 映射后写入（smoke/fire/smokeFire；未识别不写）
--      judgment_basis：研判依据（仅此字段，不再使用 disposal_suggestion）
--
-- 若当月表已按旧脚本加过字段但缺少 scene_template，可仅执行：
-- ALTER TABLE `vm_warning_order_t_202608`
--     ADD COLUMN scene_template varchar(64) NULL COMMENT '场景模板(来自配置表)' AFTER scene_code;
--
-- 若当月表已存在废弃字段 disposal_suggestion，执行第三步删除
-- ============================================================

-- ---------- 第一步：腾空间（若已是 text 可跳过本段） ----------
ALTER TABLE `vm_warning_order_t_202608`
    MODIFY COLUMN user_record text NULL,
    MODIFY COLUMN org_record text NULL,
    MODIFY COLUMN role_id text NULL;

-- ---------- 第二步：新增 AI 研判字段 ----------
ALTER TABLE `vm_warning_order_t_202608`
    ADD COLUMN risk_level tinyint NULL COMMENT 'AI风险等级' AFTER vertical_field_angle,
    ADD COLUMN judgment_basis text NULL COMMENT '研判依据(AI风险等级分析)' AFTER risk_level,
    ADD COLUMN scene_code varchar(32) NULL COMMENT '场景编码(Dify回调映射:smoke/fire/smokeFire)' AFTER judgment_basis,
    ADD COLUMN scene_template varchar(64) NULL COMMENT '场景模板(来自配置表)' AFTER scene_code,
    ADD COLUMN ai_judge_status tinyint NULL COMMENT 'AI研判状态:0未研判/1研判中/2研判完成/3研判失败' AFTER scene_template,
    ADD COLUMN agent_extend_field text NULL COMMENT '智能体扩展字段(JSON驼峰)' AFTER ai_judge_status,
    ADD INDEX idx_ai_judge_status (ai_judge_status);

-- ---------- 第三步：删除废弃字段（若列不存在可跳过） ----------
-- ALTER TABLE `vm_warning_order_t_202608` DROP COLUMN disposal_suggestion;
