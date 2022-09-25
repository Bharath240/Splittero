package bharath.uppalanchi.splittero.activities
import adapters.AddBillsAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import bharath.uppalanchi.splittero.R
import bharath.uppalanchi.splittero.databinding.ActivityAddBillsBinding
import com.google.android.material.textfield.TextInputEditText
import database.DBHandler
import modals.ParticipantDetails
import modals.SplitBillBucket
import modals.TripBillsDetails
import utils.Constants
import utils.GlobalInterface


class AddBillsActivity : AppCompatActivity(), View.OnClickListener, GlobalInterface {
    private lateinit var binding: ActivityAddBillsBinding
    private  var splitBillBucket: SplitBillBucket? = null
    private  var participantsList = ArrayList<ParticipantDetails>()
    private  var tripBillDetailsList = ArrayList<TripBillsDetails>()
    private lateinit var db : DBHandler
    private lateinit var participantsAdapter:ArrayAdapter<String>
    private var participantName : String? = null
    private var participantId : Int? = null
    private lateinit var adapter  : AddBillsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBillsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db = DBHandler(this@AddBillsActivity, null)
        setupRecyclerViewForAddParticipant(tripBillDetailsList)
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
        binding.billPayout.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.add_participant -> {
                navigateToAddParticipantActivity()
            }
            R.id.add -> {
                if(validateBill()){
                    addParticipantBill()
                }
            }
            R.id.bill_payout -> {
                openBillPayoutActivity()
            }
        }
    }



    @SuppressLint("Range", "NotifyDataSetChanged")
    private fun getParticipantDetails(){
        participantsList = db.getParticipantsDetails(splitBillBucket?.splitBillId)
        if(participantsList.size > 0){
             val participantNames = ArrayList<String?>()
            for( participant in participantsList){
                participantNames.add(participant.participantName)
            }

            setListToParticipantDropDown(participantNames)

        }

    }

    private fun setListToParticipantDropDown(participantNames : ArrayList<String?>){
        participantsAdapter = ArrayAdapter(this,R.layout.participant_names, participantNames)
        binding.participants.setAdapter(participantsAdapter)

        binding.participants.setOnItemClickListener{parent, view, position, id ->
            participantName = parent.getItemAtPosition(position).toString()
            val participantDetails = participantsList.get(position)
            participantId = participantDetails.participantID

        }
    }

    private fun addBillAmount(tripBillsDetails : TripBillsDetails){
        val success = db.addTripsBills(tripBillsDetails)

        if (success > 0){
            Toast.makeText(applicationContext,"Bill Added Successfully",Toast.LENGTH_SHORT).show()
            hideKeyboard()
            resetTheUserInputs()
            tripBillsDetails.participantName = participantName
            tripBillDetailsList.add(tripBillsDetails)
            adapter.updateList(tripBillDetailsList)
            showViewWhenDataAvailable()

        }
        else {
            Toast.makeText(applicationContext,"Something went wrong !!",Toast.LENGTH_SHORT).show()
        }


    }

    private fun resetTheUserInputs(){
        binding.participants.text = null
        binding.participants.error = null
        binding.billAmount.text = null
        binding.billAmount.error = null
        binding.billDescription.text = null
        binding.billDescription.error = null

    }

    @SuppressLint("Range", "NotifyDataSetChanged")
    private fun getTripBillDetails() {
        tripBillDetailsList = db.getTripBillDetails(splitBillBucket?.splitBillId)

        if(tripBillDetailsList.size > 0){
            adapter.updateList(tripBillDetailsList)
            showViewWhenDataAvailable()
        }
        else {
            showViewWhenDataIsNotAvailable()
        }

    }

    private fun showViewWhenDataAvailable(){
        binding.emptyLayout.visibility = View.GONE
        binding.recyclerviewScrollView.visibility = View.VISIBLE
    }

    private fun showViewWhenDataIsNotAvailable(){
        binding.emptyLayout.visibility = View.VISIBLE
        binding.recyclerviewScrollView.visibility = View.GONE
    }

    private fun setupRecyclerViewForAddParticipant(tripBillsDetails : ArrayList<TripBillsDetails>){
        binding.tripsBillRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AddBillsAdapter(this@AddBillsActivity,tripBillsDetails,this@AddBillsActivity)
        binding.tripsBillRecyclerView.adapter = adapter
    }

    private fun validateBill() : Boolean {
        val participantName = binding.participants.text.toString().trim()
        val billAmount = binding.billAmount.text.toString().trim()
        val billDescription = binding.billDescription.text.toString().trim()
        setupTextChangeListeners()

            return if(participantName.isEmpty()){
                binding.participants.error = "Participant Name cannot be empty"
                false
            }
            else if(billAmount.isEmpty()){
                binding.billAmount.error = "Amount cannot be empty"
                false
            }
            else if(billDescription.isEmpty() || billDescription.split(" ").size < 3){
               if(billDescription.isEmpty() ) binding.billDescription.error = "Description cannot be empty"
               else    binding.billDescription.error = "Bill Description should be at least 3 words"
                false
            }
            else{
                true
            }
        }

    private fun autoCompleteChangeListener(inputField : AutoCompleteTextView) : TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(!s!!.isEmpty() ){
                    inputField.error = null
                }

            }
        }

        return  textWatcher
    }

    private fun textChangeListener(inputField : TextInputEditText, typeofInput : String) : TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(!s!!.isEmpty() && typeofInput == Constants.AMOUNT ){
                    inputField.error = null
                }
                else {
                    if(s.trim().split(" ").size < 3){
                        inputField.error = "$typeofInput should be at least 3 words"
                    }
                    else {
                        inputField.error = null
                    }
                }

            }
        }

        return  textWatcher
    }

    private fun setupTextChangeListeners(){
        binding.participants.addTextChangedListener(autoCompleteChangeListener(binding.participants))
        binding.billAmount.addTextChangedListener(textChangeListener(binding.billAmount, Constants.AMOUNT))
        binding.billDescription.addTextChangedListener(textChangeListener(binding.billDescription, Constants.DESCRIPTION))
    }

    private fun navigateToAddParticipantActivity(){
        val addParticipantActivity = Intent(this@AddBillsActivity, AddParticipantActivity::class.java )
        addParticipantActivity.putExtra(Constants.SPLIT_BILL_BUCKET_DETAILS, splitBillBucket)
        startActivity(addParticipantActivity)
    }

    private fun addParticipantBill(){
        val billDescription = binding.billDescription.text.toString().trim()
        val billAmount = binding.billAmount.text.toString().trim()
        val tripBillsDetails = TripBillsDetails(null, billDescription,billAmount.toInt(),participantId,null,splitBillBucket?.splitBillId,null)
        addBillAmount(tripBillsDetails)
    }

    @SuppressLint("ServiceCast")
    private fun hideKeyboard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)

        }
    }

    override fun displayEmptyLayout() {
       showViewWhenDataIsNotAvailable()
    }

    override fun updateListForView(index: Int) {

    }

    private fun openBillPayoutActivity(){
        val billPayoutActivity = Intent(this@AddBillsActivity,BillPayoutActivity::class.java)
        billPayoutActivity.putExtra(Constants.SPLIT_BILL_BUCKET_DETAILS, splitBillBucket)
        startActivity(billPayoutActivity)
    }


}



