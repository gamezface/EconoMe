package br.com.coutinhoanderson.econome.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.model.Expense;

public class AddExpenseActivity extends AppCompatActivity {
    EditText expenseName;
    EditText categoryName;
    EditText cost;
    Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);
        initView();
        initVariables();
    }

    private void initView() {
        expenseName = findViewById(R.id.expense_name);
        categoryName = findViewById(R.id.category_name);
        cost = findViewById(R.id.cost);
    }

    public void initVariables() {
        Bundle parameters = getIntent().getExtras();
        if (parameters != null && parameters.getBoolean("EDIT_MODE"))
            this.expense = parameters.getParcelable("EXPENSE");
        else
            this.expense = new Expense();
        expenseName.setText(expense.getName());
    }

    public void goBack(View v) {
        finish();
    }

    public void saveExpense(View v) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/expenses");
        Bundle parameters = getIntent().getExtras();
        if (parameters != null && parameters.getBoolean("EDIT_MODE")) {
            Map<String, Object> expenseMap = new HashMap<String, Object>();
            expenseMap.put("category", categoryName.getText().toString());
            expenseMap.put("cost", cost.getText().toString());
            expenseMap.put("name", expenseName.getText().toString());
            ref.child(expense.getExpenseId()).updateChildren(expenseMap);
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            String key = database.getReference("expenses").push().getKey();
            if (key != null) {
                ref.child(key).setValue(new Expense(expenseName.getText().toString(),
                        cost.getText().toString(),
                        categoryName.getText().toString(),
                        key));
            }
        }
        finish();
    }
}
