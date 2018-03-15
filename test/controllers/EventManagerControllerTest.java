package controllers;

import org.junit.Test;

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
}
