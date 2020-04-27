package com.minimalistweather.android.gson_entity;

import com.google.gson.annotations.SerializedName;

public class SuggestionInfo {

    private Comf comf;

    @SerializedName("cw")
    private CarWash carWash;

    private Sport sport;

    public Comf getComf() {
        return comf;
    }

    public void setComf(Comf comf) {
        this.comf = comf;
    }

    public CarWash getCarWash() {
        return carWash;
    }

    public void setCarWash(CarWash carWash) {
        this.carWash = carWash;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public class Comf {

        @SerializedName("txt")
        private String comfInfo;

        public String getComfInfo() {
            return comfInfo;
        }

        public void setComfInfo(String comfInfo) {
            this.comfInfo = comfInfo;
        }
    }

    public class CarWash {

        @SerializedName("txt")
        private String carWashInfo;

        public String getCarWashInfo() {
            return carWashInfo;
        }

        public void setCarWashInfo(String carWashInfo) {
            this.carWashInfo = carWashInfo;
        }
    }

    public class Sport {

        @SerializedName("txt")
        private String sportInfo;

        public String getSportInfo() {
            return sportInfo;
        }

        public void setSportInfo(String sportInfo) {
            this.sportInfo = sportInfo;
        }
    }
}
