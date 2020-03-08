package blog.jsoup;

import blog.gmail.WebSendMail;
import blog.model.VolleyballModel;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import blog.util.JxlsMakeExcel;
import blog.util.JxlsMakeExcelText;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Volleyball {


    private SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    // URLConnection 연결로 데이터 호출
    public String requestURLToString(String url) throws IOException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        con.setRequestProperty("referer", "https://livescore.co.kr/");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setRequestProperty("Content-Type", "text/html;charset=UTF-8");

        con.setConnectTimeout(2000);

        StringBuffer sInputData = new StringBuffer(1024);
        String sInputLine = "";
        BufferedReader in;

        in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));


        while ((sInputLine = in.readLine()) != null) {
            sInputData.append(sInputLine).append("\n");
        }
        in.close();

        return sInputData.toString();

    }

    public void getAllMatch() throws Exception{

        JSONArray jsonArray = new JSONArray();

        String rootHtml = "";
        String url = "https://livescore.co.kr/sports/score_board/volley/view.php?date=";
        int date = 0;

        while (true){

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(2019, 9,11);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            cal.add(Calendar.DATE, date);
            System.out.println("after: " + df.format(cal.getTime()));

            //2019년 10월 12일 (토) ~ 2020년 4월 5일 (일)
           if(df.format(cal.getTime()).equals("2020-04-06")){
                System.out.println("시즌끝");
                break;
            }

            int dayNum = cal.get(Calendar.DAY_OF_WEEK);
            String dayOfWeek = getDayoOfWeek(dayNum);

            System.out.println(url + df.format(cal.getTime()));
            rootHtml = requestURLToString(url + df.format(cal.getTime()));

            Document rootDoc = Jsoup.parse(rootHtml);
            Elements elements = rootDoc.select("div#score_board div.score_tbl_individual");

            for (Element element : rootDoc.select("div#score_board div.score_tbl_individual")) {
                VolleyballModel aTeamStat = new VolleyballModel();
                VolleyballModel bTeamStat = new VolleyballModel();

                int i = 0;

                String league = element.select("thead tr th.reague").text();

                if (league.equals("V-리그")) {

                    String gameId = element.select("div.score_tbl_individual").attr("id");

                    aTeamStat.setGameId(gameId);
                    bTeamStat.setGameId(gameId);
                    aTeamStat.setDayOfWeek(dayOfWeek);
                    bTeamStat.setDayOfWeek(dayOfWeek);




                    aTeamStat.setTime(element.select("thead tr th.ptime").text().replaceAll("오전 ", "").replaceAll("오후 ", ""));
                    bTeamStat.setTime(element.select("thead tr th.ptime").text().replaceAll("오전 ", "").replaceAll("오후 ", ""));

                    aTeamStat.setDate(df.format(cal.getTime()));
                    bTeamStat.setDate(df.format(cal.getTime()));

                    aTeamStat.setGround("홈");
                    bTeamStat.setGround("원정");

                    aTeamStat.setBTeam(element.select("tbody tr > td.teaminfo.visitor strong").text());
                    aTeamStat.setATeam(element.select("tbody tr > td.teaminfo.hometeam strong").text());
                    bTeamStat.setATeam(element.select("tbody tr > td.teaminfo.visitor strong").text());
                    bTeamStat.setBTeam(element.select("tbody tr > td.teaminfo.hometeam strong").text());
                    aTeamStat.setLeague(getDivision(aTeamStat.getBTeam()));
                    bTeamStat.setLeague(getDivision(aTeamStat.getBTeam()));

                }

                if(aTeamStat.getGameId() == null|| bTeamStat.getGameId() == null){
                    continue;
                } else {
                    System.out.println(aTeamStat);
                    System.out.println(bTeamStat);
                    setalarmDAO.insertVolleyMatch(aTeamStat);
                    setalarmDAO.insertVolleyMatch(bTeamStat);
                }

            }

            date++;
        }

    }

    public void updateVolleyBall() throws IOException, ParseException, InterruptedException {
        JSONArray jsonArray = new JSONArray();


        Calendar curDate = Calendar.getInstance();
        curDate.setTime(new Date());
        curDate.add(Calendar.DATE, 1);


        String rootHtml = "";
        String url = "https://livescore.co.kr/sports/score_board/volley/view.php?date=";
        int date = 0;

        while (true){
            Calendar startDate = Calendar.getInstance();

//            startDate.set(2019, 9,11);
            startDate.setTime(new Date());
            startDate.add(Calendar.DATE, -5);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            startDate.add(Calendar.DATE, date);
            System.out.println("after: " + df.format(startDate.getTime()));
            int dayNum = startDate.get(Calendar.DAY_OF_WEEK);
            String dayOfWeek = getDayoOfWeek(dayNum);

            if(df.format(startDate.getTime()).equals(df.format(curDate.getTime()))){
                System.out.println("Today");
                break;
            }

            rootHtml = requestURLToString(url + df.format(startDate.getTime()));
            System.out.println(url + df.format(startDate.getTime()));
//            Thread.sleep(500);

            Document rootDoc = Jsoup.parse(rootHtml);
            Elements elements = rootDoc.select("div#score_board div.score_tbl_individual");
            for (Element element : rootDoc.select("div#score_board div.score_tbl_individual")) {
                int i = 0;
                VolleyballModel aTeamStat = new VolleyballModel();
                VolleyballModel bTeamStat = new VolleyballModel();

                String league = element.select("thead tr th.reague").text();
                if (league.equals("V-리그")) {
//                if ("승리") {
                    String gameId = element.select("div.score_tbl_individual").attr("id");
                    aTeamStat.setGameId(gameId);
                    bTeamStat.setGameId(gameId);
                    aTeamStat.setDayOfWeek(dayOfWeek);
                    bTeamStat.setDayOfWeek(dayOfWeek);


                    aTeamStat.setTime(element.select("thead tr th.ptime").text().replaceAll("오전 ", "").replaceAll("오후 ", ""));
                    aTeamStat.setDate(df.format(startDate.getTime()));
                    bTeamStat.setDate(df.format(startDate.getTime()));
                    aTeamStat.setGround("홈");
                    bTeamStat.setGround("원정");
                    aTeamStat.setBTeam(element.select("tbody tr > td.teaminfo.visitor strong").text());
                    aTeamStat.setATeam(element.select("tbody tr > td.teaminfo.hometeam strong").text());

                    aTeamStat.setLeague(getDivision(aTeamStat.getBTeam()));

                    String[] arrayHandi = element.select("tbody > tr > td.line").text().split(" ");

                    if (arrayHandi.length > 1) {
                        aTeamStat.setPointLine(Double.valueOf(arrayHandi[0]));
                        aTeamStat.setHandiCap(Double.valueOf(arrayHandi[1]));
                    } else {
                        aTeamStat.setPointLine(0.0);
                        aTeamStat.setHandiCap(0.0);
                    }

                    if(aTeamStat.getHandiCap() > 0){
                        aTeamStat.setOdd("역배");
                        bTeamStat.setOdd("정배");
                    } else if (aTeamStat.getHandiCap() < 0){
                        aTeamStat.setOdd("정배");
                        bTeamStat.setOdd("역배");
                    } else {
                        aTeamStat.setOdd("없음");
                        bTeamStat.setOdd("없음");
                    }


                    String[] arrayFirstScore = element.select("tbody > tr > td.s").text().split(" ");

                    if (arrayFirstScore.length == 8) {
                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setBTeamSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        aTeamStat.setBTeamThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        aTeamStat.setBTeamFourthQPoint(0);
                        aTeamStat.setBTeamFifthQPoint(0);

                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setATeamSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setATeamThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setATeamFourthQPoint(0);
                        aTeamStat.setATeamFifthQPoint(0);

                        bTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setATeamSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setATeamThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setATeamFourthQPoint(0);
                        bTeamStat.setATeamFifthQPoint(0);

                        bTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        bTeamStat.setBTeamSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        bTeamStat.setBTeamThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        bTeamStat.setBTeamFourthQPoint(0);
                        bTeamStat.setBTeamFifthQPoint(0);

                        bTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setFourthQPoint(0);
                        bTeamStat.setFifthQPoint(0);

                        aTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setFourthQPoint(0);
                        aTeamStat.setFifthQPoint(0);


                    }
                    else if(arrayFirstScore.length == 9){
                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setBTeamSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        aTeamStat.setBTeamThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        aTeamStat.setBTeamFourthQPoint(Integer.valueOf(arrayFirstScore[3]));
                        aTeamStat.setBTeamFifthQPoint(0);

                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setATeamSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setATeamThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setATeamFourthQPoint(Integer.valueOf(arrayFirstScore[8]));
                        aTeamStat.setATeamFifthQPoint(0);

                        bTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setATeamSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setATeamThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setATeamFourthQPoint(Integer.valueOf(arrayFirstScore[3]));
                        bTeamStat.setATeamFifthQPoint(0);

                        bTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        bTeamStat.setBTeamSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        bTeamStat.setBTeamThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        bTeamStat.setBTeamFourthQPoint(Integer.valueOf(arrayFirstScore[8]));
                        bTeamStat.setBTeamFifthQPoint(0);

                        bTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setFourthQPoint(Integer.valueOf(arrayFirstScore[3]));
                        bTeamStat.setFifthQPoint(0);

                        aTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setFourthQPoint(Integer.valueOf(arrayFirstScore[8]));
                        aTeamStat.setFifthQPoint(0);



                    }
                    else if(arrayFirstScore.length == 10){
                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setBTeamSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        aTeamStat.setBTeamThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        aTeamStat.setBTeamFourthQPoint(Integer.valueOf(arrayFirstScore[3]));
                        aTeamStat.setBTeamFifthQPoint(Integer.valueOf(arrayFirstScore[4]));

                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setATeamSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setATeamThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setATeamFourthQPoint(Integer.valueOf(arrayFirstScore[8]));
                        aTeamStat.setATeamFifthQPoint(Integer.valueOf(arrayFirstScore[9]));



                        bTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setATeamSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setATeamThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setATeamFourthQPoint(Integer.valueOf(arrayFirstScore[3]));
                        bTeamStat.setATeamFifthQPoint(Integer.valueOf(arrayFirstScore[4]));

                        bTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        bTeamStat.setBTeamSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        bTeamStat.setBTeamThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        bTeamStat.setBTeamFourthQPoint(Integer.valueOf(arrayFirstScore[8]));
                        bTeamStat.setBTeamFifthQPoint(Integer.valueOf(arrayFirstScore[9]));

                        bTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setFourthQPoint(Integer.valueOf(arrayFirstScore[3]));
                        bTeamStat.setFifthQPoint(Integer.valueOf(arrayFirstScore[4]));

                        aTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setFourthQPoint(Integer.valueOf(arrayFirstScore[8]));
                        aTeamStat.setFifthQPoint(Integer.valueOf(arrayFirstScore[9]));

                    }
                    else {
                        aTeamStat.setBTeamFirstQPoint(0);
                        aTeamStat.setBTeamSecondQPoint(0);
                        aTeamStat.setBTeamThirdQPoint(0);
                        aTeamStat.setBTeamFourthQPoint(0);
                        aTeamStat.setBTeamFifthQPoint(0);

                        aTeamStat.setATeamFirstQPoint(0);
                        aTeamStat.setATeamSecondQPoint(0);
                        aTeamStat.setATeamThirdQPoint(0);
                        aTeamStat.setATeamFourthQPoint(0);
                        aTeamStat.setATeamFifthQPoint(0);

                        bTeamStat.setATeamFirstQPoint(0);
                        bTeamStat.setATeamSecondQPoint(0);
                        bTeamStat.setATeamThirdQPoint(0);
                        bTeamStat.setATeamFourthQPoint(0);
                        bTeamStat.setATeamFifthQPoint(0);

                        bTeamStat.setBTeamFirstQPoint(0);
                        bTeamStat.setBTeamSecondQPoint(0);
                        bTeamStat.setBTeamThirdQPoint(0);
                        bTeamStat.setBTeamFourthQPoint(0);
                        bTeamStat.setBTeamFifthQPoint(0);

                        bTeamStat.setFirstQPoint(0);
                        bTeamStat.setSecondQPoint(0);
                        bTeamStat.setThirdQPoint(0);
                        bTeamStat.setFourthQPoint(0);
                        bTeamStat.setFifthQPoint(0);

                        aTeamStat.setFirstQPoint(0);
                        aTeamStat.setSecondQPoint(0);
                        aTeamStat.setThirdQPoint(0);
                        aTeamStat.setFourthQPoint(0);
                        aTeamStat.setFifthQPoint(0);
                    }

                    if(aTeamStat.getHandiCap() == 0){
                        aTeamStat.setHandiCapResult("적특");
                    }else {
                        if ((aTeamStat.getTotalPoint() + aTeamStat.getHandiCap()) > bTeamStat.getTotalPoint()) {
                            aTeamStat.setHandiCapResult("승리");
                        } else if ((aTeamStat.getTotalPoint() + aTeamStat.getHandiCap()) < bTeamStat.getTotalPoint()) {
                            aTeamStat.setHandiCapResult("패배");
                        } else {
                            aTeamStat.setHandiCapResult("적특");
                        }
                    }


                    if(aTeamStat.getPointLine() == 0){
                        aTeamStat.setPointLineResult("적특");
                        bTeamStat.setPointLineResult("적특");
                    } else {
                        if ((aTeamStat.getTotalPoint() + bTeamStat.getTotalPoint()) > aTeamStat.getPointLine()) {
                            aTeamStat.setPointLineResult("오버");
                            bTeamStat.setPointLineResult("오버");
                        } else if ((aTeamStat.getTotalPoint() + bTeamStat.getTotalPoint()) < aTeamStat.getPointLine()) {
                            aTeamStat.setPointLineResult("언더");
                            bTeamStat.setPointLineResult("언더");
                        } else {
                            aTeamStat.setPointLineResult("적특");
                            bTeamStat.setPointLineResult("적특");
                        }
                    }

                    aTeamStat.setATeamTotalPoint(aTeamStat.getTotalPoint());
                    aTeamStat.setBTeamTotalPoint(bTeamStat.getTotalPoint());


                    String[] arrayTotalScore = element.select("tbody > tr > td.score").text().split(" ");

                    if (arrayTotalScore.length > 1) {
                        aTeamStat.setBTeamSetScore(Integer.valueOf(arrayTotalScore[0]));
                        aTeamStat.setATeamSetScore(Integer.valueOf(arrayTotalScore[1]));
                    } else {
                        aTeamStat.setBTeamSetScore(0);
                        aTeamStat.setATeamSetScore(0);
                    }
                    if(aTeamStat.getHandiCap() < 0){
                        aTeamStat.setSetHandiCap(-1.5);
                    }else {
                        aTeamStat.setSetHandiCap(1.5);
                    }


                    if(aTeamStat.getHandiCap() == 0){
                        aTeamStat.setSetHandiCapResult("적특");
                    }else {
                        if ((aTeamStat.getATeamSetScore() + aTeamStat.getSetHandiCap()) > aTeamStat.getBTeamSetScore()) {
                            aTeamStat.setSetHandiCapResult("승리");
                        } else if ((aTeamStat.getATeamSetScore() + aTeamStat.getSetHandiCap()) < aTeamStat.getBTeamSetScore()) {
                            aTeamStat.setSetHandiCapResult("패배");
                        } else {
                            aTeamStat.setSetHandiCapResult("적특");
                        }
                    }


                    double handi = aTeamStat.getHandiCap() / 4;
                    int handiInt = (int) handi;
                    double pointHandi = handi - handiInt;

                    if ((pointHandi <= 0.333 && pointHandi >= 0.001) || (pointHandi >= -0.333 && pointHandi <= -0.001)) {
                        if(pointHandi < 0){
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.ceil(aTeamStat.getHandiCap() / 4)));
                        }else {
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.floor(aTeamStat.getHandiCap() / 4)));
                        }
                    } else if((pointHandi <= 0.999 && pointHandi >= 0.666) || (pointHandi >= -0.999 && pointHandi <= -0.666)) {
                        if(pointHandi < 0){
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.floor(aTeamStat.getHandiCap() / 4)));
                        }else {
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.ceil(aTeamStat.getHandiCap() / 4)));
                        }
                    } else if((pointHandi <= 0.665 && pointHandi >= 0.334)) {
                        aTeamStat.setFirstQHandiCap(handiInt + 0.5);
                    }  else if((pointHandi >= -0.665 && pointHandi <= -0.334)) {
                        aTeamStat.setFirstQHandiCap(handiInt - 0.5);
                    } else if (aTeamStat.getHandiCap() >= 0.5 && aTeamStat.getHandiCap() <= 1.5) {
                        aTeamStat.setFirstQHandiCap(0.5);
                    } else if (aTeamStat.getHandiCap() <= -0.5 && aTeamStat.getHandiCap() >= -1.5) {
                        aTeamStat.setFirstQHandiCap(-0.5);
                    }else {
                        aTeamStat.setFirstQHandiCap(0.0);
                    }


                    if(aTeamStat.getFirstQHandiCap() == 0){
                        aTeamStat.setFirstQHandiCapResult("적특");
                        aTeamStat.setSecondQHandiCapResult("적특");
                        aTeamStat.setThirdQHandiCapResult("적특");
                        aTeamStat.setFourthQHandiCapResult("적특");
                        aTeamStat.setFifthQHandiCapResult("적특");
                    }
                    else {

                        if ((aTeamStat.getATeamFirstQPoint() + aTeamStat.getFirstQHandiCap()) > aTeamStat.getBTeamFirstQPoint()) {
                            aTeamStat.setFirstQHandiCapResult("승리");
                        } else if ((aTeamStat.getATeamFirstQPoint() + aTeamStat.getFirstQHandiCap()) < aTeamStat.getBTeamFirstQPoint()) {
                            aTeamStat.setFirstQHandiCapResult("패배");
                        } else {
                            aTeamStat.setFirstQHandiCapResult("적특");
                        }

                        if ((aTeamStat.getATeamSecondQPoint() + aTeamStat.getFirstQHandiCap()) > aTeamStat.getBTeamSecondQPoint()) {
                            aTeamStat.setSecondQHandiCapResult("승리");
                        } else if ((aTeamStat.getATeamSecondQPoint() + aTeamStat.getFirstQHandiCap()) < aTeamStat.getBTeamSecondQPoint()) {
                            aTeamStat.setSecondQHandiCapResult("패배");
                        } else {
                            aTeamStat.setSecondQHandiCapResult("적특");
                        }

                        if ((aTeamStat.getATeamThirdQPoint() + aTeamStat.getFirstQHandiCap()) > aTeamStat.getBTeamThirdQPoint()) {
                            aTeamStat.setThirdQHandiCapResult("승리");
                        } else if ((aTeamStat.getATeamThirdQPoint() + aTeamStat.getFirstQHandiCap()) < aTeamStat.getBTeamThirdQPoint()) {
                            aTeamStat.setThirdQHandiCapResult("패배");
                        } else {
                            aTeamStat.setThirdQHandiCapResult("적특");
                        }

                        if((arrayFirstScore.length >= 9)){
                            if ((aTeamStat.getATeamFourthQPoint() + aTeamStat.getFirstQHandiCap()) > aTeamStat.getBTeamFourthQPoint()) {
                                aTeamStat.setFourthQHandiCapResult("승리");
                            } else if ((aTeamStat.getATeamFourthQPoint() + aTeamStat.getFirstQHandiCap()) < aTeamStat.getBTeamFourthQPoint()) {
                                aTeamStat.setFourthQHandiCapResult("패배");
                            } else {
                                aTeamStat.setFourthQHandiCapResult("적특");
                            }
                        } else {
                            aTeamStat.setFourthQHandiCapResult("0");

                        }


                        if((arrayFirstScore.length >= 10)){
                            if ((aTeamStat.getATeamFifthQPoint() + aTeamStat.getFirstQHandiCap()) > aTeamStat.getBTeamFifthQPoint()) {
                                aTeamStat.setFifthQHandiCapResult("승리");
                            } else if ((aTeamStat.getATeamFifthQPoint() + aTeamStat.getFirstQHandiCap()) < aTeamStat.getBTeamFifthQPoint()) {
                                aTeamStat.setFifthQHandiCapResult("패배");
                            } else {
                                aTeamStat.setFifthQHandiCapResult("적특");
                            }
                        } else {
                            aTeamStat.setFifthQHandiCapResult("0");

                        }


                    }

                    if (aTeamStat.getATeamFirstQPoint() > aTeamStat.getBTeamFirstQPoint()) {
                        aTeamStat.setFirstQResult("승리");
                        bTeamStat.setFirstQResult("패배");
                    } else if (aTeamStat.getATeamFirstQPoint() < aTeamStat.getBTeamFirstQPoint()) {
                        aTeamStat.setFirstQResult("패배");
                        bTeamStat.setFirstQResult("승리");
                    } else {
                        aTeamStat.setFirstQResult("적특");
                        bTeamStat.setFirstQResult("적특");

                    }

                    if (aTeamStat.getATeamSecondQPoint() > aTeamStat.getBTeamSecondQPoint()) {
                        aTeamStat.setSecondQResult("승리");
                        bTeamStat.setSecondQResult("패배");
                    } else if (aTeamStat.getATeamSecondQPoint() < aTeamStat.getBTeamSecondQPoint()) {
                        aTeamStat.setSecondQResult("패배");
                        bTeamStat.setSecondQResult("승리");
                    } else {
                        aTeamStat.setSecondQResult("적특");
                        bTeamStat.setSecondQResult("적특");

                    }

                    if (aTeamStat.getATeamThirdQPoint() > aTeamStat.getBTeamThirdQPoint()) {
                        aTeamStat.setThirdQResult("승리");
                        bTeamStat.setThirdQResult("패배");
                    } else if (aTeamStat.getATeamThirdQPoint() < aTeamStat.getBTeamThirdQPoint()) {
                        aTeamStat.setThirdQResult("패배");
                        bTeamStat.setThirdQResult("승리");
                    } else {
                        aTeamStat.setThirdQResult("적특");
                        bTeamStat.setThirdQResult("적특");

                    }

                    if((arrayFirstScore.length >= 9)) {
                        if (aTeamStat.getATeamFourthQPoint() > aTeamStat.getBTeamFourthQPoint()) {
                            aTeamStat.setFourthQResult("승리");
                            bTeamStat.setFourthQResult("패배");
                        } else if (aTeamStat.getATeamFourthQPoint() < aTeamStat.getBTeamFourthQPoint()) {
                            aTeamStat.setFourthQResult("패배");
                            bTeamStat.setFourthQResult("승리");
                        } else {
                            aTeamStat.setFourthQResult("적특");
                            bTeamStat.setFourthQResult("적특");
                        }
                    } else {
                        aTeamStat.setFourthQResult("0");
                        bTeamStat.setFourthQResult("0");
                    }


                    if((arrayFirstScore.length >= 10)){

                        if (aTeamStat.getATeamFifthQPoint() > aTeamStat.getBTeamFifthQPoint()) {
                            aTeamStat.setFifthQResult("승리");
                            bTeamStat.setFifthQResult("패배");
                        } else if (aTeamStat.getATeamFifthQPoint() < aTeamStat.getBTeamFifthQPoint()) {
                            aTeamStat.setFifthQResult("패배");
                            bTeamStat.setFifthQResult("승리");
                        } else {
                            aTeamStat.setFifthQResult("적특");
                            bTeamStat.setFifthQResult("적특");
                        }
                    } else {
                    aTeamStat.setFifthQResult("0");
                    bTeamStat.setFifthQResult("0");
                    }





                double point = aTeamStat.getPointLine() / 4;
                    int pointInt = (int) point;
                    double pointLine = point - pointInt;

                    if ((pointLine <= 0.333 && pointLine >= 0.001)) {
                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.floor(aTeamStat.getPointLine() / 4)));
                    } else if((pointLine <= 0.999 && pointLine >= 0.666)) {
                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.ceil(aTeamStat.getPointLine() / 4)));
                    } else if((pointLine <= 0.665 && pointLine >= 0.334)) {
                        aTeamStat.setFirstQPointLine(pointInt + 0.5);
                    } else {
                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.round(aTeamStat.getPointLine() / 4)));
                    }

                    if(aTeamStat.getFirstQPointLine() == 0){
                        aTeamStat.setFirstQPointLineResult("적특");
                        bTeamStat.setFirstQPointLineResult("적특");
                    }else {
                        if ((aTeamStat.getATeamFirstQPoint() + aTeamStat.getBTeamFirstQPoint()) > aTeamStat.getFirstQPointLine()) {
                            aTeamStat.setFirstQPointLineResult("오버");
                            bTeamStat.setFirstQPointLineResult("오버");

                        } else if ((aTeamStat.getATeamFirstQPoint() + aTeamStat.getBTeamFirstQPoint()) < aTeamStat.getFirstQPointLine()) {
                            aTeamStat.setFirstQPointLineResult("언더");
                            bTeamStat.setFirstQPointLineResult("언더");

                        } else {
                            aTeamStat.setFirstQPointLineResult("적특");
                            bTeamStat.setFirstQPointLineResult("적특");
                        }

                        if ((aTeamStat.getATeamSecondQPoint() + aTeamStat.getBTeamSecondQPoint()) > aTeamStat.getFirstQPointLine()) {
                            aTeamStat.setSecondQPointLineResult("오버");
                            bTeamStat.setSecondQPointLineResult("오버");

                        } else if ((aTeamStat.getATeamSecondQPoint() + aTeamStat.getBTeamSecondQPoint()) < aTeamStat.getFirstQPointLine()) {
                            aTeamStat.setSecondQPointLineResult("언더");
                            bTeamStat.setSecondQPointLineResult("언더");

                        } else {
                            aTeamStat.setSecondQPointLineResult("적특");
                            bTeamStat.setSecondQPointLineResult("적특");
                        }

                        if ((aTeamStat.getATeamThirdQPoint() + aTeamStat.getBTeamThirdQPoint()) > aTeamStat.getFirstQPointLine()) {
                            aTeamStat.setThirdQPointLineResult("오버");
                            bTeamStat.setThirdQPointLineResult("오버");

                        } else if ((aTeamStat.getATeamThirdQPoint() + aTeamStat.getBTeamThirdQPoint()) < aTeamStat.getFirstQPointLine()) {
                            aTeamStat.setThirdQPointLineResult("언더");
                            bTeamStat.setThirdQPointLineResult("언더");

                        } else {
                            aTeamStat.setThirdQPointLineResult("적특");
                            bTeamStat.setThirdQPointLineResult("적특");
                        }

                        if((arrayFirstScore.length >= 9)){
                            if ((aTeamStat.getATeamFourthQPoint() + aTeamStat.getBTeamFourthQPoint()) > aTeamStat.getFirstQPointLine()) {
                                aTeamStat.setFourthQPointLineResult("오버");
                                bTeamStat.setFourthQPointLineResult("오버");

                            } else if ((aTeamStat.getATeamFourthQPoint() + aTeamStat.getBTeamFourthQPoint()) < aTeamStat.getFirstQPointLine()) {
                                aTeamStat.setFourthQPointLineResult("언더");
                                bTeamStat.setFourthQPointLineResult("언더");

                            } else {
                                aTeamStat.setFourthQPointLineResult("적특");
                                bTeamStat.setFourthQPointLineResult("적특");
                            }
                        } else {
                            aTeamStat.setFourthQPointLineResult("0");
                            bTeamStat.setFourthQPointLineResult("0");
                        }


                    }



                    String[] arrayQTotalScore = element.select("tfoot > tr > td.s").text().split(" ");

                    if (arrayQTotalScore.length == 3) {
                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setFourthQTotalPoint(0);
                        aTeamStat.setFifthQTotalPoint(0);

                    } else if (arrayQTotalScore.length == 4) {

                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setFourthQTotalPoint(Integer.valueOf(arrayQTotalScore[3]));
                        aTeamStat.setFifthQTotalPoint(0);

                    } else if (arrayQTotalScore.length == 5) {
                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setFourthQTotalPoint(Integer.valueOf(arrayQTotalScore[3]));
                        aTeamStat.setFifthQTotalPoint(Integer.valueOf(arrayQTotalScore[4]));
                    }else  {
                        aTeamStat.setFirstQTotalPoint(0);
                        aTeamStat.setSecondQTotalPoint(0);
                        aTeamStat.setThirdQTotalPoint(0);
                        aTeamStat.setFourthQTotalPoint(0);
                        aTeamStat.setFifthQTotalPoint(0);

                    }


                    String test = element.select("tbody > tr > td.f.ico_linescore").text();
                    for (Element element1 : element.select("tbody > tr > td.f.ico_linescore")) {
                        if (i == 0) {
                            if (element1.select("span.ico_f_point").text().equals("첫득점")) {
                                aTeamStat.setFirstPoint("패배");
                            } else {
                                aTeamStat.setFirstPoint("승리");
                            }
                            if (element1.select("span.ico_f_block").text().equals("블로킹")) {
                                aTeamStat.setFirstBlock("패배");
                            } else {
                                aTeamStat.setFirstBlock("승리");
                            }
                            if (element1.select("span.ico_f_serve").text().equals("서브득")) {
                                aTeamStat.setFirstServe("패배");
                            } else {
                                aTeamStat.setFirstServe("승리");
                            }
                        }
                        i++;
                    }

                    int cnt = 0;
                    for (Element ele : element.select("tbody tr td.f.navy")){
//                        String[] arrayFirstPointList = ele.select("td.f.navy").text().split(" ");
                        String arrayFirst = ele.text();
                        if(cnt == 0 && arrayFirst.equals("●")){
                            aTeamStat.setFirst5Point("패배");
                            bTeamStat.setFirst5Point("승리");
                        } else if(cnt == 0 && !arrayFirst.equals("●")) {
                            aTeamStat.setFirst5Point("승리");
                            bTeamStat.setFirst5Point("패배");
                        }

                        if(cnt == 1 && arrayFirst.equals("●")){

                            aTeamStat.setFirst7Point("패배");
                            bTeamStat.setFirst7Point("승리");
                        } else if(cnt == 1 && !arrayFirst.equals("●")){
                            aTeamStat.setFirst7Point("승리");
                            bTeamStat.setFirst7Point("패배");

                        }
                        if(cnt == 2 && arrayFirst.equals("●")){

                            aTeamStat.setFirst10Point("패배");
                            bTeamStat.setFirst10Point("승리");
                        } else if(cnt == 2 && !arrayFirst.equals("●")) {
;                           aTeamStat.setFirst10Point("승리");
                            bTeamStat.setFirst10Point("패배");

                        }
                        cnt++;
                    }

                    setBteamStat(aTeamStat, bTeamStat);

//
//                    System.out.println(aTeamStat);
//                    System.out.println(bTeamStat);
                    setalarmDAO.updateVolleyStat(aTeamStat);
                    setalarmDAO.updateVolleyStat(bTeamStat);
                }

            }

            date++;
        }
    }


    public void getCategoryList() throws IOException, ParseException, InterruptedException {
        JSONArray jsonArray = new JSONArray();
        VolleyballModel aTeamStat = new VolleyballModel();
        VolleyballModel bTeamStat = new VolleyballModel();

        String rootHtml = "";
        String url = "https://livescore.co.kr/sports/score_board/volley/view.php?date=";


        for (int date = 1; date < 100; date++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            cal.add(Calendar.DATE, -date);
            System.out.println("after: " + df.format(cal.getTime()));
            int dayNum = cal.get(Calendar.DAY_OF_WEEK);
            String dayOfWeek = getDayoOfWeek(dayNum);

            System.out.println(url + df.format(cal.getTime()));
            if(df.format(cal.getTime()).equals("2019-10-11")){
                System.out.println("시즌끝");
                break;
            }
            rootHtml = requestURLToString(url + df.format(cal.getTime()));
//            Thread.sleep(500);

            Document rootDoc = Jsoup.parse(rootHtml);
            Elements elements = rootDoc.select("div#score_board div.score_tbl_individual");
            for (Element element : rootDoc.select("div#score_board div.score_tbl_individual")) {
                int i = 0;

                String league = element.select("thead tr th.reague").text();
                if (league.equals("V-리그")) {
//                if ("승리") {
                    String gameId = element.select("div.score_tbl_individual").attr("id");
                    aTeamStat.setGameId(gameId);
                    bTeamStat.setGameId(gameId);
                    aTeamStat.setDayOfWeek(dayOfWeek);
                    bTeamStat.setDayOfWeek(dayOfWeek);


                    aTeamStat.setTime(element.select("thead tr th.ptime").text().replaceAll("오전 ", "").replaceAll("오후 ", ""));
                    aTeamStat.setDate(df.format(cal.getTime()));
                    bTeamStat.setDate(df.format(cal.getTime()));
                    aTeamStat.setGround("홈");
                    bTeamStat.setGround("원정");
                    aTeamStat.setBTeam(element.select("tbody tr > td.teaminfo.visitor strong").text());
                    aTeamStat.setATeam(element.select("tbody tr > td.teaminfo.hometeam strong").text());

                    aTeamStat.setLeague(getDivision(aTeamStat.getBTeam()));

                    String[] arrayHandi = element.select("tbody > tr > td.line").text().split(" ");

                    if (arrayHandi.length > 1) {
                        aTeamStat.setPointLine(Double.valueOf(arrayHandi[0]));
                        aTeamStat.setHandiCap(Double.valueOf(arrayHandi[1]));
                    } else {
                        aTeamStat.setPointLine(0.0);
                        aTeamStat.setHandiCap(0.0);
                    }


                    String[] arrayFirstScore = element.select("tbody > tr > td.s").text().split(" ");

                    if (arrayFirstScore.length == 8) {
                        bTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setFourthQPoint(0);
                        bTeamStat.setFifthQPoint(0);

                        aTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setFourthQPoint(0);
                        aTeamStat.setFifthQPoint(0);

                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                    } else if(arrayFirstScore.length == 9){
                        bTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setFourthQPoint(Integer.valueOf(arrayFirstScore[3]));
                        bTeamStat.setFifthQPoint(0);

                        aTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setFourthQPoint(Integer.valueOf(arrayFirstScore[8]));
                        aTeamStat.setFifthQPoint(0);

                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));

                    } else if(arrayFirstScore.length == 10){
                        bTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setFourthQPoint(Integer.valueOf(arrayFirstScore[3]));
                        bTeamStat.setFifthQPoint(Integer.valueOf(arrayFirstScore[4]));

                        aTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setFourthQPoint(Integer.valueOf(arrayFirstScore[8]));
                        aTeamStat.setFifthQPoint(Integer.valueOf(arrayFirstScore[9]));

                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));

                    }else {
                        bTeamStat.setFirstQPoint(0);
                        bTeamStat.setSecondQPoint(0);
                        bTeamStat.setThirdQPoint(0);
                        bTeamStat.setFourthQPoint(0);
                        bTeamStat.setFifthQPoint(0);

                        aTeamStat.setFirstQPoint(0);
                        aTeamStat.setSecondQPoint(0);
                        aTeamStat.setThirdQPoint(0);
                        aTeamStat.setFourthQPoint(0);
                        aTeamStat.setFifthQPoint(0);
                    }

                    if(aTeamStat.getHandiCap() == 0){
                        aTeamStat.setHandiCapResult("적특");
                    }else {
                        if ((aTeamStat.getTotalPoint() + aTeamStat.getHandiCap()) > bTeamStat.getTotalPoint()) {
                            aTeamStat.setHandiCapResult("승리");
                        } else if ((aTeamStat.getTotalPoint() + aTeamStat.getHandiCap()) < bTeamStat.getTotalPoint()) {
                            aTeamStat.setHandiCapResult("패배");
                        } else {
                            aTeamStat.setHandiCapResult("적특");
                        }
                    }


                    if(aTeamStat.getPointLine() == 0){
                        aTeamStat.setPointLineResult("적특");
                        bTeamStat.setPointLineResult("적특");
                    } else {
                        if ((aTeamStat.getTotalPoint() + bTeamStat.getTotalPoint()) > aTeamStat.getPointLine()) {
                            aTeamStat.setPointLineResult("오버");
                            bTeamStat.setPointLineResult("오버");
                        } else if ((aTeamStat.getTotalPoint() + bTeamStat.getTotalPoint()) < aTeamStat.getPointLine()) {
                            aTeamStat.setPointLineResult("언더");
                            bTeamStat.setPointLineResult("언더");
                        } else {
                            aTeamStat.setPointLineResult("적특");
                            bTeamStat.setPointLineResult("적특");
                        }
                    }

                    aTeamStat.setATeamTotalPoint(aTeamStat.getTotalPoint());
                    aTeamStat.setBTeamTotalPoint(bTeamStat.getTotalPoint());


                    String[] arrayTotalScore = element.select("tbody > tr > td.score").text().split(" ");

                    if (arrayTotalScore.length > 1) {
                        aTeamStat.setBTeamSetScore(Integer.valueOf(arrayTotalScore[0]));
                        aTeamStat.setATeamSetScore(Integer.valueOf(arrayTotalScore[1]));
                    } else {
                        aTeamStat.setBTeamSetScore(0);
                        aTeamStat.setATeamSetScore(0);
                    }

                    if(aTeamStat.getHandiCap() < 0){
                        aTeamStat.setSetHandiCap(-1.5);
                    }else {
                        aTeamStat.setSetHandiCap(1.5);
                    }


                    if(aTeamStat.getHandiCap() == 0){
                        aTeamStat.setSetHandiCapResult("적특");
                    }else {
                        if ((aTeamStat.getATeamSetScore() + aTeamStat.getSetHandiCap()) > aTeamStat.getBTeamSetScore()) {
                            aTeamStat.setSetHandiCapResult("승리");
                        } else if ((aTeamStat.getATeamSetScore() + aTeamStat.getSetHandiCap()) < aTeamStat.getBTeamSetScore()) {
                            aTeamStat.setSetHandiCapResult("패배");
                        } else {
                            aTeamStat.setSetHandiCapResult("적특");
                        }
                    }


                    double handi = aTeamStat.getHandiCap() / 4;
                    int handiInt = (int) handi;
                    double pointHandi = handi - handiInt;

                    if ((pointHandi <= 0.333 && pointHandi >= 0.001) || (pointHandi >= -0.333 && pointHandi <= -0.001)) {
                        if(pointHandi < 0){
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.ceil(aTeamStat.getHandiCap() / 4)));
                        }else {
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.floor(aTeamStat.getHandiCap() / 4)));
                        }
                    } else if((pointHandi <= 0.999 && pointHandi >= 0.666) || (pointHandi >= -0.999 && pointHandi <= -0.666)) {
                        if(pointHandi < 0){
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.floor(aTeamStat.getHandiCap() / 4)));
                        }else {
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.ceil(aTeamStat.getHandiCap() / 4)));
                        }
                    } else if((pointHandi <= 0.665 && pointHandi >= 0.334)) {
                        aTeamStat.setFirstQHandiCap(handiInt + 0.5);
                    }  else if((pointHandi >= -0.665 && pointHandi <= -0.334)) {
                        aTeamStat.setFirstQHandiCap(handiInt - 0.5);
                    } else if (aTeamStat.getHandiCap() >= 0.5 && aTeamStat.getHandiCap() <= 1.5) {
                    aTeamStat.setFirstQHandiCap(0.5);
                    } else if (aTeamStat.getHandiCap() <= -0.5 && aTeamStat.getHandiCap() >= -1.5) {
                    aTeamStat.setFirstQHandiCap(-0.5);
                    };


                    if(aTeamStat.getFirstQHandiCap() == 0){
                        aTeamStat.setFirstQHandiCapResult("적특");
                    }else {
                        if ((aTeamStat.getFirstQPoint() + aTeamStat.getFirstQHandiCap()) > bTeamStat.getFirstQPoint()) {
                            aTeamStat.setFirstQHandiCapResult("승리");
                        } else if ((aTeamStat.getFirstQPoint() + aTeamStat.getFirstQHandiCap()) < bTeamStat.getFirstQPoint()) {
                            aTeamStat.setFirstQHandiCapResult("패배");
                        } else {
                            aTeamStat.setFirstQHandiCapResult("적특");
                        }
                    }


                    double point = aTeamStat.getPointLine() / 4;
                    int pointInt = (int) point;
                    double pointLine = point - pointInt;

                    if ((pointLine <= 0.333 && pointLine >= 0.001)) {
                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.floor(aTeamStat.getPointLine() / 4)));
                    } else if((pointLine <= 0.999 && pointLine >= 0.666)) {
                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.ceil(aTeamStat.getPointLine() / 4)));
                    } else if((pointLine <= 0.665 && pointLine >= 0.334)) {
                        aTeamStat.setFirstQPointLine(pointInt + 0.5);
                    } else {
                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.round(aTeamStat.getPointLine() / 4)));
                    }

                    if(aTeamStat.getFirstQPointLine() == 0){
                        aTeamStat.setFirstQPointLineResult("적특");
                        bTeamStat.setFirstQPointLineResult("적특");
                    }else {
                        if ((aTeamStat.getATeamFirstQPoint() + aTeamStat.getBTeamFirstQPoint()) > aTeamStat.getFirstQPointLine()) {
                            aTeamStat.setFirstQPointLineResult("오버");
                            bTeamStat.setFirstQPointLineResult("오버");

                        } else if ((aTeamStat.getATeamFirstQPoint() + aTeamStat.getBTeamFirstQPoint()) < aTeamStat.getFirstQPointLine()) {
                            aTeamStat.setFirstQPointLineResult("언더");
                            bTeamStat.setFirstQPointLineResult("언더");

                        } else {
                            aTeamStat.setFirstQPointLineResult("적특");
                            bTeamStat.setFirstQPointLineResult("적특");
                        }
                    }



                    String[] arrayQTotalScore = element.select("tfoot > tr > td.s").text().split(" ");

                    if (arrayQTotalScore.length == 3) {
                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setFourthQTotalPoint(0);
                        aTeamStat.setFifthQTotalPoint(0);

                    } else if (arrayQTotalScore.length == 4) {

                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setFourthQTotalPoint(Integer.valueOf(arrayQTotalScore[3]));
                        aTeamStat.setFifthQTotalPoint(0);

                    } else if (arrayQTotalScore.length == 5) {
                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setFourthQTotalPoint(Integer.valueOf(arrayQTotalScore[3]));
                        aTeamStat.setFifthQTotalPoint(Integer.valueOf(arrayQTotalScore[4]));
                    }else  {
                        aTeamStat.setFirstQTotalPoint(0);
                        aTeamStat.setSecondQTotalPoint(0);
                        aTeamStat.setThirdQTotalPoint(0);
                        aTeamStat.setFourthQTotalPoint(0);
                        aTeamStat.setFifthQTotalPoint(0);

                    }


                    String test = element.select("tbody > tr > td.f.ico_linescore").text();
                    for (Element element1 : element.select("tbody > tr > td.f.ico_linescore")) {
                        if (i == 0) {
                            if (element1.select("span.ico_f_point").text().equals("첫득점")) {
                                aTeamStat.setFirstPoint("패배");
                            } else {
                                aTeamStat.setFirstPoint("승리");
                            }
                            if (element1.select("span.ico_f_block").text().equals("블로킹")) {
                                aTeamStat.setFirstBlock("패배");
                            } else {
                                aTeamStat.setFirstBlock("승리");
                            }
                            if (element1.select("span.ico_f_serve").text().equals("서브득")) {
                                aTeamStat.setFirstServe("패배");
                            } else {
                                aTeamStat.setFirstServe("승리");
                            }
                        }
                        i++;
                    }

                    int cnt = 0;
                    for (Element ele : element.select("tbody tr td.f.navy")){
//                        String[] arrayFirstPointList = ele.select("td.f.navy").text().split(" ");
                        String arrayFirst = ele.text();
                        if(cnt == 0 && arrayFirst.equals("●")){
                            aTeamStat.setFirst5Point("패배");
                            bTeamStat.setFirst5Point("승리");
                        } else {
                            aTeamStat.setFirst5Point("승리");
                            bTeamStat.setFirst5Point("패배");
                        }
                        if(cnt == 1 && arrayFirst.equals("●")){
                            aTeamStat.setFirst7Point("패배");
                            bTeamStat.setFirst7Point("승리");

                        } else {
                            aTeamStat.setFirst7Point("승리");
                            bTeamStat.setFirst7Point("패배");

                        }
                        if(cnt == 2 && arrayFirst.equals("●")){
                            aTeamStat.setFirst10Point("패배");
                            bTeamStat.setFirst10Point("승리");
                        } else {
                            aTeamStat.setFirst10Point("승리");
                            bTeamStat.setFirst10Point("패배");
                        }
                        cnt++;
                    }

                    setBteamStat(aTeamStat, bTeamStat);


                    System.out.println(aTeamStat);
                    System.out.println(bTeamStat);
                    setalarmDAO.insertVolleyStat(aTeamStat);
                    setalarmDAO.insertVolleyStat(bTeamStat);
                }

            }
        }
    }

    public void setBteamStat(VolleyballModel aTeamStat , VolleyballModel bTeamStat){
        bTeamStat.setLeague(aTeamStat.getLeague());


        bTeamStat.setPointLine(aTeamStat.getPointLine());
        bTeamStat.setHandiCap(-(aTeamStat.getHandiCap()));

        bTeamStat.setSetHandiCap(-aTeamStat.getSetHandiCap());

        bTeamStat.setBTeamSetScore(aTeamStat.getATeamSetScore());
        bTeamStat.setATeamSetScore(aTeamStat.getBTeamSetScore());

        if (aTeamStat.getHandiCapResult().equals("패배")) {
            bTeamStat.setHandiCapResult("승리");
        } else if (aTeamStat.getHandiCapResult().equals("승리")) {
            bTeamStat.setHandiCapResult("패배");
        } else {
            bTeamStat.setHandiCapResult("적특");
        }

        bTeamStat.setATeamTotalPoint(bTeamStat.getTotalPoint());
        bTeamStat.setBTeamTotalPoint(aTeamStat.getTotalPoint());

//        bTeamStat.setBTeamFirstQPoint(aTeamStat.getATeamFirstQPoint());
//        bTeamStat.setATeamFirstQPoint(aTeamStat.getBTeamFirstQPoint());


        bTeamStat.setFirstQHandiCap(-(aTeamStat.getFirstQHandiCap()));

        bTeamStat.setFirstQPointLine(aTeamStat.getFirstQPointLine());


        if (aTeamStat.getFirstQHandiCapResult().equals("패배")) {
            bTeamStat.setFirstQHandiCapResult("승리");
        } else if  (aTeamStat.getFirstQHandiCapResult().equals("승리")) {
            bTeamStat.setFirstQHandiCapResult("패배");
        } else {
            bTeamStat.setFirstQHandiCapResult("적특");
        }

        if (aTeamStat.getSecondQHandiCapResult().equals("패배")) {
            bTeamStat.setSecondQHandiCapResult("승리");
        } else if  (aTeamStat.getSecondQHandiCapResult().equals("승리")) {
            bTeamStat.setSecondQHandiCapResult("패배");
        } else {
            bTeamStat.setSecondQHandiCapResult("적특");
        }

        if (aTeamStat.getThirdQHandiCapResult().equals("패배")) {
            bTeamStat.setThirdQHandiCapResult("승리");
        } else if  (aTeamStat.getThirdQHandiCapResult().equals("승리")) {
            bTeamStat.setThirdQHandiCapResult("패배");
        } else {
            bTeamStat.setThirdQHandiCapResult("적특");
        }

        if(!aTeamStat.getFourthQHandiCapResult().isEmpty()){
            if (aTeamStat.getFourthQHandiCapResult().equals("패배")) {
                bTeamStat.setFourthQHandiCapResult("승리");
            } else if  (aTeamStat.getFourthQHandiCapResult().equals("승리")) {
                bTeamStat.setFourthQHandiCapResult("패배");
            } else if (aTeamStat.getFourthQHandiCapResult().equals("0")){
                bTeamStat.setFourthQHandiCapResult("0");
            }else {
                bTeamStat.setFourthQHandiCapResult("적특");
            }
        }

        if(!aTeamStat.getFifthQHandiCapResult().isEmpty()){
            if (aTeamStat.getFifthQHandiCapResult().equals("패배")) {
                bTeamStat.setFifthQHandiCapResult("승리");
            } else if  (aTeamStat.getFifthQHandiCapResult().equals("승리")) {
                bTeamStat.setFifthQHandiCapResult("패배");
            } else if (aTeamStat.getFifthQHandiCapResult().equals("0")){
                bTeamStat.setFifthQHandiCapResult("0");
            }else {
                bTeamStat.setFifthQHandiCapResult("적특");
            }
        }


        if (aTeamStat.getSetHandiCapResult().equals("패배")) {
            bTeamStat.setSetHandiCapResult("승리");
        } else if  (aTeamStat.getSetHandiCapResult().equals("승리")) {
            bTeamStat.setSetHandiCapResult("패배");
        } else {
            bTeamStat.setSetHandiCapResult("적특");
        }

        bTeamStat.setFirstQTotalPoint(aTeamStat.getFirstQTotalPoint());
        bTeamStat.setSecondQTotalPoint(aTeamStat.getSecondQTotalPoint());
        bTeamStat.setThirdQTotalPoint(aTeamStat.getThirdQTotalPoint());
        bTeamStat.setFourthQTotalPoint(aTeamStat.getFourthQTotalPoint());
        bTeamStat.setFifthQTotalPoint(aTeamStat.getFifthQTotalPoint());

        bTeamStat.setATeamFirstQPoint(aTeamStat.getBTeamFirstQPoint());
        bTeamStat.setBTeamFirstQPoint(aTeamStat.getATeamFirstQPoint());

        bTeamStat.setTime(aTeamStat.getTime());
        bTeamStat.setBTeam(aTeamStat.getATeam());
        bTeamStat.setATeam(aTeamStat.getBTeam());

        //첫2득
        if(aTeamStat.getFirstServe().equals("승리")){
            bTeamStat.setFirstServe("패배");
        } else {
            bTeamStat.setFirstServe("승리");
        }


        //첫득점
        if(aTeamStat.getFirstPoint().equals("승리")){
            bTeamStat.setFirstPoint("패배");
        } else {
            bTeamStat.setFirstPoint("승리");
        }


        //자유투
        if(aTeamStat.getFirstBlock().equals("승리")){
            bTeamStat.setFirstBlock("패배");
        } else {
            bTeamStat.setFirstBlock("승리");
        }

    }

    public String getDayoOfWeek(int dayNum){
        String dayOfWeek = "";

        switch (dayNum ) {
            case 1:
                dayOfWeek = "일요일";
                break;
            case 2:
                dayOfWeek = "월요일";
                break;
            case 3:
                dayOfWeek = "화요일";
                break;
            case 4:
                dayOfWeek = "수요일";
                break;
            case 5:
                dayOfWeek = "목요일";
                break;
            case 6:
                dayOfWeek = "금요일";
                break;
            case 7:
                dayOfWeek = "토요일";
                break;
        }
        return dayOfWeek;

    }

    public String getDivision (String team){
        String result = "";
        if(team.contains("현대건설") || team.contains("흥국생명") || team.contains("GS칼텍스") || team.contains("KGC인삼공사") || team.contains("한국도로공사") || team.contains("IBK기업은행")){
            result = "여자배구";
        } else {
            result = "남자배구";
        }
        return result;
    }

    public static void main(String[] args) {
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
//            for (int i = 0 ; i < memberList.size() ; i++){
//                recipients[0] = memberList.get(i).get("EMAIL").toString();
//                System.out.println(recipients[0]);
//                webSendMail.sendSSLMessage(recipients, "test", "test", "jungyongee@gmail.com");
//            }

//                 String[] recipients = {"qjsro1204@naver.com","jungyong_e@naver.com"};
//                System.out.println(recipients[0]);
//                webSendMail.sendSSLMessage(recipients, "test", "test", "jungyongee@gmail.com");
//


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
