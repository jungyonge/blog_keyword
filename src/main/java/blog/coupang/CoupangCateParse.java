package blog.coupang;

import blog.jsoup.Basketball;
import blog.model.BasketballModel;
import blog.model.TempDealVO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CoupangCateParse {


    int errorCount = 0;
    String baseUrl = "https://www.coupang.com";
    String sid = "coupang_plan";
    String sdate;
    String edate;
    Map<String, String> sdidDupCheck;

    TempDealVO tempDeals;

    /**
     * URLConnection으로 얻어진 데이터 String으로 반환
     * @param con
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String URLConnectionString(URLConnection con, String encoding) throws IOException {
        StringBuffer sInputData = new StringBuffer(1024);
        String sInputLine = "";
        BufferedReader in;

        if (org.apache.commons.lang.StringUtils.isBlank(encoding)) {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), encoding));
        }
        while ((sInputLine = in.readLine()) != null) {
            sInputData.append(sInputLine).append("\n");
        }
        in.close();

        return sInputData.toString();
    }
    // URLConnection 연결로 데이터 호출

    public String requestURLToString(String url, boolean isCoupang, int retryCnt, int curCnt) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        String returnData = null;

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

            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);

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

    public void getCategoryList() throws IOException, ParseException, InterruptedException, KeyManagementException, NoSuchAlgorithmException {
        JSONArray jsonArray = new JSONArray();


        String rootHtml = "";
        String url = "https://www.coupang.com";

        Map<Object, Object> cateList = new HashMap<>();
        String firstCate = "";



        rootHtml = requestURLToString(url,true,0,5);



            int i = 0;
            Document rootDoc = Jsoup.parse(rootHtml);
            Elements elements = rootDoc.select("div#gnbAnalytics.category-layer ul.menu.shopping-menu-list > li");
            for (Element element : rootDoc.select("div#gnbAnalytics.category-layer ul.menu.shopping-menu-list > li")) {

                String cateName = "";
                String cateUrl = "";

                 for (Element firstCateList :  element.select("li.second-depth-list")){
                     firstCate = element.select("> a").text();

                     for(Element parseCate : firstCateList.select("div.third-depth-list ul li a")){
                         i++;
                         cateName = i + "/" + firstCate + "/" + parseCate.text();
                         cateUrl = parseCate.attr("href");

                         cateList.put(cateName,cateUrl);
                         System.out.println(cateName + " " + cateUrl);
                     }

                 }

            }
        System.out.println(i);

    }
    private boolean htmlToDealsParsing(String url, List<TempDealVO> temps) throws Exception {
        boolean result = false;
        String html = requestURLToString(baseUrl + url, true, 5, 0);
        Document rootDoc = Jsoup.parse(html);

        Elements list = rootDoc.select("ul#productList > li");
        if (list != null && list.size() > 0) {
            result = true;
            for (Element dealItem : list) {
                try {
                    String sdid = dealItem.attr("id");
                    String dcRatio = "";

                    if (sdidDupCheck.containsKey(sdid)) {
                        continue;
                    } else {
                        sdidDupCheck.put(sdid, sdid);
                    }

                    String href = dealItem.select("a.baby-product-link").attr("href");

                    String dealName = dealItem.select("dd.descriptions div.name").text();
                    String nmPrice = dealItem.select("del.base-price").text().replaceAll("[^0-9]", "");
                    String dcPrice = dealItem.select("div.price-wrap:first-child em.sale:not(em.sale-fluid)").select("strong.price-value").text().replaceAll("[^0-9]", "");
                    if (StringUtils.isEmpty(nmPrice))
                        nmPrice = dcPrice;

                    if(nmPrice != dcPrice){
                        int tempRatio = (Integer.parseInt(nmPrice) - Integer.parseInt(dcPrice)) / Integer.parseInt(nmPrice) * 100;
                        dcRatio = String.valueOf(tempRatio);
                    }

                    String imgUrl = "http:" + dealItem.select("dt.image img").attr("src").replace("230x230ex", "300x300ex");


                    tempDeals.setSdid(sdid);
                    tempDeals.setDealName(dealName);
                    tempDeals.setNmPrice(nmPrice);
                    tempDeals.setDcPrice(dcPrice);
                    tempDeals.setDcRatio(dcRatio);
                    tempDeals.setProductUrl(href);
                    tempDeals.setImgUrl(imgUrl);

                    temps.add(tempDeals);

                } catch (Exception ex) {
                    errorCount++;
                    if (errorCount > 100)
                        throw new Exception(this.getClass().getName() + ".parseDefault:::::" + sid + " : 상품 수집 파싱 오류가 많습니다. 점검 필요");
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        CoupangCateParse coupangCateParse = new CoupangCateParse();
        try {
            coupangCateParse.getCategoryList();
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
