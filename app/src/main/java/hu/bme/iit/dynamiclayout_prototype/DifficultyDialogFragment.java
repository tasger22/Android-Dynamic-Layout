package hu.bme.iit.dynamiclayout_prototype;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import hu.bme.iit.dynamiclayout_prototype.MainActivity.CodeResolveDifficulty;



/**
 * Created by Stealth on 2017. 11. 28..
 */

public class DifficultyDialogFragment extends AppCompatDialogFragment{

    public static final String TAG = "DifficultyDialogFragment";
    private DifficultyPickedListener listener;
    private String[] choices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(getActivity() instanceof DifficultyPickedListener)) {
            throw new RuntimeException("The activity does not implement the" +
                    "DifficultyPickedListener interface");
        }
        choices = getResources().getStringArray(R.array.difficulty_array);
        listener = (DifficultyPickedListener) getActivity();
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle b = getArguments();

        return new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.dif_change))
                .setSingleChoiceItems(choices,b.getInt(getString(R.string.postition_key)),difChosenListener)
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private DialogInterface.OnClickListener difChosenListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i){
                case 0: listener.OnDifficultyPicked(CodeResolveDifficulty.EASY);
                    break;
                case 1: listener.OnDifficultyPicked(CodeResolveDifficulty.HARD);
                    break;
                case 2: listener.OnDifficultyPicked(CodeResolveDifficulty.EVIL);
                    break;
                default: listener.OnDifficultyPicked(CodeResolveDifficulty.EASY);
                    break;
            }
            dismiss();
        }
    };

    public interface DifficultyPickedListener{
        public void OnDifficultyPicked(CodeResolveDifficulty difficulty);
    }
}
