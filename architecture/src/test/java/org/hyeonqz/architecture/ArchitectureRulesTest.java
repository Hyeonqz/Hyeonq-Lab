package org.hyeonqz.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

/**
 * 적합도 함수 (Step 12) — 이 책의 규칙들이 "깨질 수 있는 실행물"이 된 모습.
 * 헥사고날의 의존성 규칙을 위반하는 커밋은 여기서 빨간불이 된다.
 * 테스트 코드는 검사에서 제외한다 — 테스트는 주도 어댑터라서, 어댑터를 조립하는 것이 그의 일이다.
 */
class ArchitectureRulesTest {

    private static final JavaClasses CLASSES =
            new ClassFileImporter()
                    .withImportOption(new ImportOption.DoNotIncludeTests())
                    .importPackages("org.hyeonqz.architecture");

    @Test
    void 헥사고날_도메인은_바깥을_모른다() {
        noClasses().that().resideInAPackage("..hexagonal.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..hexagonal.application..", "..hexagonal.adapter..")
                .check(CLASSES);
    }

    @Test
    void 헥사고날_유스케이스는_어댑터를_모른다() {
        noClasses().that().resideInAPackage("..hexagonal.application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..hexagonal.adapter..")
                .check(CLASSES);
    }

    @Test
    void 스타일_구현들_사이에_순환이_없다() {
        slices().matching("org.hyeonqz.architecture.(*)..")
                .should().beFreeOfCycles()
                .check(CLASSES);
    }
}
