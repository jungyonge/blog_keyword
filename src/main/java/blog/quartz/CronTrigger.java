package blog.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


public class CronTrigger {
    private SchedulerFactory schedulerFactory;
    private Scheduler scheduler;

    public void start() throws SchedulerException {

        schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();

        //job 지정
//        JobDetail job = JobBuilder.newJob(SendEmail.class).withIdentity("SendEmail").build();
        JobDetail job1 = JobBuilder.newJob(BlogPost.class).withIdentity("BlogPost").build();

        //trigger 생성
//        Trigger trigger = TriggerBuilder.newTrigger().
//                withSchedule(CronScheduleBuilder.cronSchedule("0 00 22 * * ?")).build();

        Trigger trigger1 = TriggerBuilder.newTrigger().
                withSchedule(CronScheduleBuilder.cronSchedule("* 0/5 * * * ?")).build();

//        startAt과 endAt을 사용해 job 스케쥴의 시작, 종료 시간도 지정할 수 있다.
//        Trigger trigger = TriggerBuilder.newTrigger().startAt(startDateTime).endAt(EndDateTime)
//                .withSchedule(CronScheduleBuilder.cronSchedule("*/1 * * * *")).build();
//        scheduler.scheduleJob(job, trigger);
        scheduler.scheduleJob(job1, trigger1);

    }
}



