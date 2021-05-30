package com.example.communityreport;

public class Model {
    private String imageUrl;
    private String imageName;
    private Object location;
    public Model () {

    }

    public Model(String imageUrl, String imageName, Object location){
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.location = location;

    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getImageName(){return imageName;}
    public Object getLocation() {return location;}
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;


    }
    public void setImageName(String imageName){ this.imageName = imageName;}
    public void setLocation(Object location){ this.location = location;}

}
