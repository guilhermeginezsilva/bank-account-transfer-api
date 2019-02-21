package br.com.ginezgit;

import br.com.ginezgit.controller.exception.InvalidInputParameterValueException;
import br.com.ginezgit.dao.impl.AccountDAO;
import br.com.ginezgit.dao.impl.CustomerDAO;
import br.com.ginezgit.model.Account;
import br.com.ginezgit.model.Customer;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.apache.log4j.Logger;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.ServletException;
import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;

import static io.undertow.servlet.Servlets.servlet;

public class Application {

    static Logger log = Logger.getLogger(CustomerDAO.class.getName());

    private static final int DEFAULT_SERVER_PORT = 8080;
    private static final String DEFAULT_SERVER_HOST = "localhost";

    private static final String PORT_PARAMETER = "httpPort";
    private static final String HOST_PARAMETER = "host";

    private static final String API_NAME = "bankapi";
    private static final String API_PATH = "/bankapi";

    private static Undertow server;

    private HashMap<String, Object> parameters;

    public Application(HashMap<String, Object> parameters) {
        this.parameters = parameters;
    }

    public static Customer demoCustomer1 = new Customer("ddcdfb95-2a6e-4156-b295-f01516cd3b5c", "Customer 1");
    public static Customer demoCustomer2 = new Customer("7d788634-1cbf-4286-ad8f-60289621341c", "Customer 2");
    public static Account demoAccount1 = new Account("7e75dff6-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(100000), System.currentTimeMillis(), demoCustomer1);
    public static Account demoAccount2 = new Account("876822b6-d676-432e-9aab-721c6eeb9f3c", new BigDecimal(50000), System.currentTimeMillis(), demoCustomer2);

    public static void main(String[] args) {
        try {
            HashMap<String, Object> parameters = parseArgs(args);
            Application application = new Application(parameters);
            application.startApplication();
        } catch(Exception e) {
            System.out.println("An error has ocurred, check application's log for more information");
            log.error(e);
            System.exit(-1);
        }
    }

    public void startApplication() throws ServletException {
        log.info("Starting application");

        int serverListenerPort = this.parameters.containsKey(PORT_PARAMETER) ? Integer.parseInt(this.parameters.get(PORT_PARAMETER).toString()) : DEFAULT_SERVER_PORT;
        String host = this.parameters.containsKey(HOST_PARAMETER) ? (String) this.parameters.get(HOST_PARAMETER) : DEFAULT_SERVER_HOST;

        this.startContainer(serverListenerPort, host);
    }


    private void startContainer(int port, String host) throws ServletException {
        prepareData();

        DeploymentInfo servletBuilder = Servlets.deployment();
        servletBuilder
                .setClassLoader(Application.class.getClassLoader())
                .setContextPath(API_PATH)
                .setDeploymentName(API_NAME + ".war")
                .addServlets(servlet("jerseyServlet", ServletContainer.class)
                        .setLoadOnStartup(1)
                        .addInitParam("javax.ws.rs.Application", JerseyApp.class.getName())
                        .addMapping("/*"));

        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();

        PathHandler path = Handlers.path(Handlers.redirect(API_PATH))
                .addPrefixPath(API_PATH, manager.start());

        server = Undertow
                .builder()
                .addHttpListener(port, host)
                .setHandler(new BlockingHandler(path))
                .build();

        log.info("Starting server on: http://" + host+":"+port+servletBuilder.getContextPath());
        server.start();
    }

    private void prepareData() {
        log.debug("Inserting demo data to database");
        CustomerDAO customerDao = CustomerDAO.getInstance();
        customerDao.insert(demoCustomer1.getId(), demoCustomer1);
        log.debug("Inserting demo data: Customer 1:ddcdfb95-2a6e-4156-b295-f01516cd3b5c");
        customerDao.insert(demoCustomer2.getId(), demoCustomer2);
        log.debug("Inserting demo data: Customer 2:7d788634-1cbf-4286-ad8f-60289621341c");

        AccountDAO accountDao = AccountDAO.getInstance();
        accountDao.insert(demoAccount1.getId(), demoAccount1);
        log.debug("Inserting demo data: Customer 1 Account:7e75dff6-05b9-4cf6-aaa1-701007d6c09c");
        accountDao.insert(demoAccount2.getId(), demoAccount2);
        log.debug("Inserting demo data: Customer 2 Account:876822b6-d676-432e-9aab-721c6eeb9f3c");
    }

    private static HashMap<String, Object> parseArgs(String[] args) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.contains("=")) {
                    String[] splitArg = arg.split("=");
                    if (splitArg.length > 1) {
                        String key = splitArg[0];
                        String value = splitArg[1];
                        validateParameter(key, value);
                        parameters.put(splitArg[0], splitArg[1]);

                        log.debug("Input parameter: [" + splitArg[0] + "|" + splitArg[1] + "]");
                    }
                }
            }
        }

        return parameters;
    }

    private static void validateParameter(String key, String value) {
        switch (key) {
            case "httpPort":
                if (value == null || !value.matches("^[0-9]*$")) {
                    throw new InvalidInputParameterValueException("httpPort parameter has an invalid value, example of usage: httpPort=8080");
                }
                break;
            case "host":
                if (value == null || value.isEmpty()) {
                    throw new InvalidInputParameterValueException("host parameter has an invalid value, example of usage: host=localhost");
                }
                break;
        }
    }
}
