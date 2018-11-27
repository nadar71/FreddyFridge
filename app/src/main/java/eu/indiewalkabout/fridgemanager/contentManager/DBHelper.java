package eu.indiewalkabout.fridgemanager.contentManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by simone on 27/07/18.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static String DBNAME = "FOOD_SCHEDULE";

    public DBHelper(Context context){
        super(context,DBNAME,null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String generatingComm = "CREATE TABLE " + DBAttributes.DEADLINES_TBL +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT "+
                DBAttributes.FIELD_DESCRIPTION + " TEXT "+
                DBAttributes.FIELD_END_DATE + " TEXT "+
                DBAttributes.FIELD_STATUS + " TEXT "+
                DBAttributes.FIELD_IMG + " BLOB )";

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
