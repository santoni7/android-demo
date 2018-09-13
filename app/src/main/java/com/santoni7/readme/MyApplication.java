package com.santoni7.readme;

import android.app.Application;

import com.santoni7.readme.dagger.ContextModule;
import com.santoni7.readme.dagger.DaggerMyComponent;
import com.santoni7.readme.dagger.DataSourceModule;
import com.santoni7.readme.dagger.MyComponent;

public class MyApplication extends Application {
    private MyComponent component;
    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerMyComponent.builder()
                .contextModule(new ContextModule(getApplicationContext()))
                .dataSourceModule(new DataSourceModule())
                .build();
    }

    public MyComponent getComponent() {
        return component;
    }
}
