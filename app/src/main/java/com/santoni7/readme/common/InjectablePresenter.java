package com.santoni7.readme.common;

public interface InjectablePresenter<TDaggerComponent>{
    void init(TDaggerComponent component);
}
