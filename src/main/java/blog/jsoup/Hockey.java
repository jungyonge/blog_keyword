package blog.jsoup;

import blog.model.BasketballModel;
import blog.model.HockeyModel;
import blog.model.VolleyballModel;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
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

public class Hockey {



    private SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    // URLConnection 연결로 데이터 호출
    public String requestURLToString(String url) throws IOException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36");
        con.setRequestProperty("referer", "http://hackers-say.com/frontend/home");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        con.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
        con.setRequestProperty("Cookie","ci_session=a5i6kt83l3g1d4ultj36kijln6f6vc3k");
        con.setRequestProperty("Host","hackers-say.com");
        con.setRequestProperty("Upgrade-Insecure-Requests","1");



        con.setConnectTimeout(2000);


        int code = con.getResponseCode();
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
        String url = "https://livescore.co.kr/sports/score_board/hockey/view.php?date=";
        int date = 0;

        while (true){

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(2019, 9,01);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            cal.add(Calendar.DATE, date);
            System.out.println("after: " + df.format(cal.getTime()));

        //2019년 10월 2일 ~ 2020년 6월 라스에는 4월 5일까지
            if(df.format(cal.getTime()).equals("2020-04-06")){
                System.out.println("시즌끝");
                break;
            }

            int dayNum = cal.get(Calendar.DAY_OF_WEEK);
            String dayOfWeek = getDayoOfWeek(dayNum);

            System.out.println(url + df.format(cal.getTime()));
            rootHtml = requestURLToString(url + df.format(cal.getTime()));

            Document rootDoc = Jsoup.parse(rootHtml);

            for (Element element : rootDoc.select("div#score_board div.score_tbl_individual")) {
                HockeyModel aTeamStat = new HockeyModel();
                HockeyModel bTeamStat = new HockeyModel();

                int i = 0;

                String league = element.select("thead tr th.reague").text();

                if (league.equals("NHL")) {

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
                    aTeamStat.setLeague(league);
                    bTeamStat.setLeague(league);

                    if(aTeamStat.getATeam().contains("디비전")){
                        continue;
                    }

                }


                if(aTeamStat.getGameId() == null|| bTeamStat.getGameId() == null){
                    continue;
                } else {
                    System.out.println(aTeamStat);
                    System.out.println(bTeamStat);
                    setalarmDAO.insertHockeyMatch(aTeamStat);
                    setalarmDAO.insertHockeyMatch(bTeamStat);
                }

            }
            date++;
        }

    }

    public void updateHockeyStat() throws IOException {

        JSONArray jsonArray = new JSONArray();
        HockeyModel aTeamStat = new HockeyModel();
        HockeyModel bTeamStat = new HockeyModel();

        String rootHtml = "";
        String url = "https://livescore.co.kr/sports/score_board/hockey/view.php?date=";
        int date = 0;
        Calendar curDate = Calendar.getInstance();
        curDate.setTime(new Date());
        curDate.add(Calendar.DATE, 1);

        while (true){
            Calendar startDate = Calendar.getInstance();

//            startDate.set(2019, 9,1);
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

                String league = element.select("thead tr th.reague").text();
                if (league.equals("NHL")) {
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

                    if(aTeamStat.getATeam().contains("디비전")){
                        continue;
                    }

                    aTeamStat.setLeague(league);

                    String[] arrayHandi = element.select("tbody > tr > td.line").text().split(" ");

                    if (arrayHandi.length > 1) {
                        aTeamStat.setPointLine(Double.valueOf(arrayHandi[0]));
                        aTeamStat.setHandiCap(Double.valueOf(arrayHandi[1]));
                    } else {
                        aTeamStat.setPointLine(0.0);
                        aTeamStat.setHandiCap(0.0);
                    }

                    String[] arrayTotalScore = element.select("tbody > tr > td.score").text().split(" ");

                    if (arrayTotalScore.length > 1) {
                        aTeamStat.setBTeamTotalPoint(Integer.valueOf(arrayTotalScore[0]));
                        aTeamStat.setATeamTotalPoint(Integer.valueOf(arrayTotalScore[1]));

                        bTeamStat.setATeamTotalPoint(Integer.valueOf(arrayTotalScore[0]));
                        bTeamStat.setBTeamTotalPoint(Integer.valueOf(arrayTotalScore[1]));
                    } else {
                        aTeamStat.setBTeamTotalPoint(0);
                        aTeamStat.setATeamTotalPoint(0);

                        bTeamStat.setBTeamTotalPoint(0);
                        bTeamStat.setATeamTotalPoint(0);
                    }

                    String[] arrayFirstScore = element.select("tbody > tr > td.s").text().split(" ");

                    if (arrayFirstScore.length == 9) {
                        bTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setExtendQPoint(Integer.valueOf(arrayFirstScore[3]));
                        bTeamStat.setShotoutQPoint(0);

                        aTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setExtendQPoint(Integer.valueOf(arrayFirstScore[8]));
                        aTeamStat.setShotoutQPoint(0);

                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));

                    } else if(arrayFirstScore.length == 10){

                        bTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setExtendQPoint(Integer.valueOf(arrayFirstScore[3]));
                        bTeamStat.setShotoutQPoint(Integer.valueOf(arrayFirstScore[4]));

                        aTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setExtendQPoint(Integer.valueOf(arrayFirstScore[8]));
                        aTeamStat.setShotoutQPoint(Integer.valueOf(arrayFirstScore[9]));

                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));


                    } else {
                        bTeamStat.setFirstQPoint(0);
                        bTeamStat.setSecondQPoint(0);
                        bTeamStat.setThirdQPoint(0);
                        bTeamStat.setExtendQPoint(0);
                        bTeamStat.setShotoutQPoint(0);

                        aTeamStat.setFirstQPoint(0);
                        aTeamStat.setSecondQPoint(0);
                        aTeamStat.setThirdQPoint(0);
                        aTeamStat.setExtendQPoint(0);
                        aTeamStat.setShotoutQPoint(0);
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



                    double handi = aTeamStat.getHandiCap() / 3;
                    int handiInt = (int) handi;
                    double pointHandi = handi - handiInt;

                    if ((pointHandi <= 0.333 && pointHandi >= 0.001) || (pointHandi >= -0.333 && pointHandi <= -0.001)) {
                        if(pointHandi < 0){
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.ceil(aTeamStat.getHandiCap() / 3)));
                        }else {
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.floor(aTeamStat.getHandiCap() / 3)));
                        }
                    } else if((pointHandi <= 0.999 && pointHandi >= 0.666) || (pointHandi >= -0.999 && pointHandi <= -0.666)) {
                        if(pointHandi < 0){
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.floor(aTeamStat.getHandiCap() / 3)));
                        }else {
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.ceil(aTeamStat.getHandiCap() / 3)));
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


//                    double point = aTeamStat.getPointLine() / 3;
//                    int pointInt = (int) point;
//                    double pointLine = point - pointInt;
//
//                    if ((pointLine <= 0.333 && pointLine >= 0.001)) {
//                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.floor(aTeamStat.getPointLine() / 3)));
//                    } else if((pointLine <= 0.999 && pointLine >= 0.666)) {
//                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.ceil(aTeamStat.getPointLine() / 3)));
//                    } else if((pointLine <= 0.665 && pointLine >= 0.334)) {
//                        aTeamStat.setFirstQPointLine(pointInt + 0.5); } else {
//                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.round(aTeamStat.getPointLine() / 3)));
//                    }

                    if(aTeamStat.getPointLine() >= 7){
                        aTeamStat.setFirstQPointLine(2.0);
                    }else if(aTeamStat.getPointLine() > 5 || aTeamStat.getPointLine() < 7) {
                        aTeamStat.setFirstQPointLine(1.5);
                    }else if(aTeamStat.getPointLine() <= 5){
                        aTeamStat.setFirstQPointLine(1.0);
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
                        aTeamStat.setExtendQTotalPoint(0);
                        aTeamStat.setShotoutQTotalPoint(0);

                    } else if (arrayQTotalScore.length == 4) {

                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setExtendQTotalPoint(Integer.valueOf(arrayQTotalScore[3]));
                        aTeamStat.setShotoutQTotalPoint(0);

                    } else if (arrayQTotalScore.length == 5) {
                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setExtendQTotalPoint(Integer.valueOf(arrayQTotalScore[3]));
                        aTeamStat.setShotoutQTotalPoint(Integer.valueOf(arrayQTotalScore[4]));
                    }else  {
                        aTeamStat.setFirstQTotalPoint(0);
                        aTeamStat.setSecondQTotalPoint(0);
                        aTeamStat.setThirdQTotalPoint(0);
                        aTeamStat.setExtendQTotalPoint(0);
                        aTeamStat.setShotoutQTotalPoint(0);

                    }


                    String test = element.select("tbody > tr > td.f.ico_linescore").text();
                    for (Element element1 : element.select("tbody > tr > td.f.ico_linescore")) {
                        if (i == 0) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setFirstPoint("패배");
                            } else {
                                aTeamStat.setFirstPoint("승리");
                            }
                        }
                        i++;
                    }

                    setBteamStat(aTeamStat, bTeamStat);

//                    System.out.println(aTeamStat);
//                    System.out.println(bTeamStat);
                    setalarmDAO.updateHockeyStat(aTeamStat);
                    setalarmDAO.updateHockeyStat(bTeamStat);
                }

            }

            date++;
        }
    }

    public void getTomorrowMatch() throws Exception{

        JSONArray jsonArray = new JSONArray();

        String rootHtml = "";
        String url = "https://livescore.co.kr/sports/score_board/hockey/view.php?date=";
        int date = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


        cal.add(Calendar.DATE, 1);
        System.out.println("after: " + df.format(cal.getTime()));



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
            if (league.equals("NHL") ) {


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



            }

            if(aTeamStat.getGameId() != null && bTeamStat.getGameId() != null){
                    System.out.println(aTeamStat);
                    System.out.println(bTeamStat);
                    setalarmDAO.updateTomorrowHockeyStat(aTeamStat);
                    setalarmDAO.updateTomorrowHockeyStat(bTeamStat);

            }

        }
    }



    public void getHockeySummary(){
        setalarmDAO.truncateHockeySummary();
        setalarmDAO.insertHockeySummary();

        setalarmDAO.truncateHockeyOddSummary();
        setalarmDAO.insertHockeyOddSummary();

        setalarmDAO.truncateHockeyWeekSummary();
        setalarmDAO.insertHockeyWeekSummary();

        setalarmDAO.truncateHockeyRestSummary();
        setalarmDAO.insertHockeyRestSummary();

        setalarmDAO.truncateHockeyGroundSummary();
        setalarmDAO.insertHockeyGroundSummary();

    }
    public void getCategoryList() throws IOException, ParseException, InterruptedException {
        JSONArray jsonArray = new JSONArray();
        HockeyModel aTeamStat = new HockeyModel();
        HockeyModel bTeamStat = new HockeyModel();

        String rootHtml = "";
        String url = "https://livescore.co.kr/sports/score_board/hockey/view.php?date=";


        for (int date = 1; date < 100; date++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            cal.add(Calendar.DATE, -date);
            System.out.println("after: " + df.format(cal.getTime()));
            int dayNum = cal.get(Calendar.DAY_OF_WEEK);
            String dayOfWeek = getDayoOfWeek(dayNum);

            System.out.println(url + df.format(cal.getTime()));

            if(df.format(cal.getTime()).equals("2019-10-02")){
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
                if (league.equals("NHL")) {
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

                    aTeamStat.setLeague(league);

                    String[] arrayHandi = element.select("tbody > tr > td.line").text().split(" ");

                    if (arrayHandi.length > 1) {
                        aTeamStat.setPointLine(Double.valueOf(arrayHandi[0]));
                        aTeamStat.setHandiCap(Double.valueOf(arrayHandi[1]));
                    } else {
                        aTeamStat.setPointLine(0.0);
                        aTeamStat.setHandiCap(0.0);
                    }

                    String[] arrayTotalScore = element.select("tbody > tr > td.score").text().split(" ");

                    if (arrayTotalScore.length > 1) {
                        aTeamStat.setBTeamTotalPoint(Integer.valueOf(arrayTotalScore[0]));
                        aTeamStat.setATeamTotalPoint(Integer.valueOf(arrayTotalScore[1]));

                        bTeamStat.setATeamTotalPoint(Integer.valueOf(arrayTotalScore[0]));
                        bTeamStat.setBTeamTotalPoint(Integer.valueOf(arrayTotalScore[1]));
                    } else {
                        aTeamStat.setBTeamTotalPoint(0);
                        aTeamStat.setATeamTotalPoint(0);

                        bTeamStat.setBTeamTotalPoint(0);
                        bTeamStat.setATeamTotalPoint(0);
                    }

                    String[] arrayFirstScore = element.select("tbody > tr > td.s").text().split(" ");

                    if (arrayFirstScore.length == 9) {
                        bTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setExtendQPoint(Integer.valueOf(arrayFirstScore[3]));
                        bTeamStat.setShotoutQPoint(0);

                        aTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setExtendQPoint(Integer.valueOf(arrayFirstScore[8]));
                        aTeamStat.setShotoutQPoint(0);

                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));

                    } else if(arrayFirstScore.length == 10){

                        bTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setExtendQPoint(Integer.valueOf(arrayFirstScore[3]));
                        bTeamStat.setShotoutQPoint(Integer.valueOf(arrayFirstScore[4]));

                        aTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setExtendQPoint(Integer.valueOf(arrayFirstScore[8]));
                        aTeamStat.setShotoutQPoint(Integer.valueOf(arrayFirstScore[9]));

                        aTeamStat.setBTeamFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setATeamFirstQPoint(Integer.valueOf(arrayFirstScore[5]));


                    } else {
                        bTeamStat.setFirstQPoint(0);
                        bTeamStat.setSecondQPoint(0);
                        bTeamStat.setThirdQPoint(0);
                        bTeamStat.setExtendQPoint(0);
                        bTeamStat.setShotoutQPoint(0);

                        aTeamStat.setFirstQPoint(0);
                        aTeamStat.setSecondQPoint(0);
                        aTeamStat.setThirdQPoint(0);
                        aTeamStat.setExtendQPoint(0);
                        aTeamStat.setShotoutQPoint(0);
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



                    double handi = aTeamStat.getHandiCap() / 3;
                    int handiInt = (int) handi;
                    double pointHandi = handi - handiInt;

                    if ((pointHandi <= 0.333 && pointHandi >= 0.001) || (pointHandi >= -0.333 && pointHandi <= -0.001)) {
                        if(pointHandi < 0){
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.ceil(aTeamStat.getHandiCap() / 3)));
                        }else {
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.floor(aTeamStat.getHandiCap() / 3)));
                        }
                    } else if((pointHandi <= 0.999 && pointHandi >= 0.666) || (pointHandi >= -0.999 && pointHandi <= -0.666)) {
                        if(pointHandi < 0){
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.floor(aTeamStat.getHandiCap() / 3)));
                        }else {
                            aTeamStat.setFirstQHandiCap(Double.valueOf(Math.ceil(aTeamStat.getHandiCap() / 3)));
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


//                    double point = aTeamStat.getPointLine() / 3;
//                    int pointInt = (int) point;
//                    double pointLine = point - pointInt;
//
//                    if ((pointLine <= 0.333 && pointLine >= 0.001)) {
//                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.floor(aTeamStat.getPointLine() / 3)));
//                    } else if((pointLine <= 0.999 && pointLine >= 0.666)) {
//                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.ceil(aTeamStat.getPointLine() / 3)));
//                    } else if((pointLine <= 0.665 && pointLine >= 0.334)) {
//                        aTeamStat.setFirstQPointLine(pointInt + 0.5); } else {
//                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.round(aTeamStat.getPointLine() / 3)));
//                    }

                    if(aTeamStat.getPointLine() >= 7){
                        aTeamStat.setFirstQPointLine(2.0);
                    }else if(aTeamStat.getPointLine() > 5 || aTeamStat.getPointLine() < 7) {
                        aTeamStat.setFirstQPointLine(1.5);
                    }else if(aTeamStat.getPointLine() <= 5){
                        aTeamStat.setFirstQPointLine(1.0);
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
                        aTeamStat.setExtendQTotalPoint(0);
                        aTeamStat.setShotoutQTotalPoint(0);

                    } else if (arrayQTotalScore.length == 4) {

                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setExtendQTotalPoint(Integer.valueOf(arrayQTotalScore[3]));
                        aTeamStat.setShotoutQTotalPoint(0);

                    } else if (arrayQTotalScore.length == 5) {
                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setExtendQTotalPoint(Integer.valueOf(arrayQTotalScore[3]));
                        aTeamStat.setShotoutQTotalPoint(Integer.valueOf(arrayQTotalScore[4]));
                    }else  {
                        aTeamStat.setFirstQTotalPoint(0);
                        aTeamStat.setSecondQTotalPoint(0);
                        aTeamStat.setThirdQTotalPoint(0);
                        aTeamStat.setExtendQTotalPoint(0);
                        aTeamStat.setShotoutQTotalPoint(0);

                    }


                    String test = element.select("tbody > tr > td.f.ico_linescore").text();
                    for (Element element1 : element.select("tbody > tr > td.f.ico_linescore")) {
                        if (i == 0) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setFirstPoint("패배");
                            } else {
                                aTeamStat.setFirstPoint("승리");
                            }
                        }
                        i++;
                    }


                    setBteamStat(aTeamStat, bTeamStat);


                    System.out.println(aTeamStat);
                    System.out.println(bTeamStat);
                    setalarmDAO.insertHockeyStat(aTeamStat);
                    setalarmDAO.insertHockeyStat(bTeamStat);
                }

            }
        }
    }

    public void setBteamStat(HockeyModel aTeamStat , HockeyModel bTeamStat){
        bTeamStat.setLeague(aTeamStat.getLeague());


        bTeamStat.setPointLine(aTeamStat.getPointLine());
        bTeamStat.setHandiCap(-(aTeamStat.getHandiCap()));



        if (aTeamStat.getHandiCapResult().equals("패배")) {
            bTeamStat.setHandiCapResult("승리");
        } else if (aTeamStat.getHandiCapResult().equals("승리")) {
            bTeamStat.setHandiCapResult("패배");
        } else {
            bTeamStat.setHandiCapResult("적특");
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


        bTeamStat.setFirstQTotalPoint(aTeamStat.getFirstQTotalPoint());
        bTeamStat.setSecondQTotalPoint(aTeamStat.getSecondQTotalPoint());
        bTeamStat.setThirdQTotalPoint(aTeamStat.getThirdQTotalPoint());
        bTeamStat.setExtendQTotalPoint(aTeamStat.getExtendQTotalPoint());
        bTeamStat.setShotoutQTotalPoint(aTeamStat.getShotoutQTotalPoint());

        bTeamStat.setATeamFirstQPoint(aTeamStat.getBTeamFirstQPoint());
        bTeamStat.setBTeamFirstQPoint(aTeamStat.getATeamFirstQPoint());

        bTeamStat.setTime(aTeamStat.getTime());
        bTeamStat.setBTeam(aTeamStat.getATeam());
        bTeamStat.setATeam(aTeamStat.getBTeam());


        //첫득점
        if(aTeamStat.getFirstPoint().equals("승리")){
            bTeamStat.setFirstPoint("패배");
        } else {
            bTeamStat.setFirstPoint("승리");
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
        Hockey hockey = new Hockey();
        try {
//            hockey.getAllMatch();
////            hockey.updateHockeyStat();
//            hockey.getHockeySummary();
            hockey.requestURLToString("http://hackers-say.com/courses/details/1");

        } catch (Exception e) {

            e.printStackTrace();
        }
        ;
    }
}
