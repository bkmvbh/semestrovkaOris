package itis.semestrWork.semestrWork.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import itis.semestrWork.semestrWork.model.User;

@Service
public class AuthenticationService {

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetailImpl) {
            UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
            return userDetails.getUser().getId();
        }
        return null;
    }
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetailImpl) {
            UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
            return userDetails.getUser();
        }
        return null;
    }
}
