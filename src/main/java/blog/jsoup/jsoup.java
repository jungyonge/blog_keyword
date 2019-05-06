package blog.jsoup;

import java.io.IOException;

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
/*    public static void main(String[] args) {
        Document document = null;
        Document document2 = null;
        try {
            String url = "https://datalab.naver.com/keyword/realtimeList.naver?datetime=2019-05-01T23%3A21%3A00";
            String url2 = "https://search.naver.com/search.naver?where=post&sm=tab_jum&query=신한은행+고객센터";
            document = Jsoup.connect(url).get();
            Elements elements = document.select("div.keyword_rank span.title"); // 급상승 검색어
            document2 = Jsoup.connect(url2).get();
            //Elements elements2 = document2.select("a.url"); // 블로그 url
            //Elements elements2 = document2.select("div.main_pack div.section_head h2"); // 블로그 몇번째 있는지 search
            Elements elements2 = document2.select("span.title_num");
            String title3 = String.valueOf(elements2.get(0).childNode(0));
            int test = Integer.parseInt(title3.substring(6, title3.length()).replaceAll("[^0-9]", ""));;
            for(int i=0;i<elements2.size()-1;i++){
                Element element = elements.get(i);
                String title = String.valueOf(element.childNode(0));
                Element element2 = elements2.get(i);
                String title2 = String.valueOf(element2.childNode(0));
                System.out.println(title2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
@GetMapping("/blogparing/{Keyword}")
    public void blogparsing(@PathVariable("Keyword") String keyword){
        Document document = null;
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
                    } else if (blogURL.contains("tstory")) {
                        tstoryCnt++;
                    } else {
                        elseCnt++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    System.out.println(blogTotalPost);
    System.out.println(naverCnt +"  "+ tstoryCnt +"  "+ elseCnt);
    }

}

