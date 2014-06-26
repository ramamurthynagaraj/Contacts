package phone.Contacts;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

public class ContactsActivity
        extends Activity
        implements SearchView.OnQueryTextListener {
    private FragmentTransaction fragmentTransaction;
    private ContactsListViewFragment contactsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startNewContactsListViewFragment();
    }

    private void startNewContactsListViewFragment() {
        contactsList = new ContactsListViewFragment();
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_layout, contactsList)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem searchIcon = menu.findItem(R.id.ic_action_search);
        SearchView searchView = (SearchView)searchIcon.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        contactsList.onSearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contactsList.onSearch(newText);
        return true;
    }
}
