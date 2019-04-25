package blog.rest;

import blog.model.RelKwdStatModel;
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

    @GetMapping("/RelKwdStat/{Keyword}")
    public  RelKwdStatModel getRelKwdStat(@PathVariable("Keyword") String keyword){

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

    public RelKwdStatModel httpUrl(String hmacSHA256, String timestamp, String requestURL, String apikey, long customerId, String query) {
        BlogStat blogStat = new BlogStat();
        Gson gson = new Gson();
        HttpURLConnection connection = null;
        BufferedReader input = null;
        System.out.println(hmacSHA256 + "  " + timestamp );
        List<RelKwdStatModel> relKwdData = null;
        RelKwdStatModel relKwd = null;
        RelKwdStatModel relKwd1 = null;

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

            //result = gson.fromJson(input, Map.class);
            relKwd = gson.fromJson(input, RelKwdStatModel.class);
            int size = relKwd.getKeywordList().size();
            for(int i = 0; i < size; i++){
                String key = relKwd.getKeywordList().get(i).getRelKeyword();
                System.out.println(key);
                Map<String,Integer> res = blogStat.blogparser(key);
                if(res.get("Naver") <= 5) {
                    relKwd.getKeywordList().get(i).setNaverCnt(res.get("Naver"));
                    relKwd.getKeywordList().get(i).setTstoryCnt(res.get("Tstory"));
                    relKwd.getKeywordList().get(i).setElseCnt(res.get("Else"));
                    relKwd1 = RelKwdStatModel.builder()
                            .keywordList(Collections.singletonList(relKwd.getKeywordList().get(i))).build();
                }
                Thread.sleep(300);
            }

           System.out.println(relKwdData);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return relKwd1;
    }


    }
