package com.example.ittestdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories
class ItTestDemoApplication

fun main(args: Array<String>) {
	runApplication<ItTestDemoApplication>(*args)
}
