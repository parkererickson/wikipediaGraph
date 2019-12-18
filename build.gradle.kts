import com.optum.giraffle.tasks.GsqlTask

plugins {
    id("com.optum.giraffle") version "1.3.2-snapshot"
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