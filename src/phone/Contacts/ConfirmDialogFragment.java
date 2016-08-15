package phone.Contacts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ConfirmDialogFragment extends DialogFragment {

    private final int confirmMessage;
    private final DialogInterface.OnClickListener positiveClickListener;
    private final DialogInterface.OnClickListener negativeClickListener;

    public ConfirmDialogFragment(int confirmMessage, DialogInterface.OnClickListener positiveClickListener, DialogInterface.OnClickListener negativeClickListener){

        this.confirmMessage = confirmMessage;
        this.positiveClickListener = positiveClickListener;
        this.negativeClickListener = negativeClickListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(confirmMessage)
                .setPositiveButton(R.string.yes, positiveClickListener)
                .setNegativeButton(R.string.no, negativeClickListener);
        return dialog.create();
    }
}
