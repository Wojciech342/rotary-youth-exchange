package org.rotary.exchange.backend.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.rotary.exchange.backend.model.Coordinator;
import org.rotary.exchange.backend.repository.CoordinatorRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    CoordinatorRepository coordinatorRepository;
    @Autowired
    public UserDetailsServiceImpl(CoordinatorRepository coordinatorRepository) {
        this.coordinatorRepository = coordinatorRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Coordinator coordinator = coordinatorRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Coordinator not found with email: " + email));
        return UserPrinciple.build(coordinator);
    }
}
