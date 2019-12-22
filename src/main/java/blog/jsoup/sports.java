package blog.jsoup;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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
        SportModel sportModel = new SportModel();
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
                    sportModel.setLeague(element.select("thead tr th.reague").text());
                    String[] arrayHandi = element.select("tbody > tr > td.line").text().split(" ");

                    sportModel.setPointLine(Double.valueOf(arrayHandi[0]));
                    sportModel.setHandiCap(Double.valueOf(arrayHandi[1]));

                    String[] arrayTotalScore = element.select("tbody > tr > td.score").text().split(" ");
                    sportModel.setAwayTotalPoint(Integer.valueOf(arrayTotalScore[0]));
                    sportModel.setHomeTotalPoint(Integer.valueOf(arrayTotalScore[1]));

                    if ((sportModel.getHomeTotalPoint() + sportModel.getHandiCap()) > sportModel.getAwayTotalPoint()) {
                        sportModel.setHandiCapResult("승리");
                    } else if ((sportModel.getHomeTotalPoint() + sportModel.getHandiCap()) < sportModel.getAwayTotalPoint()) {
                        sportModel.setHandiCapResult("패배");
                    } else {
                        sportModel.setHandiCapResult("적특");
                    }

                    if ((sportModel.getHomeTotalPoint() + sportModel.getAwayTotalPoint()) > sportModel.getPointLine()) {
                        sportModel.setPointLineResult("오버");
                    } else if ((sportModel.getHomeTotalPoint() + sportModel.getAwayTotalPoint()) < sportModel.getPointLine()) {
                        sportModel.setPointLineResult("언더");
                    } else {
                        sportModel.setPointLineResult("적특");
                    }

                    String[] arrayFirstScore = element.select("tbody > tr > td.s").text().split(" ");

                    sportModel.setAwayFirstQuarterPoint(Integer.valueOf(arrayFirstScore[0]));
                    sportModel.setHomeFirstQuarterPoint(Integer.valueOf(arrayFirstScore[5]));

                    double handi = sportModel.getHandiCap() / 4;
                    int handiInt = (int) handi;
                    double pointHandi = handi - handiInt;

                    if (pointHandi == 0.5 || pointHandi == -0.5) {
                        sportModel.setFirstQuarterHandiCap(sportModel.getHandiCap() / 4);
                    } else {
                        sportModel.setFirstQuarterHandiCap(Double.valueOf(Math.round(sportModel.getHandiCap() / 4)));
                    }

                    double point = sportModel.getPointLine() / 4;
                    int pointInt = (int) point;
                    double pointLine = point - pointInt;

                    if (pointLine == 0.5) {
                        sportModel.setFirstQuarterPointLine(sportModel.getPointLine() / 4);
                    } else {
                        sportModel.setFirstQuarterPointLine(Double.valueOf(Math.round(sportModel.getPointLine() / 4)));
                    }

                    if ((sportModel.getHomeFirstQuarterPoint() + sportModel.getFirstQuarterHandiCap()) > sportModel.getAwayFirstQuarterPoint()) {
                        sportModel.setFirstQuarterHandiCapResult("승리");
                    } else if ((sportModel.getHomeFirstQuarterPoint() + sportModel.getFirstQuarterHandiCap()) < sportModel.getAwayFirstQuarterPoint()) {
                        sportModel.setFirstQuarterHandiCapResult("패배");
                    } else {
                        sportModel.setFirstQuarterHandiCapResult("적특");
                    }

                    if ((sportModel.getHomeFirstQuarterPoint() + sportModel.getAwayFirstQuarterPoint()) > sportModel.getFirstQuarterPointLine()) {
                        sportModel.setFirstQuarterPointLineResult("오버");
                    } else if ((sportModel.getHomeFirstQuarterPoint() + sportModel.getAwayFirstQuarterPoint()) < sportModel.getFirstQuarterPointLine()) {
                        sportModel.setFirstQuarterPointLineResult("언더");
                    } else {
                        sportModel.setFirstQuarterPointLineResult("적특");
                    }

                    String[] arrayScore = element.select("tfoot > tr > td.s").text().split(" ");

                    sportModel.setFirstQuarterPoint(Integer.valueOf(arrayScore[0]));
                    sportModel.setSecondQuarterPoint(Integer.valueOf(arrayScore[1]));
                    sportModel.setThirdQuarterPoint(Integer.valueOf(arrayScore[2]));
                    sportModel.setThirdQuarterPoint(Integer.valueOf(arrayScore[3]));


                    sportModel.setTime(element.select("thead tr th.ptime").text().replaceAll("오전 ", "").replaceAll("오후 ", ""));
                    sportModel.setAwayTeam(element.select("tbody tr > td.teaminfo.visitor strong").text());
                    sportModel.setHomeTeam(element.select("tbody tr > td.teaminfo.hometeam strong").text());

                    for (Element element1 : element.select("tbody > tr > td.f.ico_linescore > p")) {
                        if (i == 0) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                sportModel.setFirstQuarterfirstPoint(true);
                            } else {
                                sportModel.setFirstQuarterfirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                sportModel.setFirstQuarterfirstFreeTwo(true);
                            } else {
                                sportModel.setFirstQuarterfirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                sportModel.setFirstQuarterfirstTwoPoint(true);
                            } else {
                                sportModel.setFirstQuarterfirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                sportModel.setFirstQuarterfirstThreePoint(true);
                            } else {
                                sportModel.setFirstQuarterfirstThreePoint(false);
                            }
                        }
                        if (i == 1) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                sportModel.setSecondQuarterfirstPoint(true);
                            } else {
                                sportModel.setSecondQuarterfirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                sportModel.setSecondQuarterfirstFreeTwo(true);
                            } else {
                                sportModel.setSecondQuarterfirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                sportModel.setSecondQuarterfirstTwoPoint(true);
                            } else {
                                sportModel.setSecondQuarterfirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                sportModel.setSecondQuarterfirstThreePoint(true);
                            } else {
                                sportModel.setSecondQuarterfirstThreePoint(false);
                            }
                        }
                        if (i == 2) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                sportModel.setThirdQuarterfirstPoint(true);
                            } else {
                                sportModel.setThirdQuarterfirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                sportModel.setThirdQuarterfirstFreeTwo(true);
                            } else {
                                sportModel.setThirdQuarterfirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                sportModel.setThirdQuarterfirstTwoPoint(true);
                            } else {
                                sportModel.setThirdQuarterfirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                sportModel.setThirdQuarterfirstThreePoint(true);
                            } else {
                                sportModel.setThirdQuarterfirstThreePoint(false);
                            }
                        }
                        if (i == 3) {
                            if (element1.select("span.ico_firstpoint").text().equals("첫득점")) {
                                sportModel.setFourthQuarterfirstPoint(true);
                            } else {
                                sportModel.setFourthQuarterfirstPoint(false);
                            }
                            if (element1.select("span.ico_freetwo").text().equals("자유투")) {
                                sportModel.setFourthQuarterfirstFreeTwo(true);
                            } else {
                                sportModel.setFourthQuarterfirstFreeTwo(false);
                            }
                            if (element1.select("span.ico_twopoint").text().equals("2점슛")) {
                                sportModel.setFourthQuarterfirstTwoPoint(true);
                            } else {
                                sportModel.setFourthQuarterfirstTwoPoint(false);
                            }
                            if (element1.select("span.ico_threepoint").text().equals("3점슛")) {
                                sportModel.setFourthQuarterfirstThreePoint(true);
                            } else {
                                sportModel.setFourthQuarterfirstThreePoint(false);
                            }
                        }
                    }
                    System.out.println(sportModel);
                }

            }
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
