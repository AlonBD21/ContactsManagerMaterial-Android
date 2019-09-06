package il.co.alonbd.contactsmanagermaterial;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class ContactsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String CONTACT_EXTRA = "contact";
    public static final String INDEX_EXTRA = "index";
    DataManager dataManager;
    RecyclerView recyclerView;
    NavigationView nav;
    CoordinatorLayout coordLayout;
    DrawerLayout rootDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        rootDrawer = findViewById(R.id.drawer_root);
        nav = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recycler);
        dataManager = DataManager.getInstance(this);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        coordLayout = findViewById(R.id.coord_layout);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_menu);

        if (dataManager.getOrderAsc()){
            nav.setCheckedItem(R.id.order_asc);
        }else{
            nav.setCheckedItem(R.id.order_des);;
        }

        nav.setNavigationItemSelectedListener(this);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        recyclerView.setAdapter(new ContactsRecyclerAdapter(dataManager.getContacts(), new ContactsRecyclerAdapter.ContactClickListener() {
            @Override
            public void OnLongClick(int index) {
                Intent intent = new Intent(ContactsActivity.this, EditContactActivity.class);
                intent.putExtra(CONTACT_EXTRA, dataManager.getContactAt(index));
                intent.putExtra(INDEX_EXTRA, index);
                startActivity(intent);
            }

            public void OnClick(int index) {
                Intent intent = new Intent(ContactsActivity.this, ViewContactActivity.class);
                intent.putExtra(CONTACT_EXTRA, dataManager.getContactAt(index));
                intent.putExtra(INDEX_EXTRA, index);
                startActivity(intent);
            }
        }));

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
                if (i == ItemTouchHelper.START) {
                    final AlertDialog.OnClickListener ocl = new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int index = viewHolder.getAdapterPosition();
                            if (which == AlertDialog.BUTTON_POSITIVE) {
                                dataManager.removeContactAt(index);
                                ((ContactsRecyclerAdapter) recyclerView.getAdapter()).dataChange(dataManager.getContacts());
                                recyclerView.getAdapter().notifyItemRemoved(index);
                            } else {
                                ((ContactsRecyclerAdapter) recyclerView.getAdapter()).dataChange(dataManager.getContacts());
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }
                        }
                    };
                    AlertDialog.Builder adb = new AlertDialog.Builder(ContactsActivity.this);
                    adb.setCancelable(true).setIcon(R.drawable.ic_delete).setPositiveButton("Im Sure, Remove", ocl).setNegativeButton("Okay, Keep him in the list...", ocl)
                            .setMessage("Think twice, do you want to remove this contact?").setTitle("Removing A contact").setOnCancelListener(new AlertDialog.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            ocl.onClick(dialog, AlertDialog.BUTTON_NEUTRAL);
                        }
                    });
                    adb.show();

                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            rootDrawer.openDrawer(Gravity.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_add) {
            Intent intent = new Intent(ContactsActivity.this, AddContactActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.order_asc) {
            nav.setCheckedItem(R.id.order_asc);
            dataManager.sort(true);
            recyclerView.getAdapter().notifyDataSetChanged();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dataManager.sort(true);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }).run();
            Snackbar.make(coordLayout,"Order Changed",Snackbar.LENGTH_SHORT).show();
        }
        if (menuItem.getItemId() == R.id.order_des) {
            menuItem.setChecked(true);
            nav.setCheckedItem(R.id.order_des);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dataManager.sort(false);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }).run();
            Snackbar.make(coordLayout,"Order Changed",Snackbar.LENGTH_SHORT).show();

        }
        rootDrawer.closeDrawer(Gravity.START);
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        ((ContactsRecyclerAdapter) recyclerView.getAdapter()).dataChange(dataManager.getContacts());
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
