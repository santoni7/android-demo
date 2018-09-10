package com.santoni7.readme.common;

public interface MvpPresenter<V extends MvpView> {
    void attachView(V view);
    void viewReady();
    void detachView();
    void destroy();
}
