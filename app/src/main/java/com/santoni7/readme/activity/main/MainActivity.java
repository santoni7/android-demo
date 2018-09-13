package com.santoni7.readme.activity.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.santoni7.readme.R;
import com.santoni7.readme.activity.details.DetailsActivity;
import com.santoni7.readme.adapter.PersonRecyclerAdapter;
import com.santoni7.readme.data.ImageRepository;
import com.santoni7.readme.data.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private final String TAG = MainActivity.class.getSimpleName();
    private MainContract.Presenter presenter;

    private RecyclerView recyclerView;
    private PersonRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageRepository.instance().initialize(getApplicationContext());

        initView();

        presenter = new MainPresenter();
        presenter.attachView(this);

        adapter = new PersonRecyclerAdapter(new ArrayList<>(), presenter::onListItemClicked);
        recyclerView.setAdapter(adapter);

        presenter.viewReady();
    }

    private void initView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.main_activity_title);
        }

        this.recyclerView = findViewById(R.id.recyclerView);
        setupLayoutManager(getResources().getConfiguration().orientation);
    }

    private void setupLayoutManager(int orientation) {
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
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        presenter.onDestroy();
        presenter.detachView();
        adapter.dispose();
        ImageRepository.instance().dispose();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setupLayoutManager(newConfig.orientation);
    }
}
