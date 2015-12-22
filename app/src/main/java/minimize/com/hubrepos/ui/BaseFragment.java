package minimize.com.hubrepos.ui;

import android.support.v4.app.Fragment;

import javax.inject.Inject;

import minimize.com.hubrepos.dagger.AppComponent;
import minimize.com.hubrepos.dagger.AppModule;
import minimize.com.hubrepos.dagger.DaggerAppComponent;
import minimize.com.hubrepos.retrofit.GithubService;

/**
 * Created by ahmedrizwan on 22/12/2015.
 */
public class BaseFragment extends Fragment {
    @Inject
    GithubService mGithubService;

    void inject(BaseFragment baseFragment) {
        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getActivity().getApplication()))
                .build();
        appComponent.inject(baseFragment);
    }


}
