package com.example.ittestdemo.service

import com.example.ittestdemo.db.PersonRepository
import com.example.ittestdemo.db.document.Person
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PersonServiceImpl(val personRepository: PersonRepository): PersonService {

    override fun getAll(): Flux<Person> {
        return personRepository.findAll()
    }

    override fun getByGender(gender: String): Flux<Person> {
        return personRepository.findAllByGender(gender)
    }

    override fun get(id: String): Mono<Person> {
        return personRepository.findById(id)
    }

    override fun add(person: Person): Mono<String> {
        return personRepository.save(person).map { it.id.toHexString() }
    }
}