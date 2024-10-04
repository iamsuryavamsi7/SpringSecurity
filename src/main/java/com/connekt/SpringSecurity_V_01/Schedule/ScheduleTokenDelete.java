package com.connekt.SpringSecurity_V_01.Schedule;

import com.connekt.SpringSecurity_V_01.Repo.TokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleTokenDelete {

    private final TokenRepo tokenRepo;

    @Scheduled(
            initialDelay = 3500000,
            fixedRate = 3600000
    )
    public void deleteExpiredOrRevokedTokens(){

        tokenRepo.deleteTokensByExpiredOrRevoked();

    }

}
