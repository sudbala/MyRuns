package edu.dartmouth.cs.myruns;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyRunsDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyRunsDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String DIALOG_ID = "dialogid";
    public static final int TAKE_PHOTO = 0;
    public static final int CHOOSE_PHOTO = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyRunsDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id DialogID
     * @return A new instance of fragment MyRunsDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyRunsDialogFragment newInstance(String id) {
        MyRunsDialogFragment fragment = new MyRunsDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates the dialog window. Has a switch statements because we will be using for multiple dialogs
     *
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String dialogID = getArguments().getString(DIALOG_ID);

        if (dialogID.equals("photo")) {
            // Builds alert dialog
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.profile_photo);

            // OnClick Listeners for choosing from gallery and for taking photo
            DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((ProfileActivity) getActivity()).takeProfilePhoto(which);
                }
            };
            // Items to display
            String[] photo_options = new String[]{"Take photo from camera", "Choose photo from gallery"};
            alert.setItems(photo_options, dialogListener);
            return alert.create();
        }
        return null;
    }
}
