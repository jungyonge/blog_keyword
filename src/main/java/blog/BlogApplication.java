package blog;

import blog.quartz.CronTrigger;
import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@SpringBootApplication
/*@EnableScheduling*/
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class}) //datasource에 대한 자동설정을 막음 -> replication 설정
public class BlogApplication extends TelegramLongPollingBot {

    public static void main(String[] args) {

        CronTrigger cronTrigger = new CronTrigger();
        System.out.println("트리거실행");
        try {
            cronTrigger.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        BlogApplication telegramBot =  new BlogApplication();

        try {
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


        SpringApplication.run(BlogApplication.class, args);
    }


    @Override
    public void onUpdateReceived(Update arg0) {
        int getId = arg0.getMessage().getFrom().getId(); // userid
        String getFirstName = arg0.getMessage().getFrom().getFirstName(); //보낸사람 이름
        String getLastName = arg0.getMessage().getFrom().getLastName(); //보낸사람 성
        long getChatId = arg0.getMessage().getChatId();  // 채팅방의 ID
        String getText = arg0.getMessage().getText();  // 받은 TEXT
        long getmsgid = arg0.getMessage().getMessageId();

    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {

    }

    @Override
    public String getBotUsername() {
        return "nintendoAnimal_bot";
    }

    @Override
    public String getBotToken() {
        return "992109721:AAHNgtqq3o7GrVQ6_dvZvxeZzOkra52VzjU";
    }
}