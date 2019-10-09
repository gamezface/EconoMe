package br.com.coutinhoanderson.econome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.model.Expense;

public class ExpenseAdapter extends RecyclerSwipeAdapter<ExpenseAdapter.SimpleViewHolder> {

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView expenseName;
        SwipeLayout swipeLayout;
        TextView categoryName;
        TextView cost;
        ImageButton buttonDelete;

        SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe);
            expenseName = itemView.findViewById(R.id.expenseName);
            categoryName = itemView.findViewById(R.id.category);
            cost = itemView.findViewById(R.id.cost);
            buttonDelete = itemView.findViewById(R.id.delete_button);
        }

    }

    private List<Expense> items;
    private Context mContext;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    public ExpenseAdapter(Context context, List<Expense> items) {
        this.mContext = context;
        this.items = items;
        builder = new AlertDialog.Builder(context).setTitle("Warning")
                .setMessage("You're about to remove a expense ...")
                .setNegativeButton("Cancel", null);
    }

    public void updateList(List<Expense> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        Expense item = items.get(position);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.delete_button));
            }
        });
        holder.buttonDelete.setOnClickListener(view -> {
            alertDialog = builder.setPositiveButton("Confirm", (dialog, which) -> {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/expenses").child(item.getExpenseId()).removeValue();
            }).show();
        });
        holder.expenseName.setText(item.getName());
        holder.cost.setText(item.getCost());
        holder.categoryName.setText(item.getCategory());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new SimpleViewHolder(v);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}