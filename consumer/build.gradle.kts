plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation ("com.google.code.findbugs:jsr305")

    implementation("javax:javaee-api:8.0")
    implementation("com.sun.xml.messaging.saaj:saaj-impl:1.5.2")
    implementation("javax.xml.soap:javax.xml.soap-api:1.4.1")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("javax.xml.soap:javax.xml.soap-api:1.4.0")
}

