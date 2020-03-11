package blog.coupang;

import blog.jsoup.Basketball;
import blog.model.BasketballModel;
import blog.model.TempDealVO;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    // 로켓배송 카테고리 목록
    /*private String[] cateRoketDelvrArr = {
            "194176//식품",
            "115573//생활용품",
            "176422//뷰티",
            "184455//홈인테리어",
            "178155//가전디지털",
            "185569//주방용품",
            "221834//출산/유아동",
            "115574//반려동물용품",
            "317679//완구/취미",
            "183960//자동차용품",
            "177195//문구/오피스",
            "317678//스포츠/레저",
            "317677//도서/음반/DVD",
            "305698//헬스/건강식품",
            "186664//여성패션",
            "186969//남성패션",
            "213414//여아패션 (3세 이상)",
            "213641//남아패션 (3세 이상)",
            "213101//베이비패션 (0~3세)"
    };*/
    private String[] cateRoketDelvrArr = {
            "194176//로켓배송/식품",
            "115573//로켓배송/생활용품",
            "176422//로켓배송/뷰티",
            "184455//로켓배송/홈인테리어",
            "178155//로켓배송/가전디지털",
            "185569//로켓배송/주방용품",
            "221834//로켓배송/출산/유아동",
            "115574//로켓배송/반려동물용품",
            "317679//로켓배송/완구/취미",
            "183960//로켓배송/자동차용품",
            "177195//로켓배송/문구/오피스",
            "317678//로켓배송/스포츠/레저",
            "317677//로켓배송/도서/음반/DVD",
            "305698//로켓배송/헬스/건강식품",
            "186664//여성패션//186666//로켓배송/티셔츠",
            "186664//여성패션//186676//로켓배송/블라우스/셔츠",
            "186664//여성패션//224931//로켓배송/원피스/세트류",
            "186664//여성패션//186703//로켓배송/바지/레깅스",
            "186664//여성패션//384722//로켓배송/스커트/치마",
            "186664//여성패션//186693//로켓배송/니트류/조끼",
            "186664//여성패션//186671//로켓배송/맨투맨/후드집업",
            "186664//여성패션//186718//로켓배송/아우터",
            "186664//여성패션//186727//로켓배송/패션운동복",
            "186664//여성패션//186736//로켓배송/비키니/비치웨어",
            //"186664//여성패션//186746//빅사이즈 의류",
            "186664//여성패션//186810///로켓배송/속옷/잠옷",
            "186664//여성패션//186875///로켓배송/임부복",
            //"186664//여성패션//402745//테마의류",
            //"186664//여성패션//207907//커플룩/패밀리룩",
            "186664//여성패션//197258//로켓배송/신발",
            "186664//여성패션//197292//로켓배송/가방/잡화",
            //"186664//여성패션//186933//해외직구",
            "186969//남성패션//186971//로켓배송/티셔츠",
            "186969//남성패션//186976//로켓배송/맨투맨/후드티",
            "186969//남성패션//186981//로켓배송/셔츠",
            "186969//남성패션//186996//로켓배송/바지/청바지",
            "186969//남성패션//187020//로켓배송/트레이닝복",
            "186969//남성패션//186980//로켓배송/후드집업/집업류",
            "186969//남성패션//186985//로켓배송/스웨터",
            "186969//남성패션//186991//로켓배송/가디건",
            "186969//남성패션//186995//로켓배송/베스트/조끼",
            "186969//남성패션//187007//로켓배송/아우터",
            "186969//남성패션//187084//로켓배송/속옷/잠옷",
            //"186969//남성패션//187032//빅사이즈 의류",
            "186969//남성패션//187025//로켓배송/패션 래쉬가드",
            //"186969//남성패션//207939//커플룩/패밀리룩",
            //"186969//남성패션//402768//테마의류",
            "186969//남성패션//197356//로켓배송/신발",
            "186969//남성패션//197377//로켓배송/가방/잡화",
            "213414//로켓배송/여아패션 (3세 이상)",
            "213641//로켓배송/남아패션 (3세 이상)",
//            "213101//로켓배송/베이비패션 (0~3세)"
    };

    // 로켓직구 카테고리 목록
    private String[] cateRoketDrctrArr = {
            "356277//로켓직구/주방용품",
            "393388//로켓직구/홈인테리어",
            "218672//로켓직구/반려동물용품",
            "266106//로켓직구/도서",
            "393321//로켓직구/생활용품",
            "217526//로켓직구/출산/유아동",
            "217822//로켓직구/메이크업",
            "217886//로켓직구/스킨/바디케어",
            "217957//로켓직구/헤어케어",
            "218018//로켓직구/커피/차/음료",
            "218059//로켓직구/스낵/시리얼",
            "218113//로켓직구/조미료/오일/드레싱",
            "218139//로켓직구/통조림/가공식품",
            "218163//로켓직구/요리/베이킹",
            "362791//로켓직구/부위/효능별 건강식품",
            "217454//로켓직구/비타민/미네랄",
            "217473//로켓직구/영양제",
            "217495//로켓직구/허브/자연추출물",
            "217509//로켓직구/헬스보충식품",
            "217518//로켓직구/다이어트식품",
            "419205//로켓직구/스포츠/레저"
    };


    int errorCount = 0;
    String baseUrl = "https://www.coupang.com";
    String sid = "coupang_plan";
    String sdate;
    String edate;
    Map<String, String> sdidDupCheck;


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

    public String[] getCategoryList() throws IOException, ParseException, InterruptedException, KeyManagementException, NoSuchAlgorithmException {
        JSONArray jsonArray = new JSONArray();

        String rootHtml = "";
        String url = "https://www.coupang.com";

        Map<Object, Object> cateList = new HashMap<>();
        String firstCate = "";



        rootHtml = requestURLToString(url,true,5,0);



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
                         int idx = cateUrl.lastIndexOf("/") + 1;
                         cateUrl = cateUrl.substring(idx);
//                         cateList.put(cateName,cateUrl);
                         System.out.println(cateName + " " + cateUrl);
                     }

                 }

            }
        String[] allCate = new String[i];
            i = 0;
        rootDoc = Jsoup.parse(rootHtml);
        elements = rootDoc.select("div#gnbAnalytics.category-layer ul.menu.shopping-menu-list > li");
        for (Element element : rootDoc.select("div#gnbAnalytics.category-layer ul.menu.shopping-menu-list > li")) {

            String cateName = "";
            String cateUrl = "";

            for (Element firstCateList :  element.select("li.second-depth-list")){
                firstCate = element.select("> a").text();

                for(Element parseCate : firstCateList.select("div.third-depth-list ul li a")){

                    cateName = i + "/" + firstCate + "/" + parseCate.text();
                    cateUrl = parseCate.attr("href");
                    int idx = cateUrl.lastIndexOf("/") + 1;
                    cateUrl = cateUrl.substring(idx);
                    allCate[i] = cateUrl + "//" + cateName;
                    i++;
                    System.out.println(cateName + " " + cateUrl);
                }

            }

        }

        return allCate;

    }


    private boolean htmlToDealsParsing(String url, List<TempDealVO> temps, String cate1Nm , int page) throws Exception {
        Thread.sleep(100);
        boolean result = false;
        String html = requestURLToString(baseUrl + url, true, 5, 0);
        Document rootDoc = Jsoup.parse(html);
        int i = 1;
        Elements list = rootDoc.select("ul#productList > li");
        if (list != null && list.size() > 0) {
            result = true;
            for (Element dealItem : list) {
                try {
                    TempDealVO tempDeals = new TempDealVO();

                    String sdid = dealItem.attr("id");
                    String dcRatio = "";

                    if (sdidDupCheck.containsKey(sdid)) {
                        continue;
                    } else {
                        sdidDupCheck.put(sdid, sdid);
                    }

                    String href = baseUrl + dealItem.select("a.baby-product-link").attr("href");

                    String dealName = dealItem.select("dd.descriptions div.name").text();
                    String nmPrice = dealItem.select("del.base-price").text().replaceAll("[^0-9]", "");
                    String dcPrice = dealItem.select("div.price-wrap:first-child em.sale:not(em.sale-fluid)").select("strong.price-value").text().replaceAll("[^0-9]", "");
                    if (StringUtils.isEmpty(nmPrice))
                        nmPrice = dcPrice;

                    if(nmPrice != dcPrice){
                        double tempRatio = ((Double.valueOf(nmPrice) - Double.valueOf(dcPrice)) / Double.valueOf(nmPrice)) * 100;
                        dcRatio = String.valueOf((int) tempRatio);
                    } else {
                        dcRatio = "0";
                    }

                    String imgUrl = "http:" + dealItem.select("dt.image img").attr("src").replace("230x230ex", "300x300ex");


                    tempDeals.setSdid(sdid);
                    tempDeals.setDealrank(i  + ((page - 1 ) * 120));
                    tempDeals.setDealName(dealName);
                    tempDeals.setCate(cate1Nm);
                    tempDeals.setNmPrice(nmPrice);
                    tempDeals.setDcPrice(dcPrice);
                    tempDeals.setDcRatio(dcRatio);
                    tempDeals.setProductUrl(href);
                    tempDeals.setImgUrl(imgUrl);

                    setalarmDAO.insertCoupangDeal(tempDeals);
                    i++;
//                    temps.add(tempDeals);
                    System.out.println(tempDeals);

                } catch (Exception ex) {
                    errorCount++;
                    System.out.println(ex);
                    if (errorCount > 100)
                        throw new Exception(this.getClass().getName() + ".parseDefault:::::" + sid + " : 상품 수집 파싱 오류가 많습니다. 점검 필요");
                }
            }
        }
        return result;
    }

    public List<TempDealVO> parseDefault(String encoding) throws Exception {


        List<TempDealVO> temps = new ArrayList<>();

        sdidDupCheck = new HashMap<>();
        errorCount = 0;
        Map<Object, Object> cateList = new HashMap<>();
        String[] allCate;


        String[] cateInfo = null;
        String cateUrlKey = null;
        String cate1Id = null;
        String cate1Nm = null;
        String cate2Id = null;
        String cate2Nm = null;

        int otherDealSize = 0;

        try {

            allCate = getCategoryList();

            for (String cate : allCate) {

                cateInfo = cate.split("//");

                cate1Id = cateInfo[0];
                cate1Nm = cateInfo[1];
//                28/패션의류/잡화/베이비 여아
                if (cateInfo.length > 2) {
                    cateUrlKey = cateInfo[2];
                    cate2Nm = cateInfo[3];
                    cate2Id = cateInfo[2];
                } else {
                    cateUrlKey = cateInfo[0];
                    cate2Nm = null;
                    cate2Id = null;
                }

                String[] cateDetail = cate1Nm.split("/");
                if(Integer.parseInt(cateDetail[0]) > 1819){
                    for (int page = 1; page <= 9; page++) {

                        String url = "/np/categories/" + cateUrlKey + "?listSize=120&page=" + page;

                        htmlToDealsParsing(url, temps,cate1Nm, page);
                    }

                }

            }
//
//            로켓배송 카테고리 별(19 * 120 * 9) 9페이지까지 처리
//             현재 1뎁쓰 기준으로 루프(추후 뎁쓰가 더 늘어 나게 변경 가능성 있어 임시로 하드코딩
//             일부 카테고리 2뎁쓰 기준으로 변경. 2뎁쓰의 카테고리 경우 2뎁쓰까지 카테고리 지정. 2019.04.22. by Daniel
            for (String cate : cateRoketDelvrArr) {
                cateInfo = cate.split("//");

                cate1Id = cateInfo[0];
                cate1Nm = cateInfo[1];

                if (cateInfo.length > 2) {
                    cateUrlKey = cateInfo[2];
                    cate2Nm = cateInfo[3];
                    cate2Id = cateInfo[2];
                } else {
                    cateUrlKey = cateInfo[0];
                    cate2Nm = null;
                    cate2Id = null;
                }

                int beforeDealSize = temps.size();

                for (int page = 1; page <= 9; page++) {

                    String url = "/np/campaigns/82/components/" + cateUrlKey + "?listSize=120&page=" + page;

                    htmlToDealsParsing(url, temps,cate1Nm,page);
                }

            }

            //로켓직구 카테고리 별(21 * 60 * 17) 17페이지까지 처리
            // 현재 1뎁쓰 기준으로 루프(추후 뎁쓰가 더 늘어 나게 변경 가능성 있어 임시로 하드코딩
//            for (String cate : cateRoketDrctrArr) {
//
//                cateInfo = cate.split("//");
//
//                cate1Id = cateInfo[0];
//                cate1Nm = cateInfo[1];
//
//                if (cateInfo.length > 2) {
//                    cateUrlKey = cateInfo[2];
//                    cate2Nm = cateInfo[3];
//                    cate2Id = cateInfo[2];
//                } else {
//                    cateUrlKey = cateInfo[0];
//                    cate2Nm = null;
//                    cate2Id = null;
//                }
//
//                int beforeDealSize = temps.size();
//
//                for (int page = 1; page <= 17; page++) {
//
//                    String url = "/np/coupangglobal/categories/" + cateUrlKey + "?eventCategory=GNB2&eventLabel=coupangglobal_all&page=" + page;
//
//                    htmlToDealsParsing(url, temps,cate1Nm);
//                }
//
//            }

            if (temps.size() == 0) throw new Exception("수집 상품수가 없습니다. 수집 점검 필요");
        } catch (Exception e) {
        }


        return temps;
    }

    public String getDealInfoDetal(String url) throws Exception {

//        https://www.coupang.com/vp/products/185502198/items/530600312/vendoritems/4381872911
//        https://www.coupang.com/vp/products/185502198?itemId=530600312&vendorItemId=4381872911&sourceType=CATEGORY&categoryId=186666
        String result = "";
        try {

            Thread.sleep(100);
            Map<Object,Object> paramMap = new HashMap<>();
            String[] urlArr = url.split("\\?");



            String[] testArr = urlArr[1].split("&");
            for(String tessStr : testArr){
                String[] testArr2 = tessStr.split("=");
                paramMap.put(testArr2[0],testArr2[1]);
            }

            url = urlArr[0] + "/items/" + paramMap.get("itemId") + "/vendoritems/" + paramMap.get("vendorItemId");
            String json = requestURLToString(url, true, 5, 0);
            JsonParser jsonParser = new JsonParser();

            JsonObject jsonObject = (JsonObject) jsonParser.parse(json);
            JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonObject.get("essentials").toString());
            String thtd = "";
            String tr = "";
            for(int i = 1 ; i < jsonArray.size() + 1 ; i++){
                JsonObject object = (JsonObject) jsonArray.get(i - 1 );
                if(i != jsonArray.size()) {
                    thtd +=
                            "     <th>" + object.get("title").toString().replaceAll("\\\"", "") + "</th>\n" +
                            "     <td>" + object.get("description").toString().replaceAll("\\\"", "") + " </td>\n" ;
                }else {
                    thtd +=
                            "     <th>" + object.get("title").toString().replaceAll("\\\"", "") + "</th>\n" +
                            "     <td colspan=\"3\">" + object.get("description").toString().replaceAll("\\\"", "") + " </td>\n" ;
                }

                if( i % 2 == 0){
                    tr +=
                            "       <tr>\n" +
                                    thtd +
                            "      </tr>\n" ;
                    thtd = "";
                }

                if(i == jsonArray.size()){
                    tr +=
                            "       <tr>\n" +
                                    thtd +
                            "      </tr>\n" ;
                    thtd = "";
                }

            }
            result =
                    "<table class=\"prod-delivery-return-policy-table essential-info-table\">\n" +
                            "                <colgroup>\n" +
                            "                    <col width=\"150px\">\n" +
                            "                    <col width=\"230px\">\n" +
                            "                    <col width=\"150px\">\n" +
                            "                    <col width=\"*\">\n" +
                            "                </colgroup>\n" +
                            "                <tbody>\n" +
                                                tr+
                            "                </tbody>\n" +
                            "            </table>";

        } catch (Exception ex) {
            errorCount++;
            System.out.println(ex);
            if (errorCount > 100)
                throw new Exception(this.getClass().getName() + ".parseDefault:::::" + sid + " : 상품 수집 파싱 오류가 많습니다. 점검 필요");
        }


        return result;
    }

    public Map getDealImg(String url) throws Exception {

//        https://www.coupang.com/vp/products/185502198/items/530600312/vendoritems/4381872911
//        https://www.coupang.com/vp/products/185502198?itemId=530600312&vendorItemId=4381872911&sourceType=CATEGORY&categoryId=186666
        Map<Object,String > result = new HashMap<Object, String>();
        try {

            Thread.sleep(100);
            Map<Object,Object> paramMap = new HashMap<>();
            String[] urlArr = url.split("\\?");

            String[] testArr = urlArr[1].split("&");
            for(String tessStr : testArr){
                String[] testArr2 = tessStr.split("=");
                paramMap.put(testArr2[0],testArr2[1]);
            }

            url = urlArr[0] + "/items/" + paramMap.get("itemId") + "/vendoritems/" + paramMap.get("vendorItemId");
            String json = requestURLToString(url, true, 5, 0);
            JsonParser jsonParser = new JsonParser();

            JsonObject jsonObject = (JsonObject) jsonParser.parse(json);
            JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonObject.get("details").toString());
            int i = 0;
            for (JsonElement js : jsonArray){
                JsonArray imgDesc = js.getAsJsonObject().get("vendorItemContentDescriptions").getAsJsonArray();
                JsonObject tempObj = imgDesc.get(0).getAsJsonObject();
                String imgUrl = tempObj.get("content").getAsString();
                result.put(i,imgUrl);
                i++;
            }

        } catch (Exception ex) {
            errorCount++;
            System.out.println(ex);
            if (errorCount > 100)
                throw new Exception(this.getClass().getName() + ".parseDefault:::::" + sid + " : 상품 수집 파싱 오류가 많습니다. 점검 필요");
        }


        return result;
    }

    public String getProductImg(Map productImgMap){

        StringBuilder sb = new StringBuilder();

        for (int i = 0 ; i < productImgMap.size() ; i++){
            if(productImgMap.get(i).toString().contains("src") || productImgMap.get(i).toString().contains("span")) {
                sb.append(productImgMap.get(i));
            } else {
                sb.append("  <img src=\"").append(productImgMap.get(i)).append("\" data-lazy-src=\"\" data-width=\"500\" data-height=\"500\" width=\"500\" height=\"500\"></a>\n");
            }
        }
        String result =
                    "<div>\n" +
                        "  <center>\n" +
                        "    \n" +
                        "        <p >제품상세 이미지<br>\n" +
                        "\n" +      sb +
                         "  </center>\n" +
                    "</div>\n" ;

        return result;
    }

    public String getDealReview(String sdid) throws Exception {

//        https://www.coupang.com/vp/products/185502198/items/530600312/vendoritems/4381872911
//        https://www.coupang.com/vp/products/185502198?itemId=530600312&vendorItemId=4381872911&sourceType=CATEGORY&categoryId=186666
//        https://www.coupang.com/vp/product/reviews?productId=185502198&page=1&size=5&sortBy=DATE_DESC&ratings=5&q=&viRoleCode=3&ratingSummary=true



        String result = "";
        String reviewDiv = "";
        try {

            Thread.sleep(100);
            List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
            Map<String,Object> paramMap = new HashMap<>();

            String url = "https://www.coupang.com/vp/product/reviews?productId=" + sdid + "&page=1&size=5&sortBy=ORDER_SCORE_ASC&ratings=5&q=&viRoleCode=3&ratingSummary=true";
            String html = requestURLToString(url, true, 5, 0);
            Document rootDoc = Jsoup.parse(html);



            Elements list = rootDoc.select("article.sdp-review__article__list.js_reviewArticleReviewList");
            if (list != null && list.size() > 0) {
                reviewDiv +=     "  아래의 베스트 리뷰도 참조하시면 좋을 것 같네요. \n<br>" ;

                for (Element dealItem : list) {
                    int rating = Integer.parseInt(dealItem.select("div.sdp-review__article__list__info__product-info__star-orange.js_reviewArticleRatingValue").attr("data-rating"));
                    String review = dealItem.select("div.sdp-review__article__list__review__content.js_reviewArticleContent").html();
                    if(review.length() > 200){
                        review = review.substring(0,199);
                    }

                    String star ="";
                    for(int i = 0 ; i < rating ; i++){
                        star += "★";
                    }



                    reviewDiv +=
                            "    \t\t별점:\t<span style=\"color:rgb(255, 200, 0);\"> "+ star + "<br></span>\n" +
                                    "            상품평 :   " + review  +
                                    "    \t\t<br>\n" +
                                    "    \t\t<br>\n" +
                                    "<div class=\"se-module se-module-horizontalLine\">\n" +
                                    "                                <hr class=\"se-hr\">\n" +
                                    "</div>" ;
                }
            }else {
                reviewDiv +=
                                "           아쉽게 해당상품의 등록된 상품평이 없네요. <br>" +
                                "           판매자의 다른 리뷰도 같이 확인 해보세요!"+
                                "    \t\t<br>\n" +
                                "    \t\t<br>\n" ;
            }

            System.out.println(reviewDiv);
        } catch (Exception ex) {
            errorCount++;
            System.out.println(ex);
            if (errorCount > 100)
                throw new Exception(this.getClass().getName() + ".parseDefault:::::" + sid + " : 상품 수집 파싱 오류가 많습니다. 점검 필요");
        }


        return reviewDiv;
    }

    public static void main(String[] args) {
        CoupangCateParse coupangCateParse = new CoupangCateParse();

        String test = "https://www.coupang.com/vp/products/185502198?itemId=530600312&vendorItemId=4381872911&sourceType=CATEGORY&categoryId=186666";

        try {
            coupangCateParse.getDealImg("https://www.coupang.com/vp/products/185502198?itemId=530600312&vendorItemId=4381872911&sourceType=CATEGORY&categoryId=186666");
//            coupangCateParse.getDealReview("185502198");
//            coupangCateParse.parseDefault("UTF-8");
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
