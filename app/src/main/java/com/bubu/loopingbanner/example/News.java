package com.bubu.loopingbanner.example;

/**
 * Created by Administrator on 2016/10/28.
 */

public class News {
    private String url;
    private String jumpUrl;

    public News(String url, String jumpUrl) {
        this.url = url;
        this.jumpUrl = jumpUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    @Override
    public String toString() {
        return "News{" +
                "url='" + url + '\'' +
                ", jumpUrl='" + jumpUrl + '\'' +
                '}';
    }
}
