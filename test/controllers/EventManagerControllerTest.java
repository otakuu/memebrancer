package controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import pojo.Constants;
import pojo.EventManager;

public class EventManagerControllerTest
{

  @Test
  public void testKogel()
  {

    EventManager eventManager = new EventManager("C:\\temp\\birthdays.txt");
    eventManager.getKogelJubliees();
  }

  @Test
  public void testUpcomming()
  {

    EventManager eventManager = new EventManager("C:\\temp\\birthdays.txt");
    eventManager.getUpcommingEvents();
  }

  @Test
  public void testDate()
  {
    LocalDate localDate = LocalDate.now();// For reference
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMATGUI);
    String formattedString = localDate.format(formatter);
    System.out.println(formattedString);
  }
}
