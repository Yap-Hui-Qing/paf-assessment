package vttp.batch5.paf.movies.bootstrap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;

@Component
public class Dataloader implements CommandLineRunner {

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private MongoMovieRepository mongoRepo;

  @Value("${zip.name}")
  private String zipName;

  public static final String SQL_COUNT_RECORDS = "select count(*) from imdb";
  private final Logger logger = Logger.getLogger(Dataloader.class.getName());
  // // zip data file should be configurable --> need to put in properties
  // String zipName = "C:\\Users\\Hui Qing\\Visa\\Persistence and Analytics Fundamentals\\paf_b5_assessment_template\\data\\movies_post_2010.zip";

  // TODO: Task 2

  // task 2.1
  // check if data has been loaded into the database
  // Create connection and statement
  public Boolean checkLoaded() {
    SqlRowSet rs = template.queryForRowSet(SQL_COUNT_RECORDS);
    rs.next();
    boolean exists = rs.getInt("COUNT(*)") > 0;
    return exists;
  }

  // task 2.2
  // read the json document from the zip file
  public void readJson() {
    try {
      FileInputStream fis = new FileInputStream(zipName);
      ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;

      // Read each entry from the ZipInputStream until no
      // more entry found indicated by a null return value
      // of the getNextEntry() method.
      while ((entry = zis.getNextEntry()) != null) {
        System.out.println("Unzipping: " + entry.getName());

        int size;
        byte[] buffer = new byte[2048];

        FileOutputStream fos = new FileOutputStream(entry.getName());
        BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);

        while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
          bos.write(buffer, 0, size);
        }
        bos.flush();
        bos.close();
      }
      zis.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  // filter movies after 2018
  // impute and missing/erroenous attribute
  public List<Document> processJson() {
    // try with dummy date first 
    Path p = Paths.get("movies_post_2010.json");
    // Path p = Paths.get("movies_dummy.json");
    List<Document> documents = new LinkedList<>();
    try (Reader r = new FileReader(p.toFile())) {
      BufferedReader br = new BufferedReader(r);
      String line;
      while ((line = br.readLine()) != null) {
        JsonReader jsonReader = Json.createReader(new StringReader(line));
        JsonObject j = jsonReader.readObject();
        // System.out.printf(">>%s", Document.parse(j.toString()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date release_date = sdf.parse(j.getString("release_date"));
        Date target = sdf.parse("2018-01-01");
        if (release_date.after(target)) {
          Document d = Document.parse(j.toString());
          for (String key : d.keySet()){
            if (d.get(key) == null || d.get(key) == ""){
              if (key.equals("vote_average") || key.equals("vote_count") || key.equals("revenue") || key.equals("runtime") || key.equals("budget") || key.equals("popularity") || key.equals("imdb_rating") || key.equals("imdb_votes")){
                d.replace(key, 0);
              } else{
                d.replace(key, "");
              }
            }
          }
          documents.add(d);
        }
      }
      return documents;

    } catch (Exception ex) {
      ex.printStackTrace();
      return documents;
    }
  }

  @Override
  public void run(String... args) {
    if (checkLoaded()) {
      logger.info("Data has been loaded");
      return;
    }
    readJson();
    List<Document> documents = processJson();

    int count = 0;
    List<Document> batch = new LinkedList<>();
    List<List<Document>> batchDocuments = new LinkedList<>();
    // split into batches of 25
    for (Document d : documents){
      batch.add(d);
      count += 1;
      if (count > 25){
        batchDocuments.add(batch);
        batch = new LinkedList<>();
      }
    }

    // test queries
    // List<Document> results = mongoRepo.getMoviePerformance(5);
    // for (Document d : results){
    //   System.out.printf(">> %s", d);
    // }

  }
}
