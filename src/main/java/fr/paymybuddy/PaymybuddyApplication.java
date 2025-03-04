package fr.paymybuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Pay My Buddy application.
 * <p>
 * This class initializes and starts the Spring Boot application that provides
 * a platform for users to manage transactions and payments between friends.
 * </p>
 */
@SpringBootApplication
public class PaymybuddyApplication {

	/**
	 * The main method that starts the Pay My Buddy application.
	 * 
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(PaymybuddyApplication.class, args);
	}
}