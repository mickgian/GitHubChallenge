package link.mgiannone.githubchallenge.data.model;

import com.google.gson.annotations.SerializedName;

public class AccessToken {

	@SerializedName("access_token")
	private String accesToken;

	@SerializedName("token_type")
	private String tokenType;

	public String getAccesToken() {
		return accesToken;
	}

	public String getTokenType() {
		return tokenType;
	}
}
