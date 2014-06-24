package phone.Contacts;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.QuickContact;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactsListViewFragment
        extends Fragment
        implements ContactsLoaderCallback,
        AdapterView.OnItemClickListener  {
    private SimpleCursorAdapter cursorAdapter;
    private ListView contactsListView;

    public ContactsListViewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeEmptyContactsList();
        startLoadingContactsInBackground();
    }

    private void startLoadingContactsInBackground() {
        ContactsLoader contactsLoader = new ContactsLoader(getActivity(), this);
        getLoaderManager().initLoader(0, null, contactsLoader);

    }

    private void initializeEmptyContactsList() {
        contactsListView = (ListView) getActivity().findViewById(android.R.id.list);
        cursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_view,
                null,
                new String[] { ContactsLoader.DISPLAY_NAME, ContactsLoader.CONTACT_TYPE },
                new int[] { R.id.contact_name, R.id.contact_type },
                0);
        contactsListView.setAdapter(cursorAdapter);
        contactsListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) parent.getAdapter();
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);
        String contact_type = cursor.getString(ContactsLoader.CONTACT_TYPE_INDEX);
        if (contact_type == ContactsLoader.CONTACT_TYPE_MOBILE) {
            long contactId = cursor.getLong(ContactsLoader.ID_INDEX);
            String contactLookUpKey = cursor.getString(ContactsLoader.LOOKUP_KEY_INDEX);
            Uri contactLookupUri = Contacts.getLookupUri(contactId, contactLookUpKey);
            QuickContact.showQuickContact(getActivity(), getActivity().findViewById(android.R.id.list), contactLookupUri, QuickContact.MODE_LARGE, null);
        }
    }

    public void getAllRawContactsFor(long contactId){
        String whereCondition = RawContacts.CONTACT_ID + "=?";
        Cursor rawContacts = getActivity().getContentResolver()
                .query(RawContacts.CONTENT_URI,
                        new String[]{ RawContacts._ID, RawContacts.ACCOUNT_TYPE, RawContacts.ACCOUNT_NAME},
                        whereCondition,
                        new String[]{ String.valueOf(contactId) },
                        null);
        rawContacts.moveToFirst();
        String accountType = rawContacts.getString(1);
        String accountName = rawContacts.getString(2);
        while(rawContacts.moveToNext()){
            accountType +=  "," + rawContacts.getString(1);
            accountName +=  "," + rawContacts.getString(2);
        }
    }

    @Override
    public void onContactsLoaded(Cursor contactsCursor) {
        cursorAdapter.swapCursor(contactsCursor);
    }
}
