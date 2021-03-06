package net.frju.androidquery.sample.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.frju.androidquery.models.Contact;
import net.frju.androidquery.operation.function.CursorResult;
import net.frju.androidquery.sample.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private CursorResult<Contact> mContacts;

    public void setContacts(CursorResult<Contact> contacts) {
        this.mContacts = contacts;
        notifyDataSetChanged();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_adapter, parent, false);

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

        @BindView(R.id.contact_icon)
        ImageView contactIcon;
        @BindView(R.id.contact_name)
        TextView contactName;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void populate(Contact contact) {
            contactName.setText(contact.displayName);
            Glide.with(contactIcon.getContext()).load(contact.photoThumbnailUri).into(contactIcon);
        }
    }
}
