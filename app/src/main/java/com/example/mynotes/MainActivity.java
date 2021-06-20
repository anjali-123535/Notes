/*
 *
 *   Created Anjali Parihar on 20/6/21 7:45 PM
 *   Copyright Ⓒ 2021. All rights reserved Ⓒ 2021 http://freefuninfo.com/
 *   Last modified: 20/6/21 7:22 PM
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *   except in compliance with the License. You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENS... Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *    either express or implied. See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.example.mynotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.model.Adapter;
import com.example.mynotes.model.Note;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FloatingActionButton fab;
    Query query;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;
    FirebaseFirestore firestore;
    FirestoreRecyclerOptions<Note> notesList;
    CardView cardView;
    private  static final String TAG_NAME=MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        cardView=findViewById(R.id.cardview_item);
        recyclerView = findViewById(R.id.recycler);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);

        Log.d(TAG_NAME,"in OnCreate()");

         query = firestore.collection("notes").orderBy("created", Query.Direction.DESCENDING);
       // Query q=firestore.collection(("notes")
        notesList = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note.class).build();
        //fab.setVisibility(View.INVISIBLE);

        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setTitle("My Notes");
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(notesList) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull final Note note) {

                noteViewHolder.title.setText(note.getTitle());
                noteViewHolder.note.setText(note.getNote());
                noteViewHolder.created.setText("created: "+note.getCreated().substring(0,11)+" / ");
                noteViewHolder.updated.setText("updated: "+note.getUpdated().substring(0,11));

                //change the colour of cardview if the note is bookmarked
//                if(note.getMark())
//                    cardView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));

                final String id= notesList.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(view.getContext(), "holder is clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(view.getContext(), DetailsActivity.class);
                       // intent.putExtra("title", note.getTitle());
                        //intent.putExtra("note", note.getNote());
                        intent.putExtra("id",id);
                        view.getContext().startActivity(intent);
                    }
                });

                noteViewHolder.popup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu menu=new PopupMenu(view.getContext(),view);
                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                showDeleteConfirmationDialog(id);
                              //  DeleteNote();
                                return false;
                            }
                        });
                        menu.show();
                    }
                });
            }
            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
                return new NoteViewHolder(v);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);

        // FloatingAction Button, when clicked will navigate to AddActivity to create note
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("fab","null");
                startActivity(intent);
            }
        });
        Log.d(TAG_NAME,"onCreate is called");
    }

    //delete note confirmation dialog box
    void showDeleteConfirmationDialog(final String id)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Delete note?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DeleteNote(id);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                       if(dialogInterface!=null)
                           dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    //DeleteNote method get invoked when user selects delete option to delete the note
void DeleteNote(String id)
{
    DocumentReference reference=firestore.collection("notes").document(id);
    reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            //Toast.makeText(getApplicationContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            //Toast.makeText(getApplicationContext(), "DELETION FAILED", Toast.LENGTH_SHORT).show();
        }
    });
}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_mark:
               // query=firestore.collection("notes").whereEqualTo("bookmark",true).orderBy("updated");
                Log.d(TAG_NAME,"bookmark clicked");
                break;
            default:
              //  Toast.makeText(this, "item isclicked", Toast.LENGTH_SHORT).show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings)
            Toast.makeText(this, "setting sclicked", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, note,created,updated;
        View view;
        ImageView popup;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            note = itemView.findViewById(R.id.content);
            created=itemView.findViewById(R.id.created);
            updated=itemView.findViewById(R.id.updated);
            view = itemView;
            popup=itemView.findViewById(R.id.popup);

        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG_NAME,"onResume is called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG_NAME,"onRestartis called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG_NAME,"onDestroy is called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_NAME,"onStart is called");
        noteAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG_NAME,"onPause is called");
    }

    @Override
    protected void onStop() {
        super.onStop();
     Log.d(TAG_NAME,"onStop is called");
    if(noteAdapter!=null)
    {
        Toast.makeText(this, "noteAdapter is not null", Toast.LENGTH_SHORT).show();
        noteAdapter.stopListening();
    }
}
}
