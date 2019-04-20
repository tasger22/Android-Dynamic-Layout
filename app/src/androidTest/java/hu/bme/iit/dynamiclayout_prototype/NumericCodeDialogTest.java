package hu.bme.iit.dynamiclayout_prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import hu.bme.iit.dynamiccodedialog.CryptographyImplementation;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class NumericCodeDialogTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    private String testCode = "1996";
    private String encryptedTestCode;
    private NumericCodeDialog shownNumericDialog;
    private CryptographyImplementation testCrypter = new CryptographyImplementation();

    @Before
    public void setUp() throws Throwable {
        Context appContext = InstrumentationRegistry.getContext();
        MainActivity activityContext = mActivityRule.launchActivity(new Intent(appContext,MainActivity.class));

        final SharedPreferences customPref = appContext.getSharedPreferences("numericTest",Context.MODE_PRIVATE);
        SharedPreferences.Editor appPrefEditor = customPref.edit();
        byte[] stringByteArray = testCrypter.encrypt(testCode);
        encryptedTestCode = testCrypter.byteArrayToHexString(stringByteArray);

        appPrefEditor.putString(activityContext.getString(R.string.encrypted_code_key), encryptedTestCode);
        appPrefEditor.apply();

        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shownNumericDialog = new NumericCodeDialog(mActivityRule.getActivity(),false, customPref, testCrypter);
                try {
                    shownNumericDialog.initialSetup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                shownNumericDialog.show();
            }
        });

    }

    @Test
    public void testCodeMatchesSetSecurityCode(){
        //Check if setting the code to test code really changed it meaning initialSetup really changed the userCode var and setting the code to it worked
         //The security code which supposed to equal to testCode set above
        assertTrue(shownNumericDialog.isInputCodeCorrect(encryptedTestCode));
    }

    @Test
    public void inputCodeMatchesPasswordLineAndSetSecurityCode(){
        //Pressing buttons to put in security code
        for (int i = 0; i < testCode.length(); i++) {
            onView(withText(Character.toString(testCode.charAt(i)))).perform(click());
        }

        //Check if the password line holds the same values
        EditText passLine = shownNumericDialog.findViewById(R.id.passwordLine);
        byte[] inputEncryptedBytes = testCrypter.encrypt(passLine.getText().toString());
        String hexaInputString = testCrypter.byteArrayToHexString(inputEncryptedBytes);
        assertEquals(testCode,passLine.getText().toString());
        assertTrue(shownNumericDialog.isInputCodeCorrect(hexaInputString));


        //Testing if the code was really correct with checking if the Toast for the correct code appeared (throws exception if the Toast with the message was not found)
        onView(withId(R.id.acceptButton)).perform(click());
        onView(withText(R.string.code_accepted)).inRoot(withDecorView(not(shownNumericDialog.getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void deleteCharacterButtonWorks(){
        //Pressing buttons to put in something
        onView(withText("1")).perform(click());
        onView(withText("9")).perform(click());

        //Pressing the delete button two times to undo the input
        onView(withId(R.id.deleteButton)).perform(click());
        onView(withId(R.id.deleteButton)).perform(click());

        //Check if the password line is empty
        EditText passLine = shownNumericDialog.findViewById(R.id.passwordLine);
        assertEquals("",passLine.getText().toString());
    }
}