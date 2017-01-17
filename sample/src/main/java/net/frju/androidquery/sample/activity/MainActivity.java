package net.frju.androidquery.sample.activity;

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

import net.frju.androidquery.gen.Q;
import net.frju.androidquery.models.Contact;
import net.frju.androidquery.operation.function.CursorResult;
import net.frju.androidquery.operation.keyword.OrderBy;
import net.frju.androidquery.sample.R;
import net.frju.androidquery.sample.adapter.CommentAdapter;
import net.frju.androidquery.sample.adapter.ContactsAdapter;
import net.frju.androidquery.sample.loader.ContactsLoader;
import net.frju.androidquery.sample.model.Comment;
import net.frju.androidquery.sample.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static net.frju.androidquery.operation.join.Join.innerJoin;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.comment_count)
    TextView mCommentsCount;

    @BindView(R.id.enter_comment)
    EditText mEnterCommentEditText;

    @BindView(R.id.comments)
    RecyclerView mCommentsRecyclerView;

    @BindView(R.id.contacts)
    RecyclerView mContactsRecyclerView;

    private CommentAdapter mCommentAdapter;
    private ContactsAdapter mContactsAdapter;

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private final LoaderManager.LoaderCallbacks<CursorResult<Contact>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<CursorResult<Contact>>() {

        @Override
        public Loader<CursorResult<Contact>> onCreateLoader(int id, Bundle args) {
            ContactsLoader loader = new ContactsLoader(MainActivity.this);
            loader.setUpdateThrottle(300);
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<CursorResult<Contact>> loader, CursorResult<Contact> data) {
            mContactsAdapter.setContacts(data);
        }

        @Override
        public void onLoaderReset(Loader<CursorResult<Contact>> loader) {
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

        getContentResolver().registerContentObserver(Q.COMMENT.getContentUri(), true, new ContentObserver(new Handler()) {
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
        Q.USER.count()
                .rx2()
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
        user.id = 1;
        user.username = "Sam";

        mCompositeDisposable.add(Q.USER.insert(user)
                .rx2()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer nbInserted) throws Exception {
                        refreshComments();
                    }
                }));
    }

    private void countComments() {
        mCompositeDisposable.add(Q.COMMENT.count()
                .rx2()
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

        mCompositeDisposable.add(Q.COMMENT.insert(comment)
                .rx2()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer nbInserted) throws Exception {
                        refreshComments();
                    }
                }));

        mEnterCommentEditText.getText().clear();
    }

    private void refreshComments() {
        mCompositeDisposable.add(Q.COMMENT.select()
                .join(innerJoin(Comment.class, Q.COMMENT.USER_ID, User.class, Q.USER.ID))
                .orderBy(Comment.class.getSimpleName() + '.' + Q.COMMENT.TIMESTAMP, OrderBy.Order.DESC)
                .rx2()
                .subscribe(new Consumer<CursorResult<Comment>>() {
                    @Override
                    public void accept(CursorResult<Comment> comments) throws Exception {
                        mCommentAdapter.setComments(comments.toArray());
                    }
                }));
    }
}
