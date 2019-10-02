package br.com.coutinhoanderson.econome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.model.Expense;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.SimpleViewHolder> {

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView expenseName;
        TextView categoryName;
        TextView cost;

        SimpleViewHolder(View itemView) {
            super(itemView);
            expenseName = itemView.findViewById(R.id.expenseName);
            categoryName = itemView.findViewById(R.id.category);
            cost = itemView.findViewById(R.id.cost);
        }

    }

    private List<Expense> items;
    private Context mContext;

    public ExpenseAdapter(Context context, List<Expense> items) {
        this.mContext = context;
        this.items = items;
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
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}