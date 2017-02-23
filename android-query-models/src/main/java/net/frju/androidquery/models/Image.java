package net.frju.androidquery.models;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;

@DbModel(dbName = "external/images/media", databaseProvider = MediaStoreContentDatabaseProvider.class)
public class Image {

    /**
     * The unique ID for a row.
     */
    @DbField(dbName = MediaStore.Images.Media._ID)
    public long id;

    /**
     * The title of the content
     */
    @DbField(dbName = MediaStore.Images.Media.TITLE)
    public String title;


    /**
     * Path to the file on disk.
     * <p>
     * Note that apps may not have filesystem permissions to directly access
     * this path. Instead of trying to open this path directly, apps should
     * use {@link ContentResolver#openFileDescriptor(Uri, String)} to gain
     * access.
     */
    @DbField(dbName = MediaStore.Images.Media.DATA)
    public String path;

    /**
     * The size of the file in bytes
     */
    @DbField(dbName = MediaStore.Images.Media.SIZE)
    public long size;

    /**
     * The display name of the file
     */
    @DbField(dbName = MediaStore.Images.Media.DISPLAY_NAME)
    public String displayName;

    /**
     * The MIME type of the file
     */
    @DbField(dbName = MediaStore.Images.Media.MIME_TYPE)
    public String mimeType;

    /**
     * The width of the image/video in pixels.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @DbField(dbName = MediaStore.Images.Media.WIDTH)
    public int width;

    /**
     * The height of the image/video in pixels.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @DbField(dbName = MediaStore.Images.Media.HEIGHT)
    public int height;

    /**
     * The description of the image
     */
    @DbField(dbName = MediaStore.Images.Media.DESCRIPTION)
    public String description;

    /**
     * The latitude where the image was captured.
     */
    @DbField(dbName = MediaStore.Images.Media.LATITUDE)
    public double latitude;

    /**
     * The longitude where the image was captured.
     */
    @DbField(dbName = MediaStore.Images.Media.LONGITUDE)
    public double longitude;

    /**
     * The date and time that the image was taken in units
     * of milliseconds since jan 1, 1970.
     */
    @DbField(dbName = MediaStore.Images.Media.DATE_TAKEN)
    public int dateTaken;

    /**
     * The orientation for the image expressed as degrees.
     * Only degrees 0, 90, 180, 270 will work.
     */
    @DbField(dbName = MediaStore.Images.Media.ORIENTATION)
    public int orientation;
}