package il.co.alonbd.contactsmanagermaterial;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class DataManager {
    private static DataManager instance;
    private final static Object SYNCHRO = new Object();
    private final static String FILE = "ContactsArrayList2";
    private boolean orderAsc;

    public boolean getOrderAsc() {
        return orderAsc;
    }

    private Context context;
    private ArrayList<Contact> data;

    private DataManager(Context context) {
        orderAsc = true;
        this.context = context;
        data = new ArrayList<>();

        synchronized (SYNCHRO) {
            try {
                FileInputStream fis = context.openFileInput(FILE);
                ObjectInputStream ois = new ObjectInputStream(fis);
                data = (ArrayList<Contact>) ois.readObject();
                ois.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (data.size() == 0) {
            Contact example = new Contact();
            example.setName("Travis Scott");
            example.setPhone("Tap to view, Hold to edit, Swipe to delete");
            data.add(example);
            saveData();
        }
    }

    private void saveData() {
        synchronized (SYNCHRO) {
            sort(orderAsc);
            try {
                FileOutputStream fos = context.openFileOutput(FILE, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadData() {
        synchronized (SYNCHRO) {
            try {
                FileInputStream fis = context.openFileInput(FILE);
                ObjectInputStream ois = new ObjectInputStream(fis);
                data = (ArrayList<Contact>) ois.readObject();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            sort(orderAsc);
        }
    }

    //Public Functions
    public static DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    public void addContact(Contact contact) {
        loadData();
        data.add(contact);
        saveData();
    }

    public void addContactAt(int index, Contact contact) {
        loadData();
        data.add(index, contact);
        saveData();
    }

    public void replaceContact(Contact contact, int index) {
        loadData();
        removeContactAt(index);
        addContactAt(index, contact);
        saveData();
    }

    public Contact getContactAt(int index) {
        loadData();
        return data.get(index);
    }

    public ArrayList<Contact> getContacts() {
        loadData();
        return data;
    }

    public void removeContactAt(int index) {
        loadData();
        data.remove(index);
        saveData();
    }
    public void sort(boolean asc){
        orderAsc = asc;
        Collections.sort(data, new Contact.AlphabetComparator(orderAsc));
    }
}
