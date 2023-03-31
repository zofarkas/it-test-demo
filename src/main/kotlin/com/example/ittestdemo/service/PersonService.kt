package com.example.ittestdemo.service

import com.example.ittestdemo.db.document.Person
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PersonService {

    fun getAll(): Flux<Person>
    fun getByGender(gender: String): Flux<Person>
    fun get(id: String): Mono<Person>
    fun add(person: Person): Mono<String>
}