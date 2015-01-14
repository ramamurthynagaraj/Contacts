package phone.Contacts;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

public class ContactsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startNewContactsListViewFragment();
    }

    private void startNewContactsListViewFragment() {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.main_layout, new ContactsListViewFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_contacts_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
