package org.example.services;

import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для сброса пароля. Электронная подпись (код 0000) никуда не отправляется, а просто заносится в <b>confirmationCodes</b>
 */
@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    private final Map<String, String> confirmationCodes = new ConcurrentHashMap<>();

    public void sendConfirmationCode(User user) {
        // Для упрощения используется заглушка "0000"
        String confirmationCode = "0000";
        confirmationCodes.put(user.getUsername(), confirmationCode);
    }

    public boolean validateConfirmationCode(String username, String code) {
        return confirmationCodes.containsKey(username) && confirmationCodes.get(username).equals(code);
    }

    public void removeConfirmationCode(String username) {
        confirmationCodes.remove(username);
    }
}
