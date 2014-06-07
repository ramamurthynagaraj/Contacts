package phone.Contacts;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ContactsActivity extends Activity {
    private FragmentTransaction fragmentTransaction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startNewContactsListViewFragment();
    }

    private void startNewContactsListViewFragment(){
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_layout, new ContactsListViewFragment()).commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ic_action_search){

        }
        return super.onOptionsItemSelected(item);
    }
}
