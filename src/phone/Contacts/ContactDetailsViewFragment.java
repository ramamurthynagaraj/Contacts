package phone.Contacts;

import android.app.Fragment;
import android.app.Notification;
import android.content.Intent;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        setThumbnailPhoto(contactsLoader.getContactPhotoUri(contactId));
        setContactName(displayName);
        if (contactType == ContactsLoader.CONTACT_TYPE_MOBILE){
            setPhoneNumber(contactsLoader.getMobilePhoneNumber(contactId));
        }
        else {
            setPhoneNumber(contactsLoader.getSimPhoneNumber(displayName));
        }
    }

    private void setPhoneNumber(final String phoneNumber) {
        TextView phoneNumberTextView = (TextView)getActivity().findViewById(R.id.phone_number_details);
        phoneNumberTextView.setText(phoneNumber);
        phoneNumberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCall = new Intent(Intent.ACTION_CALL);
                intentCall.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intentCall);
            }
        });

    }

    private void setContactName(String contactName) {
        TextView nameTextView = (TextView)getActivity().findViewById(R.id.contact_name_details);
        nameTextView.setText(contactName);
    }

    private void setThumbnailPhoto(Uri contactPhotoUri) {
        ImageView photoView = (ImageView) getActivity().findViewById(R.id.photo_details);
        if (contactPhotoUri != null) {
            photoView.setImageURI(contactPhotoUri);
        }
        else {
            photoView.setImageResource(R.drawable.ic_action_user);
        }
    }
}
