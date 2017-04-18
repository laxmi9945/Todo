package com.app.todo.ui;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.baseclass.BaseActivity;
import com.app.todo.database.DataBaseUtility;
import com.app.todo.fragment.AboutFragment;
import com.app.todo.fragment.DummyFragment;
import com.app.todo.model.NotesModel;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.LENGTH_SHORT;


public class TodoNotesActivity extends BaseActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    boolean isListView = false;
    RecyclerAdapter recyclerAdapter;
    SharedPreferences sharedPreferences;
    CardView cardView;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    private Menu menu;
    DataBaseUtility dataBaseUtility;
    AppCompatTextView titleTextView, dateTextview, contentTextview;
    CircleImageView circleImageView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    List<NotesModel> notesModels;
    public FloatingActionButton floatingActionButton;
    Context context;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawerlayout);
        //Getting reference to Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        initView();
        initSwipeView();
        setSupportActionBar(toolbar);
        dataBaseUtility = new DataBaseUtility(this);
        notesModels = dataBaseUtility.getDatafromDB();
        toolbar.setVisibility(View.VISIBLE);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        /*recyclerAdapter = new RecyclerAdapter(this, notesModels, this);
        recyclerView.setAdapter(recyclerAdapter);*/
        sharedPreferences = getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        floatingActionButton.setVisibility(View.VISIBLE);
        notesModels = new ArrayList<NotesModel>();
        progressDialog = new ProgressDialog(this);




       /* ImageView circularImageView = (ImageView)findViewById(R.id.imageView);
        circularImageView.setImageBitmap(circularBitmap);*/
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<NotesModel>> t = new GenericTypeIndicator<ArrayList<NotesModel>>() {
                };
                //ArrayList<NotesModel> fsf=new ArrayList<>();
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    NotesModel fsf = post.getValue(NotesModel.class);
                    notesModels.add(fsf);
                }

                recyclerAdapter = new RecyclerAdapter(getApplicationContext(), notesModels);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error while fetching data" + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
       /* databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getAllTask(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getAllTask(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                taskDeletion(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && floatingActionButton.isShown()) {
                    floatingActionButton.hide();
                }
                //super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    floatingActionButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }


        });

    }
    /*private void getAllTask(DataSnapshot dataSnapshot){
        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
            String taskTitle = singleSnapshot.getValue(String.class);
            notesModels.add(new NotesModel(taskTitle));
            recyclerAdapter = new RecyclerAdapter(getApplicationContext(), taskTitle);
            recyclerView.setAdapter(recyclerAdapter);
        }
    }
    private void taskDeletion(DataSnapshot dataSnapshot){
        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            String taskTitle = singleSnapshot.getValue(String.class);
            for(int i = 0; i < notesModels.size(); i++){
                if(notesModels.get(i).getTitle().equals(taskTitle)){
                    notesModels.remove(i);
                }
            }

            recyclerAdapter.notifyDataSetChanged();
            recyclerAdapter = new RecyclerAdapter(getApplicationContext(), taskTitle);
            recyclerView.setAdapter(recyclerAdapter);
        }
    }*/


    @Override
    public void onBackPressed() {
        if (floatingActionButton.getVisibility() == View.GONE)
            floatingActionButton.setVisibility(View.VISIBLE);
        new AlertDialog.Builder(this)
                .setMessage("Close app?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TodoNotesActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void initSwipeView() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query applesQuery = ref.orderByChild("task").equalTo(taskTitle);
                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                appleSnapshot.getRef().removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());
                        }
                    });*/
                    dataBaseUtility.delete(notesModels.get(position));
                    recyclerAdapter.deleteItem(position);


                } else {

                    recyclerAdapter.archiveItem(position);
                    recyclerView.setAdapter(recyclerAdapter);
                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    @Override
    public void initView() {

        View view = getLayoutInflater().inflate(R.layout.activity_todonotes, null, false);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_button);
        recyclerView = (RecyclerView) findViewById(R.id.myrecyclerView);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        titleTextView = (AppCompatTextView) view.findViewById(R.id.title_TextView);
        dateTextview = (AppCompatTextView) view.findViewById(R.id.date_TextView);
        contentTextview = (AppCompatTextView) view.findViewById(R.id.content_TextView);
        cardView = (CardView) view.findViewById(R.id.myCardView);

        // circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        setClicklistener();
    }

    @Override
    public void setClicklistener() {
        floatingActionButton.setOnClickListener(this);
        //circleImageView.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_notes_action, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.changeview) {
            toggle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        MenuItem item = menu.findItem(R.id.changeview);
        if (!isListView) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            item.setIcon(R.drawable.listbutton);
            isListView = true;
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            item.setIcon(R.drawable.gridbutton);
            isListView = false;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String titleData = "";
        String contentData = "";
        String recentTimeData = "";

        if (resultCode == RESULT_OK) {

            Bundle bundle = data.getBundleExtra("bundle");
            if (bundle != null) {
                Log.i("g", "onActivityResult: ");
                titleData = bundle.getString("Title_data");
                contentData = bundle.getString("Content_data");
                recentTimeData = bundle.getString("date_data");
            }
            NotesModel note = new NotesModel();
            note.setTitle(titleData);
            note.setContent(contentData);
            note.setDate(recentTimeData);
            Log.i("abc", "onActivityResult: ");
            recyclerAdapter.addNotes(note);
            recyclerAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(recyclerAdapter);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab_button:

                Intent intent = new Intent(this, NotesAddActivity.class);
                startActivityForResult(intent, 2);


                break;
            case R.id.profile_image:
                //profilePictureSet();


                break;
        }
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        if (floatingActionButton.getVisibility() == View.GONE)
            floatingActionButton.setVisibility(View.VISIBLE);
    }*/
    private void profilePictureSet() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
// tells your intent to get the contents
// opens the URI for your image directory on your sdcard
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notes:
                Toast.makeText(this, "Notes", LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.myframe_layout, new DummyFragment()).
                        addToBackStack(null).commit();
                drawer.closeDrawers();
                break;
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                /*SharedPreferences.Editor shEditor = sharedPreferences.edit();
                if (sharedPreferences.contains("login")) {

                    Log.i("check", "onOptionsItemSelected: ");
                    shEditor.putString("login", "false");
                    shEditor.commit();
                    Intent intent = new Intent(TodoNotesActivity.this, LoginActivity.class);
                    Toast.makeText(TodoNotesActivity.this, getString(R.string.logout), LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();

                }*/
                //Toast.makeText(TodoNotesActivity.this, getString(R.string.logout_success), LENGTH_SHORT).show();
                break;
            case R.id.reminder:

                Toast.makeText(this, getString(R.string.logic), LENGTH_SHORT).show();

                break;

            case R.id.archive:
                Toast.makeText(this, getString(R.string.logic), LENGTH_SHORT).show();

                break;
            case R.id.about:

                getSupportFragmentManager().beginTransaction().replace(R.id.myframe_layout, new AboutFragment()).
                        addToBackStack(null).commit();
                setTitle("About");
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;
        }

        return true;
    }

    /*public void updateNote(NotesModel notesModel) {
        recyclerAdapter.addNotes(notesModel);
        recyclerView.setAdapter(recyclerAdapter);
    }*/

    public void setData(NotesModel notesModel) {
        recyclerAdapter.addNotes(notesModel);
        recyclerView.setAdapter(recyclerAdapter);
    }


}
