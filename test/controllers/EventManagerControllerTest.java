package controllers;

import org.junit.Test;

import pojo.EventManager;

public class EventManagerControllerTest
{

  @Test
  public void testIndex()
  {

    EventManager eventManager = new EventManager("C:\\temp\\birthdays.txt");
    eventManager.getKogelJubliees();
  }

}
