package il.co.alonbd.contactsmanagermaterial;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewContactActivity extends AppCompatActivity {
    private Contact contact;
    private Bitmap pic;
    ImageView imageView;

    TextView nameData;
    TextView phoneData;
    TextView mailData;
    TextView siteData;
    TextView addressData;
    TextView bdayData;
    TextView timeData;

    int PR_CALL = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PR_CALL){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewcontact);

        nameData = findViewById(R.id.name_et);
        phoneData = findViewById(R.id.phone_et);
        mailData = findViewById(R.id.mail_et);
        imageView = findViewById(R.id.pic_iv);

        contact = (Contact) getIntent().getExtras().get(ContactsActivity.CONTACT_EXTRA);
        pic = contact.getPic();
        if (pic != null) {
            imageView.setImageBitmap(pic);
        } else
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_person_outline_black_48dp));

        nameData.setText(contact.getName());
        phoneData.setText(contact.getPhone());
        mailData.setText(contact.getMail());


        phoneData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = ((TextView) v).getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        callPhone(txt);
                    }else{
                        requestPermissions(new String[] {Manifest.permission.CALL_PHONE},PR_CALL);
                    }
                }else{
                    callPhone(txt);
                }
            }
            private void callPhone(String txt){
                Uri uri = Uri.parse("tel://"+txt);
                Intent intent = new Intent(Intent.ACTION_CALL,uri);
                startActivity(intent);
            }
        });


    }
}
