package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.arch.core.executor.DefaultTaskExecutor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {
Toolbar toolbar;
EditText title,note;
FirebaseFirestore firestore;
    Intent data;
    private Boolean contentChanged=false;
    View.OnTouchListener touchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            contentChanged=true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
       setUIViews();
        firestore=FirebaseFirestore.getInstance();
       toolbar=findViewById(R.id.toolbar);
       //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayShowTitleEnabled(false);
     //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         data=getIntent();
        if(!data.hasExtra("fab"))
        {
            Toast.makeText(this,"this is from edit activity",Toast.LENGTH_SHORT).show();
        DisplayData();
        }
    }
void setUIViews()
{
    title=findViewById(R.id.add_title);
    note=findViewById(R.id.add_note);
    title.setOnTouchListener(touchListener);
    note.setOnTouchListener(touchListener);
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater=getMenuInflater();
       inflater.inflate(R.menu.add_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.save:
                if(data.hasExtra("fab")) {
                    save();
                }
                else
                    update();
                break;
            case R.id.cancel:
                if(contentChanged) {
                    if(data.hasExtra("fab"))
                    {
                        if(title.getText().toString().isEmpty() && note.getText().toString().isEmpty()) {
                            finish();
                        return true;
                        }
                    }
                    showUnSavedChangesDialog();
                }
                else
                    finish();
        }
        return super.onOptionsItemSelected(item);
    }
    void DisplayData()
    {
        title.setText(data.getStringExtra("title"));
        note.setText(data.getStringExtra("note"));
    }
    void save()
    {
        saveNote();
        finish();
    }
    void update()
    {
        updataNote();
        finish();
    }

    @Override
    public void onBackPressed() {
        if(contentChanged)
        showUnSavedChangesDialog();
        else
            super.onBackPressed();
      //  super.onBackPressed();
    }

    void showUnSavedChangesDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Save your changes or discard them?");
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(data.hasExtra("fab"))
                save();
                else
                    update();
            }
        });
        builder.setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(data.hasExtra("fab"))
           startActivity(new Intent(getApplicationContext(),MainActivity.class));
            else
                finish();
            }
        });
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface!=null)
                    dialogInterface.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    void saveNote() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String sdate = dateFormat.format(date);
        String stitle = title.getText().toString();
        String snote = note.getText().toString();
        if (!stitle.isEmpty() ||  !snote.isEmpty())
        {
            if (stitle.isEmpty())
                stitle = "<Untitled>";
            if (snote.isEmpty())
                snote = "";
            DocumentReference reference = firestore.collection("notes").document();
            //String id=reference.getId();
            Map<String, Object> note = new HashMap<>();
            note.put("title", stitle);
            note.put("note", snote);
            note.put("created", sdate);
            note.put("updated", sdate);
            note.put("bookmark",false);

            reference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Task Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    void updataNote()
    {
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date updateddate=new Date();
        String sdate=dateFormat.format(updateddate);
        String stitle=title.getText().toString();
        String snote=note.getText().toString();

        DocumentReference reference=firestore.collection("notes").document(data.getStringExtra("id"));
        if(stitle.isEmpty() && snote.isEmpty())
        {
            reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"Deleted the note bcuse tittle and note both are empty",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Failed to Delete the note in which tittle and note both are empty",Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        if (stitle.isEmpty())
            stitle = "<Untitled>";
        Map<String,Object> map=new HashMap<>();
        map.put("title",stitle);
        map.put("note",snote);
        map.put("updated",sdate);
        reference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"update Task Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
