package link.mgiannone.githubchallenge.data.repository.remote;

import javax.inject.Inject;

import link.mgiannone.githubchallenge.data.api.AccessTokenService;
import link.mgiannone.githubchallenge.data.model.AccessToken;
import link.mgiannone.githubchallenge.data.repository.AccessTokenDataSource;
import retrofit2.Call;

public class AccessTokenRemoteDataSource implements AccessTokenDataSource {

	private AccessTokenService accessTokenService;

	@Inject
	public AccessTokenRemoteDataSource(AccessTokenService accessTokenService) {
		this.accessTokenService = accessTokenService;
	}

	@Override
	public Call<AccessToken> getToken(String clientId, String clientSecret, String code){
		return accessTokenService.getAccessToken(clientId, clientSecret, code, "https://github.com/login/oauth/access_token");
	}
}
