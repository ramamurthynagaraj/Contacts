package phone.Contacts;

import android.database.Cursor;

public interface ContactsLoaderCallback {
    void onContactsLoaded(Cursor contactsCursor);
}
