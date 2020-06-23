package com.example.autoappv31.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Auto {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("run")
    @Expose
    private Integer run;

    @SerializedName("year")
    @Expose
    private Integer year;

    @SerializedName("fuel")
    @Expose
    private Integer fuel;

    @SerializedName("comments")
    @Expose
    private String comments;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    @SerializedName("mark")
    @Expose
    private Mark mark;

    @SerializedName("model")
    @Expose
    private Model model;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRun() {
        return run;
    }

    public void setRun(Integer run) {
        this.run = run;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getFuel() {
        return fuel;
    }

    public void setFuel(Integer fuel) {
        this.fuel = fuel;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
