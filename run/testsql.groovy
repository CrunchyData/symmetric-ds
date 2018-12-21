#!/usr/bin/env groovy
import groovy.sql.Sql

def postgresUser='postgres'
def postgresPassword='mysecretpassword'
def primaryPort=5432

def createUser(port, adminUser, adminPassword, user, password){
        def sql = Sql.newInstance("jdbc:postgresql://localhost:$port/postgres", adminUser, adminPassword)
        sql.execute("create user rep with password 'foo'")
}
def createPrimarySymTables(port, dbname, user, pass) {
        def sqlString = [
                "insert into sym_node_group_link (source_node_group_id,target_node_group_id,data_event_action) values ('primary','primary','P')"
                "insert into sym_router (router_id,source_node_group_id,target_node_group_id,router_type,router_expression,sync_on_update,sync_on_insert,sync_on_delete,use_source_catalog_schema,create_time,last_update_by,last_update_time) values ('primary_2_primary','primary','primary','default',null,1,1,1,0,current_timestamp,'console',current_timestamp)"
                "insert into sym_parameter (external_id, node_group_id, param_key, param_value, create_time, last_update_by, last_update_time) values ('ALL','ALL','push.thread.per.server.count','10',current_timestamp,'console',current_timestamp)"
                "insert into sym_parameter (external_id, node_group_id, param_key, param_value,create_time) values ('ALL', 'ALL', 'job.pull.period.time.ms', 2000, now())"
                "insert into sym_parameter (external_id, node_group_id, param_key, param_value,create_time) values ('ALL', 'ALL','job.push.period.time.ms', 2000, now())"
                "insert into sym_parameter (external_id, node_group_id, param_key, param_value,create_time) values ('ALL', 'ALL','initial.load.create.first', 1, now())"
        ]
        def sql = Sql.newInstance("jdbc:postgresql://localhost:$port/$dbname", user, pass)
        for (str in sqlString){
                sql.execute(str)
        }

}
createPrimarySymTables(5432, 'sales', 'rep','foo');
createUser(primaryPort, postgresUser, postgresPassword,'rep','foo')
