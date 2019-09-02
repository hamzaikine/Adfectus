package io.github.hamzaikine.loginex;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

    ArrayList<Person> persons;
    Context context;
    RVAdapter(Context context, ArrayList<Person> persons){
        this.context = context;
        this.persons = persons;
    }


    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder personViewHolder, int i) {
        personViewHolder.personEmotion.setText("Emotions: "+persons.get(i).emotion);
        personViewHolder.personAge.setText("Age: "+persons.get(i).age);
        personViewHolder.personGender.setText("Gender: "+persons.get(i).gender);
        personViewHolder.dateId.setText("Date: "+persons.get(i).id);
        personViewHolder.personPhoto.setImageBitmap(BitmapFactory.decodeFile(persons.get(i).photoId));

    }

    @Override
    public int getItemCount() {
        return persons.size();
    }


    public void removeItem(int position) {
        persons.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Person person, int position) {
        persons.add(position, person);
        // notify item added by position
        notifyItemInserted(position);
    }

    public  class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        public RelativeLayout viewBackground, viewForeground;
        TextView personEmotion;
        TextView personAge;
        ImageView personPhoto;
        TextView dateId;
        TextView personGender;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.card_view);
            cv.setUseCompatPadding(true);
            personEmotion = itemView.findViewById(R.id.person_emotion);
            personAge = itemView.findViewById(R.id.person_age);
            personPhoto = itemView.findViewById(R.id.person_photo);
            personGender = itemView.findViewById(R.id.person_gender);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            dateId = itemView.findViewById(R.id.date_id);
        }


    }

}
