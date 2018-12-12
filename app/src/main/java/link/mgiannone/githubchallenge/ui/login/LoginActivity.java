package link.mgiannone.githubchallenge.ui.login;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import javax.inject.Inject;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import link.mgiannone.githubchallenge.R;
import link.mgiannone.githubchallenge.ui.base.BaseActivity;

public class LoginActivity extends BaseActivity implements LoginContract.View{

	TextView notificationText;

	@Inject
	LoginPresenter presenter;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		actionBarSetup(LoginActivity.this);
		ButterKnife.bind(this);
		initializePresenter();
		setupWidgets();
	}

	private void initializePresenter() {
		DaggerLoginComponent.builder()
				.loginPresenterModule(new LoginPresenterModule(this))
				.gitHubChallengeRepositoryComponent(getLoginRepositoryComponent())
				.build()
				.inject(this);
	}

	private void setupWidgets() {

		// Set notification text visible first
//		notificationText.setVisibility(View.GONE);


	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);


		return true;
	}

	@Override public void showNoDataMessage() {
		showNotification(getString(R.string.msg_no_data));
	}

	@Override public void showErrorMessage(String error) {
		showNotification(error);
	}

	private void showNotification(String message) {
		notificationText.setVisibility(View.VISIBLE);
		notificationText.setText(message);
	}

}
