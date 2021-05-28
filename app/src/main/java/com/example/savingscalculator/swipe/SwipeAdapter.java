package com.example.savingscalculator.swipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.savingscalculator.MainActivity;
import com.example.savingscalculator.Plan;
import com.example.savingscalculator.R;

import java.util.ArrayList;

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.SwipeViewHolder> {

    private final Context context;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private ArrayList<Plan> Plans;

    /**
     * Constructor for the adapter
     *
     * @param context
     * @param Plans
     */
    public SwipeAdapter(Context context, ArrayList<Plan> Plans) {
        this.context = context;
        this.Plans = Plans;
    }

    /**
     * updates the plans to be displayed in the recyclerview
     *
     * @param Plans
     */
    public void setPlans(ArrayList<Plan> Plans) {
        this.Plans = new ArrayList<>();
        this.Plans = Plans;
        notifyDataSetChanged();
    }

    /**
     * This method is called when the user wants to remove a plan from the screen.
     * It removes the specific plan and then notifies the adapter that its contents have changed.
     *
     * @param position
     */
    public void remove(int position) {
        Plans.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @NonNull
    @Override
    public SwipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plan_swipe, parent, false);
        return new SwipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SwipeViewHolder holder, int position) {

        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(Plans.get(position).getName()));
        viewBinderHelper.closeLayout(String.valueOf(Plans.get(position).getName()));
        holder.bindData(Plans.get(position));

    }

    @Override
    public int getItemCount() {
        return Plans.size();
    }


    /**
     * The class that displayes the name of each individual class and the edit, delete buttons.
     */
    class SwipeViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final SwipeRevealLayout swipeRevealLayout;


        public SwipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            TextView txtEdit = itemView.findViewById(R.id.txtEdit);
            TextView txtDelete = itemView.findViewById(R.id.txtDelete);
            swipeRevealLayout = itemView.findViewById(R.id.swipelayout);

            //Handling click event

            txtEdit.setOnClickListener(v -> {
                //Toast.makeText(context, "Edit is Clicked", Toast.LENGTH_SHORT).show();
                Intent editIntent = new Intent(context, MainActivity.class);
                editIntent.putExtra("PlanDetails", Plans);
                editIntent.putExtra("PlanKey", getAdapterPosition());
                context.startActivity(editIntent);


            });
            txtDelete.setOnClickListener(v -> {
                //Toast.makeText(context, "Delete is Clicked", Toast.LENGTH_SHORT).show();
                remove(getAdapterPosition());

            });

        }

        void bindData(Plan plan) {
            textView.setText(plan.getName());
        }
    }
}
