package blog.coupang;

import blog.gmail.WebSendMail;
import blog.jsoup.*;
import blog.model.BaseballModel;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import blog.util.JxlsMakeExcel;
import blog.util.JxlsMakeExcelText;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.atan;
import static java.lang.Math.round;

public final class NamedGetAPI {
    private final static String REQUEST_METHOD = "GET";
    private final static String DOMAIN = "sports-api.picksmatch.com";
    private final static String URL = "/named/v1/sports/baseball/games/?";
    private final static String API_KEY = "1rar2zCZvKjp";
    private final static String API_NAME = "named_score";
    private SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    public void allBaseballMatch() throws IOException, ParseException {
        StringEntity entity = new StringEntity("", "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        long unixTime = System.currentTimeMillis() / 1000;

        int date1 = 0;

        while (true){
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(2020, 4, 01);
            DateFormat df = new SimpleDateFormat("yyyyMMdd");

            cal.add(Calendar.DATE, date1);
            String matchDate = df.format(cal.getTime());
            if(df.format(cal.getTime()).equals("20201005")){
                System.out.println("시즌끝");
                break;
            }

            String url = "https://api.picksmatch.com/v1.0/sports/baseball/games?date=2020-07-01&status=ALL";
//                          https://api.picksmatch.com/v1.0/sports/baseball/games?date=2020-07-03&status=ALL
            org.apache.http.HttpHost host = org.apache.http.HttpHost.create("api.picksmatch.com");
            org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                    .get("/v1.0/sports/baseball/games?date=2020-07-01&status=ALL").setEntity(entity)
                    .addHeader("accept", "*/*")
//                    .addHeader("oki-api-key", API_KEY)
//                    .addHeader("oki-api-name", API_NAME)
                    .addHeader("origin", "https://sports.picksmatch.com")
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36")
//                    .addParameter("broadcast", "true")
//                    .addParameter("broadcastLatest", "true")
//                    .addParameter("odds", "true")
//                    .addParameter("scores", "true")
//                    .addParameter("specials", "true")
//                    .addParameter("seasonTeamStat", "true")
//                    .addParameter("startDate", matchDate)
//                    .addParameter("endDate", matchDate)
//                    .addParameter("v", String.valueOf(unixTime))
                    .build();


            org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

            try {

                JSONParser jsonParser = new JSONParser();

                String json = EntityUtils.toString(httpResponse.getEntity());

                //JSON데이터를 넣어 JSON Object 로 만들어 준다.
                JSONArray jsonObject = new JSONArray(json);

                //books의 배열을 추출
//                JSONArray matchArr = jsonObject.getJSONArray("response");

                for (int i = 0; i < jsonObject.length(); i++) {
                    BaseballModel aTeamModel = new BaseballModel();
                    BaseballModel bTeamModel = new BaseballModel();

                    JSONObject matchObject = jsonObject.getJSONObject(i);

//                    String gameStatus = matchObject.getJSONArray("broadcasts").getJSONObject(0).getString("playText");
//                    if (gameStatus.contains("취소")) {
//                        continue;
//                    }

                    aTeamModel.setGameId(String.valueOf(matchObject.getInt("id")));
                    bTeamModel.setGameId(String.valueOf(matchObject.getInt("id")));
                    aTeamModel.setLeague(matchObject.getJSONObject("league").getString("name"));
                    bTeamModel.setLeague(matchObject.getJSONObject("league").getString("name"));

                    String startDatetime = matchObject.getString("startDatetime");
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                    String[] startDatetimeArr = startDatetime.split("T");

                    cal = Calendar.getInstance();
                    cal.setTime(format1.parse(startDatetime));
                    int dayNum = cal.get(Calendar.DAY_OF_WEEK);

                    String date = startDatetimeArr[0];
                    String dayOfWeek = getDayoOfWeek(dayNum);
                    String time = startDatetimeArr[1].substring(0, 5);

                    aTeamModel.setDate(date);
                    aTeamModel.setDayOfWeek(dayOfWeek);
                    aTeamModel.setTime(time);

                    bTeamModel.setDate(date);
                    bTeamModel.setDayOfWeek(dayOfWeek);
                    bTeamModel.setTime(time);

                    aTeamModel.setGround("홈");
                    bTeamModel.setGround("원정");

//                    aTeamModel.setStadium(matchObject.getJSONObject("venue").getString("name"));
//                    bTeamModel.setStadium(matchObject.getJSONObject("venue").getString("name"));

                    JSONArray groundArr = matchObject.getJSONArray("gameTeams");
                    JSONObject homeTeam = null;
                    JSONObject awayTeam = null;
                    if (groundArr.getJSONObject(0).getString("locationType").equals("AWAY")){
                        homeTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(1);
                        awayTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(0);
                    }else {
                        homeTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(0);
                        awayTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(1);
                    }

                    if(aTeamModel.getLeague().contains("퓨처스") || aTeamModel.getLeague().contains("KBO")){
                        aTeamModel.setATeam(homeTeam.getJSONObject("team").getString("nickname"));
                        aTeamModel.setBTeam(awayTeam.getJSONObject("team").getString("nickname"));
                    } else {
                        aTeamModel.setATeam(homeTeam.getJSONObject("team").getString("name"));
                        aTeamModel.setBTeam(awayTeam.getJSONObject("team").getString("name"));
                    }

                    if (aTeamModel.getATeam().equals("")){
                        continue;
                    }

                    bTeamModel.setATeam(aTeamModel.getBTeam());
                    bTeamModel.setBTeam(aTeamModel.getATeam());

//                    setalarmDAO.insertBaseballMatch(aTeamModel);
//                    setalarmDAO.insertBaseballMatch(bTeamModel);

                }
            } catch (Exception e) {
                throw e;
            }
            date1++;

        }

    }

    public void updateTomorrowMatch() throws Exception {
        StringEntity entity = new StringEntity("", "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        long unixTime = System.currentTimeMillis() / 1000;


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(2020, 4, 01);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        cal.add(Calendar.DATE, 1);
        String matchDate = df.format(cal.getTime());

        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .get(URL).setEntity(entity)
                .addHeader("accept", "*/*")
                .addHeader("oki-api-key", API_KEY)
                .addHeader("oki-api-name", API_NAME)
                .addHeader("origin", "https://sports.picksmatch.com")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36")
                .addParameter("broadcast", "true")
                .addParameter("broadcastLatest", "true")
                .addParameter("odds", "true")
                .addParameter("scores", "true")
                .addParameter("specials", "true")
                .addParameter("seasonTeamStat", "true")
                .addParameter("startDate", matchDate)
                .addParameter("endDate", matchDate)
                .addParameter("v", String.valueOf(unixTime))
                .build();


        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

        try {
            JSONParser jsonParser = new JSONParser();

            String json = EntityUtils.toString(httpResponse.getEntity());

            //JSON데이터를 넣어 JSON Object 로 만들어 준다.
            JSONObject jsonObject = new JSONObject(json);

            //books의 배열을 추출
            JSONArray matchArr = jsonObject.getJSONArray("response");

            for (int i = 0; i < matchArr.length(); i++) {
                BaseballModel aTeamModel = new BaseballModel();
                BaseballModel bTeamModel = new BaseballModel();

                JSONObject matchObject = matchArr.getJSONObject(i);



            }


        }catch (Exception e){

        }
    }

    public void updateBaseball() throws Exception{
        StringEntity entity = new StringEntity("", "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        long unixTime = System.currentTimeMillis() / 1000;

        Calendar curDate = Calendar.getInstance();
        curDate.setTime(new Date());
        curDate.add(Calendar.DATE, 1);

        int date = 0;
        while (true) {
            Calendar startDate = Calendar.getInstance();
//            startDate.set(2020,4,03);
            startDate.setTime(new Date());
            startDate.add(Calendar.DATE, -2);

            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df2 = new SimpleDateFormat("yyyy-M-d");



            startDate.add(Calendar.DATE, date);
            if (df.format(startDate.getTime()).equals(df.format(curDate.getTime()))) {
                System.out.println("Today");
                break;
            }

            String matchDate = df.format(startDate.getTime());
            String matchDate1 = df1.format(startDate.getTime());
            String matchDate2 = df2.format(startDate.getTime());



            org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
            org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                    .get(URL).setEntity(entity)
                    .addHeader("accept", "*/*")
                    .addHeader("oki-api-key", API_KEY)
                    .addHeader("oki-api-name", API_NAME)
                    .addHeader("origin", "https://sports.picksmatch.com")
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36")
                    .addParameter("broadcast", "true")
                    .addParameter("broadcastLatest", "true")
                    .addParameter("odds", "true")
                    .addParameter("scores", "true")
                    .addParameter("specials", "true")
                    .addParameter("seasonTeamStat", "true")
                    .addParameter("startDate",matchDate )
                    .addParameter("endDate", matchDate)
                    .addParameter("v", String.valueOf(unixTime))
                    .build();


            org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

            try {
                JSONParser jsonParser = new JSONParser();

                String json = EntityUtils.toString(httpResponse.getEntity());

                //JSON데이터를 넣어 JSON Object 로 만들어 준다.
                JSONObject jsonObject = new JSONObject(json);

                //books의 배열을 추출
                JSONArray matchArr = jsonObject.getJSONArray("response");

                for (int i = 0; i < matchArr.length(); i++) {
                    BaseballModel aTeamModel = new BaseballModel();
                    BaseballModel bTeamModel = new BaseballModel();

                    JSONObject matchObject = matchArr.getJSONObject(i);


                    aTeamModel.setGameId(String.valueOf(matchObject.getInt("id")));
                    bTeamModel.setGameId(String.valueOf(matchObject.getInt("id")));
                    aTeamModel.setLeague(matchObject.getJSONObject("league").getString("name"));
                    bTeamModel.setLeague(matchObject.getJSONObject("league").getString("name"));

                    String startDatetime = matchObject.getString("startDatetime");
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                    String[] startDatetimeArr = startDatetime.split("T");

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(format1.parse(startDatetime));
                    int dayNum = cal.get(Calendar.DAY_OF_WEEK);

                    String date1 = startDatetimeArr[0];
                    String dayOfWeek = getDayoOfWeek(dayNum);
                    String time = startDatetimeArr[1].substring(0, 5);

                    aTeamModel.setDate(date1);
                    aTeamModel.setDayOfWeek(dayOfWeek);
                    aTeamModel.setTime(time);

                    bTeamModel.setDate(date1);
                    bTeamModel.setDayOfWeek(dayOfWeek);
                    bTeamModel.setTime(time);

                    aTeamModel.setGround("홈");
                    bTeamModel.setGround("원정");


                    JSONArray groundArr = matchObject.getJSONArray("gameTeams");
                    JSONObject homeTeam = null;
                    JSONObject awayTeam = null;
                    if (groundArr.getJSONObject(0).getString("locationType").equals("AWAY")){
                        homeTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(1);
                        awayTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(0);
                    }else {
                        homeTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(0);
                        awayTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(1);
                    }


                    if(aTeamModel.getLeague().contains("퓨처스") || aTeamModel.getLeague().contains("KBO")){
                        aTeamModel.setATeam(homeTeam.getJSONObject("team").getString("nickname"));
                        aTeamModel.setBTeam(awayTeam.getJSONObject("team").getString("nickname"));
                    } else {
                        aTeamModel.setATeam(homeTeam.getJSONObject("team").getString("name"));
                        aTeamModel.setBTeam(awayTeam.getJSONObject("team").getString("name"));
                    }


                    bTeamModel.setATeam(aTeamModel.getBTeam());
                    bTeamModel.setBTeam(aTeamModel.getATeam());

                    String gameStatus = matchObject.getJSONArray("broadcasts").getJSONObject(0).getString("playText");
                    if (gameStatus.contains("취소") || gameStatus.contains("콜드") || gameStatus.contains("우천")) {
                        aTeamModel.setATeamTotalPoint(99);
                        aTeamModel.setBTeamTotalPoint(99);

                        bTeamModel.setATeamTotalPoint(99);
                        bTeamModel.setBTeamTotalPoint(99);

                        setalarmDAO.updateBaseballStat(aTeamModel);
                        setalarmDAO.updateBaseballStat(bTeamModel);

                        continue;
                    }

                    boolean checkNull = matchObject.getJSONObject("gameStatus").isNull("homeStarterPlayer");

                    if (!checkNull) {
                        aTeamModel.setATeamPitcher(matchObject.getJSONObject("gameStatus").getJSONObject("homeStarterPlayer").getString("displayName"));
                        aTeamModel.setBTeamPitcher(matchObject.getJSONObject("gameStatus").getJSONObject("awayStarterPlayer").getString("displayName"));
                    } else {
                        aTeamModel.setATeamPitcher("");
                        aTeamModel.setBTeamPitcher("");
                    }



                    bTeamModel.setATeamPitcher(aTeamModel.getBTeamPitcher());
                    bTeamModel.setBTeamPitcher(aTeamModel.getATeamPitcher());
                    int extendScore = 0;
                    if(homeTeam.getJSONArray("scores").length() > 9){
                        for (int id = 9 ; id < homeTeam.getJSONArray("scores").length() ; id ++){
                            extendScore = extendScore + homeTeam.getJSONArray("scores").getJSONObject(id).getInt("score");
                        }
                        aTeamModel.setExtendScore(extendScore);
                        aTeamModel.setExtendYn(true);
                    }else {
                        aTeamModel.setExtendScore(extendScore);
                        aTeamModel.setExtendYn(false);

                    }

                    extendScore = 0;
                    if(awayTeam.getJSONArray("scores").length() > 9){
                        for (int id = 9 ; id < awayTeam.getJSONArray("scores").length() ; id ++){
                            extendScore = extendScore + awayTeam.getJSONArray("scores").getJSONObject(id).getInt("score");
                        }
                        bTeamModel.setExtendScore(extendScore);
                        bTeamModel.setExtendYn(true);

                    }else {
                        bTeamModel.setExtendScore(extendScore);
                        bTeamModel.setExtendYn(false);

                    }

                    aTeamModel.setFirstScore(homeTeam.getJSONArray("scores").getJSONObject(0).getInt("score"));
                    aTeamModel.setSecondScore(homeTeam.getJSONArray("scores").getJSONObject(1).getInt("score"));
                    aTeamModel.setThirdScore(homeTeam.getJSONArray("scores").getJSONObject(2).getInt("score"));
                    aTeamModel.setFourthScore(homeTeam.getJSONArray("scores").getJSONObject(3).getInt("score"));
                    aTeamModel.setFifthScore(homeTeam.getJSONArray("scores").getJSONObject(4).getInt("score"));
                    aTeamModel.setSixthScore(homeTeam.getJSONArray("scores").getJSONObject(5).getInt("score"));
                    aTeamModel.setSeventhScore(homeTeam.getJSONArray("scores").getJSONObject(6).getInt("score"));
                    aTeamModel.setEighthScore(homeTeam.getJSONArray("scores").getJSONObject(7).getInt("score"));
                    if (homeTeam.getJSONArray("scores").length() > 8) {
                        aTeamModel.setNinthScore(homeTeam.getJSONArray("scores").getJSONObject(8).getInt("score"));
                    } else {
                        aTeamModel.setNinthScore(0);
                    }

                    bTeamModel.setFirstScore(awayTeam.getJSONArray("scores").getJSONObject(0).getInt("score"));
                    bTeamModel.setSecondScore(awayTeam.getJSONArray("scores").getJSONObject(1).getInt("score"));
                    bTeamModel.setThirdScore(awayTeam.getJSONArray("scores").getJSONObject(2).getInt("score"));
                    bTeamModel.setFourthScore(awayTeam.getJSONArray("scores").getJSONObject(3).getInt("score"));
                    bTeamModel.setFifthScore(awayTeam.getJSONArray("scores").getJSONObject(4).getInt("score"));
                    bTeamModel.setSixthScore(awayTeam.getJSONArray("scores").getJSONObject(5).getInt("score"));
                    bTeamModel.setSeventhScore(awayTeam.getJSONArray("scores").getJSONObject(6).getInt("score"));
                    bTeamModel.setEighthScore(awayTeam.getJSONArray("scores").getJSONObject(7).getInt("score"));
                    if (awayTeam.getJSONArray("scores").length() > 8) {
                        bTeamModel.setNinthScore(awayTeam.getJSONArray("scores").getJSONObject(8).getInt("score"));
                    } else {
                        bTeamModel.setNinthScore(0);
                    }
                    aTeamModel.setATeamTotalPoint(aTeamModel.getTotalScore());
                    aTeamModel.setBTeamTotalPoint(bTeamModel.getTotalScore());

                    bTeamModel.setATeamTotalPoint(bTeamModel.getTotalScore());
                    bTeamModel.setBTeamTotalPoint(aTeamModel.getTotalScore());

                    aTeamModel.setATeamThirdPoint(aTeamModel.get3InningScore());
                    aTeamModel.setBTeamThirdPoint(bTeamModel.get3InningScore());

                    bTeamModel.setATeamThirdPoint(bTeamModel.get3InningScore());
                    bTeamModel.setBTeamThirdPoint(aTeamModel.get3InningScore());

                    aTeamModel.setATeamFourthPoint(aTeamModel.get4InningScore());
                    aTeamModel.setBTeamFourthPoint(bTeamModel.get4InningScore());

                    bTeamModel.setATeamFourthPoint(bTeamModel.get4InningScore());
                    bTeamModel.setBTeamFourthPoint(aTeamModel.get4InningScore());

                    aTeamModel.setATeamFifthPoint(aTeamModel.get5InningScore());
                    aTeamModel.setBTeamFifthPoint(bTeamModel.get5InningScore());

                    bTeamModel.setATeamFifthPoint(bTeamModel.get5InningScore());
                    bTeamModel.setBTeamFifthPoint(aTeamModel.get5InningScore());

                    JSONObject koreaOdd = matchObject.getJSONArray("odds").getJSONObject(1);
                    JSONObject overseaOdd = matchObject.getJSONArray("odds").getJSONObject(0);
                    double firstInninPointLine = 0.0;
                    if (!aTeamModel.getLeague().equals("퓨처스")) {

                        Double handi = 0.0;
                        Double unOver = 0.0;
//                        JSONObject tempObject = getNewNameAPI(matchDate1,aTeamModel.getGameId());
                        JSONObject tempObject = getOldNameAPI(matchDate2,aTeamModel.getGameId());

                        if(tempObject != null){
                            if(!tempObject.getJSONObject("away").isNull("handiRate")){
                                handi = tempObject.getJSONObject("away").getDouble("handiRate");
                                aTeamModel.setHandiCap(handi);
                                bTeamModel.setHandiCap(handi * -1);
                            } else {
                                aTeamModel.setHandiCap(0.0);
                                bTeamModel.setHandiCap(0.0);
                            }
                            if(!tempObject.getJSONObject("home").isNull("unoverRate")){
                                unOver =  tempObject.getJSONObject("home").getDouble("unoverRate");

                                aTeamModel.setPointLine(unOver);
                                bTeamModel.setPointLine(unOver);

                                firstInninPointLine = unOver / 9;
                            }else {
                                aTeamModel.setPointLine(0.0);
                                bTeamModel.setPointLine(0.0);

                                firstInninPointLine = 0.0;
                            }

                        } else {
                            aTeamModel.setHandiCap(0.0);
                            bTeamModel.setHandiCap(0.0);

                            aTeamModel.setPointLine(0.0);
                            bTeamModel.setPointLine(0.0);

                            firstInninPointLine = 0.0;

                        }
//
//                        if(tempObject != null){
//                            if(tempObject.getJSONObject("odds").getJSONArray("internationalHandicapOdds").length() != 0){
//                                handi = tempObject.getJSONObject("odds").getJSONArray("internationalHandicapOdds").getJSONObject(0).getDouble("optionValue");
//                                aTeamModel.setHandiCap(handi);
//                                bTeamModel.setHandiCap(handi * -1);
//                            } else {
//                                aTeamModel.setHandiCap(0.0);
//                                bTeamModel.setHandiCap(0.0);
//                            }
//                            if(tempObject.getJSONObject("odds").getJSONArray("internationalUnderOverOdds").length() != 0 ){
//                                unOver = tempObject.getJSONObject("odds").getJSONArray("internationalUnderOverOdds").getJSONObject(0).getDouble("optionValue");
//
//                                aTeamModel.setPointLine(unOver);
//                                bTeamModel.setPointLine(unOver);
//
//                                firstInninPointLine = unOver / 9;
//                            }else {
//                                aTeamModel.setPointLine(0.0);
//                                bTeamModel.setPointLine(0.0);
//
//                                firstInninPointLine = 0.0;
//                            }
//
//                        } else {
//                            aTeamModel.setHandiCap(0.0);
//                            bTeamModel.setHandiCap(0.0);
//
//                            aTeamModel.setPointLine(0.0);
//                            bTeamModel.setPointLine(0.0);
//
//                            firstInninPointLine = 0.0;
//
//                        }


                        double thirdPointLine = firstInninPointLine * 4;
                        int thirdPointLineInt = (int) thirdPointLine;
                        double pointLine = thirdPointLine - thirdPointLineInt;

                        if ((pointLine <= 0.333 && pointLine >= 0.001)) {
                            aTeamModel.setThirdPointLine((double) thirdPointLineInt);
                            bTeamModel.setThirdPointLine((double) thirdPointLineInt);

                        } else if ((pointLine <= 0.999 && pointLine >= 0.666)) {
                            aTeamModel.setThirdPointLine((double) (thirdPointLineInt + 1));
                            bTeamModel.setThirdPointLine((double) (thirdPointLineInt + 1));

                        } else if ((pointLine <= 0.665 && pointLine >= 0.334)) {
                            aTeamModel.setThirdPointLine(thirdPointLineInt + 0.5);
                            bTeamModel.setThirdPointLine(thirdPointLineInt + 0.5);

                        } else {
                            aTeamModel.setThirdPointLine((double) round(thirdPointLineInt));
                            bTeamModel.setThirdPointLine((double) round(thirdPointLineInt));

                        }

                        double forthPointLine = firstInninPointLine * 4;
                        int forthPointLineInt = (int) forthPointLine;
                        pointLine = forthPointLine - forthPointLineInt;

                        if ((pointLine <= 0.333 && pointLine >= 0.001)) {
                            aTeamModel.setFourthPointLine((double) forthPointLineInt);
                            bTeamModel.setFourthPointLine((double) forthPointLineInt);

                        } else if ((pointLine <= 0.999 && pointLine >= 0.666)) {
                            aTeamModel.setFourthPointLine((double) (forthPointLineInt + 1));
                            bTeamModel.setFourthPointLine((double) (forthPointLineInt + 1));

                        } else if ((pointLine <= 0.665 && pointLine >= 0.334)) {
                            aTeamModel.setFourthPointLine(forthPointLineInt + 0.5);
                            bTeamModel.setFourthPointLine(forthPointLineInt + 0.5);

                        } else {
                            aTeamModel.setFourthPointLine((double) round(forthPointLine));
                            bTeamModel.setFourthPointLine((double) round(forthPointLine));

                        }

                        double fifthPointLine = firstInninPointLine * 5;
                        int fifthPointLineInt = (int) fifthPointLine;
                        pointLine = fifthPointLine - fifthPointLineInt;

                        if ((pointLine <= 0.333 && pointLine >= 0.001)) {
                            aTeamModel.setFifthPointLine((double) fifthPointLineInt);
                            bTeamModel.setFifthPointLine((double) fifthPointLineInt);

                        } else if ((pointLine <= 0.999 && pointLine >= 0.666)) {
                            aTeamModel.setFifthPointLine((double) (fifthPointLineInt + 1));
                            bTeamModel.setFifthPointLine((double) (fifthPointLineInt + 1));

                        } else if ((pointLine <= 0.665 && pointLine >= 0.334)) {
                            aTeamModel.setFifthPointLine(fifthPointLineInt + 0.5);
                            bTeamModel.setFifthPointLine(fifthPointLineInt + 0.5);

                        } else {
                            aTeamModel.setFifthPointLine((double) round(fifthPointLine));
                            bTeamModel.setFifthPointLine((double) round(fifthPointLine));

                        }


                        if (aTeamModel.getHandiCap() > 0) {
                            aTeamModel.setOdd("역배");
                            bTeamModel.setOdd("정배");

                            aTeamModel.setThirdHandiCap(0.5);
                            bTeamModel.setThirdHandiCap(-0.5);

                            aTeamModel.setFourthHandiCap(0.5);
                            bTeamModel.setFourthHandiCap(-0.5);

                            aTeamModel.setFifthHandiCap(0.5);
                            bTeamModel.setFifthHandiCap(-0.5);

                        } else if (aTeamModel.getHandiCap() < 0) {
                            aTeamModel.setOdd("정배");
                            bTeamModel.setOdd("역배");

                            aTeamModel.setThirdHandiCap(-0.5);
                            bTeamModel.setThirdHandiCap(0.5);

                            aTeamModel.setFourthHandiCap(-0.5);
                            bTeamModel.setFourthHandiCap(0.5);

                            aTeamModel.setFifthHandiCap(-0.5);
                            bTeamModel.setFifthHandiCap(0.5);
                        } else {
                            aTeamModel.setOdd("없음");
                            bTeamModel.setOdd("없음");

                            aTeamModel.setThirdHandiCap(0.0);
                            bTeamModel.setThirdHandiCap(0.0);

                            aTeamModel.setFourthHandiCap(0.0);
                            bTeamModel.setFourthHandiCap(0.0);

                            aTeamModel.setFifthHandiCap(0.0);
                            bTeamModel.setFifthHandiCap(0.0);
                        }

                        if (aTeamModel.getHandiCap() == 0) {
                            aTeamModel.setHandiCapResult("적특");
                            bTeamModel.setHandiCapResult("적특");

                        } else {
                            if ((aTeamModel.getATeamTotalPoint() + aTeamModel.getHandiCap()) > aTeamModel.getBTeamTotalPoint()) {
                                aTeamModel.setHandiCapResult("승리");
                                bTeamModel.setHandiCapResult("패배");

                            } else if ((aTeamModel.getATeamTotalPoint() + aTeamModel.getHandiCap()) < aTeamModel.getBTeamTotalPoint()) {
                                aTeamModel.setHandiCapResult("패배");
                                bTeamModel.setHandiCapResult("승리");

                            } else {
                                aTeamModel.setHandiCapResult("적특");
                                bTeamModel.setHandiCapResult("적특");
                            }
                        }

                        if (aTeamModel.getThirdHandiCap() == 0) {
                            aTeamModel.setThirdHandiCapResult("적특");
                            bTeamModel.setThirdHandiCapResult("적특");

                        } else {
                            if ((aTeamModel.getATeamThirdPoint() + aTeamModel.getThirdHandiCap()) > aTeamModel.getBTeamThirdPoint()) {
                                aTeamModel.setThirdHandiCapResult("승리");
                                bTeamModel.setThirdHandiCapResult("패배");

                            } else if ((aTeamModel.getATeamThirdPoint() + aTeamModel.getThirdHandiCap()) < aTeamModel.getBTeamThirdPoint()) {
                                aTeamModel.setThirdHandiCapResult("패배");
                                bTeamModel.setThirdHandiCapResult("승리");

                            } else {
                                aTeamModel.setThirdHandiCapResult("적특");
                                bTeamModel.setThirdHandiCapResult("적특");
                            }
                        }

                        if (aTeamModel.getFourthHandiCap() == 0) {
                            aTeamModel.setFourthHandiCapResult("적특");
                            bTeamModel.setFourthHandiCapResult("적특");

                        } else {
                            if ((aTeamModel.getATeamFourthPoint() + aTeamModel.getFourthHandiCap()) > aTeamModel.getBTeamFourthPoint()) {
                                aTeamModel.setFourthHandiCapResult("승리");
                                bTeamModel.setFourthHandiCapResult("패배");

                            } else if ((aTeamModel.getATeamFourthPoint() + aTeamModel.getFourthHandiCap()) < aTeamModel.getBTeamFourthPoint()) {
                                aTeamModel.setFourthHandiCapResult("패배");
                                bTeamModel.setFourthHandiCapResult("승리");

                            } else {
                                aTeamModel.setFourthHandiCapResult("적특");
                                bTeamModel.setFourthHandiCapResult("적특");
                            }
                        }

                        if (aTeamModel.getFifthHandiCap() == 0) {
                            aTeamModel.setFifthHandiCapResult("적특");
                            bTeamModel.setFifthHandiCapResult("적특");

                        } else {
                            if ((aTeamModel.getATeamFifthPoint() + aTeamModel.getFifthHandiCap()) > aTeamModel.getBTeamFifthPoint()) {
                                aTeamModel.setFifthHandiCapResult("승리");
                                bTeamModel.setFifthHandiCapResult("패배");

                            } else if ((aTeamModel.getATeamFifthPoint() + aTeamModel.getFifthHandiCap()) < aTeamModel.getBTeamFifthPoint()) {
                                aTeamModel.setFifthHandiCapResult("패배");
                                bTeamModel.setFifthHandiCapResult("승리");

                            } else {
                                aTeamModel.setFifthHandiCapResult("적특");
                                bTeamModel.setFifthHandiCapResult("적특");
                            }
                        }

                        if (aTeamModel.getPointLine() == 0) {
                            aTeamModel.setPointLineResult("적특");
                            bTeamModel.setPointLineResult("적특");

                        } else {
                            if ((aTeamModel.getATeamTotalPoint() + aTeamModel.getBTeamTotalPoint()) > aTeamModel.getPointLine()) {
                                aTeamModel.setPointLineResult("오버");
                                bTeamModel.setPointLineResult("오버");

                            } else if ((aTeamModel.getATeamTotalPoint() + aTeamModel.getBTeamTotalPoint()) < aTeamModel.getPointLine()) {
                                aTeamModel.setPointLineResult("언더");
                                bTeamModel.setPointLineResult("언더");

                            } else {
                                aTeamModel.setPointLineResult("적특");
                                bTeamModel.setPointLineResult("적특");
                            }
                        }

                        if (aTeamModel.getThirdPointLine() == 0) {
                            aTeamModel.setThirdPointLineResult("적특");
                            bTeamModel.setThirdPointLineResult("적특");

                        } else {
                            if ((aTeamModel.getATeamThirdPoint() + aTeamModel.getBTeamThirdPoint()) > aTeamModel.getThirdPointLine()) {
                                aTeamModel.setThirdPointLineResult("오버");
                                bTeamModel.setThirdPointLineResult("오버");

                            } else if ((aTeamModel.getATeamThirdPoint() + aTeamModel.getBTeamThirdPoint()) < aTeamModel.getThirdPointLine()) {
                                aTeamModel.setThirdPointLineResult("언더");
                                bTeamModel.setThirdPointLineResult("언더");

                            } else {
                                aTeamModel.setThirdPointLineResult("적특");
                                bTeamModel.setThirdPointLineResult("적특");
                            }
                        }


                        if (aTeamModel.getFourthPointLine() == 0) {
                            aTeamModel.setFourthPointLineResult("적특");
                            bTeamModel.setFourthPointLineResult("적특");

                        } else {
                            if ((aTeamModel.getATeamFourthPoint() + aTeamModel.getBTeamFourthPoint()) > aTeamModel.getFourthPointLine()) {
                                aTeamModel.setFourthPointLineResult("오버");
                                bTeamModel.setFourthPointLineResult("오버");

                            } else if ((aTeamModel.getATeamFourthPoint() + aTeamModel.getBTeamFourthPoint()) < aTeamModel.getFourthPointLine()) {
                                aTeamModel.setFourthPointLineResult("언더");
                                bTeamModel.setFourthPointLineResult("언더");

                            } else {
                                aTeamModel.setFourthPointLineResult("적특");
                                bTeamModel.setFourthPointLineResult("적특");
                            }
                        }

                        if (aTeamModel.getFifthPointLine() == 0) {
                            aTeamModel.setFifthPointLineResult("적특");
                            bTeamModel.setFifthPointLineResult("적특");

                        } else {
                            if ((aTeamModel.getATeamFifthPoint() + aTeamModel.getBTeamFifthPoint()) > aTeamModel.getFifthPointLine()) {
                                aTeamModel.setFifthPointLineResult("오버");
                                bTeamModel.setFifthPointLineResult("오버");

                            } else if ((aTeamModel.getATeamFifthPoint() + aTeamModel.getBTeamFifthPoint()) < aTeamModel.getFifthPointLine()) {
                                aTeamModel.setFifthPointLineResult("언더");
                                bTeamModel.setFifthPointLineResult("언더");

                            } else {
                                aTeamModel.setFifthPointLineResult("적특");
                                bTeamModel.setFifthPointLineResult("적특");
                            }
                        }


                    } else {
                        aTeamModel.setHandiCap(0.0);
                        bTeamModel.setHandiCap(0.0);

                        aTeamModel.setPointLine(0.0);
                        bTeamModel.setPointLine(0.0);
                    }


                    JSONArray homeTeamSpecial = homeTeam.getJSONArray("specials");
                    JSONArray awayTeamSpecial = awayTeam.getJSONArray("specials");

                    String firstStrikeOut = "패배";
                    String firstHomerun = "패배";
                    String firstBaseOnBall = "패배";

                    for (int j = 0; j < homeTeamSpecial.length(); j++) {

                        JSONObject specialArr = homeTeamSpecial.getJSONObject(j);

                        if (specialArr.getBoolean("firstStrikeOut")) {
                            firstStrikeOut = "승리";
                        }

                        if (specialArr.getBoolean("firstHomerun")) {
                            firstHomerun = "승리";
                        }

                        if (specialArr.getBoolean("firstBaseOnBall")) {
                            firstBaseOnBall = "승리";
                        }

                    }

                    aTeamModel.setFirstStrikeOut(firstStrikeOut);
                    aTeamModel.setFirstHomerun(firstHomerun);
                    aTeamModel.setFirstBaseOnBall(firstBaseOnBall);

                    firstStrikeOut = "패배";
                    firstHomerun = "패배";
                    firstBaseOnBall = "패배";

                    for (int j = 0; j < awayTeamSpecial.length(); j++) {

                        JSONObject specialArr = awayTeamSpecial.getJSONObject(j);

                        if (specialArr.getBoolean("firstStrikeOut")) {
                            firstStrikeOut = "승리";
                        }
                        if (specialArr.getBoolean("firstHomerun")) {
                            firstHomerun = "승리";
                        }
                        if (specialArr.getBoolean("firstBaseOnBall")) {
                            firstBaseOnBall = "승리";
                        }
                    }

                    bTeamModel.setFirstStrikeOut(firstStrikeOut);
                    bTeamModel.setFirstHomerun(firstHomerun);
                    bTeamModel.setFirstBaseOnBall(firstBaseOnBall);

                    if(aTeamModel.getFirstStrikeOut().equals("패배") && bTeamModel.getFirstStrikeOut().equals("패배")){
                        aTeamModel.setFirstStrikeOut("적특");
                        bTeamModel.setFirstStrikeOut("적특");
                    }
                    if(aTeamModel.getFirstHomerun().equals("패배") && bTeamModel.getFirstHomerun().equals("패배")){
                        aTeamModel.setFirstHomerun("적특");
                        bTeamModel.setFirstHomerun("적특");
                    }
                    if(aTeamModel.getFirstBaseOnBall().equals("패배") && bTeamModel.getFirstBaseOnBall().equals("패배")){
                        aTeamModel.setFirstBaseOnBall("적특");
                        bTeamModel.setFirstBaseOnBall("적특");
                    }


                    aTeamModel.setATeamTotalPoint(aTeamModel.getTotalScore() + aTeamModel.getExtendScore());
                    aTeamModel.setBTeamTotalPoint(bTeamModel.getTotalScore() + bTeamModel.getExtendScore());

                    bTeamModel.setATeamTotalPoint(bTeamModel.getTotalScore() + bTeamModel.getExtendScore());
                    bTeamModel.setBTeamTotalPoint(aTeamModel.getTotalScore() + aTeamModel.getExtendScore() );

                    System.out.println(aTeamModel);
                    System.out.println(bTeamModel);

                    setalarmDAO.updateBaseballStat(aTeamModel);
                    setalarmDAO.updateBaseballStat(bTeamModel);
                }

                date++;


            }catch (Exception e){
                throw e;
            }

        }

    }
    public void test() throws IOException {
        StringEntity entity = new StringEntity("", "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        long unixTime = System.currentTimeMillis() / 1000;


        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .get(URL).setEntity(entity)
                .addHeader("accept", "*/*")
                .addHeader("oki-api-key", API_KEY)
                .addHeader("oki-api-name", API_NAME)
                .addHeader("origin", "https://sports.picksmatch.com")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36")
                .addParameter("broadcast", "true")
                .addParameter("broadcastLatest", "true")
                .addParameter("odds", "true")
                .addParameter("scores", "true")
                .addParameter("specials", "true")
                .addParameter("seasonTeamStat", "true")
                .addParameter("startDate", "20200517")
                .addParameter("endDate", "20200517")
                .addParameter("v", String.valueOf(unixTime))
                .build();


        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

        // verify

        try {
            JSONParser jsonParser = new JSONParser();

            String json = EntityUtils.toString(httpResponse.getEntity());

            //JSON데이터를 넣어 JSON Object 로 만들어 준다.
            JSONObject jsonObject = new JSONObject(json);

            //books의 배열을 추출
            JSONArray matchArr = jsonObject.getJSONArray("response");

            for (int i = 0; i < matchArr.length(); i++) {
                BaseballModel aTeamModel = new BaseballModel();
                BaseballModel bTeamModel = new BaseballModel();

                JSONObject matchObject = matchArr.getJSONObject(i);

                String gameStatus = matchObject.getJSONArray("broadcasts").getJSONObject(0).getString("playText");
                if (gameStatus.contains("취소")) {
                    continue;
                }

                aTeamModel.setGameId(String.valueOf(matchObject.getInt("id")));
                bTeamModel.setGameId(String.valueOf(matchObject.getInt("id")));
                aTeamModel.setLeague(matchObject.getJSONObject("league").getString("name"));
                bTeamModel.setLeague(matchObject.getJSONObject("league").getString("name"));

                String startDatetime = matchObject.getString("startDatetime");
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                String[] startDatetimeArr = startDatetime.split("T");

                Calendar cal = Calendar.getInstance();
                cal.setTime(format1.parse(startDatetime));
                int dayNum = cal.get(Calendar.DAY_OF_WEEK);

                String date = startDatetimeArr[0];
                String dayOfWeek = getDayoOfWeek(dayNum);
                String time = startDatetimeArr[1].substring(0, 5);

                aTeamModel.setDate(date);
                aTeamModel.setDayOfWeek(dayOfWeek);
                aTeamModel.setTime(time);

                bTeamModel.setDate(date);
                bTeamModel.setDayOfWeek(dayOfWeek);
                bTeamModel.setTime(time);

                aTeamModel.setGround("홈");
                bTeamModel.setGround("원정");

//                aTeamModel.setStadium(matchObject.getJSONObject("venue").getString("name"));
//                bTeamModel.setStadium(matchObject.getJSONObject("venue").getString("name"));

                JSONObject homeTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(1);
                JSONObject awayTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(0);
                aTeamModel.setATeam(homeTeam.getJSONObject("team").getString("nickname"));
                aTeamModel.setBTeam(awayTeam.getJSONObject("team").getString("nickname"));

                if (aTeamModel.getLeague().equals("KBO")) {
                    aTeamModel.setATeamPitcher(matchObject.getJSONObject("gameStatus").getJSONObject("homeStarterPlayer").getString("displayName"));
                    aTeamModel.setBTeamPitcher(matchObject.getJSONObject("gameStatus").getJSONObject("awayStarterPlayer").getString("displayName"));
                } else {
                    aTeamModel.setATeamPitcher("");
                    aTeamModel.setBTeamPitcher("");
                }


                bTeamModel.setATeamPitcher(aTeamModel.getBTeamPitcher());
                bTeamModel.setATeam(aTeamModel.getBTeam());

                bTeamModel.setBTeam(aTeamModel.getATeam());
                bTeamModel.setBTeamPitcher(aTeamModel.getATeamPitcher());

                aTeamModel.setFirstScore(homeTeam.getJSONArray("scores").getJSONObject(0).getInt("score"));
                aTeamModel.setSecondScore(homeTeam.getJSONArray("scores").getJSONObject(1).getInt("score"));
                aTeamModel.setThirdScore(homeTeam.getJSONArray("scores").getJSONObject(2).getInt("score"));
                aTeamModel.setFourthScore(homeTeam.getJSONArray("scores").getJSONObject(3).getInt("score"));
                aTeamModel.setFifthScore(homeTeam.getJSONArray("scores").getJSONObject(4).getInt("score"));
                aTeamModel.setSixthScore(homeTeam.getJSONArray("scores").getJSONObject(5).getInt("score"));
                aTeamModel.setSeventhScore(homeTeam.getJSONArray("scores").getJSONObject(6).getInt("score"));
                aTeamModel.setEighthScore(homeTeam.getJSONArray("scores").getJSONObject(7).getInt("score"));
                if (homeTeam.getJSONArray("scores").length() > 8) {
                    aTeamModel.setNinthScore(homeTeam.getJSONArray("scores").getJSONObject(8).getInt("score"));
                } else {
                    aTeamModel.setNinthScore(0);
                }

                bTeamModel.setFirstScore(awayTeam.getJSONArray("scores").getJSONObject(0).getInt("score"));
                bTeamModel.setSecondScore(awayTeam.getJSONArray("scores").getJSONObject(1).getInt("score"));
                bTeamModel.setThirdScore(awayTeam.getJSONArray("scores").getJSONObject(2).getInt("score"));
                bTeamModel.setFourthScore(awayTeam.getJSONArray("scores").getJSONObject(3).getInt("score"));
                bTeamModel.setFifthScore(awayTeam.getJSONArray("scores").getJSONObject(4).getInt("score"));
                bTeamModel.setSixthScore(awayTeam.getJSONArray("scores").getJSONObject(5).getInt("score"));
                bTeamModel.setSeventhScore(awayTeam.getJSONArray("scores").getJSONObject(6).getInt("score"));
                bTeamModel.setEighthScore(awayTeam.getJSONArray("scores").getJSONObject(7).getInt("score"));
                if (awayTeam.getJSONArray("scores").length() > 8) {
                    bTeamModel.setNinthScore(awayTeam.getJSONArray("scores").getJSONObject(8).getInt("score"));
                } else {
                    bTeamModel.setNinthScore(0);
                }
                aTeamModel.setATeamTotalPoint(aTeamModel.getTotalScore());
                aTeamModel.setBTeamTotalPoint(bTeamModel.getTotalScore());

                bTeamModel.setATeamTotalPoint(bTeamModel.getTotalScore());
                bTeamModel.setBTeamTotalPoint(aTeamModel.getTotalScore());

                aTeamModel.setATeamFourthPoint(aTeamModel.get4InningScore());
                aTeamModel.setBTeamFourthPoint(bTeamModel.get4InningScore());

                bTeamModel.setATeamFourthPoint(bTeamModel.get4InningScore());
                bTeamModel.setBTeamFourthPoint(aTeamModel.get4InningScore());

                aTeamModel.setATeamFifthPoint(aTeamModel.get5InningScore());
                aTeamModel.setBTeamFifthPoint(bTeamModel.get5InningScore());

                bTeamModel.setATeamFifthPoint(bTeamModel.get5InningScore());
                bTeamModel.setBTeamFifthPoint(aTeamModel.get5InningScore());

                JSONObject koreaOdd = matchObject.getJSONArray("odds").getJSONObject(1);
                if (aTeamModel.getLeague().equals("KBO")) {
                    aTeamModel.setHandiCap(koreaOdd.getDouble("handi"));
                    bTeamModel.setHandiCap(koreaOdd.getDouble("handi") * -1);

                    aTeamModel.setPointLine(koreaOdd.getDouble("unover"));
                    bTeamModel.setPointLine(koreaOdd.getDouble("unover"));


                    double firstInninPointLine = koreaOdd.getDouble("unover") / 9;

                    double forthPointLine = firstInninPointLine * 4;
                    int forthPointLineInt = (int) forthPointLine;
                    double pointLine = forthPointLine - forthPointLineInt;

                    if ((pointLine <= 0.333 && pointLine >= 0.001)) {
                        aTeamModel.setFourthPointLine((double) forthPointLineInt);
                        bTeamModel.setFourthPointLine((double) forthPointLineInt);

                    } else if ((pointLine <= 0.999 && pointLine >= 0.666)) {
                        aTeamModel.setFourthPointLine((double) (forthPointLineInt + 1));
                        bTeamModel.setFourthPointLine((double) (forthPointLineInt + 1));

                    } else if ((pointLine <= 0.665 && pointLine >= 0.334)) {
                        aTeamModel.setFourthPointLine(forthPointLineInt + 0.5);
                        bTeamModel.setFourthPointLine(forthPointLineInt + 0.5);

                    } else {
                        aTeamModel.setFourthPointLine((double) round(forthPointLine));
                        bTeamModel.setFourthPointLine((double) round(forthPointLine));

                    }

                    double fifthPointLine = firstInninPointLine * 5;
                    int fifthPointLineInt = (int) fifthPointLine;
                    pointLine = fifthPointLine - fifthPointLineInt;

                    if ((pointLine <= 0.333 && pointLine >= 0.001)) {
                        aTeamModel.setFifthPointLine((double) fifthPointLineInt);
                        bTeamModel.setFifthPointLine((double) fifthPointLineInt);

                    } else if ((pointLine <= 0.999 && pointLine >= 0.666)) {
                        aTeamModel.setFifthPointLine((double) (fifthPointLineInt + 1));
                        bTeamModel.setFifthPointLine((double) (fifthPointLineInt + 1));

                    } else if ((pointLine <= 0.665 && pointLine >= 0.334)) {
                        aTeamModel.setFifthPointLine(fifthPointLineInt + 0.5);
                        bTeamModel.setFifthPointLine(fifthPointLineInt + 0.5);

                    } else {
                        aTeamModel.setFifthPointLine((double) round(fifthPointLine));
                        bTeamModel.setFifthPointLine((double) round(fifthPointLine));

                    }


                    if (aTeamModel.getHandiCap() > 0) {
                        aTeamModel.setOdd("역배");
                        bTeamModel.setOdd("정배");

                        aTeamModel.setFourthHandiCap(0.5);
                        aTeamModel.setFourthHandiCap(-0.5);

                        aTeamModel.setFifthHandiCap(0.5);
                        aTeamModel.setFifthHandiCap(-0.5);

                    } else if (aTeamModel.getHandiCap() < 0) {
                        aTeamModel.setOdd("정배");
                        bTeamModel.setOdd("역배");

                        aTeamModel.setFourthHandiCap(-0.5);
                        aTeamModel.setFourthHandiCap(0.5);

                        aTeamModel.setFifthHandiCap(-0.5);
                        aTeamModel.setFifthHandiCap(0.5);
                    } else {
                        aTeamModel.setOdd("없음");
                        bTeamModel.setOdd("없음");

                        aTeamModel.setFourthHandiCap(0.0);
                        aTeamModel.setFourthHandiCap(0.0);

                        aTeamModel.setFifthHandiCap(0.0);
                        aTeamModel.setFifthHandiCap(0.0);
                    }

                    if (aTeamModel.getHandiCap() == 0) {
                        aTeamModel.setHandiCapResult("적특");
                        bTeamModel.setHandiCapResult("적특");

                    } else {
                        if ((aTeamModel.getATeamTotalPoint() + aTeamModel.getHandiCap()) > aTeamModel.getBTeamTotalPoint()) {
                            aTeamModel.setHandiCapResult("승리");
                            bTeamModel.setHandiCapResult("패배");

                        } else if ((aTeamModel.getATeamTotalPoint() + aTeamModel.getHandiCap()) < aTeamModel.getBTeamTotalPoint()) {
                            aTeamModel.setHandiCapResult("패배");
                            bTeamModel.setHandiCapResult("승리");

                        } else {
                            aTeamModel.setHandiCapResult("적특");
                            bTeamModel.setHandiCapResult("적특");
                        }
                    }

                    if (aTeamModel.getFourthHandiCap() == 0) {
                        aTeamModel.setFourthHandiCapResult("적특");
                        bTeamModel.setFourthHandiCapResult("적특");

                    } else {
                        if ((aTeamModel.getATeamFourthPoint() + aTeamModel.getFourthHandiCap()) > aTeamModel.getBTeamFourthPoint()) {
                            aTeamModel.setFourthHandiCapResult("승리");
                            bTeamModel.setFourthHandiCapResult("패배");

                        } else if ((aTeamModel.getATeamFourthPoint() + aTeamModel.getFourthHandiCap()) < aTeamModel.getBTeamFourthPoint()) {
                            aTeamModel.setFourthHandiCapResult("패배");
                            bTeamModel.setFourthHandiCapResult("승리");

                        } else {
                            aTeamModel.setFourthHandiCapResult("적특");
                            bTeamModel.setFourthHandiCapResult("적특");
                        }
                    }

                    if (aTeamModel.getFifthHandiCap() == 0) {
                        aTeamModel.setFifthHandiCapResult("적특");
                        bTeamModel.setFifthHandiCapResult("적특");

                    } else {
                        if ((aTeamModel.getATeamFifthPoint() + aTeamModel.getFifthHandiCap()) > aTeamModel.getBTeamFifthPoint()) {
                            aTeamModel.setFifthHandiCapResult("승리");
                            bTeamModel.setFifthHandiCapResult("패배");

                        } else if ((aTeamModel.getATeamFifthPoint() + aTeamModel.getFifthHandiCap()) < aTeamModel.getBTeamFifthPoint()) {
                            aTeamModel.setFifthHandiCapResult("패배");
                            bTeamModel.setFifthHandiCapResult("승리");

                        } else {
                            aTeamModel.setFifthHandiCapResult("적특");
                            bTeamModel.setFifthHandiCapResult("적특");
                        }
                    }

                    if (aTeamModel.getPointLine() == 0) {
                        aTeamModel.setPointLineResult("적특");
                        bTeamModel.setPointLineResult("적특");

                    } else {
                        if ((aTeamModel.getATeamTotalPoint() + aTeamModel.getBTeamTotalPoint()) > aTeamModel.getPointLine()) {
                            aTeamModel.setPointLineResult("오버");
                            bTeamModel.setPointLineResult("오버");

                        } else if ((aTeamModel.getATeamTotalPoint() + aTeamModel.getBTeamTotalPoint()) < aTeamModel.getPointLine()) {
                            aTeamModel.setPointLineResult("언더");
                            bTeamModel.setPointLineResult("언더");

                        } else {
                            aTeamModel.setPointLineResult("적특");
                            bTeamModel.setPointLineResult("적특");
                        }
                    }

                    if (aTeamModel.getFourthPointLine() == 0) {
                        aTeamModel.setFourthPointLineResult("적특");
                        bTeamModel.setFourthPointLineResult("적특");

                    } else {
                        if ((aTeamModel.getATeamFourthPoint() + aTeamModel.getBTeamFourthPoint()) > aTeamModel.getFourthPointLine()) {
                            aTeamModel.setFourthPointLineResult("오버");
                            bTeamModel.setFourthPointLineResult("오버");

                        } else if ((aTeamModel.getATeamFourthPoint() + aTeamModel.getBTeamFourthPoint()) < aTeamModel.getFourthPointLine()) {
                            aTeamModel.setFourthPointLineResult("언더");
                            bTeamModel.setFourthPointLineResult("언더");

                        } else {
                            aTeamModel.setFourthPointLineResult("적특");
                            bTeamModel.setFourthPointLineResult("적특");
                        }
                    }

                    if (aTeamModel.getFifthPointLine() == 0) {
                        aTeamModel.setFifthPointLineResult("적특");
                        bTeamModel.setFifthPointLineResult("적특");

                    } else {
                        if ((aTeamModel.getATeamFifthPoint() + aTeamModel.getBTeamFifthPoint()) > aTeamModel.getFifthPointLine()) {
                            aTeamModel.setFifthPointLineResult("오버");
                            bTeamModel.setFifthPointLineResult("오버");

                        } else if ((aTeamModel.getATeamFifthPoint() + aTeamModel.getBTeamFifthPoint()) < aTeamModel.getFifthPointLine()) {
                            aTeamModel.setFifthPointLineResult("언더");
                            bTeamModel.setFifthPointLineResult("언더");

                        } else {
                            aTeamModel.setFifthPointLineResult("적특");
                            bTeamModel.setFifthPointLineResult("적특");
                        }
                    }


                } else {
                    aTeamModel.setHandiCap(0.0);
                    bTeamModel.setHandiCap(0.0);

                    aTeamModel.setPointLine(0.0);
                    bTeamModel.setPointLine(0.0);
                }


                JSONArray homeTeamSpecial = homeTeam.getJSONArray("specials");
                JSONArray awayTeamSpecial = awayTeam.getJSONArray("specials");

                String firstStrikeOut = "패배";
                String firstHomerun = "패배";
                String firstBaseOnBall = "패배";

                for (int j = 0; j < homeTeamSpecial.length(); j++) {

                    JSONObject specialArr = homeTeamSpecial.getJSONObject(j);

                    if (specialArr.getBoolean("firstStrikeOut")) {
                        firstStrikeOut = "승리";
                    }

                    if (specialArr.getBoolean("firstHomerun")) {
                        firstHomerun = "승리";
                    }

                    if (specialArr.getBoolean("firstBaseOnBall")) {
                        firstBaseOnBall = "승리";
                    }

                }

                aTeamModel.setFirstStrikeOut(firstStrikeOut);
                aTeamModel.setFirstHomerun(firstHomerun);
                aTeamModel.setFirstBaseOnBall(firstBaseOnBall);

                firstStrikeOut = "패배";
                firstHomerun = "패배";
                firstBaseOnBall = "패배";

                for (int j = 0; j < awayTeamSpecial.length(); j++) {

                    JSONObject specialArr = awayTeamSpecial.getJSONObject(j);

                    if (specialArr.getBoolean("firstStrikeOut")) {
                        firstStrikeOut = "승리";
                    }

                    if (specialArr.getBoolean("firstHomerun")) {
                        firstHomerun = "승리";
                    }

                    if (specialArr.getBoolean("firstBaseOnBall")) {
                        firstBaseOnBall = "승리";
                    }

                }

                bTeamModel.setFirstStrikeOut(firstStrikeOut);
                bTeamModel.setFirstHomerun(firstHomerun);
                bTeamModel.setFirstBaseOnBall(firstBaseOnBall);


                System.out.println(aTeamModel);
                System.out.println(bTeamModel);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getDayoOfWeek(int dayNum) {
        String dayOfWeek = "";

        switch (dayNum) {
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

    public JSONObject getNewNameAPI(String matchDate, String gameId) throws IOException {
        StringEntity entity = new StringEntity("", "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");

        String domain = "https://api.picksmatch.com";
        String path = "/v1.0/sports/baseball/games?date=" + matchDate + "&status=ALL";

        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(domain);
        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .get(path).setEntity(entity)
                .addHeader("accept", "*/*")

                .addHeader("origin", "https://sports.picksmatch.com")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36")
                .build();

        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);


        try {
            JSONParser jsonParser = new JSONParser();

            String json = EntityUtils.toString(httpResponse.getEntity());

            //JSON데이터를 넣어 JSON Object 로 만들어 준다.
            JSONArray jsonArray = new JSONArray(json);

            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject tempObject = jsonArray.getJSONObject(i);
                int tempGameId = tempObject.getInt("id");
                if(gameId.equals(String.valueOf(tempGameId))){
                    return tempObject;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getOldNameAPI(String matchDate, String gameId) throws IOException {
        StringEntity entity = new StringEntity("", "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");

        String domain = "https://sports.picksmatch.com";
        String path = "/gateway/livegames/live_schedule_gateway.php";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("date",matchDate));
        params.add(new BasicNameValuePair("football_mode","7m"));
        params.add(new BasicNameValuePair("url",path));

        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(domain);

        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .post(path).setEntity(entity).setEntity(new UrlEncodedFormEntity(params,"UTF-8"))
                .addHeader("accept", "*/*")
                .addHeader("origin", "https://sports.picksmatch.com")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36")
//                .addParameter("date",matchDate)
//                .addParameter("football_mode","7m")
//                .addParameter("url",path)
                .build();

        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);


        try {
            JSONParser jsonParser = new JSONParser();

            String json = EntityUtils.toString(httpResponse.getEntity());

            //JSON데이터를 넣어 JSON Object 로 만들어 준다.
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject tempObject = jsonArray.getJSONObject(i);
                int tempGameId = tempObject.getInt("gidx");
//                System.out.println(tempGameId);
                if(gameId.equals(String.valueOf(tempGameId))){
                    return tempObject;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
        // Generate HMAC string
        Volleyball volleyball = new Volleyball();
        Hockey hockey = new Hockey();
        Soccer soccer = new Soccer();
        Basketball basketball = new Basketball();
        Nba nba = new Nba();
        JxlsMakeExcel jxlsMakeExcel = new JxlsMakeExcel();
        JxlsMakeExcelText jxlsMakeExcelText = new JxlsMakeExcelText();
        SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());
        NamedGetAPI namedGetAPI = new NamedGetAPI();


        try {

//            hockey.getAllMatch();
//            soccer.getAllMatch();
//            basketball.getAllMatch();
//            nba.getAllMatch();
//            volleyball.getAllMatch();
//            namedGetAPI.allBaseballMatch();


//            hockey.updateHockeyStat();
//            soccer.updateSoccerStat();
//            basketball.updateBasketBall();
//            nba.updateBasketBall();
//            volleyball.updateVolleyBall();
//
//            nba.getTomorrowMatch();
//            basketball.getTomorrowMatch();
//            basketball.getBasketBallSummary();
//
//            hockey.getTomorrowMatch();
//            hockey.getHockeySummary();

//            namedGetAPI.getOldNameAPI("2020-7-7","10559556");
            namedGetAPI.updateBaseball();
//
//            jxlsMakeExcel.statXlsDown("basketball");
//            jxlsMakeExcel.statXlsDown("volleyball");
//            jxlsMakeExcel.statXlsDown("soccer");
//            jxlsMakeExcel.statXlsDown("hockey");

//            jxlsMakeExcelText.statXlsDown("basketball_summary");

            jxlsMakeExcel.statXlsDown("baseball");
            jxlsMakeExcelText.baseballDown("baseball_summary");
//
//
//
            List<HashMap<String, Object>> memberList = setalarmDAO.selectMemberList();
            String[] recipients = new String[1];

            WebSendMail webSendMail = new WebSendMail();
//            recipients[0] = "jungyong_e@naver.com";
//            System.out.println(recipients[0]);
//            webSendMail.sendSSLMessage(recipients, "test", "test", "jungyongee@gmail.com");
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