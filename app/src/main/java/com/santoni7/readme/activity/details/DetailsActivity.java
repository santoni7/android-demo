package com.santoni7.readme.activity.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.santoni7.readme.MyApplication;
import com.santoni7.readme.R;
import com.santoni7.readme.common.OnSwipeTouchListener;
import com.santoni7.readme.dagger.MyComponent;
import com.santoni7.readme.data.Person;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class DetailsActivity extends AppCompatActivity implements DetailsContract.View {

    public static final String EXTRA_PERSON_ID = "person_id";
    private static final String TAG = DetailsActivity.class.getSimpleName();

    private DetailsPresenter presenter;

    private CompositeDisposable disposables = new CompositeDisposable();

    TextView txtName;
    TextView txtAge;
    ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        presenter = new DetailsPresenter();
        presenter.attachView(this);

        initView();

        MyComponent component = ((MyApplication)getApplication()).getComponent();
        presenter.init(component);
        presenter.viewReady();
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

        findViewById(R.id.constraintLayout).setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                finish();
            }
        });
    }

    @Override
    public void displayPerson(Person person) {
        txtName.setText(person.getFullName());
        txtAge.setText(getString(R.string.age_string_format, person.getAge()));
        Disposable d = person.getImageSource().subscribe(imgAvatar::setImageBitmap);
        disposables.add(d);
    }


    @Override
    public Single<String> getPersonIdExtra() {
        Intent i = getIntent();
        String id = i.getStringExtra(EXTRA_PERSON_ID);
        return Observable.just(id).singleOrError();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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
    protected void onStop() {
        super.onStop();
        disposables.clear();
        presenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
