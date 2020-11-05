package com.example.cropprediction;

public class HistoryDetails {
    public String crop_name, image;

    public HistoryDetails(){}

    public HistoryDetails(String crop_name, String image){
        this.crop_name = crop_name;
        this.image = image;
    }

    public String getHistoryCropName(){ return crop_name;}

    public String getHistoryImage(){return image;}


}
