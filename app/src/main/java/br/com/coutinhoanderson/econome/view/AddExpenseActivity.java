package br.com.coutinhoanderson.econome.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.model.Expense;
import br.com.coutinhoanderson.econome.utils.FormValidator;

public class AddExpenseActivity extends AppCompatActivity {
    TextInputLayout expenseName;
    TextInputLayout categoryName;
    TextInputLayout cost;
    Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        initView();
        initVariables();
    }

    private void initView() {
        expenseName = findViewById(R.id.expense_name);
        categoryName = findViewById(R.id.category_name);
        cost = findViewById(R.id.cost_input);
    }

    public void initVariables() {
        Bundle parameters = getIntent().getExtras();
        if (parameters != null && parameters.getBoolean("EDIT_MODE"))
            this.expense = parameters.getParcelable("EXPENSE");
        else
            this.expense = new Expense();
        expenseName.getEditText().setText(expense.getName());
    }

    public void goBack(View v) {
        finish();
    }

    public void saveExpense(View v) {
        if (!FormValidator.hasEmptyFields(expenseName, cost, categoryName)) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference().child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/expenses");
            Bundle parameters = getIntent().getExtras();
            if (parameters != null && parameters.getBoolean("EDIT_MODE")) {
                Map<String, Object> expenseMap = new HashMap<String, Object>();
                expenseMap.put("category", categoryName.getEditText().getText().toString());
                expenseMap.put("cost", cost.getEditText().getText().toString());
                expenseMap.put("name", expenseName.getEditText().getText().toString());
                ref.child(expense.getExpenseId()).updateChildren(expenseMap);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
            } else {
                String key = database.getReference("expenses").push().getKey();
                if (key != null) {
                    ref.child(key).setValue(new Expense(expenseName.getEditText().getText().toString(),
                            cost.getEditText().getText().toString(),
                            categoryName.getEditText().getText().toString(),
                            key));
                }
            }
            finish();
        }
    }
}
