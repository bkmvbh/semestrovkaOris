package itis.semestrWork.semestrWork.service;

import itis.semestrWork.semestrWork.model.User;
import itis.semestrWork.semestrWork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUserName(username);
        if (user != null) {
            return new UserDetailImpl(user);
        } else {
            throw new UsernameNotFoundException("Пользователь не найден.");
        }
    }
}