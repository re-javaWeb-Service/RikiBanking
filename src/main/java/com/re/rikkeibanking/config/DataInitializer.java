package com.re.rikkeibanking.config;


import com.re.rikkeibanking.entity.Account;
import com.re.rikkeibanking.entity.Role;
import com.re.rikkeibanking.entity.User;
import com.re.rikkeibanking.repository.AccountRepository;
import com.re.rikkeibanking.repository.RoleRepository;
import com.re.rikkeibanking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("dev-test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        Role adminRole = createRoleIfMissing("ROLE_ADMIN", "System administrator");
        Role staffRole = createRoleIfMissing("ROLE_STAFF", "Bank staff");
        Role customerRole = createRoleIfMissing("ROLE_CUSTOMER", "Bank customer");

        if(!userRepository.existsByUsername("admin")){
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@Rikkei-bank.com");
            admin.setPhoneNumber("090000000");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setIsActive(true);
            admin.setIsKyc(true);
            admin.setRole(adminRole);
            userRepository.save(admin);
        }

        User staff = createUserIfMissing(
                "staff",
                "staff@rikkei-bank.com",
                "091000000",
                "123456",
                true,
                staffRole
        );
        User customer1 = createUserIfMissing(
                "customer1",
                "customer1@rikkei-bank.com",
                "092000001",
                "123456",
                true,
                customerRole
        );
        User customer2 = createUserIfMissing(
                "customer2",
                "customer2@rikkei-bank.com",
                "092000002",
                "123456",
                true,
                customerRole
        );
        createAccountIfMissing(customer1, "1000000001", new BigDecimal("10000000.00"), "123456");
        createAccountIfMissing(customer2, "1000000002", new BigDecimal("5000000.00"), "123456");
    }

    private Role createRoleIfMissing(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    role.setDescription(description);
                    return roleRepository.save(role);
                });
    }

    private User createUserIfMissing(String username, String email, String phoneNumber, String password, boolean isKyc, Role role) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPhoneNumber(phoneNumber);
                    user.setPassword(passwordEncoder.encode(password));
                    user.setIsActive(true);
                    user.setIsKyc(isKyc);
                    user.setRole(role);
                    return userRepository.save(user);
                });
    }

    private void createAccountIfMissing(User user, String accountNumber, BigDecimal balance, String transactionPin) {
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            return;
        }
        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(accountNumber);
        account.setCurrency("VND");
        account.setBalance(balance);
        account.setTransactionPin(passwordEncoder.encode(transactionPin));
        account.setActive(true);
        accountRepository.save(account);
    }
}
