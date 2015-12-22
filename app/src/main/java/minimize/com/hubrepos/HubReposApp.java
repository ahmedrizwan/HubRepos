package minimize.com.hubrepos;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by ahmedrizwan on 22/12/2015.
 */
public class HubReposApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Timber
        Timber.plant(new Timber.DebugTree());


    }
}
