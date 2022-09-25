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
import modals.ParticipantDetails
import utils.GlobalInterface


class ParticipantsListAdapter(private val context : Context, private var list : ArrayList<ParticipantDetails>, private val globalInterface : GlobalInterface) : RecyclerView.Adapter<ParticipantsListAdapter.ViewHolder>() {
    private val db = DBHandler(context,null)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.participant_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val currentObject = list.get(position)
        holder.participant_name.text = currentObject.participantName
        holder.participant_card.setOnClickListener {
            doYouWantToDeleteTheParticipant(currentObject,position)
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val participant_name : TextView = itemView.findViewById(R.id.participant_name)
        val participant_card : CardView = itemView.findViewById(R.id.participant_card)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun doYouWantToDeleteTheParticipant(participantDetails: ParticipantDetails, position: Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Participant")
        builder.setMessage("Do you want to delete participant ${list[position].participantName} ?")

        builder.setPositiveButton("Delete") { dialog, which ->
            deleteParticipant(participantDetails,position)

        }

        builder.setNegativeButton("No") { dialog, which ->
            //Do Nothing
        }
        builder.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteParticipant(participantDetails: ParticipantDetails, position: Int){
        val success = db.deleteParticipant(participantDetails)
        if(success > 0) {
            Toast.makeText(context, "${list[position].participantName} is deleted successfully !!", Toast.LENGTH_SHORT).show()
            list.removeAt(position)
            globalInterface.updateListForView(position)
            if(list.size == 0){
                globalInterface.displayEmptyLayout()
            }
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
     fun updateList(updatedList : ArrayList<ParticipantDetails>){
        list = updatedList
        notifyDataSetChanged()
    }


}