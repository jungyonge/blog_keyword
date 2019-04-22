package blog.rest;

import blog.model.Blog;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

@RestController
public class blogparser {

    @GetMapping("/blog/{Keyword}")
    public Map<String, Object> blogparser(@PathVariable("Keyword") String keyword){
        String clientId = "C1YQC3o_0RJDmqnYEioo";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "mSkCZUBrym";//애플리케이션 클라이언트 시크릿값";
        Map<String, Object> result = null;
        java.net.HttpURLConnection connection = null;

        Gson gson = new Gson();
        try {
            String text = URLEncoder.encode(keyword, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/Blog?query="+ text; // json 결과
            //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과
            URL url = new URL("https://openapi.naver.com/v1/search/Blog?query="+ text);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Naver-Client-Id", clientId);
            connection.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            result = gson.fromJson(br, Map.class);
            Blog bloginfo = gson.fromJson(br, Blog.class);
            List<Blog> upbit_btcprice = gson.fromJson(br, new TypeToken<List<Blog>>(){}.getType());
            ArrayList resultArr = (ArrayList) result.get("items");
            for(int i = 0; i < resultArr.size(); i++){
                LinkedTreeMap<String,String> map = (LinkedTreeMap<String, String>) resultArr.get(i);
                String url1 = map.get("link");
                System.out.println(map);
            }
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }
}
