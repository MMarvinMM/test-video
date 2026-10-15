-- **基础库 video_platform_base
-- 删除遥感时间轴表新增停留时间
ALTER TABLE `vm_timeline_detail` DROP COLUMN `stay_time`;

-- 停留时间字段默认值
update vm_timeline_detail set stay_time = NULL where delete_flag = 0 and stay_time is null;

-- **业务库 video_platform
-- 修改字段类型回退
ALTER TABLE vm_warning_order_collection_t MODIFY COLUMN `process_name` text COMMENT '处理人姓名';

