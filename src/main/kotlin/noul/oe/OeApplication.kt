package noul.oe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class OeApplication

fun main(args: Array<String>) {
	runApplication<OeApplication>(*args)
}
