package com.wafflestudio.areucoming.couples.service;

import com.wafflestudio.areucoming.couples.exceptions.CoupleNotFoundException;
import com.wafflestudio.areucoming.couples.model.Couples;
import com.wafflestudio.areucoming.couples.repository.CouplesRepository;
import com.wafflestudio.areucoming.users.model.User;
import com.wafflestudio.areucoming.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Service
public class CouplesService {
    private final CouplesRepository couplesRepository;
    private final UserRepository userRepository;

    public Couples getCouplesInfo(String email) {
        User user = userRepository.findByEmail(email);
        Long userId = user.getId();
        Couples couples = couplesRepository.findByUser1IdOrUser2Id(userId, userId);
        if(couples == null){
            throw new CoupleNotFoundException("Couple not found");
        }

        return couples;
    }

    public Couples getCoupleByUserIdOrThrow(Long userId) {
        Couples couples = couplesRepository.findByUser1IdOrUser2Id(userId, userId);
        if (couples == null) throw new ResponseStatusException(NOT_FOUND, "Couple not found");
        return couples;
    }

    public void deleteCouples(String email){
        User user = userRepository.findByEmail(email);
        Long userId = user.getId();
        Couples toDelete = couplesRepository.findByUser1IdOrUser2Id(userId, userId);
        if(toDelete == null) throw new ResponseStatusException(NOT_FOUND, "Couple not found");
        couplesRepository.deleteById(toDelete.getId());
    }

    public User getUserById(Long userId){
        return userRepository.findById(userId).get();
    }
}
