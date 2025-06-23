package com.utm.hometuition.model;
import java.time.LocalDate;

import com.utm.hometuition.scheduler.Scheduler;

public interface Schedulable {
    public void scheduleSession(Scheduler scheduler);
    public void rescheduleSession(Scheduler scheduler);
    public LocalDate getValidDate();
    public String[] getValidStartAndEndTimes();
    public void saveSessions(Scheduler scheduler);
}