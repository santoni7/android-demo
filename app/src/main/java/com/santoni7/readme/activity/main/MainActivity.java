package com.santoni7.readme.activity.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.santoni7.readme.MyApplication;
import com.santoni7.readme.R;
import com.santoni7.readme.activity.details.DetailsActivity;
import com.santoni7.readme.adapter.PersonRecyclerAdapter;
import com.santoni7.readme.dagger.MyComponent;
import com.santoni7.readme.data.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private final String TAG = MainActivity.class.getSimpleName();
    private MainContract.Presenter presenter;

    private RecyclerView recyclerView;
    private PersonRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter();

        initView();

        MyComponent component = ((MyApplication) getApplication()).getComponent();

        presenter.attachView(this);
        presenter.init(component);

        adapter = new PersonRecyclerAdapter(new ArrayList<>(), presenter::onListItemClicked);
        recyclerView.setAdapter(adapter);

        setupLayoutManager(getResources().getConfiguration().orientation);

        presenter.viewReady();
    }

    private void initView() {
        this.recyclerView = findViewById(R.id.recyclerView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.main_activity_title);
        toolbar.setSubtitle(null);
//        toolbar.setSubtitle(R.string.main_activity_subtitle);
        toolbar.inflateMenu(R.menu.main_menu);

        swipeLayout = findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setOnRefreshListener(presenter::onRefreshRequested);
        showProgress();
    }

    private void setupLayoutManager(int orientation) {
        // Change RecyclerView LayoutManager to display cards in two columns in landscape mode
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    @Override
    public void addPerson(Person person) {
        adapter.addPerson(person);
    }

    @Override
    public void updatePerson(Person person) {
        if (adapter != null) {
            adapter.updatePerson(person);
        }
    }

    @Override
    public void clearPeopleList() {
        adapter.clear();
        adapter.dispose();

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_refresh:
                presenter.onRefreshRequested();
                break;
            case R.id.menu_item_about:
                presenter.onAboutClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress() {
        Log.d(TAG, "showProgress");
        swipeLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        Log.d(TAG, "HideProgress");
        if (swipeLayout.isRefreshing()) {
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public InputStream openAssetFile(String fileName) throws IOException {
        return getAssets().open(fileName);
    }

    @Override
    public void openDetailsScreen(Person person) {
        Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
        i.putExtra(DetailsActivity.EXTRA_PERSON_ID, person.getId());
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupLayoutManager(newConfig.orientation);
    }

    @Override
    public void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setView(R.layout.about_dialog)
                .setPositiveButton("Close", null).show();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        presenter.onDestroy();
        presenter.detachView();
        adapter.dispose();
        super.onDestroy();
    }
}
