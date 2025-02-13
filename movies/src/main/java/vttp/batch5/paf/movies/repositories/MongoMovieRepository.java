package vttp.batch5.paf.movies.repositories;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;

@Repository
public class MongoMovieRepository {

    @Autowired
    private MongoTemplate template;

    // TODO: Task 2.3
    // You can add any number of parameters and return any type from the method
    // You can throw any checked exceptions from the method
    // Write the native Mongo query you implement in the method in the comments
    //
    // native MongoDB query here
    //
    /*
     * db.imdb.insertMany([
     * {
     * _id: 'imdb_id',
     * title: 'title',
     * directors: 'director',
     * overview: 'overview',
     * tagline: 'tagline',
     * genres: 'genres',
     * imdb_rating: 'imdb_rating',
     * imdb_votes: 'imdb_votes'
     * }
     * ])
     */
 public void batchInsertMovies(List<List<Document>> documentBatches) {
    try{
        for (List<Document> documents : documentBatches){
            List<Document> docsToInsert = new LinkedList<>();
            // add document to list
            for (Document d: documents){
                docsToInsert.add(Document.parse(Json.createObjectBuilder()
                .add("_id", d.getString("imdb_id"))
                .add("title", d.getString("title"))
                .add("directors", d.getString("director"))
                .add("overview", d.getString("overview"))
                .add("tagline", d.getString("tagline"))
                .add("genres", d.getString("genres"))
                .add("imdb_rating", d.getInteger("imdb_rating"))
                .add("imdb_votes", d.getInteger("imdb_votes"))
                .build().toString()));
                template.insert(docsToInsert, "imdb");
            }
        }
    } catch (Exception ex){
        ex.printStackTrace();
        throw new RuntimeException("Failed to insert movies");
    }
    
 }

    // TODO: Task 2.4
    // You can add any number of parameters and return any type from the method
    // You can throw any checked exceptions from the method
    // Write the native Mongo query you implement in the method in the comments
    //
    // native MongoDB query here
    //
    /*
    
     */
    public void logError() {

    }

    // TODO: Task 3
    // Write the native Mongo query you implement in the method in the comments
    //
    // native MongoDB query here
    //
    /*
     * db.imdb.aggregate([
     * {
     * $match: {'release_date': {$gte: '2018-01-01'}}
     * },
     * {
     * $group: {
     * _id: '$director',
     * count_movies: {$sum: 1},
     * total_revenue: {$sum: '$revenue'},
     * total_budget: {$sum: '$budget'}
     * }
     * },
     * {
     * $sort: {count_movies: -1}
     * },
     * {
     * $limit: 5
     * }
     * ])
     */
    public List<Document> getMoviePerformance(int limit) {
        MatchOperation match = Aggregation.match(Criteria.where("release_date").gte("2018-01-01"));
        GroupOperation groupByDirector = Aggregation.group("director")
                .count().as("movies_count")
                .sum("revenue").as("total_revenue")
                .sum("budget").as("total_budget");
        SortOperation sortByCount = Aggregation.sort(Sort.by(Sort.Direction.DESC, "movies_count"));
        LimitOperation limitBy = Aggregation.limit(limit);
        Aggregation pipeline = Aggregation.newAggregation(match, groupByDirector, sortByCount, limitBy);
        return template.aggregate(pipeline, "imdb", Document.class).getMappedResults();
    }

}
