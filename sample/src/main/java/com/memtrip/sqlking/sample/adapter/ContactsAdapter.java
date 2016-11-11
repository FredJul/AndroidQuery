package com.memtrip.sqlking.sample.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.memtrip.sqlking.operation.function.Result;
import com.memtrip.sqlking.sample.R;
import com.memtrip.sqlking.sample.model.Contact;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private Result<Contact> mContacts;

    public void setContacts(Result<Contact> contacts) {
        this.mContacts = contacts;
        notifyDataSetChanged();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_adapter, parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        holder.populate(contact);
    }

    @Override
    public int getItemCount() {
        return mContacts != null ? mContacts.getCount() : 0;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.comment_adapter_author)
        TextView author;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void populate(Contact contact) {
            author.setText(contact.displayName);
        }
    }
}
