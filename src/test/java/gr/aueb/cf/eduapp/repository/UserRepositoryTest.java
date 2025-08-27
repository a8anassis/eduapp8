package gr.aueb.cf.eduapp.repository;

import gr.aueb.cf.eduapp.core.enums.GenderType;
import gr.aueb.cf.eduapp.core.enums.Role;
import gr.aueb.cf.eduapp.model.User;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;


    private Faker faker;

    @BeforeEach
    public void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        faker = new Faker(new Locale("el"));
        List<String> femaleNames = List.of("Μαρία", "Ελένη", "Κατερίνα", "Σοφία", "Αναστασία");
        List<String> maleNames   = List.of("Γιάννης", "Κωνσταντίνος", "Νίκος", "Δημήτρης", "Αλέξανδρος");


        IntStream.range(0, 10).forEach(i -> {
            User user = new User();
            user.setUsername(faker.internet().username() + "-" + UUID.randomUUID().toString().substring(0, 4)); // ensure uniqueness
            String rawPassword = "C0d1ngF@";
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setFirstname(faker.name().firstName());
            user.setLastname(faker.name().lastName());
            user.setVat(faker.number().digits(10));
            user.setFatherName(maleNames.get(faker.random().nextInt(maleNames.size())));
            user.setFatherLastname(faker.name().lastName());
            user.setMotherName(femaleNames.get(faker.random().nextInt(femaleNames.size())));
            user.setMotherLastname(faker.name().lastName());
            user.setDateOfBirth(randomBirthday(18, 65));
            user.setGender(faker.options().option(GenderType.values()));
            user.setRole(faker.options().option(Role.values()));
            user.setIsActive(true);

            userRepository.save(user);
        });
    }

    @Test
    void testUsersAreInserted() {
        List<User> users = userRepository.findAll();
//        assertThat(users).hasSize(10);  // AssertJ
        assertEquals(10, users.size()); // JUnit
    }

    private LocalDate randomBirthday(int minAge, int maxAge) {
        LocalDate today = LocalDate.now();

        // Oldest allowed DOB (maxAge years ago)
        LocalDate minDate = today.minusYears(maxAge);

        // Youngest allowed DOB (minAge years ago)
        LocalDate maxDate = today.minusYears(minAge);

        // Pick a random day between minDate and maxDate
        long days = ChronoUnit.DAYS.between(minDate, maxDate);
        return minDate.plusDays(ThreadLocalRandom.current().nextLong(days + 1));
    }


}