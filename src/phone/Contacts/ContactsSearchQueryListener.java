package phone.Contacts;

import android.widget.SearchView;

/**
 * Created by Nagaraj on 19/01/15.
 */
public class ContactsSearchQueryListener
    implements SearchView.OnQueryTextListener {
    private ContactsLoader contactsLoader;
    private ContactsLoaderCallback contactsLoaderCallback;

    public ContactsSearchQueryListener(ContactsLoader contactsLoader, ContactsLoaderCallback contactsLoaderCallback){
        this.contactsLoader = contactsLoader;
        this.contactsLoaderCallback = contactsLoaderCallback;
    }
    @Override
    public boolean onQueryTextSubmit(String newText) {
        contactsLoader.onSearchContact(newText,contactsLoaderCallback);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contactsLoader.onSearchContact(newText, contactsLoaderCallback);
        return true;
    }
}
