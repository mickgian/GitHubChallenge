package link.mgiannone.githubchallenge.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import link.mgiannone.githubchallenge.R;
import link.mgiannone.githubchallenge.data.Config;
import link.mgiannone.githubchallenge.ui.base.BaseActivity;

public class LoginActivity extends BaseActivity implements LoginContract.View{

	@BindView(R.id.loginButton)
	Button loginButton;

	@Inject
	LoginPresenter presenter;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);
		initializePresenter();
		setupWidgets();

	}

	@Override
	protected void onResume(){
		super.onResume();

		actionBarSetup(LoginActivity.this);

		Uri uri = getIntent().getData();

		if(uri != null && uri.toString().startsWith(Config.REDIRECT_URI)){
			String code = uri.getQueryParameter("code");
			presenter.getAccessToken(Config.CLIENT_ID, Config.CLIENT_SECRET, code);
		}
	}


	private void initializePresenter() {
		DaggerLoginComponent.builder()
				.loginPresenterModule(new LoginPresenterModule(this))
				.gitHubChallengeRepositoryComponent(getLoginRepositoryComponent())
				.build()
				.inject(this);
	}

	private void setupWidgets() {

		loginButton.setOnClickListener(v -> {
			Intent intent = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse(Config.GITHUB_API_AUTHORIZE + "?client_id=" + Config.CLIENT_ID + "&redirect_uri=" + Config.REDIRECT_URI));
			startActivity(intent);
		});

	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
