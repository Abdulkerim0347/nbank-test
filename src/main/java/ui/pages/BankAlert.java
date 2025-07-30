package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: "),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY("Name must contain two words with letters only"),
    PLEASE_ENTER_A_VALID_NAME("❌ Please enter a valid name."),
    SUCCESSFULLY_DEPOSITED("✅ Successfully deposited"),
    PLEASE_DEPOSIT_LESS_THAN_OR_EQUAL_TO_5000("❌ Please deposit less or equal to 5000$."),
    SUCCESSFULLY_TRANSFERRED("✅ Successfully transferred $"),
    ERROR_INVALID_TRANSFER("❌ Error: Invalid transfer: insufficient funds or invalid accounts"),
    ERROR_TRANSFER_AMOUNT_CANNOT_EXCEED_10000("❌ Error: Transfer amount cannot exceed 10000"),
    PLEASE_SELECT_AN_ACCOUNT("❌ Please select an account."),
    PLEASE_ENTER_A_VALID_AMOUNT("❌ Please enter a valid amount."),
    PLEASE_FILL_ALL_FIELDS_AND_CONFIRM("❌ Please fill all fields and confirm."),
    NO_USER_FOUND_WITH_THIS_ACCOUNT_NUMBER("❌ No user found with this account number.");

    private final String message;
    BankAlert(String message) {
        this.message = message;
    }
}
