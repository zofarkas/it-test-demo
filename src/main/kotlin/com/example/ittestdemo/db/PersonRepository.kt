package com.example.ittestdemo.db

import com.example.ittestdemo.db.document.Person
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PersonRepository: ReactiveMongoRepository<Person, String> {

    fun findAllByGender(gender: String): Flux<Person>
}