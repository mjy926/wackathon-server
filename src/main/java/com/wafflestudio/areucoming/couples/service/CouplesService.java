package com.wafflestudio.areucoming.couples.service;

import com.wafflestudio.areucoming.couples.model.Couples;
import com.wafflestudio.areucoming.couples.repository.CouplesRepository;
import com.wafflestudio.areucoming.users.model.User;
import com.wafflestudio.areucoming.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CouplesService {
    private final CouplesRepository couplesRepository;
    private final UserRepository userRepository;

    public Couples getCouplesInfo(String email) {
        User user = userRepository.findByEmail(email);
        Long userId = user.getId();
        Couples couples = couplesRepository.findByUser1IdOrUser2Id(userId, userId);

        return couples;
    }
}
