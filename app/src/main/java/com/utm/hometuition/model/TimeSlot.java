package com.utm.hometuition.model;
import java.time.LocalTime;

public enum TimeSlot {
    SLOT_1(LocalTime.of(8, 0), LocalTime.of(9, 0)),
    SLOT_2(LocalTime.of(9, 0), LocalTime.of(10, 0)),
    SLOT_3(LocalTime.of(10, 0), LocalTime.of(11, 0)),
    SLOT_4(LocalTime.of(11, 0), LocalTime.of(12, 0)),
    SLOT_5(LocalTime.of(12, 0), LocalTime.of(13, 0)),
    SLOT_6(LocalTime.of(13, 0), LocalTime.of(14, 0)),
    SLOT_7(LocalTime.of(14, 0), LocalTime.of(15, 0)),
    SLOT_8(LocalTime.of(15, 0), LocalTime.of(16, 0)),
    SLOT_9(LocalTime.of(16, 0), LocalTime.of(17, 0)),;

    private final LocalTime start;
    private final LocalTime end;

    TimeSlot(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() { return start; }
    public LocalTime getEnd() { return end; }
}
