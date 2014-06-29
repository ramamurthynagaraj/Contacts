package phone.Contacts;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.view.*;
import android.widget.*;

public class ContactsListViewFragment
        extends Fragment
        implements ContactsLoaderCallback,
        AdapterView.OnItemClickListener,
        SearchView.OnQueryTextListener {
    private SimpleCursorAdapter cursorAdapter;
    private ContactsLoader contactsLoader;
    private ListView contactsListView;
    private String NO_CONTACTS_FOUND = "No Contacts found";

    public ContactsListViewFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.list_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeEmptyContactsList();
        startLoadingContactsInBackground();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_contacts_actionbar, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        MenuItem searchIcon = menu.findItem(R.id.ic_action_search);
        SearchView searchView = (SearchView)searchIcon.getActionView();
        searchView.setOnQueryTextListener(this);
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        contactsLoader.onSearchContact(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contactsLoader.onSearchContact(newText);
        return true;
    }

    @Override
    public void onContactsLoaded(Cursor contactsCursor) {
        cursorAdapter.swapCursor(contactsCursor);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) parent.getAdapter();
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);
        String contactType = cursor.getString(ContactsLoader.CONTACT_TYPE_INDEX);
        String displayName = cursor.getString(ContactsLoader.DISPLAY_NAME_INDEX);
        long contactId = cursor.getLong(ContactsLoader.ID_INDEX);
        loadContactDetailsViewFragment(contactType, displayName, contactId);
    }

    private void startLoadingContactsInBackground() {
        contactsLoader = new ContactsLoader(getActivity());
        contactsLoader.loadAllInBackground(getLoaderManager(), this);
    }

    private void initializeEmptyContactsList() {
        contactsListView = (ListView) getActivity().findViewById(android.R.id.list);
        contactsListView.setEmptyView(getDefaultTextView());
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

    private TextView getDefaultTextView(){
        TextView textView = (TextView)getActivity().findViewById(android.R.id.empty);
        textView.setText(NO_CONTACTS_FOUND);
        return textView;
    }

    private void loadContactDetailsViewFragment(String contactType, String displayName, long contactId){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_layout, new ContactDetailsViewFragment(contactType, displayName, contactId))
                    .addToBackStack(displayName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
    }
}
