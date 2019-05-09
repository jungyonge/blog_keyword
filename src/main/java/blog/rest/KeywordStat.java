package blog.rest;

import blog.jsoup.jsoup;
import blog.model.RelateKeywordStatModel;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import blog.util.PropertiesLoader;
import blog.util.Signatures;

import java.io.*;
import java.net.*;
import java.security.SignatureException;
import java.util.*;

@RestController
public class KeywordStat {
    private SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());
    private Gson gson = new Gson();
    private Properties properties;
    {
        try {
            properties = PropertiesLoader.fromResource("sample.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String baseUrl = properties.getProperty("BASE_URL");
    private String apiKey = properties.getProperty("API_KEY");
    private String secretKey = properties.getProperty("SECRET_KEY");
    private long customerId = Long.parseLong(properties.getProperty("CUSTOMER_ID"));
    private String charset = "UTF-8";
    private String showDetail = "1";

    //특정 키워드의 연관검색어 구하기 네이버광고 API사용
    @GetMapping("/getRelateKeyword/{Keyword}")
    public  void getRelateKeyword(@PathVariable("Keyword") String keyword){
        try {
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String hmacSHA256 = Signatures.of(timestamp,"GET","/keywordstool",secretKey);
            String query = String.format("hintKeywords=%s&showDetail=%s", URLEncoder.encode(keyword,charset),URLEncoder.encode(showDetail,charset));
            getRelateKeywordHttpConnection(hmacSHA256,timestamp,baseUrl+"/keywordstool",apiKey,customerId,query);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }

    //Master테이블의 연관검색어 구하기 네이버 광고 API사용
    @GetMapping("/getRelateKeywordByTable")
    public void getRelateKeywordByTable(){
        List masterList = null;
        masterList = setalarmDAO.getKeywordMaster();
        int masterSize = masterList.size();

        for(int i = 0 ; i < masterSize; i++) {
            HashMap<String, Object> masterMap = (HashMap<String, Object>) masterList.get(i);
            String keyword = String.valueOf(masterMap.get("keyword"));
            try {
                String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                String hmacSHA256 = Signatures.of(timestamp, "GET", "/keywordstool", secretKey);
                String query = String.format("hintKeywords=%s&showDetail=%s", URLEncoder.encode(keyword,charset),URLEncoder.encode(showDetail,charset));
                getRelateKeywordHttpConnection(hmacSHA256, timestamp, baseUrl + "/keywordstool", apiKey, customerId,query);
            } catch (SignatureException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    //저장된 키워드 stat 구하기기
   @GetMapping("/getKeywordStat")
    public void getKeywordStat(){
        List keywordList = null;
        keywordList = setalarmDAO.getKeywordRelate();
        int size = keywordList.size();
        for(int i = 0 ; i < size; i++){

            HashMap<String,Object> keywordMap = (HashMap<String, Object>) keywordList.get(i);
            try {
                String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                String hmacSHA256 = Signatures.of(timestamp,"GET","/keywordstool",secretKey);
                String keyword = String.valueOf(keywordMap.get("keyword_rel"));
                String keywordNoEmpty = keyword.replaceAll(" ", "");
                String query = String.format("hintKeywords=%s&showDetail=%s", URLEncoder.encode(keywordNoEmpty,charset),URLEncoder.encode(showDetail,charset));
                getKeywordStatHttpConnection(hmacSHA256,timestamp,baseUrl+"/keywordstool",apiKey,customerId,query,keyword);

            } catch (IOException | SignatureException e) {
                e.printStackTrace();
            }
        }
        setalarmDAO.updateUsed_Relete();
    }

    public void getRelateKeywordHttpConnection(String hmacSHA256, String timestamp, String requestURL, String apikey, long customerId, String query) {
        HttpURLConnection connection = null;
        BufferedReader input = null;
        System.out.println(hmacSHA256 + "  " + timestamp );
        RelateKeywordStatModel relateKeywordStatModel = null;
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            //Private API Header 세팅
            URL url = new URL(requestURL + "?" + query);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("X-Timestamp", timestamp);
            connection.setRequestProperty("X-API-KEY", apikey);
            connection.setRequestProperty("X-Customer", String.valueOf(customerId));
            connection.setRequestProperty("X-Signature", hmacSHA256);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            relateKeywordStatModel = gson.fromJson(input, RelateKeywordStatModel.class);
            int size = relateKeywordStatModel.getKeywordList().size();
                for(int i = 0; i < size; i++){
                    String key = relateKeywordStatModel.getKeywordList().get(i).getRelKeyword();
                    map.put("keyword",key);
                    setalarmDAO.insertKeyword_Relate(map);
                    System.out.println(key);
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getKeywordStatHttpConnection(String hmacSHA256, String timestamp, String requestURL, String apikey, long customerId, String query, String keyword) {
        jsoup jsoup = new jsoup();
        Gson gson = new Gson();
        HttpURLConnection connection = null;
        BufferedReader input = null;
        RelateKeywordStatModel relateKeywordStatModel = null;
        Map<String, Object> map = new HashMap<String, Object>();

            try {
                //Private API Header 세팅
                Map<String, Integer> res = jsoup.blogPostStat(keyword);

                URL url = new URL(requestURL + "?" + query);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept-Charset", "UTF-8");
                connection.setRequestProperty("X-Timestamp", timestamp);
                connection.setRequestProperty("X-API-KEY", apikey);
                connection.setRequestProperty("X-Customer", String.valueOf(customerId));
                connection.setRequestProperty("X-Signature", hmacSHA256);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                relateKeywordStatModel = gson.fromJson(input, RelateKeywordStatModel.class);

                map.put("relKeyword",keyword);
                map.put("monthlyPcQcCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyPcQcCnt());
                map.put("monthlyMobileQcCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyMobileQcCnt());
                map.put("monthlyAvePcClkCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAvePcClkCnt());
                map.put("monthlyAveMobileClkCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAveMobileClkCnt());
                map.put("monthlyAvePcCtr", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAvePcCtr());
                map.put("monthlyAveMobileCtr", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAveMobileCtr());
                map.put("plAvgDepth", relateKeywordStatModel.getKeywordList().get(0).getPlAvgDepth());
                map.put("compIdx", relateKeywordStatModel.getKeywordList().get(0).getCompIdx());
                map.put("totalPost",res.get("totalPost"));
                map.put("naverCnt",res.get("Naver"));
                map.put("tstoryCnt",res.get("Tstory"));
                map.put("elseCnt",res.get("Else"));
                map.put("whereBlog", res.get("whereBlog"));
                setalarmDAO.insertKeywordStat(map);
            } catch (IOException e) {
                e.printStackTrace();
            }

        System.out.println(keyword);
    }

}
