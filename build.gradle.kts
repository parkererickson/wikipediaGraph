import com.optum.giraffle.tasks.GsqlTask

plugins {
    id("com.optum.giraffle") version "1.3.2"
    id("net.saliman.properties") version "1.5.1"
}

repositories {
    jcenter()
}

val gsqlGraphname: String by project // <1>
val gsqlHost: String by project
val gsqlUserName: String by project
val gsqlPassword: String by project
val gsqlAdminUserName: String by project
val gsqlAdminPassword: String by project
val gsqlCaCert: String by project
val tokenMap: LinkedHashMap<String, String> =
    linkedMapOf("graphname" to gsqlGraphname) // <2>

val grpSchema: String = "Tigergraph Schema"
val grpQueries: String = "Tigergraph Queries"
val grpLoad: String = "Tigergraph Loading"

tigergraph { // <3>
    scriptDir.set(file("db_scripts"))
    tokens.set(tokenMap)
    serverName.set(gsqlHost)
    userName.set(gsqlUserName)
    password.set(gsqlPassword)
    adminUserName.set(gsqlAdminUserName)
    adminPassword.set(gsqlAdminPassword)
    caCert.set(gsqlCaCert)
}

tasks {
    val createSchema by registering(GsqlTask::class) {
        group = grpSchema
        description = "Create the schema on the database"
        dependsOn("dropAll")
        scriptPath = "createSchema.gsql"
        superUser = true 
    }
    val dropAll by registering(GsqlTask::class) {
        group = grpSchema
        description = "Drops the schema on the database"
        scriptPath = "drop.gsql"
        superUser = true
    }
    val createArticleTuple by registering(GsqlTask::class){
        group = grpQueries
        description = "Creates the articleTuple query"
        scriptPath = "getArticleTuples.gsql"
        superUser = true
    }
    val installArticleTuple by registering(GsqlTask::class){
        group = grpQueries
        description = "Installs the articleTuple query"
        dependsOn("createArticleTuple")
        scriptPath = "installArticleTuple.gsql"
        superUser = true
    }
    val createGetArticles by registering(GsqlTask::class){
        group = grpQueries
        description = "Creates the getArticles query"
        scriptPath = "getArticles.gsql"
        superUser = true
    }
    val installGetArticles by registering(GsqlTask::class){
        group = grpQueries
        description = "Installs the getArticles query"
        dependsOn("createGetArticles")
        scriptPath = "installGetArticles.gsql"
        superUser = true
    }
    val createGetKeywords by registering(GsqlTask::class){
        group = grpQueries
        description = "Creates the getKeywords query"
        scriptPath = "getKeywords.gsql"
        superUser = true
    }
    val installGetKeywords by registering(GsqlTask::class){
        group = grpQueries
        description = "Installs the getKeywords query"
        dependsOn("createGetKeywords")
        scriptPath = "installGetKeywords.gsql"
        superUser = true
    }
    val createLoadArticles by registering(GsqlTask::class){
        group = grpLoad
        description = "Creates the loading job loadArticles"
        scriptPath = "createLoadArticles.gsql"
        superUser = true
    }
    val loadArticles by registering(GsqlTask::class){
        group = grpLoad
        description = "Runs the loading job loadArticles"
        scriptPath = "loadArticles.gsql"
        superUser = true
    }
    val createLoadKeywords by registering(GsqlTask::class){
        group = grpLoad
        description = "Creates the loading job loadKeywords"
        scriptPath = "createLoadKeywords.gsql"
        superUser = true
    }
    val loadKeywords by registering(GsqlTask::class){
        group = grpLoad
        description = "Runs the loading job loadArticles"
        scriptPath = "loadKeywords.gsql"
        superUser = true
    }
    val createAllEdgeTuples by registering(GsqlTask::class){
        group = grpQueries
        description = "Creates the allEdgeTuples query"
        scriptPath = "allEdgeTuples.gsql"
        superUser = true
    }
    val installAllEdgeTuples by registering(GsqlTask::class){
        group = grpQueries
        description = "Installs the allEdgeTuples query"
        scriptPath = "installAllEdgeTuples.gsql"
        superUser = true
    }
    val createEverything by registering(GsqlTask::class){
        group = grpLoad
        description = "Creates schema, loads data, and installs all queries"
        dependsOn("createSchema")
        dependsOn("createLoadArticles")
        dependsOn("createLoadKeywords")
        dependsOn("loadArticles")
        dependsOn("loadKeywords")
        dependsOn("installArticleTuple")
        dependsOn("installGetKeywords")
        dependsOn("installGetArticles")
    }
}