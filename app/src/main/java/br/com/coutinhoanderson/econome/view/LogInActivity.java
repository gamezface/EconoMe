package br.com.coutinhoanderson.econome.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.utils.NavigationManager;

public class LogInActivity extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText editTextCarrierNumber;
    private TextInputLayout phoneNumber;
    private EditText nameInput;
    private EditText budgetInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initView();
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
    }

    private void initView() {
        ccp = findViewById(R.id.ccp);
        editTextCarrierNumber = findViewById(R.id.editText_carrierNumber);
        phoneNumber = findViewById(R.id.phoneInput);
        nameInput = findViewById(R.id.name_input);
        budgetInput = findViewById(R.id.budget_input);

    }

    public void sendMessage(View view) {
        boolean hasError = false;
        if (TextUtils.isEmpty(this.editTextCarrierNumber.getText().toString())) {
            phoneNumber.setError(getResources().getString(R.string.empty_phone_input));
            hasError = true;
        } else if (!ccp.isValidFullNumber()) {
            phoneNumber.setError(getResources().getString(R.string.invalid_phone_number));
            hasError = true;
        }
        if (TextUtils.isEmpty(this.budgetInput.getText().toString())) {
            budgetInput.setError("Required");
            hasError = true;
        }
        if (TextUtils.isEmpty(this.nameInput.getText().toString())) {
            nameInput.setError("Required");
            hasError = true;
        }
        if (!hasError) {
            Bundle bundle = new Bundle();
            bundle.putString("PHONE_NUMBER", this.ccp.getFormattedFullNumber());
            bundle.putString("PHONE_NUMBER_UNFORMATTTED", this.ccp.getFullNumberWithPlus());
            bundle.putString("USER_NAME", this.nameInput.getText().toString());
            bundle.putString("USER_BUDGET", this.budgetInput.getText().toString());
            NavigationManager.openActivity(this, PINActivity.class, bundle);
        }
    }

}
