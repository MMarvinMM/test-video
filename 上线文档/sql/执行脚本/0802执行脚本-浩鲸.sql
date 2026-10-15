# mysql 基础库

# 删除重复数据 
delete a.* from vm_sensor_monitor_status a, 
(select device_code, monitor_code, min(id) id from vm_sensor_monitor_status group by device_code, monitor_code having count(*) > 1) b
where a.device_code  = b.device_code and a.monitor_code = b.monitor_code and a.id != b.id;

# 增加唯一索引
ALTER TABLE vm_sensor_monitor_status ADD UNIQUE `uniq_device_monitor_code` (device_code, monitor_code);