package com.santoni7.readme.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.santoni7.readme.R;
import com.santoni7.readme.data.ImageRepository;
import com.santoni7.readme.data.Person;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonRecyclerAdapter extends RecyclerView.Adapter<PersonRecyclerAdapter.ViewHolder> {
    private static final String TAG = PersonRecyclerAdapter.class.getSimpleName();

    private List<Person> people;

    private Map<String, ViewHolder> viewHolderById = new HashMap<>();
    private OnItemClickListener clickListener;

    public PersonRecyclerAdapter(List<Person> people, OnItemClickListener clickListener) {
        this.people = people;
        this.clickListener = clickListener;
    }

    public void addPerson(Person p){
        people.add(p);
        notifyDataSetChanged();
    }

    public void updateViewHolder(Person person){
        ViewHolder vh = viewHolderById.get(person.getId());
        if(vh != null){
            vh.updateImage(person);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
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


    public class ViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private TextView txtName;
        private ImageView imgAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.txtName = itemView.findViewById(R.id.txtName);
            this.imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }

        public void bind(final Person person, final OnItemClickListener clickListener) {
            String nameString = person.getFirstName() + " " + person.getSecondName();
            txtName.setText(nameString);
            //TODO: Replace with custom image processing

            updateImage(person);

            itemView.setOnClickListener(view -> {
                if (clickListener != null) {
                    clickListener.onItemClick(person);
                }
            });
        }

        public void updateImage(Person person) {
            String imgUrl = ImageRepository.instance().getImageUrl(person.getId());
            if(imgUrl != null) {
                Picasso.get()
                        .load(Uri.parse(imgUrl))
                        //.resize(44, 44)
                        .fit()
                        .centerCrop()
                        .into(imgAvatar);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Person p);
    }
}
