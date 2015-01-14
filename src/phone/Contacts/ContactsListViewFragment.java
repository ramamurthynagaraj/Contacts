package phone.Contacts;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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
    public void onPrepareOptionsMenu(Menu menu){
        MenuItem searchIcon = menu.findItem(R.id.ic_action_search);
        SearchView searchView = (SearchView)searchIcon.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        contactsLoader.onSearchContact(newText,this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contactsLoader.onSearchContact(newText, this);
        return true;
    }

    @Override
    public void onContactsLoaded(Cursor contactsCursor) {
        cursorAdapter.swapCursor(contactsCursor);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) parent.getAdapter();
        view.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);
        final String contactType = cursor.getString(ContactsLoader.CONTACT_TYPE_INDEX);
        final String displayName = cursor.getString(ContactsLoader.DISPLAY_NAME_INDEX);
        final long contactId = cursor.getLong(ContactsLoader.ID_INDEX);
        final View listItemView = view;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadContactDetailsViewFragment(contactType, displayName, contactId);
                listItemView.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }, 300);
    }

    private void startLoadingContactsInBackground() {
        contactsLoader = new ContactsLoader(getActivity());
        contactsLoader.loadAllContacts(this);
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
        Intent intent = new Intent(getActivity(), ContactDetailsViewActivity.class);
        intent.putExtra("contactType", contactType);
        intent.putExtra("displayName", displayName);
        intent.putExtra("contactId", contactId);
        startActivity(intent);
    }
}
