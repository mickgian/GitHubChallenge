package link.mgiannone.githubchallenge.data;

import android.content.SharedPreferences;
import java.io.IOException;
import link.mgiannone.githubchallenge.AndroidApplication;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

	//This class provides an Authenticator (https://square.github.io/okhttp/3.x/okhttp/okhttp3/Authenticator.html)
	//that is  attached to the instance of OkHttpClient created in GitHubChallengeApiServiceModule

	@Override
	public Request authenticate(Route route, Response response) throws IOException {

		final SharedPreferences pref = AndroidApplication.getAppContext().getSharedPreferences("access_token", 0); // 0 - for private mode
		String accessTokenString = pref.getString("oauth.accesstoken", "");
		String tokenTypeString = pref.getString("oauth.tokentype", "");

		// Add new header to rejected request and retry it
		return response.request().newBuilder()
				.header("Accept", "application/json")
				.header("Content-type", "application/json")
				.header("Authorization", "access_token=" + accessTokenString + "&token_type=" + tokenTypeString)
				.build();
	}
}
