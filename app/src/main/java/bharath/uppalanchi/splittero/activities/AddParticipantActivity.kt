package bharath.uppalanchi.splittero.activities

import adapters.ParticipantsListAdapter
import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import bharath.uppalanchi.splittero.R
import bharath.uppalanchi.splittero.databinding.ActivityAddParticipantBinding
import com.google.android.material.textfield.TextInputLayout
import database.DBHandler
import modals.ParticipantDetails
import modals.SplitBillBucket
import utils.Constants
import utils.GlobalInterface
import kotlin.collections.ArrayList

class AddParticipantActivity : AppCompatActivity(), View.OnClickListener,GlobalInterface {

    private lateinit var binding: ActivityAddParticipantBinding
    private lateinit var db : DBHandler
    private  var splitBillBucket: SplitBillBucket? = null
    private  var participantsList = ArrayList<ParticipantDetails>()
    private lateinit var adapter  : ParticipantsListAdapter
    private var participantNames = ArrayList<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                binding = ActivityAddParticipantBinding.inflate(layoutInflater)
                val view = binding.root
                setContentView(view)
                db = DBHandler(this@AddParticipantActivity, null)
                setupRecyclerViewForAddParticipant(participantsList)

                if(intent.hasExtra(Constants.SPLIT_BILL_BUCKET_DETAILS)){
                    splitBillBucket = intent.getParcelableExtra(Constants.SPLIT_BILL_BUCKET_DETAILS)
                    getParticipantDetails()
                    setOnClickListeners()
                }
    }

    private fun setOnClickListeners(){
        binding.addParticipant.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    private fun addParticipant() {
        binding.participantName.addTextChangedListener(textChangeListener(binding.participantInputLayout))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (addParticipantValidate()) {
                val participantName : String =  binding.participantName.text.toString().trim().uppercase()
                val participantDetails  = ParticipantDetails(null,participantName,splitBillBucket?.splitBillId,null)
                val success: Long = db.addParticipant(participantDetails)

                if(success > 0){
                    binding.participantName.setText("")
                    Toast.makeText(applicationContext,"$participantName added successfully !!", Toast.LENGTH_SHORT).show()
                    binding.participantInputLayout.error = null
                    getParticipantDetails()
                }
                else{
                    Toast.makeText(applicationContext,"Something went wrong !!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun addParticipantValidate() : Boolean {
        val participantName : String = binding.participantName.text.toString().trim().uppercase()
        return when {
            binding.participantName.text.isNullOrEmpty() -> {
                binding.participantInputLayout.error = "Participant Name should not be empty"
                false
            }
            participantName.length <3 -> {
                binding.participantInputLayout.error = "Participant Name should be at least 3 letters"
                false
            }
            participantNames.contains(participantName) -> {
                binding.participantInputLayout.error = "Participant Name already exists !!"
                false
            }

            else -> true
        }

    }

    private fun textChangeListener(participantInput : TextInputLayout) : TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(participantInput.error !=null){
                   binding.participantInputLayout.error = "Participant Name should be at least 3 letters"
                }
                if(s?.length!! > 2){
                    participantInput.error = null
                }
            }
        }

        return  textWatcher
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(view: View?) {
       when(view?.id){
         R.id.add_participant -> {
             addParticipant()
         }
       }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getParticipantDetails(){
        participantsList = db.getParticipantsDetails(splitBillBucket?.splitBillId)

        if(participantsList.size > 0){
            binding.participantsRecyclerView.visibility = View.VISIBLE
            binding.emptyLayout.visibility = View.GONE
            adapter.updateList(participantsList)

            participantNames.clear()
            for( participant in participantsList){
                participantNames.add(participant.participantName)
            }
        }
        else{
            binding.participantsRecyclerView.visibility = View.GONE
            binding.emptyLayout.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerViewForAddParticipant(participantsList : ArrayList<ParticipantDetails>){
        binding.participantsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ParticipantsListAdapter(this@AddParticipantActivity,participantsList,this@AddParticipantActivity)
        binding.participantsRecyclerView.adapter = adapter
    }

    override fun displayEmptyLayout() {
        binding.participantsRecyclerView.visibility = View.GONE
        binding.emptyLayout.visibility = View.VISIBLE
    }

    override fun updateListForView(index: Int) {
        if(binding.participantName.text.toString().trim().uppercase() == participantNames[index]) binding.participantInputLayout.error = null
        participantNames.removeAt(index)
    }


}