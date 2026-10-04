package dev.charold.coach.practice;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

/**
 * Guards the hexagonal rules so they don't erode over time. Each rule maps to
 * a sentence in the README's architecture section.
 */
class ArchitectureTest {

    private static final String DOMAIN = "dev.charold.coach.practice.domain..";
    private static final String PORTS = "dev.charold.coach.practice.domain.port..";
    private static final String APPLICATION = "dev.charold.coach.practice.application..";
    private static final String ADAPTERS_IN = "dev.charold.coach.practice.infrastructure.adapter.in..";
    private static final String ADAPTERS_OUT = "dev.charold.coach.practice.infrastructure.adapter.out..";
    private static final String CONFIG = "dev.charold.coach.practice.infrastructure.config..";

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("dev.charold.coach");
    }

    @Test
    void domainIsPlainJava() {
        classes().that().resideInAPackage(DOMAIN)
                .should().onlyDependOnClassesThat().resideInAnyPackage("java..", DOMAIN)
                .because("the domain must not know about frameworks, the LLM or HTTP")
                .check(classes);
    }

    @Test
    void applicationDependsOnlyOnTheDomain() {
        classes().that().resideInAPackage(APPLICATION)
                .should().onlyDependOnClassesThat().resideInAnyPackage("java..", DOMAIN, APPLICATION)
                .because("use cases orchestrate ports; adapters and CDI are wired in infrastructure.config")
                .check(classes);
    }

    @Test
    void portsAreInterfaces() {
        classes().that().resideInAPackage(PORTS)
                .should().beInterfaces()
                .check(classes);
    }

    @Test
    void primaryAdaptersDoNotReachSecondaryAdapters() {
        noClasses().that().resideInAPackage(ADAPTERS_IN)
                .should().dependOnClassesThat().resideInAPackage(ADAPTERS_OUT)
                .because("the web layer talks to use cases, never to the LLM or the quota store directly")
                .check(classes);
    }

    @Test
    void secondaryAdaptersDoNotReachPrimaryAdapters() {
        noClasses().that().resideInAPackage(ADAPTERS_OUT)
                .should().dependOnClassesThat().resideInAPackage(ADAPTERS_IN)
                .check(classes);
    }

    @Test
    void onlyConfigKnowsTheApplicationServices() {
        noClasses().that().resideOutsideOfPackages(APPLICATION, CONFIG)
                .should().dependOnClassesThat().resideInAPackage(APPLICATION)
                .because("callers depend on the driver ports (use cases), not on their implementations")
                .check(classes);
    }

    @Test
    void adaptersOutImplementTheirPort() {
        classes().that().resideInAPackage(ADAPTERS_OUT).and().haveSimpleNameEndingWith("Adapter")
                .should().implement(dev.charold.coach.practice.domain.port.out.AnswerEvaluatorPort.class)
                .orShould().implement(dev.charold.coach.practice.domain.port.out.UsageQuotaPort.class)
                .check(classes);
    }
}
