package blog.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import blog.util.PropertiesLoader;
import blog.util.RestClient;
import blog.util.Signatures;

import java.io.*;
import java.net.*;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@RestController
public class RelKwdStat {

    @GetMapping("/RelKwdStat/{Keyword}")
    public  Map<String, Object> getRelKwdStat(@PathVariable("Keyword") String keyword){

        Map<String, Object> result = null;

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

    public Map<String, Object> httpUrl(String hmacSHA256, String timestamp, String requestURL, String apikey, long customerId, String query) {
        Gson gson = new Gson();
        HttpURLConnection connection = null;
        BufferedReader input = null;
        System.out.println(hmacSHA256 + "  " + timestamp );
        List<blog.model.RelKwdStat.RelKwd> relKwdData = null;
        StringBuffer buffer = new StringBuffer();
        int code = 0;
        InputStream errormsg = null;
        Map<String, Object> result = null;

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
            /*OutputStream os = connection.getOutputStream();
            os.write(body.getBytes());
            os.flush();
            code = connection.getResponseCode();
            errormsg = connection.getErrorStream();*/

            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String test = input.toString();
            result = gson.fromJson(input, Map.class);
            relKwdData = gson.fromJson(input, new TypeToken<List<blog.model.RelKwdStat.RelKwd>>() {}.getType());


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    }
