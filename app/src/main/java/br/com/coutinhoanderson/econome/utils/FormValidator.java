package br.com.coutinhoanderson.econome.utils;

import android.text.TextUtils;

import com.google.android.material.textfield.TextInputLayout;

public class FormValidator {
    public static boolean hasEmptyFields(TextInputLayout... textFields) {
        boolean hasEmpty = false;
        for (TextInputLayout textField : textFields) {
            if (textField.getEditText() != null && TextUtils.isEmpty(textField.getEditText().getText())) {
                textField.setError("Required Field.");
                hasEmpty = true;
            } else {
                textField.setErrorEnabled(false);
            }
        }
        return hasEmpty;
    }
}
