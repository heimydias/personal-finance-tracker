package dias.heimy.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathConstants {

    private static final String API = "/api/v1";

    public static final String AUTH = API + "/auth";
    public static final String AUTH_TOKEN = AUTH + "/token";
    public static final String AUTH_REFRESH_TOKEN = AUTH + "/refresh-token";

    public static final String USERS = API + "/users";
    public static final String USERS_BY_ID = USERS + "/{id}";

    public static final String TRANSACTIONS = API + "/transactions";
    public static final String TRANSACTIONS_BY_ID = TRANSACTIONS + "/{id}";
    public static final String TRANSACTIONS_BALANCE = TRANSACTIONS + "/balance";

    public static final String SAVINGS = API + "/savings";
    public static final String SAVINGS_BY_ID = SAVINGS + "/{id}";
    public static final String SAVINGS_YIELD = SAVINGS + "/{id}/yield";

    public static final String BALANCE = API + "/balance";
    public static final String BALANCE_MONTHLY = BALANCE + "/monthly";
}
