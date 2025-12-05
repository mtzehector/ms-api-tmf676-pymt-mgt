package mx.att.digital.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * The Class ManagementApplication.
 */
@SpringBootApplication
@EntityScan("mx.att.digital.api.models")
@ComponentScan(basePackages = { "mx.att.digital.api" })
public class ManagementApplication {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ManagementApplication.class, args);
	}
}