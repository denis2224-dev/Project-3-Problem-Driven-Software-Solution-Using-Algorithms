package com.unischeduler.solver.csp;

import java.util.Objects;

/**
 * CSP value representing a concrete timeslot-room option for one event.
 */
public class CSPValue {

    private final String id;
    private final Long timeslotId;
    private final Long roomId;
    private final String dayOfWeek;
    private final String startTime;
    private final String endTime;
    private final Integer roomCapacity;
    private final String roomEquipment;
    private final String buildingCode;

    public CSPValue(String id) {
        this(id, null, null, null, null, null, null, null, null);
    }

    public CSPValue(
        String id,
        Long timeslotId,
        Long roomId,
        String dayOfWeek,
        String startTime,
        String endTime,
        Integer roomCapacity,
        String roomEquipment,
        String buildingCode
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.timeslotId = timeslotId;
        this.roomId = roomId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomCapacity = roomCapacity;
        this.roomEquipment = roomEquipment;
        this.buildingCode = buildingCode;
    }

    public String getId() {
        return id;
    }

    public Long getTimeslotId() {
        return timeslotId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Integer getRoomCapacity() {
        return roomCapacity;
    }

    public String getRoomEquipment() {
        return roomEquipment;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CSPValue cspValue)) {
            return false;
        }
        return id.equals(cspValue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CSPValue{" + "id='" + id + '\'' + '}';
    }
}
