package com.wafflestudio.areucoming.couples.service;

import com.wafflestudio.areucoming.couples.exceptions.ExpiredInvitesException;
import com.wafflestudio.areucoming.couples.model.Code;
import com.wafflestudio.areucoming.couples.model.Couples;
import com.wafflestudio.areucoming.couples.model.Invites;
import com.wafflestudio.areucoming.couples.repository.CodeRepository;
import com.wafflestudio.areucoming.couples.repository.CouplesRepository;
import com.wafflestudio.areucoming.couples.repository.InvitesRepository;
import com.wafflestudio.areucoming.users.model.User;
import com.wafflestudio.areucoming.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InvitesService {
    private final CouplesRepository couplesRepository;
    private final InvitesRepository invitesRepository;
    private final CodeRepository codeRepository;
    private final UserRepository userRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public Invites createInvites(String email){
        User user = userRepository.findByEmail(email);
        Long userId = user.getId();
        String randomCode = createCode();
        Invites newInvites = Invites.builder()
                .inviterUserId(userId)
                .code(randomCode)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .createdAt(LocalDateTime.now())
                .build();
        invitesRepository.save(newInvites);
        return newInvites;
    }

    public Invites joinInvites(String code, String email){
        User user1 = userRepository.findByEmail(email);
        Invites invites = invitesRepository.findByCode(code).get();
        User user2 = userRepository.findById(invites.getInviterUserId()).get();
        if(invites.getUsedAt() != null){
            throw new ExpiredInvitesException("used invites");
        }
        if(LocalDateTime.now().isAfter(invites.getExpiresAt())){
            throw new ExpiredInvitesException("expired invites");
        }

        invites.setUsedAt(LocalDateTime.now());
        Couples c = Couples.builder()
                .user1Id(user1.getId())
                .user2Id(user2.getId())
                .createdAt(LocalDateTime.now())
                .build();
        couplesRepository.save(c);

        Code toDelete = codeRepository.findByCode(code);
        codeRepository.delete(toDelete);
        return invitesRepository.save(invites);
    }

    public String createCode(){
        String code;
        do{
            code = createRandomCode();
        }while(codeRepository.existsByCode(code));
        Code c = Code.builder().code(code).build();
        codeRepository.save(c);
        return code;
    }

    public String createRandomCode(){
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for(int i = 0; i < CODE_LENGTH; i++){
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
