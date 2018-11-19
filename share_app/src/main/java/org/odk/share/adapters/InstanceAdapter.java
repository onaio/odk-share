package org.odk.share.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.odk.share.R;
import org.odk.share.preferences.PreferenceKeys;
import org.odk.share.provider.InstanceProviderAPI;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by laksh on 5/20/2018.
 */

public class InstanceAdapter extends RecyclerView.Adapter<InstanceAdapter.InstanceHolder> {

    private Cursor cursor;
    private Context context;
    private final OnItemClickListener listener;
    private LinkedHashSet<Long> selectedInstances;
    private List<Instance> instances;

    public InstanceAdapter(Context context, Cursor cursor, OnItemClickListener listener,
                           LinkedHashSet<Long> selectedInstances) {
        this.context = context;
        this.cursor = cursor;

        instances = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String filter = prefs.getString(PreferenceKeys.KEY_SUBMISSION_FILTER, context.getString(R.string.default_submission_filter));
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String displayName = cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME));
            if (TextUtils.isEmpty(filter) || displayName.contains(filter)) {
                instances.add(new Instance(
                        cursor.getLong(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID)),
                        cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME)),
                        cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_SUBTEXT))
                ));
            }
            cursor.moveToNext();
        }

        this.listener = listener;
        this.selectedInstances = selectedInstances;
    }

    @NonNull
    @Override
    public InstanceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_checkbox, null);
        return new InstanceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstanceHolder holder, int position) {
        Instance instance = instances.get(holder.getAdapterPosition());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, holder.getAdapterPosition());
            }
        });
        holder.title.setText(instance.title);
        holder.subtitle.setText(instance.subtitle);
        holder.checkBox.setChecked(selectedInstances.contains(instance.id));
    }

    @Override
    public int getItemCount() {
        return instances.size();
    }



    public Cursor getCursor() {
        return cursor;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    static class Instance {
        public final long id;
        public final String title;
        public final String subtitle;

        public Instance(long id, String title, String subtitle) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    static class InstanceHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle) public TextView title;
        @BindView(R.id.tvSubtitle) public TextView subtitle;
        @BindView(R.id.checkbox) public CheckBox checkBox;

        InstanceHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
