package com.example.mynotes.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.DetailsActivity;
import com.example.mynotes.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<String>titles,details;
    public Adapter(List<String>titles,List<String>details)
    {
        this.details=details;
        this.titles=titles;
    }
    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate( R.layout.card_item,parent,false);
        return new ViewHolder(v);
    }
class ViewHolder extends RecyclerView.ViewHolder{
TextView title,detail;
    View view;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        title=itemView.findViewById(R.id.card_title);
        detail=itemView.findViewById(R.id.content);
         view=itemView;
    }
}
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
holder.title.setText(titles.get(position));
holder.detail.setText(details.get(position));
holder.view.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(),"holder is clicked",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(view.getContext(), DetailsActivity.class);
        intent.putExtra("title",titles.get(position));
        intent.putExtra("content",details.get(position));
        view.getContext().startActivity(intent);
    }
});
    }

    @Override
    public int getItemCount() {
        return 14;
    }
}
