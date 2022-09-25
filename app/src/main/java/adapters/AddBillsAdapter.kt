package adapters
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import bharath.uppalanchi.splittero.R
import database.DBHandler
import modals.TripBillsDetails
import utils.GlobalInterface

class AddBillsAdapter(private val context : Context, private var list : ArrayList<TripBillsDetails>,private val globalInterface : GlobalInterface) : RecyclerView.Adapter<AddBillsAdapter.ViewHolder>() {
    private val db = DBHandler(context,null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bill_details_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tripBillsDetails = list[position]
        holder.participantName.text = tripBillsDetails.participantName
        holder.billDescription.text = tripBillsDetails.billDescription
        holder.billAmount.text = "${tripBillsDetails.billAmount} ₹"

        holder.billDetail.setOnClickListener{
            doYouWantToDeleteTheBill(tripBillsDetails, position)
        }

    }

    override fun getItemCount(): Int {
        return  list.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val participantName : TextView = itemView.findViewById(R.id.participant_name)
        val billDescription : TextView = itemView.findViewById(R.id.bill_description)
        val billAmount : TextView = itemView.findViewById(R.id.bill_amount)
        val billDetail : CardView = itemView.findViewById(R.id.bill_detail)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun doYouWantToDeleteTheBill(tripBillsDetails: TripBillsDetails, position: Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Bill")
        builder.setMessage("Do you want to delete Bill of ${list[position].participantName} ?")

        builder.setPositiveButton("Delete") { dialog, which ->
            deleteBill(tripBillsDetails,position)

        }

        builder.setNegativeButton("No") { dialog, which ->
            //Do Nothing
        }
        builder.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteBill(tripBillsDetails: TripBillsDetails, position: Int){
        val success = db.deleteTripBill(tripBillsDetails)
        if(success > 0) {
            Toast.makeText(context, "${tripBillsDetails.participantName} bill Deleted successfully !!", Toast.LENGTH_SHORT).show()
            list.removeAt(position)
            if(list.size == 0){
                globalInterface.displayEmptyLayout()
            }
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(updatedList : ArrayList<TripBillsDetails>){
        list = updatedList
        notifyDataSetChanged()
    }
}