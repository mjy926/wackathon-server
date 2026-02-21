package com.wafflestudio.areucoming.history.service;

import com.wafflestudio.areucoming.couples.model.Couples;
import com.wafflestudio.areucoming.couples.repository.CouplesRepository;
import com.wafflestudio.areucoming.history.dto.PointHistoryDto;
import com.wafflestudio.areucoming.history.dto.SessionHistoryResponse;
import com.wafflestudio.areucoming.sessions.model.Session;
import com.wafflestudio.areucoming.sessions.model.SessionPoint;
import com.wafflestudio.areucoming.sessions.repository.SessionPointRepository;
import com.wafflestudio.areucoming.sessions.repository.SessionRepository;
import com.wafflestudio.areucoming.users.model.User;
import com.wafflestudio.areucoming.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HistoryService {
    private final SessionRepository sessionRepository;
    private final SessionPointRepository sessionPointRepository;
    private final CouplesRepository couplesRepository;
    private final UserRepository userRepository;

    public SessionHistoryResponse getHistory(Long sessionId, String email){
        User user = userRepository.findByEmail(email);
        Couples couples = couplesRepository.findByUser1IdOrUser2Id(user.getId(), user.getId());
        Long user1Id = couples.getUser1Id();
        Long user2Id = couples.getUser2Id();

        List<SessionPoint> user1SessionPoints = sessionPointRepository.findAllByUserIdWithSessionId(sessionId, user1Id);
        List<SessionPoint> user2SessionPoints = sessionPointRepository.findAllByUserIdWithSessionId(sessionId, user2Id);

        List<PointHistoryDto> user1DtoList = user1SessionPoints.stream()
                .map(point -> new PointHistoryDto(
                        point.getType(),
                        point.getCreatedAt(),
                        point.getLat(),
                        point.getLng(),
                        point.getPhotoPath(),
                        point.getText()
                )).toList();
        List<PointHistoryDto> user2DtoList = user2SessionPoints.stream()
                .map(point -> new PointHistoryDto(
                        point.getType(),
                        point.getCreatedAt(),
                        point.getLat(),
                        point.getLng(),
                        point.getPhotoPath(),
                        point.getText()
                )).toList();
        return SessionHistoryResponse.builder().user1(user1DtoList).user2(user2DtoList).build();
    }

    public List<Long> getHistoryIdList(String email){
        User user = userRepository.findByEmail(email);
        Couples couples = couplesRepository.findByUser1IdOrUser2Id(user.getId(), user.getId());
        Long coupleId = couples.getId();
        List<Session> sessions = sessionRepository.findAllByCoupleId(coupleId);

        return sessions.stream()
                .map(Session::getId)
                .toList();
    }
}
