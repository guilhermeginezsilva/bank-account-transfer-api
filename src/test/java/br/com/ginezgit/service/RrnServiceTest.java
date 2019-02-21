package br.com.ginezgit.service;

import br.com.ginezgit.dao.CrudDAO;
import br.com.ginezgit.dao.impl.CustomerDAO;
import br.com.ginezgit.model.Customer;
import br.com.ginezgit.service.exception.EntityNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
public class RrnServiceTest {

    @Test
    public void validateRrnGeneration() throws Exception {
        Assert.assertEquals(RrnService.getNewRrn()+1, RrnService.getNewRrn());
        Assert.assertEquals(RrnService.getNewRrn()+1, RrnService.getNewRrn());
        Assert.assertEquals(RrnService.getNewRrn()+1, RrnService.getNewRrn());
        Assert.assertEquals(RrnService.getNewRrn()+1, RrnService.getNewRrn());
        Assert.assertEquals(RrnService.getNewRrn()+1, RrnService.getNewRrn());
    }

}
