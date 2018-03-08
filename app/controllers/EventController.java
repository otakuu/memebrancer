package controllers;

import com.google.inject.Inject;
import com.typesafe.config.Config;

import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import pojo.Event;
import pojo.EventManager;

public class EventController extends Controller
{

  @Inject
  FormFactory                         formFactory;

  private Config                      config;

  private EventManager                eventManager;

  private static final Logger.ALogger LOGGER = Logger.of(EventController.class);

  @Inject
  EventController(
                  Config config)
  {
    this.config = config;
  }

  public Result index()
  {
    LOGGER.info("Storage File Path: " + config.getString("storageFilePath"));
    eventManager = new EventManager(config.getString("storageFilePath"));
    return ok(views.html.index.render(eventManager.getEventList()));
  }

  public Result create(String eventAsStr)
  {
    LOGGER.info("Creating event: " + eventAsStr);
    int lineNr = eventManager.createEvent(eventAsStr);
    return ok("" + lineNr);
  }

  public Result update(String eventAsStr)
  {
    LOGGER.info("Updating event: " + eventAsStr);
    eventManager.updateEvent(eventAsStr);
    return ok("ok");
  }

  public Result delete(Integer eventId)
  {
    LOGGER.info("Deleting event: " + eventId);
    eventManager.deleteEventByLineNr(eventId);
    return ok("ok");
  }

  public Result getEventById(Integer eventId)
  {
    LOGGER.info("Get event: " + eventId);
    Event event = eventManager.getEventById(eventId);
    return ok(Json.toJson(event));
  }
}