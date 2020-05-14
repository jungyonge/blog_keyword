package blog.coupang;

import blog.model.BaseballModel;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NamedGetAPI {
    private final static String REQUEST_METHOD = "GET";
    private final static String DOMAIN = "sports-api.picksmatch.com";
    private final static String URL = "/named/v1/sports/baseball/games/?";
    private final static String API_KEY = "1rar2zCZvKjp";
    private final static String API_NAME = "named_score";

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
                .addParameter("startDate", "20200510")
                .addParameter("endDate", "20200512")
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
                System.out.println(matchObject);
                aTeamModel.setGameId(String.valueOf(matchObject.getInt("id")));
                bTeamModel.setGameId(String.valueOf(matchObject.getInt("id")));
                aTeamModel.setLeague(matchObject.getJSONObject("league").getString("name"));
                bTeamModel.setLeague(matchObject.getJSONObject("league").getString("name"));

                if(!aTeamModel.getLeague().equals("KBO"))
                    continue;;

                aTeamModel.setGround("홈");
                bTeamModel.setGround("원정");

                aTeamModel.setStadium(matchObject.getJSONObject("venue").getString("name"));
                bTeamModel.setStadium(matchObject.getJSONObject("venue").getString("name"));

                JSONObject homeTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(1);
                JSONObject awayTeam = (JSONObject) matchObject.getJSONArray("gameTeams").get(0);

                aTeamModel.setATeamPitcher(matchObject.getJSONObject("gameStatus").getJSONObject("homeStarterPlayer").getString("displayName"));
                aTeamModel.setATeam(homeTeam.getJSONObject("team").getString("nickname"));

                aTeamModel.setFirstScore(homeTeam.getJSONArray("scores").getJSONObject(0).getInt("score"));
                aTeamModel.setSecondScore(homeTeam.getJSONArray("scores").getJSONObject(1).getInt("score"));
                aTeamModel.setThirdScore(homeTeam.getJSONArray("scores").getJSONObject(2).getInt("score"));
                aTeamModel.setFourthScore(homeTeam.getJSONArray("scores").getJSONObject(3).getInt("score"));
                aTeamModel.setFifthScore(homeTeam.getJSONArray("scores").getJSONObject(4).getInt("score"));
                aTeamModel.setSixthScore(homeTeam.getJSONArray("scores").getJSONObject(5).getInt("score"));
                aTeamModel.setSeventhScore(homeTeam.getJSONArray("scores").getJSONObject(6).getInt("score"));
                aTeamModel.setEighthScore(homeTeam.getJSONArray("scores").getJSONObject(7).getInt("score"));
//                aTeamModel.setNinthScore(homeTeam.getJSONArray("scores").getJSONObject(8).getInt("score"));

                bTeamModel.setFirstScore(awayTeam.getJSONArray("scores").getJSONObject(0).getInt("score"));
                bTeamModel.setSecondScore(awayTeam.getJSONArray("scores").getJSONObject(1).getInt("score"));
                bTeamModel.setThirdScore(awayTeam.getJSONArray("scores").getJSONObject(2).getInt("score"));
                bTeamModel.setFourthScore(awayTeam.getJSONArray("scores").getJSONObject(3).getInt("score"));
                bTeamModel.setFifthScore(awayTeam.getJSONArray("scores").getJSONObject(4).getInt("score"));
                bTeamModel.setSixthScore(awayTeam.getJSONArray("scores").getJSONObject(5).getInt("score"));
                bTeamModel.setSeventhScore(awayTeam.getJSONArray("scores").getJSONObject(6).getInt("score"));
                bTeamModel.setEighthScore(awayTeam.getJSONArray("scores").getJSONObject(7).getInt("score"));
//                bTeamModel.setNinthScore(awayTeam.getJSONArray("scores").getJSONObject(8).getInt("score"));

                aTeamModel.setATeamTotalPoint(aTeamModel.getTotalScore());
                aTeamModel.setBTeamTotalPoint(bTeamModel.getTotalScore());

                aTeamModel.setBTeam(awayTeam.getJSONObject("team").getString("nickname"));
                aTeamModel.setBTeamPitcher(matchObject.getJSONObject("gameStatus").getJSONObject("awayStarterPlayer").getString("displayName"));

                JSONObject koreaOdd = matchObject.getJSONArray("odds").getJSONObject(1);
                aTeamModel.setHandiCap(koreaOdd.getDouble("handi"));
                aTeamModel.setPointLine(koreaOdd.getDouble("unover"));
                bTeamModel.setPointLine(koreaOdd.getDouble("unover"));



                if(aTeamModel.getHandiCap() > 0){
                    aTeamModel.setOdd("역배");
                    bTeamModel.setOdd("정배");

                    aTeamModel.setHalfHandiCap(0.5);
                    aTeamModel.setHalfHandiCap(-0.5);

                } else if (aTeamModel.getHandiCap() < 0){
                    aTeamModel.setOdd("정배");
                    bTeamModel.setOdd("역배");

                    aTeamModel.setHalfHandiCap(-0.5);
                    aTeamModel.setHalfHandiCap(0.5);
                } else {
                    aTeamModel.setOdd("없음");
                    bTeamModel.setOdd("없음");

                    aTeamModel.setHalfHandiCap(0.0);
                    aTeamModel.setHalfHandiCap(0.0);
                }

                if(aTeamModel.getHandiCap() == 0){
                    aTeamModel.setHandiCapResult("적특");
                }else {
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

                if(aTeamModel.getHalfHandiCap() == 0){
                    aTeamModel.setHandiCapResult("적특");
                }else {
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

                System.out.println(aTeamModel);






            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws IOException {
        // Generate HMAC string

        // Send request
        NamedGetAPI namedGetAPI = new NamedGetAPI();
        namedGetAPI.test();
//        StringEntity entity = new StringEntity("", "UTF-8");
//        entity.setContentEncoding("UTF-8");
//        entity.setContentType("application/json");
//        long unixTime = System.currentTimeMillis() / 1000;
//
//
//        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
//        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
//                .get(URL).setEntity(entity)
//                .addHeader("accept", "*/*")
//                .addHeader("oki-api-key", API_KEY)
//                .addHeader("oki-api-name", API_NAME)
//                .addHeader("origin", "https://sports.picksmatch.com")
//                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36")
//                .addParameter("broadcast", "true")
//                .addParameter("broadcastLatest", "true")
//                .addParameter("odds", "true")
//                .addParameter("scores", "true")
//                .addParameter("specials", "true")
//                .addParameter("seasonTeamStat", "true")
//                .addParameter("startDate", "20200510")
//                .addParameter("endDate", "20200520")
//                .addParameter("v", String.valueOf(unixTime))
//                .build();
//
//
//        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);
//
//        // verify
//        System.out.println(EntityUtils.toString(httpResponse.getEntity()));

//        JSONArray matchArr =
    }
}