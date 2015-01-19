package phone.Contacts;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class ContactsActivity extends Activity
        implements ContactsLoaderCallback,
        AdapterView.OnItemClickListener {

    private SimpleCursorAdapter cursorAdapter;
    private ContactsLoader contactsLoader;
    private ListView contactsListView;
    private String NO_CONTACTS_FOUND = "No Contacts found";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        initializeEmptyContactsList();
        startLoadingContactsInBackground();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_contacts_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem searchIcon = menu.findItem(R.id.ic_action_search);
        SearchView searchView = (SearchView)searchIcon.getActionView();
        searchView.setOnQueryTextListener(new ContactsSearchQueryListener(contactsLoader, this));
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
        contactsLoader = new ContactsLoader(this);
        contactsLoader.loadAllContacts(this);
    }

    private void initializeEmptyContactsList() {
        contactsListView = (ListView) this.findViewById(android.R.id.list);
        contactsListView.setEmptyView(getDefaultTextView());
        cursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item_view,
                null,
                new String[] { ContactsLoader.DISPLAY_NAME, ContactsLoader.CONTACT_TYPE },
                new int[] { R.id.contact_name, R.id.contact_type },
                0);
        contactsListView.setAdapter(cursorAdapter);
        contactsListView.setOnItemClickListener(this);
    }

    private TextView getDefaultTextView(){
        TextView textView = (TextView)this.findViewById(android.R.id.empty);
        textView.setText(NO_CONTACTS_FOUND);
        return textView;
    }

    private void loadContactDetailsViewFragment(String contactType, String displayName, long contactId){
        Intent intent = new Intent(this, ContactDetailsViewActivity.class);
        intent.putExtra("contactType", contactType);
        intent.putExtra("displayName", displayName);
        intent.putExtra("contactId", contactId);
        startActivity(intent);
    }
}
