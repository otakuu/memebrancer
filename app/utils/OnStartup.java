package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import com.typesafe.config.Config;

import akka.actor.ActorSystem;
import play.Logger;
import play.libs.mailer.MailerClient;
import pojo.Event;
import pojo.EventManager;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

@Singleton
public class OnStartup
{

  @Inject
  MailerClient                        mailerClient;

  private Config                      config;

  private ActorSystem                 actorSystem;

  private ExecutionContext            executionContext;

  private Properties                  props;

  private static final Logger.ALogger LOGGER = Logger.of(OnStartup.class);

  @Inject
  public OnStartup(
                   Config config,
                   ActorSystem actorSystem,
                   ExecutionContext executionContext) throws FileNotFoundException, IOException, InterruptedException
  {

    this.config = config;
    this.actorSystem = actorSystem;
    this.executionContext = executionContext;

    props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    // chronjob at 02:42 every day
    this.actorSystem.scheduler().schedule(Duration.create(nextExecutionInSeconds(1, 42), TimeUnit.SECONDS), // initialDelay
        Duration.create(1, TimeUnit.DAYS), // once a day
        () -> {

          sendNotificatonMail();

        }, this.executionContext);

    LOGGER.info("Membrancer started!");

  }

  public static int nextExecutionInSeconds(int hour, int minute)
  {
    return Seconds.secondsBetween(new DateTime(), nextExecution(hour, minute)).getSeconds();
  }

  public static DateTime nextExecution(int hour, int minute)
  {
    DateTime next = new DateTime().withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(0).withMillisOfSecond(0);

    return (next.isBeforeNow()) ? next.plusHours(24) : next;
  }

  private void sendNotificatonMail()
  {

    Session session = Session.getInstance(props, new javax.mail.Authenticator()
    {
      protected PasswordAuthentication getPasswordAuthentication()
      {
        return new PasswordAuthentication(config.getString("gmailUsername"), config.getString("gmailPassword"));
      }
    });

    try
    {
      StringBuilder sb = new StringBuilder();

      // get todays events
      EventManager eventManager = new EventManager(config.getString("storageFilePath"));
      List<Event> bdaysToday = eventManager.getTodaysEvents(EventEnum.Birthday);
      LOGGER.info("bdays: " + bdaysToday);
      if (bdaysToday.size() > 0)
      {
        sb.append("Birthdays:\n");
        sb = createBeautyList(bdaysToday, sb);
        sb.append("\n");
      }

      List<Event> deadToday = eventManager.getTodaysEvents(EventEnum.Death);
      LOGGER.info("deads: " + deadToday);
      if (deadToday.size() > 0)
        sb.append("Deaths:\n");
      sb = createBeautyList(deadToday, sb);

      List<Event> upcomingLlist = eventManager.getUpcommingEvents();
      LOGGER.info("upcomingLlist: " + upcomingLlist);
      if (upcomingLlist.size() > 0)
        sb.append("Upcomming:\n");
      sb = createBeautyListUpcomming(upcomingLlist, sb);

      sb.append("\nHave a nice day!");

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress("sz2pdf@gmail.com"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.getString("emailTo")));
      message.setSubject("Birthday's and others: " + (bdaysToday.size() + deadToday.size()));
      message.setText(sb.toString());

      Transport.send(message);

      LOGGER.info("Mail sent!");

    }
    catch (MessagingException e)
    {
      throw new RuntimeException(e);
    }

  }

  private StringBuilder createBeautyList(List<Event> eventList, StringBuilder sb)
  {
    for (Event event : eventList)
    {
      sb.append(" - " + event.getDisplayName() + "\n");
    }

    return sb;
  }

  private StringBuilder createBeautyListUpcomming(List<Event> eventList, StringBuilder sb)
  {
    for (Event event : eventList)
    {
      sb.append(" - " + event.getDay() + "." + event.getMonth() + ". : " + event.getDisplayName() + "\n");
    }

    return sb;
  }

}