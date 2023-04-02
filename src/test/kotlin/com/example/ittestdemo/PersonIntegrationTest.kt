package com.example.ittestdemo

import com.example.ittestdemo.db.document.Person
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.remove
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.client.MockMvcWebTestClient
import org.springframework.web.context.WebApplicationContext
import reactor.test.StepVerifier
import java.time.Duration
import java.util.*

@SpringBootTest
class PersonIntegrationTest : IntegrationTestContext() {

    private lateinit var webTestClient: WebTestClient
    private lateinit var mongoTemplate: ReactiveMongoTemplate
    private lateinit var mapper: ObjectMapper

    @BeforeEach
    fun initContext(@Autowired webApplicationContext: WebApplicationContext) {
        webTestClient = MockMvcWebTestClient.bindToApplicationContext(webApplicationContext).build()
        mapper = jacksonObjectMapper()
        mongoTemplate = mongoTemplate()
    }

    @AfterEach
    fun cleanUp() {
        val deletedCount = mongoTemplate.remove<Person>().all().block(Duration.ofSeconds(1))?.deletedCount
        println("Deleted $deletedCount documents from the DB.")
    }

    @Test
    fun testSavePerson() {
        val savePersonResponseBody = webTestClient.post()
            .uri("/api/person")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(SavePersonRequestBody(firstName = "Zoltan", lastName = "Farkas", gender = "male"))
            .exchange()
            .expectStatus().isOk
            .expectBody().jsonPath("$.id").exists()
            .returnResult()
            .responseBodyContent
        val id = mapper.readValue(savePersonResponseBody, Id::class.java)

        StepVerifier.create(mongoTemplate.findById(id.id, Person::class.java))
            .assertNext { person ->
                run {
                    assertThat(person.id.toHexString()).isInstanceOf(String::class.java)
                    assertThat(person.firstName).isEqualTo("Zoltan")
                    assertThat(person.lastName).isEqualTo("Farkas")
                    assertThat(person.gender).isEqualTo("male")
                }
            }
            .verifyComplete()
    }

    @Test
    fun testGetPerson() {
        val id = savePersonToDb("John", "Doe", "male")

        webTestClient.get()
            .uri("/api/person/$id")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").value(equalTo(id))
            .jsonPath("$.firstName").value(equalTo("John"))
            .jsonPath("$.lastName").value(equalTo("Doe"))
            .jsonPath("$.gender").value(equalTo("male"))
    }

    @Test
    fun testGetAllFemales() {
        savePersonToDb("John", "Doe", "male")
        val id = savePersonToDb("Jane", "Doe", "female")

        webTestClient.get()
            .uri { it.path("/api/person").queryParam("gender", "female").build() }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").value(equalTo(1))
            .jsonPath("$[0].id").value(equalTo(id))
            .jsonPath("$[0].firstName").value(equalTo("Jane"))
            .jsonPath("$[0].lastName").value(equalTo("Doe"))
            .jsonPath("$[0].gender").value(equalTo("female"))
    }

    private fun savePersonToDb(firstName: String, lastName: String, gender: String) =
        mongoTemplate.save(Person(ObjectId(Date()), firstName, lastName, gender))
            .block(Duration.ofSeconds(1))?.id?.toHexString() ?: throw RuntimeException()

    data class SavePersonRequestBody(val firstName: String, val lastName: String, val gender: String)
    data class Id(val id: String)
}
