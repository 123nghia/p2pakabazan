package com.akabazan.service.impl;

import com.akabazan.repository.UserRepository;
import com.akabazan.repository.entity.User;
import com.akabazan.service.PartnerUserProvisioningService;
import com.akabazan.service.dto.PartnerUserProvisioningCommand;
import jakarta.transaction.Transactional;
import java.util.Locale;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class PartnerUserProvisioningServiceImpl implements PartnerUserProvisioningService {

    private final UserRepository userRepository;

    public PartnerUserProvisioningServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UUID provisionPartnerUser(PartnerUserProvisioningCommand command) {
        String type = requireTrimmed(command.getPartnerId(), "partnerId");
        String relId = requireTrimmed(command.getExternalUserId(), "externalUserId");

        User existing = userRepository.findByTypeAndRelId(type, relId).orElse(null);
        if (existing != null) {
            boolean changed = false;

            if (isNotBlank(command.getUsername()) && (existing.getUsername() == null || existing.getUsername().isBlank())) {
                existing.setUsername(command.getUsername().trim());
                changed = true;
            }

            if (existing.getRelId() == null || existing.getRelId().isBlank()) {
                existing.setRelId(relId);
                changed = true;
            }

            if (existing.getType() == null || existing.getType().isBlank()) {
                existing.setType(type);
                changed = true;
            }

            if (command.getKycStatus() != null && existing.getKycStatus() != command.getKycStatus()) {
                existing.setKycStatus(command.getKycStatus());
                changed = true;
            }

            if (changed) {
                userRepository.save(existing);
            }
            return existing.getId();
        }

        String email = normalizeEmail(isNotBlank(command.getEmail()) ? command.getEmail() : command.getFallbackEmail());

        User user = new User();
        user.setType(type);
        user.setRelId(relId);
        user.setEmail(email);
        user.setUsername(isNotBlank(command.getUsername()) ? command.getUsername().trim() : relId);
        user.setPassword(UUID.randomUUID().toString());
        if (command.getKycStatus() != null) {
            user.setKycStatus(command.getKycStatus());
        }

        try {
            return userRepository.save(user).getId();
        } catch (DataIntegrityViolationException e) {
            return userRepository.findByTypeAndRelId(type, relId)
                    .map(User::getId)
                    .orElseThrow(() -> e);
        }
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static String requireTrimmed(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }
}
