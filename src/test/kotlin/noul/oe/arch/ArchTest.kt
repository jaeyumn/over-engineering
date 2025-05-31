package noul.oe.arch

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ArchTest {
    // 분석할 기준 패키지 루트
    private val basePackage = "noul.oe.domain"

    // 지정한 패키지 이하의 class 파일 로드
    private val classes = ClassFileImporter().importPackages(basePackage)

    @Test
    @DisplayName("post 패키지는 user, comment 패키지에 의존하면 안된다")
    fun test1() {
        noClasses()
            .that().resideInAPackage("$basePackage.post..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("$basePackage.user..", "$basePackage.comment..")
            .check(classes)
    }

    @Test
    @DisplayName("comment 패키지는 user, post 패키지에 의존하면 안된다")
    fun test2() {
        noClasses()
            .that().resideInAPackage("$basePackage.comment..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("$basePackage.user..", "$basePackage.post..")
            .check(classes)
    }
}