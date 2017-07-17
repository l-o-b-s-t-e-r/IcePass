package com.yamert.icepass.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.csm.R;
import com.yamert.icepass.models.Visitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lobster on 21.03.17.
 */

public class VisitorsAdapter extends RecyclerView.Adapter<VisitorsAdapter.ViewHolder> {

    private List<Visitor> mVisitors = new ArrayList<>();

    @Override
    public VisitorsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int layoutId) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_visitor, parent, false);

        return new VisitorsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VisitorsAdapter.ViewHolder holder, int position) {
        holder.setContent(mVisitors.get(position));
    }

    public void updateVisitors(List<Visitor> visitors) {
        mVisitors = visitors;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mVisitors.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private Visitor mVisitor;

        @BindView(R.id.name)
        TextView visitorName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setContent(Visitor visitor) {
            mVisitor = visitor;
            visitorName.setText(mVisitor.getName());
        }
    }

}
