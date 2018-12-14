package link.mgiannone.githubchallenge.data.api;

import link.mgiannone.githubchallenge.data.model.AccessToken;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface AccessTokenService {

	@Headers("Accept: application/json")
	@POST
	@FormUrlEncoded
	Call<AccessToken> getAccessToken(
			@Field("client_id") String clientId,
			@Field("client_secret") String clientSecret,
			@Field("code") String code,
			@Url String url
	);
}
