package noul.oe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OeApplication

fun main(args: Array<String>) {
	runApplication<OeApplication>(*args)
}
