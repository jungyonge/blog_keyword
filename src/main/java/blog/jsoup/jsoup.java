package blog.jsoup;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// 네이버 실시간 급상승 검색어 가져오기
public class jsoup {
    public static void main(String[] args) {
        Document document = null;
        Document document2 = null;
        try {
            String url = "https://datalab.naver.com/keyword/realtimeList.naver?datetime=2019-05-01T23%3A21%3A00";
            String url2 = "https://search.naver.com/search.naver?where=post&sm=tab_jum&query=2019+%EB%B0%B1%EC%83%81%EC%98%88%EC%88%A0%EB%8C%80%EC%83%81";
            document = Jsoup.connect(url).get();
            Elements elements = document.select("div.keyword_rank span.title");
            document2 = Jsoup.connect(url2).get();
            Elements elements2 = document2.select("a.url");
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
    }
}

