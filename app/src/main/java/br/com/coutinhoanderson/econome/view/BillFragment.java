package br.com.coutinhoanderson.econome.view;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.adapter.ExpenseAdapter;
import br.com.coutinhoanderson.econome.model.Expense;
import br.com.coutinhoanderson.econome.model.Group;
import br.com.coutinhoanderson.econome.utils.DoubleFormat;


public class BillFragment extends Fragment {

    public BillFragment() {
    }

    private RecyclerView expenseList;
    private EditText recyclerFilter;
    private List<Expense> expenses;
    private ExpenseAdapter expenseAdapter;
    private ChildEventListener dataListener;
    private DatabaseReference ref;
    private ChildEventListener listener;
    private DatabaseReference ref2;
    Group group;
    String groupKey = "";
    TextView totalBudget;
    TextView remainFunds;
    TextView totalSpent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);
        initView(view);
        recyclerFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        return view;
    }
    private void initView(View view){
        expenseList = view.findViewById(R.id.list);
        recyclerFilter = view.findViewById(R.id.editText);
        totalBudget = view.findViewById(R.id.total_budget);
        totalSpent = view.findViewById(R.id.total_spents);
        remainFunds = view.findViewById(R.id.remain_funds);
        group = new Group();
    }

    @Override
    public void onResume() {
        super.onResume();
        expenses = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(getContext(), expenses);
        expenseList.setAdapter(expenseAdapter);
        FirebaseServer fs = new FirebaseServer();
        fs.fetchDataFromFirebase();
        fs.getFundsAndBudgets();
    }

    private void filter(String text) {
        List<Expense> temp = new ArrayList<>();
        for (Expense d : expenses)
            if (d.getName().toLowerCase().contains(text.toLowerCase())) temp.add(d);
        expenseAdapter.updateList(temp);
    }

    @Override
    public void onPause() {
        super.onPause();
        ref.removeEventListener(dataListener);
        ref2.removeEventListener(listener);
    }

    private class FirebaseServer {
        void getFundsAndBudgets() {
            ref2 = FirebaseDatabase.getInstance().getReference("Groups");
            listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Group currentGroup = dataSnapshot.getValue(Group.class);
                    String key = dataSnapshot.getKey();
                    Query groupsByUser = ref2
                            .child(dataSnapshot.getKey())
                            .child("members").orderByKey()
                            .equalTo(FirebaseAuth.getInstance().getUid());
                    groupsByUser.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                groupKey = key;
                                group = currentGroup;
                                remainFunds.setText(group.getRemainingFunds());
                                totalBudget.setText(group.getTotalBudget());
                                if (group.getTotalSpent() != null)
                                    totalSpent.setText(group.getTotalSpent());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Group currentGroup = dataSnapshot.getValue(Group.class);
                    String key = dataSnapshot.getKey();
                    Query groupsByUser = ref2
                            .child(dataSnapshot.getKey())
                            .child("members").orderByKey()
                            .equalTo(FirebaseAuth.getInstance().getUid());
                    groupsByUser.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                groupKey = key;
                                group = currentGroup;
                                remainFunds.setText(group.getRemainingFunds());
                                totalBudget.setText(group.getTotalBudget());
                                if(Double.valueOf(group.getRemainingFunds()) < 0){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle("Warning")
                                            .setMessage("You're out of funds ...")
                                            .setPositiveButton("Ok", null);
                                    builder.show();
                                }
                                if (group.getTotalSpent() != null)
                                    totalSpent.setText(group.getTotalSpent());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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
            ref2.addChildEventListener(listener);
        }

        void fetchDataFromFirebase() {
            ref = FirebaseDatabase.getInstance().getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/expenses");
            dataListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildKey) {
                    int index = 0;
                    Expense expense = snapshot.getValue(Expense.class);
                    if (previousChildKey != null)
                        index = getIndexForKey(previousChildKey, expenses) + 1;
                    expenses.add(index, expense);
                    expenseAdapter.notifyItemInserted(expenses.size());
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildKey) {
                    int index = getIndexForKey(snapshot.getKey(), expenses);
                    expenses.set(index, snapshot.getValue(Expense.class));
                    expenseAdapter.notifyItemChanged(index);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    int index = getIndexForKey(snapshot.getKey(), expenses);
                    expenses.remove(index);
                    Expense expense = snapshot.getValue(Expense.class);
                    if (expense != null) {
                        Double value = Double.valueOf(expense.getCost());
                        value = DoubleFormat.round(value);
                        Double totalSpent = (Double.valueOf(group.getTotalSpent()));
                        totalSpent -= value;
                        DoubleFormat.round(totalSpent);
                        Double remainingFunds = (Double.valueOf(group.getRemainingFunds()));
                        remainingFunds += value;
                        remainingFunds = DoubleFormat.round(remainingFunds);
                        group.setTotalSpent(String.valueOf(totalSpent));
                        group.setRemainingFunds(String.valueOf(remainingFunds));
                        Map<String, Object> map = new HashMap<>();
                        map.put(groupKey, group);
                        FirebaseDatabase.getInstance().getReference("/Groups").updateChildren(map);
                    }

                    expenseAdapter.notifyItemRemoved(index);
                    expenseAdapter.notifyItemRangeChanged(0, expenses.size());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildKey) {
                    int oldIndex = getIndexForKey(snapshot.getKey(), expenses);
                    expenses.remove(oldIndex);
                    int newIndex = previousChildKey == null ? 0 : getIndexForKey(previousChildKey, expenses) + 1;
                    expenses.add(newIndex, snapshot.getValue(Expense.class));
                    expenseAdapter.notifyItemMoved(oldIndex, newIndex);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseReadError", "The read failed: " + error.getCode());
                }
            };
            ref.addChildEventListener(dataListener);
        }

        private int getIndexForKey(String key, List<Expense> expenses) {
            try {
                int index = 0;
                for (Expense expense : expenses) {
                    if (expense.getExpenseId().equals(key)) {
                        return index;
                    } else {
                        index++;
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Key not found");
            }
            return -1;
        }

    }

}