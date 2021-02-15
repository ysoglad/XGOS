package com.app.XGOS;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class SchedulerService {

    @Transactional
    @Scheduled(cron = "0 0 2 * * *", zone = "UTC")
    public void updateWishesAndOrdersDatabase() {

    }
}