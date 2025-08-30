package org.ericp.ehub.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EHubServerApplication

fun main(args: Array<String>) {
    runApplication<EHubServerApplication>(*args)
}
