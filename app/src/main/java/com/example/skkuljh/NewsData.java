package com.example.skkuljh;

import java.io.Serializable;

public class NewsData implements Serializable{


    private String title;
    private String urlToImage;
    private String Despcription;
    private String url;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getUrlToImage() {
        return urlToImage;
    }
    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }


    public String getDespcription() {
        return Despcription;
    }

    public void setDespcription(String despcription) {
        Despcription = despcription;
    }

    public String getUrl(){return url;}

    public void setUrl(String url){
        this.url=url;
    }
}
