#!/usr/bin/env groovy
def postgresPassword='secret'


def startPostgres(instance, expose, password){
        def output = "docker run --name postgres-$instance -p $expose:5432 -e POSTGRES_PASSWORD=$password -d postgres".execute()
   	println output.errorStream.text
}
startPostgres('secondary', 5433, postgresPassword)
