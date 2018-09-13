package com.santoni7.readme.common;

/**
 * Represents a
 * @param <TDaggerComponent>
 */
public interface InjectablePresenter<TDaggerComponent>{
    void init(TDaggerComponent component);
}
