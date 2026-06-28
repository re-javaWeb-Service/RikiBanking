package com.re.rikkeibanking.config;


import com.re.rikkeibanking.entity.Role;
import com.re.rikkeibanking.entity.User;
import com.re.rikkeibanking.repository.RoleRepository;
import com.re.rikkeibanking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(()->{
                    Role role = new Role();
                    role.setName("ROLE_ADMIN");
                    role.setDescription("System administrator");
                    return roleRepository.save(role);
                });
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
    }
}
