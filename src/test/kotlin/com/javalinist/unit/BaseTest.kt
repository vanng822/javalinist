package com.javalinist.unit

import com.javalinist.logic.DB
import com.javalinist.logic.DB_CONNECTION_INFO
import com.javalinist.logic.users_table
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

interface BaseTest {
    @BeforeEach
    fun setUpEach() {
        DB_CONNECTION_INFO = "jdbc:h2:mem:javalinist_unittest"
        DB.run {
            transaction {
                SchemaUtils.create(users_table)
            }
        }
    }

    @AfterEach
    fun tearDownEach() {
        DB.run {
            transaction {
                SchemaUtils.drop(users_table)
            }
        }
    }
}