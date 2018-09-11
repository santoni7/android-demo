package com.santoni7.readme.async;

import io.reactivex.subjects.AsyncSubject;

/**
 * A basic interface for AsyncTasks that return execution result via reactive method
 * @param <TResult> AsyncTask's return type
 */
public interface ReactiveAsyncTask<TResult> {
    /**
     * Get result observable (AsyncSubject) which will pass result to observers,
     * or notify about error occurred
     */
    AsyncSubject<TResult> getResultSource();
}
