package com.santoni7.readme.data;

import android.graphics.Bitmap;

import io.reactivex.Observable;


public class PersonWithImage extends Person {
//    private Observable<String> realUrlSource;
//    private Observable<Bitmap> imageSource;

    private String imageFileName;
    private Observable<Bitmap> imageSource;

    public PersonWithImage(){

    }

    public PersonWithImage(ImageRepository.ImageData imageData, Observable<Bitmap> imageSource){
        super(imageData.owner);
        setAvatarUrl(imageData.url);
        setImageFileName(imageData.fileName);
        setImageSource(imageSource);
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
