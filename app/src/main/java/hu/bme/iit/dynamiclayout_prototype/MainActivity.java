package hu.bme.iit.dynamiclayout_prototype;

import android.app.AlertDialog;

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

import hu.bme.iit.dynamiccodedialog.CodeDialogBase;
import hu.bme.iit.dynamiclayout_prototype.service_elements.ScreenOnWatcherService;

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

        startService(new Intent(this,ScreenOnWatcherService.class)); //Just to start the service when the app is started

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        isTestMode = settings.getBoolean(SettingsActivity.KEY_PREF_TESTMODE,false);
        final Button numericButton = findViewById(R.id.numericButton);

        //OnClickListener to unify the listeners of the two activity start buttons to reduce repetition
        View.OnClickListener activityStarterListener = view -> {
            if(view.equals(numericButton))
                dialogBase = new NumericCodeDialog(MainActivity.this,wasStartedByBroadcastReceiver,settings, new CryptographyImplementation());
            if(isTestMode){
                final CodeDialogBase finalDialog = dialogBase;
                AlertDialog.Builder attentionDialog = new AlertDialog.Builder(view.getContext());
                attentionDialog.setMessage(R.string.attention_dialog_disclaimer);
                attentionDialog.setTitle(R.string.attention_dialog_title);
                attentionDialog.setPositiveButton("YES", (dialogInterface, i) -> finalDialog.show());
                attentionDialog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
                attentionDialog.show(); }
            else dialogBase.show();
        };
        numericButton.setOnClickListener(activityStarterListener);
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
        isTestMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_TESTMODE,false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (wasStartedByBroadcastReceiver) dialogBase.show();
    }
}
