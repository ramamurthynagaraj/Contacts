package phone.Contacts;

import android.app.Fragment;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Nagaraj on 02/06/14.
 */
public class ContactsListViewFragment extends Fragment {
    public ContactsListViewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView contactsListView = (ListView) getActivity().findViewById(android.R.id.list);
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_view,
                getSampleData(),
                new String[]{"Column"},
                new int[]{android.R.id.text1},
                0);
        contactsListView.setAdapter(cursorAdapter);
    }

    public Cursor getSampleData(){
        MatrixCursor cursor = new MatrixCursor(new String[]{"_id","Column"});
        cursor.addRow(new Object[]{"1","One"});
        cursor.addRow(new Object[]{"2","Two"});
        cursor.addRow(new Object[]{"3","Three"});
        cursor.addRow(new Object[]{"4","Four"});
        cursor.addRow(new Object[]{"5","Eight"});
        return cursor;
    }
}
