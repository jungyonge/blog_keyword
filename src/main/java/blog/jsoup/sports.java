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
                        aTeamStat.setBTeamFirstQuarterPoint(Integer.valueOf(arrayFirstScore[0]));
                        aTeamStat.setATeamFirstQuarterPoint(Integer.valueOf(arrayFirstScore[5]));
                    }else {
                        aTeamStat.setBTeamFirstQuarterPoint(0);
                        aTeamStat.setATeamFirstQuarterPoint(0);
                    }



                    double handi = aTeamStat.getHandiCap() / 4;
                    int handiInt = (int) handi;
                    double pointHandi = handi - handiInt;

                    if (pointHandi == 0.5 || pointHandi == -0.5) {
                        aTeamStat.setFirstQuarterHandiCap(aTeamStat.getHandiCap() / 4);
                    } else {
                        aTeamStat.setFirstQuarterHandiCap(Double.valueOf(Math.round(aTeamStat.getHandiCap() / 4)));
                    }

                    double point = aTeamStat.getPointLine() / 4;
                    int pointInt = (int) point;
                    double pointLine = point - pointInt;

                    if (pointLine == 0.5) {
                        aTeamStat.setFirstQuarterPointLine(aTeamStat.getPointLine() / 4);
                    } else {
                        aTeamStat.setFirstQuarterPointLine(Double.valueOf(Math.round(aTeamStat.getPointLine() / 4)));
                    }

                    if ((aTeamStat.getATeamFirstQuarterPoint() + aTeamStat.getFirstQuarterHandiCap()) > aTeamStat.getBTeamFirstQuarterPoint()) {
                        aTeamStat.setFirstQuarterHandiCapResult("승리");
                    } else if ((aTeamStat.getATeamFirstQuarterPoint() + aTeamStat.getFirstQuarterHandiCap()) < aTeamStat.getBTeamFirstQuarterPoint()) {
                        aTeamStat.setFirstQuarterHandiCapResult("패배");
                    } else {
                        aTeamStat.setFirstQuarterHandiCapResult("적특");
                    }

                    if ((aTeamStat.getATeamFirstQuarterPoint() + aTeamStat.getBTeamFirstQuarterPoint()) > aTeamStat.getFirstQuarterPointLine()) {
                        aTeamStat.setFirstQuarterPointLineResult("오버");
                    } else if ((aTeamStat.getATeamFirstQuarterPoint() + aTeamStat.getBTeamFirstQuarterPoint()) < aTeamStat.getFirstQuarterPointLine()) {
                        aTeamStat.setFirstQuarterPointLineResult("언더");
                    } else {
                        aTeamStat.setFirstQuarterPointLineResult("적특");
                    }

                    String[] arrayScore = element.select("tfoot > tr > td.s").text().split(" ");

                    if(arrayScore.length > 0){
                        aTeamStat.setFirstQuarterPoint(Integer.valueOf(arrayScore[0]));
                        aTeamStat.setSecondQuarterPoint(Integer.valueOf(arrayScore[1]));
                        aTeamStat.setThirdQuarterPoint(Integer.valueOf(arrayScore[2]));
                        aTeamStat.setFourthQuarterPoint(Integer.valueOf(arrayScore[3]));
                    }else {
                        aTeamStat.setFirstQuarterPoint( 0);
                        aTeamStat.setSecondQuarterPoint(0);
                        aTeamStat.setThirdQuarterPoint( 0);
                        aTeamStat.setFourthQuarterPoint(0);
                    }

                    aTeamStat.setTime(element.select("thead tr th.ptime").text().replaceAll("오전 ", "").replaceAll("오후 ", ""));
                    aTeamStat.setDate(df.format(cal.getTime()));
                    aTeamStat.setBTeam(element.select("tbody tr > td.teaminfo.visitor strong").text());
                    aTeamStat.setATeam(element.select("tbody tr > td.teaminfo.hometeam strong").text());

                    for (Element element1 : element.select("tbody > tr > td.f.ico_linescore > p")) {
                        if (i == 0) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setFirstQuarterfirstPoint(true);
                            } else {
                                aTeamStat.setFirstQuarterfirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setFirstQuarterfirstFreeTwo(true);
                            } else {
                                aTeamStat.setFirstQuarterfirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setFirstQuarterfirstTwoPoint(true);
                            } else {
                                aTeamStat.setFirstQuarterfirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setFirstQuarterfirstThreePoint(true);
                            } else {
                                aTeamStat.setFirstQuarterfirstThreePoint(false);
                            }
                        }
                        if (i == 1) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setSecondQuarterfirstPoint(true);
                            } else {
                                aTeamStat.setSecondQuarterfirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setSecondQuarterfirstFreeTwo(true);
                            } else {
                                aTeamStat.setSecondQuarterfirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setSecondQuarterfirstTwoPoint(true);
                            } else {
                                aTeamStat.setSecondQuarterfirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setSecondQuarterfirstThreePoint(true);
                            } else {
                                aTeamStat.setSecondQuarterfirstThreePoint(false);
                            }
                        }
                        if (i == 2) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setThirdQuarterfirstPoint(true);
                            } else {
                                aTeamStat.setThirdQuarterfirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setThirdQuarterfirstFreeTwo(true);
                            } else {
                                aTeamStat.setThirdQuarterfirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setThirdQuarterfirstTwoPoint(true);
                            } else {
                                aTeamStat.setThirdQuarterfirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setThirdQuarterfirstThreePoint(true);
                            } else {
                                aTeamStat.setThirdQuarterfirstThreePoint(false);
                            }
                        }
                        if (i == 3) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                aTeamStat.setFourthQuarterfirstPoint(true);
                            } else {
                                aTeamStat.setFourthQuarterfirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                aTeamStat.setFourthQuarterfirstFreeTwo(true);
                            } else {
                                aTeamStat.setFourthQuarterfirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                aTeamStat.setFourthQuarterfirstTwoPoint(true);
                            } else {
                                aTeamStat.setFourthQuarterfirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                aTeamStat.setFourthQuarterfirstThreePoint(true);
                            } else {
                                aTeamStat.setFourthQuarterfirstThreePoint(false);
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


        bTeamStat.setBTeamFirstQuarterPoint(aTeamStat.getATeamFirstQuarterPoint());
        bTeamStat.setATeamFirstQuarterPoint(aTeamStat.getBTeamFirstQuarterPoint());


        bTeamStat.setFirstQuarterHandiCap(-(aTeamStat.getFirstQuarterHandiCap()));

        bTeamStat.setFirstQuarterPointLine(aTeamStat.getFirstQuarterPointLine());


        if (aTeamStat.getFirstQuarterHandiCapResult().equals("패배")) {
            bTeamStat.setFirstQuarterHandiCapResult("승리");
        } else if  (aTeamStat.getFirstQuarterHandiCapResult().equals("승리")) {
            bTeamStat.setFirstQuarterHandiCapResult("패배");
        } else {
            bTeamStat.setFirstQuarterHandiCapResult("적특");
        }

        if (aTeamStat.getFirstQuarterPointLineResult().equals("언더")) {
            bTeamStat.setFirstQuarterPointLineResult("오버");
        } else if (aTeamStat.getFirstQuarterPointLineResult().equals("오버")) {
            bTeamStat.setFirstQuarterPointLineResult("언더");
        } else {
            bTeamStat.setFirstQuarterPointLineResult("적특");
        }

        bTeamStat.setFirstQuarterPoint(aTeamStat.getFirstQuarterPoint());
        bTeamStat.setSecondQuarterPoint(aTeamStat.getSecondQuarterPoint());
        bTeamStat.setThirdQuarterPoint(aTeamStat.getThirdQuarterPoint());
        bTeamStat.setFourthQuarterPoint(aTeamStat.getFourthQuarterPoint());


        bTeamStat.setTime(aTeamStat.getTime());
        bTeamStat.setBTeam(aTeamStat.getATeam());
        bTeamStat.setATeam(aTeamStat.getBTeam());

        //첫2득
        if(aTeamStat.getFirstQuarterfirstTwoPoint()){
            bTeamStat.setFirstQuarterfirstTwoPoint(false);
        } else {
            bTeamStat.setFirstQuarterfirstTwoPoint(true);
        }

        if(aTeamStat.getSecondQuarterfirstTwoPoint()){
            bTeamStat.setSecondQuarterfirstTwoPoint(false);
        } else {
            bTeamStat.setSecondQuarterfirstTwoPoint(true);
        }

        if(aTeamStat.getThirdQuarterfirstTwoPoint()){
            bTeamStat.setThirdQuarterfirstTwoPoint(false);
        } else {
            bTeamStat.setThirdQuarterfirstTwoPoint(true);
        }

        if(aTeamStat.getFourthQuarterfirstTwoPoint()){
            bTeamStat.setFourthQuarterfirstTwoPoint(false);
        } else {
            bTeamStat.setFourthQuarterfirstTwoPoint(true);
        }

        //첫득점
        if(aTeamStat.getFirstQuarterfirstPoint()){
            bTeamStat.setFirstQuarterfirstPoint(false);
        } else {
            bTeamStat.setFirstQuarterfirstPoint(true);
        }

        if(aTeamStat.getSecondQuarterfirstPoint()){
            bTeamStat.setSecondQuarterfirstPoint(false);
        } else {
            bTeamStat.setSecondQuarterfirstPoint(true);
        }

        if(aTeamStat.getThirdQuarterfirstPoint()){
            bTeamStat.setThirdQuarterfirstPoint(false);
        } else {
            bTeamStat.setThirdQuarterfirstPoint(true);
        }

        if(aTeamStat.getFourthQuarterfirstPoint()){
            bTeamStat.setFourthQuarterfirstPoint(false);
        } else {
            bTeamStat.setFourthQuarterfirstPoint(true);
        }

        //첫3점
        if(aTeamStat.getFirstQuarterfirstThreePoint()){
            bTeamStat.setFirstQuarterfirstThreePoint(false);
        } else {
            bTeamStat.setFirstQuarterfirstThreePoint(true);
        }

        if(aTeamStat.getSecondQuarterfirstThreePoint()){
            bTeamStat.setSecondQuarterfirstThreePoint(false);
        } else {
            bTeamStat.setSecondQuarterfirstThreePoint(true);
        }

        if(aTeamStat.getThirdQuarterfirstThreePoint()){
            bTeamStat.setThirdQuarterfirstThreePoint(false);
        } else {
            bTeamStat.setThirdQuarterfirstThreePoint(true);
        }

        if(aTeamStat.getFourthQuarterfirstThreePoint()){
            bTeamStat.setFourthQuarterfirstThreePoint(false);
        } else {
            bTeamStat.setFourthQuarterfirstThreePoint(true);
        }

        //자유투
        if(aTeamStat.getFirstQuarterfirstFreeTwo()){
            bTeamStat.setFirstQuarterfirstFreeTwo(false);
        } else {
            bTeamStat.setFirstQuarterfirstFreeTwo(true);
        }

        if(aTeamStat.getSecondQuarterfirstFreeTwo()){
            bTeamStat.setSecondQuarterfirstFreeTwo(false);
        } else {
            bTeamStat.setSecondQuarterfirstFreeTwo(true);
        }

        if(aTeamStat.getThirdQuarterfirstFreeTwo()){
            bTeamStat.setThirdQuarterfirstFreeTwo(false);
        } else {
            bTeamStat.setThirdQuarterfirstFreeTwo(true);
        }

        if(aTeamStat.getFourthQuarterfirstFreeTwo()){
            bTeamStat.setFourthQuarterfirstFreeTwo(false);
        } else {
            bTeamStat.setFourthQuarterfirstFreeTwo(true);
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
