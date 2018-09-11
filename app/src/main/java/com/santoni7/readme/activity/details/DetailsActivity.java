package com.santoni7.readme.activity.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.santoni7.readme.R;
import com.santoni7.readme.data.PersonImageRepository;
import com.santoni7.readme.data.Person;

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

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        initView();

        Intent i = getIntent();
        String id = i.getStringExtra(EXTRA_PERSON_ID);
        if (id != null) {
            Disposable d = PersonImageRepository.instance().findPersonById(id).subscribe(
                    this::onPersonReceived,
                    err -> Toast.makeText(this, "Error: Could not find person!", Toast.LENGTH_SHORT).show()
            );
            subscriptions.add(d);
        }
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.details_activity_title);
        }

        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        imgAvatar = findViewById(R.id.imgAvatar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private void onPersonReceived(Person person) {
        txtName.setText(person.getFullName());
        String ageString = String.format(Locale.getDefault(), "Age: %d", person.getAge());
        txtAge.setText(ageString);

        Disposable subscription = person.getImageSource().singleOrError()
                .subscribe(imgAvatar::setImageBitmap, e -> Log.e(TAG, e.toString()));
        subscriptions.add(subscription);


        subscriptions.clear();
    }
}
