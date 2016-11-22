AndroidQuery
======================

AndroidQuery is an Android SQLite ORM powered by an annotation preprocessor, tables are defined by @Table
annotations and CRUD classes expose an expressive api for executing SQLite queries.
####Gradle dependencies####
```groovy
dependencies {
    annotationProcessor 'net.frju.androidquery:android-query-preprocessor:1.0.0'
    compile 'net.frju.androidquery:android-query:1.0.0'
}
```

####Define your models###
SQL tables are defined by POJOs that are annotated with `@Table`. Table columns are annotated with `@Column`
and must have matching getter / setter methods i.e; `private String name;` must be accompanied by both a
`String getName()` and a `setName(String newVal)` method.

```java
@Table
public class User {
    @Column private String username;
    @Column private long timestamp;
    @Column private boolean isRegistered;
    @Column private byte[] profilePicture;

    public String getUsername() {
        return username;
    }

    public void setUsername(String newVal) {
        username = newVal;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long newVal) {
        timestamp = newVal;
    }

    public boolean getIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(boolean newVal) {
        isRegistered = newVal;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] newVal) {
        profilePicture = newVal;
    }
}
```

###Q###
The Q class is generated by the annotation preprocessor, it contains a DefaultResolver() method
which is required by SQLInit to create the database. Q also contains a series of static variables
that can be used to reference @Table columns. As a good practise these variables should be used
whenever you reference a table column.

```java
// an example of columns that are auto generated within a Q class
public static final String USERNAME = "username";
public static final String TIMESTAMP = "timestamp";
public static final String IS_REGISTERED = "isRegistered";
public static final String PROFILE_PICTURE = "profilePicture";

// the columns can be accessed directly from the Q class, e.g;
String usernameColumnFromUserTable = Q.User.USERNAME;
```

####Initialise the database####
AndroidQuery will create a database based on the POJOs that are annotated with @Table,
when these POJOs are changed or new POJOs are added, the version number argument must be incremented.
The `SQLProvider` instance that is returned from `SQLInit` must be kept throughout the lifecycle of your application,
it is required by the `execute()` and `rx()` methods. We recommend you attach inject it as a dependency or attach
it to your Application context.
NOTE: Incrementing the version number will drop and recreate the database.

```java
public void setUp() {
    SQLProvider provider = SQLInit.createDatabase(
         "AndroidQuery",
         1,
         new Q.DefaultResolver(),
         getContext(),
         User.class,
         Post.class
    );
}
```

####Querying the database####
The `Insert`, `Select`, `Update`, `Delete` and `Count` classes are used to query database tables,
they use a `getBuilder()` method to add clause and operation arguments. The Builder finishes by
using either the `execute()` method or the `rx()` method.

The `rx()` method returns an RxJava Observable.

```java
Select.getBuilder()
    .rx(User.class, sqlProvider)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<User[]>() {
        @Override
        public void call(User[] users) {
            // do something with the users
        }
    });
```

The `execute()` method returns results directly. NOTE: `execute()` will block the ui thread, 
we recommend you use RxJava.

```java
User user = new User();
user.setUsername("12345678");
user.setIsRegistered(true);
user.setTimestamp(System.currentTimeMillis());

// INSERT INTO User (username, isRegistered, timestamp) VALUES ('12345678',true,632348968244);
Insert.getBuilder().values(users).execute(User.class, sqlProvider);
```

```java
// SELECT * FROM User;
User[] users = Select.getBuilder().execute(User.class, sqlProvider);
```

```java
ContentValues contentValues = new ContentValues();
contentValues.put(Q.User.IS_REGISTERED, true);
contentValues.put(Q.User.TIMESTAMP, System.currentTimeMillis());

// UPDATE User SET isRegistered = 'true', timestamp = '123456789'
int rowsUpdated = Update.getBuilder()
        .values(contentValues)
        .execute(User.class, getSQLProvider());
```

```java
// DELETE FROM User;
int rowsDeleted = Delete.getBuilder().execute(User.class, sqlProvider);
```

```java
// SELECT Count(*) FROM User;
int count = Count.getBuilder().execute(User.class, sqlProvider);
```

####Clauses####
The `Where`, `And`, `In`, and `Or` classes are used to build up the query.
`Where` is powered by the `Exp`ression enum:

```java
public enum Exp {
	EQUAL_TO ("="),
	MORE_THAN (">"),
	MORE_THAN_OR_EQUAL_TO (">="),
	LESS_THAN ("<"),
	LESS_THAN_OR_EQUAL_TO ("<="),
	LIKE ("LIKE");
}
``` 

The following illustrate how to build more complex queries: 

```java
// SELECT * FROM User WHERE isRegistered = 'true';
User[] users = Select.getBuilder()
        .where(new Where(Q.User.IS_REGISTERED, Where.Exp.EQUAL_TO, true))
        .execute(User.class, sqlProvider);
```

```java
// SELECT * FROM User WHERE username LIKE 'jo%'
User[] users = Select.getBuilder()
        .where(new Where(Q.User.USERNAME, Where.Exp.LIKE, "jo%"))
        .execute(User.class, sqlProvider);
```

```java
// SELECT * FROM User WHERE username IN ("sam","josh");
User[] users = Select.getBuilder()
        .where(new In(Q.User.USERNAME, "sam", "josh"))
        .execute(User.class, sqlProvider);
```

```java
// SELECT * FROM User WHERE ((username = "sam" OR username = "angie") AND (timestamp >= 1234567890));
User[] users = Select.getBuilder()
		.where(new And(
                new Or(
                        new Where(Q.User.USERNAME, Where.Exp.EQUAL_TO, "sam"),
                        new Where(Q.User.USERNAME, Where.Exp.EQUAL_TO, "angie")
                ),
                new And(
                        new Where(Q.User.TIMESTAMP, Where.Exp.MORE_THAN_OR_EQUAL_TO, 1234567890)
                )))
        .execute(User.class, sqlProvider);
```

####Keywords####
The `OrderBy` and `Limit` classes are used to manipulate the results of the `Select` class

```java
// SELECT * FROM user ORDER BY username DESC
User[] users = Select.getBuilder()
        .orderBy(Q.User.USERNAME, OrderBy.Order.DESC)
        .execute(User.class, sqlProvider);
```

```java
// SELECT * FROM user ORDER BY username DESC LIMIT 2,4
User[] users = Select.getBuilder()
        .limit(2,4)
        .orderBy(Q.User.USERNAME, OrderBy.Order.DESC)
        .execute(User.class, sqlProvider);
```

####Joins####
Joins can be performed using the `InnerJoin`, `LeftOutJoin`, `CrossInnerJoin`, `NaturalInnerJoin`, `NaturalLeftOuterJoin` classes.
The target table for the join must be defined as an @Column, the object will be populated with any join results.

```java
@Table
public class Comment {
    @Column(index = true) int id;
    @Column int userId;
    @Column User user; // The target table for a potential join

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

@Table
public class User {
    @Column(index = true) int id;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

Comment[] comments = Select.getBuilder()
		.join(innerJoin(User.class, on("Comment.userId","User.id")))
        .execute(Comment.class, App.getInstance().getSQLProvider());
        
User user = comments[0].getUser(); // The nested User object is populated by the join
```
####Primary Key####
An auto incrementing primary key can be defined using:

```java
@Table
public class Data {
    @Column(primary_key = true, auto_increment = true) int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
```

####Tests####
The `tests/java/com/memtrip/AndroidQuery` package contains a full set of unit and integration tests. The
tests can be used as a good reference on how to structure queries.

####TODO####
- Validate that object relationships defined by @Column are annotated with @Table
- Validate that auto_increment columns must be int or long
- @Table annotation should support foreign_key functionality
- @NotNull annotation and handle this validation in the software layer
- Composite Foreign Key Constraints
