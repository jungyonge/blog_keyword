package blog.telegram;

import blog.jsoup.Soccer;
import blog.model.SoccerModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class sendMessage {

    static private String botToken = "992109721:AAHNgtqq3o7GrVQ6_dvZvxeZzOkra52VzjU";


    public void sendMessage() {
        String url = null;
        System.out.println("----------------------------------------------------------------------------");

        try {
            String[] chatidArr = {"495476676"};

            for (String chatId : chatidArr){
                url = "https://api.telegram.org/bot" + botToken + "/sendmessage?text=" + "닌텐도 재입고 %0A" +
                        "https://www.coupang.com/vp/products/1384804427?sourceType=share&itemId=2419615336&vendorItemId=70413795361&shareChannel=kakaoTalk&isAddedCart=" +
                        "" + "&chat_id=" + chatId ;
                requestURLToString(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("전송된 url = " + url);
    }

    // URLConnection 연결로 데이터 호출
    public String requestURLToString(String url) throws IOException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
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


    public static void main(String[] args) throws Exception {
        sendMessage sendMessage = new sendMessage();
        sendMessage.sendMessage();
        ;
    }
}
