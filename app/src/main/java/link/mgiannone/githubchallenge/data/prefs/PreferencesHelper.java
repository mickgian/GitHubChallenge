package link.mgiannone.githubchallenge.data.prefs;

public interface PreferencesHelper {


    String getCurrentTempOwner();

    void setCurrentTempOwner(String tempOwner);

    String getAccessTokenString();

    void setAccessTokenString(String accessTokenString);

    String getAccessTokenType();

    void setAccessTokenType(String accessTokenType);

}
