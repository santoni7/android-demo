package com.santoni7.readme.dagger;

import android.content.Context;

import java.lang.ref.WeakReference;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    private Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context context() {
        return context.getApplicationContext();
    }

    @Provides
    public WeakReference<Context> contextWeakReference() {
        return new WeakReference<>(context.getApplicationContext());
    }
}
