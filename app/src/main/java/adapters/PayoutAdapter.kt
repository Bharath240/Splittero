package adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import bharath.uppalanchi.splittero.R
import modals.PayoutDetails
import modals.TripBillsDetails
import utils.GlobalInterface

class PayoutAdapter(private val context : Context, private var list : ArrayList<PayoutDetails>) : RecyclerView.Adapter<PayoutAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.payout_card, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObject = list[position]
        holder.participant_name.text = currentObject.participantName
        holder.amount_paid.text = "${currentObject.totalPaid} ₹"
        holder.pay_or_receive.text = "${currentObject.payOrReceiveAmount} ₹"
        if(currentObject.payOrReceiveAmount!! > 0){
            holder.payout_card.setCardBackgroundColor(Color.parseColor("#9EF9A1"))
        }
        else {
            holder.payout_card.setCardBackgroundColor(Color.parseColor("#F99E9E"))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val participant_name : TextView = itemView.findViewById(R.id.participant_name)
        val amount_paid : TextView = itemView.findViewById(R.id.amount_paid)
        val pay_or_receive : TextView = itemView.findViewById(R.id.pay_or_receive)
        val payout_card : CardView = itemView.findViewById(R.id.payout_card)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(updatedList : ArrayList<PayoutDetails>){
        list = updatedList
        notifyDataSetChanged()
    }

}