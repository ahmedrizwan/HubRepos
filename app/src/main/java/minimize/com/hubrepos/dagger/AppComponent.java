package minimize.com.hubrepos.dagger;

import javax.inject.Singleton;

import dagger.Component;
import minimize.com.hubrepos.ui.BaseActivity;
import minimize.com.hubrepos.ui.BaseFragment;

@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {

    void inject(BaseActivity activity);

    void inject(BaseFragment fragment);
}