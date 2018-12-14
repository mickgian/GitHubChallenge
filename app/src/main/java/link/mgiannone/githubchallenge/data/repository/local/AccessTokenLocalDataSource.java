package link.mgiannone.githubchallenge.data.repository.local;

import link.mgiannone.githubchallenge.data.model.AccessToken;
import link.mgiannone.githubchallenge.data.repository.AccessTokenDataSource;
import retrofit2.Call;

public class AccessTokenLocalDataSource implements AccessTokenDataSource {

	@Override
	public Call<AccessToken> getToken(String clientId, String clientSecret, String code) {
		return null;
	}

}
