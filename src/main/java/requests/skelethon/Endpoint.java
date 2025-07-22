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
            BaseUserResponse.class
    ),

    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),

    DEPOSIT(
            "/accounts/deposit",
            DepositRequest.class,
            BaseAccountResponse.class
    ),

    CUSTOMER_PROFILE(
            "/customer/profile",
            ChangeNameRequest.class,
            ChangeNameResponse.class
    ),

    GET_CUSTOMER_PROFILE(
            "/customer/profile",
            ChangeNameRequest.class,
            BaseUserResponse.class
    ),

    TRANSFER(
            "/accounts/transfer",
            TransferMoneyRequest.class,
            TransferMoneyResponse.class
    ),

    CUSTOMER_ACCOUNTS(
            "/customer/accounts",
            BaseModel.class,
            BaseAccountResponse.class
    ),

    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            BaseAccountResponse.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
