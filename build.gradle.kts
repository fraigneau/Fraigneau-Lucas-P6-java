plugins {
    java
    id("org.springframework.boot") version "3.4.2"
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
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)

    reports {
        html.required.set(true)
        junitXml.required.set(true)
        junitXml.outputLocation.set(layout.buildDirectory.dir("test-results/junit"))
    }

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Amapstruct.defaultComponentModel=spring")
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
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

tasks.javadoc {
    val javadocOptions = options as StandardJavadocDocletOptions
    javadocOptions.encoding = "UTF-8"
    javadocOptions.charSet("UTF-8")
    javadocOptions.setAuthor(true)
    javadocOptions.setVersion(true)
    javadocOptions.links("https://docs.oracle.com/en/java/javase/21/docs/api/")
    javadocOptions.windowTitle = "Documentation API"
    javadocOptions.docTitle("<h1>Documentation API</h1>")
    
    // Configuration pour ignorer les erreurs de documentation
    javadocOptions.addStringOption("Xdoclint:none", "-quiet")
    
    // Exclusion des classes générées par MapStruct, Lombok, etc.
    exclude("**/mapper/impl/**", "**/dto/**", "**/model/**")
    
    // Définition du dossier de destination
    destinationDir = file("${layout.buildDirectory.get()}/docs/javadoc")
    
    // Pour ignorer les erreurs de Javadoc
    (options as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
    
    // Désactiver l'échec de la build en cas d'erreurs de Javadoc
    isFailOnError = false
}