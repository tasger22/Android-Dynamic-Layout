package hu.bme.iit.dynamiclayout_prototype;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;



/**
 * Created by Stealth on 2017. 11. 28..
 */

public class DifficultyDialogFragment extends AppCompatDialogFragment{

    public static final String TAG = "DifficultyDialogFragment";
    private DifficultyPickedListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(getActivity() instanceof DifficultyPickedListener)) {
            throw new RuntimeException("The activity does not implement the" +
                    "DifficultyPickedListener interface");
        }

        listener = (DifficultyPickedListener) getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle("Difficulty change")
                .setView(getDifChangeView())
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private View getDifChangeView() {

        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dif_change_fragment_layout, null);



        final RadioGroup difChangeRadioGroup = (RadioGroup) view.findViewById(R.id.difChangeRadioGroup);
        RadioButton easyButton = (RadioButton) view.findViewById(R.id.easyButton);
        RadioButton hardButton = (RadioButton) view.findViewById(R.id.hardButton);
        RadioButton evilButton = (RadioButton) view.findViewById(R.id.evilButton);
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedButtonId = difChangeRadioGroup.getCheckedRadioButtonId();

                switch (checkedButtonId){
                    case 0: listener.OnDifficultyPicked(MainActivity.CodeResolveDifficulty.EASY);
                    break;

                    case 1: listener.OnDifficultyPicked(MainActivity.CodeResolveDifficulty.HARD);
                        break;

                    case 2: listener.OnDifficultyPicked(MainActivity.CodeResolveDifficulty.EVIL);
                        break;

                    default: listener.OnDifficultyPicked(MainActivity.CodeResolveDifficulty.EASY);
                        break;
                }

                dismiss();
            }
        };

        easyButton.setOnClickListener(buttonClickListener);
        hardButton.setOnClickListener(buttonClickListener);
        evilButton.setOnClickListener(buttonClickListener);

        return view;

    }

    public interface DifficultyPickedListener{
        public void OnDifficultyPicked(MainActivity.CodeResolveDifficulty difficulty);
    }
}
