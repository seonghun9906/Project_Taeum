package com.icia.Taeumproject.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteInfo {
    private int distance;
    private int duration;

    public RouteInfo(int distance, int duration) {
        this.distance = distance;
        this.duration = duration;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
