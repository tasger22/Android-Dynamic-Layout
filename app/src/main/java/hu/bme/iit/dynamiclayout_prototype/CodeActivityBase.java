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
    protected abstract void setCodeToRandom();
    protected abstract void compareCodeToInput(String input);
}
