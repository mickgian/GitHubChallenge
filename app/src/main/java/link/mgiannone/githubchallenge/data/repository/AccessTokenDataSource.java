package link.mgiannone.githubchallenge.data.repository;

import link.mgiannone.githubchallenge.data.model.AccessToken;
import retrofit2.Call;

public interface AccessTokenDataSource {

	Call<AccessToken> getToken(String clientId, String clientSecret, String code);

}
