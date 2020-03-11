package blog.quartz;

import blog.gmail.WebSendMail;
import blog.jsoup.*;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import blog.util.JxlsMakeExcel;
import blog.util.JxlsMakeExcelText;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.List;

public class SendEmail implements Job {


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        Volleyball volleyball = new Volleyball();
        Hockey hockey = new Hockey();
        Soccer soccer = new Soccer();
        Basketball basketball = new Basketball();
        Nba nba = new Nba();
        JxlsMakeExcel jxlsMakeExcel = new JxlsMakeExcel();
        JxlsMakeExcelText jxlsMakeExcelText = new JxlsMakeExcelText();
        SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

        try {

//            hockey.getAllMatch();
//            soccer.getAllMatch();
//            basketball.getAllMatch();
//            nba.getAllMatch();
//            volleyball.getAllMatch();
//

            hockey.updateHockeyStat();
            soccer.updateSoccerStat();
            basketball.updateBasketBall();
            nba.updateBasketBall();
            volleyball.updateVolleyBall();

            nba.getTomorrowMatch();
            basketball.getTomorrowMatch();
            basketball.getBasketBallSummary();

            hockey.getTomorrowMatch();
            hockey.getHockeySummary();


            jxlsMakeExcel.statXlsDown("basketball");
            jxlsMakeExcel.statXlsDown("volleyball");
            jxlsMakeExcel.statXlsDown("soccer");
            jxlsMakeExcel.statXlsDown("hockey");

            jxlsMakeExcelText.statXlsDown("basketball_summary");

            List<HashMap<String, Object>> memberList = setalarmDAO.selectMemberList();
            String[] recipients = new String[1];
//
            WebSendMail webSendMail = new WebSendMail();
//
            for (int i = 0 ; i < memberList.size() ; i++){
                recipients[0] = memberList.get(i).get("EMAIL").toString();
                System.out.println(recipients[0]);
                webSendMail.sendSSLMessage(recipients, "test", "test", "jungyongee@gmail.com");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

