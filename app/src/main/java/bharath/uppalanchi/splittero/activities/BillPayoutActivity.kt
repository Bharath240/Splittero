package bharath.uppalanchi.splittero.activities

import adapters.AddBillsAdapter
import adapters.PayoutAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import bharath.uppalanchi.splittero.R
import bharath.uppalanchi.splittero.databinding.ActivityBillPayoutBinding
import database.DBHandler
import modals.ParticipantDetails
import modals.PayoutDetails
import modals.SplitBillBucket
import modals.TripBillsDetails
import utils.Constants

class BillPayoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBillPayoutBinding
    private var splitBillBucket : SplitBillBucket? = null
    private var payoutDetails = ArrayList<PayoutDetails>()
    private var paidParticipantIds = ArrayList<Int?>()
    private lateinit var db : DBHandler
    private var totalExpenseOfTrip : Int = 0
    private lateinit var adapter  : PayoutAdapter
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillPayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db = DBHandler(this@BillPayoutActivity, null)
        setupRecyclerViewForAddParticipant(payoutDetails)
        if(intent.hasExtra(Constants.SPLIT_BILL_BUCKET_DETAILS)){
            splitBillBucket = intent.getParcelableExtra(Constants.SPLIT_BILL_BUCKET_DETAILS)
            getPayoutDetails()
        }
    }

    private fun getPayoutDetails(){
        payoutDetails = db.getPayoutDetails(splitBillBucket?.splitBillId)
        setPaidParticipantIdsAndTotalExpenseOfTrip()
    }

    private fun setPaidParticipantIdsAndTotalExpenseOfTrip(){
        for (payoutDetail in payoutDetails){
            paidParticipantIds.add(payoutDetail.participantId)
            totalExpenseOfTrip += payoutDetail.totalPaid!!
        }
        getAllParticipantDetailsInTrip()
    }

    private fun getAllParticipantDetailsInTrip(){
      val  participantDetails = db.getParticipantsDetails(splitBillBucket?.splitBillId)
        addNoPaidParticipantsIntoList(participantDetails)
    }
    
    private fun addNoPaidParticipantsIntoList(participantDetails: ArrayList<ParticipantDetails>){
        for (item in participantDetails){
            if (!paidParticipantIds.contains(item.participantID)){
                payoutDetails.add(PayoutDetails(item.participantID,item.participantName,0,null))
            }
        }
        
        payoutDetails.sortByDescending { it.totalPaid } //sorting payoutDetails by totalPaid in descending order

        val totalNumberOfParticipantsInTrip = payoutDetails.size
       val expenseOfTripForIndividual = totalExpenseOfTrip/totalNumberOfParticipantsInTrip

        setPayOrReceiveAmountOfIndividual(expenseOfTripForIndividual)

    }

    private fun setPayOrReceiveAmountOfIndividual(expenseOfTripForIndividual : Int){
        for (payoutDetail in payoutDetails) {
            payoutDetail.payOrReceiveAmount = payoutDetail.totalPaid?.minus(expenseOfTripForIndividual)
        }

        if(payoutDetails.size > 0){
            adapter.updateList(payoutDetails)
        }
    }

    private fun setupRecyclerViewForAddParticipant(payoutDetailsList: ArrayList<PayoutDetails>){
        binding.payoutRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PayoutAdapter(this@BillPayoutActivity,payoutDetailsList)
        binding.payoutRecyclerView.adapter = adapter
    }

}