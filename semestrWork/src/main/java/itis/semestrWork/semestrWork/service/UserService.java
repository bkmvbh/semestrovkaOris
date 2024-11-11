package itis.semestrWork.semestrWork.service;

import itis.semestrWork.semestrWork.dto.LoginFormDto;
import itis.semestrWork.semestrWork.dto.RegistrationFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import itis.semestrWork.semestrWork.model.User;
import itis.semestrWork.semestrWork.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void saveUser(RegistrationFormDto dto) {
        if (userExists(dto.getUsername())) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }

        User user = User.builder()
                .userName(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении пользователя: " + e.getMessage());

        }

    }

    public boolean userExists(String username) {
        return userRepository.findByUserName(username) != null;
    }

    public User findByLogin(String username) {
        return userRepository.findByUserName(username);
    }

    public User findByLoginAndPassword(String username) {
        return userRepository.findByUserName(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
