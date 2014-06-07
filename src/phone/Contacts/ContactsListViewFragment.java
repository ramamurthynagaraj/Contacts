package phone.Contacts;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactsListViewFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private String[] Contacts_Details_Projection = new String[]{
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Contacts.DISPLAY_NAME_PRIMARY
    };
    private int CONTACTS_ID_INDEX = 0;
    private int CONTACTS_LOOKUP_KEY_INDEX = 1;
    private int CONTACTS_DISPLAY_NAME_INDEX = 2;
    private SimpleCursorAdapter cursorAdapter;

    public ContactsListViewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeEmptyContactsList();
        getLoaderManager().initLoader(0,null,this);
    }

    private void initializeEmptyContactsList() {
        ListView contactsListView = (ListView) getActivity().findViewById(android.R.id.list);
        cursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_view,
                null,
                new String[] { Contacts_Details_Projection[CONTACTS_DISPLAY_NAME_INDEX] },
                new int[] { android.R.id.text1 },
                0);
        contactsListView.setAdapter(cursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getActivity());
        cursorLoader.setUri(Contacts.CONTENT_URI);
        cursorLoader.setProjection(Contacts_Details_Projection);
        cursorLoader.setSortOrder(Contacts_Details_Projection[CONTACTS_DISPLAY_NAME_INDEX]);
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
}
