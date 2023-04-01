package com.uplus.zip

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class ZipApplication

fun main(args: Array<String>) {
    runApplication<ZipApplication>(*args)
}
