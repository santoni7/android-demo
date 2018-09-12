package com.santoni7.readme.data;

public class ImageData {
    private String url;
    private String fileName;
    private Person owner;

    public ImageData(String url, String fileName, Person owner) {
        this.setUrl(url);
        this.setFileName(fileName);
        this.setOwner(owner);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }
}
