package blog.coupang;

import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public final class CoupangGetAPI {
    private final static String REQUEST_METHOD = "GET";
    private final static String DOMAIN = "https://api-gateway.coupang.com";
    private final static String URL = "/v2/providers/affiliate_open_api/apis/openapi/v1/products/bestcategories/1001";
    // Replace with your own ACCESS_KEY and SECRET_KEY
    private final static String ACCESS_KEY = "3d3ead5f-bc75-4c91-809d-bf633f1fe687";
    private final static String SECRET_KEY = "2ed0c3999f0d33fe610a340a10adc00c09b7a273";

    private final static String REQUEST_JSON = "{\"coupangUrls\": [\"https://www.coupang.com/vp/products/335811322?itemId=1071522108&vendorItemId=5561687320&sourceType=CAMPAIGN&campaignId=82&categoryId=115573&isAddedCart=\"]}";

    public static void main(String[] args) throws IOException {
        // Generate HMAC string
        String authorization = HmacGenerator.generate(REQUEST_METHOD, URL, SECRET_KEY, ACCESS_KEY);

        // Send request
        StringEntity entity = new StringEntity("", "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");

        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .get(URL).setEntity(entity)
                .addHeader("Authorization", authorization)
                .build();

        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

        // verify
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));
    }
}