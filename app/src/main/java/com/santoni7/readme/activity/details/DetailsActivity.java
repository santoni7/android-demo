package com.santoni7.readme.activity.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.santoni7.readme.R;
import com.santoni7.readme.data.Person;
import com.santoni7.readme.data.PersonRepository;

import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_PERSON_ID = "person_id";
    private static final String TAG = DetailsActivity.class.getSimpleName();

    CompositeDisposable subscriptions = new CompositeDisposable();

    TextView txtName;
    TextView txtAge;
    ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initView();

        Intent i = getIntent();
        String id = i.getStringExtra(EXTRA_PERSON_ID);
        if(id != null){
            Disposable d = PersonRepository.instance().findPersonById(id).subscribe(
                    this::onPersonReceived,
                    err -> Toast.makeText(this, "Error: Could not find person!", Toast.LENGTH_SHORT).show()
            );
            subscriptions.add(d);
        }
    }

    private void initView() {
        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        imgAvatar = findViewById(R.id.imgAvatar);
    }

    private void onPersonReceived(Person person){
        txtName.setText(person.getFullName());
        String ageString = String.format(Locale.getDefault(), "Age: %d", person.getAge());
        txtAge.setText(ageString);




        subscriptions.clear();
    }
}
