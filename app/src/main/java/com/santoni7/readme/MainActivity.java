package com.santoni7.readme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerDecoration);

        List<Person> people = new ArrayList<>();
        people.add(new Person("1", "Toxa", "Sakhniuk", "http://url.com", 20));
        people.add(new Person("2", "Satoshi", "Nakamoto", "http://url.com", 40));
        people.add(new Person("3", "Craig", "Wright", "http://url.com", 50));

        PersonRecyclerAdapter adapter = new PersonRecyclerAdapter(people);
        recyclerView.setAdapter(adapter);
    }
}
