package minimize.com.hubrepos.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import minimize.com.hubrepos.R;
import minimize.com.hubrepos.databinding.ActivityContainerBinding;

public class ContainerActivity extends BaseActivity {

    ActivityContainerBinding mBinding;

    public boolean isTwoPane() {
        return mTwoPane;
    }

    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_container);
        injectActivity(this);
        setSupportActionBar(mBinding.mainToolbar);
        mBinding.mainToolbar.setTitle(getString(R.string.app_name));
        //Check for twoPanes
        if (mBinding.containerRepos != null) {
            mTwoPane = true;
            //load the artist fragment in the tracksContainer
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentLanguages, new LanguagesFragment())
                    .commit();
        } else {
            mTwoPane = false;
            //make transaction for the artists fragment
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new LanguagesFragment())
                        .commit();
            }
        }
    }

    public void hideSelectMessage(){
        mBinding.textViewSelect.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
