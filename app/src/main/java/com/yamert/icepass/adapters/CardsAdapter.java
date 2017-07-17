package com.yamert.icepass.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firebase.csm.R;
import com.yamert.icepass.App;
import com.yamert.icepass.models.Card;
import com.yamert.icepass.models.Visitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Lobster on 21.03.17.
 */

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private List<Card> mCards = new ArrayList<>();

    private int chosenItem = 0;

    @Override
    public CardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int layoutId) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);

        return new CardsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardsAdapter.ViewHolder holder, int position) {
        holder.setContent(mCards.get(position), position);
    }

    public void updateVisitors(List<Card> cards) {
        mCards = cards;
        notifyDataSetChanged();
    }

    public Card getChosenCardNumber() {
        return mCards.get(chosenItem);
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout)
        LinearLayout layout;

        @BindView(R.id.card_number)
        TextView cardNumberView;

        @BindView(R.id.seller_name)
        TextView sellerNameView;

        @BindView(R.id.seller_id)
        TextView sellerIdView;

        private int position;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemChanged(chosenItem);

                    cardNumberView.setTextColor(App.getInstance().getResources().getColor(R.color.white));
                    sellerNameView.setTextColor(App.getInstance().getResources().getColor(R.color.white));
                    sellerIdView.setTextColor(App.getInstance().getResources().getColor(R.color.white));
                    layout.setBackgroundColor(App.getInstance().getResources().getColor(R.color.colorAccent));

                    chosenItem = position;
                }
            });
        }

        void setContent(Card card, int pos) {
            position = pos;
            cardNumberView.setText(card.getCardNumber());

            if (card.getSellerName() == null || card.getSellerName().isEmpty()) {
                sellerNameView.setVisibility(View.GONE);
            } else {
                sellerNameView.setVisibility(View.VISIBLE);
                sellerNameView.setText(card.getSellerName());
            }

            if (card.getSellerExternalId() == null || card.getSellerExternalId().isEmpty()) {
                sellerIdView.setVisibility(View.GONE);
            } else {
                sellerIdView.setVisibility(View.VISIBLE);
                sellerIdView.setText(card.getSellerExternalId());
            }

            if (chosenItem == pos) {
                cardNumberView.setTextColor(App.getInstance().getResources().getColor(R.color.white));
                sellerNameView.setTextColor(App.getInstance().getResources().getColor(R.color.white));
                sellerIdView.setTextColor(App.getInstance().getResources().getColor(R.color.white));
                layout.setBackgroundColor(App.getInstance().getResources().getColor(R.color.colorAccent));
            } else {
                cardNumberView.setTextColor(App.getInstance().getResources().getColor(R.color.colorAccent));
                sellerNameView.setTextColor(App.getInstance().getResources().getColor(R.color.colorAccent));
                sellerIdView.setTextColor(App.getInstance().getResources().getColor(R.color.colorAccent));
                layout.setBackgroundColor(App.getInstance().getResources().getColor(R.color.white));
            }
        }
    }

}
