package com.santoni7.readme.activity.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.santoni7.readme.data.Person;
import com.santoni7.readme.adapter.PersonRecyclerAdapter;
import com.santoni7.readme.R;
import com.santoni7.readme.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View, PersonRecyclerAdapter.OnItemClickListener {
    private Logger log = new Logger(MainActivity.class.getSimpleName());

    private MainPresenter presenter;

    private RecyclerView recyclerView;
    private PersonRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.i("onCreate");
        setContentView(R.layout.activity_main);
        initView();

        presenter = new MainPresenter();
        presenter.attachView(this);
        presenter.viewReady();
    }

    private void initView() {
        //Init RecyclerView:
        this.recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerDecoration);
    }

    @Override
    public void showProgressOverlay() {
        log.i("showProgressOverlay");
        //TODO
    }

    @Override
    public void hideProgressOverlay() {
        log.i("hideProgressOverlay");

        //TODO
    }

    @Override
    public void displayPersonList(List<Person> people) {
        log.i("displayPersonList", "People count: " + people.size());
        adapter = new PersonRecyclerAdapter(people, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void addPerson(Person person) {
        if(adapter == null)
            displayPersonList(new ArrayList<>());
        adapter.addPerson(person);
    }

    @Override
    public void updatePerson(Person person) {
        adapter.updateViewHolder(person);
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
    public void showSnackbar(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
