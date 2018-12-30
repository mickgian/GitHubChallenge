package link.mgiannone.githubchallenge.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import link.mgiannone.githubchallenge.data.ApplicationContext;
import link.mgiannone.githubchallenge.data.PreferenceInfo;



public class AppPreferencesHelper implements PreferencesHelper {


    private static final String PREF_KEY_TEMP_OWNER = "PREF_KEY_TEMP_OWNER";
    private static final String PREF_KEY_ACCESS_TOKEN_STRING = "PREF_KEY_ACCESS_TOKEN_STRING";
    private static final String PREF_KEY_ACCESS_TOKEN_TYPE = "PREF_KEY_ACCESS_TOKEN_TYPE";

    private final SharedPreferences mPrefs;

    @Inject
    public AppPreferencesHelper(@ApplicationContext Context context,
                                @PreferenceInfo String prefFileName) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }


    @Override
    public String getCurrentTempOwner() {
        return mPrefs.getString(PREF_KEY_TEMP_OWNER, null);
    }

    @Override
    public void setCurrentTempOwner(String tempOwner) {
        mPrefs.edit().putString(PREF_KEY_TEMP_OWNER, tempOwner).apply();
    }

    @Override
    public String getAccessTokenString() {
        return mPrefs.getString(PREF_KEY_ACCESS_TOKEN_STRING, null);
    }

    @Override
    public void setAccessTokenString(String accessTokenString) {
        mPrefs.edit().putString(PREF_KEY_ACCESS_TOKEN_STRING, accessTokenString).apply();
    }

    @Override
    public String getAccessTokenType() {
        return mPrefs.getString(PREF_KEY_ACCESS_TOKEN_TYPE, null);
    }

    @Override
    public void setAccessTokenType(String accessTokenType) {
        mPrefs.edit().putString(PREF_KEY_ACCESS_TOKEN_TYPE, accessTokenType).apply();
    }


}
