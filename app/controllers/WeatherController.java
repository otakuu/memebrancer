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

public class WeatherController extends Controller {

	private final WSClient ws;

	private static final Logger.ALogger LOGGER = Logger.of(WeatherController.class);

	@Inject
	WeatherController(WSClient ws) {
		this.ws = ws;

	}

	public Result forecast() {

		WSRequest request = ws.url("https://app-prod-ws.meteoswiss-app.ch/v1/plzDetail?plz=450000");
		WSRequest complexRequest = request.setRequestTimeout(Duration.of(6000, ChronoUnit.MILLIS));
		JsonNode returnBody = complexRequest.get().thenApply(response -> response.asJson()).toCompletableFuture()
				.join();
		return ok(returnBody).withHeader("Access-Control-Allow-Origin", "*");
	}

	public Result temperature() {

		WSRequest request = ws.url(
				"https://www.meteoschweiz.admin.ch/product/input/measured-values/messwerte-lufttemperatur-10min/stationsTable/stationsTable.messwerte-lufttemperatur-10min.de.json");
		WSRequest complexRequest = request.setRequestTimeout(Duration.of(6000, ChronoUnit.MILLIS));
		JsonNode returnBody = complexRequest.get().thenApply(response -> response.asJson()).toCompletableFuture()
				.join();
		return ok(returnBody).withHeader("Access-Control-Allow-Origin", "*");
	}

}