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
import net.frju.androidquery.model.Contact;
import net.frju.androidquery.operation.function.Result;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static net.frju.androidquery.operation.condition.On.on;
import static net.frju.androidquery.operation.join.InnerJoin.innerJoin;

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

        getContentResolver().registerContentObserver(Q.Comment.getContentUri(), true, new ContentObserver(new Handler()) {
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
        Q.User.count()
                .rx()
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
        user.id = 1;
        user.username = "Sam";

        mCompositeDisposable.add(Q.User.insert(user)
                .rx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer nbInserted) throws Exception {
                        refreshComments();
                    }
                }));
    }

    private void countComments() {
        mCompositeDisposable.add(Q.Comment.count()
                .rx()
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

        mCompositeDisposable.add(Q.Comment.insertViaContentProvider(comment)
                .rx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer nbInserted) throws Exception {
                        refreshComments();
                    }
                }));

        mEnterCommentEditText.getText().clear();
    }

    private void refreshComments() {
        mCompositeDisposable.add(Q.Comment.select()
                .join(innerJoin(User.class, on(Comment.class.getSimpleName() + '.' + Q.Comment.USER_ID, User.class.getSimpleName() + '.' + Q.User.ID)))
                .orderBy(Comment.class.getSimpleName() + '.' + Q.Comment.TIMESTAMP, OrderBy.Order.DESC)
                .rx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result<Comment>>() {
                    @Override
                    public void accept(Result<Comment> comments) throws Exception {
                        mCommentAdapter.setComments(comments.toArray());
                    }
                }));
    }
}
