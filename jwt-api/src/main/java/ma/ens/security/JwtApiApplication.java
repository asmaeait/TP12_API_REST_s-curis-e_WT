package ma.ens.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ma.ens.security")
public class JwtApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(JwtApiApplication.class, args);
	}
}