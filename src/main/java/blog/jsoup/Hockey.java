package blog.jsoup;

import blog.model.HockeyModel;
import blog.model.VolleyballModel;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
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
            Thread.sleep(500);

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
            hockey.getCategoryList();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ;
    }
}
