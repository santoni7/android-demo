package com.santoni7.readme.data;

import android.graphics.Bitmap;

import io.reactivex.Observable;

public class Person {
    private String id;
    private String firstName;
    private String secondName;
    private String avatarUrl;
    private int age;


    private String imageFileName;
    private Observable<Bitmap> imageSource;

    public Person() {

    }



    @Override
    public String toString() {
        return String.format("{id: %s, full_name: %s, avatar_url: %s, image_source: %s}", id, getFullName(), avatarUrl, imageSource);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFullName() {
        return (getFirstName() + " " + getSecondName()).trim();
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public Observable<Bitmap> getImageSource() {
        return imageSource;
    }

    public void setImageSource(Observable<Bitmap> imageSource) {
        this.imageSource = imageSource;
    }
}
