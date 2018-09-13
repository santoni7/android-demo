package com.santoni7.readme.dagger;


import com.santoni7.readme.activity.details.DetailsPresenter;
import com.santoni7.readme.activity.main.MainPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DataSourceModule.class})
public interface MyComponent {
    void inject(MainPresenter presenter);

    void inject(DetailsPresenter presenter);
}
