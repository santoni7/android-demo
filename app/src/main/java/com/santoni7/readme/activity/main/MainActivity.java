package com.santoni7.readme.activity.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.santoni7.readme.activity.details.DetailsActivity;
import com.santoni7.readme.data.ImageRepository;
import com.santoni7.readme.data.Person;
import com.santoni7.readme.adapter.PersonRecyclerAdapter;
import com.santoni7.readme.R;
import com.santoni7.readme.data.PersonRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View, PersonRecyclerAdapter.OnItemClickListener {
    private final String TAG = MainActivity.class.getSimpleName();
    private MainPresenter presenter;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private PersonRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        initView();

        presenter = new MainPresenter();
        presenter.attachView(this);
        presenter.viewReady();
    }

    private void initView() {
        this.progressBar = findViewById(R.id.progressBar);

        //Init RecyclerView:
        this.recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerDecoration);
    }

    @Override
    public void showProgressOverlay() {
        Log.d(TAG, "showProgressOverlay");
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
    }

    @Override
    public void hideProgressOverlay() {
        Log.d(TAG, "hideProgressOverlay");
//        progressBar.setVisibility(View.GONE);
        //TODO
    }

    @Override
    public void displayPersonList(List<Person> people) {
        Log.d(TAG, "displayPersonList(): People count = " + people.size());
        adapter = new PersonRecyclerAdapter(people, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void addPerson(Person person) {
        if (adapter == null) {
            displayPersonList(new ArrayList<>());
        }
        adapter.addPerson(person);
    }

    @Override
    public void updatePerson(Person person) {
        if (adapter != null) {
            adapter.updateViewHolder(person);
        }
    }

    @Override
    public InputStream openAssetFile(String fileName) throws IOException {
        return getAssets().open(fileName);
    }

    @Override
    public void onItemClick(Person p) {
        presenter.onListItemClicked(p);
    }

    @Override
    public void openDetailsScreen(Person person) {
        Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
        i.putExtra(DetailsActivity.EXTRA_PERSON_ID, person.getId());
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    @Override
    public void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PersonRepository.instance().dispose();
        ImageRepository.instance().dispose();
    }
}
