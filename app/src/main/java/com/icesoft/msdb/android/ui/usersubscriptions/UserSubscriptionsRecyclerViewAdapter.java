package com.icesoft.msdb.android.ui.usersubscriptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.model.UserSubscription;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.icesoft.msdb.android.model.UpcomingSession}.
 */
public class UserSubscriptionsRecyclerViewAdapter extends RecyclerView.Adapter<UserSubscriptionsRecyclerViewAdapter.ViewHolder> {

    private final UserSubscriptionsViewModel userSubscriptionsViewModel;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public UserSubscriptionsRecyclerViewAdapter(Context context) {
        userSubscriptionsViewModel =
                new ViewModelProvider((FragmentActivity)context).get(UserSubscriptionsViewModel.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_subscription_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UserSubscription userSubscription = userSubscriptionsViewModel.getUserSubscriptions().get(position);
        holder.mSeriesNameView.setText(userSubscription.getSeriesEditionName());
        holder.mPracticesSwitch.setChecked(userSubscription.isPracticeSessions());
        holder.mQualifyingSwitch.setChecked(userSubscription.isQualiSessions());
        holder.mRacesSwitch.setChecked(userSubscription.isRaces());
        holder.mFifteenMinsSwitch.setChecked(userSubscription.isFifteenMinWarning());
        holder.mOneHourSwitch.setChecked(userSubscription.isOneHourWarning());
        holder.mThreeHoursSwitch.setChecked(userSubscription.isThreeHoursWarning());

        Glide.with(holder.mView)
            .load(userSubscription.getSeriesLogo())
            .fitCenter()
            .into(holder.mViewSeriesLogo);

        holder.mPracticesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userSubscription.setPracticeSessions(isChecked);
        });
        holder.mQualifyingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userSubscription.setQualiSessions(isChecked);
        });
        holder.mRacesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userSubscription.setRaces(isChecked);
        });
        holder.mFifteenMinsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userSubscription.setFifteenMinWarning(isChecked);
        });
        holder.mOneHourSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userSubscription.setOneHourWarning(isChecked);
        });
        holder.mThreeHoursSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userSubscription.setThreeHoursWarning(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return userSubscriptionsViewModel.getUserSubscriptions().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mViewSeriesLogo;
        public final TextView mSeriesNameView;
        public final SwitchMaterial mPracticesSwitch;
        public final SwitchMaterial mQualifyingSwitch;
        public final SwitchMaterial mRacesSwitch;
        public final SwitchMaterial mFifteenMinsSwitch;
        public final SwitchMaterial mOneHourSwitch;
        public final SwitchMaterial mThreeHoursSwitch;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mViewSeriesLogo = view.findViewById(R.id.userSubsSeriesLogoImageView);
            mSeriesNameView = view.findViewById(R.id.seriesNameTextView);
            mPracticesSwitch = view.findViewById(R.id.practicesSwitch);
            mQualifyingSwitch = view.findViewById(R.id.qualifyingSwitch);
            mRacesSwitch = view.findViewById(R.id.racesSwitch);
            mFifteenMinsSwitch = view.findViewById(R.id.fifteenMinsSwitch);
            mOneHourSwitch = view.findViewById(R.id.oneHourSwitch);
            mThreeHoursSwitch = view.findViewById(R.id.threeHoursSwitch);
        }

    }
}