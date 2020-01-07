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
    val createTrainAnswerTuples by registering(GsqlTask::class){
        group = grpQueries
        description = "Creates the trainAnswerTuples query"
        scriptPath = "trainAnswerTuples.gsql"
        superUser = true
    }
    val installTrainAnswerTuples by registering(GsqlTask::class){
        group = grpQueries
        description = "Installs the trainAnswerTuples query"
        scriptPath = "installTrainAnswerTuples.gsql"
        superUser = true
    }
    val createTrainQuestionTuples by registering(GsqlTask::class){
        group = grpQueries
        description = "Creates the trainQuestionTuples query"
        scriptPath = "trainQuestionTuples.gsql"
        superUser = true
    }
    val installTrainQuestionTuples by registering(GsqlTask::class){
        group = grpQueries
        description = "Installs the trainQuestionTuples query"
        scriptPath = "installTrainQuestionTuples.gsql"
        superUser = true
    }
    val createLoadArticleKeywords by registering(GsqlTask::class){
        group = grpLoad
        description = "Creates the loading job loadArticleKeywords"
        scriptPath = "createLoadArticleKeywords.gsql"
        superUser = true
    }
    val loadArticleKeywords by registering(GsqlTask::class){
        group = grpLoad
        description = "Runs the loading job loadArticleKeywords"
        scriptPath = "loadArticleKeywords.gsql"
        superUser = true
    }
    val createLoadQuestionKeywords by registering(GsqlTask::class){
        group = grpLoad
        description = "Creates the loading job loadQuestionKeywords"
        scriptPath = "createLoadQuestionKeywords.gsql"
        superUser = true
    }
    val loadQuestionKeywords by registering(GsqlTask::class){
        group = grpLoad
        description = "Runs the loading job loadQuestionKeywords"
        scriptPath = "loadQuestionKeywords.gsql"
        superUser = true
    }
    val createLoadQuestions by registering(GsqlTask::class){
        group = grpLoad
        description = "Creates the loading job loadQuestionKeywords"
        scriptPath = "createLoadQuestions.gsql"
        superUser = true
    }
    val loadQuestions by registering(GsqlTask::class){
        group = grpLoad
        description = "Runs the loading job loadQuestionKeywords"
        scriptPath = "loadQuestions.gsql"
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
}