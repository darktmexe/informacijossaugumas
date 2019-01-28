package com.example.rytis.saugumo1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    NumberPicker rotationPicker;
    EditText inputText;
    TextView cipherText;
    RadioGroup radioGroup;
    EditText codewordText;
    Button clearButton;
    TextView decoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        cipherText = (TextView) findViewById(R.id.textView2);
        decoded = (TextView) findViewById(R.id.deocoded);

        rotationPicker = (NumberPicker) findViewById(R.id.numberPicker);
        rotationPicker.setMinValue(0);
        rotationPicker.setMaxValue(25);

        rotationPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                refreshCipherText();
            }
        });

        codewordText = (EditText) findViewById(R.id.editText2);
        codewordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                refreshCipherText();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioButton:
                        codewordText.setVisibility(View.GONE);
                        rotationPicker.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radioButton2:
                       // rotationPicker.setVisibility(View.GONE);
                        rotationPicker.setVisibility(View.VISIBLE);
                        break;
                }
                refreshCipherText();
            }
        });


        inputText = (EditText) findViewById(R.id.editText);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                refreshCipherText();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i)) && source.charAt(i) != ' ') {
                        return "";
                    }
                }
                return null;
            }
        };

        inputText.setFilters(new InputFilter[]{filter});
        codewordText.setFilters(new InputFilter[]{filter});

        clearButton = (Button) findViewById(R.id.button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputText.setText("");
                cipherText.setText("");
                decoded.setText(" ");
            }
        });

        loadState();

        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radioButton:
                codewordText.setVisibility(View.GONE);
                rotationPicker.setVisibility(View.VISIBLE);
                break;
            case R.id.radioButton2:
                rotationPicker.setVisibility(View.VISIBLE);
               // codewordText.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences settings = getSharedPreferences("cipherPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        saveState(editor);
        editor.commit();
    }

    public void saveState(SharedPreferences.Editor editor) {
        editor.putString("inputText", inputText.getText().toString());
        editor.putInt("rotation", rotationPicker.getValue());
        editor.putString("codeword", codewordText.getText().toString());
        editor.putInt("checkedRadiobutton", radioGroup.getCheckedRadioButtonId());
    }

    public void loadState() {
        SharedPreferences settings = getSharedPreferences("cipherPrefs", 0);

        inputText.setText(settings.getString("inputText", ""));
        rotationPicker.setValue(settings.getInt("rotation", 0));
        codewordText.setText(settings.getString("codeword", ""));
        radioGroup.check(settings.getInt("checkedRadiobutton", R.id.radioButton));
        refreshCipherText();
    }

    public void refreshCipherText() {
        String output = computeCipher(inputText.getText().toString());
        cipherText.setText(output);
        decoded.setText(output);
    }

    public static String computeRotationCipherdecode(int offset, String enc) {
        return computeRotationCipher(26-offset, enc);
    }

    public static String computeRotationCipher( int offset, String enc) {
        offset = offset % 26 + 26;
        StringBuilder encoded = new StringBuilder();
        for (char i : enc.toCharArray()) {
            if (Character.isLetter(i)) {
                if (Character.isUpperCase(i)) {
                    encoded.append((char) ('A' + (i - 'A' + offset) % 26 ));
                } else {
                    encoded.append((char) ('a' + (i - 'a' + offset) % 26 ));
                }
            } else {
                encoded.append(i);
            }
        }
        return encoded.toString();
    }

}
