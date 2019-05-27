package controllers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Result;

public class SonglogController extends Controller
{

  private final WSClient              ws;

  private static final Logger.ALogger LOGGER = Logger.of(SonglogController.class);

  @Inject
  SonglogController(
                    WSClient ws)
  {
    this.ws = ws;

  }

  public Result proxy(String stationId)
  {
    String url = "https://www.srf.ch/play/radio/songlog/";
    if (!stationId.contains("-"))
      url = url.replace("srf", "rts");
    LOGGER.debug("try to get songlog of station: " + url + stationId);

    WSRequest request = ws.url(url + stationId);
    WSRequest complexRequest = request.setRequestTimeout(Duration.of(6000, ChronoUnit.MILLIS));
    JsonNode returnBody = complexRequest.get().thenApply(response -> response.asJson()).toCompletableFuture().join();
    return ok(returnBody).withHeader("Access-Control-Allow-Origin", "*");
  }

}