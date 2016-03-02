package com.hit56.android.helper;


public class FeedItem {
    private int id;
    private String name, detail, image, profilePic, timeStamp, cell;

    public FeedItem() {
    }

    public FeedItem(String name, String image, String detail,
                    String profilePic, String timeStamp, String cell) {
        super();
        this.name = name;
        this.image = image;
        this.detail = detail;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.cell = cell;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImge() {
        return image;
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

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }
}
