package com.example.ittestdemo.db.document

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Person(@Id @JsonSerialize(using = ToStringSerializer::class) val id: ObjectId = ObjectId(),
                  val firstName: String,
                  val lastName: String,
                  val gender: String)
