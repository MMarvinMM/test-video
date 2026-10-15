-- **基础库 video_platform_base
-- 遥感时间轴新增停留时间
ALTER TABLE vm_timeline_detail ADD stay_time varchar(5) NULL COMMENT '停留时间';

-- 遥感时间轴批量修改默认停留时间字段
update vm_timeline_detail set stay_time = '3' where delete_flag = 0 and stay_time is null;

-- **业务库 video_platform
-- 修改字段类型
ALTER TABLE vm_warning_order_collection_t MODIFY COLUMN `process_name` longtext COMMENT '处理人姓名';