package com.leroy.magmobile.api.tests;

import com.google.inject.Inject;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.TestCase;

import static com.leroy.magmobile.api.tests.matchers.IsNotANumber.notANumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ApiTest extends BaseProjectTest {

    @Inject
    private MagMobileClient magMobileClient;

    @TestCase(111)
    @Test(description = "C111 tttt")
    public void test1() {
        // Some actions
        // magMobileClient.searchProductsBy()

        assertThat(Math.sqrt(-1), is(notANumber()));
    }

}
