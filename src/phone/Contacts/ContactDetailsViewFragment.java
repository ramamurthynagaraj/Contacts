package phone.Contacts;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContactDetailsViewFragment extends Fragment {
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
        TextView detailsTextView = (TextView)getActivity().findViewById(R.id.details_phone_number);
        detailsTextView.setText(contactType +" : "+displayName+" : "+contactId);
    }
}
