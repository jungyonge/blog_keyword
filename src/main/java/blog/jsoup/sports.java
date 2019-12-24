package blog.jsoup;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;

import blog.model.SportModel;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.*;

public class sports {

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
        SportModel aTeamStat = new SportModel();
        SportModel bTeamStat = new SportModel();

        String rootHtml = "";
        String url = "https://livescore.co.kr/sports/score_board/basket/view.php?date=";


        for(int date = 1 ; date < 30 ; date++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            cal.add(Calendar.DATE, -date);
            System.out.println("after: " + df.format(cal.getTime()));

            System.out.println(url+df.format(cal.getTime()));
            rootHtml = requestURLToString(url + df.format(cal.getTime()));
            Thread.sleep(2000);

            Document rootDoc = Jsoup.parse(rootHtml);
            Elements elements = rootDoc.select("div#score_board div.score_tbl_individual");
            for (Element element : rootDoc.select("div#score_board div.score_tbl_individual")) {
                int i = 0;
                if (element.select("thead tr th.reague").text().contains("NBA") || element.select("thead tr th.reague").text().contains("KBL") || element.select("thead tr th.reague").text().contains("WKBL")) {
                    aTeamStat.setLeague(element.select("thead tr th.reague").text());
                    String[] arrayHandi = element.select("tbody > tr > td.line").text().split(" ");

                    if(arrayHandi.length > 0){
                        aTeamStat.setPointLine(Double.valueOf(arrayHandi[0]));
                        aTeamStat.setHandiCap(Double.valueOf(arrayHandi[1]));
                    } else {
                        aTeamStat.setPointLine(0.0);
                        aTeamStat.setHandiCap(0.0);
                    }


                    String[] arrayTotalScore = element.select("tbody > tr > td.score").text().split(" ");
                    if(arrayTotalScore.length > 0){
                        aTeamStat.setBTeamTotalPoint(Integer.valueOf(arrayTotalScore[0]));
                        aTeamStat.setATeamTotalPoint(Integer.valueOf(arrayTotalScore[1]));
                    } else {
                        aTeamStat.setBTeamTotalPoint(0);
                        aTeamStat.setATeamTotalPoint(0);
                    }

                    if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getHandiCap()) > aTeamStat.getBTeamTotalPoint()) {
                        aTeamStat.setHandiCapResult("승리");
                    } else if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getHandiCap()) < aTeamStat.getBTeamTotalPoint()) {
                        aTeamStat.setHandiCapResult("패배");
                    } else {
                        aTeamStat.setHandiCapResult("적특");
                    }

                    if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getBTeamTotalPoint()) > aTeamStat.getPointLine()) {
                        aTeamStat.setPointLineResult("오버");
                    } else if ((aTeamStat.getATeamTotalPoint() + aTeamStat.getBTeamTotalPoint()) < aTeamStat.getPointLine()) {
                        aTeamStat.setPointLineResult("언더");
                    } else {
                        aTeamStat.setPointLineResult("적특");
                    }

                    String[] arrayFirstScore = element.select("tbody > tr > td.s").text().split(" ");
                    if(arrayFirstScore.length > 0){
                        bTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[0]));
                        bTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[1]));
                        bTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[2]));
                        bTeamStat.setFourthQPoint(Integer.valueOf(arrayFirstScore[3]));
                        bTeamStat.setExtendQPoint(Integer.valueOf(arrayFirstScore[4]));

                        aTeamStat.setFirstQPoint(Integer.valueOf(arrayFirstScore[5]));
                        aTeamStat.setSecondQPoint(Integer.valueOf(arrayFirstScore[6]));
                        aTeamStat.setThirdQPoint(Integer.valueOf(arrayFirstScore[7]));
                        aTeamStat.setFourthQPoint(Integer.valueOf(arrayFirstScore[8]));
                        aTeamStat.setExtendQPoint(Integer.valueOf(arrayFirstScore[9]));

                    }else {
                        bTeamStat.setFirstQPoint( 0);
                        bTeamStat.setSecondQPoint(0);
                        bTeamStat.setThirdQPoint( 0);
                        bTeamStat.setFourthQPoint(0);
                        bTeamStat.setExtendQPoint(0);

                        aTeamStat.setFirstQPoint( 0);
                        aTeamStat.setSecondQPoint(0);
                        aTeamStat.setThirdQPoint( 0);
                        aTeamStat.setFourthQPoint(0);
                        aTeamStat.setExtendQPoint(0);
                    }



                    double handi = aTeamStat.getHandiCap() / 4;
                    int handiInt = (int) handi;
                    double pointHandi = handi - handiInt;

                    if (pointHandi == 0.5 || pointHandi == -0.5) {
                        aTeamStat.setFirstQHandiCap(aTeamStat.getHandiCap() / 4);
                    } else {
                        aTeamStat.setFirstQHandiCap(Double.valueOf(Math.round(aTeamStat.getHandiCap() / 4)));
                    }

                    double point = aTeamStat.getPointLine() / 4;
                    int pointInt = (int) point;
                    double pointLine = point - pointInt;

                    if (pointLine == 0.5) {
                        aTeamStat.setFirstQPointLine(aTeamStat.getPointLine() / 4);
                    } else {
                        aTeamStat.setFirstQPointLine(Double.valueOf(Math.round(aTeamStat.getPointLine() / 4)));
                    }

                    if ((aTeamStat.getFirstQPoint() + aTeamStat.getFirstQHandiCap()) > bTeamStat.getFirstQPoint()) {
                        aTeamStat.setFirstQHandiCapResult("승리");
                    } else if ((aTeamStat.getFirstQPoint() + aTeamStat.getFirstQHandiCap()) < bTeamStat.getFirstQPoint()) {
                        aTeamStat.setFirstQHandiCapResult("패배");
                    } else {
                        aTeamStat.setFirstQHandiCapResult("적특");
                    }

                    if ((aTeamStat.getFirstQPoint() + bTeamStat.getFirstQPoint()) > aTeamStat.getFirstQPointLine()) {
                        aTeamStat.setFirstQPointLineResult("오버");
                    } else if ((aTeamStat.getFirstQPoint() + aTeamStat.getFirstQPoint()) < aTeamStat.getFirstQPointLine()) {
                        aTeamStat.setFirstQPointLineResult("언더");
                    } else {
                        aTeamStat.setFirstQPointLineResult("적특");
                    }

                    String[] arrayQScore = element.select("tbody > tr > td.s").text().split(" ");
                    String[] arrayQTotalScore = element.select("tfoot > tr > td.s").text().split(" ");

                    if(arrayQTotalScore.length > 0){
                        aTeamStat.setFirstQTotalPoint(Integer.valueOf(arrayQTotalScore[0]));
                        aTeamStat.setSecondQTotalPoint(Integer.valueOf(arrayQTotalScore[1]));
                        aTeamStat.setThirdQTotalPoint(Integer.valueOf(arrayQTotalScore[2]));
                        aTeamStat.setFourthQTotalPoint(Integer.valueOf(arrayQTotalScore[3]));
                    }else {
                        aTeamStat.setFirstQTotalPoint( 0);
                        aTeamStat.setSecondQTotalPoint(0);
                        aTeamStat.setThirdQTotalPoint( 0);
                        aTeamStat.setFourthQTotalPoint(0);
                    }

                    aTeamStat.setTime(element.select("thead tr th.ptime").text().replaceAll("오전 ", "").replaceAll("오후 ", ""));
                    aTeamStat.setDate(df.format(cal.getTime()));
                    aTeamStat.setBTeam(element.select("tbody tr > td.teaminfo.visitor strong").text());
                    aTeamStat.setATeam(element.select("tbody tr > td.teaminfo.hometeam strong").text());

                    for (Element element1 : element.select("tbody > tr > td.f.ico_linescore > p")) {
                        if (i == 0) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setFirstQFirstPoint(true);
                            } else {
                                aTeamStat.setFirstQFirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setFirstQFirstFreeTwo(true);
                            } else {
                                aTeamStat.setFirstQFirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setFirstQFirstTwoPoint(true);
                            } else {
                                aTeamStat.setFirstQFirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setFirstQFirstThreePoint(true);
                            } else {
                                aTeamStat.setFirstQFirstThreePoint(false);
                            }
                        }
                        if (i == 1) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setSecondQFirstPoint(true);
                            } else {
                                aTeamStat.setSecondQFirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setSecondQFirstFreeTwo(true);
                            } else {
                                aTeamStat.setSecondQFirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setSecondQFirstTwoPoint(true);
                            } else {
                                aTeamStat.setSecondQFirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setSecondQFirstThreePoint(true);
                            } else {
                                aTeamStat.setSecondQFirstThreePoint(false);
                            }
                        }
                        if (i == 2) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setThirdQFirstPoint(true);
                            } else {
                                aTeamStat.setThirdQFirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setThirdQFirstFreeTwo(true);
                            } else {
                                aTeamStat.setThirdQFirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setThirdQFirstTwoPoint(true);
                            } else {
                                aTeamStat.setThirdQFirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setThirdQFirstThreePoint(true);
                            } else {
                                aTeamStat.setThirdQFirstThreePoint(false);
                            }
                        }
                        if (i == 3) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setFourthQFirstPoint(true);
                            } else {
                                aTeamStat.setFourthQFirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setFourthQFirstFreeTwo(true);
                            } else {
                                aTeamStat.setFourthQFirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setFourthQFirstTwoPoint(true);
                            } else {
                                aTeamStat.setFourthQFirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setFourthQFirstThreePoint(true);
                            } else {
                                aTeamStat.setFourthQFirstThreePoint(false);
                            }
                        }
                        i++;
                    }
                    setBteamStat(aTeamStat,bTeamStat);
                    System.out.println(aTeamStat);
                    System.out.println(bTeamStat);
                }

            }
        }
    }

    public void setBteamStat(SportModel aTeamStat ,SportModel bTeamStat){
        bTeamStat.setLeague(aTeamStat.getLeague());


        bTeamStat.setPointLine(aTeamStat.getPointLine());
        bTeamStat.setHandiCap(-(aTeamStat.getHandiCap()));

        bTeamStat.setBTeamTotalPoint(aTeamStat.getATeamTotalPoint());
        bTeamStat.setATeamTotalPoint(aTeamStat.getBTeamTotalPoint());

        if (aTeamStat.getHandiCapResult().equals("패배")) {
            bTeamStat.setHandiCapResult("승리");
        } else if (aTeamStat.getHandiCapResult().equals("승리")) {
            bTeamStat.setHandiCapResult("패배");
        } else {
            bTeamStat.setHandiCapResult("적특");
        }

        if (aTeamStat.getPointLineResult().equals("언더")) {
            bTeamStat.setPointLineResult("오버");
        } else if (aTeamStat.getPointLineResult().equals("오버"))  {
            bTeamStat.setPointLineResult("언더");
        } else {
            bTeamStat.setPointLineResult("적특");
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

        if (aTeamStat.getFirstQPointLineResult().equals("언더")) {
            bTeamStat.setFirstQPointLineResult("오버");
        } else if (aTeamStat.getFirstQPointLineResult().equals("오버")) {
            bTeamStat.setFirstQPointLineResult("언더");
        } else {
            bTeamStat.setFirstQPointLineResult("적특");
        }

        bTeamStat.setFirstQTotalPoint(aTeamStat.getFirstQTotalPoint());
        bTeamStat.setSecondQTotalPoint(aTeamStat.getSecondQTotalPoint());
        bTeamStat.setThirdQTotalPoint(aTeamStat.getThirdQTotalPoint());
        bTeamStat.setFourthQTotalPoint(aTeamStat.getFourthQTotalPoint());


        bTeamStat.setTime(aTeamStat.getTime());
        bTeamStat.setBTeam(aTeamStat.getATeam());
        bTeamStat.setATeam(aTeamStat.getBTeam());

        //첫2득
        if(aTeamStat.getFirstQFirstTwoPoint()){
            bTeamStat.setFirstQFirstTwoPoint(false);
        } else {
            bTeamStat.setFirstQFirstTwoPoint(true);
        }

        if(aTeamStat.getSecondQFirstTwoPoint()){
            bTeamStat.setSecondQFirstTwoPoint(false);
        } else {
            bTeamStat.setSecondQFirstTwoPoint(true);
        }

        if(aTeamStat.getThirdQFirstTwoPoint()){
            bTeamStat.setThirdQFirstTwoPoint(false);
        } else {
            bTeamStat.setThirdQFirstTwoPoint(true);
        }

        if(aTeamStat.getFourthQFirstTwoPoint()){
            bTeamStat.setFourthQFirstTwoPoint(false);
        } else {
            bTeamStat.setFourthQFirstTwoPoint(true);
        }

        //첫득점
        if(aTeamStat.getFirstQFirstPoint()){
            bTeamStat.setFirstQFirstPoint(false);
        } else {
            bTeamStat.setFirstQFirstPoint(true);
        }

        if(aTeamStat.getSecondQFirstPoint()){
            bTeamStat.setSecondQFirstPoint(false);
        } else {
            bTeamStat.setSecondQFirstPoint(true);
        }

        if(aTeamStat.getThirdQFirstPoint()){
            bTeamStat.setThirdQFirstPoint(false);
        } else {
            bTeamStat.setThirdQFirstPoint(true);
        }

        if(aTeamStat.getFourthQFirstPoint()){
            bTeamStat.setFourthQFirstPoint(false);
        } else {
            bTeamStat.setFourthQFirstPoint(true);
        }

        //첫3점
        if(aTeamStat.getFirstQFirstThreePoint()){
            bTeamStat.setFirstQFirstThreePoint(false);
        } else {
            bTeamStat.setFirstQFirstThreePoint(true);
        }

        if(aTeamStat.getSecondQFirstThreePoint()){
            bTeamStat.setSecondQFirstThreePoint(false);
        } else {
            bTeamStat.setSecondQFirstThreePoint(true);
        }

        if(aTeamStat.getThirdQFirstThreePoint()){
            bTeamStat.setThirdQFirstThreePoint(false);
        } else {
            bTeamStat.setThirdQFirstThreePoint(true);
        }

        if(aTeamStat.getFourthQFirstThreePoint()){
            bTeamStat.setFourthQFirstThreePoint(false);
        } else {
            bTeamStat.setFourthQFirstThreePoint(true);
        }

        //자유투
        if(aTeamStat.getFirstQFirstFreeTwo()){
            bTeamStat.setFirstQFirstFreeTwo(false);
        } else {
            bTeamStat.setFirstQFirstFreeTwo(true);
        }

        if(aTeamStat.getSecondQFirstFreeTwo()){
            bTeamStat.setSecondQFirstFreeTwo(false);
        } else {
            bTeamStat.setSecondQFirstFreeTwo(true);
        }

        if(aTeamStat.getThirdQFirstFreeTwo()){
            bTeamStat.setThirdQFirstFreeTwo(false);
        } else {
            bTeamStat.setThirdQFirstFreeTwo(true);
        }

        if(aTeamStat.getFourthQFirstFreeTwo()){
            bTeamStat.setFourthQFirstFreeTwo(false);
        } else {
            bTeamStat.setFourthQFirstFreeTwo(true);
        }

    }
    public static void main(String[] args) {
        sports sports = new sports();
        try {
            sports.getCategoryList();
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
