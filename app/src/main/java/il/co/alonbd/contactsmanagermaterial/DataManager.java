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

    private Context context;
    private ArrayList<Contact> data;

    private DataManager(Context context) {
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
            example.setName("Israel");
            example.setPhone("Israeli");
            data.add(example);
            saveData();
        }
    }

    private void saveData() {
        synchronized (SYNCHRO) {
            Collections.sort(data);
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
            Collections.sort(data);
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

    public class AlphabetComparator implements Comparator<Contact> {
        boolean asc;
        public AlphabetComparator(boolean asc){
            this.asc = asc;
        }
        @Override
        public int compare(Contact o1, Contact o2) {
            int stringcompare = o1.getName().compareTo(o2.getName());
            if (asc = true){
                return  stringcompare;
            }
            else return stringcompare*-1;
        }
    }
}
