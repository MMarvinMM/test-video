DROP TABLE IF EXISTS vm_ar_tags_info;

RENAME TABLE vm_ar_tags_info_tmp240726 TO vm_ar_tags_info;

DROP TABLE IF EXISTS sys_role_screen_ref;

RENAME TABLE sys_role_screen_ref_tmp240726 TO sys_role_screen_ref;

DROP TABLE IF EXISTS bm_inspection_conf;

RENAME TABLE bm_inspection_conf_tmp240726 TO bm_inspection_conf;

DROP TABLE IF EXISTS bm_inspection_task;

RENAME TABLE bm_inspection_task_tmp240726 TO bm_inspection_task;

-- 回退sys_menu表
DROP TABLE sys_menu;
RENAME sys_menu_0726 TO sys_menu;

-- 回退sys_industry_auth表
DROP TABLE sys_industry_auth;
RENAME sys_industry_auth_0726 TO sys_industry_auth;