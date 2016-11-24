package net.frju.androidquery.integration;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RxTest extends IntegrationTest {

    @Before
    public void setUp() {
        super.setUp();
        getSetupUser().tearDownFourTestUsers();
        getSetupUser().setupFourTestUsers();
    }

    @Test
    public void testDummy() {
        assertTrue(true);
    }

//    @Test
//    public void testRxCreate() {
//        // setup
//        String USER_ID = "rx123456789";
//        long USER_TIMESTAMP = System.currentTimeMillis();
//        boolean USER_IS_REGISTERED = true;
//
//        User user = new User();
//        user.setUsername(USER_ID);
//        user.setIsRegistered(USER_IS_REGISTERED);
//        user.setTimestamp(USER_TIMESTAMP);
//
//        // exercise
//        Observable<Integer> create = Q.User.insert(user).rx();
//        TestSubscriber<Integer> sub = new TestSubscriber<>();
//        create.subscribe(sub);
//
//        // verify
//        sub.assertNoErrors();
//
//        Observable<User> verify = Q.User.select()
//                .where(where(Q.User.USERNAME, Where.Op.IS, USER_ID))
//                .rxSingle();
//
//        TestSubscriber<User> subVerify = new TestSubscriber<>();
//        verify.subscribe(subVerify);
//        List<User> verifiedUsers = subVerify.getOnNextEvents();
//
//        assertTrue(user.getUsername().equals(verifiedUsers.get(0).getUsername()));
//        assertTrue(user.getTimestamp() == verifiedUsers.get(0).getTimestamp());
//        assertTrue(user.getIsRegistered() == verifiedUsers.get(0).getIsRegistered());
//    }
//
//    @Test
//    public void testRxRead() {
//        // exercise
//        Observable<Result<User>> read = Q.User.select()
//                .rx();
//
//        TestSubscriber<Result<User>> sub = new TestSubscriber<>();
//        read.subscribe(sub);
//        List<Result<User>> users = sub.getOnNextEvents();
//
//        // verify
//        assertEquals(4, users.get(0).length);
//    }
//
//    @Test
//    public void testRxReadOne() {
//        // exercise
//        Observable<User> read = Q.User.select()
//                .where(where(Q.User.USERNAME, Where.Op.IS, SetupUser.CLYDE_USER_NAME))
//                .rxSingle();
//
//        TestSubscriber<User> sub = new TestSubscriber<>();
//        read.subscribe(sub);
//        List<User> user = sub.getOnNextEvents();
//
//        // verify
//        assertEquals(SetupUser.CLYDE_USER_NAME, user.get(0).getUsername());
//    }
//
//    @Test
//    public void testRxUpdate() {
//        // setup
//        long timestamp = System.currentTimeMillis();
//        String newUsername = "CHANGED";
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(Q.User.IS_REGISTERED, true);
//        contentValues.put(Q.User.TIMESTAMP, timestamp);
//        contentValues.put(Q.User.USERNAME, newUsername);
//
//        // exercise
//        Observable<Integer> update = Q.User.update()
//                .values(contentValues)
//                .rx();
//
//        TestSubscriber<Integer> sub = new TestSubscriber<>();
//        update.subscribe(sub);
//        List<Integer> updateCount = sub.getOnNextEvents();
//
//        // verify
//        Observable<Result<User>> verify = Q.User.select().rx();
//        TestSubscriber<Result<User>> subVerify = new TestSubscriber<>();
//        verify.subscribe(subVerify);
//        List<Result<User>> verifiedUsers = subVerify.getOnNextEvents();
//
//        for (User user : verifiedUsers.get(0)) {
//            assertEquals(timestamp, user.getTimestamp());
//            assertEquals(true, user.getIsRegistered() );
//            assertEquals(newUsername, user.getUsername());
//        }
//
//        assertEquals(updateCount.get(0).intValue(), verifiedUsers.get(0).length);
//    }
//
//    @Test
//    public void testRxDelete() {
//        // exercise
//        Observable<Integer> delete = Q.User.delete().rx();
//        TestSubscriber<Integer> sub = new TestSubscriber<>();
//        delete.subscribe(sub);
//        List<Integer> deletedRows = sub.getOnNextEvents();
//
//        // verify
//        Observable<Result<User>> verify = Q.User.select().rx();
//        TestSubscriber<Result<User>> subVerify = new TestSubscriber<>();
//        verify.subscribe(subVerify);
//        List<Result<User>> verifiedUsers = subVerify.getOnNextEvents();
//
//        assertEquals(0, verifiedUsers.get(0).length);
//        assertEquals(4, deletedRows.get(0).intValue());
//    }
//
//    @Test
//    public void testRxCount() {
//        // exercise
//        Observable<Long> count = Q.User.count().rx();
//        TestSubscriber<Long> sub = new TestSubscriber<>();
//        count.subscribe(sub);
//        List<Long> countedRows = sub.getOnNextEvents();
//
//        // verify
//        assertEquals(4, countedRows.get(0).intValue());
//    }
}
