package com.santoni7.readme.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.santoni7.readme.R;
import com.santoni7.readme.data.Person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class PersonRecyclerAdapter extends RecyclerView.Adapter<PersonRecyclerAdapter.ViewHolder> {
    private static final String TAG = PersonRecyclerAdapter.class.getSimpleName();

    private List<Person> people;

    private Map<String, ViewHolder> viewHolderById = new HashMap<>();
    private OnItemClickListener clickListener;

    private CompositeDisposable disposables = new CompositeDisposable();

    public PersonRecyclerAdapter(List<Person> peopleList, OnItemClickListener clickListener) {
        people = peopleList;
        this.clickListener = clickListener;
    }

    public void addPerson(Person p) {
        people.add(p);
        notifyDataSetChanged();
    }

    public void updatePerson(Person person) {
        ViewHolder vh = viewHolderById.get(person.getId());

        // Replace old Person object with new
        for (int i = 0; i < people.size(); ++i) {
            if (people.get(i).getId().equals(person.getId())) {
                people.set(i, person);
                break;
            }
        }
        if (vh != null) {
            vh.updateImage(person);
        } else {
            // todo
            Log.d(TAG, "updatePerson(id=" + person.getId() + "): ViewHolder is not found in map!");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Person p = people.get(position);
        viewHolder.bind(p, clickListener);
        viewHolderById.put(p.getId(), viewHolder);
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public void dispose() {
        disposables.dispose();
    }

    public interface OnItemClickListener {
        void onItemClick(Person p);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView txtName;
        private ImageView imgAvatar;
        private ProgressBar progressBar;
        private FrameLayout imageContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageContainer = itemView.findViewById(R.id.imageContainer);
            this.cardView = itemView.findViewById(R.id.card_view);
            this.txtName = itemView.findViewById(R.id.txtName);
            this.imgAvatar = itemView.findViewById(R.id.imgAvatar);
            this.progressBar = itemView.findViewById(R.id.progressBar);
        }

        void bind(final Person person, final OnItemClickListener clickListener) {
            String nameString = person.getFirstName() + " " + person.getSecondName();
            txtName.setText(nameString);

            updateImage(person);

            cardView.setOnClickListener(view -> {
                if (clickListener != null) {
                    clickListener.onItemClick(person);
                }
            });

        }

        void updateImage(Person person) {
            if (person == null) {
                Log.e(TAG, "updateImage: person = null");
                return;
            }
            if (person.getImageSource() != null) {
                Disposable d = person.getImageSource()
                        .subscribe(this::onBitmapReady,
                                err -> {
                                    //todo
                                    Log.e(TAG, "person.getImageSource produced error: " + err);
                                });
                disposables.add(d);
            }
        }

        private void onBitmapReady(Bitmap bitmap) {
            Log.d(TAG, "onBitmapReady");
            progressBar.setVisibility(View.GONE);
            imgAvatar.setImageBitmap(bitmap);
        }
    }
}
