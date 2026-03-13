package com.example.spring_kotlin_final_demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka

class SpringKotlinFinalDemoApplication

fun main(args: Array<String>) {
	runApplication<SpringKotlinFinalDemoApplication>(*args)
}
