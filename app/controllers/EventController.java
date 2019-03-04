package controllers;

import java.util.List;

import com.google.inject.Inject;
import com.typesafe.config.Config;

import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import pojo.Event;
import pojo.EventManager;
import utils.EventEnum;

public class EventController extends Controller {

	@Inject
	FormFactory formFactory;

	private Config config;

	private EventManager eventManager;

	private static final Logger.ALogger LOGGER = Logger.of(EventController.class);

	@Inject
	EventController(Config config) {
		this.config = config;
	}

	public Result index() {
		LOGGER.info("Storage File Path: " + config.getString("storageFilePath"));
		eventManager = new EventManager(config.getString("storageFilePath"));
		List<Event> eventsToday = eventManager.getTodaysEvents(EventEnum.Birthday);
		eventsToday.addAll(eventManager.getTodaysEvents(EventEnum.Death));

		return ok(views.html.index.render(eventManager.getEventList(), eventsToday.size()));
	}

	public Result create(String eventAsStr) {
		LOGGER.info("Creating event: " + eventAsStr);
		int lineNr = eventManager.createEvent(eventAsStr);
		return ok("" + lineNr);
	}

	public Result update(String eventAsStr) {
		LOGGER.info("Updating event: " + eventAsStr);
		eventManager.updateEvent(eventAsStr);
		return ok("ok");
	}

	public Result delete(Integer eventId) {
		LOGGER.info("Deleting event: " + eventId);
		eventManager.deleteEventByLineNr(eventId);
		return ok("ok");
	}

	public Result getEventById(Integer eventId) {
		LOGGER.info("Get event: " + eventId);
		Event event = eventManager.getEventById(eventId);
		return ok(Json.toJson(event));
	}

	public Result todaysBirthdays() {
		eventManager = new EventManager(config.getString("storageFilePath"));
		List<Event> events = eventManager.getTodaysEvents(EventEnum.Birthday);
		return ok(Json.toJson(events)).withHeader("Access-Control-Allow-Origin", "*");
	}

	public Result todaysDeaths() {
		eventManager = new EventManager(config.getString("storageFilePath"));
		List<Event> events = eventManager.getTodaysEvents(EventEnum.Death);
		return ok(Json.toJson(events)).withHeader("Access-Control-Allow-Origin", "*");
	}

	public Result todaysKogelJubilee() {
		eventManager = new EventManager(config.getString("storageFilePath"));
		List<Event> events = eventManager.getKogelJubliees();
		return ok(Json.toJson(events)).withHeader("Access-Control-Allow-Origin", "*");
	}

	public Result upcoming() {
		eventManager = new EventManager(config.getString("storageFilePath"));
		List<Event> events = eventManager.getUpcommingEvents();
		return ok(Json.toJson(events)).withHeader("Access-Control-Allow-Origin", "*");
	}

}