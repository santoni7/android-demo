package com.santoni7.readme;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PersonRecyclerAdapter extends RecyclerView.Adapter<PersonRecyclerAdapter.ViewHolder> {

    private List<Person> people;

    public PersonRecyclerAdapter(List<Person> people){
        this.people = people;
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
        viewHolder.showData(people.get(position));
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtName;
//        private TextView tvSecondName;
        private ImageView imgAvatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtName = itemView.findViewById(R.id.txtName);
            this.imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
        public void showData(Person person){
            txtName.setText(person.getFirstName() + " " + person.getSecondName());
            //TODO: load image from web
        }
    }
}
