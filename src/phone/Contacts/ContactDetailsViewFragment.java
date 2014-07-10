package phone.Contacts;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ContactDetailsViewFragment extends Fragment {
    private ContactsLoader contactsLoader;
    private String contactType;
    private String displayName;
    private long contactId;

    public ContactDetailsViewFragment(String contactType, String displayName, long contactId) {
        this.contactType = contactType;
        this.displayName = displayName;
        this.contactId = contactId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.contactsLoader = new ContactsLoader(getActivity());
        showThumbnailPhoto(contactsLoader.getContactPhotoUri(contactId));
        showContactName(displayName);
        if (contactType == ContactsLoader.CONTACT_TYPE_MOBILE){
            showPhoneNumbers(contactsLoader.getMobilePhoneNumber(contactId));
        }
        else {
            showPhoneNumbers(contactsLoader.getSimPhoneNumber(displayName));
        }
    }

    private void showPhoneNumbers(List<String> phoneNumbers){
        for (String phoneNumber : phoneNumbers){
            showPhoneNumber(phoneNumber);
        }
    }

    private void showPhoneNumber(final String phoneNumber) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
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
        LinearLayout detailsLayout = (LinearLayout)getActivity().findViewById(R.id.details_view);
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
        TextView nameTextView = (TextView)getActivity().findViewById(R.id.contact_name_details);
        nameTextView.setText(contactName);
    }

    private void showThumbnailPhoto(Uri contactPhotoUri) {
        ImageView photoView = (ImageView) getActivity().findViewById(R.id.photo_details);
        if (contactPhotoUri != null) {
            photoView.setImageURI(contactPhotoUri);
        }
        else {
            photoView.setImageResource(R.drawable.ic_action_user);
        }
    }
}
