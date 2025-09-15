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
}
