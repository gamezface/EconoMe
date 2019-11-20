package br.com.coutinhoanderson.econome.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.model.Expense;
import br.com.coutinhoanderson.econome.model.Group;
import br.com.coutinhoanderson.econome.utils.DoubleFormat;
import br.com.coutinhoanderson.econome.utils.FormValidator;

public class AddExpenseActivity extends AppCompatActivity {
    TextInputLayout expenseName;
    TextInputLayout categoryName;
    TextInputLayout cost;
    Expense expense;
    String groupKey = "";

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
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
            ChildEventListener listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String groupKeyTemp = dataSnapshot.getKey();
                    Query groupsByUser = ref1
                            .child(dataSnapshot.getKey())
                            .child("members").orderByKey()
                            .equalTo(FirebaseAuth.getInstance().getUid());
                    groupsByUser.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                groupKey = groupKeyTemp;
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/Groups/");
                                reference.orderByKey().equalTo(groupKey)
                                        .addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                Group group = (dataSnapshot.getValue(Group.class));
                                                Map<String, Object> map = new HashMap<>();
                                                if (group.getTotalSpent() == null) {
                                                    group.setTotalSpent("0");
                                                }
                                                group.setRemainingFunds(String.valueOf(
                                                        DoubleFormat.round(Double.valueOf(group.getRemainingFunds()) - Double.valueOf(cost.getEditText().getText().toString())))
                                                );
                                                group.setTotalSpent(String.valueOf(
                                                        DoubleFormat.round(Double.valueOf(group.getTotalSpent()) + Double.valueOf(cost.getEditText().getText().toString())))
                                                );
                                                map.put(dataSnapshot.getKey(), group);
                                                reference.updateChildren(map);
                                            }

                                            @Override
                                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                Log.d("teste", dataSnapshot.getKey());
                                            }

                                            @Override
                                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                                Log.d("teste", dataSnapshot.getKey());
                                            }

                                            @Override
                                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                Log.d("teste", dataSnapshot.getKey());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
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
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            ref1.addChildEventListener(listener);
                finish();
        }
    }
}
