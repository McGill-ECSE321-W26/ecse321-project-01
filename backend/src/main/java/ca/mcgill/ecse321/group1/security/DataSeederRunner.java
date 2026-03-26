package ca.mcgill.ecse321.group1.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// Activated only when the "seed" profile is active (i.e. ./gradlew seedDatabase).
// Calls DataSeeder.seed()
@Component
@Profile("seed")
public class DataSeederRunner implements CommandLineRunner {

  private final DataSeeder dataSeeder;

  public DataSeederRunner(DataSeeder dataSeeder) {
    this.dataSeeder = dataSeeder;
  }

  @Override
  public void run(String... args) {
    dataSeeder.seed();
  }
}
