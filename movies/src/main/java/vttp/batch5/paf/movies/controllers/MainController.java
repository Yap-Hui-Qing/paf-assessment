package vttp.batch5.paf.movies.controllers;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import net.sf.jasperreports.engine.JRException;
import vttp.batch5.paf.movies.services.MovieService;

@Controller
@RequestMapping
public class MainController {

  @Autowired
  private MovieService movieSvc;

  // TODO: Task 3
  @GetMapping(path = "/api/summary", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getMoviePerformance(@RequestParam int count) {
    JsonArray results = movieSvc.getProlificDirectors(count);
    return ResponseEntity.ok(results.toString());
  }

  // TODO: Task 4
  @GetMapping(path = "/api/summary/pdf", produces=MediaType.TEXT_HTML_VALUE)
  public void generatePDFReport(@RequestParam int count) throws FileNotFoundException, JRException{
    movieSvc.generatePDFReport(count);
  }
}
