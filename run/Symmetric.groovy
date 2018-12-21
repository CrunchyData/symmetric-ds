#!/usr/bin/env groovy

import groovy.sql.Sql
import groovy.json.JsonSlurper

def symmetricHost
def symmetricPort=31415
def postgresUser='postgres'
def postgresPassword='pass'
def symmetricUser='rep'
def symmetricPass='foo'
def symmetricDatabase='sales'

debug=1

curPath = new File('.').getAbsolutePath()

/**
 *
 * @param host
 * @param port
 * @param operation
 * @return an HttpURLconnection
 */
def HttpURLConnection getConnection(String host,  int port, String operation){
    def con =  new URL("http://$host:$port/api/$operation").openConnection() as HttpURLConnection
    con.setRequestProperty( 'User-Agent', 'groovy-2.4.4' )
    con.setRequestProperty( 'Accept', 'application/json' )
    return con
}

/**
 *
 * @param host
 * @param port
 * @return
 */
def getStatus(String host, int port) {
    try {
      def con = getConnection(host, port, 'engine/status')
      if (con.responseCode == 200){
    	return con.inputStream.withCloseable { inStream ->
        	new JsonSlurper().parse( inStream as InputStream )
	}
      }
    } 
    catch(Exception ex){
		if (debug == 1) println ex
    }

    return null

}

/**
 * use the api to synchronize the triggers for this engine
 * @param host
 * @param port
 * @return
 */

def syncTriggers(String host, int port){
    try {
      def con = getConnection(host, port, 'engine/synctriggers')
      con.setRequestMethod('POST')
      return  (con.responseCode == 200)
    } 
    catch(Exception ex){
		if (debug == 1) println ex
    }

    return false
}

def dockerContainers() {
    def process = "curl --unix-socket /var/run/docker.sock http:/containers/json".execute()
    def json = process.inputStream.withCloseable { inStream ->
        new JsonSlurper().parse(inStream as InputStream)
    }
    return json
}

/**
 *
 * @param instance
 * @param curPath
 * @param name
 * @param expose
 */
def startSymmetric(int instance, String curPath, String name, int expose) {
    def output = """docker run -d -p $expose:31415 --name $name -v $curPath/engines$instance:/opt/symmetric-ds/engines 
-v $curPath/conf$instance:/opt/symmetric-ds/conf -v $curPath/security$instance:/opt/symmetric-ds/security jumpmind/symmetricds""".execute()
	println "Error ${output.errorStream.text}"
}

/**
 *
 * @param port
 * @param seconds
 * @param user
 * @param password
 * @return
 */
def waitForPostgres(int port, int seconds, String user, String password){
	while(seconds-- > 0){
		try {
			def sql = Sql.newInstance("jdbc:postgresql://localhost:$port/postgres", user, password)
			if (sql.execute('select 1') != null ) return true
		} catch(Exception ex){
		}
		sleep(1000)
	}
	return false;
}


def startPostgres(String instance, int expose, String password){
    "docker run --name postgres-$instance -p $expose:5432 -e POSTGRES_PASSWORD=$password -d postgres".execute()
	waitForPostgres(expose, 10, 'postgres', 'pass')
}


def writePGPass(String postgresUser, String postgresPassword, int port1, int port2, String symmetricUser, String symmetricPassword){
	def homeDir = System.getProperty("user.home")
	def file = new File("$homeDir/.pgpass")
	file.write("localhost:$port1:*:$postgresUser:$postgresPassword\n")
	file.append("localhost:$port1:*:$postgresUser:$postgresPassword\n")
	file.append("localhost:$port1:*:$symmetricUser:$symmetricPassword\n")
	file.append("localhost:$port2:*:$symmetricUser:$symmetricPassword\n")
	"chmod 0600 $homeDir/.pgpass".execute()
}

def writeEngine(int instance, String ipAddress, int expose, int registrationPort, int postgresPort){
	def file = newFile("engines${instance}/sales2.properties")
	file.write("""
db.connection.properties=
#auto.config.registration.svr.sql.script=sql/primary-2-primary-config.sql
db.password=foo
sync.url=http\\://${ipAddress}\\:${expose}/sync/sales2
group.id=primary
db.init.sql=
registration.url=http\\://${ipAddress}\\:${registrationPort}/sync/sales2
db.driver=org.postgresql.Driver
db.user=rep
engine.name=sales2
external.id=sales2
db.validation.query=select 1
cluster.lock.enabled=false
db.url=jdbc\\:postgresql\\://${ipAddress}:${postgresPort}/sales?protocolVersion\\=3&stringtype\\=unspecified&socketTimeout\\=300&tcpKeepAlive\\=true

""")
}
/**
 *
 * @param port
 * @param adminUser
 * @param adminPassword
 * @param user
 * @param password
 * @return
 */
def createUser(int port, String adminUser, String adminPassword, String user, String password){
        def sql = Sql.newInstance("jdbc:postgresql://localhost:$port/postgres", adminUser, adminPassword)
        sql.execute("create user $user with password '$password'".toString())
}

/**
 *
 * @param port
 * @param adminUser
 * @param adminPassword
 * @param dbname
 * @param owner
 * @return
 */
def createDatabase(int port, String adminUser, String adminPassword, String dbname, String owner) {
        def sql = Sql.newInstance("jdbc:postgresql://localhost:$port/postgres", adminUser, adminPassword)
	sql.execute("create database $dbname owner $owner".toString())
}

/**
 *
 * @param port
 * @param dbname
 * @param user
 * @param pass
 * @return
 */
def createPrimaryTables(int port, String dbname, user, pass){
        def sql = Sql.newInstance("jdbc:postgresql://localhost:$port/$dbname", user, pass)
	sql.execute("create table item (id serial primary key, description text, price numeric(8,2))")
	sql.execute("create table sale (id serial primary key, item_id int4 references item(id), price numeric(8,2))")
}

/**
 *
 * @param port
 * @param dbname
 * @param user
 * @param pass
 */
def createPrimarySymTables(int port, String dbname, String user, String pass) {
	def sqlString = [
		"insert into sym_node_group_link (source_node_group_id,target_node_group_id,data_event_action) values ('primary','primary','P')",
		"""insert into sym_router (router_id,source_node_group_id,target_node_group_id,router_type,router_expression,sync_on_update,sync_on_insert,sync_on_delete,use_source_catalog_schema,create_time,last_update_by,last_update_time) values ('primary_2_primary','primary','primary','default',null,1,1,1,0,current_timestamp,'console',current_timestamp)""",
		"""insert into sym_parameter (external_id, node_group_id, param_key, param_value, create_time, last_update_by, last_update_time) values ('ALL','ALL','push.thread.per.server.count','10',current_timestamp,'console',current_timestamp)""",
		"insert into sym_parameter (external_id, node_group_id, param_key, param_value,create_time) values ('ALL', 'ALL', 'job.pull.period.time.ms', 2000, now())",
		"insert into sym_parameter (external_id, node_group_id, param_key, param_value,create_time) values ('ALL', 'ALL','job.push.period.time.ms', 2000, now())",
		"insert into sym_parameter (external_id, node_group_id, param_key, param_value,create_time) values ('ALL', 'ALL','initial.load.create.first', 1, now())"
	]
    def sql = Sql.newInstance("jdbc:postgresql://localhost:$port/$dbname", user, pass)
	for (str in sqlString){
		sql.execute(str)
	}
}

/**
 *
 * @param port
 * @param dbname
 * @param user
 * @param pass
 */
def createPrimaryTriggers(int port, String dbname, String user, String pass){
	def triggerSql = [
	"insert into sym_trigger (trigger_id, source_schema_name, source_table_name, channel_id, sync_on_update, sync_on_insert, sync_on_delete, sync_on_update_condition, sync_on_insert_condition, sync_on_delete_condition, last_update_time, create_time) values ('public.item', 'public', 'item', 'default', 1, 1, 1, '1=1', '1=1', '1=1', now(), now())",
	"insert into sym_trigger (trigger_id, source_schema_name, source_table_name, channel_id, sync_on_update, sync_on_insert, sync_on_delete, sync_on_update_condition, sync_on_insert_condition, sync_on_delete_condition, last_update_time, create_time) values ('public.sale', 'public', 'sale', 'default', 1, 1, 1, '1=1', '1=1', '1=1', now(), now())",
	"insert into sym_trigger_router (trigger_id, router_id, enabled, initial_load_order, create_time, last_update_time) values ('public.item', 'primary_2_primary', 1, 10, now(), now())",
	"insert into sym_trigger_router (trigger_id, router_id, enabled, initial_load_order, create_time, last_update_time) values ('public.sale', 'primary_2_primary', 1, 20, now(), now())"
	]
	def sql = Sql.newInstance("jdbc:postgresql://localhost:$port/$dbname", user, pass)
    for (str in triggerSql){
            sql.execute(str)
    }
}

// TODO USE RIGHT INSTANCE
def openRegistration() {
	output = "docker exec sym1 /opt/symmetric-ds/bin/symadmin -e sales2 open-registration primary sales2".execute().text
}


def String findDockerGateway(){
	process = "docker inspect network bridge".execute()
	def json = process.inputStream.withCloseable { inStream ->
		new JsonSlurper().parse(inStream as InputStream)
	}
	return json[0].IPAM.Config[0].Gateway

}


startPostgres('primary', 5432, postgresPassword)
println "primary started"
startPostgres('secondary', 5433, postgresPassword)
println "secondary started"

symmetricHost = findDockerGateway()

writeEngine(1,symmetricHost, 31415, 31415, 5432 )
writeEngine(2, symmetricHost, 31416, 31415, 5433)

createUser(5432, postgresUser, postgresPassword, symmetricUser, symmetricPass)
createDatabase(5432, postgresUser, postgresPassword, symmetricDatabase, symmetricUser)

createUser(5433, postgresUser, postgresPassword, symmetricUser, symmetricPass)
createDatabase(5433, postgresUser, postgresPassword, symmetricDatabase, symmetricUser)

println dockerContainers()
startSymmetric(1, curPath, 'sym1', 31415)


while(getStatus(symmetricHost, symmetricPort)?.started != true) {
	println "waiting for sym1"
	sleep(1000)
	if (true) {
		println "docker logs sym1".execute().text
	}

}

println "sym1 started"

createPrimaryTables(5432, symmetricDatabase, symmetricUser, symmetricPass);
createPrimarySymTables(5432, symmetricDatabase, symmetricUser, symmetricPass)

createPrimaryTriggers(5432, symmetricDatabase, symmetricUser, symmetricPass)
syncTriggers(symmetricHost, symmetricPort)
openRegistration()
// TODO: need to reload primary node to refresh cache
startSymmetric(2, curPath, 'sym2', 31416)

while(getStatus(symmetricHost, 31416)?.started != true){
	println "waiting for sym2"
    sleep(1000)
}

java.net.Inet4Address.getAllByName('ubuntu')
println "sym2 started"


