package blog.jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
    //Elements elements2 = document2.select("div.main_pack div.section_head h2"); // 블로그 몇번째 있는지 search

    @GetMapping("/blogPostStat/{Keyword}")
    public Map<String, Integer> blogPostStat(@PathVariable("Keyword") String keyword){
        long start = System.currentTimeMillis();
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

            long start3 = System.currentTimeMillis();

            naverBlogDocument = Jsoup.connect(naverBlogURL).get();
            naverMainDocument = Jsoup.connect(naverMainURL).get();
/*            Elements elements1 = naverBlogDocument.select("body.tabsch.tabsch_blog div#wrap div#container div#content.pack_group " +
                    "div#main_pack.main_pack div.blog.section._blogBase._prs_blg ul#elThumbnailResultArea dl dd.txt_block span.inline a.url"); // 블로그 url
            Elements elements2 = naverBlogDocument.select("body.tabsch.tabsch_blog div#wrap div#container div#content.pack_group " +
                    "div#main_pack.main_pack div.blog.section._blogBase._prs_blg div.section_head span.title_num");// 블로그 개수
            Elements elements3 = naverMainDocument.select("div#wrap div#container div#content.pack_group div#main_pack.main_pack div.section_head h2"); // 블로그 몇번째 있는지 search*/
            Elements elements1 = naverBlogDocument.select("ul#elThumbnailResultArea a.url"); // 블로그 url
            Elements elements2 = naverBlogDocument.select("div#main_pack.main_pack span.title_num");// 블로그 개수
            Elements elements3 = naverMainDocument.select("div#main_pack.main_pack div.section_head h2"); // 블로그 몇번째 있는지 search
            long end3 = System.currentTimeMillis();
            long used3 = end3 - start3;
            System.out.println("element파싱 속도 : " + used3 + " 밀리초");
            int element1Size = elements1.size();
            int element2Size = elements2.size();
            int element3Size = elements3.size();

            if(element3Size != 0){
                long start2 = System.currentTimeMillis();
                for(int b = 0 ; b < element3Size ; b++){
                    String find = String.valueOf(elements3.get(b).childNode(0));
                    if(find.equals("블로그")){
                        whereBlog = b + 1;
                        break;
                    }
                }
                long end2 = System.currentTimeMillis();
                long used2 = end2 - start2;
                System.out.println("whereblog 속도 : " + used2 + " 밀리초");
            }


            if(element1Size != 0 && element2Size != 0){
                long start1 = System.currentTimeMillis();
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
                long end1 = System.currentTimeMillis();
                long used1 = end1 - start1;
                System.out.println("url 속도 : " + used1 + " 밀리초");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.put("Naver", naverCnt);
        result.put("Tstory", tstoryCnt);
        result.put("Else", elseCnt);
        result.put("totalPost", blogTotalPost);
        result.put("whereBlog", whereBlog);

        long end = System.currentTimeMillis();
        long used = end - start;
        System.out.println("blogPostStat: " + used + " 밀리초");
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
                Elements naverElements = naverDocument.select("ul._related_keyword_ul li a"); // 블로그 url
                Elements daumElements = daumDocument.select("div#netizen_lists_top.list_keyword.type2 span.wsn a.keyword");
                int naverSize = naverElements.size();
                int daumSize = daumElements.size();
                if (naverSize != 0) {
                    for (int a = 0; a < naverSize; a++) {
                        String keyword = String.valueOf(naverElements.get(a).childNode(0));
                        map.put("keyword",keyword);
                        setalarmDAO.insertKeyword_Relate(map);
                        //System.out.println(str);
                    }
                }
                if (daumSize != 0) {
                    for (int j = 0; j < daumSize; j++) {
                        String str1 = String.valueOf(daumElements.get(j).childNode(0));
                        System.out.println(str1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setalarmDAO.deleteKeyword_Relete();
        setalarmDAO.updateUsed_Master();
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
                String naverTrandURL = "https://datalab.naver.com/keyword/realtimeList.naver?datetime=2019-05-07T" +timeArr[j]+ "%3A00%3A00";
                document = Jsoup.connect(naverTrandURL).get();
                Elements elements = document.select("div.keyword_rank span.title"); // 급상승 검색어
                int size = elements.size();
                for(int i = 0 ; i < size; i++){
                    Element element = elements.get(i);
                    String title = String.valueOf(element.childNode(0));
                    String title2 = title.replaceAll(" ", "");
                    //relKwdStat.getRelateKeyword(title2);
                    map.put("keyword",title);
                    setalarmDAO.insertKeyword_Master(map);
                    System.out.println(title);
                    //Thread.sleep(200);
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

