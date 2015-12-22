package minimize.com.hubrepos.ui;

import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import io.realm.Realm;
import minimize.com.hubrepos.dagger.AppComponent;
import minimize.com.hubrepos.dagger.AppModule;
import minimize.com.hubrepos.dagger.DaggerAppComponent;
import retrofit.Retrofit;

/**
 * Created by ahmedrizwan on 22/12/2015.
 */
public class BaseActivity extends AppCompatActivity {
    @Inject
    Realm mRealm;

    @Inject
    Retrofit mRetrofit;

    public void injectActivity(BaseActivity baseActivity){
        AppComponent appComponent = DaggerAppComponent.builder().appModule(new AppModule(getApplication())).build();
        appComponent.inject(baseActivity);
    }
}
