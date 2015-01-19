package phone.Contacts;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ContactDetailsEditActivity extends Activity {
    private ContactsLoader contactsLoader;
    private Contact contact;

    public ContactDetailsEditActivity() {
        contactsLoader = new ContactsLoader(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.edit_details);
        Intent intent = getIntent();
        long contactId = intent.getLongExtra("contactId", 0);
        String displayName = intent.getStringExtra("displayName");
        String contactType = intent.getStringExtra("contactType");

        contact = contactsLoader.getContactFor(contactId, displayName, contactType);
        showThumbnailPhoto(contact.photoUri);
        showPhoneNumbers(contact.phoneNumber);
        showContactName(contact.displayName);

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_details_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showPhoneNumbers(List<String> phoneNumbers){
        for (String phoneNumber : phoneNumbers){
            showPhoneNumber(phoneNumber);
        }
    }

    private void showPhoneNumber(final String phoneNumber) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View contactContentView = inflater.inflate(R.layout.edit_contact_content, null);
        TextView phoneNumberTextView = (TextView)contactContentView.findViewById(R.id.edit_phone_number);
        phoneNumberTextView.setText(phoneNumber);
        LinearLayout detailsLayout = (LinearLayout)this.findViewById(R.id.edit_details);
        detailsLayout.addView(contactContentView);
    }

    private void showContactName(String contactName) {
        TextView nameTextView = (TextView)this.findViewById(R.id.edit_contact_name);
        nameTextView.setText(contactName);
    }

    private void showThumbnailPhoto(Uri contactPhotoUri) {
        ImageView photoView = (ImageView) this.findViewById(R.id.photo_details);
        if (contactPhotoUri != null) {
            photoView.setImageURI(contactPhotoUri);
        }
        else {
            photoView.setImageResource(R.drawable.ic_action_user);
        }
    }

    public void onEditDone(MenuItem item){

    }
}
