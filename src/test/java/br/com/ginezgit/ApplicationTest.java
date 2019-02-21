package br.com.ginezgit;

import br.com.ginezgit.controller.exception.InvalidInputParameterValueException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.testing.http.MockHttpTransport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Application.class)
public class ApplicationTest {

    @Test
    public void ApplicationNoInputArgsTest() throws Exception {
        HashMap<String,Object> map = new HashMap<String,Object>();
        Application application = PowerMockito.spy(new Application(map));

        PowerMockito.doNothing().when(application, method(Application.class, "startContainer", int.class, String.class)).withArguments(anyInt(), anyString());
        application.startApplication();
        Object[] expected = {8080, "localhost"};
        PowerMockito.verifyPrivate(application).invoke("startContainer", expected);
    }

    @Test
    public void ApplicationWithHostInputArgsTest() throws Exception {
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("host", "127.0.0.1");
        Application application = PowerMockito.spy(new Application(map));

        PowerMockito.doNothing().when(application, method(Application.class, "startContainer", int.class, String.class)).withArguments(anyInt(), anyString());
        application.startApplication();
        Object[] expected = {8080, "127.0.0.1"};
        PowerMockito.verifyPrivate(application).invoke("startContainer", expected);
    }

    @Test
    public void ApplicationWithPortInputArgsTest() throws Exception {
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("httpPort", 90);
        Application application = PowerMockito.spy(new Application(map));

        PowerMockito.doNothing().when(application, method(Application.class, "startContainer", int.class, String.class)).withArguments(anyInt(), anyString());
        application.startApplication();
        Object[] expected = {90, "localhost"};
        PowerMockito.verifyPrivate(application).invoke("startContainer", expected);
    }

    @Test
    public void ApplicationWithHostAndPortInputArgsTest() throws Exception {
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("host", "127.0.0.1");
        map.put("httpPort", 90);
        Application application = PowerMockito.spy(new Application(map));

        PowerMockito.doNothing().when(application, method(Application.class, "startContainer", int.class, String.class)).withArguments(anyInt(), anyString());
        application.startApplication();
        Object[] expected = {90, "127.0.0.1"};
        PowerMockito.verifyPrivate(application).invoke("startContainer", expected);
    }

    @Test
    public void ApplicationWithUnknownInputArgsTest() throws Exception {
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("adfa", "fdsafsa");
        Application application = PowerMockito.spy(new Application(map));

        PowerMockito.doNothing().when(application, method(Application.class, "startContainer", int.class, String.class)).withArguments(anyInt(), anyString());
        application.startApplication();
        Object[] expected = {8080, "localhost"};
        PowerMockito.verifyPrivate(application).invoke("startContainer", expected);
    }

    @Test
    public void ApplicationWithHostPortAndUnknownInputArgsTest() throws Exception {
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("host", "127.0.0.1");
        map.put("httpPort", 90);
        map.put("adfa", "fdsafsa");
        Application application = PowerMockito.spy(new Application(map));

        PowerMockito.doNothing().when(application, method(Application.class, "startContainer", int.class, String.class)).withArguments(anyInt(), anyString());
        application.startApplication();
        Object[] expected = {90, "127.0.0.1"};
        PowerMockito.verifyPrivate(application).invoke("startContainer", expected);
    }

    @Test(expected = InvalidInputParameterValueException.class)
    public void ApplicationWithInvalidPortInputArgsTest() throws Exception {
        String[] args = {"httpPort=teste"};
        Application.main(args);
    }

}
