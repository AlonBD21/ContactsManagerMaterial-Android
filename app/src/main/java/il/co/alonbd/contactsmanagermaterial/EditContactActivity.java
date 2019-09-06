package il.co.alonbd.contactsmanagermaterial;

import android.graphics.BitmapFactory;
import android.os.Bundle;

public class EditContactActivity extends AddContactActivity {
    private Contact contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Super Method Already Contains setContentView()

        contact = (Contact) getIntent().getExtras().get(ContactsActivity.CONTACT_EXTRA);

        pic = contact.getPic();
        if (pic != null){
            imageView.setImageBitmap(pic);
        }
        else imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.baseline_person_outline_black_48dp));

        nameData.setText(contact.getName());
        phoneData.setText(contact.getPhone());
        mailData.setText(contact.getMail());


    }
}
