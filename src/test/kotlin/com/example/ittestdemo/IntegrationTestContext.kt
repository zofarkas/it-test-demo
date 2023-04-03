package com.example.ittestdemo

import com.mongodb.ConnectionString
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.BindMode
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
            registry.add("MONGO_DB_CONNECTION_STRING") { container.connectionString + "/personDB?authSource=admin" }
            container.withClasspathResourceMapping(
                "mongo-init.js",
                "/docker-entrypoint-initdb.d/mongo-init.js",
                BindMode.READ_ONLY
            )
        }

        fun mongoTemplate(): ReactiveMongoTemplate {
            val mongoClientDatabaseFactory =
                SimpleReactiveMongoDatabaseFactory(ConnectionString(container.connectionString + "/personDB?authSource=admin"))
            return ReactiveMongoTemplate(mongoClientDatabaseFactory)
        }
    }
}