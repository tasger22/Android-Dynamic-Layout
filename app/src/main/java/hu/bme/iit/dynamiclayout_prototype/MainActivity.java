package hu.bme.iit.dynamiclayout_prototype;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DifficultyDialogFragment.DifficultyPickedListener {

    private CodeResolveDifficulty currentDifficulty = CodeResolveDifficulty.EASY;

    public enum CodeResolveDifficulty{ //Enumeration to indicate the toughness of the code resolution
        EASY,HARD,EVIL;

        public double getDifficultyMultiplier(){ //getting the multiplier which is used in the user performance evaluation
            switch(this){
                case EASY: return 1;
                case HARD: return 1.5;
                case EVIL: return 2;
                default: return 1;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

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

                activityIntent.putExtra(getString(R.string.diff_key), currentDifficulty);
                activityIntent.putExtra(getString(R.string.test_mode_key), false); //Test mode is default off

                final Intent finalIntent = (Intent) activityIntent.clone();

                AlertDialog.Builder attentionDialog = new AlertDialog.Builder(view.getContext());
                attentionDialog.setMessage(R.string.attention_dialog_disclaimer);
                attentionDialog.setTitle(R.string.attention_dialog_title);
                attentionDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finalIntent.putExtra(getString(R.string.test_mode_key),true);
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
                    case EASY: b.putInt(getString(R.string.postition_key),0);
                        break;

                    case HARD: b.putInt(getString(R.string.postition_key),1);
                        break;

                    case EVIL: b.putInt(getString(R.string.postition_key),2);
                        break;

                    default: b.putInt(getString(R.string.postition_key),0);
                        break;
                }

                DifficultyDialogFragment difficultyDialogFragment = new DifficultyDialogFragment();
                difficultyDialogFragment.setArguments(b);

                difficultyDialogFragment.show(getSupportFragmentManager(), DifficultyDialogFragment.TAG);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void OnDifficultyPicked(CodeResolveDifficulty difficulty) {
        currentDifficulty = difficulty;
        //Toast.makeText(this,difficulty.toString(),Toast.LENGTH_SHORT).show();
    }
}
