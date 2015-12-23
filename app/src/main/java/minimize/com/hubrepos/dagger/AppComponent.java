package minimize.com.hubrepos.dagger;

import javax.inject.Singleton;

import dagger.Component;
import minimize.com.hubrepos.retrofit.GithubService;
import minimize.com.hubrepos.ui.BaseActivity;
import minimize.com.hubrepos.ui.BaseFragment;
import minimize.com.hubrepos.ui.DetailsFragment;

@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {

    void inject(BaseActivity activity);

    void inject(BaseFragment fragment);

    void inject(DetailsFragment detailsFragment);

    GithubService getGithubService();
}