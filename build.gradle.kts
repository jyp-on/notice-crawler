plugins {
	kotlin("jvm") version "1.9.10"
	kotlin("plugin.spring") version "1.9.10"
	kotlin("plugin.jpa") version "1.9.10"
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "io.jyp"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.jsoup:jsoup:1.14.3") // Jsoup for HTML parsing
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Lombok 추가
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.test {
	// 테스트 실행 시 로그 레벨을 INFO로 설정
	testLogging {
		events("passed", "skipped", "failed")
		showStandardStreams = true // 표준 출력 스트림을 항상 표시
	}
	useJUnitPlatform()
}
