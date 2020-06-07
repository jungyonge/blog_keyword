package blog.coupang;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public final class CoupangAPI {
    private final static String REQUEST_METHOD = "POST";
    private final static String DOMAIN = "https://api-gateway.coupang.com";
    private final static String URL = "/v2/providers/affiliate_open_api/apis/openapi/v1/deeplink";
    // Replace with your own ACCESS_KEY and SECRET_KEY
    private final static String ACCESS_KEY = "-9b08---";
    private final static String SECRET_KEY = "";


    public String getCoupangUrl (String url) throws IOException {
        String REQUEST_JSON = "{\"coupangUrls\": [\" "+ url + "\"]}";

        // Generate HMAC string
        String coupangUrl = "";
        String authorization = HmacGenerator.generate(REQUEST_METHOD, URL, SECRET_KEY, ACCESS_KEY);

        // Send request
        StringEntity entity = new StringEntity(REQUEST_JSON, "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");

        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .post(URL).setEntity(entity)
                .addHeader("Authorization", authorization)
                .build();

        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

        // verify
        String tempUrl = EntityUtils.toString(httpResponse.getEntity());

        JsonParser jsonParser = new JsonParser();

        JsonObject jsonObject = (JsonObject) jsonParser.parse(tempUrl);
        JsonArray jsonObject2 =   (JsonArray) jsonParser.parse(jsonObject.get("data").toString());
        JsonObject jsonObject3 =   (JsonObject) jsonParser.parse(jsonObject2.get(0).toString());

        System.out.println(jsonObject3.get("shortenUrl"));

        coupangUrl = jsonObject3.get("shortenUrl").toString().replaceAll("\\\"", "");
        return coupangUrl;


    }

    public static void main(String[] args) throws IOException {
     CoupangAPI coupangAPI = new CoupangAPI();
     String coupangUrl = "";
     coupangUrl = coupangAPI.getCoupangUrl("https://www.coupang.com/vp/products/185502198?itemId=530600312&vendorItemId=4381872911&sourceType=CATEGORY&categoryId=186666");
        System.out.println(coupangUrl);
    }
}