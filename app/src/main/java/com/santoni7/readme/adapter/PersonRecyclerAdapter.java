package com.santoni7.readme.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.santoni7.readme.R;
import com.santoni7.readme.data.Person;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PersonRecyclerAdapter extends RecyclerView.Adapter<PersonRecyclerAdapter.ViewHolder> {

    private List<Person> people;

    private OnItemClickListener clickListener;

    public PersonRecyclerAdapter(List<Person> people, OnItemClickListener clickListener) {
        this.people = people;
        this.clickListener = clickListener;
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
        viewHolder.bind(people.get(position), clickListener);
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

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
            Picasso.get()
                    .load(Uri.parse(person.getAvatarUrl()))
                    .resize(44, 44)
                    .centerCrop()
                    .into(imgAvatar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onItemClick(person);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Person p);
    }
}
