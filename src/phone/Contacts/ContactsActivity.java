package phone.Contacts;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
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
}
