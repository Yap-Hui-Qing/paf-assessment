package vttp.batch5.paf.movies.models;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Performance {
    private String director_name;
    private int movies_count;
    private int total_revenue;
    private int total_budget;

    public String getDirector_name() {
        return director_name;
    }
    public void setDirector_name(String director_name) {
        this.director_name = director_name;
    }
    public int getMovies_count() {
        return movies_count;
    }
    public void setMovies_count(int movies_count) {
        this.movies_count = movies_count;
    }
    public int getTotal_revenue() {
        return total_revenue;
    }
    public void setTotal_revenue(int total_revenue) {
        this.total_revenue = total_revenue;
    }
    public int getTotal_budget() {
        return total_budget;
    }
    public void setTotal_budget(int total_budget) {
        this.total_budget = total_budget;
    }

    // public static Performance toPerformance(Document d) {
    //     Performance performance = new Performance();
    //     performance.setDirector_name(d.getString("_id"));
    //     performance.setMovies_count(d.getInteger("movies_count"));
    //     performance.setTotal_revenue(d.getInteger("total_revenue"));
    //     performance.setTotal_budget(d.getInteger("total_budget"));
    //     return performance;
    // }

    public static JsonObject toJson(Document d){
        return Json.createObjectBuilder()
            .add("director_name", d.getString("_id"))
            .add("movies_count", d.getInteger("movies_count"))
            .add("total_revenue", d.getInteger("total_revenue"))
            .add("total_budget", d.getInteger("total_budget"))
            .build();
    }
}
