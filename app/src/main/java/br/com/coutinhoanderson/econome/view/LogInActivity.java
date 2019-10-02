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
    }

    public void sendMessage(View view) {
        if (TextUtils.isEmpty(this.editTextCarrierNumber.getText().toString())) {
            phoneNumber.setError(getResources().getString(R.string.empty_phone_input));
        } else if (!ccp.isValidFullNumber()) {
            phoneNumber.setError(getResources().getString(R.string.invalid_phone_number));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("PHONE_NUMBER", this.ccp.getFormattedFullNumber());
            bundle.putString("PHONE_NUMBER_UNFORMATTTED", this.ccp.getFullNumberWithPlus());
            NavigationManager.openActivity(this, PINActivity.class, bundle);
        }
    }

}
