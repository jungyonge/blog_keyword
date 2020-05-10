package blog.quartz;

import blog.telegram.sendMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static blog.coupang.CoupangCateParse.URLConnectionString;

public class checkDeal  implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            getSoldOutInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean getSoldOutInfo() throws Exception {
        Boolean soldOut = false;
        String checkSoldOut = "";

        sendMessage sendMessage = new sendMessage();


        String url = "https://www.coupang.com/vp/products/1384804427?sourceType=share&itemId=2419615336&vendorItemId=70413795361&shareChannel=kakaoTalk&isAddedCart=";

        try {
//            String responseData = CrawlUtil.getInputStreamToString(response.getEntity().getContent());
            String responseData = requestURLToString(url, true,5,0);
            Document doc = Jsoup.parse(responseData);
            checkSoldOut = doc.select("div.oos-label").text();
            TimeZone time;
            Date date = new Date();
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String first = df1.format(date);
            if (!checkSoldOut.contains("품절")) {
                sendMessage.sendMessage();
                System.out.println(first + " 재입고 됨");
                System.out.println(checkSoldOut);
            }else {
                System.out.println(first + " 재입고 안됨 문구 상태 : " + checkSoldOut);
            }

        } catch (Exception e){
        }

        return soldOut;
    }

    /**
     * URLConnection 연결로 데이터 호출(재시도 횟수만큼 재시도)
     *
     * @param url
     * @param isCoupang
     * @param retryCnt  재시도 최대 횟수
     * @param curCnt    현재 시도 횟수
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public String requestURLToString(String url, boolean isCoupang, int retryCnt, int curCnt) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        String returnData = null;

//        logger.debug("[URL Connection] : " + url);

        // 시도 횟수 증가
        curCnt++;

        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null); // No validation for now
            con.setSSLSocketFactory(context.getSocketFactory());
            con.setDoOutput(true);
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.220 Whale/1.3.51.7 Safari/537.36");
            con.setRequestProperty("Accept", "application/xhtml+xml, */*");
            con.setRequestProperty("Accept-Language", "ko,ko-KR;q=0.9,en-US;q=0.8,en;q=0.7,ja;q=0.6");

            if (isCoupang)
                con.setRequestProperty("cookie", "sid=8615b532673a4b81b8c8bbd9bc858d059e7ed53a; PCID=15457016336445250506383; _ga=GA1.2.995817309.1542766693; _gid=GA1.2.99606668.1542766693; cto_lwid=79a98655-7b73-4ce2-8801-759894943d5a; trac_sid=\"\"; trac_appver=\"\"; searchSoterTooltip=OFF; trac_src=1139086; trac_spec=10799999; trac_addtag=400; trac_ctag=15735181; trac_lptag=AF8824652; _fbp=fb.1.1542864453060.1427270413; ak_bmsc=E8DAA56876FEB865402B46CE27DF1A77B6A26A57A2420000443EF65B0CBC6511~plT8Idi2ZQad9w5LibzCSpMVO9yz3tzJUoBqV4vI78NOex5EcfDAjyZRu84GWMHtMKJY35+grjAcmiuWSZSXiiMEJSV4Hsshjgy8MB9kRaGn1s8TbxHwJODaRkfXZk7nX3WtIeu2iYVKlDXYlyjUgrI4+I3Et2beqj86vKrGwU/fq++BdRdHWDCOC3ojfRiUUJzZZ0ezJZOo9MnZCTUz4vGNIjN8I/YEemLrpdj9A0RbPLLdc0flSerSZwtBAkDm2T; trac_itime=20181122142743; baby-isWide=wide; bm_sv=64BC94C92B33AAACCEAA81B9CC2B8C50~liEIkMkwOB6rmeDdvj6wL//0Cn/kulbReYg0yqU2eUOkGjJR/tBPJOQOJnU1utEfKF9NVu/s07RF1Wi67ui0aCSg5Gcft+QHV8QcByvLDspjgxHyaRTJd8thwaAPkaTRG8htNykqP46oYK9gM3GHEI0iGrVQ/lz95586YGiJRcc=");

            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);

            returnData = URLConnectionString(con, "UTF-8");

        } catch (IOException e) {
            if (curCnt < retryCnt) {
                returnData = requestURLToString(url, isCoupang, retryCnt, curCnt);
            } else {
                throw new IOException(e.getMessage());
            }
        }

        return returnData;
    }


    public static void main(String[] args) throws Exception {
      checkDeal checkDeal = new checkDeal();
      checkDeal.getSoldOutInfo();
    }
}
