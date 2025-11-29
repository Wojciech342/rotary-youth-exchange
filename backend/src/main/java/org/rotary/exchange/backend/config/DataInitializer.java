package org.rotary.exchange.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rotary.exchange.backend.model.Role;
import org.rotary.exchange.backend.model.RoleName;
import org.rotary.exchange.backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // Initialize roles if they don't exist
        initializeRole(RoleName.ROLE_COORDINATOR);
        initializeRole(RoleName.ROLE_ADMIN);
        
        log.info("Data initialization completed. Roles are ready.");
    }

    private void initializeRole(RoleName roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role(roleName);
            roleRepository.save(role);
            log.info("Created role: {}", roleName);
        }
    }
}
