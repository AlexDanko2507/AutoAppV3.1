package com.example.autoappv31.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("sum")
    @Expose
    private Double sum;

    @SerializedName("currentRun")
    @Expose
    private Integer currentRun;

    @SerializedName("comments")
    @Expose
    private String comments;

    @SerializedName("date")
    @Expose
    private Date date;

    @SerializedName("category")
    @Expose
    private Category category;

    @SerializedName("auto")
    @Expose
    private Auto auto;

    @SerializedName("technicalWork")
    @Expose
    private TechnicalWork technicalWork;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Integer getCurrentRun() {
        return currentRun;
    }

    public void setCurrentRun(Integer currentRun) {
        this.currentRun = currentRun;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    public TechnicalWork getTechnicalWork() {
        return technicalWork;
    }

    public void setTechnicalWork(TechnicalWork technicalWork) {
        this.technicalWork = technicalWork;
    }
}
