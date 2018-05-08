package hu.bme.iit.dynamiclayout_prototype;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{

    private boolean isTestMode;
    private boolean wasStartedByBroadcastReceiver;
    private CodeDialogBase dialogBase;

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

        startService(new Intent(this,ScreenOnWatcherService.class)); //Just to start the service when the app is started TODO: it is not necessary if the user set it to off

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        isTestMode = settings.getBoolean(SettingsActivity.KEY_PREF_TESTMODE,false);
        final Button numericButton = (Button) findViewById(R.id.numericButton);
        final Button graphicButton = (Button) findViewById(R.id.graphicButton);
        View.OnClickListener activityStarterListener = new View.OnClickListener() { //OnClickListener to unify the listeners of the two activity start buttons to reduce repetition
            @Override
            public void onClick(View view) {
                if(view.equals(numericButton))
                    dialogBase = new NumericCodeDialog(MainActivity.this,wasStartedByBroadcastReceiver);
                else if (view.equals(graphicButton))
                    dialogBase = new GraphicCodeDialog(MainActivity.this,wasStartedByBroadcastReceiver);
                if(isTestMode){
                    final CodeDialogBase finalDialog = dialogBase;
                    AlertDialog.Builder attentionDialog = new AlertDialog.Builder(view.getContext());
                    attentionDialog.setMessage(R.string.attention_dialog_disclaimer);
                    attentionDialog.setTitle(R.string.attention_dialog_title);
                    attentionDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finalDialog.show(); }});
                    attentionDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss(); }});
                    attentionDialog.show(); }
                else dialogBase.show();
            }
        };
        numericButton.setOnClickListener(activityStarterListener);
        graphicButton.setOnClickListener(activityStarterListener);


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

                Intent settingsActivityIntent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(settingsActivityIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
        isTestMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_TESTMODE,false); //TODO: change it to something less resource hungry
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (wasStartedByBroadcastReceiver) dialogBase.show();
    }
}
