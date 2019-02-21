package br.com.ginezgit.controller;

import br.com.ginezgit.controller.exception.InvalidInputParameterValueException;
import br.com.ginezgit.model.transaction.Transaction;
import br.com.ginezgit.model.transaction.TransactionOrigin;
import br.com.ginezgit.service.AccountOperationService;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import br.com.ginezgit.service.exception.InsufficientAccountBalanceException;
import br.com.ginezgit.service.exception.InvalidParameterException;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Produces("application/json")
@Path("/account")
public class AccountOperationController {

    static Logger log = Logger.getLogger(AccountOperationController.class.getName());

    private static AccountOperationService accountOperationService = new AccountOperationService();

    @POST
    @Path("/transfer")
    public Response transfer(@FormParam("fromAccountId") String fromAccountId,
                             @FormParam("toAccountId") String toAccountId,
                             @FormParam("amount") BigDecimal amount) {
        try {
            log.debug(Thread.currentThread().getId() + " Request received: transfer("+fromAccountId+","+toAccountId+","+amount+")" );
            validateTransfer(fromAccountId, toAccountId, amount);
            Transaction transaction = this.accountOperationService.transfer(TransactionOrigin.EXTERNAL, fromAccountId, toAccountId, amount);
            return Response.ok(transaction).build();
        } catch (InvalidInputParameterValueException e) {
            return RestUtil.badRequest(e.getMessage());
        } catch (InsufficientAccountBalanceException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (EntityNotFoundException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (InvalidParameterException e) {
            return RestUtil.error();
        } catch (Exception e) {
            log.error(Thread.currentThread().getId() + " transfer("+fromAccountId+","+toAccountId+","+amount+")" ,e);
            return RestUtil.error();
        }
    }

    private void validateTransfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        if (fromAccountId == null || fromAccountId.isEmpty()) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("fromAccountId"));
        }

        if (toAccountId == null || toAccountId.isEmpty()) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("toAccountId"));
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("amount"));
        }
    }

    @POST
    @Path("/credit")
    public Response credit(@FormParam("accountId") String accountId,
                           @FormParam("amount") BigDecimal amount) {
        try {
            log.debug(Thread.currentThread().getId() + " Request received: credit("+accountId+","+amount+")" );
            validateCredit(accountId, amount);
            Transaction transaction = this.accountOperationService.credit(TransactionOrigin.EXTERNAL, accountId, amount);
            return Response.ok(transaction).build();
        } catch (InvalidInputParameterValueException e) {
            return RestUtil.badRequest(e.getMessage());
        } catch (EntityNotFoundException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (InvalidParameterException e) {
            return RestUtil.error();
        } catch (Exception e) {
            log.error(Thread.currentThread().getId() + " credit("+accountId+","+amount+")" ,e);
            return RestUtil.error();
        }
    }

    private void validateCredit(String accountId, BigDecimal amount) {
        if (accountId == null || accountId.isEmpty()) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("accountId"));
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("amount"));
        }
    }

    @POST
    @Path("/debit")
    public Response debit(@FormParam("accountId") String accountId,
                          @FormParam("amount") BigDecimal amount) {
        try {
            log.debug(Thread.currentThread().getId() + " Request received: debit("+accountId+","+amount+")" );
            validateDebit(accountId, amount);
            Transaction transaction = this.accountOperationService.debit(TransactionOrigin.EXTERNAL, accountId, amount);
            return Response.ok(transaction).build();
        } catch (InvalidInputParameterValueException e) {
            return RestUtil.badRequest(e.getMessage());
        } catch (InsufficientAccountBalanceException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (EntityNotFoundException e) {
            return RestUtil.unauthorized(e.getMessage());
        } catch (InvalidParameterException e) {
            return RestUtil.error();
        } catch (Exception e) {
            log.error(Thread.currentThread().getId() + " debit("+accountId+","+amount+")" ,e);
            return RestUtil.error();
        }
    }

    private void validateDebit(String accountId, BigDecimal amount) {
        if (accountId == null || accountId.isEmpty()) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("accountId"));
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputParameterValueException(InvalidInputParameterValueException.buildInvalidParameterValueMessage("amount"));
        }
    }

    @GET
    @Path("/transactions")
    public Response getAllTransactions() {
        log.debug(Thread.currentThread().getId() + " Request received: getAllTransactions()" );
        return Response.ok(this.accountOperationService.getAllTransactions()).build();
    }
}
