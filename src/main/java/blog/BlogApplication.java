package blog;

import blog.quartz.CronTrigger;
import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
/*@EnableScheduling*/
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class}) //datasource에 대한 자동설정을 막음 -> replication 설정
public class BlogApplication {

    public static void main(String[] args) {

        CronTrigger cronTrigger = new CronTrigger();
        System.out.println("트리거실행");
        try {
            cronTrigger.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        SpringApplication.run(BlogApplication.class, args);
    }


}