package com.ksy.mpl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "photoTag.db";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE Clothes(name TEXT, up TEXT, down TEXT, out TEXT, acc TEXT, rate INT, photo BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Clothes");
        onCreate(sqLiteDatabase);
    }

    public void insert(String name, String up, String down, String out, String acc, int rate, byte[] photo) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO Clothes VALUES('" + name + "', " + up + "', " + down + ", '" +
                out + "', " + acc + "', " + rate + "', " + photo + "')");
        db.close();
    }

    public void Update(String name, String up, String down, String out, String acc, int rate, byte[] photo) {
        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("UPDATE Clothes SET up = " + up + ", down = '" + down, ", out = '" + out, ", acc = " + acc, ", rate = " + rate, ", photo = " + photo + "'" + " WHERE NAME = '" + name + "'");
        db.close();
    }

    public void Delete(String name) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE Person WHERE NAME = '" + name + "'");
        db.close();
    }

    // Person Table 조회
    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Person", null);
        while (cursor.moveToNext()) {
            result += " 이름 : " + cursor.getString(0)
                    + ", 나이 : "
                    + cursor.getInt(1)
                    + ", 주소 : "
                    + cursor.getString(2)
                    + "\n";
        }

        return result;
}
}
