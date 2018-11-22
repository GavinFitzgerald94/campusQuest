package com.example.campusquest;

public class User {
    private String name;
    private int rank;
    private int steps;
    private int ImageId;

    public User(String name, int rank, int steps, int ImageId){
        this.name = name;
        this.rank = rank;
        this.steps = steps;
        this.ImageId = ImageId;
    }

    public int getImageId() {
        return ImageId;
    }

    public int getRank() {
        return rank;
    }

    public int getSteps() {
        return steps;
    }

    public String getName() {
        return name;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
