package bharath.uppalanchi.splittero.activities
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import utils.Constants


class AddBillsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddBillsBinding
    private  var splitBillBucket: SplitBillBucket? = null
    private  var participantsList = ArrayList<ParticipantDetails>()
    private var participantNames = ArrayList<String?>()
    private lateinit var db : DBHandler
    private lateinit var participantsAdapter:ArrayAdapter<String>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBillsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db = DBHandler(this@AddBillsActivity, null)

        if(intent.hasExtra(Constants.SPLIT_BILL_BUCKET_DETAILS)){
            splitBillBucket = intent.getParcelableExtra(Constants.SPLIT_BILL_BUCKET_DETAILS)
            getParticipantDetails()
            setOnClickListeners()
        }



    }

    private fun setOnClickListeners(){
        binding.addParticipant.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.add_participant -> {
                val addParticipantActivity = Intent(this@AddBillsActivity, AddParticipantActivity::class.java )
                addParticipantActivity.putExtra(Constants.SPLIT_BILL_BUCKET_DETAILS, splitBillBucket)
                startActivity(addParticipantActivity)
            }
        }
    }



    @SuppressLint("Range", "NotifyDataSetChanged")
    private fun getParticipantDetails(){
        participantsList.clear()
        val cursor = db.getParticipantsDetails(splitBillBucket?.splitBillId)
        while (cursor!!.moveToNext()){
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
                val participantName = parent.getItemAtPosition(position).toString()

                Toast.makeText(applicationContext,"$participantName", Toast.LENGTH_SHORT).show()


            }
        }

    }



}