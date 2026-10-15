create table `sys_layer_type_fields_bk_1670` like `sys_layer_type_fields`;
insert into `sys_layer_type_fields_bk_1670` select * from `sys_layer_type_fields` where abbreviation = 'xztb';