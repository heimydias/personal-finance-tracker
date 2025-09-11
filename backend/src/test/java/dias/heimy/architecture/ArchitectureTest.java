package dias.heimy.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@AnalyzeClasses(packages = "dias.heimy")
class ArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter().importPackages("dias.heimy");

    @Test
    void controllersShouldResideInControllerPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Controller")
                .should()
                .resideInAPackage("..controller..")
                .check(importedClasses);
    }

    @Test
    void repositoriesShouldResideInRepositoryPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Repository")
                .should()
                .resideInAPackage("..repository..")
                .check(importedClasses);
    }

    @Test
    void servicesShouldResideInServicePackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Service")
                .should()
                .resideInAPackage("..service..")
                .check(importedClasses);
    }

    @Test
    void configsShouldResideInConfigPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Config")
                .should()
                .resideInAPackage("..config..")
                .check(importedClasses);
    }

    @Test
    void constantsShouldResideInConstantsPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Constants")
                .should()
                .resideInAPackage("..constants..")
                .check(importedClasses);
    }

    @Test
    void noClassesShouldUseFieldInjection() {
        ArchRuleDefinition.noFields()
                .that()
                .areDeclaredInClassesThat()
                .resideOutsideOfPackage("..test..")
                .and()
                .areDeclaredInClassesThat()
                .haveSimpleNameNotEndingWith("Test")
                .should()
                .beAnnotatedWith(Inject.class)
                .orShould()
                .beAnnotatedWith(Autowired.class)
                .check(importedClasses);
    }

    @Test
    void mappersShouldResideInMapperPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Mapper")
                .should()
                .resideInAPackage("..dto.mapper..")
                .check(importedClasses);
    }

    @Test
    void requestsShouldResideInRequestPackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Request")
                .should()
                .resideInAPackage("..dto.request..")
                .orShould()
                .resideInAPackage("..webclient.request..")
                .check(importedClasses);
    }

    @Test
    void responseShouldResideInResponsePackage() {
        ArchRuleDefinition.classes()
                .that()
                .haveSimpleNameEndingWith("Response")
                .should()
                .resideInAPackage("..dto.response..")
                .orShould()
                .resideInAPackage("..webclient.response..")
                .check(importedClasses);
    }

    @Test
    void servicesShouldNotCallControllers() {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAPackage("..service..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .check(importedClasses);
    }

    @Test
    void controllersShouldNotDependOnEntities() {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAPackage("..controller..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..domain.entity..")
                .check(importedClasses);
    }

    @Test
    void entitiesShouldResideInDomainEntityPackage() {
        ArchRuleDefinition.classes()
                .that()
                .resideInAPackage("..domain.entity..")
                .should()
                .haveSimpleNameNotEndingWith("Request")
                .andShould()
                .haveSimpleNameNotEndingWith("Response")
                .andShould()
                .haveSimpleNameNotEndingWith("DTO")
                .check(importedClasses);
    }

    @Test
    void valueObjectsShouldResideInDomainValueObjectPackage() {
        ArchRuleDefinition.classes()
                .that()
                .resideInAPackage("..domain.valueobject..")
                .should()
                .beRecords()
                .orShould()
                .haveSimpleNameEndingWith("Token")
                .check(importedClasses);
    }

    @Test
    void dtosShouldNotResideInDomainPackage() {
        ArchRuleDefinition.noClasses()
                .that()
                .haveSimpleNameEndingWith("Request")
                .or()
                .haveSimpleNameEndingWith("Response")
                .or()
                .haveSimpleNameEndingWith("DTO")
                .should()
                .resideInAPackage("..domain.entity..")
                .orShould()
                .resideInAPackage("..domain.exception..")
                .orShould()
                .resideInAPackage("..domain.enums..")
                .check(importedClasses);
    }

    @Test
    void domainShouldNotDependOnDtoPackage() {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..dto..")
                .check(importedClasses);
    }

    @Test
    void domainShouldNotDependOnControllerPackage() {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .check(importedClasses);
    }

    @Test
    void repositoriesShouldNotDependOnControllers() {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAPackage("..repository..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .check(importedClasses);
    }

    @Test
    void repositoriesShouldNotDependOnDto() {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAPackage("..repository..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..dto..")
                .check(importedClasses);
    }

    @Test
    void controllersShouldOnlyDependOnServicesAndDtos() {
        ArchRuleDefinition.classes()
                .that()
                .resideInAPackage("..controller..")
                .and()
                .haveSimpleNameNotEndingWith("Test")
                .should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "..controller..",
                        "..service..",
                        "..dto..",
                        "java..",
                        "org.springframework..",
                        "lombok..",
                        "io.swagger..",
                        "jakarta..",
                        "org.slf4j..",
                        "dias.heimy.constants..")
                .check(importedClasses);
    }

    @Test
    void layeredArchitectureShouldBeRespected() {
        JavaClasses mainClasses = new ClassFileImporter()
                .withImportOption(location -> !location.contains("/test-classes/"))
                .importPackages("dias.heimy");

        Architectures.layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller")
                .definedBy("..controller..")
                .layer("Service")
                .definedBy("..service..")
                .layer("Repository")
                .definedBy("..repository..")
                .layer("Domain")
                .definedBy("..domain..")
                .layer("DTO")
                .definedBy("..dto..")
                .layer("Config")
                .definedBy("..config..")
                .whereLayer("Controller")
                .mayNotBeAccessedByAnyLayer()
                .whereLayer("Service")
                .mayOnlyBeAccessedByLayers("Controller", "Config")
                .whereLayer("Repository")
                .mayOnlyBeAccessedByLayers("Service", "Config")
                .whereLayer("Domain")
                .mayOnlyBeAccessedByLayers("Service", "Repository", "DTO", "Config")
                .whereLayer("DTO")
                .mayOnlyBeAccessedByLayers("Controller", "Service")
                .check(mainClasses);
    }
}
