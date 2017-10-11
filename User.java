package net.akg.com.tracker;



public class User {
    private String userId;
    private String userName;
    private String telePhone;
    private String imageData;
    private String photoData;

    void setUserId(String userId)
    {
        this.userId = userId;
    }

    String getUserId()
    {
        return this.userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName()
    {
        return this.userName;
    }

    void setTelePhone(String phone)
    {
        this.telePhone = phone;
    }

    String getTelePhone()
    {
        return this.telePhone;
    }

    void setImageData(String imageData)
    {
       this.imageData = imageData;
    }

    String getImageData()
    {
        return this.imageData;
    }

    void setPhotoData(String photoData){
        this.photoData = photoData;
    }
    String getPhotoData(){
        return this.photoData;
    }



}
