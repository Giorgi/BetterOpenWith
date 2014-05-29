package com.aboutmycode.openwith.app.common;

/**
 * Created by Giorgi on 5/29/2014.
 */

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;

import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
abstract public class AbstractListLoader<T> extends AsyncTaskLoader<List<T>> {
    abstract protected List<T> buildList();

    List<T> lastList = null;

    public AbstractListLoader(Context context) {
        super(context);
    }

    /**
     * Runs on a worker thread, loading in our data. Delegates
     * the real work to concrete subclass' buildList() method.
     */
    @Override
    public List<T> loadInBackground() {
        List<T> list = buildList();

        if (list != null) {
            // Ensure the list window is filled
            list.size();
        }

        return (list);
    }

    /**
     * Runs on the UI thread, routing the results from the
     * background thread to whatever is using the Cursor
     * (e.g., a CursorAdapter).
     */
    @Override
    public void deliverResult(List<T> list) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (list != null) {
                list.clear();
            }

            return;
        }

        List<T> oldList = lastList;
        lastList = list;

        if (isStarted()) {
            super.deliverResult(list);
        }

        if (oldList != null && oldList != list) {
            oldList.clear();
        }
    }

    /**
     * Starts an asynchronous load of the list data.
     * When the result is ready the callbacks will be called
     * on the UI thread. If a previous load has been completed
     * and is still valid the result may be passed to the
     * callbacks immediately.
     * <p/>
     * Must be called from the UI thread.
     */
    @Override
    protected void onStartLoading() {
        if (lastList != null) {
            deliverResult(lastList);
        }

        if (takeContentChanged() || lastList == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread, triggered by a
     * call to stopLoading().
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Must be called from the UI thread, triggered by a
     * call to cancel(). Here, we make sure our Cursor
     * is closed, if it still exists and is not already closed.
     */
    @Override
    public void onCanceled(List<T> list) {
        if (list != null) {
            list.clear();
        }
    }

    /**
     * Must be called from the UI thread, triggered by a
     * call to reset(). Here, we make sure our Cursor
     * is closed, if it still exists and is not already closed.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (lastList != null) {
            lastList.clear();
        }

        lastList = null;
    }
}