package com.example.resiliencemap.core.verification;

import com.example.resiliencemap.core.user.UserRepository;
import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.core.verification.model.VerificationCode;
import com.example.resiliencemap.core.verification.model.VerificationCodeSendStatusResponse;
import com.example.resiliencemap.core.verification.model.VerificationContactMethod;
import com.example.resiliencemap.functional.exception.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository userRepository;
    private final Environment env;

    public VerificationCodeSendStatusResponse sendVerificationCodeToPhone(User user, String phone) {
        VerificationCode verificationCode = buildVerificationCode(user, phone, VerificationContactMethod.PHONE);
        verificationCodeRepository.save(verificationCode);

        // TODO: send SMS

        VerificationCodeSendStatusResponse response = new VerificationCodeSendStatusResponse();
        if (env.acceptsProfiles(Profiles.of("dev"))) {
            response.setCode(verificationCode.getCode());
        }
        response.setMessage("");
        response.setStatus("OK");
        return response;
    }

    private VerificationCode buildVerificationCode(User user, String destination, VerificationContactMethod verificationMethod) {
        VerificationCode verificationCode = new VerificationCode();
        String code = String.format("%06d", new SecureRandom().nextInt(999999));
        verificationCode.setCode(code);
        verificationCode.setDestination(destination);
        verificationCode.setCreatedAt(OffsetDateTime.now());
        verificationCode.setUser(user);
        verificationCode.setExpiresAt(OffsetDateTime.now().plusMinutes(10));
        verificationCode.setType(verificationMethod);
        verificationCode.setUsed(false);
        return verificationCode;
    }

    public VerificationCodeSendStatusResponse sendVerificationCodeToEmail(User user, String email) {
        VerificationCode verificationCode = buildVerificationCode(user, email, VerificationContactMethod.EMAIL);
        verificationCodeRepository.save(verificationCode);

        // TODO: send Email

        VerificationCodeSendStatusResponse response = new VerificationCodeSendStatusResponse();
        if (env.acceptsProfiles(Profiles.of("dev"))) {
            response.setCode(verificationCode.getCode());
        }
        response.setMessage("");
        response.setStatus("OK");
        return response;
    }

    @Transactional
    public User confirmContactVerification(String code, String destination) {
        VerificationCode verificationCode = getAndValidateCode(code, destination);
        return updateAfterVerification(verificationCode);
    }

    private VerificationCode getAndValidateCode(String code, String destination) {
        List<VerificationCode> list = verificationCodeRepository.findLastUnusedCode(destination, PageRequest.of(0, 1));
        if (!list.isEmpty() && list.getFirst().getCode().equals(code)) {
            VerificationCode verificationCode = list.getFirst();
            verificationCode.setUsed(true);
            return verificationCodeRepository.save(verificationCode);
        } else {
            throw new BadRequestException("Code not found");
        }
    }

    private User updateAfterVerification(VerificationCode verificationCode) {
        User user = verificationCode.getUser();
        switch (verificationCode.getType()) {
            case VerificationContactMethod.PHONE -> user.setPhoneNumber(verificationCode.getDestination());
            case VerificationContactMethod.EMAIL -> user.setEmail(verificationCode.getDestination());
        }
        updateNewUserStatus(user);
        return userRepository.save(user);
    }

    private void updateNewUserStatus(User user) {
        if (!user.getStatus().equals(User.StatusType.ACTIVE)) {
            if (user.getStatus().equals(User.StatusType.PENDING)) {
                user.setStatus(User.StatusType.ACTIVE);
            } else {
                throw new BadRequestException("User is not active");
            }
        }
    }
}

