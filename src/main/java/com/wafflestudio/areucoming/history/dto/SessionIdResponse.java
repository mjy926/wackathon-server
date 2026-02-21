package com.wafflestudio.areucoming.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SessionIdResponse {
    List<Long> ids;
}
