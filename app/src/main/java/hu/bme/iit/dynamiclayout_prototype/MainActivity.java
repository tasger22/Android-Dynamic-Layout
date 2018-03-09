package hu.bme.iit.dynamiclayout_prototype;

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

        Button numericButton = (Button) findViewById(R.id.numericButton);
        final Button graphicButton = (Button) findViewById(R.id.graphicButton);
        final Button difficultyButton = (Button) findViewById(R.id.difficultyButton);


        numericButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent numericActivityIntent = new Intent(getApplicationContext(),NumericCodeActivity.class);
                numericActivityIntent.putExtra("difficulty",currentDifficulty);
                startActivity(numericActivityIntent);
            }
        });

        graphicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent graphicActivityIntent = new Intent(getApplicationContext(),GraphicCodeActivity.class);
                graphicActivityIntent.putExtra("difficulty",currentDifficulty);
                startActivity(graphicActivityIntent);
            }
        });

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
