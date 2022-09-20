package database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import modals.ParticipantDetails
import modals.SplitBillBucket

class DBHandler(context: Context, factory: SQLiteDatabase.CursorFactory?) :
SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SPLITTERO"
        private const val DATABASE_VERSION = 10

        //--------------- SPLIT BILLS BUCKET TABLE -------------------
        const val TABLE_NAME = "tbl_bills_bucket"
        const val ID_COL = "split_bill_id"
        const val SPLIT_BILL_NAME_COL = "split_bill_name"
        const val IMAGE_INDEX = "img_index"
        const val TEMPORARY_DELETE = "temp_del"
        const val PERMANENT_DELETE = "permanent_del"
        const val CREATED_DATE = "dt_created_date"

        //--------------- PARTICIPANTS TABLE -------------------
        const val PARTICIPANTS_TABLE = "tbl_split_bucket_participants"
        const val PARTICIPANT_ID = "participant_id"
        const val PARTICIPANT_NAME = "participant_name"
        const val PARTICIPANT_DELETE = "bt_delete"


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
        onUpgrade(db, DATABASE_VERSION-1, DATABASE_VERSION)


    }

    override fun onUpgrade(db: SQLiteDatabase?, olderVersion: Int, newerVersion: Int) {

        if(olderVersion < newerVersion) {
            val checkForParticipantsTable : Cursor? = db?.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                    + PARTICIPANTS_TABLE + "'", null)
            if(checkForParticipantsTable?.count == 0){
                createParticipantsTable(db)
            }

        }

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

    fun addParticipant(participantDetails: ParticipantDetails) : Long{

        val values = ContentValues()

        values.put(PARTICIPANT_NAME, participantDetails.participantName)
        values.put(ID_COL, participantDetails.participantSplitBucketID)
        val db = this.writableDatabase
        val success : Long = db.insert(PARTICIPANTS_TABLE, null, values)
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

        val success = db.update(TABLE_NAME,cv, ID_COL +" ="+splitBill.splitBillId,null)
        db.close()
        return success
    }

    fun restoreSplitBill(splitBill : SplitBillBucket) : Int{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(TEMPORARY_DELETE,0)


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

    private fun createParticipantsTable(db : SQLiteDatabase?){
            val createParticipantsTableQuery = ("CREATE TABLE " + PARTICIPANTS_TABLE + " ("
                    + PARTICIPANT_ID + " INTEGER PRIMARY KEY, " +
                    PARTICIPANT_NAME + " TEXT," +
                    PARTICIPANT_DELETE + " INTEGER DEFAULT 0,"+
                    ID_COL+ " INTEGER,"+
                    " FOREIGN KEY ("+ ID_COL+") REFERENCES "+ TABLE_NAME+"("+ID_COL+")"+

                    ")")
            db?.execSQL(createParticipantsTableQuery)

    }

    fun getParticipantsDetails(splitBucketId : Int?): Cursor?{
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + PARTICIPANTS_TABLE + " WHERE "+ ID_COL+" = "+splitBucketId+" AND " +PARTICIPANT_DELETE + "=0" , null)
    }

    fun deleteParticipant(participantDetails : ParticipantDetails) : Int{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(PARTICIPANT_DELETE,1)

        val success = db.update(PARTICIPANTS_TABLE,cv, PARTICIPANT_ID +" ="+participantDetails.participantID,null)
        db.close()
        return success
    }
}