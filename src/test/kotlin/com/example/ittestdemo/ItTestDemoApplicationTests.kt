package com.example.ittestdemo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.client.MockMvcWebTestClient
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
class ItTestDemoApplicationTests : IntegrationTestContext() {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext
    private lateinit var webTestClient: WebTestClient
    private lateinit var mapper: ObjectMapper

    @BeforeEach
    fun initContext() {
        webTestClient = MockMvcWebTestClient.bindToApplicationContext(webApplicationContext).build()
        mapper  = jacksonObjectMapper()
    }

    @Test
    fun test() {
        val savePersonResponseBody = webTestClient.post()
            .uri("/api/person")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Person(firstName = "Zoltan", lastName = "Farkas", gender = "male"))
            .exchange()
            .expectStatus().isOk
            .expectBody().jsonPath("$.id").exists()
            .returnResult()
            .responseBodyContent
        val id = mapper.readValue(savePersonResponseBody, Id::class.java)

        webTestClient.get()
            .uri("/api/person/${id.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").value(equalTo(id.id))
            .jsonPath("$.firstName").value(equalTo("Zoltan"))
            .jsonPath("$.lastName").value(equalTo("Farkas"))
            .jsonPath("$.gender").value(equalTo("male"))
    }

    data class Person(val id: String = "", val firstName: String, val lastName: String, val gender: String)
    data class Id(val id: String)
}
