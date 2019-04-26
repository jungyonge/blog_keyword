package blog.rest;

import blog.model.RelKwdStatModel;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
public class RelKwdStat {
    SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    @GetMapping("/getrelkeyword/{Keyword}")
    public  RelKwdStatModel getRelateKeyword(@PathVariable("Keyword") String keyword){

        RelKwdStatModel result = null;
        try {
            Properties properties = PropertiesLoader.fromResource("sample.properties");
            String baseUrl = properties.getProperty("BASE_URL");
            String apiKey = properties.getProperty("API_KEY");
            String secretKey = properties.getProperty("SECRET_KEY");
            long customerId = Long.parseLong(properties.getProperty("CUSTOMER_ID"));
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String hmacSHA256 = Signatures.of(timestamp,"GET","/keywordstool",secretKey);
            String charset = "UTF-8";
            String kwd = keyword;
            String showDetail = "1";
            String query = String.format("hintKeywords=%s&showDetail=%s", URLEncoder.encode(kwd,charset),URLEncoder.encode(showDetail,charset));

            result = httpUrl(hmacSHA256,timestamp,baseUrl+"/keywordstool",apiKey,customerId,query);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return result;
    }


    @GetMapping("/getrelkeywordByTable")
    public  RelKwdStatModel getRelateKeywordByTable(){

        RelKwdStatModel result = null;
        try {
            Properties properties = PropertiesLoader.fromResource("sample.properties");
            String baseUrl = properties.getProperty("BASE_URL");
            String apiKey = properties.getProperty("API_KEY");
            String secretKey = properties.getProperty("SECRET_KEY");
            long customerId = Long.parseLong(properties.getProperty("CUSTOMER_ID"));
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String hmacSHA256 = Signatures.of(timestamp,"GET","/keywordstool",secretKey);

            result = httpUrl2(hmacSHA256,timestamp,baseUrl+"/keywordstool",apiKey,customerId);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return result;
    }

    public RelKwdStatModel httpUrl(String hmacSHA256, String timestamp, String requestURL, String apikey, long customerId, String query) {
        BlogStat blogStat = new BlogStat();
        Gson gson = new Gson();
        HttpURLConnection connection = null;
        BufferedReader input = null;
        System.out.println(hmacSHA256 + "  " + timestamp );
        List<RelKwdStatModel> relKwdData = null;
        RelKwdStatModel relKwd = null;
        RelKwdStatModel relKwd1 = null;
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
            relKwd = gson.fromJson(input, RelKwdStatModel.class);
            int size = relKwd.getKeywordList().size();
                for(int i = 0; i < size; i++){
                    String key = relKwd.getKeywordList().get(i).getRelKeyword();
                    map.put("keyword",key);
                    setalarmDAO.insertKeyword_Relate(map);
                    System.out.println(key);
                    //Thread.sleep(300);
                }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return relKwd1;
    }

    public RelKwdStatModel httpUrl2(String hmacSHA256, String timestamp, String requestURL, String apikey, long customerId) {
        BlogStat blogStat = new BlogStat();
        Gson gson = new Gson();
        HttpURLConnection connection = null;
        BufferedReader input = null;
        System.out.println(hmacSHA256 + "  " + timestamp );
        List<RelKwdStatModel> relKwdData = null;
        RelKwdStatModel relKwd = null;
        RelKwdStatModel relKwd1 = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List masterList = null;
        String charset = "UTF-8";
        String showDetail = "1";
        masterList = setalarmDAO.getKeywordMaste();

        for(int i = 0 ; i < masterList.size(); i++){
            HashMap<String,Object> test = (HashMap<String, Object>) masterList.get(i);
            String kwd = String.valueOf(test.get("keyword"));

            try {
                //Private API Header 세팅
                String query = String.format("hintKeywords=%s&showDetail=%s", URLEncoder.encode(kwd,charset),URLEncoder.encode(showDetail,charset));
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
                relKwd = gson.fromJson(input, RelKwdStatModel.class);
                int size = relKwd.getKeywordList().size();
                for(int i2 = 0; i2 < size; i2++){
                    String key = relKwd.getKeywordList().get(i2).getRelKeyword();
                    map.put("keyword",key);
                    setalarmDAO.insertKeyword_Relate(map);
                    System.out.println(key);
                    //Thread.sleep(300);
                }

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return relKwd1;
    }


    }
