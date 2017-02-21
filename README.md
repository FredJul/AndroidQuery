AndroidQuery
======================

AndroidQuery is an Java/Kotlin Android ORM for SQLite and ContentProvider.

While you can use other good ORMs like DbFlow/Freezer/sqlitemagic/whatever/... for your local databases, the goal of AndroidQuery is to work with several solutions/language/framework without forcing you to use one of them.
For example you have:
- ContentProvider support (including some defaults models for easily accessing Android data like contacts)
- Android Loaders support (both normal and support-v4) and ContentObserver support, even without ContentProvider
- RxJava support (both version 1 and 2)
- Raw queries and custom field type support
- Kotlin support

It is also very lightweight and efficient (code generation via annotation processing).

#Setup#

###Gradle dependencies###

```groovy
ext.androidquery_version = '1.5.1'

dependencies {
    annotationProcessor "net.frju.androidquery:android-query-preprocessor:${androidquery_version}"
    compile "net.frju.androidquery:android-query:${androidquery_version}"
}
```

If you want to use RxJava1 or RxJava2 you also need to add some of the following lines:

```groovy
    compile 'io.reactivex:rxjava:1.2.3' // For RxJava1 and rx() method
    compile 'io.reactivex:rxandroid:1.2.1' // For RxJava1 and rx() method

    compile 'io.reactivex.rxjava2:rxjava:2.0.2' // For RxJava2 and rx2() method
    compile 'io.reactivex.rxjava2:rxandroid:2.0.1' // For RxJava2 and rx2() method
```

###Initialize the ORM###

You need to initialize the ORM with a context to make it work properly. A good way to do it is by defining your Application object:

```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Q.init(this);
    }
}
```

And then declare it into your AndroidManifest.xml:

```xml
<application
        android:name=".App">
```

###Define your local database and models###

You first need to declare your database. A `BaseLocalDatabaseProvider` is using SQLite to store data.

```java
public class LocalDatabaseProvider extends BaseLocalDatabaseProvider {

    public LocalDatabaseProvider(Context context) {
        super(context);
    }

    @Override
    protected String getDbName() {
        return "local_models";
    }

    @Override
    protected int getDbVersion() {
        return 1;
    }

    @Override
    protected Resolver getResolver() {
        return Q.getResolver();
    }

    @Override
    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion); // by default, AndroidQuery will create new models and add new fields

        // Put here your migration code, do that directly on the db object, no AndroidQuery methods
    }

    @Override
    protected void onPostUpgrade(int oldVersion, int newVersion) {
        // Here you can directly call all kind of AndroidQuery methods, including MODEL.insert()/update()/delete()...
    }
}
```

Then models are defined by POJOs that are annotated with `@DbModel`. Model fields are annotated with `@DbField`.

<table style="width:100%; border-collapse: collapse;" >
  <tr>
    <th>Java</th>
    <th>Kotlin</th>
  </tr>
  <tr style="background: none">
    <td style="padding:0; margin:0; border:none; width:50%;">
      <pre lang="java"><code class="language-java">
@DbModel(databaseProvider = LocalDatabaseProvider.class)
public class User {

    @DbField(index = true, dbName = "_id",
            primaryKey = true, autoIncrement = true)
    public int id;
    
    @DbField
    public String username;
    
    @DbField
    public long timestamp;
    
    @DbField
    public boolean isRegistered;
    
    @DbField
    public byte[] profilePicture;
}
      </code></pre>
    </td>
    <td style="padding:0; margin:0; border:none; width:50%;">
      <pre lang="java"><code class="language-java">
@DbModel(databaseProvider = LocalDatabaseProvider::class)
class Feed { // data class not supported

    @DbField(primaryKey = true, autoIncrement = true)
    var id = 0
    
    @DbField(unique = true)
    var username: String? = null
}
      </code></pre>
    </td>
  </tr>
</table>

###Use custom types###

By default AndroidQuery supports several Java/Android types, but you are not restricted to them and can define some additional types:

```java
@TypeConverter(dbClass = String.class, modelClass = Uri.class)
public class UriConverter extends BaseTypeConverter<String, Uri> {

    @Override
    public String convertToDb(Uri model) {
        return model == null ? null : model.toString();
    }

    @Override
    public Uri convertFromDb(String data) {
        return data == null ? null : Uri.parse(data);
    }
}
```

###Supported constraints###

Here are the supported constraints:
- primary key (only on one field)
- autoincrement
- unique (both on one field or several thanks to the uniqueGroup attribute)
- foreign key

#Queries#

By convenience all examples of this section will be done synchronously. For asynchronous queries, please refer to the corresponding section. 

###Select###

For a `select()` query you will get back a `CursorResult` object, which needs to be closed after use. You can use a try-with-resources statement for that:

```java
// SELECT * FROM User;
try(CursorResult<User> users = USER.select().query()) {
    int count = users.getCount();
    User secondUser = users.get(1);
    for (User user : users) {
        //...
    }
} catch (NullPointerException e) {
    // can happen if the database returned a null cursor
}
```

You can also limit/order the results, or directly retrieve an array or a list from the `CursorResult` object:

```java
// SELECT * FROM user ORDER BY username DESC LIMIT 10
User[] users = USER.select()
        .limit(10) // you can limit or order the result
        .orderBy(USER.USERNAME, OrderBy.Order.DESC)
        .query()
        .toArray(); // toList() also available, but a bit less efficient
```

However be careful: this is less efficient than directly using the `CursorResult` object since it needs to read and copy everything in memory.
Calling `toArray()` or `toList()` methods will automatically close the `CursorResult` object for you.

###Insert###

```java
User user = new User();
user.setUsername("12345678");
user.setIsRegistered(true);
user.setTimestamp(System.currentTimeMillis());

// INSERT INTO User (username, isRegistered, timestamp) VALUES ('12345678',true,632348968244);
USER.insert(user).query();
```

###Update###

```java
ContentValues contentValues = new ContentValues();
contentValues.put(USER.IS_REGISTERED, true);
contentValues.put(USER.TIMESTAMP, System.currentTimeMillis());

// UPDATE User SET isRegistered = 'true', timestamp = '123456789'
int rowsUpdated = USER.update()
        .values(contentValues)
        .query();
```

###Save###

```java
USER.save(user).query();
```

The `save()` method will either insert the data if not in database or will update it, since this can be slower you should use that method only if you don't know if the data has been already inserted.
You need to define a primary key in your model to be able to use the `save()` method.

###Delete###

```java
// DELETE FROM User;
int rowsDeleted = USER.delete().query(); // delete all users, can add a where() if necessary
```

###Count###

```java
// SELECT Count(*) FROM User;
int count = USER.count().query();
```

###Raw Query###

```java
// Raw queries;
Cursor cursor = USER.raw("UPDATE User SET isRegistered = 'true', timestamp = '123456789'").query;
if (cursor != null) {
    // ...
    cursor.close();
}
```

###Where clauses###

The `Where` class is used to build up the where query:

```java
// SELECT * FROM User WHERE isRegistered = 'true';
User[] users = USER.select()
        .where(Where.field(USER.IS_REGISTERED).isEqualTo(true))
        .query()
        .toArray();
```

```java
// SELECT * FROM User WHERE username LIKE 'jo%'
User[] users = USER.select()
        .where(Where.field(USER.USERNAME).isLike("jo%"))
        .query()
        .toArray();
```

```java
// SELECT * FROM User WHERE username IN ("sam","josh");
User[] users = USER.select()
        .where(Where.field(USER.USERNAME).isIn("sam", "josh"))
        .query()
        .toArray();
```

```java
// SELECT * FROM User WHERE ((username = "sam" OR username = "angie") AND timestamp >= 1234567890);
User[] users = USER.select()
		.where(Where.field(USER.USERNAME).isEqualTo("sam")
                        .or(Where.field(USER.USERNAME).isEqualTo("angie")),
               Where.field(USER.TIMESTAMP).isMoreThanOrEqualTo(1234567890))
        .query()
        .toArray();
```

#Relations between models#

Sometimes you want to automatically populate data of a model from another one (ie. get all posts of a user). There is actually two ways of doing so.

###Variable initializer###

You can initialize some variable thanks to the `InitMethod` annotation.

```java
@DbModel(databaseProvider = LocalDatabaseProvider.class)
public class User {
    @DbField(primaryKey = true, dbName = "_id")
    public long id;

    public Post[] posts; // No @DbField annotation here. "Post" class is declared as another model.

    @InitMethod
    public void initPosts() {
        // Do another query here. Can call queryAndInit() if and only if there is no circular reference
        posts = POST.select().where(Where.field(POST.USER_ID).isEqualTo(id)).queryAndInit();
    }
}
```

When you call `queryAndInit()` or similar RxJava methods, you will actually execute all init methods. This can be noticeably slower depending of what you do inside (could even be a network request).

Be careful to not do any circular reference.

###Local database and joins###

If you do not need a list of sub models and if both model share the same `BaseLocalDatabaseProvider` (does not work with `BaseContentDatabaseProvider`), you could use a join.
This is far more efficient than the previous method since it does not add any database request.

Joins can be performed using the `innerJoin()`, `leftOutJoin()`, `crossInnerJoin()`, `naturalInnerJoin()`, `naturalLeftOuterJoin()` methods.
The target model for the join must be defined as an @DbField, the object will be populated with any join results.

```java
@DbModel(databaseProvider = LocalDatabaseProvider.class)
public class Comment {
    @DbField(index = true)
    public int id;
    @DbField
    public long timestamp;
    @DbField
    public int userId;
    @DbField
    public User user; // The target model for a potential join
}

@DbModel(databaseProvider = LocalDatabaseProvider.class)
public class User {
    @DbField(index = true)
    public int id;
    @DbField
    public String username;
}

Comment[] comments = COMMENT.select()
        .join(innerJoin(Comment.class, COMMENT.USER_ID, User.class, USER.ID))
        .orderBy(Comment.class.getSimpleName() + '.' + COMMENT.TIMESTAMP, OrderBy.Order.DESC)
        .query()
        .toArray();
        
User user = comments[0].getUser(); // The nested User object is populated by the join
```

#Asynchronous queries#

For an asynchronous query (to not block the UI), you can notably use RxJava (v1 or v2) or Kotlin Anko library.

<table style="width:100%; border-collapse: collapse;" >
  <tr>
    <th>Java with RxJava2</th>
    <th>Kotlin with Anko</th>
  </tr>
  <tr style="background: none">
    <td style="padding:0; margin:0; border:none; width:50%;">
    
    It is recommended to put all the returned Disposable into a CompositeDisposable and clear it inside the activity onDestroy():
    
      <pre lang="java"><code class="language-java">
private final CompositeDisposable mCompositeDisposable
                = new CompositeDisposable();

private void doQuery() {
    mCompositeDisposable.add(USER.select()
        .rx2First() // we get the first user only
        .flatMap(new Function<User, Single<CursorResult<Comment>>>() {
            @Override
            public Single<CursorResult<Comment>> apply(User user)
                    throws Exception {
                return COMMENT.select()
                    .where(Where.field(COMMENT.USER_ID)
                    .isEqualTo(user.id))
                    .rx2();
            }
        })
        .subscribe(new Consumer<CursorResult<Comment>>() {
            @Override
            public void accept(CursorResult<Comment> comments)
                    throws Exception {
                // do something with the comments of first user
                // you are in UI thread here
            }
        }));
}

@Override
protected void onDestroy() {
    super.onDestroy();
    mCompositeDisposable.clear();
}
      </code></pre>
      
By default RxJava queries are always executed on Schedulers.io() and the result given on AndroidSchedulers.mainThread() unless you call the methods subscribeOn() and observeOn().

    </td>
    <td style="padding:0; margin:0; border:none; width:50%;">
      <pre lang="kotlin"><code class="language-kotlin">

doAsync {
    val firstUser = USER.select()
        .queryFirst()

    val comments = COMMENT.select()
        .where(Where.field(COMMENT.USER_ID)
        .isEqualTo(firstUser.id))
        .query()

    uiThread {
        // do something with the comments of first user
        // you are in UI thread here
    }
}
      </code></pre>
    </td>
  </tr>
</table>

#Listening data changes#

To listen to the data changes in your activity/fragment, you can create a Android loader this way:
```java
public class UsersLoader extends BaseSelectLoader<User> {

    public UsersLoader(Context context) {
        super(context);
    }

    @Override
    public CursorResult<User> doSelect() {
        return USER.select().query();
    }
}
```
```java
public class ExampleActivity extends Activity {

    private static final int USER_LOADER_ID = 0;

    private final LoaderManager.LoaderCallbacks<CursorResult<User>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<CursorResult<User>>() {

        @Override
        public Loader<CursorResult<User>> onCreateLoader(int id, Bundle args) {
            UsersLoader loader = new UsersLoader(ExampleActivity.this);
            loader.setUpdateThrottle(100);
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<CursorResult<User>> loader, CursorResult<User> data) {
            // ...
        }

        @Override
        public void onLoaderReset(Loader<CursorResult<User>> loader) {
            // ...
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(USER_LOADER_ID, null, mLoaderCallbacks);
    }
}
```

You can also use a simple `ContentObserver`
```java
private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // ...
        }
    });

// Listen to all changes (for example on your activity's onCreate())
// You can also call USER.getContentUri(model) to listen to only one model
getContentResolver().registerContentObserver(USER.getContentUri(), true, mContentObserver);

// Unregister when not needed anymore (potentially in your activity's onDestroy())
getContentResolver().unregisterContentObserver(mContentObserver);
```

You can instanciate an `ThrottledContentObserver` instead if you want to group the calls for performance reasons.

```java
private final ContentObserver mContentObserver = new ThrottledContentObserver(new Handler(), 100) {
        @Override
        public void onChangeThrottled() {
            // ...
        }
    });
```

Please note that this is working even without setting any ContentProvider for your models. Be careful: you will not be notified if you modify the data with raw queries.

###Database operation hooks###

If you just need to maintain the data coherence or generate some default value you can inherits your model from `ModelListener`.

```java
@DbModel(databaseProvider = LocalDatabaseProvider.class)
public class User implements ModelListener {

    @DbField(primaryKey = true)
    public String id;
    @DbField
    public long creationDate;

    @Override
    public void onPreInsert() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        if (creationDate == 0) {
            creationDate = System.currentTimeMillis();
        }
    }

    @Override
    public void onPreUpdate() {
    }

    @Override
    public void onPreDelete() {
    }
}
```

Again, be careful: you will not be notified if you modify the data with raw queries.

#Expose your models to an external application#

Your data can also be accessed by an external application through a ContentProvider.
To do so, you first need to declare the authority of your `BaseLocalDatabaseProvider`:

```java
public class LocalDatabaseProvider extends BaseLocalDatabaseProvider {

    @Override
    protected String getAuthority() {
        return "net.frju.androidquery.sample";
    }

    ...

}
```

Then declare your ContentProvider with that authority:

```xml
<application
    android:name=".App">
    <provider
        android:name="net.frju.androidquery.gen.USER$ContentProvider"
        android:authorities="net.frju.androidquery.sample"
        android:exported="true" />
</application>
```

If several model share the same `DatabaseProvider`, they will all be available with only one ContentProvider declaration.

From the external application, you will be able to access to the data either by manual `ContentProvider` queries, or either by setting up AndroidQuery in that project as well (see below).

#Access to some external data#

You can as well declare some models which are actually stored into another application.
AndroidQuery will access these data through a ContentProvider.

```java
public class UserContentDatabaseProvider extends BaseContentDatabaseProvider {

    public UserContentDatabaseProvider(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    protected String getAuthority() {
        return "net.frju.androidquery.sample"; // you want to access to models stored into the sample app
    }

    @Override
    protected Resolver getResolver() {
        return Q.getResolver();
    }
}
```

```java
@DbModel(databaseProvider = UserContentDatabaseProvider.class)
public class User {
    @DbField(primaryKey = true, dbName = "_id", autoIncrement = true)
    public int id;
}
```

Then you can query that model in the same way. However, please note that raw queries and joins are not available for external model.

#Default Android models#

AndroidQuery also provide a library which allows you to easily access to default Android ContentProviders. You need to add `android-query-models` into your dependencies.
```groovy
dependencies {
    annotationProcessor "net.frju.androidquery:android-query-preprocessor:${androidquery_version}"
    compile "net.frju.androidquery:android-query-models:${androidquery_version}"
}
```

Currently the supported models are `Contact`, `RawContact`, `RawContactData` and `BlockedNumber`.

You need to add the initialization of the corresponding Q class in your `Application` object this way:

```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        net.frju.androidquery.models.gen.Q.init(this); // for Android default models
        Q.init(this); // only if you also have your own models
    }
}
```

Then you can queries the lib models as you would do with your own models:
```java
Contact[] contacts = CONTACT.select()
         .where(Where.field(CONTACT.IN_VISIBLE_GROUP).isEqualTo(true))
         .orderBy(new OrderBy(CONTACT.DISPLAY_NAME, OrderBy.Order.ASC, OrderBy.Collate.LOCALIZED))
         .query()
         .toArray();
```

#TODO#
- Support for transactions
- Support for more constraints
- Support for Trigger
- Better default database updater (which also adds new constraints)
- Improve javadoc and add annotations like @NotNull
- Support for more types by default (ArrayList<String>, Bitmap, byte, Byte, Byte[], Set/Map, ...)
- Support for avg, sum, min, max, ...
- Add more android models
