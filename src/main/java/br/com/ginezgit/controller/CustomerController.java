package br.com.ginezgit.controller;

import br.com.ginezgit.controller.exception.InvalidInputParameterValueException;
import br.com.ginezgit.model.Account;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.service.AccountService;
import br.com.ginezgit.service.CustomerService;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import br.com.ginezgit.service.exception.InsufficientAccountBalanceException;
import br.com.ginezgit.service.exception.InvalidParameterException;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Produces("application/json")
@Path("/customer")
public class CustomerController {

    static Logger log = Logger.getLogger(CustomerController.class.getName());

    private static CustomerService customerService = new CustomerService();
    private static AccountService accountService = new AccountService();

    @GET
    @Path("/{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {
        try {
            log.debug(Thread.currentThread().getId() + " Request received: getCustomer("+customerId+")" );
            validateGetCustomer(customerId);
            return Response.ok(this.customerService.getCustomer(customerId)).build();
        } catch (InvalidInputParameterValueException e) {
            return RestUtil.badRequest(e.getMessage());
        } catch (EntityNotFoundException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (Exception e) {
            log.error(Thread.currentThread().getId() + " getCustomer("+customerId+")" ,e);
            return RestUtil.error();
        }
    }

    private void validateGetCustomer(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("customerId"));
        }
    }

    @GET
    public Response getAllCustomers() {
        log.debug(Thread.currentThread().getId() + " Request received: getAllAccounts()" );
        return Response.ok(this.customerService.getAllCustomers()).build();
    }

    @POST
    public Response createCustomer(Customer customer) {
        try {
            validateCreateCustomer(customer);
            log.debug(Thread.currentThread().getId() + " Request received: createCustomer("+customer.getName()+")" );
            Customer newCustomer = this.customerService.createNewCustomer(customer);
            return Response.ok(newCustomer).build();
        } catch (InvalidInputParameterValueException e) {
            return RestUtil.badRequest(e.getMessage());
        } catch (InsufficientAccountBalanceException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (EntityNotFoundException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (InvalidParameterException e) {
            return RestUtil.error();
        } catch (Exception e) {
            log.error(Thread.currentThread().getId() + " createCustomer("+customer.getName()+")" ,e);
            return RestUtil.error();
        }
    }

    private void validateCreateCustomer(Customer customer) {
        if (customer == null || customer.getName() == null || customer.getName().isEmpty()) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("customer.name"));
        }
    }

    @PUT
    @Path("/{customerId}/createAccount")
    public Response createNewAccount(@PathParam("customerId") String customerId) {
        try {
            log.debug(Thread.currentThread().getId() + " Request received: createAccount("+customerId+")" );
            validateCreateNewAccount(customerId);

            Account createdAccount = this.accountService.createNewAccount(customerId);
            return Response.ok(createdAccount).build();
        } catch (InvalidInputParameterValueException e) {
            return RestUtil.badRequest(e.getMessage());
        } catch (InsufficientAccountBalanceException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (EntityNotFoundException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (InvalidParameterException e) {
            return RestUtil.error();
        } catch (Exception e) {
            log.error(Thread.currentThread().getId() + " createAccount("+customerId+")" ,e);
            return RestUtil.error();
        }
    }

    private void validateCreateNewAccount(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("customerId"));
        }
    }

    @PUT
    @Path("/{customerId}")
    public Response updateCustomer(@PathParam("customerId") String customerId, Customer customer) {
        try {
            validateUpdateCustomer(customerId, customer);
            log.debug(Thread.currentThread().getId() + " Request received: updateCustomer("+customerId+","+customer.getName()+")" );

            customer.setId(customerId);
            Customer updatedCustomer = this.customerService.updateCustomer(customer);
            return Response.ok(updatedCustomer).build();
        } catch (InvalidInputParameterValueException e) {
            return RestUtil.badRequest(e.getMessage());
        } catch (InsufficientAccountBalanceException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (EntityNotFoundException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (InvalidParameterException e) {
            return RestUtil.error();
        } catch (Exception e) {
            log.error(Thread.currentThread().getId() + " updateCustomer("+customerId+","+customer.getName()+")" ,e);
            return RestUtil.error();
        }
    }

    private void validateUpdateCustomer(String customerId, Customer customer) {
        if (customerId == null || customerId.isEmpty()) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("customerId"));
        }

        if (customer == null || customer.getName() == null || customer.getName().isEmpty()) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("customer.name"));
        }
    }

}
