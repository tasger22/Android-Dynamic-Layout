package hu.bme.iit.dynamiclayout_prototype;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements DifficultyDialogFragment.DifficultyPickedListener {

    private CodeResolveDifficulty currentDifficulty = CodeResolveDifficulty.EASY;
    private boolean istestMode, isCodeUserCode;
    private String userCode;
    private String chosenLayout;

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
        Bundle b = getIntent().getExtras();
        boolean bootStart = false;

        if(b != null)  bootStart = b.getBoolean("bootStart");

        if(bootStart){
            setDataFromSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));

            Intent startupIntent = new Intent();

            putExtrasForCodeActivites(startupIntent);

            if(chosenLayout.equals("numeric"))  startupIntent.setClass(getApplicationContext(),NumericCodeActivity.class);
            else startupIntent.setClass(getApplicationContext(),GraphicCodeActivity.class);

            startupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startupIntent);
        }
        else{
            setContentView(R.layout.activity_main);

            Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
            setSupportActionBar(toolbar);
            setDataFromSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));

            final Button numericButton = (Button) findViewById(R.id.numericButton);
            final Button graphicButton = (Button) findViewById(R.id.graphicButton);

            View.OnClickListener activityStarterListener = new View.OnClickListener() { //OnClickListener to unify the listeners of the two activity start buttons to reduce repetition
                @Override
                public void onClick(View view) {
                    Intent activityIntent = new Intent();

                    if(view == numericButton)
                        activityIntent = new Intent(getApplicationContext(),NumericCodeActivity.class);
                    else if (view == graphicButton)
                        activityIntent = new Intent(getApplicationContext(),GraphicCodeActivity.class);

                    putExtrasForCodeActivites(activityIntent);
                    if(istestMode){

                        final Intent finalIntent = (Intent) activityIntent.clone();

                        AlertDialog.Builder attentionDialog = new AlertDialog.Builder(view.getContext());
                        attentionDialog.setMessage(R.string.attention_dialog_disclaimer);
                        attentionDialog.setTitle(R.string.attention_dialog_title);
                        attentionDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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
                    else startActivity(activityIntent);
                }
            };

            numericButton.setOnClickListener(activityStarterListener);
            graphicButton.setOnClickListener(activityStarterListener);
        }

    }

    private void putExtrasForCodeActivites(Intent activityIntent) {
        activityIntent.putExtra(getString(R.string.diff_key), currentDifficulty);
        activityIntent.putExtra(getString(R.string.test_mode_key), istestMode); //Test mode is default off
        activityIntent.putExtra(getString(R.string.randomized_code_key), isCodeUserCode);
        activityIntent.putExtra(getString(R.string.user_code_key),userCode);
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

                Intent settingsActivityIntent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(settingsActivityIntent);
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

    //Receive a CodeResolveDifficulty object by saying which you need by name
    public static CodeResolveDifficulty getCodeResolveDifficultyFromString(String string){
        switch(string){
            case "EASY": return CodeResolveDifficulty.EASY;
            case "HARD" : return CodeResolveDifficulty.HARD;
            case "EVIL" : return CodeResolveDifficulty.EVIL;
            default: return CodeResolveDifficulty.EASY;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDataFromSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this)); //TODO: change it to something less resource hungry
    }

    private void setDataFromSharedPreferences(SharedPreferences settings){

        istestMode = settings.getBoolean(SettingsActivity.KEY_PREF_TESTMODE,false);
        currentDifficulty = getCodeResolveDifficultyFromString(settings.getString(SettingsActivity.KEY_PREF_DIFFICULTY,"EASY"));
        isCodeUserCode = settings.getBoolean(SettingsActivity.KEY_PREF_USERCODE,false);
        userCode = settings.getString(SettingsActivity.KEY_PREF_CODEINPUT,"0000");
        while(userCode.length() < 4)
            userCode = "0" + userCode;

        chosenLayout = settings.getString(SettingsActivity.KEY_PREF_LAYOUT,"numeric");
    }
}
