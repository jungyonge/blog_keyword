package blog.rest;

import blog.model.BlogModel;
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
public class BlogStat {

    private String clientId = "C1YQC3o_0RJDmqnYEioo";//애플리케이션 클라이언트 아이디값";
    private String clientSecret = "mSkCZUBrym";//애플리케이션 클라이언트 시크릿값";
    private Gson gson = new Gson();

    @GetMapping("/blog/{Keyword}")
    public Map<String, Integer> blogparser(@PathVariable("Keyword") String keyword){

        Map<String, Integer> result = new HashMap<String, Integer>();
        java.net.HttpURLConnection connection = null;

        try {
            String text = URLEncoder.encode(keyword, "UTF-8");
            URL url = new URL("https://openapi.naver.com/v1/search/blog?query="+ text);
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
                System.out.println(br);
            }
            int naverCnt = 0;
            int tistoryCnt = 0;
            int elseCnt = 0;

            BlogModel bloginfo = gson.fromJson(br, BlogModel.class);

            if(bloginfo.getItems() != null){
                for(int i = 0; i <bloginfo.getItems().size() ; i++) {
                    String url2 = bloginfo.getItems().get(i).link;
                    if (url2.contains("naver")) {
                        naverCnt++;
                    } else if (url2.contains("tistory")) {
                        tistoryCnt++;
                    } else {
                        elseCnt++;
                    }
                }
            }else{
                naverCnt = 100;
            }
            result.put("Naver",naverCnt);
            result.put("Tistory",tistoryCnt);
            result.put("Else",elseCnt);
            result.put("totalPost", Integer.valueOf(bloginfo.getTotal()));
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }
}
