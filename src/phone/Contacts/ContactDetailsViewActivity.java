package phone.Contacts;

import android.app.Activity;
import android.app.Fragment;
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

public class ContactDetailsViewActivity extends Activity {
    private ContactsLoader contactsLoader;
    private String contactType;
    private String displayName;
    private long contactId;

    public ContactDetailsViewActivity() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.details_view);
        Intent intent = getIntent();
        this.contactId = intent.getLongExtra("contactId", 0);
        this.displayName = intent.getStringExtra("displayName");
        this.contactType = intent.getStringExtra("contactType");

        this.contactsLoader = new ContactsLoader(this);
        showThumbnailPhoto(contactsLoader.getContactPhotoUri(contactId));
        showContactName(displayName);
        if (ContactsLoader.CONTACT_TYPE_MOBILE.equalsIgnoreCase(contactType)){
            showPhoneNumbers(contactsLoader.getMobilePhoneNumber(contactId));
        }
        else {
            showPhoneNumbers(contactsLoader.getSimPhoneNumber(displayName));
        }
        super.onCreate(savedInstanceState);
    }

    private void showPhoneNumbers(List<String> phoneNumbers){
        for (String phoneNumber : phoneNumbers){
            showPhoneNumber(phoneNumber);
        }
    }

    private void showPhoneNumber(final String phoneNumber) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View contactContentView = inflater.inflate(R.layout.contact_content_view, null);
        TextView phoneNumberTextView = (TextView)contactContentView.findViewById(R.id.phone_number_details);
        phoneNumberTextView.setText(phoneNumber);
        showThemedImage(R.drawable.ic_action_call, R.id.ic_action_call, contactContentView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(phoneNumber);
            }
        });
        showThemedImage(R.drawable.ic_action_mail, R.id.ic_action_mail, contactContentView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message(phoneNumber);
            }
        });
        LinearLayout detailsLayout = (LinearLayout)this.findViewById(R.id.details_view);
        detailsLayout.addView(contactContentView);
    }

    private void message(String phoneNumber) {
        Intent intentCall = new Intent(Intent.ACTION_SENDTO);
        intentCall.setData(Uri.parse("sms:" + phoneNumber));
        startActivity(intentCall);
    }

    private void call(String phoneNumber) {
        Intent intentCall = new Intent(Intent.ACTION_CALL);
        intentCall.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intentCall);
    }

    private void showThemedImage(int drawableId, int imageViewId, View parentView, View.OnClickListener clickListener){
        Drawable drawable = getResources().getDrawable(drawableId);
        drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        ImageView imageView = (ImageView)parentView.findViewById(imageViewId);
        imageView.setImageDrawable(drawable);
        imageView.setOnClickListener(clickListener);
    }

    private void showContactName(String contactName) {
        TextView nameTextView = (TextView)this.findViewById(R.id.contact_name_details);
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

    public void onDeleteAction(MenuItem item) {

    }
}
