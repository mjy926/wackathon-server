package com.wafflestudio.areucoming.couples.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("codes")
public class Code {
    @Id
    Long id;

    String code;
}
