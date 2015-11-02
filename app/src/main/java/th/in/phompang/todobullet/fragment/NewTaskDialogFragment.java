package th.in.phompang.todobullet.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import th.in.phompang.todobullet.R;

/**
 * Created by Pichai Sivawat on 25/10/2558.
 */
public class NewTaskDialogFragment extends DialogFragment {

    public NewTaskDialogFragment() {

    }

    public static NewTaskDialogFragment newInstance() {

        Bundle args = new Bundle();

        NewTaskDialogFragment fragment = new NewTaskDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
//
//    @Override
//    public void onActivityCreated(Bundle bundle) {
//        super.onActivityCreated(bundle);
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//    }
//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceSate) {
        View v = inflater.inflate(R.layout.dialog_new_task, container, false);
        EditText title = (EditText) v.findViewById(R.id.new_task_name);

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;

//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        builder.setView(inflater.inflate(R.layout.dialog_new_task, null))
//                .setTitle("New Task")
//                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        sendResult(Activity.RESULT_OK);
//                    }
//                })
//                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        sendResult(Activity.RESULT_CANCELED);
//                    }
//                });
//        return builder.create();
    }
//
//    private void sendResult(int REQUEST_CODE) {
//        Intent intent = new Intent();
//        switch (REQUEST_CODE) {
//            case Activity.RESULT_OK:
//                //TODO check if empty
//                EditText editText = (EditText) getDialog().findViewById(R.id.new_task_name);
//                intent.putExtra("name", editText.getText().toString());
//                break;
//            case Activity.RESULT_CANCELED: default:
//                break;
//        }
//        getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
//    }
}
