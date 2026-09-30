package lv.turiba.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Library Inventory System of company "ABC".
 * Run this class from IntelliJ IDEA (green arrow) or with "mvn spring-boot:run".
 */
@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
