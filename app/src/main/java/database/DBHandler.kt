package database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import modals.ParticipantDetails
import modals.PayoutDetails
import modals.SplitBillBucket
import modals.TripBillsDetails

open class DBHandler(context: Context, factory: SQLiteDatabase.CursorFactory?) :
SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SPLITTERO"
        private const val DATABASE_VERSION = 10

        //--------------- HELPER VARIABLES ---------------------------
        private const val TOTAL_PAID = "total_paid"

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


        //--------------- TRIP BILLS TABLE --------------------------
        const val TRIPS_BILL_TABLE = "tbl_trips_bills"
        const val TRIP_BILL_ID = "trip_bill_id"
        const val TRIP_BILL_DESCRIPTION = "trip_bill_description"
        const val TRIP_BILL_AMOUNT = "bill_amount"
        const val TRIP_BILL_DELETE = "trip_bill_delete"


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
            if(checkIfTheTableExistsInDatabaseOrNot(db, PARTICIPANTS_TABLE) == 0){
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


        if(checkIfTheTableExistsInDatabaseOrNot(db, TRIPS_BILL_TABLE) == 0){
            createTripBillsTable(db)
        }

    }

    private fun createTripBillsTable(db : SQLiteDatabase?){
        val createTripBillsTable = ("CREATE TABLE " + TRIPS_BILL_TABLE + " ("
                + TRIP_BILL_ID + " INTEGER PRIMARY KEY, "
                + TRIP_BILL_DESCRIPTION + " TEXT , "
                + TRIP_BILL_AMOUNT + " INTEGER , " +
                TRIP_BILL_DELETE + " INTEGER DEFAULT 0,"+
                ID_COL+ " INTEGER,"+
                PARTICIPANT_ID+ " INTEGER,"+
                " FOREIGN KEY ("+ ID_COL+") REFERENCES "+ TABLE_NAME+"("+ID_COL+"), "+
                " FOREIGN KEY ("+ PARTICIPANT_ID+") REFERENCES "+ PARTICIPANTS_TABLE+"("+PARTICIPANT_ID+")"+

                ")")
        db?.execSQL(createTripBillsTable)

    }

    @SuppressLint("Range")
    fun getParticipantsDetails(splitBucketId : Int?): ArrayList<ParticipantDetails>{
        val participantsList = ArrayList<ParticipantDetails>()
        val db = this.readableDatabase
        val cursor =  db.rawQuery("SELECT * FROM " + PARTICIPANTS_TABLE + " WHERE "+ ID_COL+" = "+splitBucketId+" AND " +PARTICIPANT_DELETE + "=0" , null)

        while (cursor!!.moveToNext()) {
            participantsList.add(
                ParticipantDetails(cursor.getInt(cursor.getColumnIndex(PARTICIPANT_ID)),cursor.getString(cursor.getColumnIndex(
                    PARTICIPANT_NAME)),cursor.getInt(cursor.getColumnIndex(ID_COL)),cursor.getInt(cursor.getColumnIndex(
                    PARTICIPANT_DELETE)))
            )
        }
        return  participantsList
    }

    fun deleteParticipant(participantDetails : ParticipantDetails) : Int{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(PARTICIPANT_DELETE,1)

        val success = db.update(PARTICIPANTS_TABLE,cv, PARTICIPANT_ID +" ="+participantDetails.participantID,null)
        db.close()
        return success
    }

    private fun checkIfTheTableExistsInDatabaseOrNot(db: SQLiteDatabase?, nameOfTheTable : String): Int?{
      val cursor =  db?.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + nameOfTheTable + "'", null)

        return cursor?.count
    }

     fun addTripsBills(tripBillsDetails : TripBillsDetails) : Long{
        val values = ContentValues()

        values.put(TRIP_BILL_DESCRIPTION, tripBillsDetails.billDescription)
        values.put(ID_COL,tripBillsDetails.splitBucketId)
        values.put(PARTICIPANT_ID,tripBillsDetails.participantId)
        values.put(TRIP_BILL_AMOUNT, tripBillsDetails.billAmount)

        val db = this.writableDatabase
        val success : Long = db.insert(TRIPS_BILL_TABLE, null, values)
        db.close()
        return  success
    }

    @SuppressLint("Range")
    fun getTripBillDetails(splitBucketId : Int?): ArrayList<TripBillsDetails>{
        val tripBillsDetails = ArrayList<TripBillsDetails>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT tbl.*, tbl_pt."+ PARTICIPANT_NAME+" FROM " + TRIPS_BILL_TABLE + " tbl JOIN "+ PARTICIPANTS_TABLE+ " tbl_pt ON tbl_pt."+PARTICIPANT_ID+"="+"tbl."+ PARTICIPANT_ID+" WHERE tbl."+ ID_COL+" = "+splitBucketId+" AND " + TRIP_BILL_DELETE + "=0" , null)
        while (cursor!!.moveToNext()){
            tripBillsDetails.add(TripBillsDetails(cursor.getInt(cursor.getColumnIndex(TRIP_BILL_ID)),cursor.getString(cursor.getColumnIndex(TRIP_BILL_DESCRIPTION)),cursor.getInt(cursor.getColumnIndex(TRIP_BILL_AMOUNT)),cursor.getInt(cursor.getColumnIndex(PARTICIPANT_ID)),cursor.getString(cursor.getColumnIndex(
                PARTICIPANT_NAME)),cursor.getInt(cursor.getColumnIndex(ID_COL)),cursor.getInt(cursor.getColumnIndex(TRIP_BILL_DELETE))))
        }
        return  tripBillsDetails
    }

    fun deleteTripBill(tripBillsDetails : TripBillsDetails) : Int{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(TRIP_BILL_DELETE,1)

        val success = db.update(TRIPS_BILL_TABLE,cv, TRIP_BILL_ID +" ="+tripBillsDetails.tripBillId,null)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getPayoutDetails(splitBucketId : Int?): ArrayList<PayoutDetails>{
        val payoutDetails = ArrayList<PayoutDetails>()
        val db = this.readableDatabase
        val query =
            "SELECT tbt.$PARTICIPANT_ID,pt.$PARTICIPANT_NAME, SUM(tbt.$TRIP_BILL_AMOUNT) as $TOTAL_PAID " +
                    "FROM $TRIPS_BILL_TABLE tbt " +
                    "JOIN $PARTICIPANTS_TABLE pt ON pt.$PARTICIPANT_ID = tbt.$PARTICIPANT_ID" +
                    " WHERE tbt.$ID_COL = $splitBucketId AND tbt.$TRIP_BILL_DELETE=0 " +
                    "GROUP BY tbt.$PARTICIPANT_ID"
        Log.d("getPayoutDetails", query)
        val cursor =  db.rawQuery(query  , null)

        while (cursor.moveToNext()){
            payoutDetails.add(PayoutDetails(cursor.getInt(cursor.getColumnIndex(PARTICIPANT_ID)), cursor.getString(cursor.getColumnIndex(
                PARTICIPANT_NAME)), cursor.getInt(cursor.getColumnIndex(TOTAL_PAID)),null))
        }

        return payoutDetails

    }



}