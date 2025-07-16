package iteration2.api;

import generators.RandomData;
import iteration1.api.BaseTest;
import org.junit.jupiter.api.Test;
import requests.steps.AdminSteps;
import requests.steps.DepositSteps;

public class DepositMoneyTest extends BaseTest {

    @Test
    public void userCanDepositMoneyTest() {
        final double DEPOSIT_AMOUNT = RandomData.getRandom().nextDouble(5000);

        var userRequest = AdminSteps.createUser();

        var account = DepositSteps.createAccount(userRequest);

        var depositResponse = DepositSteps.depositMoney(userRequest, account, DEPOSIT_AMOUNT);

        softly.assertThat(depositResponse.getBalance()).isEqualTo(DEPOSIT_AMOUNT);
        softly.assertThat(depositResponse.getAccountNumber()).isNotBlank();
        softly.assertThat(depositResponse.getId()).isGreaterThan(0);
    }
}
