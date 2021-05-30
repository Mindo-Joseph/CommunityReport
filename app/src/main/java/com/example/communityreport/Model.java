package com.example.communityreport;

public class Model {
    private String imageUrl;
    private String imageName;
    public Model () {

    }

    public Model(String imageUrl, String imageName){
        this.imageUrl = imageUrl;
        this.imageName = imageName;

    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getImageName(){return imageName;}
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;


    }
    public void setImageName(String imageName){ this.imageName = imageName;}

}
