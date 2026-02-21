package com.wafflestudio.areucoming.history.service;

import com.wafflestudio.areucoming.common.utils.DistanceCalculator;
import com.wafflestudio.areucoming.couples.model.Couples;
import com.wafflestudio.areucoming.couples.repository.CouplesRepository;
import com.wafflestudio.areucoming.history.dto.*;
import com.wafflestudio.areucoming.sessions.model.Session;
import com.wafflestudio.areucoming.sessions.model.SessionPoint;
import com.wafflestudio.areucoming.sessions.repository.SessionPointRepository;
import com.wafflestudio.areucoming.sessions.repository.SessionRepository;
import com.wafflestudio.areucoming.users.model.User;
import com.wafflestudio.areucoming.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HistoryService {
    private final SessionRepository sessionRepository;
    private final SessionPointRepository sessionPointRepository;
    private final CouplesRepository couplesRepository;
    private final UserRepository userRepository;

    public SessionHistoryResponse getSessionHistory(Long sessionId, String email){
        User user = userRepository.findByEmail(email);
        Couples couples = couplesRepository.findByUser1IdOrUser2Id(user.getId(), user.getId());
        Long user1Id = couples.getUser1Id();
        Long user2Id = couples.getUser2Id();

        List<SessionPoint> user1SessionPoints = sessionPointRepository.findAllByUserIdWithSessionId(user1Id, sessionId);
        List<SessionPoint> user2SessionPoints = sessionPointRepository.findAllByUserIdWithSessionId(user2Id, sessionId);

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

    public SessionPointResponse getSessionPoints(String email){
        List<Long> ids = getHistoryIdList(email);

        List<SessionPoint> pointList = sessionPointRepository.findAllBySessionIdIn(ids);
        List<PointHistoryDto> pointDtoList = pointList.stream()
                .map(point -> new PointHistoryDto(
                        point.getType(),
                        point.getCreatedAt(),
                        point.getLat(),
                        point.getLng(),
                        point.getPhotoPath(),
                        point.getText()
                )).toList();

        return new SessionPointResponse(pointDtoList);
    }

    public int calculateDistance(SessionPoint p1, SessionPoint p2){
        double dis = DistanceCalculator.calculateDistance(p1.getLat().doubleValue(), p1.getLng().doubleValue(),
                                                        p2.getLat().doubleValue(), p2.getLng().doubleValue());
        return (int)dis;
    }

    public double getUserDistanceInSession(Long sessionId, String email){
        User user = userRepository.findByEmail(email);
        List<SessionPoint> pointList = sessionPointRepository.findAllByUserIdWithSessionId(user.getId(), sessionId);
        if(pointList == null || pointList.size() < 2){
            return 0;
        }
        double totalDistance = 0.0;

        for (int i = 0; i < pointList.size() - 1; i++) {
            SessionPoint p1 = pointList.get(i);
            SessionPoint p2 = pointList.get(i + 1);

            totalDistance += calculateDistance(p1, p2);
        }

        return totalDistance;
    }

    public int getTotalDistanceInSession(Long sessionId){
        Session s = sessionRepository.findById(sessionId).get();
        Couples couple = couplesRepository.findById(s.getCoupleId()).get();
        User user1 = userRepository.findById(couple.getUser1Id()).get();
        User user2 = userRepository.findById(couple.getUser2Id()).get();
        double totalDistance = getUserDistanceInSession(sessionId, user1.getEmail()) + getUserDistanceInSession(sessionId, user2.getEmail());
        return (int)totalDistance;
    }

    public List<HistoryDto> getHistoryList(String email){
        List<Long> ids = getHistoryIdList(email);
        List<HistoryDto> historyList = new ArrayList<>();

        for(Long sessionId : ids){
            int dis = getTotalDistanceInSession(sessionId);
            Session session = sessionRepository.findById(sessionId).get();
            long travelTime = Duration.between(session.getStartAt(), session.getEndAt()).toMinutes();

            HistoryDto res = new HistoryDto(sessionId, session.getMeetAt(), travelTime, dis);
            historyList.add(res);
        }

        return historyList;
    }

    public int getTotalMeetings(){
        return sessionRepository.countSessionsThisMonth();
    }

    public StatResponse getStat(String email){
        int totalMeetings = getTotalMeetings();
        int minMinutes = Integer.MAX_VALUE;
        double totalMinutes = 0.0;
        double totalDistance = 0.0;

        List<HistoryDto> historyList = getHistoryList(email);
        int len = historyList.size();
        for(HistoryDto h : historyList){
            totalMinutes += h.getTravelMinutes();
            totalDistance += h.getDistance();
            if(h.getTravelMinutes() < minMinutes){
                minMinutes = (int)h.getTravelMinutes();
            }
        }

        return new StatResponse(totalMeetings, (int)(totalMinutes/len), (int)(totalDistance/len), (int)totalMinutes, (int)totalDistance, minMinutes);
    }
}
