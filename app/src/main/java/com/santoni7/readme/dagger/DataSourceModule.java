package com.santoni7.readme.dagger;

import android.content.Context;

import com.santoni7.readme.data.ImageRepository;
import com.santoni7.readme.data.ImageRepositoryImpl;
import com.santoni7.readme.data.datasource.ImageDataSource;
import com.santoni7.readme.data.datasource.LocalImageDataSource;
import com.santoni7.readme.data.datasource.PersonDataSource;
import com.santoni7.readme.data.datasource.RemoteImageDataSource;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ContextModule.class)
public class DataSourceModule {

    @Provides
    @Singleton
    ImageRepository imageRepository(@Named("LocalImageDataSource") ImageDataSource localDS,
                                    @Named("RemoteImageDataSource") ImageDataSource remoteDS) {
        return new ImageRepositoryImpl(localDS, remoteDS);
    }

    @Named("LocalImageDataSource")
    @Provides
    @Singleton
    ImageDataSource localImageDataSource(Context appContext) {
        return new LocalImageDataSource(appContext);
    }

    @Named("RemoteImageDataSource")
    @Provides
    @Singleton
    ImageDataSource remoteImageDataSource(){
        return new RemoteImageDataSource();
    }

    @Provides
    @Singleton
    PersonDataSource personDataSource(){
        return new PersonDataSource();
    }
}
