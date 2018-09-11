package com.santoni7.readme.data;

public class Person {
    private String id;
    private String firstName;
    private String secondName;
    private String avatarUrl;
    private int age;

    public Person(){

    }

    public Person(String id, String firstName, String secondName, String avatarUrl, int age){
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.avatarUrl = avatarUrl;
        this.age = age;
    }

    public Person(Person person){
        this.id = person.id;
        this.firstName = person.firstName;
        this.secondName = person.secondName;
        this.avatarUrl = person.avatarUrl;
        this.age = person.age;
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
        return getFirstName() + " " + getSecondName();
    }
}
