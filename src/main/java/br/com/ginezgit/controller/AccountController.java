package br.com.ginezgit.controller;

import br.com.ginezgit.controller.exception.InvalidInputParameterValueException;
import br.com.ginezgit.service.AccountService;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import br.com.ginezgit.service.exception.InsufficientAccountBalanceException;
import br.com.ginezgit.service.exception.InvalidParameterException;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Produces("application/json")
@Path("/account")
public class AccountController {

    static Logger log = Logger.getLogger(AccountController.class.getName());

    private static AccountService accountService = new AccountService();

    @GET
    @Path("/{accountId}")
    public Response getAccount(@PathParam("accountId") String accountId) {
        try {
            log.debug(Thread.currentThread().getId() + " Request received: getAccount("+accountId+")" );
            validateGetAccount(accountId);
            return Response.ok(this.accountService.getAccount(accountId)).build();
        } catch (InvalidInputParameterValueException e) {
            return RestUtil.badRequest(e.getMessage());
        } catch (EntityNotFoundException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (Exception e) {
            log.error(Thread.currentThread().getId() + " getAccount("+accountId+")" ,e);
            return RestUtil.error();
        }
    }

    private void validateGetAccount(String accountId) {
        if (accountId == null || accountId.isEmpty()) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("accountId"));
        }
    }

    @GET
    public Response getAllAccounts() {
        log.debug(Thread.currentThread().getId() + " Request received: getAllAccounts()" );
        return Response.ok(this.accountService.getAllAccounts()).build();
    }

}
