package pojo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class Event
{

  private Integer lineNr;

  private String  name;

  private Date    date;

  private Integer type;

  public Event(
               Integer lineNr,
               String name,
               Date date,
               Integer type)
  {
    super();
    this.lineNr = lineNr;
    this.name = name;
    this.date = date;
    this.type = type;
  }

  public Integer getLineNr()
  {
    return lineNr;
  }

  public void setLineNr(Integer lineNr)
  {
    this.lineNr = lineNr;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Date getDate()
  {
    return date;
  }

  public void setDate(Date date)
  {
    this.date = date;
  }

  public Integer getType()
  {
    return type;
  }

  public void setType(Integer type)
  {
    this.type = type;
  }

  @Override
  public String toString()
  {
    return "Event [lineNr=" + lineNr + ", name=" + name + ", date=" + date + ", type=" + type + "]";
  }

  public String toStoreString()
  {
    DateFormat df = new SimpleDateFormat(Constants.DATEFORMAT);
    String dateAsStr = df.format(date);
    return dateAsStr + Constants.SEPARATOR + type + Constants.SEPARATOR + name;
  }

  public String toGuiDate()
  {
    DateFormat df = new SimpleDateFormat("yyyy");
    String dateAsStr = df.format(date);
    return dateAsStr + ";" + this.type;
  }

  public String getDisplayName(int plusYear)
  {
    return name + ": " + getYear() + " (" + (diff() + plusYear) + ")";
  }

  public Integer getMonth()
  {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() - 1;
  }

  public Integer getDay()
  {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth();
  }

  public Integer getYear()
  {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
  }

  public Integer getMonthAndDay()
  {
    return Integer.parseInt(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() + ""
        + String.format("%02d", date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth()));
  }

  public Integer diff()
  {
    return Calendar.getInstance().get(Calendar.YEAR) - date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
  }

  public long getDaysOnEarth()
  {
    LocalDate today = LocalDate.now();
    long daysOnEarth = java.time.temporal.ChronoUnit.DAYS.between(today, this.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    return daysOnEarth;
  }

  public String getFriendlyType()
  {
    if (type == 1)
      return "Birthday";

    return "Death";
  }

  public Integer getAge()
  {
    return Calendar.getInstance().get(Calendar.YEAR) - date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
  }

}
