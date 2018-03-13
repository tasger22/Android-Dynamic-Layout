package hu.bme.iit.dynamiclayout_prototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public abstract class CodeActivityBase extends AppCompatActivity {

    protected String code;
    protected MainActivity.CodeResolveDifficulty currentDifficulty;
    protected boolean isTestMode;
    protected int tries = 2;
    protected int fails;
    protected long testStartTime;
    protected int initialTries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initialSetup(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                 currentDifficulty = MainActivity.CodeResolveDifficulty.EASY;
                 isTestMode = false;
            } else {
                currentDifficulty = (MainActivity.CodeResolveDifficulty) extras.get(getString(R.string.diff_key));
                isTestMode = extras.getBoolean(getString(R.string.test_mode_key));
            }
        } else {
            currentDifficulty = (MainActivity.CodeResolveDifficulty) savedInstanceState.getSerializable(getString(R.string.diff_key));
            isTestMode = savedInstanceState.getBoolean(getString(R.string.test_mode_key));
        }

        if(isTestMode){
            tries = initialTries = 10;
            fails = 0;
            testStartTime = System.currentTimeMillis();
        }
    }

    protected abstract void setCodeToRandom();
    protected abstract void compareCodeToInput(String input);
}
