package com.example.porestaurant.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DirectionsResponse {
    @SerializedName("routes")
    public List<Route> routes;

    @SerializedName("status")
    public String status;

    public static class Route {
        @SerializedName("overview_polyline")
        public OverviewPolyline overviewPolyline;

        @SerializedName("legs")
        public List<Leg> legs;

        @SerializedName("summary")
        public String summary;
    }

    public static class OverviewPolyline {
        @SerializedName("points")
        public String points;
    }

    public static class Leg {
        @SerializedName("distance")
        public Distance distance;

        @SerializedName("duration")
        public Duration duration;

        @SerializedName("start_address")
        public String startAddress;

        @SerializedName("end_address")
        public String endAddress;
    }

    public static class Distance {
        @SerializedName("text")
        public String text;

        @SerializedName("value")
        public int value;
    }

    public static class Duration {
        @SerializedName("text")
        public String text;

        @SerializedName("value")
        public int value;
    }
}