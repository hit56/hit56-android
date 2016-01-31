package com.hit56.android.helper;


public class FeedItem {
    private int id;
    private String name, detail, image, profilePic, timeStamp, url;

    public FeedItem() {
    }

    public FeedItem(int id, String name, String image, String detail,
                    String profilePic, String timeStamp, String url) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
        this.detail = detail;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImge() {
        return "";
    }

    public void setImge(String image) {
        this.image = image;
    }

    public String getStatus() {
        return detail;
    }

    public void setStatus(String detail) {
        this.detail = detail;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
