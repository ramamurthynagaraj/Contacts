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

public class ContactDetailsViewActivity extends Activity {
    private ContactsLoader contactsLoader;
    private Contact contact;

    public ContactDetailsViewActivity() {
        contactsLoader = new ContactsLoader(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.details_view);
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
        menuInflater.inflate(R.menu.contact_details_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
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
        showConfirmDialog();
    }

    private void showConfirmDialog() {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment(R.string.delete_contact_question, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean isSuccess = contactsLoader.delete(contact);
                if (isSuccess) {
                    Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
                    startActivity(intent);
                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirmDialogFragment.show(getFragmentManager(), "Confirm Delete Contact");
    }
}
