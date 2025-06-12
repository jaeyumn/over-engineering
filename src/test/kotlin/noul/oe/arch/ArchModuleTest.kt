package noul.oe.arch

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ArchModuleTest {
    private val basePackage = "noul.oe"
    private val classes = ClassFileImporter().importPackages(basePackage)

    @Test
    @DisplayName("support 패키지는 core 패키지에 의존하면 안된다")
    fun test() {
        noClasses()
            .that().resideInAPackage("$basePackage.support..")
            .should().dependOnClassesThat()
            .resideInAPackage("$basePackage.core..")
            .check(classes)
    }
}