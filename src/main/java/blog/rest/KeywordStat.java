package blog.rest;

import blog.jsoup.jsoup;
import blog.model.RelateKeywordStatModel;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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
    @GetMapping("/getKeywordStat1")
    public void getKeywordStat1(){
        System.out.println("1번 시작");
        List keywordList = null;
        Map<String, Object> map = new HashMap<String, Object>();
        keywordList = setalarmDAO.getKeywordRelate1();
        String make = "Notmake";
        int size = keywordList.size();
        for(int i = 0 ; i < size; i++){
            HashMap<String,Object> keywordMap = (HashMap<String, Object>) keywordList.get(i);
            try {
                String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                String hmacSHA256 = Signatures.of(timestamp,"GET","/keywordstool",secretKey);
                String keyword = String.valueOf(keywordMap.get("keyword_rel"));
                map.put("keyword_rel", keyword);
                setalarmDAO.updateUsed_Relate(map);
                String keywordNoEmpty = keyword.replaceAll(" ", "");
                String query = String.format("hintKeywords=%s&showDetail=%s", URLEncoder.encode(keywordNoEmpty,charset),URLEncoder.encode(showDetail,charset));
                getKeywordStatHttpConnection1(hmacSHA256,timestamp,baseUrl+"/keywordstool",apiKey,customerId,query,keyword,make);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //저장된 키워드 stat 구하기기
    @GetMapping("/getKeywordStat2")
    public void getKeywordStat2(){
        System.out.println("2번 시작");
        List keywordList = null;
        Map<String, Object> map = new HashMap<String, Object>();
        keywordList = setalarmDAO.getKeywordRelate2();
        String make = "Notmake";
        int size = keywordList.size();
        for(int i = 0 ; i < size; i++){
            HashMap<String,Object> keywordMap = (HashMap<String, Object>) keywordList.get(i);
            try {
                String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                String hmacSHA256 = Signatures.of(timestamp,"GET","/keywordstool",secretKey);
                String keyword = String.valueOf(keywordMap.get("keyword_rel"));
                map.put("keyword_rel", keyword);
                setalarmDAO.updateUsed_Relate(map);
                String keywordNoEmpty = keyword.replaceAll(" ", "");
                String query = String.format("hintKeywords=%s&showDetail=%s", URLEncoder.encode(keywordNoEmpty,charset),URLEncoder.encode(showDetail,charset));
                getKeywordStatHttpConnection2(hmacSHA256,timestamp,baseUrl+"/keywordstool",apiKey,customerId,query,keyword,make);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/getKeywordStat3")
    public void getKeywordStat3(){
        System.out.println("3번 시작");
        List keywordList = null;
        Map<String, Object> map = new HashMap<String, Object>();
        keywordList = setalarmDAO.getKeywordRelate3();
        String make = "Notmake";
        int size = keywordList.size();
        for(int i = 0 ; i < size; i++){
            HashMap<String,Object> keywordMap = (HashMap<String, Object>) keywordList.get(i);
            try {
                String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                String hmacSHA256 = Signatures.of(timestamp,"GET","/keywordstool",secretKey);
                String keyword = String.valueOf(keywordMap.get("keyword_rel"));
                map.put("keyword_rel", keyword);
                setalarmDAO.updateUsed_Relate(map);
                String keywordNoEmpty = keyword.replaceAll(" ", "");
                String query = String.format("hintKeywords=%s&showDetail=%s", URLEncoder.encode(keywordNoEmpty,charset),URLEncoder.encode(showDetail,charset));
                getKeywordStatHttpConnection3(hmacSHA256,timestamp,baseUrl+"/keywordstool",apiKey,customerId,query,keyword,make);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/getKeywordStat4")
    public void getKeywordStat4(){
        System.out.println("4번 시작");
        List keywordList = null;
        Map<String, Object> map = new HashMap<String, Object>();
        keywordList = setalarmDAO.getKeywordRelate4();
        String make = "Notmake";
        int size = keywordList.size();
        for(int i = 0 ; i < size; i++){
            HashMap<String,Object> keywordMap = (HashMap<String, Object>) keywordList.get(i);
            try {
                String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                String hmacSHA256 = Signatures.of(timestamp,"GET","/keywordstool",secretKey);
                String keyword = String.valueOf(keywordMap.get("keyword_rel"));
                map.put("keyword_rel", keyword);
                setalarmDAO.updateUsed_Relate(map);
                String keywordNoEmpty = keyword.replaceAll(" ", "");
                String query = String.format("hintKeywords=%s&showDetail=%s", URLEncoder.encode(keywordNoEmpty,charset),URLEncoder.encode(showDetail,charset));
                getKeywordStatHttpConnection4(hmacSHA256,timestamp,baseUrl+"/keywordstool",apiKey,customerId,query,keyword,make);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //만들어진 키워드 stat 구하기기
    @GetMapping("/getMakeKeywordStat")
    public void getMakeKeywordStat(){
        List keywordList = null;
        Map<String, Object> map = new HashMap<String, Object>();
        keywordList = setalarmDAO.getMakeKeywordRelate();
        String make = "make";
        int size = keywordList.size();
        for(int i = 0 ; i < size; i++){
            HashMap<String,Object> keywordMap = (HashMap<String, Object>) keywordList.get(i);
            try {
                String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                String hmacSHA256 = Signatures.of(timestamp,"GET","/keywordstool",secretKey);
                String keyword = String.valueOf(keywordMap.get("keyword_rel"));
                map.put("keyword_rel", keyword);
                setalarmDAO.updateUsed_Relate(map);
                String keywordNoEmpty = keyword.replaceAll(" ", "");
                String query = String.format("hintKeywords=%s&showDetail=%s", URLEncoder.encode(keywordNoEmpty,charset),URLEncoder.encode(showDetail,charset));
                getKeywordStatHttpConnection1(hmacSHA256,timestamp,baseUrl+"/keywordstool",apiKey,customerId,query,keyword,make);

            } catch (IOException | SignatureException e) {
                e.printStackTrace();
            }
        }
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

    public void getKeywordStatHttpConnection1(String hmacSHA256, String timestamp, String requestURL, String apikey, long customerId, String query, String keyword, String make) {
        jsoup jsoup = new jsoup();
        HttpURLConnection connection = null;
        BufferedReader input = null;
        RelateKeywordStatModel relateKeywordStatModel = null;
        Map<String, Object> resultMap = new HashMap<String, Object>();

            try {
                //Private API Header 세팅
                Map<String, Integer> blogPostStatMap = jsoup.blogPostStat1(keyword);

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
                int responseCode = connection.getResponseCode();
                if(responseCode == 200){
                    input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    relateKeywordStatModel = gson.fromJson(input, RelateKeywordStatModel.class);

                    resultMap.put("relKeyword",keyword);
                    resultMap.put("monthlyPcQcCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyPcQcCnt());
                    resultMap.put("monthlyMobileQcCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyMobileQcCnt());
                    resultMap.put("monthlyAvePcClkCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAvePcClkCnt());
                    resultMap.put("monthlyAveMobileClkCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAveMobileClkCnt());
                    resultMap.put("monthlyAvePcCtr", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAvePcCtr());
                    resultMap.put("monthlyAveMobileCtr", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAveMobileCtr());
                    resultMap.put("plAvgDepth", relateKeywordStatModel.getKeywordList().get(0).getPlAvgDepth());
                    resultMap.put("compIdx", relateKeywordStatModel.getKeywordList().get(0).getCompIdx());
                    resultMap.put("totalPost",blogPostStatMap.get("totalPost"));
                    resultMap.put("naverCnt",blogPostStatMap.get("Naver"));
                    resultMap.put("tistoryCnt",blogPostStatMap.get("Tistory"));
                    resultMap.put("elseCnt",blogPostStatMap.get("Else"));
                    resultMap.put("whereBlog", blogPostStatMap.get("whereBlog"));
                    resultMap.put("whereWeb", blogPostStatMap.get("whereWeb"));
                    resultMap.put("whereMobileBlog",  blogPostStatMap.get("whereMobileBlog"));
                    resultMap.put("whereMobileWeb", blogPostStatMap.get("whereMobileWeb"));
                    resultMap.put("make", make);
                    setalarmDAO.insertKeywordStat(resultMap);
                }
                else{
                    resultMap.put("relKeyword",keyword);
                    resultMap.put("make", "error");
                    setalarmDAO.insertKeywordStat(resultMap);
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        System.out.println("1번 : " + keyword);
    }

    public void getKeywordStatHttpConnection2(String hmacSHA256, String timestamp, String requestURL, String apikey, long customerId, String query, String keyword, String make) {
        jsoup jsoup = new jsoup();
        HttpURLConnection connection = null;
        BufferedReader input = null;
        RelateKeywordStatModel relateKeywordStatModel = null;
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            //Private API Header 세팅
            Map<String, Integer> blogPostStatMap = jsoup.blogPostStat2(keyword);

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
            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                relateKeywordStatModel = gson.fromJson(input, RelateKeywordStatModel.class);

                resultMap.put("relKeyword",keyword);
                resultMap.put("monthlyPcQcCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyPcQcCnt());
                resultMap.put("monthlyMobileQcCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyMobileQcCnt());
                resultMap.put("monthlyAvePcClkCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAvePcClkCnt());
                resultMap.put("monthlyAveMobileClkCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAveMobileClkCnt());
                resultMap.put("monthlyAvePcCtr", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAvePcCtr());
                resultMap.put("monthlyAveMobileCtr", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAveMobileCtr());
                resultMap.put("plAvgDepth", relateKeywordStatModel.getKeywordList().get(0).getPlAvgDepth());
                resultMap.put("compIdx", relateKeywordStatModel.getKeywordList().get(0).getCompIdx());
                resultMap.put("totalPost",blogPostStatMap.get("totalPost"));
                resultMap.put("naverCnt",blogPostStatMap.get("Naver"));
                resultMap.put("tistoryCnt",blogPostStatMap.get("Tistory"));
                resultMap.put("elseCnt",blogPostStatMap.get("Else"));
                resultMap.put("whereBlog", blogPostStatMap.get("whereBlog"));
                resultMap.put("whereWeb", blogPostStatMap.get("whereWeb"));
                resultMap.put("whereMobileBlog",  blogPostStatMap.get("whereMobileBlog"));
                resultMap.put("whereMobileWeb", blogPostStatMap.get("whereMobileWeb"));
                resultMap.put("make", make);
                setalarmDAO.insertKeywordStat(resultMap);
            }
            else{
                resultMap.put("relKeyword",keyword);
                resultMap.put("make", "error");
                setalarmDAO.insertKeywordStat(resultMap);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("2번 : " + keyword);
    }

    public void getKeywordStatHttpConnection3(String hmacSHA256, String timestamp, String requestURL, String apikey, long customerId, String query, String keyword, String make) {
        jsoup jsoup = new jsoup();
        HttpURLConnection connection = null;
        BufferedReader input = null;
        RelateKeywordStatModel relateKeywordStatModel = null;
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            //Private API Header 세팅
            Map<String, Integer> blogPostStatMap = jsoup.blogPostStat3(keyword);

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
            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                relateKeywordStatModel = gson.fromJson(input, RelateKeywordStatModel.class);

                resultMap.put("relKeyword",keyword);
                resultMap.put("monthlyPcQcCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyPcQcCnt());
                resultMap.put("monthlyMobileQcCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyMobileQcCnt());
                resultMap.put("monthlyAvePcClkCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAvePcClkCnt());
                resultMap.put("monthlyAveMobileClkCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAveMobileClkCnt());
                resultMap.put("monthlyAvePcCtr", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAvePcCtr());
                resultMap.put("monthlyAveMobileCtr", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAveMobileCtr());
                resultMap.put("plAvgDepth", relateKeywordStatModel.getKeywordList().get(0).getPlAvgDepth());
                resultMap.put("compIdx", relateKeywordStatModel.getKeywordList().get(0).getCompIdx());
                resultMap.put("totalPost",blogPostStatMap.get("totalPost"));
                resultMap.put("naverCnt",blogPostStatMap.get("Naver"));
                resultMap.put("tistoryCnt",blogPostStatMap.get("Tistory"));
                resultMap.put("elseCnt",blogPostStatMap.get("Else"));
                resultMap.put("whereBlog", blogPostStatMap.get("whereBlog"));
                resultMap.put("whereWeb", blogPostStatMap.get("whereWeb"));
                resultMap.put("whereMobileBlog",  blogPostStatMap.get("whereMobileBlog"));
                resultMap.put("whereMobileWeb", blogPostStatMap.get("whereMobileWeb"));
                resultMap.put("make", make);
                setalarmDAO.insertKeywordStat(resultMap);
            }
            else{
                resultMap.put("relKeyword",keyword);
                resultMap.put("make", "error");
                setalarmDAO.insertKeywordStat(resultMap);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("3번 : " + keyword);
    }

    public void getKeywordStatHttpConnection4(String hmacSHA256, String timestamp, String requestURL, String apikey, long customerId, String query, String keyword, String make) {
        jsoup jsoup = new jsoup();
        HttpURLConnection connection = null;
        BufferedReader input = null;
        RelateKeywordStatModel relateKeywordStatModel = null;
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            //Private API Header 세팅
            Map<String, Integer> blogPostStatMap = jsoup.blogPostStat4(keyword);

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
            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                relateKeywordStatModel = gson.fromJson(input, RelateKeywordStatModel.class);

                resultMap.put("relKeyword",keyword);
                resultMap.put("monthlyPcQcCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyPcQcCnt());
                resultMap.put("monthlyMobileQcCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyMobileQcCnt());
                resultMap.put("monthlyAvePcClkCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAvePcClkCnt());
                resultMap.put("monthlyAveMobileClkCnt", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAveMobileClkCnt());
                resultMap.put("monthlyAvePcCtr", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAvePcCtr());
                resultMap.put("monthlyAveMobileCtr", relateKeywordStatModel.getKeywordList().get(0).getMonthlyAveMobileCtr());
                resultMap.put("plAvgDepth", relateKeywordStatModel.getKeywordList().get(0).getPlAvgDepth());
                resultMap.put("compIdx", relateKeywordStatModel.getKeywordList().get(0).getCompIdx());
                resultMap.put("totalPost",blogPostStatMap.get("totalPost"));
                resultMap.put("naverCnt",blogPostStatMap.get("Naver"));
                resultMap.put("tistoryCnt",blogPostStatMap.get("Tistory"));
                resultMap.put("elseCnt",blogPostStatMap.get("Else"));
                resultMap.put("whereBlog", blogPostStatMap.get("whereBlog"));
                resultMap.put("whereWeb", blogPostStatMap.get("whereWeb"));
                resultMap.put("whereMobileBlog",  blogPostStatMap.get("whereMobileBlog"));
                resultMap.put("whereMobileWeb", blogPostStatMap.get("whereMobileWeb"));
                resultMap.put("make", make);
                setalarmDAO.insertKeywordStat(resultMap);
            }
            else{
                resultMap.put("relKeyword",keyword);
                resultMap.put("make", "error");
                setalarmDAO.insertKeywordStat(resultMap);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("4번 : " + keyword);
    }



    @GetMapping("/testing/{type}/{param}/{where}")
    public void test(@PathVariable("type") String type , @PathVariable("param") String param , @PathVariable("where") int where) {
        System.out.println("test Start");
        System.out.println("진행중인 것 " + type + "/" + param);
        HttpURLConnection connection = null;
        BufferedReader input = null;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List row = null;
        int totalCnt = 0;
        int rowSize = 0;
        Map<String, Object> resultMap1 = new HashMap<String, Object>();
        Map<String, Object> resultMap2 = new HashMap<String, Object>();
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            //Private API Header 세팅

            URL url = new URL("http://openapi.foodsafetykorea.go.kr/api/2520cc2dbe504a248f84/"+ type + "/json/1/5");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                resultMap = gson.fromJson(input, Map.class);
                LinkedTreeMap testList = (LinkedTreeMap) resultMap.get(type);
                totalCnt = Integer.valueOf(testList.get("total_count").toString());
            }
            int cnt = totalCnt / 1000;
            for(int i = where ; i < cnt +1 ; i++){
                int first =  (i * 1000) + 1;
                int second =  ((i + 1) *1000) ;
                URL url1 = new URL("http://openapi.foodsafetykorea.go.kr/api/2520cc2dbe504a248f84/"+ type + "/json/"+ first +"/"+ second);
                connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestProperty("Accept-Charset", "UTF-8");
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                int responseCode1 = connection.getResponseCode();
                if(responseCode1 == 200){
                    input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    resultMap = gson.fromJson(input, Map.class);
                    LinkedTreeMap testList = (LinkedTreeMap) resultMap.get(type);
                    row = (ArrayList) testList.get("row");
                    rowSize = row.size();
                    if( rowSize != 0 ){
                        for(int j = 0 ; j < rowSize ; j ++){
                            int cnt1 = first + j ;
                            resultMap2 = (Map<String, Object>) row.get(j);
                            map.put("keyword",resultMap2.get(param));
                            setalarmDAO.insertKeyword_Master(map);
                            //System.out.println("진행중인 것 " + type + "/" + param + " : " + cnt1);
                        }
                    }
                    else {
                        System.out.println("진행중인 것 " + type + "/" + param );
                        System.out.println("실패 : " + i);
                    }

                }
                setalarmDAO.deleteKeyword_Master();
                System.out.println("토탈 : " +  totalCnt + " 횟수 : " + i + "rowSize : " + rowSize);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("끝낫 것 " + type + "/" + param + " : " + "끝");
        //return resultMap1;
/*        진행중인 것 I0300/PRDLST_NM/110 : 112000
        토탈 : 1653806 횟수 : 111
        진행중인 것 I0320/PDT_NM/102 : 104000
        토탈 : 911850 횟수 : 103
        진행중인 것 I0310/PRDLST_NM/53 : 55000
        토탈 : 161819 횟수 : 54
        토탈 : 1653806 횟수 : 556rowSize : 1000
토탈 : 911850 횟수 : 560rowSize : 1000*/
    }
}
