package com.santoni7.readme.common;

public abstract class PresenterBase<V extends MvpView> implements MvpPresenter<V>{
    private V view;
    @Override
    public void attachView(V view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void destroy() {
        detachView();
    }

    public V getView(){
        return view;
    }

    public boolean isViewAttached(){
        return view != null;
    }
}
