package vttp.batch5.paf.movies.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.json.data.JsonDataSource;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.SimplePdfExporterConfiguration;
import net.sf.jasperreports.pdf.SimplePdfReportConfiguration;
import vttp.batch5.paf.movies.models.Performance;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;

import static vttp.batch5.paf.movies.models.Performance.*;

@Service
public class MovieService {

  @Value("${report.name}")
  private String user;
  @Value("${report.batch}")
  private String batch;

  @Autowired
  private MongoMovieRepository movieRepo;

  @Autowired
  private MySQLMovieRepository moviesqlRepo;

  // TODO: Task 2
  @Transactional(rollbackFor = Exception.class)
  public void insertMovies(List<List<Document>> mongodoc, List<Document> sqldoc){
    try{
      movieRepo.batchInsertMovies(mongodoc);
      moviesqlRepo.batchInsertMovies(sqldoc, 25);
    } catch(Exception ex){
      movieRepo.logError();
    }
  }

  // TODO: Task 3
  // You may change the signature of this method by passing any number of
  // parameters
  // and returning any type
  public JsonArray getProlificDirectors(int limit) {
    List<Document> results = movieRepo.getMoviePerformance(limit);
    JsonArrayBuilder builder = Json.createArrayBuilder();
    for (Document d : results) {
      builder.add(toJson(d));
    }
    return builder.build();
  }

  // TODO: Task 4
  // You may change the signature of this method by passing any number of
  // parameters
  // and returning any type
  public void generatePDFReport(int limit) throws FileNotFoundException, JRException {
    JsonObject jsonUser = Json.createObjectBuilder()
      .add("name", user)
      .build();
    JsonObject jsonBatch = Json.createObjectBuilder()
      .add("batch", batch)
      .build();

    JsonDataSource reportDS = new JsonDataSource(jsonUser.toString(), jsonBatch.toString());
    JsonDataSource directorDS = new JsonDataSource((InputStream) getProlificDirectors(limit));

    Map<String, Object> params = new HashMap<>();
    params.put("DIRECTOR_TABLE_DATASET", directorDS);

    InputStream moviesReportStream = getClass().getResourceAsStream("/director_movies_report.jrxml");
    JasperReport jasperReport = JasperCompileManager.compileReport(moviesReportStream);

    JasperPrint print = JasperFillManager.fillReport(jasperReport, params, reportDS);
    
    JRPdfExporter exporter = new JRPdfExporter();
    exporter.setExporterInput(new SimpleExporterInput(print));
    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("moviesReport.pdf"));
    SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
    reportConfig.setSizePageToContent(true);
    reportConfig.setForceLineBreakPolicy(false);
    SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
    exportConfig.setMetadataAuthor(user);
    exportConfig.setEncrypted(true);
    exportConfig.setAllowedPermissionsHint("PRINTING");
    exporter.setConfiguration(reportConfig);
    exporter.setConfiguration(exportConfig);
    exporter.exportReport();




  }

}
