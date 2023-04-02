package com.example.ittestdemo

import com.example.ittestdemo.db.document.Person
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.isA
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
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

    @BeforeEach
    fun initContext(@Autowired webApplicationContext: WebApplicationContext) {
        webTestClient = MockMvcWebTestClient.bindToApplicationContext(webApplicationContext).build()
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
        webTestClient.get()
            .uri("/api/person/${johnDoeId}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").value(equalTo(johnDoeId))
            .jsonPath("$.firstName").value(equalTo("John"))
            .jsonPath("$.lastName").value(equalTo("Doe"))
            .jsonPath("$.gender").value(equalTo("male"))
    }

    @Test
    fun testGetAllFemales() {
        webTestClient.get()
            .uri { it.path("/api/person").queryParam("gender", "female").build() }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").value(equalTo(1))
            .jsonPath("$[0].id").value(isA(String::class.java))
            .jsonPath("$[0].firstName").value(equalTo("Jane"))
            .jsonPath("$[0].lastName").value(equalTo("Doe"))
            .jsonPath("$[0].gender").value(equalTo("female"))
    }

    data class SavePersonRequestBody(val firstName: String, val lastName: String, val gender: String)
    data class Id(val id: String)
    companion object {

        private lateinit var mongoTemplate: ReactiveMongoTemplate
        private lateinit var johnDoeId: String
        private lateinit var mapper: ObjectMapper

        @JvmStatic
        @BeforeAll
        internal fun setUp() {
            mapper = jacksonObjectMapper()
            mongoTemplate = mongoTemplate()
            johnDoeId = mongoTemplate.save(Person(ObjectId(Date()), "John", "Doe", "male"))
                .block(Duration.ofSeconds(1))?.id?.toHexString() ?: throw RuntimeException()
            mongoTemplate.save(Person(ObjectId(Date()), "Jane", "Doe", "female")).block(Duration.ofSeconds(1))
        }
    }
}
