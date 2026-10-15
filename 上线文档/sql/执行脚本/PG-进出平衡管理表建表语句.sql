-- 进出平衡的管理表的建表语句
DROP TABLE IF EXISTS t_tmp_jcphmanage;

CREATE TABLE IF NOT EXISTS t_tmp_jcphmanage
(
	guid character varying(200) COLLATE pg_catalog."default" NOT NULL,	    -- 耕地的进出平衡计算结果的GUID
	preLayerName character varying(200) COLLATE pg_catalog.default,			-- 之前的图层表名
	curLayerName character varying(200) COLLATE pg_catalog.default,			-- 当前的图层表名
	g1 character varying(200) COLLATE pg_catalog.default,					-- 之前的耕地图斑表
	f1 character varying(200) COLLATE pg_catalog.default,					-- 之前的非耕农用地图斑表
	g2 character varying(200) COLLATE pg_catalog.default,					-- 当前的耕地图斑表
	f2 character varying(200) COLLATE pg_catalog.default,					-- 当前的非耕农用地图斑表
	gdlc character varying(200) COLLATE pg_catalog.default,					-- 耕地流出图斑表
	gdlr character varying(200) COLLATE pg_catalog.default,					-- 耕地流入图斑表
	lc0101 double precision,												-- 耕地流出面积中水田的面积
	lc0102 double precision,												-- 耕地流出面积中水浇地的面积
	lc0103 double precision,												-- 耕地流出面积中旱地的面积
	lr0101 double precision,												-- 耕地流入面积中水田的面积
	lr0102 double precision,												-- 耕地流入面积中水浇地的面积
	lr0103 double precision,												-- 耕地流入面积中旱地的面积
	creator character varying(200) COLLATE pg_catalog."default",			-- 创建人
	create_time timestamp with time zone,									-- 创建时间
	update_time timestamp with time zone,									-- 更新时间
	delete_time timestamp with time zone,									-- 删除时间
							  delete_flag character varying(1) COLLATE pg_catalog.default,						-- 删除状态
	tenant_id character varying(50) COLLATE pg_catalog."default",			-- 租户id
	industry_code character varying(50) COLLATE pg_catalog."default",		-- 行业编码
	app_ver_code character varying(50) COLLATE pg_catalog."default",		-- 行业版本编码
	CONSTRAINT t_tmp_jcphmanage_pkey PRIMARY KEY (guid)
	);
