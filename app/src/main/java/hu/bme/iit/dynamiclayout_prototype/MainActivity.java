package hu.bme.iit.dynamiclayout_prototype;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DifficultyDialogFragment.DifficultyPickedListener {

    private CodeResolveDifficulty currentDifficulty = CodeResolveDifficulty.EASY;

    public enum CodeResolveDifficulty{ //Enumeration to indicate the toughness of the code resolution
        EASY,HARD,EVIL
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button numericButton = (Button) findViewById(R.id.numericButton);
        final Button graphicButton = (Button) findViewById(R.id.graphicButton);
        Button difficultyButton = (Button) findViewById(R.id.difficultyButton);

        View.OnClickListener activityStarterListener = new View.OnClickListener() { //OnClickListener to unify the listeners of the two activity start buttons to reduce repetition
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent();

                if(view == numericButton)
                    activityIntent = new Intent(getApplicationContext(),NumericCodeActivity.class);
                else if (view == graphicButton)
                    activityIntent = new Intent(getApplicationContext(),GraphicCodeActivity.class);

                activityIntent.putExtra("difficulty", currentDifficulty);
                activityIntent.putExtra("testMode", false); //Test mode is default off

                final Intent finalIntent = (Intent) activityIntent.clone();

                AlertDialog.Builder attentionDialog = new AlertDialog.Builder(view.getContext());
                attentionDialog.setMessage(R.string.attention_dialog_discalimer);
                attentionDialog.setTitle(R.string.attention_dialog_title);
                attentionDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finalIntent.putExtra("testMode",true);
                        startActivity(finalIntent);
                    }
                });
                attentionDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                attentionDialog.show();
            }
        };

        numericButton.setOnClickListener(activityStarterListener);
        graphicButton.setOnClickListener(activityStarterListener);

        difficultyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();

                switch (currentDifficulty){
                    case EASY: b.putInt("position",0);
                        break;

                    case HARD: b.putInt("position",1);
                        break;

                    case EVIL: b.putInt("position",2);
                        break;

                    default: b.putInt("position",0);
                        break;
                }

                DifficultyDialogFragment difficultyDialogFragment = new DifficultyDialogFragment();
                difficultyDialogFragment.setArguments(b);

                difficultyDialogFragment.show(getSupportFragmentManager(), DifficultyDialogFragment.TAG);
            }
        });
    }

    @Override
    public void OnDifficultyPicked(CodeResolveDifficulty difficulty) {
        currentDifficulty = difficulty;
        Toast.makeText(this,difficulty.toString(),Toast.LENGTH_SHORT).show();
    }
}
