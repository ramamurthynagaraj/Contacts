package phone.Contacts;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener  {

    private static final String[] CONTACT_DETAILS_PROJECTION = new String[]{
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Contacts.DISPLAY_NAME_PRIMARY
    };
    private static final int CONTACTS_ID_INDEX = 0;
    private static final int CONTACTS_LOOKUP_KEY_INDEX = 1;
    private static final int CONTACTS_DISPLAY_NAME_INDEX = 2;

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
        getLoaderManager().initLoader(0,null,this);
    }

    private void initializeEmptyContactsList() {
        contactsListView = (ListView) getActivity().findViewById(android.R.id.list);
        cursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_view,
                null,
                new String[] { CONTACT_DETAILS_PROJECTION[CONTACTS_DISPLAY_NAME_INDEX] },
                new int[] { android.R.id.text1 },
                0);
        contactsListView.setAdapter(cursorAdapter);
        contactsListView.setOnItemClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getActivity());
        cursorLoader.setUri(Contacts.CONTENT_URI);
        cursorLoader.setProjection(CONTACT_DETAILS_PROJECTION);
        cursorLoader.setSortOrder(CONTACT_DETAILS_PROJECTION[CONTACTS_DISPLAY_NAME_INDEX]);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateContactsListItems(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateContactsListItems(null);
    }

    private void updateContactsListItems(Cursor contactItemsToBeReplaced){
        cursorAdapter.swapCursor(contactItemsToBeReplaced);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) parent.getAdapter();
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);
        long contactId = cursor.getLong(CONTACTS_ID_INDEX);
        String contactLookUpKey = cursor.getString(CONTACTS_LOOKUP_KEY_INDEX);
        Uri contactLookupUri = Contacts.getLookupUri(contactId, contactLookUpKey);
        QuickContact.showQuickContact(getActivity(),getActivity().findViewById(android.R.id.list),contactLookupUri,QuickContact.MODE_LARGE,null);
    }
}
