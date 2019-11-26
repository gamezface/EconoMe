package br.com.coutinhoanderson.econome.view;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.List;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.adapter.ExpenseAdapter;
import br.com.coutinhoanderson.econome.model.Expense;
import br.com.coutinhoanderson.econome.model.Group;


public class BillFragment extends Fragment {

    public BillFragment() {
    }

    private RecyclerView expenseList;
    private EditText recyclerFilter;
    private List<Expense> expenses;
    private ExpenseAdapter expenseAdapter;
    private ChildEventListener listener;
    private DatabaseReference ref2;
    private Group group;
    private TextView totalBudget;
    private TextView remainFunds;
    private TextView totalSpent;

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
//        expenseAdapter = new ExpenseAdapter(getContext(), expenses);
//        expenseList.setAdapter(expenseAdapter);
        FirebaseServer fs = new FirebaseServer();
        fs.getFundsAndBudgets();
    }

    private void filter(String text) {
        List<Expense> temp = new ArrayList<>();
        for (Expense d : expenses)
            if (d.getName().toLowerCase().contains(text.toLowerCase()) || d.getCost().toLowerCase().contains(text.toLowerCase()) || d.getCategory().toLowerCase().contains(text.toLowerCase()))
                temp.add(d);
        expenseAdapter.updateList(temp);
    }

    @Override
    public void onPause() {
        super.onPause();
        ref2.removeEventListener(listener);
    }

    private class FirebaseServer {
        void getFundsAndBudgets() {
            ref2 = FirebaseDatabase.getInstance().getReference("Groups");
            listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Group currentGroup = dataSnapshot.getValue(Group.class);
                    Query groupsByUser = ref2
                            .child(dataSnapshot.getKey())
                            .child("members").orderByKey()
                            .equalTo(FirebaseAuth.getInstance().getUid());
                    groupsByUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                group = currentGroup;
                                if (group != null && group.getExpenses() != null) {
                                    expenses = null;
                                    expenses = group.getExpenses();
                                    expenseAdapter = null;
                                    expenseAdapter = new ExpenseAdapter(getContext(), expenses);
                                    expenseList.setAdapter(expenseAdapter);
//                                    for (int i = expenses.size(); i < group.getExpenses().size(); i++) {
//                                        expenses.add(group.getExpenses().get(i));
//                                    }
//                                    expenseList.getAdapter().notifyDataSetChanged();
//                                    expenseAdapter.notifyItemInserted(expenses.size());
                                }
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
                    Query groupsByUser = ref2
                            .child(dataSnapshot.getKey())
                            .child("members").orderByKey()
                            .equalTo(FirebaseAuth.getInstance().getUid());
                    groupsByUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                group = currentGroup;
                                if (group != null && group.getExpenses() != null) {
                                    expenses = null;
                                    expenses = group.getExpenses();
                                    expenseAdapter = null;
                                    expenseAdapter = new ExpenseAdapter(getContext(), expenses);
                                    expenseList.setAdapter(expenseAdapter);
//                                    for (int i = expenses.size(); i < group.getExpenses().size(); i++) {
//                                        expenses.add(group.getExpenses().get(i));
//                                    }
//                                    expenseList.getAdapter().notifyDataSetChanged();
//                                    expenseAdapter.notifyItemInserted(expenses.size());
                                }
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
    }
}