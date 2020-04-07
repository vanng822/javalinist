package com.javalinist.integration

import com.javalinist.JavalinistApplication
import com.javalinist.logic.DB
import com.javalinist.logic.DB_CONNECTION_INFO
import com.javalinist.logic.users_table
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*


open class BaseTest {

    private lateinit var app: JavalinistApplication
    protected var port = 5001

    @BeforeAll
    fun setUp() {
        DB_CONNECTION_INFO = "jdbc:h2:mem:javalinist_integrationtest"
        app = JavalinistApplication()
        app.start(port)
    }

    @AfterAll
    fun tearDown() {
        app.stop()
        DB.run {
            transaction {
                SchemaUtils.drop(users_table)
            }
        }
    }
}