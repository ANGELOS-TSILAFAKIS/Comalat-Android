package org.sakaiproject.customviews.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sakaiproject.api.events.EventsCollection;
import org.sakaiproject.api.pojos.assignments.Assignment;
import org.sakaiproject.sakai.R;

/**
 * Created by vspallas on 23/02/16.
 */
public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private Assignment assignment;
    private String siteId;
    private Context context;

    public AssignmentAdapter(Context context, Assignment assignment, String siteId) {
        this.context = context;
        this.assignment = assignment;
        this.siteId = siteId;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_item, parent, false);

        ViewHolder vh = new ViewHolder(v);

        vh.title = (TextView) v.findViewById(R.id.assignment_title);
        vh.date = (TextView) v.findViewById(R.id.assignment_date);
        vh.status = (TextView) v.findViewById(R.id.assignment_status);
        vh.membership = (TextView) v.findViewById(R.id.assignment_membership);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(assignment.getAssignmentsCollectionList().get(position).getTitle());

        holder.date.setText(assignment.getAssignmentsCollectionList().get(position).getOpenTimeString());
        holder.date.append("-");
        holder.date.append(assignment.getAssignmentsCollectionList().get(position).getCloseTimeString());

        holder.status.setText(assignment.getAssignmentsCollectionList().get(position).getStatus());

        if (siteId == null) {

            SharedPreferences prfs = context.getSharedPreferences("assignment_site_names", Context.MODE_PRIVATE);
            String siteName = prfs.getString(assignment.getAssignmentsCollectionList().get(position).getContext(), "");

            holder.membership.setText(siteName);
            holder.membership.setVisibility(View.VISIBLE);
        } else {
            holder.membership.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return assignment != null && assignment.getAssignmentsCollectionList() != null ? assignment.getAssignmentsCollectionList().size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, status, membership;

        public ViewHolder(View v) {
            super(v);
        }
    }
}
