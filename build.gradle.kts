plugins {
	java
	id("org.springframework.boot") version "3.5.0" // pode atualizar de 3.4.4 se quiser
	id("io.spring.dependency-management") version "1.1.7"
	jacoco
}

group = "com.gymtutor"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	/* ───────── Spring ───────── */
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

	/* Segurança + Thymeleaf */
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

	/* Jakarta EE (apenas se realmente usar APIs de lá) */
	implementation("jakarta.platform:jakarta.jakartaee-api:10.0.0")

	/* Banco */
	// runtimeOnly("com.mysql:mysql-connector-j")

	/* Testes */
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	/* DevTools - Ferramentas para desenvolvimento */
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	runtimeOnly("com.mysql:mysql-connector-j")

}


// --- CONFIGURAÇÃO DO JACOCO ---

// 1. Configura a tarefa jacocoTestReport
tasks.named<JacocoReport>("jacocoTestReport") {
	// Garante que os testes rodem antes de gerar o relatório
	dependsOn(tasks.test)

	reports {
		// Gera o relatório no formato XML (útil para integração contínua)
		xml.required.set(true)
		// Não precisamos do formato CSV
		csv.required.set(false)
		// Gera o relatório HTML para fácil visualização e captura do percentual
		html.required.set(true)
	}
}

// 2. Configura a tarefa 'test' (corrigido e simplificado)
tasks.withType<Test> {
	useJUnitPlatform()
	// Adiciona o jacocoTestReport como uma tarefa que deve ser executada após o 'test'
	finalizedBy(tasks.jacocoTestReport)
}