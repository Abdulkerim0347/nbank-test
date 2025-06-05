package requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),

    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),

    DEPOSIT(
            "/accounts/deposit",
            DepositRequest.class,
            DepositResponse.class
    ),

    PROFILE(
            "/customer/profile",
            ChangeNameRequest.class,
            ChangeNameResponse.class
    ),

    TRANSFER(
            "/accounts/transfer",
            TransferMoneyRequest.class,
            TransferMoneyResponse.class
    ),

    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            CreateAccountResponse.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
