package com.egov.docvalidation.service;

import com.egov.docvalidation.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    User saveUser(User user);
}
