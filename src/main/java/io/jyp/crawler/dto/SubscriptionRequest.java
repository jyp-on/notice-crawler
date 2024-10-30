package io.jyp.crawler.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
    @NotNull
    private String email;
    private boolean NoticeFlag;
    private String name;
    private String noticeType;
}
