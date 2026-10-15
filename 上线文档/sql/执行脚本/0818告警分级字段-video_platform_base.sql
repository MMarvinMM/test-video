-- ============================================================
-- 基础库 video_platform_base
-- 告警分级：行业-算法-场景模板关系表
--
-- 说明：存在有效配置（delete_flag=0）的告警上报后进入 AI 未研判队列
-- 幂等：CREATE TABLE IF NOT EXISTS
--
-- 若环境已按旧脚本建表（字段名为 scene_code），可改执行：
-- ALTER TABLE vm_ai_judge_scene_rel
--     CHANGE COLUMN scene_code scene_template varchar(64) NOT NULL COMMENT '场景模板';
-- ============================================================

CREATE TABLE IF NOT EXISTS vm_ai_judge_scene_rel (
    id              bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    industry_code   varchar(64)  NOT NULL COMMENT '行业编码',
    algorithm_code  varchar(64)  NOT NULL COMMENT '算法编码',
    scene_template  varchar(64)  NOT NULL COMMENT '场景模板',
    delete_flag     tinyint      NOT NULL DEFAULT 0 COMMENT '删除标识:0正常 1删除',
    create_time     datetime     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     datetime     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_industry_algorithm (industry_code, algorithm_code)
) COMMENT='告警分级行业算法场景关系表';

-- 场景关系初始化示例（按实际行业/算法/场景模板替换后执行）
-- INSERT INTO vm_ai_judge_scene_rel (industry_code, algorithm_code, scene_template, delete_flag)
-- VALUES ('INDUSTRY_CODE', 'ALGORITHM_CODE', 'SCENE_TEMPLATE', 0);
