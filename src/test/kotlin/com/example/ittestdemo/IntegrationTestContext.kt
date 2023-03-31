package com.example.ittestdemo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
open class IntegrationTestContext {

    companion object {
        @Container
        private val container: MongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))

        @DynamicPropertySource
        @JvmStatic
        fun mongoDbProperties(registry: DynamicPropertyRegistry) {
            registry.add("MONGO_DB_CONNECTION_STRING") { container.connectionString + "/admin" }
        }
    }

}