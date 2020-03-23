package com.javalinist.logic

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database


fun getDb(): Database {
    val db = Database.connect("jdbc:h2:mem:javalinist;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false", driver = "org.h2.Driver")
    return db
}

object users_table : IntIdTable(name = "users") {
    val name = varchar("name", 100).uniqueIndex()
}

class DbUser(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DbUser>(users_table)

    var name by users_table.name
}