package io.github.hamzaikine.loginex;

public class Person {
    private String id;
    private String emotion;
    private String age;
    private String gender;
    private String photoId;

    Person(String id, String emotion, String age, String gender, String photoId) {
        this.id = id;
        this.emotion = emotion;
        this.age = age;
        this.photoId = photoId;
        this.gender = gender;
    }


    @Override
    public String toString() {
        return "You are a "+ age + " "+gender+" who feels "+ emotion + " "+id;
    }
}
