package br.com.ginezgit.controller.account.operation;

import br.com.ginezgit.controller.AccountOperationController;
import br.com.ginezgit.model.Account;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.model.transaction.CreditTransaction;
import br.com.ginezgit.model.transaction.DebitTransaction;
import br.com.ginezgit.model.transaction.TransactionOrigin;
import br.com.ginezgit.model.transaction.TransferTransaction;
import br.com.ginezgit.service.AccountOperationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
public class AccountOperationControllerTest {

    AccountOperationController accountOperationController = null;
    AccountOperationService accountsOperationService = null;

    Customer testCustomer1 = null;
    Account testAccount1 = null;
    Customer testCustomer2 = null;
    Account testAccount2 = null;
    TransferTransaction transferTransaction = null;
    CreditTransaction creditTransaction = null;
    DebitTransaction debitTransaction = null;

    String fakeAccount = "123456789";

    @Before
    public void initialize() {
        prepareData();

        this.accountOperationController = PowerMockito.spy(new AccountOperationController());
        this.accountsOperationService = PowerMockito.spy(new AccountOperationService());

        this.transferTransaction = TransferTransaction.newTransaction(TransactionOrigin.EXTERNAL, testAccount1, testAccount2, BigDecimal.TEN);
        this.creditTransaction = CreditTransaction.newTransaction(TransactionOrigin.EXTERNAL, testAccount1, new BigDecimal(1000));
        this.debitTransaction = DebitTransaction.newTransaction(TransactionOrigin.EXTERNAL, testAccount1, new BigDecimal(1000));

        Whitebox.setInternalState(AccountOperationController.class, "accountOperationService", accountsOperationService);
    }

    private void prepareData() {
        this.testCustomer1 = new Customer("ddcdfb95-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testAccount1 = new Account("7e75dff6-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(1000000000), System.currentTimeMillis(), testCustomer1);
        this.testCustomer2 = new Customer("sdasdadd-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testAccount2 = new Account("vxcvxcvx-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(1000000000), System.currentTimeMillis(), testCustomer2);
    }

    @Test
    public void transfer() throws Exception {
        PowerMockito
                .doReturn(this.transferTransaction)
                .when(accountsOperationService, method(AccountOperationService.class, "transfer", TransactionOrigin.class, String.class, String.class, BigDecimal.class))
                .withArguments(any(TransactionOrigin.class), anyString(), anyString(), any(BigDecimal.class));

        Response httpResponse = accountOperationController.transfer(this.testAccount1.getId(), this.testAccount2.getId(), BigDecimal.ONE);
        Assert.assertEquals(this.transferTransaction, httpResponse.getEntity());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void credit() throws Exception {
        PowerMockito
                .doReturn(this.creditTransaction)
                .when(accountsOperationService, method(AccountOperationService.class, "credit", TransactionOrigin.class, String.class, BigDecimal.class))
                .withArguments(any(TransactionOrigin.class), anyString(), any(BigDecimal.class));

        Response httpResponse = accountOperationController.credit(this.testAccount1.getId(), BigDecimal.ONE);
        Assert.assertEquals(this.creditTransaction, httpResponse.getEntity());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void debit() throws Exception {
        PowerMockito
                .doReturn(this.debitTransaction)
                .when(accountsOperationService, method(AccountOperationService.class, "debit", TransactionOrigin.class, String.class, BigDecimal.class))
                .withArguments(any(TransactionOrigin.class), anyString(), any(BigDecimal.class));

        Response httpResponse = accountOperationController.debit(this.testAccount1.getId(), BigDecimal.ONE);
        Assert.assertEquals(this.debitTransaction, httpResponse.getEntity());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatus());
    }

}
