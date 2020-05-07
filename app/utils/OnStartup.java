package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import com.typesafe.config.Config;

import akka.actor.ActorSystem;
import play.Environment;
import play.Logger;
import play.libs.mailer.MailerClient;
import pojo.Constants;
import pojo.Event;
import pojo.EventManager;

@Singleton
public class OnStartup {

	@Inject
	MailerClient mailerClient;

	private Config config;

	private Environment environment;

	private ActorSystem actorSystem;

	private scala.concurrent.ExecutionContext executionContext;

	private Properties props;

	private static final Logger.ALogger LOGGER = Logger.of(OnStartup.class);

	@Inject
	public OnStartup(Environment environment, Config config, ActorSystem actorSystem,
			scala.concurrent.ExecutionContext executionContext)
			throws FileNotFoundException, IOException, InterruptedException {
		this.environment = environment;
		this.config = config;
		this.actorSystem = actorSystem;
		this.executionContext = executionContext;

		props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		// chronjob at 02:42 every day (-1, UTC)
		this.actorSystem.scheduler().schedule(
				scala.concurrent.duration.Duration.create(nextExecutionInSeconds(1, 42), TimeUnit.SECONDS), // initialDelay
				scala.concurrent.duration.Duration.create(1, TimeUnit.DAYS), // once a day
				() -> {

					if (!this.environment.isDev())
						sendNotificatonMail();

				}, this.executionContext);

		LOGGER.info("Memebrancer started!");

	}

	public static int nextExecutionInSeconds(int hour, int minute) {
		return Seconds.secondsBetween(new DateTime(), nextExecution(hour, minute)).getSeconds();
	}

	public static DateTime nextExecution(int hour, int minute) {
		DateTime next = new DateTime().withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(0)
				.withMillisOfSecond(0);

		return (next.isBeforeNow()) ? next.plusHours(24) : next;
	}

	private void sendNotificatonMail() {

		LOGGER.info("try to send memebrancer mail");
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(config.getString("gmailUsername"), config.getString("gmailPassword"));
			}
		});

		try {
			StringBuilder sb = new StringBuilder();

			// get todays events
			EventManager eventManager = new EventManager(config.getString("storageFilePath"));
			List<Event> bdaysToday = eventManager.getTodaysEvents(EventEnum.Birthday);
			LOGGER.info("bdays: " + bdaysToday);
			if (bdaysToday.size() > 0) {
				sb.append("Birthdays:\n");
				sb = createBeautyList(bdaysToday, sb);
				sb.append("\n");
			}

			List<Event> deadToday = eventManager.getTodaysEvents(EventEnum.Death);
			LOGGER.info("deads: " + deadToday);
			if (deadToday.size() > 0)
				sb.append("Deaths:\n");
			sb = createBeautyList(deadToday, sb);

			List<Event> kogelJubilee = eventManager.getKogelJubliees();
			LOGGER.info("kogelJubilee: " + kogelJubilee);
			if (kogelJubilee.size() > 0)
				sb.append("1000 days on earth:\n");
			sb = createBeautyListKogel(kogelJubilee, sb);

			List<Event> upcomingList = eventManager.getUpcommingEvents(1);
			LOGGER.info("upcomingList: " + upcomingList);
			if (upcomingList.size() > 0)
				sb.append("Upcoming:\n");
			sb = createBeautyListUpcomming(upcomingList, sb);

			sb.append("\nHave a nice day! (" + LocalDate.now().getDayOfYear() + ")");

			Message message = new MimeMessage(session);

			InternetAddress from = new InternetAddress(config.getString("gmailUsername"));
			from.setPersonal("Memebrancer Service");
			message.setFrom(from);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.getString("emailTo")));
			message.setSubject("Birthday's and others: " + (bdaysToday.size() + deadToday.size() + kogelJubilee.size())
					+ " - " + getBeautifyToday());
			message.setText(sb.toString());

			Transport.send(message);

			LOGGER.info("Mail sent!");

		} catch (Exception e) {
			LOGGER.error("exception: " + e.getMessage(), e);
		}

	}

	private StringBuilder createBeautyList(List<Event> eventList, StringBuilder sb) {
		for (Event event : eventList) {
			sb.append(" - " + event.getDisplayName(0) + "\n");
		}
		return sb;
	}

	private StringBuilder createBeautyListUpcomming(List<Event> eventList, StringBuilder sb) {
		for (Event event : eventList) {
			sb.append(" - " + event.getDay() + "." + (event.getMonth() + 1) + ". " + event.getDisplayName(0) + " ("
					+ event.getFriendlyType() + ")" + "\n");
		}
		return sb;
	}

	private StringBuilder createBeautyListKogel(List<Event> eventList, StringBuilder sb) {
		boolean hasEvent = false;
		for (Event event : eventList) {
			sb.append(" - " + event.getName() + " (" + (event.getDaysOnEarth() * -1) + ")\n");
			hasEvent = true;
		}

		if (hasEvent)
			sb.append("\n");
		return sb;
	}

	private String getBeautifyToday() {

		LocalDate localDate = LocalDate.now();// For reference
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMATGUI);
		String formattedString = localDate.format(formatter);
		return formattedString;

	}

}