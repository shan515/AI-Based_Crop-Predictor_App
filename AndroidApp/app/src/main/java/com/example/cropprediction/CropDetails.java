package com.example.cropprediction;

public class CropDetails {

public String crop_name, image_url, season, pdf_link;
public int percent;
public float price;

    public CropDetails(){

    }

    public CropDetails(String crop_name, String image_url, int percent, String season, String pdf_link, Float price){
        this.crop_name = crop_name;
        this.image_url = image_url;
        this.percent = percent;
        this.season = season;
        this.pdf_link = pdf_link;
        this.price = price;
    }

    public String getCropName() {
        return crop_name;
    }

    public String getImageURL() {
        return image_url;
    }

    public int getCropPercent() {
        return percent;
    }

    public String getSeason(){
       return season;}

    public String getPdfLink(){return pdf_link;}

    public Float getPrice(){return price;}
}
