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
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener  {

    private static final String[] CONTACT_DETAILS_PROJECTION = new String[]{
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Contacts.DISPLAY_NAME_PRIMARY,
            Contacts.HAS_PHONE_NUMBER,
            "CONTACT_TYPE"
    };
    private static final int ID_INDEX = 0;
    private static final int LOOKUP_KEY_INDEX = 1;
    private static final int DISPLAY_NAME_INDEX = 2;
    private static final int HAS_PHONE_NUMBER_INDEX = 3;
    private static final int CONTACT_TYPE_INDEX = 4;

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
                new String[] { CONTACT_DETAILS_PROJECTION[DISPLAY_NAME_INDEX], CONTACT_DETAILS_PROJECTION[CONTACT_TYPE_INDEX] },
                new int[] { R.id.contact_name, R.id.contact_type },
                0);
        contactsListView.setAdapter(cursorAdapter);
        contactsListView.setOnItemClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getActivity());
        cursorLoader.setUri(Contacts.CONTENT_URI);
        cursorLoader.setProjection(new String[]{
                CONTACT_DETAILS_PROJECTION[ID_INDEX],
                CONTACT_DETAILS_PROJECTION[LOOKUP_KEY_INDEX],
                CONTACT_DETAILS_PROJECTION[DISPLAY_NAME_INDEX],
                CONTACT_DETAILS_PROJECTION[HAS_PHONE_NUMBER_INDEX]
        });
        cursorLoader.setSortOrder(CONTACT_DETAILS_PROJECTION[DISPLAY_NAME_INDEX]);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Cursor contactsWithPhoneNumber = getContactsWithPhoneNumber(data);
        Cursor simContacts = getSimContacts();
        updateContactsListItems(new MergeCursor(new Cursor[]{contactsWithPhoneNumber, simContacts}));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateContactsListItems(null);
    }

    private void updateContactsListItems(Cursor contactItemsToBeReplaced){
        cursorAdapter.swapCursor(contactItemsToBeReplaced);
    }

    private Cursor getContactsWithPhoneNumber(Cursor allContacts){
        MatrixCursor contactsWithPhoneNumber = new MatrixCursor(CONTACT_DETAILS_PROJECTION);
        while (allContacts.moveToNext()){
            if (allContacts.getInt(HAS_PHONE_NUMBER_INDEX) == 1){
                contactsWithPhoneNumber.addRow(new Object []{
                        allContacts.getLong(ID_INDEX),
                        allContacts.getString(LOOKUP_KEY_INDEX),
                        allContacts.getString(DISPLAY_NAME_INDEX),
                        1,
                        "Mobile"
                });
            }
        }
        return contactsWithPhoneNumber;
    }

    private Cursor getSimContacts() {
        Uri simUri = Uri.parse("content://icc/adn");
        Cursor simContacts = getActivity().getContentResolver().query(simUri, null, null, null, null);
        MatrixCursor simContactsForApp = new MatrixCursor(CONTACT_DETAILS_PROJECTION);
        while (simContacts.moveToNext()){
            String name =simContacts.getString(simContacts.getColumnIndex("name"));
            String number = simContacts.getString(simContacts.getColumnIndex("number"));
            simContactsForApp.addRow(new Object[]{
                    9999,
                    name,
                    name,
                    0,
                    "Sim"
            });
        }
        return simContactsForApp;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) parent.getAdapter();
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);
        String contact_type = cursor.getString(CONTACT_TYPE_INDEX);
        if (contact_type == "Mobile") {
            long contactId = cursor.getLong(ID_INDEX);
            String contactLookUpKey = cursor.getString(LOOKUP_KEY_INDEX);
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
}
