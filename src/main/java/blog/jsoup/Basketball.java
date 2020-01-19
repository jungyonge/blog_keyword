package blog.jsoup;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;

import blog.model.BasketballModel;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.*;

public class Basketball {

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
        String url = "https://livescore.co.kr/sports/score_board/basket/view.php?date=";
        int date = 0;

        while (true){

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(2019, 9,03);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            cal.add(Calendar.DATE, date);
            System.out.println("after: " + df.format(cal.getTime()));

            //nba 10-22 ~ 2020.04.16
            //wkbl  2019년 10월 19일 (토) ~ 2020년 3월 19일 (목)
            // kbl 2019년 10월 5일 (토) ~ 2020년 3월 31일 (화)
            if(df.format(cal.getTime()).equals("2020-04-02")){
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
                BasketballModel aTeamStat = new BasketballModel();
                BasketballModel bTeamStat = new BasketballModel();

                int i = 0;

                String league = element.select("thead tr th.reague").text();
                if (league.equals("KBL") || league.equals("WKBL") ) {


                    String gameId = element.select("div.score_tbl_individual").attr("id");

                    aTeamStat.setGameId(gameId);
                    bTeamStat.setGameId(gameId);
                    aTeamStat.setDayOfWeek(dayOfWeek);
                    bTeamStat.setDayOfWeek(dayOfWeek);
                    aTeamStat.setLeague(league);
                    bTeamStat.setLeague(league);


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

                }

                if(aTeamStat.getGameId() == null|| bTeamStat.getGameId() == null){
                    continue;
                } else {
                    System.out.println(aTeamStat);
                    System.out.println(bTeamStat);
                    setalarmDAO.insertBasketMatch(aTeamStat);
                    setalarmDAO.insertBasketMatch(bTeamStat);
                }

            }

            date++;
        }

    }

    public void updateBasketBall() throws IOException, ParseException, InterruptedException {
        JSONArray jsonArray = new JSONArray();
        BasketballModel aTeamStat = new BasketballModel();
        BasketballModel bTeamStat = new BasketballModel();

        String rootHtml = "";
        String url = "https://livescore.co.kr/sports/score_board/basket/view.php?date=";

        int date = 0;
        Calendar curDate = Calendar.getInstance();
        curDate.setTime(new Date());
        curDate.add(Calendar.DATE, 1);

        while (true){

            Calendar startDate = Calendar.getInstance();

            startDate.set(2019,9,03);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            startDate.add(Calendar.DATE, date);
            System.out.println("after: " + df.format(startDate.getTime()));
            int dayNum = startDate.get(Calendar.DAY_OF_WEEK);
            String dayOfWeek = getDayoOfWeek(dayNum);


            System.out.println(url + df.format(startDate.getTime()));
            rootHtml = requestURLToString(url + df.format(startDate.getTime()));


            if(df.format(startDate.getTime()).equals(df.format(curDate.getTime()))){
                System.out.println("Today");
                break;
            }


            Document rootDoc = Jsoup.parse(rootHtml);
            Elements elements = rootDoc.select("div#score_board div.score_tbl_individual");
            for (Element element : rootDoc.select("div#score_board div.score_tbl_individual")) {
                int i = 0;

                String league = element.select("thead tr th.reague").text();

                if (league.equals("KBL") || league.equals("WKBL")) {

                    if (league.contains("CBA")) {
                        league = league.replaceAll("중국: ", "");
                    }

                    String gameId = element.select("div.score_tbl_individual").attr("id");
                    aTeamStat.setGameId(gameId);
                    bTeamStat.setGameId(gameId);
                    aTeamStat.setDayOfWeek(dayOfWeek);
                    bTeamStat.setDayOfWeek(dayOfWeek);
                    aTeamStat.setLeague(league);
                    String[] arrayHandi = element.select("tbody > tr > td.line").text().split(" ");

                    if (arrayHandi.length == 1) {
                        System.out.println("stop");
                    }
                    if (arrayHandi.length > 1) {
                        aTeamStat.setPointLine(Double.valueOf(arrayHandi[0]));
                        aTeamStat.setHandiCap(Double.valueOf(arrayHandi[1]));
                    } else {
                        aTeamStat.setPointLine(0.0);
                        aTeamStat.setHandiCap(0.0);
                    }


                    String[] arrayTotalScore = element.select("tbody > tr > td.score").text().split(" ");

                    if (arrayTotalScore.length == 1) {
                        System.out.println("stop");
                    }

                    if (arrayTotalScore.length > 1) {
                        aTeamStat.setBTeamTotalPoint(Integer.valueOf(arrayTotalScore[0]));
                        aTeamStat.setATeamTotalPoint(Integer.valueOf(arrayTotalScore[1]));
                    } else {
                        aTeamStat.setBTeamTotalPoint(0);
                        aTeamStat.setATeamTotalPoint(0);
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

                    if(aTeamStat.getHandiCap() == 0){
                        aTeamStat.setHandiCapResult("적특");
                    }else {
                        if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getHandiCap()) > aTeamStat.getBTeamTotalPoint()) {
                            aTeamStat.setHandiCapResult("승리");
                        } else if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getHandiCap()) < aTeamStat.getBTeamTotalPoint()) {
                            aTeamStat.setHandiCapResult("패배");
                        } else {
                            aTeamStat.setHandiCapResult("적특");
                        }
                    }


                    if(aTeamStat.getPointLine() == 0){
                        aTeamStat.setPointLineResult("적특");
                        bTeamStat.setPointLineResult("적특");
                    } else {
                        if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getBTeamTotalPoint()) > aTeamStat.getPointLine()) {
                            aTeamStat.setPointLineResult("오버");
                            bTeamStat.setPointLineResult("오버");
                        } else if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getBTeamTotalPoint()) < aTeamStat.getPointLine()) {
                            aTeamStat.setPointLineResult("언더");
                            bTeamStat.setPointLineResult("언더");
                        } else {
                            aTeamStat.setPointLineResult("적특");
                            bTeamStat.setPointLineResult("적특");
                        }
                    }


                    String[] arrayFirstScore = element.select("tbody > tr > td.s").text().split(" ");
                    if (arrayFirstScore.length == 1) {
                        System.out.println("stop");
                    }

                    if (arrayFirstScore.length > 8) {
                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setBTeamSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        aTeamStat.setBTeamThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        aTeamStat.setBTeamFourthQPoint(Integer.valueOf(arrayFirstScore[3]));

                        bTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setATeamSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setATeamThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setATeamFourthQPoint(Integer.valueOf(arrayFirstScore[3]));

                        if (arrayFirstScore.length == 10) {
                            aTeamStat.setBTeamExtendQPoint(Integer.valueOf(arrayFirstScore[4]));
                            bTeamStat.setATeamExtendQPoint(Integer.valueOf(arrayFirstScore[4]));

                        } else {
                            aTeamStat.setBTeamExtendQPoint(0);
                            bTeamStat.setBTeamExtendQPoint(0);

                        }

                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setATeamSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setATeamThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setATeamFourthQPoint(Integer.valueOf(arrayFirstScore[8]));

                        bTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        bTeamStat.setBTeamSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        bTeamStat.setBTeamThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        bTeamStat.setBTeamFourthQPoint(Integer.valueOf(arrayFirstScore[8]));
                        if (arrayFirstScore.length == 10) {
                            aTeamStat.setATeamExtendQPoint(Integer.valueOf(arrayFirstScore[9]));
                            bTeamStat.setBTeamExtendQPoint(Integer.valueOf(arrayFirstScore[9]));

                        } else {
                            aTeamStat.setATeamExtendQPoint(0);
                            bTeamStat.setBTeamExtendQPoint(0);

                        }
                    } else {

                        bTeamStat.setATeamFirstQPoint(0);
                        bTeamStat.setATeamSecondQPoint(0);
                        bTeamStat.setATeamThirdQPoint(0);
                        bTeamStat.setATeamFourthQPoint(0);
                        bTeamStat.setATeamExtendQPoint(0);

                        aTeamStat.setATeamFirstQPoint(0);
                        aTeamStat.setATeamSecondQPoint(0);
                        aTeamStat.setATeamThirdQPoint(0);
                        aTeamStat.setATeamFourthQPoint(0);
                        aTeamStat.setATeamExtendQPoint(0);

                        bTeamStat.setBTeamFirstQPoint(0);
                        bTeamStat.setBTeamSecondQPoint(0);
                        bTeamStat.setBTeamThirdQPoint(0);
                        bTeamStat.setBTeamFourthQPoint(0);
                        bTeamStat.setBTeamExtendQPoint(0);

                        aTeamStat.setBTeamFirstQPoint(0);
                        aTeamStat.setBTeamSecondQPoint(0);
                        aTeamStat.setBTeamThirdQPoint(0);
                        aTeamStat.setBTeamFourthQPoint(0);
                        aTeamStat.setBTeamExtendQPoint(0);
                    }


                    double handi = aTeamStat.getHandiCap() / 4;
                    int handiInt = (int) handi;
                    double pointHandi = handi - handiInt;

                    if (pointHandi == 0.5 || pointHandi == -0.5) {
                        aTeamStat.setFirstQHandiCap(aTeamStat.getHandiCap() / 4);
                    }else if (aTeamStat.getHandiCap() >= 0.5 && aTeamStat.getHandiCap() <= 1.5) {
                        aTeamStat.setFirstQHandiCap(0.5);
                    } else if (aTeamStat.getHandiCap() <= -0.5 && aTeamStat.getHandiCap() >= -1.5) {
                        aTeamStat.setFirstQHandiCap(-0.5);
                    } else {
                        aTeamStat.setFirstQHandiCap(Double.valueOf(Math.round(aTeamStat.getHandiCap() / 4)));
                    }

                    if(aTeamStat.getFirstQHandiCap() == 0){
                        aTeamStat.setFirstQHandiCapResult("적특");
                        aTeamStat.setSecondQHandiCapResult("적특");
                        aTeamStat.setThirdQHandiCapResult("적특");
                        aTeamStat.setFourthQHandiCapResult("적특");

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

                        if ((aTeamStat.getATeamFourthQPoint() + aTeamStat.getFirstQHandiCap()) > aTeamStat.getBTeamFourthQPoint()) {
                            aTeamStat.setFourthQHandiCapResult("승리");
                        } else if ((aTeamStat.getATeamFourthQPoint() + aTeamStat.getFirstQHandiCap()) < aTeamStat.getBTeamFourthQPoint()) {
                            aTeamStat.setFourthQHandiCapResult("패배");
                        } else {
                            aTeamStat.setFourthQHandiCapResult("적특");
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
                        aTeamStat.setSecondQPointLineResult("적특");
                        aTeamStat.setThirdQPointLineResult("적특");
                        aTeamStat.setFourthQPointLineResult("적특");
                        bTeamStat.setFirstQPointLineResult("적특");
                        bTeamStat.setSecondQPointLineResult("적특");
                        bTeamStat.setThirdQPointLineResult("적특");
                        bTeamStat.setFourthQPointLineResult("적특");
                    }
                    else {
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

                    }


                    double halfHandi = aTeamStat.getHandiCap() / 2;
                    int halfHandiInt = (int) halfHandi;
                    double pointHalfHandi = halfHandi - halfHandiInt;

                    if (pointHalfHandi == 0.5 || pointHalfHandi == -0.5) {
                        aTeamStat.setHalfHandiCap(aTeamStat.getHandiCap() / 2);
                    }else if (aTeamStat.getHandiCap() >= 0.5 && aTeamStat.getHandiCap() <= 1.5) {
                        aTeamStat.setHalfHandiCap(0.5);
                    } else if (aTeamStat.getHandiCap() <= -0.5 && aTeamStat.getHandiCap() >= -1.5) {
                        aTeamStat.setHalfHandiCap(-0.5);
                    } else {
                        aTeamStat.setHalfHandiCap(Double.valueOf(Math.round(aTeamStat.getHandiCap() / 4)));
                    }

                    aTeamStat.setATeamFirstHalfPoint(aTeamStat.getATeamFirstQPoint() + aTeamStat.getATeamSecondQPoint());
                    aTeamStat.setBTeamFirstHalfPoint(aTeamStat.getBTeamFirstQPoint() + aTeamStat.getBTeamSecondQPoint());

                    bTeamStat.setATeamFirstHalfPoint(bTeamStat.getATeamFirstQPoint() + bTeamStat.getATeamSecondQPoint());
                    bTeamStat.setBTeamFirstHalfPoint(bTeamStat.getBTeamFirstQPoint() + bTeamStat.getBTeamSecondQPoint());

                    aTeamStat.setATeamSecondHalfPoint(aTeamStat.getATeamThirdQPoint() + aTeamStat.getATeamFourthQPoint());
                    aTeamStat.setBTeamSecondHalfPoint(aTeamStat.getBTeamThirdQPoint() + aTeamStat.getBTeamFourthQPoint());

                    bTeamStat.setATeamSecondHalfPoint(bTeamStat.getATeamThirdQPoint() + bTeamStat.getATeamFourthQPoint());
                    bTeamStat.setBTeamSecondHalfPoint(bTeamStat.getBTeamThirdQPoint() + bTeamStat.getBTeamFourthQPoint());


                    if(aTeamStat.getHalfHandiCap() == 0){
                        aTeamStat.setFirstHalfHandiCapResult("적특");
                    }
                    else {
                        if ((aTeamStat.getATeamFirstHalfPoint() + aTeamStat.getHalfHandiCap()) > aTeamStat.getBTeamFirstHalfPoint()) {
                            aTeamStat.setFirstHalfHandiCapResult("승리");
                        } else if ((aTeamStat.getATeamFirstHalfPoint() + aTeamStat.getHalfHandiCap()) < aTeamStat.getBTeamFirstHalfPoint()) {
                            aTeamStat.setFirstHalfHandiCapResult("패배");
                        } else {
                            aTeamStat.setFirstHalfHandiCapResult("적특");
                        }

                        if ((aTeamStat.getATeamSecondHalfPoint() + aTeamStat.getHalfHandiCap()) > aTeamStat.getBTeamSecondHalfPoint()) {
                            aTeamStat.setSecondHalfHandiCapResult("승리");
                        } else if ((aTeamStat.getATeamSecondHalfPoint() + aTeamStat.getHalfHandiCap()) < aTeamStat.getBTeamSecondHalfPoint()) {
                            aTeamStat.setSecondHalfHandiCapResult("패배");
                        } else {
                            aTeamStat.setSecondHalfHandiCapResult("적특");
                        }

                    }

                    double halfPoint = aTeamStat.getPointLine() / 2;
                    int halfPointInt = (int) halfPoint;
                    double halftPointLine = halfPoint - halfPointInt;

                    if ((halftPointLine <= 0.333 && halftPointLine >= 0.001)) {
                        aTeamStat.setHalfPointLine(Double.valueOf(Math.floor(aTeamStat.getPointLine() / 2)));
                    } else if((halftPointLine <= 0.999 && halftPointLine >= 0.666)) {
                        aTeamStat.setHalfPointLine(Double.valueOf(Math.ceil(aTeamStat.getPointLine() / 2)));
                    } else if((halftPointLine <= 0.665 && halftPointLine >= 0.334)) {
                        aTeamStat.setHalfPointLine(halfPointInt + 0.5);
                    } else {
                        aTeamStat.setHalfPointLine(Double.valueOf(Math.round(aTeamStat.getPointLine() / 2)));
                    }


                    if(aTeamStat.getHalfPointLine() == 0){
                        aTeamStat.setFirstHalfPointLineResult("적특");
                        aTeamStat.setSecondHalfPointLineResult("적특");

                        bTeamStat.setFirstHalfPointLineResult("적특");
                        bTeamStat.setSecondHalfPointLineResult("적특");

                    }
                    else {
                        if ((aTeamStat.getATeamFirstHalfPoint() + aTeamStat.getBTeamFirstHalfPoint()) > aTeamStat.getHalfPointLine()) {
                            aTeamStat.setFirstHalfPointLineResult("오버");
                            bTeamStat.setFirstHalfPointLineResult("오버");

                        } else if ((aTeamStat.getATeamFirstHalfPoint() + aTeamStat.getBTeamFirstHalfPoint()) < aTeamStat.getHalfPointLine()) {
                            aTeamStat.setFirstHalfPointLineResult("언더");
                            bTeamStat.setFirstHalfPointLineResult("언더");

                        } else {
                            aTeamStat.setFirstHalfPointLineResult("적특");
                            bTeamStat.setFirstHalfPointLineResult("적특");
                        }

                        if ((aTeamStat.getATeamSecondHalfPoint() + aTeamStat.getBTeamSecondHalfPoint()) > aTeamStat.getHalfPointLine()) {
                            aTeamStat.setSecondHalfPointLineResult("오버");
                            bTeamStat.setSecondHalfPointLineResult("오버");

                        } else if ((aTeamStat.getATeamSecondHalfPoint() + aTeamStat.getBTeamSecondHalfPoint()) < aTeamStat.getHalfPointLine()) {
                            aTeamStat.setSecondHalfPointLineResult("언더");
                            bTeamStat.setSecondHalfPointLineResult("언더");

                        } else {
                            aTeamStat.setSecondHalfPointLineResult("적특");
                            bTeamStat.setSecondHalfPointLineResult("적특");
                        }
                    }

                    String[] arrayQScore = element.select("tbody > tr > td.s").text().split(" ");
                    String[] arrayQTotalScore = element.select("tfoot > tr > td.s").text().split(" ");

                    if (arrayQTotalScore.length == 1) {
                        System.out.println("stop");
                    }

                    if (arrayQTotalScore.length > 1) {
                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setFourthQTotalPoint(Integer.valueOf(arrayQTotalScore[3]));
                        if (arrayQTotalScore.length > 4) {
                            aTeamStat.setExtendQTotalPoint(Integer.valueOf(arrayQTotalScore[4]));
                        } else {
                            aTeamStat.setExtendQTotalPoint(0);
                        }
                    } else {
                        aTeamStat.setFirstQTotalPoint(0);
                        aTeamStat.setSecondQTotalPoint(0);
                        aTeamStat.setThirdQTotalPoint(0);
                        aTeamStat.setFourthQTotalPoint(0);
                        aTeamStat.setExtendQTotalPoint(0);

                    }

                    aTeamStat.setTime(element.select("thead tr th.ptime").text().replaceAll("오전 ", "").replaceAll("오후 ", ""));
                    aTeamStat.setDate(df.format(startDate.getTime()));
                    bTeamStat.setDate(df.format(startDate.getTime()));
                    aTeamStat.setGround("홈");
                    bTeamStat.setGround("원정");
                    aTeamStat.setBTeam(element.select("tbody tr > td.teaminfo.visitor strong").text());
                    aTeamStat.setATeam(element.select("tbody tr > td.teaminfo.hometeam strong").text());

                    for (Element element1 : element.select("tbody > tr > td.f.ico_linescore > p")) {
                        if (i == 0) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setFirstQFirstPoint("패배");
                            } else {
                                aTeamStat.setFirstQFirstPoint("승리");
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setFirstQFirstFreeTwo("패배");
                            } else {
                                aTeamStat.setFirstQFirstFreeTwo("승리");
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setFirstQFirstTwoPoint("패배");
                            } else {
                                aTeamStat.setFirstQFirstTwoPoint("승리");
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setFirstQFirstThreePoint("패배");
                            } else {
                                aTeamStat.setFirstQFirstThreePoint("승리");
                            }
                        }
                        if (i == 1) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setSecondQFirstPoint("패배");
                            } else {
                                aTeamStat.setSecondQFirstPoint("승리");
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setSecondQFirstFreeTwo("패배");
                            } else {
                                aTeamStat.setSecondQFirstFreeTwo("승리");
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setSecondQFirstTwoPoint("패배");
                            } else {
                                aTeamStat.setSecondQFirstTwoPoint("승리");
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setSecondQFirstThreePoint("패배");
                            } else {
                                aTeamStat.setSecondQFirstThreePoint("승리");
                            }
                        }
                        if (i == 2) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setThirdQFirstPoint("패배");
                            } else {
                                aTeamStat.setThirdQFirstPoint("승리");
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setThirdQFirstFreeTwo("패배");
                            } else {
                                aTeamStat.setThirdQFirstFreeTwo("승리");
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setThirdQFirstTwoPoint("패배");
                            } else {
                                aTeamStat.setThirdQFirstTwoPoint("승리");
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setThirdQFirstThreePoint("패배");
                            } else {
                                aTeamStat.setThirdQFirstThreePoint("승리");
                            }
                        }
                        if (i == 3) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setFourthQFirstPoint("패배");
                            } else {
                                aTeamStat.setFourthQFirstPoint("승리");
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setFourthQFirstFreeTwo("패배");
                            } else {
                                aTeamStat.setFourthQFirstFreeTwo("승리");
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setFourthQFirstTwoPoint("패배");
                            } else {
                                aTeamStat.setFourthQFirstTwoPoint("승리");
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setFourthQFirstThreePoint("패배");
                            } else {
                                aTeamStat.setFourthQFirstThreePoint("승리");
                            }
                        }
                        i++;
                    }
                    setBteamStat(aTeamStat, bTeamStat);


                    System.out.println(aTeamStat);
                    System.out.println(bTeamStat);
                    System.out.println("!!!");
                    setalarmDAO.updateBasketStat(aTeamStat);
                    setalarmDAO.updateBasketStat(bTeamStat);
                }

            }

            date++;
        }
    }

    public void getCategoryList() throws IOException, ParseException, InterruptedException {
        JSONArray jsonArray = new JSONArray();
        BasketballModel aTeamStat = new BasketballModel();
        BasketballModel bTeamStat = new BasketballModel();

        String rootHtml = "";
        String url = "https://livescore.co.kr/sports/score_board/basket/view.php?date=";


        for (int date = 1; date < 100; date++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            cal.add(Calendar.DATE, -date);
            System.out.println("after: " + df.format(cal.getTime()));
            int dayNum = cal.get(Calendar.DAY_OF_WEEK);
            String dayOfWeek = getDayoOfWeek(dayNum);

            cal.set(2019,10,4);

            System.out.println(url + df.format(cal.getTime()));
            rootHtml = requestURLToString(url + df.format(cal.getTime()));

            //nba 10-22
            if(df.format(cal.getTime()).equals("2019-10-04")){
                System.out.println("시즌끝");
                break;
            }

//            rootHtml = requestURLToString(url + "2019-12-12");
//            Thread.sleep(500);

            Document rootDoc = Jsoup.parse(rootHtml);
            Elements elements = rootDoc.select("div#score_board div.score_tbl_individual");
            for (Element element : rootDoc.select("div#score_board div.score_tbl_individual")) {
                int i = 0;

                String league = element.select("thead tr th.reague").text();

                if (league.equals("KBL") || league.equals("WKBL") || league.contains("CBA")) {

                    if (league.contains("CBA")) {
                        league = league.replaceAll("중국: ", "");
                    }

                    String gameId = element.select("div.score_tbl_individual").attr("id");
                    aTeamStat.setGameId(gameId);
                    bTeamStat.setGameId(gameId);
                    aTeamStat.setDayOfWeek(dayOfWeek);
                    bTeamStat.setDayOfWeek(dayOfWeek);
                    aTeamStat.setLeague(league);
                    String[] arrayHandi = element.select("tbody > tr > td.line").text().split(" ");

                    if (arrayHandi.length == 1) {
                        System.out.println("stop");
                    }
                    if (arrayHandi.length > 1) {
                        aTeamStat.setPointLine(Double.valueOf(arrayHandi[0]));
                        aTeamStat.setHandiCap(Double.valueOf(arrayHandi[1]));
                    } else {
                        aTeamStat.setPointLine(0.0);
                        aTeamStat.setHandiCap(0.0);
                    }


                    String[] arrayTotalScore = element.select("tbody > tr > td.score").text().split(" ");

                    if (arrayTotalScore.length == 1) {
                        System.out.println("stop");
                    }

                    if (arrayTotalScore.length > 1) {
                        aTeamStat.setBTeamTotalPoint(Integer.valueOf(arrayTotalScore[0]));
                        aTeamStat.setATeamTotalPoint(Integer.valueOf(arrayTotalScore[1]));
                    } else {
                        aTeamStat.setBTeamTotalPoint(0);
                        aTeamStat.setATeamTotalPoint(0);
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

                    if(aTeamStat.getHandiCap() == 0){
                        aTeamStat.setHandiCapResult("적특");
                    }else {
                        if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getHandiCap()) > aTeamStat.getBTeamTotalPoint()) {
                            aTeamStat.setHandiCapResult("승리");
                        } else if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getHandiCap()) < aTeamStat.getBTeamTotalPoint()) {
                            aTeamStat.setHandiCapResult("패배");
                        } else {
                            aTeamStat.setHandiCapResult("적특");
                        }
                    }


                    if(aTeamStat.getPointLine() == 0){
                        aTeamStat.setPointLineResult("적특");
                        bTeamStat.setPointLineResult("적특");
                    } else {
                        if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getBTeamTotalPoint()) > aTeamStat.getPointLine()) {
                            aTeamStat.setPointLineResult("오버");
                            bTeamStat.setPointLineResult("오버");
                        } else if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getBTeamTotalPoint()) < aTeamStat.getPointLine()) {
                            aTeamStat.setPointLineResult("언더");
                            bTeamStat.setPointLineResult("언더");
                        } else {
                            aTeamStat.setPointLineResult("적특");
                            bTeamStat.setPointLineResult("적특");
                        }
                    }


                    String[] arrayFirstScore = element.select("tbody > tr > td.s").text().split(" ");
                    if (arrayFirstScore.length == 1) {
                        System.out.println("stop");
                    }

                    if (arrayFirstScore.length > 8) {
                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setBTeamSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        aTeamStat.setBTeamThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        aTeamStat.setBTeamFourthQPoint(Integer.valueOf(arrayFirstScore[3]));

                        bTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setATeamSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setATeamThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setATeamFourthQPoint(Integer.valueOf(arrayFirstScore[3]));

                        if (arrayFirstScore.length == 10) {
                            aTeamStat.setBTeamExtendQPoint(Integer.valueOf(arrayFirstScore[4]));
                            bTeamStat.setATeamExtendQPoint(Integer.valueOf(arrayFirstScore[4]));

                        } else {
                            aTeamStat.setBTeamExtendQPoint(0);
                            bTeamStat.setBTeamExtendQPoint(0);

                        }

                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setATeamSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setATeamThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setATeamFourthQPoint(Integer.valueOf(arrayFirstScore[8]));

                        bTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        bTeamStat.setBTeamSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        bTeamStat.setBTeamThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        bTeamStat.setBTeamFourthQPoint(Integer.valueOf(arrayFirstScore[8]));
                        if (arrayFirstScore.length == 10) {
                            aTeamStat.setATeamExtendQPoint(Integer.valueOf(arrayFirstScore[9]));
                            bTeamStat.setBTeamExtendQPoint(Integer.valueOf(arrayFirstScore[9]));

                        } else {
                            aTeamStat.setATeamExtendQPoint(0);
                            bTeamStat.setBTeamExtendQPoint(0);

                        }
                    } else {

                        bTeamStat.setATeamFirstQPoint(0);
                        bTeamStat.setATeamSecondQPoint(0);
                        bTeamStat.setATeamThirdQPoint(0);
                        bTeamStat.setATeamFourthQPoint(0);
                        bTeamStat.setATeamExtendQPoint(0);

                        aTeamStat.setATeamFirstQPoint(0);
                        aTeamStat.setATeamSecondQPoint(0);
                        aTeamStat.setATeamThirdQPoint(0);
                        aTeamStat.setATeamFourthQPoint(0);
                        aTeamStat.setATeamExtendQPoint(0);

                        bTeamStat.setBTeamFirstQPoint(0);
                        bTeamStat.setBTeamSecondQPoint(0);
                        bTeamStat.setBTeamThirdQPoint(0);
                        bTeamStat.setBTeamFourthQPoint(0);
                        bTeamStat.setBTeamExtendQPoint(0);

                        aTeamStat.setBTeamFirstQPoint(0);
                        aTeamStat.setBTeamSecondQPoint(0);
                        aTeamStat.setBTeamThirdQPoint(0);
                        aTeamStat.setBTeamFourthQPoint(0);
                        aTeamStat.setBTeamExtendQPoint(0);
                    }


                    double handi = aTeamStat.getHandiCap() / 4;
                    int handiInt = (int) handi;
                    double pointHandi = handi - handiInt;

                    if (pointHandi == 0.5 || pointHandi == -0.5) {
                        aTeamStat.setFirstQHandiCap(aTeamStat.getHandiCap() / 4);
                    }else if (aTeamStat.getHandiCap() >= 0.5 && aTeamStat.getHandiCap() <= 1.5) {
                        aTeamStat.setFirstQHandiCap(0.5);
                    } else if (aTeamStat.getHandiCap() <= -0.5 && aTeamStat.getHandiCap() >= -1.5) {
                        aTeamStat.setFirstQHandiCap(-0.5);
                    } else {
                        aTeamStat.setFirstQHandiCap(Double.valueOf(Math.round(aTeamStat.getHandiCap() / 4)));
                    }

                    if(aTeamStat.getFirstQHandiCap() == 0){
                        aTeamStat.setFirstQHandiCapResult("적특");
                        aTeamStat.setSecondQHandiCapResult("적특");
                        aTeamStat.setThirdQHandiCapResult("적특");
                        aTeamStat.setFourthQHandiCapResult("적특");

                    }else {
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

                        if ((aTeamStat.getATeamFourthQPoint() + aTeamStat.getFirstQHandiCap()) > aTeamStat.getBTeamFourthQPoint()) {
                            aTeamStat.setFourthQHandiCapResult("승리");
                        } else if ((aTeamStat.getATeamFourthQPoint() + aTeamStat.getFirstQHandiCap()) < aTeamStat.getBTeamFourthQPoint()) {
                            aTeamStat.setFourthQHandiCapResult("패배");
                        } else {
                            aTeamStat.setFourthQHandiCapResult("적특");
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
                        aTeamStat.setSecondQPointLineResult("적특");
                        aTeamStat.setThirdQPointLineResult("적특");
                        aTeamStat.setFourthQPointLineResult("적특");
                        bTeamStat.setFirstQPointLineResult("적특");
                        bTeamStat.setSecondQPointLineResult("적특");
                        bTeamStat.setThirdQPointLineResult("적특");
                        bTeamStat.setFourthQPointLineResult("적특");
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

                    }


                    String[] arrayQScore = element.select("tbody > tr > td.s").text().split(" ");
                    String[] arrayQTotalScore = element.select("tfoot > tr > td.s").text().split(" ");

                    if (arrayQTotalScore.length == 1) {
                        System.out.println("stop");
                    }

                    if (arrayQTotalScore.length > 1) {
                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setFourthQTotalPoint(Integer.valueOf(arrayQTotalScore[3]));
                        if (arrayQTotalScore.length > 4) {
                            aTeamStat.setExtendQTotalPoint(Integer.valueOf(arrayQTotalScore[4]));
                        } else {
                            aTeamStat.setExtendQTotalPoint(0);
                        }
                    } else {
                        aTeamStat.setFirstQTotalPoint(0);
                        aTeamStat.setSecondQTotalPoint(0);
                        aTeamStat.setThirdQTotalPoint(0);
                        aTeamStat.setFourthQTotalPoint(0);
                        aTeamStat.setExtendQTotalPoint(0);

                    }

                    aTeamStat.setTime(element.select("thead tr th.ptime").text().replaceAll("오전 ", "").replaceAll("오후 ", ""));
                    aTeamStat.setDate(df.format(cal.getTime()));
                    bTeamStat.setDate(df.format(cal.getTime()));
                    aTeamStat.setGround("홈");
                    bTeamStat.setGround("원정");
                    aTeamStat.setBTeam(element.select("tbody tr > td.teaminfo.visitor strong").text());
                    aTeamStat.setATeam(element.select("tbody tr > td.teaminfo.hometeam strong").text());

                    for (Element element1 : element.select("tbody > tr > td.f.ico_linescore > p")) {
                        if (i == 0) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setFirstQFirstPoint("패배");
                            } else {
                                aTeamStat.setFirstQFirstPoint("승리");
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setFirstQFirstFreeTwo("패배");
                            } else {
                                aTeamStat.setFirstQFirstFreeTwo("승리");
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setFirstQFirstTwoPoint("패배");
                            } else {
                                aTeamStat.setFirstQFirstTwoPoint("승리");
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setFirstQFirstThreePoint("패배");
                            } else {
                                aTeamStat.setFirstQFirstThreePoint("승리");
                            }
                        }
                        if (i == 1) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setSecondQFirstPoint("패배");
                            } else {
                                aTeamStat.setSecondQFirstPoint("승리");
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setSecondQFirstFreeTwo("패배");
                            } else {
                                aTeamStat.setSecondQFirstFreeTwo("승리");
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setSecondQFirstTwoPoint("패배");
                            } else {
                                aTeamStat.setSecondQFirstTwoPoint("승리");
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setSecondQFirstThreePoint("패배");
                            } else {
                                aTeamStat.setSecondQFirstThreePoint("승리");
                            }
                        }
                        if (i == 2) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setThirdQFirstPoint("패배");
                            } else {
                                aTeamStat.setThirdQFirstPoint("승리");
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setThirdQFirstFreeTwo("패배");
                            } else {
                                aTeamStat.setThirdQFirstFreeTwo("승리");
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setThirdQFirstTwoPoint("패배");
                            } else {
                                aTeamStat.setThirdQFirstTwoPoint("승리");
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setThirdQFirstThreePoint("패배");
                            } else {
                                aTeamStat.setThirdQFirstThreePoint("승리");
                            }
                        }
                        if (i == 3) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setFourthQFirstPoint("패배");
                            } else {
                                aTeamStat.setFourthQFirstPoint("승리");
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setFourthQFirstFreeTwo("패배");
                            } else {
                                aTeamStat.setFourthQFirstFreeTwo("승리");
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setFourthQFirstTwoPoint("패배");
                            } else {
                                aTeamStat.setFourthQFirstTwoPoint("승리");
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setFourthQFirstThreePoint("패배");
                            } else {
                                aTeamStat.setFourthQFirstThreePoint("승리");
                            }
                        }
                        i++;
                    }
                    setBteamStat(aTeamStat, bTeamStat);


                    System.out.println(aTeamStat);
                    System.out.println(bTeamStat);
                    System.out.println("!!!");
                    setalarmDAO.insertBasketStat(aTeamStat);
                    setalarmDAO.insertBasketStat(bTeamStat);
                }

            }
        }
    }

    public void setBteamStat(BasketballModel aTeamStat , BasketballModel bTeamStat){
        bTeamStat.setLeague(aTeamStat.getLeague());


        bTeamStat.setPointLine(aTeamStat.getPointLine());
        bTeamStat.setHandiCap(-(aTeamStat.getHandiCap()));
        bTeamStat.setHalfHandiCap(-(aTeamStat.getHalfHandiCap()));
        bTeamStat.setHalfPointLine((aTeamStat.getHalfPointLine()));


        bTeamStat.setBTeamTotalPoint(aTeamStat.getATeamTotalPoint());
        bTeamStat.setATeamTotalPoint(aTeamStat.getBTeamTotalPoint());

        if (aTeamStat.getHandiCapResult().equals("패배")) {
            bTeamStat.setHandiCapResult("승리");
        } else if (aTeamStat.getHandiCapResult().equals("승리")) {
            bTeamStat.setHandiCapResult("패배");
        } else {
            bTeamStat.setHandiCapResult("적특");
        }

        if (aTeamStat.getFirstHalfHandiCapResult().equals("패배")) {
            bTeamStat.setFirstHalfHandiCapResult("승리");
        } else if (aTeamStat.getFirstHalfHandiCapResult().equals("승리")) {
            bTeamStat.setFirstHalfHandiCapResult("패배");
        } else {
            bTeamStat.setFirstHalfHandiCapResult("적특");
        }

        if (aTeamStat.getSecondHalfHandiCapResult().equals("패배")) {
            bTeamStat.setSecondHalfHandiCapResult("승리");
        } else if (aTeamStat.getSecondHalfHandiCapResult().equals("승리")) {
            bTeamStat.setSecondHalfHandiCapResult("패배");
        } else {
            bTeamStat.setSecondHalfHandiCapResult("적특");
        }


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

        if (aTeamStat.getFourthQHandiCapResult().equals("패배")) {
            bTeamStat.setFourthQHandiCapResult("승리");
        } else if  (aTeamStat.getFourthQHandiCapResult().equals("승리")) {
            bTeamStat.setFourthQHandiCapResult("패배");
        } else {
            bTeamStat.setFourthQHandiCapResult("적특");
        }

        bTeamStat.setFirstQTotalPoint(aTeamStat.getFirstQTotalPoint());
        bTeamStat.setSecondQTotalPoint(aTeamStat.getSecondQTotalPoint());
        bTeamStat.setThirdQTotalPoint(aTeamStat.getThirdQTotalPoint());
        bTeamStat.setFourthQTotalPoint(aTeamStat.getFourthQTotalPoint());
        bTeamStat.setExtendQTotalPoint(aTeamStat.getExtendQTotalPoint());

        bTeamStat.setATeamFirstQPoint(aTeamStat.getBTeamFirstQPoint());
        bTeamStat.setBTeamFirstQPoint(aTeamStat.getATeamFirstQPoint());

        bTeamStat.setTime(aTeamStat.getTime());
        bTeamStat.setBTeam(aTeamStat.getATeam());
        bTeamStat.setATeam(aTeamStat.getBTeam());

        //첫2득
        if(aTeamStat.getFirstQFirstTwoPoint().equals("승리")){
            bTeamStat.setFirstQFirstTwoPoint("패배");
        } else {
            bTeamStat.setFirstQFirstTwoPoint("승리");
        }

        if(aTeamStat.getSecondQFirstTwoPoint().equals("승리")){
            bTeamStat.setSecondQFirstTwoPoint("패배");
        } else {
            bTeamStat.setSecondQFirstTwoPoint("승리");
        }

        if(aTeamStat.getThirdQFirstTwoPoint().equals("승리")){
            bTeamStat.setThirdQFirstTwoPoint("패배");
        } else {
            bTeamStat.setThirdQFirstTwoPoint("승리");
        }

        if(aTeamStat.getFourthQFirstTwoPoint().equals("승리")){
            bTeamStat.setFourthQFirstTwoPoint("패배");
        } else {
            bTeamStat.setFourthQFirstTwoPoint("승리");
        }

        //첫득점
        if(aTeamStat.getFirstQFirstPoint().equals("승리")){
            bTeamStat.setFirstQFirstPoint("패배");
        } else {
            bTeamStat.setFirstQFirstPoint("승리");
        }

        if(aTeamStat.getSecondQFirstPoint().equals("승리")){
            bTeamStat.setSecondQFirstPoint("패배");
        } else {
            bTeamStat.setSecondQFirstPoint("승리");
        }

        if(aTeamStat.getThirdQFirstPoint().equals("승리")){
            bTeamStat.setThirdQFirstPoint("패배");
        } else {
            bTeamStat.setThirdQFirstPoint("승리");
        }

        if(aTeamStat.getFourthQFirstPoint().equals("승리")){
            bTeamStat.setFourthQFirstPoint("패배");
        } else {
            bTeamStat.setFourthQFirstPoint("승리");
        }

        //첫3점
        if(aTeamStat.getFirstQFirstThreePoint().equals("승리")){
            bTeamStat.setFirstQFirstThreePoint("패배");
        } else {
            bTeamStat.setFirstQFirstThreePoint("승리");
        }

        if(aTeamStat.getSecondQFirstThreePoint().equals("승리")){
            bTeamStat.setSecondQFirstThreePoint("패배");
        } else {
            bTeamStat.setSecondQFirstThreePoint("승리");
        }

        if(aTeamStat.getThirdQFirstThreePoint().equals("승리")){
            bTeamStat.setThirdQFirstThreePoint("패배");
        } else {
            bTeamStat.setThirdQFirstThreePoint("승리");
        }

        if(aTeamStat.getFourthQFirstThreePoint().equals("승리")){
            bTeamStat.setFourthQFirstThreePoint("패배");
        } else {
            bTeamStat.setFourthQFirstThreePoint("승리");
        }

        //자유투
        if(aTeamStat.getFirstQFirstFreeTwo().equals("승리")){
            bTeamStat.setFirstQFirstFreeTwo("패배");
        } else {
            bTeamStat.setFirstQFirstFreeTwo("승리");
        }

        if(aTeamStat.getSecondQFirstFreeTwo().equals("승리")){
            bTeamStat.setSecondQFirstFreeTwo("패배");
        } else {
            bTeamStat.setSecondQFirstFreeTwo("승리");
        }

        if(aTeamStat.getThirdQFirstFreeTwo().equals("승리")){
            bTeamStat.setThirdQFirstFreeTwo("패배");
        } else {
            bTeamStat.setThirdQFirstFreeTwo("승리");
        }

        if(aTeamStat.getFourthQFirstFreeTwo().equals("승리")){
            bTeamStat.setFourthQFirstFreeTwo("패배");
        } else {
            bTeamStat.setFourthQFirstFreeTwo("승리");
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
    public static void main(String[] args) {
        Basketball basketball = new Basketball();
        try {
//            basketball.getCategoryList();
//            basketball.getAllMatch();
            basketball.updateBasketBall();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;
    }

}
