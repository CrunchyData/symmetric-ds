insert into sym_trigger (trigger_id, source_schema_name, source_table_name, channel_id, sync_on_update, sync_on_insert, sync_on_delete, sync_on_update_condition, sync_on_insert_condition, sync_on_delete_condition, last_update_time, create_time) values ('public.item', 'public', 'item', 'default', 1, 1, 1, '1=1', '1=1', '1=1', now(), now());

insert into sym_trigger (trigger_id, source_schema_name, source_table_name, channel_id, sync_on_update, sync_on_insert, sync_on_delete, sync_on_update_condition, sync_on_insert_condition, sync_on_delete_condition, last_update_time, create_time) values ('public.sale', 'public', 'sale', 'default', 1, 1, 1, '1=1', '1=1', '1=1', now(), now());



insert into sym_trigger_router (trigger_id, router_id, enabled, initial_load_order, create_time, last_update_time) values ('public.item', 'primary_2_primary', 1, 10, now(), now());

insert into sym_trigger_router (trigger_id, router_id, enabled, initial_load_order, create_time, last_update_time) values ('public.sale', 'primary_2_primary', 1, 20, now(), now()); 

