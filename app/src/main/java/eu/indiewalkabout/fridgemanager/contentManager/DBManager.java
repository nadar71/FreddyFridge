package eu.indiewalkabout.fridgemanager.contentManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;

/**
 * Created by simone on 27/07/18.
 */

public class DBManager {

    private DBHelper dbhelper;
    SQLiteDatabase db;


    /**
     * standard constructor
     * @param context
     */
    public DBManager(Context context){
        dbhelper = new DBHelper(context);
    }

    /**
     * Open db for writing
     * @return
     * @throws SQLException
     */
    public SQLiteDatabase openDBWrite() throws SQLException{
        db = dbhelper.getWritableDatabase();
        return db;
    }

    /**
     * open db for reading
     * @return
     * @throws SQLException
     */
    public SQLiteDatabase openDBRead() throws SQLException{
        db = dbhelper.getReadableDatabase();
        return db;
    }

    /**
     * close db wrapper
     */
    public void closeDB(){
        dbhelper.close();

    }


    /**
     * Insert new rec
     * @param newDesc
     * @param endDate
     * @param status
     */
    public void insertNew(String newDesc,String endDate, String status ){
        openDBWrite();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBAttributes.FIELD_DESCRIPTION,newDesc);
        contentValues.put(DBAttributes.FIELD_END_DATE,endDate);
        contentValues.put(DBAttributes.FIELD_STATUS,status);
        try{
            db.insert(DBAttributes.DEADLINES_TBL,null,contentValues);
        } catch(SQLiteException s){
            // TODO

        }
    }

    /**
     * Delete 1 rec based on id
     * @param id
     * @return
     */
    public boolean deleteRec(long id){
        openDBWrite();
        try{
            if (db.delete(DBAttributes.DEADLINES_TBL,DBAttributes.FIELD_ID+"=?",new String[]{Long.toString(id)})>0){
                return true;
            } else {
                return false;
            }

        } catch(SQLiteException s){
            return false;

        }
    }


    /**
     * Return all the field in the db
     * @return
     */
    public Cursor showAll(){
        Cursor cs=null ;
        try{
            openDBRead();
            cs = db.query(DBAttributes.DEADLINES_TBL,null, null, null, null, null, null, null);
        }
        catch(SQLiteException sqle)
        {
            return null;
        }
        return cs;
    }

    

}



