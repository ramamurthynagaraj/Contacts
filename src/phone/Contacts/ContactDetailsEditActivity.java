package phone.Contacts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
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
        showPhoneNumbers(contact.phoneNumbers);
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

    private List<String> getPhoneNumbers(){
        ArrayList<String> phoneNumbers = new ArrayList<String>();
        ArrayList<View> editedPhoneNumberViews = getViewsByTag((ViewGroup) this.findViewById(R.id.edit_details), "edited_phone_number");
        for (View view : editedPhoneNumberViews){
            phoneNumbers.add(((TextView) view).getText().toString());
        }
        return phoneNumbers;
    }

    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
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

    private String getContactName() {
        TextView nameTextView = (TextView)this.findViewById(R.id.edit_contact_name);
        return nameTextView.getText().toString();
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
        List<String> phoneNumbers = getPhoneNumbers();
        String contactName = getContactName();
        contact.displayName = contactName;
        contact.phoneNumbers = phoneNumbers;
        contactsLoader.save(contact);
    }
}
