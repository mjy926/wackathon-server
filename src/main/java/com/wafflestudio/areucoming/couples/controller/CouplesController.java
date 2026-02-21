package com.wafflestudio.areucoming.couples.controller;

import com.wafflestudio.areucoming.auth.dto.UserDto;
import com.wafflestudio.areucoming.couples.dto.CouplesResponse;
import com.wafflestudio.areucoming.couples.dto.InvitesRequest;
import com.wafflestudio.areucoming.couples.dto.InvitesResponse;
import com.wafflestudio.areucoming.couples.model.Couples;
import com.wafflestudio.areucoming.couples.model.Invites;
import com.wafflestudio.areucoming.couples.service.CouplesService;
import com.wafflestudio.areucoming.couples.service.InvitesService;
import com.wafflestudio.areucoming.users.model.User;
import com.wafflestudio.areucoming.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/api/couples")
@RestController
public class CouplesController {
    private final CouplesService couplesService;
    private final InvitesService invitesService;

    @PostMapping("/invite-code")
    public ResponseEntity<InvitesResponse> createInvites(@AuthenticationPrincipal String email){
        Invites newInvites = invitesService.createInvites(email);
        InvitesResponse res = new InvitesResponse(
                newInvites.getId(),
                newInvites.getInviterUserId(),
                newInvites.getCode(),
                newInvites.getExpiresAt(),
                newInvites.getUsedAt(),
                newInvites.getCreatedAt());

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/join")
    public ResponseEntity<InvitesResponse> joinInvites(@AuthenticationPrincipal String email,
                                                        @RequestBody InvitesRequest invitesRequest){
        Invites joinedInvites = invitesService.joinInvites(invitesRequest.getCode(), email);
        InvitesResponse res = new InvitesResponse(
                joinedInvites.getId(),
                joinedInvites.getInviterUserId(),
                joinedInvites.getCode(),
                joinedInvites.getExpiresAt(),
                joinedInvites.getUsedAt(),
                joinedInvites.getCreatedAt());

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<CouplesResponse> getCouplesInfo(@AuthenticationPrincipal String email){
        Couples couples = couplesService.getCouplesInfo(email);
        User dbUser1 = couplesService.getUserById(couples.getUser1Id());
        User dbUser2 = couplesService.getUserById(couples.getUser2Id());

        User me = dbUser1.getEmail().equals(email) ? dbUser1 : dbUser2;
        User partner = dbUser1.getEmail().equals(email) ? dbUser2 : dbUser1;

        UserDto user1Dto = new UserDto(me.getId(), me.getEmail(), me.getDisplayName(), me.getProfileImageUrl());
        UserDto user2Dto = new UserDto(partner.getId(), partner.getEmail(), partner.getDisplayName(), partner.getProfileImageUrl());
        CouplesResponse res = new CouplesResponse(couples.getId(), user1Dto, user2Dto);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteCouples(@AuthenticationPrincipal String email){
        couplesService.deleteCouples(email);
        return ResponseEntity.noContent().build();
    }
}
