package com.javalinist.logic

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database

var DB_CONNECTION_INFO: String = "jdbc:h2:mem:javalinist"

var DB_OPTIONS: Set<String> = setOf<String>(
    "DB_CLOSE_DELAY=-1",
    "DATABASE_TO_UPPER=false"
)

val DB: Database by lazy {
    val urlParts = setOf<String>(DB_CONNECTION_INFO) + DB_OPTIONS
    Database.connect(urlParts.joinToString(";"))
}

object users_table : IntIdTable(name = "users") {
    val name = varchar("name", 100).uniqueIndex()
}

class DbUser(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DbUser>(users_table)

    var name by users_table.name
}