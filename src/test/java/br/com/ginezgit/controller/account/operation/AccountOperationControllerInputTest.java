package br.com.ginezgit.controller.account.operation;

import br.com.ginezgit.controller.AccountOperationController;
import br.com.ginezgit.model.Account;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.model.transaction.TransactionOrigin;
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
public class AccountOperationControllerInputTest {

    AccountOperationController accountOperationController = null;
    AccountOperationService accountsOperationService = null;

    Customer testCustomer1 = null;
    Account testAccount1 = null;
    Customer testCustomer2 = null;
    Account testAccount2 = null;

    @Before
    public void initialize() {
        prepareData();

        this.accountOperationController = PowerMockito.spy(new AccountOperationController());
        this.accountsOperationService = PowerMockito.spy(new AccountOperationService());
        Whitebox.setInternalState(AccountOperationController.class, "accountOperationService", accountsOperationService);
    }

    private void prepareData() {
        this.testCustomer1 = new Customer("ddcdfb95-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testAccount1 = new Account("7e75dff6-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(1000000000), System.currentTimeMillis(), testCustomer1);
        this.testCustomer2 = new Customer("sdasdadd-2a6e-4156-b295-f01516cd3b5c", "Nome de teste");
        this.testAccount2 = new Account("vxcvxcvx-05b9-4cf6-aaa1-701007d6c09c", new BigDecimal(1000000000), System.currentTimeMillis(), testCustomer2);
    }

    @Test
    public void transferWithNullInput() {
        Response httpResponse = accountOperationController.transfer(null, null, null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void transferWithFromAccountNullInput() {
        Response httpResponse = accountOperationController.transfer(null, this.testAccount2.getId(), BigDecimal.ONE);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void transferWithToAccountNullInput() {
        Response httpResponse = accountOperationController.transfer(this.testAccount1.getId(), null, BigDecimal.ONE);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void transferWithAmountNullInput() {
        Response httpResponse = accountOperationController.transfer(this.testAccount1.getId(), this.testAccount2.getId(), null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void transferWithZeroAmountInput() {
        Response httpResponse = accountOperationController.transfer(this.testAccount1.getId(), this.testAccount2.getId(), BigDecimal.ZERO);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void transferWithNegativeAmountInput() {
        Response httpResponse = accountOperationController.transfer(this.testAccount1.getId(), this.testAccount2.getId(), new BigDecimal(-1));
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void creditWithNullInput() {
        Response httpResponse = accountOperationController.credit(null, null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void creditWithAccountNullInput() {
        Response httpResponse = accountOperationController.credit(null, BigDecimal.ONE);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void creditWithAmountNullInput() {
        Response httpResponse = accountOperationController.credit(this.testAccount1.getId(), null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void creditWithZeroAmountInput() {
        Response httpResponse = accountOperationController.credit(this.testAccount1.getId(), BigDecimal.ZERO);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void creditWithNegativeAmountInput() {
        Response httpResponse = accountOperationController.credit(this.testAccount1.getId(), new BigDecimal(-1));
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void debitWithNullInput() {
        Response httpResponse = accountOperationController.debit(null, null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void debitWithAccountNullInput() {
        Response httpResponse = accountOperationController.debit(null, BigDecimal.ONE);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void debitWithAmountNullInput() {
        Response httpResponse = accountOperationController.debit(this.testAccount1.getId(), null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void debitWithZeroAmountInput() {
        Response httpResponse = accountOperationController.debit(this.testAccount1.getId(), BigDecimal.ZERO);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

    @Test
    public void debitWithNegativeAmountInput() {
        Response httpResponse = accountOperationController.debit(this.testAccount1.getId(), new BigDecimal(-1));
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpResponse.getStatus());
    }

}
