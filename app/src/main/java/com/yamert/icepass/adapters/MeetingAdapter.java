package com.yamert.icepass.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.csm.R;
import com.yamert.icepass.App;
import com.yamert.icepass.models.Meeting;
import com.yamert.icepass.ui.detail.one.MeetingDetailOneActivity;
import com.yamert.icepass.ui.detail.one.MeetingDetailOnePresenter;
import com.yamert.icepass.ui.detail.zero.MeetingDetailZeroActivity;
import com.yamert.icepass.ui.detail.zero.MeetingDetailZeroPresenter;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yamert.icepass.db.DataManager.LOOKUP_TYPE;
import static com.yamert.icepass.ui.main.MainActivity.ACTUAL;

/**
 * Created by Lobster on 06.02.17.
 */

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy HH:mm");

    private List<Meeting> mMeetings;
    private int meetingType;
    private String people;
    private Activity mContext;

    public MeetingAdapter(int type, Activity context) {
        meetingType = type;
        people = (type == ACTUAL ? App.getInstance().getString(R.string.people_actual) : App.getInstance().getString(R.string.people_past));
        mContext = context;
        mMeetings = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int layoutId) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meeting, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setContent(mMeetings.get(position));
    }

    public void updateMeeting(Meeting meeting) {
        for (int i = 0; i < mMeetings.size(); i++) {
            if (mMeetings.get(i).getId() == meeting.getId()) {
                mMeetings.set(i, meeting);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void addAllMeetings(List<Meeting> meetings) {
        mMeetings.addAll(meetings);
        notifyDataSetChanged();
    }

    public void clearMeetings() {
        mMeetings.clear();
    }

    @Override
    public int getItemCount() {
        return mMeetings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.visitors_count)
        TextView visitorsCount;

        private Meeting mMeeting;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.meeting_card)
        void onCardClick() {
            switch (App.sharedPreferences().getInt(LOOKUP_TYPE, 0)) {
                case 0:
                    MeetingDetailZeroActivity.startActivity(mMeeting.getId(), meetingType, mContext);
                    break;
                case 1:
                    MeetingDetailOneActivity.startActivity(mMeeting.getId(), meetingType, mContext);
                    break;

                default:
                    MeetingDetailZeroActivity.startActivity(mMeeting.getId(), meetingType, mContext);
            }
        }

        void setContent(Meeting meeting) {
            mMeeting = meeting;
            title.setText(meeting.getTitle());
            visitorsCount.setText(meeting.getStarts() != null ? WordUtils.capitalize(sdf.format(meeting.getStarts())) : "");
        }
    }
}
