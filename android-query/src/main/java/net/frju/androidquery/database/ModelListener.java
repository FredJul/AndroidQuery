package net.frju.androidquery.database;

public interface ModelListener {
    void onPreInsert();

    void onPreUpdate();

    void onPreDelete();
}