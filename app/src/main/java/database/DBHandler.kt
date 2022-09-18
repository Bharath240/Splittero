package database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import modals.SplitBillBucket

class DBHandler(context: Context, factory: SQLiteDatabase.CursorFactory?) :
SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SPLITTERO"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "tbl_bills_bucket"
        const val ID_COL = "split_bill_id"
        const val SPLIT_BILL_NAME_COL = "split_bill_name"
        const val IMAGE_INDEX = "img_index"
        const val TEMPORARY_DELETE = "temp_del"
        const val PERMANENT_DELETE = "permanent_del"
        const val CREATED_DATE = "dt_created_date"


    }
    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                SPLIT_BILL_NAME_COL + " TEXT," +
                IMAGE_INDEX + " INTEGER," +
                TEMPORARY_DELETE + " INTEGER DEFAULT 0,"+
                PERMANENT_DELETE + " INTEGER DEFAULT 0,"+
                CREATED_DATE + " TEXT "+
                ")")
        db?.execSQL(query)

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertSplitBill(splitBillName : String, imageIndex : Int,splitBillCreatedDate : String ) : Long{

        val values = ContentValues()

        values.put(SPLIT_BILL_NAME_COL, splitBillName)
        values.put(IMAGE_INDEX, imageIndex)
        values.put(CREATED_DATE, splitBillCreatedDate)
        val db = this.writableDatabase

        // all values are inserted into database
       val success : Long = db.insert(TABLE_NAME, null, values)
        db.close()
        return  success
    }

    fun getSplitBucketDetails(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_NAME+" WHERE "+ TEMPORARY_DELETE + " = 0" +" AND "+ PERMANENT_DELETE+ " =0", null)

    }

    fun getTrashSplitBucketDetails(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_NAME+" WHERE "+ TEMPORARY_DELETE + " = 1" +" AND "+ PERMANENT_DELETE+ " =0", null)

    }

    fun getPreviousSplitBillBucketIndex(): Cursor?{
        val db = this.readableDatabase
        return db.rawQuery("SELECT " + IMAGE_INDEX+ " FROM " + TABLE_NAME + " WHERE "+ PERMANENT_DELETE + "=0"+ " ORDER BY "+ ID_COL +" DESC", null)
    }

    fun deleteAllSplitBillBuckets(){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM "+ TABLE_NAME)

    }

    fun deleteSplitBill(splitBill : SplitBillBucket) : Int{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(TEMPORARY_DELETE,1)

        //Inserting data into table
        val success = db.update(TABLE_NAME,cv, ID_COL +" ="+splitBill.splitBillId,null)
        db.close()
        return success
    }

    fun restoreSplitBill(splitBill : SplitBillBucket) : Int{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(TEMPORARY_DELETE,0)

        //Inserting data into table
        val success = db.update(TABLE_NAME,cv, ID_COL +" ="+splitBill.splitBillId,null)
        db.close()
        return success
    }


    fun permanentlyDeleteSplitBill(splitBill : SplitBillBucket) : Int{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(PERMANENT_DELETE,1)

        val success = db.update(TABLE_NAME,cv, ID_COL +" ="+splitBill.splitBillId,null)
        db.close()
        return success
    }

    fun getSplitBillBucketNames(): Cursor?{
        val db = this.readableDatabase
        return db.rawQuery("SELECT *" + " FROM " + TABLE_NAME + " WHERE "+ PERMANENT_DELETE + "=0" , null)
    }


}