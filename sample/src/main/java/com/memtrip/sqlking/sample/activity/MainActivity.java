package com.memtrip.sqlking.sample.activity;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.memtrip.sqlking.model.Contact;
import com.memtrip.sqlking.operation.function.Count;
import com.memtrip.sqlking.operation.function.Insert;
import com.memtrip.sqlking.operation.function.Result;
import com.memtrip.sqlking.operation.function.Select;
import com.memtrip.sqlking.operation.keyword.OrderBy;
import com.memtrip.sqlking.sample.App;
import com.memtrip.sqlking.sample.R;
import com.memtrip.sqlking.sample.adapter.CommentAdapter;
import com.memtrip.sqlking.sample.adapter.ContactsAdapter;
import com.memtrip.sqlking.sample.loader.ContactsLoader;
import com.memtrip.sqlking.sample.model.Comment;
import com.memtrip.sqlking.sample.model.User;
import com.memtrip.sqlking.sample.model.gen.Q;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.memtrip.sqlking.operation.clause.On.on;
import static com.memtrip.sqlking.operation.join.InnerJoin.innerJoin;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.comment_count)
    TextView mCommentsCount;

    @Bind(R.id.enter_comment)
    EditText mEnterCommentEditText;

    @Bind(R.id.comments)
    RecyclerView mCommentsRecyclerView;

    @Bind(R.id.contacts)
    RecyclerView mContactsRecyclerView;

    private CommentAdapter mCommentAdapter;
    private ContactsAdapter mContactsAdapter;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private final LoaderManager.LoaderCallbacks<Result<Contact>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Result<Contact>>() {

        @Override
        public Loader<Result<Contact>> onCreateLoader(int id, Bundle args) {
            ContactsLoader loader = new ContactsLoader(MainActivity.this);
            loader.setUpdateThrottle(300);
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Result<Contact>> loader, Result<Contact> data) {
            mContactsAdapter.setContacts(data);
        }

        @Override
        public void onLoaderReset(Loader<Result<Contact>> loader) {
//            BaseAdapter adapter = getListAdapter();
//            if (adapter != null && adapter instanceof CursorAdapter) {
//                ((CursorAdapter) adapter).swapCursor(null);
//            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
        ButterKnife.bind(this);

        mCommentAdapter = new CommentAdapter();
        mContactsAdapter = new ContactsAdapter();

        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCommentsRecyclerView.setAdapter(mCommentAdapter);

        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mContactsRecyclerView.setAdapter(mContactsAdapter);

        mEnterCommentEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    insert();
                    return true;
                }

                return false;
            }
        });

        getContentResolver().registerContentObserver(App.getInstance().getContentDatabaseProvider().getUri(Comment.class), true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                countComments();
            }
        });

        checkUserExists();
        countComments();

        getLoaderManager().initLoader(0, null, mLoaderCallbacks);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

    private void checkUserExists() {
        Count.getBuilder()
                .rx(User.class, App.getInstance().getContentDatabaseProvider())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long count) throws Exception {
                        if (count == 0) {
                            insertUser();
                        } else {
                            refreshComments();
                        }
                    }
                });
    }

    private void insertUser() {
        User user = new User();
        user._id = 1;
        user.username = "Sam";

        mCompositeDisposable.add(Insert.getBuilder().values(user)
                .rx(App.getInstance().getLocalDatabaseProvider())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Void>() {
                    @Override
                    public void accept(Void unused) throws Exception {
                        refreshComments();
                    }
                }));
    }

    private void countComments() {
        mCompositeDisposable.add(Count.getBuilder()
                .rx(Comment.class, App.getInstance().getLocalDatabaseProvider())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long count) throws Exception {
                        mCommentsCount.setText(getResources().getString(R.string.count, count.toString()));
                    }
                }));
    }

    @OnClick(R.id.insert_comment)
    public void insert() {
        Comment comment = new Comment();
        comment.body = mEnterCommentEditText.getText().toString();
        comment.timestamp = System.currentTimeMillis();
        comment.userId = 1;

        mCompositeDisposable.add(Insert.getBuilder().values(comment)
                .rx(App.getInstance().getContentDatabaseProvider())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Void>() {
                    @Override
                    public void accept(Void unused) throws Exception {
                        refreshComments();
                    }
                }));

        mEnterCommentEditText.getText().clear();
    }

    private void refreshComments() {
        mCompositeDisposable.add(Select.getBuilder()
                .join(innerJoin(User.class, on(Comment.class.getSimpleName() + '.' + Q.Comment.USER_ID, User.class.getSimpleName() + '.' + Q.User._ID)))
                .orderBy(Comment.class.getSimpleName() + '.' + Q.Comment.TIMESTAMP, OrderBy.Order.DESC)
                .rx(Comment.class, App.getInstance().getLocalDatabaseProvider())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result<Comment>>() {
                    @Override
                    public void accept(Result<Comment> comments) throws Exception {
                        mCommentAdapter.setComments(comments.asArray());
                    }
                }));
    }
}
