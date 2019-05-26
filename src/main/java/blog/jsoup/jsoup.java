package blog.jsoup;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public Map<String, Integer> blogPostStat1(@PathVariable("Keyword") String keyword){
        Document naverBlogDocument = null;
        Document naverPCDocument = null;
        Document naverMobileDocument = null;
        boolean ban = false;
        Map<String, Integer> result = new HashMap<String, Integer>();

        int whereBlog = 0;
        int whereWeb = 0;
        int whereMobileBlog = 0;
        int whereMobileWeb = 0;
        int blogTotalPost = 0;
        int naverCnt = 0;
        int tistoryCnt = 0;
        int elseCnt = 0;

        String naverBlogURL = "https://search.naver.com/search.naver?where=post&sm=tab_jum&query=" + keyword;
        String naverPCURL = "https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=" + keyword;
        String naverMobileURL = "https://m.search.naver.com/search.naver?query=" + keyword;
        try {

            naverBlogDocument = Jsoup.connect(naverBlogURL).get();
            naverPCDocument = Jsoup.connect(naverPCURL).get();
            naverMobileDocument = Jsoup.connect(naverMobileURL).get();

            Elements elements1 = naverBlogDocument.select("ul#elThumbnailResultArea a.url");// 블로그 url
            Elements elements2 = naverBlogDocument.select("div#main_pack.main_pack span.title_num");// 블로그 포스팅 개수
            Elements elements3 = naverPCDocument.select("div#main_pack.main_pack div.section_head h2"); // pc 검색시 블로그 몇번째 있는지 search
            Elements elements4 = naverMobileDocument.select("div#ct a.api_more"); // 모바일 검색시 블로그 몇번째 있는지 search
            int element1Size = elements1.size();
            int element2Size = elements2.size();
            int element3Size = elements3.size();
            int element4Size = elements4.size();

            if(element3Size != 0){
                for(int b = 0 ; b < element3Size ; b++){
                    String find = String.valueOf(elements3.get(b).childNode(0));
                    if(find.equals("블로그")){
                        whereBlog = b + 1;
                    }
                    if(find.equals("웹사이트")){
                        whereWeb = b + 1;
                    }
                }
            }

            if(element4Size != 0){
                for(int b = 0 ; b < element4Size ; b++){
                    String find = String.valueOf(elements4.get(b).childNode(0));
                    if(find.equals("VIEW 더보기")){
                        whereMobileBlog = b + 1;
                    }
                    if(find.equals(" 더보기")){
                        whereMobileWeb = b + 1;
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
                        tistoryCnt++;
                    } else {
                        elseCnt++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.put("Naver", naverCnt);
        result.put("Tistory", tistoryCnt);
        result.put("Else", elseCnt);
        result.put("totalPost", blogTotalPost);
        result.put("whereBlog", whereBlog);
        result.put("whereWeb",whereWeb);
        result.put("whereMobileBlog", whereMobileBlog);
        result.put("whereMobileWeb",whereMobileWeb);
        return result;
    }


    @GetMapping("/blogRelateKeyword")
    public void blogRelateKeyword(){
        Document naverDocument = null;
        Document daumDocument = null;
        boolean ban = false;
        Map<String, Object> map = new HashMap<String, Object>();
        List masterList = null;

        masterList = setalarmDAO.getKeywordMaster();
        int size = masterList.size();
        for(int i = 0 ; i < size ; i++) {
            if(ban){
                System.out.println("벤당했다");
                break;
            }
            HashMap<String, Object> test = (HashMap<String, Object>) masterList.get(i);
            String kwd = String.valueOf(test.get("keyword"));

            String naverBlogURL = "https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=" + kwd;
            String daumBlogURL = "https://search.daum.net/search?w=tot&DA=YZR&t__nil_searchbox=btn&sug=&sugo=&q=" + kwd;
            map.put("keyword",kwd);
            setalarmDAO.insertKeyword_Relate(map);


            try {
                naverDocument = Jsoup.connect(naverBlogURL).get();
                daumDocument = Jsoup.connect(daumBlogURL).get();
                Elements naverElements = naverDocument.select("div#nx_related_keywords.sp_keyword.section ul._related_keyword_ul li a"); // 블로그 url
                Elements daumElements = daumDocument.select("div#netizen_lists_top.list_keyword.type2 span.wsn a.keyword");
                int naverSize = naverElements.size();
                int daumSize = daumElements.size();

                if(size == 0){
                    ban = true;
                    System.out.println("벤당했다");
                    break;
                }
                if (naverSize != 0) {
                    for (int a = 0; a < naverSize; a++) {
                        String naverKeyword = String.valueOf(naverElements.get(a).childNode(0));
                        map.put("keyword",naverKeyword);
                        System.out.println("네이버 : " + naverKeyword);
                        setalarmDAO.insertKeyword_Relate(map);
                    }
                }
                if (daumSize != 0) {
                    for (int j = 0; j < daumSize; j++) {
                        String daumKeyword = String.valueOf(daumElements.get(j).childNode(0));
                        System.out.println("다음 : " + daumKeyword);
                        map.put("keyword",daumKeyword);
                        setalarmDAO.insertKeyword_Relate(map);
                    }
                }
                setalarmDAO.updateUsed_Master(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setalarmDAO.deleteKeyword_Relete();
    }

    // 네이버 실시간 급상승 검색어 가져오기
    @GetMapping("/naverTrand/{date}")
    public void naverTrand(@PathVariable("date") String date){
        Document document = null;
        boolean ban = false;
        Map<String, Object> map = new HashMap<String, Object>();
        String[] timeArr = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
        String[] timeArr1 = {"00","02","04","06","08","10","12","14","16","18","20","22",};
        int timeArrsize = timeArr.length;
        int timeArr1size = timeArr1.length;
        int cnt = 0;

        try {
            for(int a = 0 ; a < 31 ; a++) {
                if(ban){
                    System.out.println("벤당했다");
                    break;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date beginDate = formatter.parse(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(beginDate);
                cal.add(Calendar.DATE, -a);
                String strDate = formatter.format(cal.getTime());
                System.out.println(strDate);
                for (int j = 0; j < timeArr1size; j++) {
                    String naverTrandURL = "https://datalab.naver.com/keyword/realtimeList.naver?datetime=" + strDate + "T" + timeArr1[j] + "%3A00%3A00";
                    document = Jsoup.connect(naverTrandURL).get();
                    Elements elements = document.select("div#content.content div.keyword_rank span.title"); // 급상승 검색어
                    int size = elements.size();
                    if(size == 0){
                        ban = true;
                        System.out.println("벤당했다");
                        break;
                    }
                    for (int i = 0; i < size; i++) {
                        Element element = elements.get(i);
                        String title = String.valueOf(element.childNode(0));
                        map.put("keyword", title);
                        setalarmDAO.insertKeyword_Master(map);
                        System.out.println(title);
                        cnt++;
                    }
                }
            }
            System.out.println(cnt);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setalarmDAO.deleteKeyword_Master();

    }

    // 네이버 실시간 급상승 검색어 가져오기
    @GetMapping("/daumTrand/{date}")
    public void daumTrand(@PathVariable("date") String date){
        Document document = null;
        boolean ban = false;
        Map<String, Object> map = new HashMap<String, Object>();
        String[] strArr = {"http://rank.ezme.net/?mode=google&day=","http://rank.ezme.net/?mode=daum&day=","http://rank.ezme.net/?mode=naver&day="};
        int cnt = 0;

        try {
            for (int b = 0; b < 3; b++) {
                String URL = strArr[b];
                for (int a = 0; a < 365; a++) {
                    if (ban) {
                        System.out.println("벤당했다");
                        break;
                    }
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date beginDate = formatter.parse(date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(beginDate);
                    cal.add(Calendar.DATE, -a);
                    String strDate = formatter.format(cal.getTime());
                    System.out.println(strDate);
                    String naverTrandURL = URL + strDate;
                    document = Jsoup.connect(naverTrandURL).get();
                    Elements elements = document.select("div.mdl-grid span.mdl-chip__text"); // 급상승 검색어
                    int size = elements.size();
                    if (size == 0) {
                        ban = true;
                        System.out.println("벤당했다");
                        break;
                    }
                    for (int i = 0; i < size; i++) {
                        Element element = elements.get(i);
                        if (element.childNodeSize() != 0) {
                            String title = String.valueOf(element.childNode(0));
                            map.put("keyword", title);
                            setalarmDAO.insertKeyword_Master(map);
                            System.out.println(title);
                            cnt++;
                        } else {
                            System.out.println("없음");
                        }

                    }
                }
                System.out.println(cnt);
            }
        }catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setalarmDAO.deleteKeyword_Master();

    }

}

