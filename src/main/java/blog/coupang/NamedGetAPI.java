package blog.coupang;

        import org.apache.http.entity.StringEntity;
        import org.apache.http.util.EntityUtils;

        import java.io.IOException;

public final class NamedGetAPI {
    private final static String REQUEST_METHOD = "GET";
    private final static String DOMAIN = "https://sports-api.named.com";
    private final static String URL = "/named/v1/sports/baseball/games/?";
    private final static String API_KEY = "1rar2zCZvKjp";
    private final static String API_NAME = "named_score";


    public static void main(String[] args) throws IOException {
        // Generate HMAC string

        // Send request
        StringEntity entity = new StringEntity("", "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        long unixTime = System.currentTimeMillis() / 1000;


        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .get(URL).setEntity(entity)
                .addHeader("accept", "*/*")
                .addHeader("oki-api-key",API_KEY)
                .addHeader("oki-api-name",API_NAME)
                .addHeader("referer","http://sports.named.com/baseball")
                .addHeader("origin","http://sports.named.com")
                .addHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36")
                .addParameter("broadcast","true")
                .addParameter("broadcastLatest","true")
                .addParameter("odds","true")
                .addParameter("scores","true")
                .addParameter("specials","true")
                .addParameter("seasonTeamStat","true")
                .addParameter("startDate","20200226")
                .addParameter("endDate","20200226")
                .addParameter("v", String.valueOf(unixTime))
                .build();


        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

        // verify
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));
    }
}