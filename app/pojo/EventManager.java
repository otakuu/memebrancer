package pojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.Logger;
import utils.EventEnum;

public class EventManager
{

  public List<Event>           eventList = new ArrayList<Event>();

  private String               filePath  = null;

  private final Logger.ALogger LOGGER    = Logger.of(EventManager.class);

  public EventManager(
                      String filePath)
  {
    this.filePath = filePath;
    loadFromFile();
  }

  // iterate file and add to list
  public void loadFromFile()
  {

    BufferedReader br = null;
    String line = "";

    try
    {

      br = new BufferedReader(new FileReader(filePath));
      int lineNr = 1;
      while ((line = br.readLine()) != null)
      {

        LOGGER.debug(line);
        try
        {
          Event event = createEventFromStr(lineNr, line);
          eventList.add(event);
        }
        catch (Exception ex)
        {
          LOGGER.error("error during parsing file: " + ex.getMessage(), ex);
        }
        lineNr++;
      }
      LOGGER.info("File parsed, Nbr of lines: " + (lineNr - 1));
      LOGGER.debug("" + eventList);

    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (br != null)
      {
        try
        {
          br.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }

  }

  public Event createEventFromStr(Integer lineNr, String line) throws ParseException
  {
    String[] event = line.split(Constants.SEPARATOR);
    LOGGER.debug("Event [date= " + event[0] + ", type=" + event[1] + ", name=" + event[2] + "]");
    DateFormat format = new SimpleDateFormat(Constants.DATEFORMAT);
    Date date = format.parse(event[0]);
    return new Event(lineNr, event[2], date, Integer.parseInt(event[1]));
  }

  public Event updateEventFromStr(String line) throws ParseException
  {
    String[] event = line.split(Constants.SEPARATOR);
    LOGGER.info("Event [date= " + event[1] + ", type=" + event[2] + ", name=" + event[3] + "]");
    DateFormat format = new SimpleDateFormat(Constants.DATEFORMATGUI);
    Date date = format.parse(event[1]);
    return new Event(Integer.parseInt(event[0]), event[3], date, Integer.parseInt(event[2]));
  }

  public void createEvent(String newLine)
  {

    LOGGER.info("Creating new event");
    LOGGER.debug("" + newLine);
    try
    {
      Event event = updateEventFromStr(newLine);
      newLine = event.toStoreString() + "\n";
      Files.write(Paths.get(filePath), newLine.getBytes(), StandardOpenOption.APPEND);
      eventList.add(event);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (ParseException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void deleteEventByLineNr(Integer lineNr)
  {

    LOGGER.info("Deleting event: " + lineNr);
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(filePath));

      // String buffer to store contents of the file
      StringBuffer sb = new StringBuffer("");

      // Keep track of the line number
      int linenumber = 1;
      String line;

      while ((line = br.readLine()) != null)
      {
        // Store each valid line in the string buffer
        if (linenumber < lineNr || linenumber >= lineNr + 1)
          sb.append(line + "\n");
        linenumber++;
      }
      if (lineNr + 1 > linenumber)
        System.out.println("End of file reached.");
      br.close();

      FileWriter fw = new FileWriter(new File(filePath));
      // Write entire string buffer into the file
      fw.write(sb.toString());
      fw.close();
      eventList.remove(linenumber);
      LOGGER.info("Event deleted");
    }
    catch (Exception e)
    {
      System.out.println("Something went horribly wrong: " + e.getMessage());
    }
  }

  public void updateEvent(String eventAsStr)
  {

    LOGGER.info("Updating event: " + eventAsStr);
    try
    {
      Event event = updateEventFromStr(eventAsStr);
      LOGGER.info(event.toString());
      BufferedReader br = new BufferedReader(new FileReader(filePath));

      // String buffer to store contents of the file
      StringBuffer sb = new StringBuffer("");

      // Keep track of the line number
      int linenumber = 1;
      String line;

      while ((line = br.readLine()) != null)
      {
        // Store each valid line in the string buffer
        if (linenumber < event.getLineNr() || linenumber >= event.getLineNr() + 1)
        {
          sb.append(line + "\n");
        }
        else
        {
          sb.append(event.toStoreString() + "\n");
        }
        linenumber++;
      }
      br.close();

      FileWriter fw = new FileWriter(new File(filePath));
      // Write entire string buffer into the file
      fw.write(sb.toString());
      fw.close();
      eventList.set(event.getLineNr() - 1, event);
      LOGGER.info("Event updated");
    }
    catch (Exception e)
    {
      LOGGER.error("Something went horribly wrong: " + e.getMessage(), e);
    }
  }

  public List<Event> getEventList()
  {
    return eventList;
  }

  public List<Event> getTodaysEvents(EventEnum eventType)
  {
    List<Event> _eventList = new ArrayList<Event>();
    for (Event event : eventList)
    {
      if (event.getMonth() == new Date().getMonth() && event.getDay() == new Date().getDate() && eventType.getValue() == event.getType())
        _eventList.add(event);
    }
    return _eventList;

  }

  public Event getEventById(Integer eventId)
  {
    for (Event event : eventList)
    {
      if (event.getLineNr() == eventId)
        return event;
    }
    return null;
  }

}
