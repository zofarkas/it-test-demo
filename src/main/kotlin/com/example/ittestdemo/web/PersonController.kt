package com.example.ittestdemo.web

import com.example.ittestdemo.db.document.Person
import com.example.ittestdemo.service.PersonService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/person")
class PersonController(val personService: PersonService) {

    @GetMapping
    fun getAll(@RequestParam(value = "gender", required = false) gender: String?): Flux<Person> {
        return gender?.let { personService.getByGender(it) } ?: personService.getAll()
    }

    @PostMapping
    fun add(@RequestBody person: Person): Mono<Id> {
        return personService.add(person).map { Id(it) }
    }

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: String): Mono<ResponseEntity<Any>> {
        return personService.get(id)
            .map { ResponseEntity.ok<Any>(it) }
            .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
    }
}