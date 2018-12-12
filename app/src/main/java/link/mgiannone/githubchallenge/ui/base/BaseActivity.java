package link.mgiannone.githubchallenge.ui.base;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListener;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleRegistry;
import androidx.appcompat.app.AppCompatActivity;
import link.mgiannone.githubchallenge.AndroidApplication;
import link.mgiannone.githubchallenge.R;
import link.mgiannone.githubchallenge.data.GitHubChallengeRepositoryComponent;
import link.mgiannone.githubchallenge.ui.login.LoginActivity;
import link.mgiannone.githubchallenge.ui.repositories.RepositoriesActivity;


public class BaseActivity extends AppCompatActivity {
  private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

  protected GitHubChallengeRepositoryComponent getPropertyRepositoryComponent() {
    return ((AndroidApplication) getApplication()).getGitHubChallengeRepositoryComponent();
  }

  protected GitHubChallengeRepositoryComponent getLoginRepositoryComponent() {
    return ((AndroidApplication) getApplication()).getGitHubChallengeRepositoryComponent();
  }

  @Override
  public LifecycleRegistry getLifecycle() {
    return lifecycleRegistry;
  }

  public void actionBarSetup(Context context) {

      ActionBar mActionBar = getSupportActionBar();
      assert mActionBar != null;
      mActionBar.setDisplayShowHomeEnabled(false);
      mActionBar.setDisplayShowTitleEnabled(false);
      LayoutInflater mInflater = LayoutInflater.from(this);

      View actionBar = mInflater.inflate(R.layout.custom_actionbar, null);
      TextView mTitleTextView = (TextView) actionBar.findViewById(R.id.title_text);

      if(context instanceof RepositoriesActivity) {
		mTitleTextView.setText(getString(R.string.repos_activity_title));
      }else if (context instanceof LoginActivity) {
	  	mTitleTextView.setText(getString(R.string.login_activity_title));
      }

      mActionBar.setCustomView(actionBar);
      mActionBar.setDisplayShowCustomEnabled(true);
      ((Toolbar) actionBar.getParent()).setContentInsetsAbsolute(0,0);

      BoomMenuButton leftBmb = (BoomMenuButton) actionBar.findViewById(R.id.action_bar_left_bmb);

      leftBmb.setButtonEnum(ButtonEnum.SimpleCircle);
      leftBmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_2_1);
      leftBmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_2_1);
      leftBmb.setShowDuration(500);
      leftBmb.setHideDuration(500);
      for (int i = 0; i < leftBmb.getPiecePlaceEnum().pieceNumber(); i++) {
        if(i == 0) {
          leftBmb.addBuilder(new SimpleCircleButton.Builder()
                  .normalImageRes(R.drawable.ic_person_white)
                  .normalColor(getResources().getColor(R.color.actionColor)));
        }else if (i == 1){
          leftBmb.addBuilder(new SimpleCircleButton.Builder()
                  .normalImageRes(R.drawable.ic_repo)
                  .normalColor(getResources().getColor(R.color.actionColor)));
        }
      }
      leftBmb.setOnBoomListener(new OnBoomListener() {

          int clickedIndex = -1;

        @Override
        public void onClicked(int index, BoomButton boomButton) {
          switch (index){
            case 0:
                clickedIndex = 0;
              break;
            case 1:
                clickedIndex = 1;
          }
        }

        @Override
        public void onBackgroundClick() {
  //        Toast.makeText(context, "Click background!!!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onBoomWillHide() {
  //        Toast.makeText(context, "Will RE-BOOM!!!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onBoomDidHide() {
  //        Toast.makeText(context, "Did RE-BOOM!!!", Toast.LENGTH_LONG).show();
            switch (clickedIndex){
                case 0:
                    startActivity(new Intent(context, LoginActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(context, RepositoriesActivity.class));
                    break;
                case -1:
                    return;
            }
        }

        @Override
        public void onBoomWillShow() {
  //        Toast.makeText(context, "Will BOOM!!!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBoomDidShow() {
  //        Toast.makeText(context, "Did BOOM!!!", Toast.LENGTH_SHORT).show();
        }
    });
  }

}
