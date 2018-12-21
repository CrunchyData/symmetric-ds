#!/usr/bin/env groovy
def writePGPass(postgresUser, postgresPassword,port1, port2, symmetricUser, symmetricPassword){
        def homeDir = System.getProperty("user.home")
        def file = new File("$homeDir/.pgpass")
        file.write("localhost:$port1:*:$postgresUser:$postgresPassword\n")
        file.append("localhost:$port1:*:$postgresUser:$postgresPassword\n")
        file.append("localhost:$port1:*:$symmetricUser:$symmetricPassword\n")
        file.append("localhost:$port2:*:$symmetricUser:$symmetricPassword\n")
        "chmod 0600 $homeDir/.pgpass".execute()
}
writePGPass('postgres', 'mysecretpassword', 5432, 5433, 'rep', 'foo')

