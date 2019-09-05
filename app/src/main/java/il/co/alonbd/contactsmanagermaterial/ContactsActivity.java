package il.co.alonbd.contactsmanagermaterial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

public class ContactsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout rootDrawer;
    public static final String CONTACT_EXTRA = "contact";
    public static final String INDEX_EXTRA = "index";
    DataManager dataManager;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        rootDrawer = findViewById(R.id.drawer_root);
        NavigationView nav = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recycler);
        dataManager = DataManager.getInstance(this);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_menu);

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
                                //TODO Check If Swipe Do Cancels
                                ((ContactsRecyclerAdapter) recyclerView.getAdapter()).dataChange(dataManager.getContacts());
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }
                        }
                    };
                    AlertDialog.Builder adb = new AlertDialog.Builder(ContactsActivity.this);
                    adb.setCancelable(true).setPositiveButton("Im Sure, Remove", ocl).setNegativeButton("Okay, Keep him in the list...", ocl)
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
            Intent intent = new Intent(this, AddContactActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.order_asc) {
            //TODO Change Order
        }
        if (menuItem.getItemId() == R.id.order_des) {
            //TODO Change Order
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        ((ContactsRecyclerAdapter) recyclerView.getAdapter()).dataChange(dataManager.getContacts());
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
