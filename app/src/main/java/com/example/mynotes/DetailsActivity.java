package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynotes.model.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getName();
    TextView title,details,update;
    Intent data;
    ImageView mark0;
    Boolean mark=false,markIsChanged=false;
Toolbar toolbar;
FirebaseFirestore firestore;
String stitle,snote,id;
Map<String,Object>map;
MenuItem menushare;
    DocumentReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        title=findViewById(R.id.content_title);
        details=findViewById(R.id.content_details);
        update=findViewById(R.id.updated_details);
        data=getIntent();
        id=data.getStringExtra("id");
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      //  mark0=(ImageView)findViewById(R.id.star);
        // toolbar.setTitle("jdfgrt");
        //toolbar.setLogo(R.drawable.ic_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        firestore=FirebaseFirestore.getInstance();
         reference=firestore.collection("notes").document(id);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null) {
                     Log.d(TAG, e.getMessage());
                return;
                }
                if(documentSnapshot!=null  && documentSnapshot.exists())
                {
                    map=documentSnapshot.getData();
                   // stitle=documentSnapshot.getString("title");
                    //snote=documentSnapshot.getString("note");
                    //mark=documentSnapshot.getBoolean("mark");
                    title.setText(map.get("title").toString());
                    details.setText(map.get("note").toString());
                    update.setText("updated: "+map.get("updated").toString());
                    mark=(Boolean)map.get("bookmark");
                   invalidateOptionsMenu();
                }
                else
                    Log.d(TAG,"current data is null for"+data.getStringExtra("id"));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.details_menu,menu);
            Log.d(TAG,"inOncreateOPtionsMenu()");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG,"in onPrepareOptionsMenu()");
        MenuItem menustar = menu.findItem(R.id.star);

        menushare=menu.findItem(R.id.note_share);
        menustar.setEnabled(false).setVisible(false);
        if (markIsChanged) {
            if (mark) {
                menustar.setIcon(R.drawable.ic_star_blank).setEnabled(true).setVisible(true);
                mark = false;
                Log.d(TAG,"mark is changed and mark was true now it is set to false");
            } else {
                menustar.setIcon(R.drawable.ic_star_black).setEnabled(true).setVisible(true);
                 mark = true;
                Log.d(TAG,"mark is changed and mark was false now it is set to true");
            }
            markIsChanged=false;
            UpdateMark();
        } else {
            if (mark) {
             Log.d(TAG,"mark is not changed and mark is true and set to black");
            menustar.setIcon(R.drawable.ic_star_black).setEnabled(true).setVisible(true);

            }else {
                menustar.setIcon(R.drawable.ic_star_blank).setEnabled(true).setVisible(true);
                Log.d(TAG,"mark is not changed and mark is false and set to blank");
            }
            }
            return super.onPrepareOptionsMenu(menu);
        }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG,"inOptionsItemSelected");
        switch(item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.edit:
                Intent intent=new Intent(this,AddActivity.class);
                intent.putExtra("title",map.get("title").toString());
                intent.putExtra("note",map.get("note").toString());
                intent.putExtra("id",id);
                startActivity(intent);
                break;
            case R.id.star:
                markIsChanged=true;
                invalidateOptionsMenu();
                break;
              //  UpdateMark();
            case R.id.note_share:
                Sharenote(map);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    void Sharenote(Map<String,Object>map)
    {
Intent share=new Intent(Intent.ACTION_SEND);
share.setType("text/plain");
share.putExtra(Intent.EXTRA_SUBJECT,String.valueOf(map.get("title")));
        share.putExtra(Intent.EXTRA_TEXT,String.valueOf(map.get("note"))+"\n\n" +
                "             Created:  "+String.valueOf(map.get("created")));
        startActivity(Intent.createChooser(share,"sharing note"));
    }
    void UpdateMark()
    {
        Toast.makeText(this,"in UpdateMark",Toast.LENGTH_SHORT).show();
            map.put("bookmark",mark);
            reference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG,"mark is udpated"+mark);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,"mark is not udpated"+mark);
                }
            });
    }
}
