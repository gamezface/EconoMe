package br.com.coutinhoanderson.econome.view;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.adapter.ExpenseAdapter;
import br.com.coutinhoanderson.econome.model.EmptyRecyclerView;
import br.com.coutinhoanderson.econome.model.Expense;


public class BillFragment extends Fragment {

    public BillFragment() {
    }

    private EmptyRecyclerView expenseList;
    private TextView emptyText;
    private EditText recyclerFilter;
    private List<Expense> expenses;
    private ExpenseAdapter expenseAdapter;
    private ChildEventListener dataListener;
    private DatabaseReference ref;

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
        emptyText = view.findViewById(android.R.id.empty);
        recyclerFilter = view.findViewById(R.id.editText);
    }

    @Override
    public void onResume() {
        super.onResume();
        expenses = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(getContext(), expenses);
        expenseList.setAdapter(expenseAdapter);
        expenseList.setEmptyView(emptyText);
        expenseList.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseServer fs = new FirebaseServer();
        fs.fetchDataFromFirebase();
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
    }

    private class FirebaseServer {
        void fetchDataFromFirebase() {
            ref = FirebaseDatabase.getInstance().getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/expenses");
            dataListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildKey) {
                    int index = 0;
                    if (previousChildKey != null)
                        index = getIndexForKey(previousChildKey, expenses) + 1;
                    expenses.add(index, snapshot.getValue(Expense.class));
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
            int index = 0;
            for (Expense expense : expenses) {
                if (expense.getExpenseId().equals(key)) {
                    return index;
                } else {
                    index++;
                }
            }
            throw new IllegalArgumentException("Key not found");
        }

    }

}