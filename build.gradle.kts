plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
}

group = "fr"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    runtimeOnly("com.mysql:mysql-connector-j")

    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Amapstruct.defaultComponentModel=spring")
}

// Configuration des sources pour les tests d'intégration
val integrationTest by sourceSets.creating {
    java.srcDir("src/test/java")
    resources.srcDir("src/test/resources")
    compileClasspath = sourceSets.main.get().output + 
                      sourceSets.test.get().output + 
                      configurations.testCompileClasspath.get()
    runtimeClasspath = output + 
                      sourceSets.main.get().output + 
                      sourceSets.test.get().output + 
                      configurations.testRuntimeClasspath.get()
}

// Configuration des dépendances pour les tests d'intégration
val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val integrationTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.testRuntimeOnly.get())
}

// Configuration de la tâche de test unitaire
tasks.test {
    description = "Exécute les tests unitaires."
    group = "verification"
    
    useJUnitPlatform {
        excludeTags("integration")
    }
    
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    
    systemProperty("spring.profiles.active", "test")
    
    finalizedBy(tasks.jacocoTestReport)
}

// Configuration de la tâche de test d'intégration
val integrationTestTask = tasks.register<Test>("integrationTest") {
    description = "Exécute les tests d'intégration."
    group = "verification"
    
    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath
    
    useJUnitPlatform {
        includeTags("integration")
    }
    
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    
    systemProperty("spring.profiles.active", "integration-test")
    
    shouldRunAfter(tasks.test)
    
    finalizedBy(tasks.jacocoTestReport)
}

// Tâche pour exécuter tous les tests (unitaires et intégration)
tasks.register<Test>("testAll") {
    description = "Exécute tous les tests (unitaires et d'intégration)."
    group = "verification"
    
    dependsOn(tasks.test, integrationTestTask)
}

// Configuration de JaCoCo pour les rapports de couverture de code
jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    
    reports {
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
    }
    
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/dto/**")
                exclude("**/model/**")
                exclude("**/mapper/**")
            }
        })
    )
}

// Configuration de la documentation JavaDoc
tasks.javadoc {
    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        charSet = "UTF-8"
        setAuthor(true)
        setVersion(true)
        links("https://docs.oracle.com/en/java/javase/21/docs/api/")
        windowTitle = "Documentation API"
        docTitle = "<h1>Documentation API</h1>"
        addStringOption("Xdoclint:none", "-quiet")
    }
    
    exclude("**/mapper/impl/**", "**/dto/**", "**/model/**")
    destinationDir = file("${layout.buildDirectory}/docs/javadoc")
    isFailOnError = false
}

// Configuration pour le check de version de Java
tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}