package blog.jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class jsoup {
    private SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    @GetMapping("/blogPostStat/{Keyword}")
    public Map<String, Integer> blogPostStat(@PathVariable("Keyword") String keyword){
        Document naverBlogDocument = null;
        Document naverMainDocument = null;
        Map<String, Integer> result = new HashMap<String, Integer>();

        int whereBlog = 0;
        int blogTotalPost = 0;
        int naverCnt = 0;
        int tstoryCnt = 0;
        int elseCnt = 0;

        String naverBlogURL = "https://search.naver.com/search.naver?where=post&sm=tab_jum&query=" + keyword;
        String naverMainURL = "https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=" + keyword;
        try {

            naverBlogDocument = Jsoup.connect(naverBlogURL).get();
            naverMainDocument = Jsoup.connect(naverMainURL).get();

            Elements elements1 = naverBlogDocument.select("ul#elThumbnailResultArea a.url");// 블로그 url
            Elements elements2 = naverBlogDocument.select("div#main_pack.main_pack span.title_num");// 블로그 포스팅 개수
            Elements elements3 = naverMainDocument.select("div#main_pack.main_pack div.section_head h2"); // 검색시 블로그 몇번째 있는지 search
            int element1Size = elements1.size();
            int element2Size = elements2.size();
            int element3Size = elements3.size();

            if(element3Size != 0){
                for(int b = 0 ; b < element3Size ; b++){
                    String find = String.valueOf(elements3.get(b).childNode(0));
                    if(find.equals("블로그")){
                        whereBlog = b + 1;
                        break;
                    }
                }
            }

            if(element1Size != 0 && element2Size != 0){
                String str = String.valueOf(elements2.get(0).childNode(0));
                blogTotalPost = Integer.parseInt(str.substring(6).replaceAll("[^0-9]", ""));;
                for(int i = 0 ; i < element1Size ;i++){
                    Element element = elements1.get(i);
                    String blogURL = String.valueOf(element.childNode(0));
                    if (blogURL.indexOf("naver") > -1 || blogURL.indexOf("blog.me") > -1)  {
                        naverCnt++;
                    } else if (blogURL.indexOf("tistory") > -1) {
                        tstoryCnt++;
                    } else {
                        elseCnt++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.put("Naver", naverCnt);
        result.put("Tstory", tstoryCnt);
        result.put("Else", elseCnt);
        result.put("totalPost", blogTotalPost);
        result.put("whereBlog", whereBlog);
        return result;
    }

    @GetMapping("/blogRelateKwd")
    public void blogRelateKwd(){
        Document naverDocument = null;
        Document daumDocument = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List masterList = null;

        masterList = setalarmDAO.getKeywordMaster();
        int size = masterList.size();
        for(int i = 0 ; i < size ; i++) {
            HashMap<String, Object> test = (HashMap<String, Object>) masterList.get(i);
            String kwd = String.valueOf(test.get("keyword"));

            String naverBlogURL = "https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=" + kwd;
            String daumBlogURL = "https://search.daum.net/search?w=tot&DA=YZR&t__nil_searchbox=btn&sug=&sugo=&q=" + kwd;
            map.put("keyword",kwd);
            setalarmDAO.insertKeyword_Relate(map);

            try {
                naverDocument = Jsoup.connect(naverBlogURL).get();
                daumDocument = Jsoup.connect(daumBlogURL).get();
                Elements naverElements = naverDocument.select("div#nx_related_keywords.sp_keyword.section a"); // 블로그 url
                Elements daumElements = daumDocument.select("div#netizen_lists_top.list_keyword.type2 span.wsn a.keyword");
                int naverSize = naverElements.size();
                int daumSize = daumElements.size();
                if (naverSize != 0) {
                    for (int a = 0; a < naverSize; a++) {
                        String naverKeyword = String.valueOf(naverElements.get(a).childNode(0));
                        map.put("keyword",naverKeyword);
                        setalarmDAO.insertKeyword_Relate(map);
                    }
                }
/*                if (daumSize != 0) {
                    for (int j = 0; j < daumSize; j++) {
                        String doumKeyword = String.valueOf(daumElements.get(j).childNode(0));
                        map.put("keyword",doumKeyword);
                        setalarmDAO.insertKeyword_Relate(map);
                    }
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setalarmDAO.deleteKeyword_Relete();
        setalarmDAO.updateUsed_Master();
    }

    // 네이버 실시간 급상승 검색어 가져오기
    @GetMapping("/naverTrand")
    public void naverTrand(){
        Document document = null;
        Map<String, Object> map = new HashMap<String, Object>();
        String[] timeArr = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
        int cnt = 0;

        try {
            for(int j = 0 ; j < 24 ; j++){
                String naverTrandURL = "https://datalab.naver.com/keyword/realtimeList.naver?datetime=2019-05-07T" +timeArr[j]+ "%3A00%3A00";
                document = Jsoup.connect(naverTrandURL).get();
                Elements elements = document.select("div#content.content div.keyword_rank span.title"); // 급상승 검색어
                int size = elements.size();
                for(int i = 0 ; i < size; i++){
                    Element element = elements.get(i);
                    String title = String.valueOf(element.childNode(0));
                    map.put("keyword",title);
                    setalarmDAO.insertKeyword_Master(map);
                    System.out.println(title);
                    cnt++;
                }
            }
            System.out.println(cnt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setalarmDAO.deleteKeyword_Master();

    }

}

