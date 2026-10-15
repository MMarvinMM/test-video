# 基础库
CREATE TABLE `sys_tenant_map_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `map_type` varchar(50) NOT NULL COMMENT 'vector_2D：常规地图、satellite_2D：卫星地图、satellite_3D：三维地形图、heightmap_3D：二维地形图',
  `max_zoom` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '最大层级',
  `min_zoom` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '最小层级',
  `map_url` varchar(1024) NOT NULL COMMENT '大屏链接地址',
  `sort` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '大屏排序',
  `tenant_id` varchar(128) DEFAULT NULL COMMENT '客户编码',
  `industry_code` varchar(50) NOT NULL COMMENT '行业编码',
  `app_ver_code` varchar(50) NOT NULL DEFAULT '' COMMENT '应用版本编码',
  `accept_node` varchar(255) NOT NULL COMMENT '受理节点',
  `creator` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `modifier` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人',
  `delete_flag` tinyint NOT NULL DEFAULT '0' COMMENT '删除表示 0-未删除 1-已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_industry_id` (`tenant_id`,`industry_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='客户地图配置表';