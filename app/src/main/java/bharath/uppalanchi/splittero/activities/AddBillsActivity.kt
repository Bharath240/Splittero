package bharath.uppalanchi.splittero.activities
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bharath.uppalanchi.splittero.R
import bharath.uppalanchi.splittero.databinding.ActivityAddBillsBinding
import database.DBHandler
import modals.ParticipantDetails
import modals.SplitBillBucket
import modals.TripBillsDetails
import utils.Constants


class AddBillsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddBillsBinding
    private  var splitBillBucket: SplitBillBucket? = null
    private  var participantsList = ArrayList<ParticipantDetails>()
    private  var tripBillDetails = ArrayList<TripBillsDetails>()
    private var participantNames = ArrayList<String?>()
    private lateinit var db : DBHandler
    private lateinit var participantsAdapter:ArrayAdapter<String>;
    private var participantName : String? = null
    private var participantId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBillsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db = DBHandler(this@AddBillsActivity, null)

        if(intent.hasExtra(Constants.SPLIT_BILL_BUCKET_DETAILS)){
            splitBillBucket = intent.getParcelableExtra(Constants.SPLIT_BILL_BUCKET_DETAILS)
            getTripBillDetails()
            getParticipantDetails()
            setOnClickListeners()
        }
    }

    private fun setOnClickListeners(){
        binding.addParticipant.setOnClickListener(this)
        binding.add.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.add_participant -> {
                val addParticipantActivity = Intent(this@AddBillsActivity, AddParticipantActivity::class.java )
                addParticipantActivity.putExtra(Constants.SPLIT_BILL_BUCKET_DETAILS, splitBillBucket)
                startActivity(addParticipantActivity)
            }
            R.id.add -> {
                val billDescription = binding.billDescription.text.toString().trim()
                val billAmount = binding.billAmount.text.toString().trim()
                val tripBillsDetails = TripBillsDetails(null, billDescription,billAmount.toInt(),participantId,splitBillBucket?.splitBillId,null)
                addBillAmount(tripBillsDetails)
            }
        }
    }



    @SuppressLint("Range", "NotifyDataSetChanged")
    private fun getParticipantDetails(){
        participantsList.clear()
        val cursor = db.getParticipantsDetails(splitBillBucket?.splitBillId)
        while (cursor!!.moveToNext()) {
            participantsList.add(
                ParticipantDetails(cursor.getInt(cursor.getColumnIndex(DBHandler.PARTICIPANT_ID)),cursor.getString(cursor.getColumnIndex(
                    DBHandler.PARTICIPANT_NAME)),cursor.getInt(cursor.getColumnIndex(DBHandler.ID_COL)),cursor.getInt(cursor.getColumnIndex(
                    DBHandler.PARTICIPANT_DELETE)))
            )
        }

        if(participantsList.size > 0){
            participantNames.clear()
            for( participant in participantsList){
                participantNames.add(participant.participantName)
            }


            participantsAdapter = ArrayAdapter(this,R.layout.participant_names, participantNames)
            binding.participants.setAdapter(participantsAdapter)

            binding.participants.setOnItemClickListener{parent, view, position, id ->
                 participantName = parent.getItemAtPosition(position).toString()

                for (participant in participantsList){
                    if (participant.participantName == participantName){
                        participantId = participant.participantID
                        break;
                    }
                }
            }
        }

    }


    private fun addBillAmount(tripBillsDetails : TripBillsDetails){
        val success = db.addTripsBills(tripBillsDetails);

        if (success > 0){
            Toast.makeText(applicationContext,"Bill Added Successfully",Toast.LENGTH_SHORT).show()
            binding.participants.text = null
            binding.billAmount.text = null
            binding.billDescription.text = null
        }
        else {
            Toast.makeText(applicationContext,"Something went wrong !!",Toast.LENGTH_SHORT).show()
        }


    }

    @SuppressLint("Range", "NotifyDataSetChanged")
    private fun getTripBillDetails(){
        tripBillDetails.clear()
        val cursor = db.getTripBillDetails(splitBillBucket?.splitBillId)
        while (cursor!!.moveToNext()){
            tripBillDetails.add(TripBillsDetails(cursor.getInt(cursor.getColumnIndex(DBHandler.TRIP_BILL_ID)),cursor.getString(cursor.getColumnIndex(DBHandler.TRIP_BILL_DESCRIPTION)),cursor.getInt(cursor.getColumnIndex(DBHandler.TRIP_BILL_AMOUNT)),cursor.getInt(cursor.getColumnIndex(DBHandler.PARTICIPANT_ID)),cursor.getInt(cursor.getColumnIndex(DBHandler.ID_COL)),cursor.getInt(cursor.getColumnIndex(DBHandler.TRIP_BILL_DELETE))))
        }


    }



}