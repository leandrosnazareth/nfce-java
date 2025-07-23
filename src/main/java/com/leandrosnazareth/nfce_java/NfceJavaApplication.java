package com.leandrosnazareth.nfce_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaRepositories
public class NfceJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NfceJavaApplication.class, args);
		System.out.println("🚀 NFC-e Java API iniciada com sucesso!");
		System.out.println("📖 Documentação: http://localhost:8080/nfce-api/swagger-ui/index.html");
		System.out.println("🗄️  H2 Console: http://localhost:8080/nfce-api/h2-console");
	}

}
