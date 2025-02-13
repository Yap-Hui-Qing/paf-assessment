package vttp.batch5.paf.movies.repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MySQLMovieRepository {

  @Autowired
  private JdbcTemplate template;

  public static final String SQL_INSERT_MOVIES = "insert into imdb values (?, ?, ?, ?, ?, ?, ?)";

  // TODO: Task 2.3
  // You can add any number of parameters and return any type from the method
  public int[][] batchInsertMovies(List<Document> documents, int batchSize){
    int[][] updateCounts = template.batchUpdate(SQL_INSERT_MOVIES, documents, batchSize,
        new ParameterizedPreparedStatementSetter<Document>() {
          @Override
          public void setValues(PreparedStatement ps, Document doc)
              throws SQLException {
            ps.setString(1, doc.getString("imdb_id"));
            ps.setFloat(2, doc.get("vote_average", Number.class).floatValue());
            ps.setInt(3, doc.getInteger("vote_count"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date release_date;
            try {
              release_date = sdf.parse(doc.getString("release_date"));
              ps.setDate(4, new java.sql.Date(release_date.getTime()));
            } catch (ParseException e) {
              e.printStackTrace();
            }
            ps.setFloat(5, doc.get("revenue", Number.class).floatValue());
            ps.setFloat(6, doc.get("budget", Number.class).floatValue());
            ps.setInt(7, doc.getInteger("runtime"));
          }

        });
    return updateCounts;

  }

  // TODO: Task 3

}
