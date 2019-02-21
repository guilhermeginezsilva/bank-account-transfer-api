package br.com.ginezgit;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.testing.http.MockHttpTransport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class ComponentTest {

    @Test
    public void applicationServerOnlineOnLocalhostAnd8080Test() throws Exception {
        HashMap args = new HashMap();

        Application application = new Application(args);
        application.startApplication();

        HttpTransport transport = new MockHttpTransport();
        HttpRequest request = transport.createRequestFactory().buildGetRequest(new GenericUrl("http://localhost:8080/bankapi/account"));
        HttpResponse response = request.execute();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        application.stopContainer();
    }

    @Test
    public void transferTest() throws Exception {
        HashMap args = new HashMap();

        Application application = new Application(args);
        application.startApplication();

        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory httpRequestFactory = httpTransport.createRequestFactory();

        Map<Object, Object> params = new HashMap<>();
        params.put("fromAccountId", Application.demoAccount1.getId());
        params.put("toAccountId", Application.demoAccount2.getId());
        params.put("amount", "1000");

        HttpRequest request = httpRequestFactory.buildPostRequest(new GenericUrl("http://localhost:8080/bankapi/account/transfer"), new UrlEncodedContent(params));
        HttpHeaders headers = new HttpHeaders();
        headers.put("X-HTTP-Method-Override", "GET");
        request.setHeaders(headers);
        HttpResponse response = request.execute();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        Assert.assertEquals(0, new BigDecimal(99000).compareTo(Application.demoAccount1.getBalance()));
        Assert.assertEquals(0, new BigDecimal(51000).compareTo(Application.demoAccount2.getBalance()));
        application.stopContainer();
    }

}
