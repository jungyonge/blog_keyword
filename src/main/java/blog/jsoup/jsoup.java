package blog.jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import blog.rest.RelKwdStat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// 네이버 실시간 급상승 검색어 가져오기
@RestController
public class jsoup {
    SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

@GetMapping("/blogPostStat/{Keyword}")
    public Map<String, Integer> blogPostStat(@PathVariable("Keyword") String keyword){
        Document document = null;
        Map<String, Integer> result = new HashMap<String, Integer>();

        int blogTotalPost = 0;
        int naverCnt = 0;
        int tstoryCnt = 0;
        int elseCnt = 0;
        String naverBlogURL = "https://search.naver.com/search.naver?where=post&sm=tab_jum&query=" + keyword;
        try {

            document = Jsoup.connect(naverBlogURL).get();
            Elements elements1 = document.select("a.url"); // 블로그 url
            Elements elements2 = document.select("span.title_num");// 블로그 개수
            if(elements1.size() != 0 && elements2.size() != 0){
                String str = String.valueOf(elements2.get(0).childNode(0));
                blogTotalPost = Integer.parseInt(str.substring(6).replaceAll("[^0-9]", ""));;
                System.out.println(blogTotalPost);
                for(int i=0;i<elements1.size()-1;i++){
                    Element element = elements1.get(i);
                    String blogURL = String.valueOf(element.childNode(0));
                    if (blogURL.contains("naver")) {
                        naverCnt++;
                    } else if (blogURL.contains("tistory")) {
                        tstoryCnt++;
                    } else {
                        elseCnt++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.put("Naver",naverCnt);
        result.put("Tstory",tstoryCnt);
        result.put("Else",elseCnt);
        result.put("totalPost", blogTotalPost);
        return result;
    }

    @GetMapping("/blogRelateKwd/{Keyword}")
    public void blogRelateKwd(@PathVariable("Keyword") String keyword){
        Document naverDocument = null;
        Document daumDocument = null;
        String naverBlogURL = "https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=" + keyword;
        String daumBlogURL = "https://search.daum.net/search?w=tot&DA=YZR&t__nil_searchbox=btn&sug=&sugo=&q=" + keyword;

        try {
            naverDocument = Jsoup.connect(naverBlogURL).get();
            daumDocument = Jsoup.connect(daumBlogURL).get();
            Elements naverElements = naverDocument.select("ul._related_keyword_ul li a"); // 블로그 url
            Elements daumElements = daumDocument.select("div#netizen_lists_top.list_keyword.type2 span.wsn a.keyword");
            if(naverElements.size() != 0 && naverElements.size() != 0){
                for(int i = 0 ; i < naverElements.size(); i++){
                    String str = String.valueOf(naverElements.get(i).childNode(0));
                    //System.out.println(str);
                }
            }

            if(daumElements.size() != 0 && daumElements.size() != 0){
                for(int i = 0 ; i < daumElements.size(); i++){
                    String str1 = String.valueOf(daumElements.get(i).childNode(0));
                    System.out.println(str1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @GetMapping("/naverTrand")
    public void naverTrand(){
        RelKwdStat relKwdStat = new RelKwdStat();
        Document document = null;
        Map<String, Object> map = new HashMap<String, Object>();
        String[] timeArr = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
        int cnt = 0;

        try {
            for(int j = 0 ; j < 24 ; j++){
                String naverTrandURL = "https://datalab.naver.com/keyword/realtimeList.naver?datetime=2019-05-01T" +timeArr[j]+ "%3A00%3A00";
                document = Jsoup.connect(naverTrandURL).get();
                Elements elements = document.select("div.keyword_rank span.title"); // 급상승 검색어
                for(int i = 0 ; i < elements.size()-1; i++){
                    Element element = elements.get(i);
                    String title = String.valueOf(element.childNode(0));
                    String title2 = title.replaceAll(" ", "");
                    relKwdStat.getRelateKeyword(title2);
                    map.put("keyword",title);
                    setalarmDAO.insertKeyword_Master(map);
                    System.out.println(title);
                    Thread.sleep(200);
                    cnt++;
                }
            }
            System.out.println(cnt);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

