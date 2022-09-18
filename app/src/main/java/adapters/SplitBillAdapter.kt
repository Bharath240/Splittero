package adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import bharath.uppalanchi.splittero.R
import database.DBHandler
import modals.SplitBillBucket
import utils.Constants

class SplitBillAdapter(private val context : Context, private var list : ArrayList<SplitBillBucket>, private val activityName : String, private val splitBillAdapterInterface : SplitBillAdapterInterface) : RecyclerView.Adapter<SplitBillAdapter.ViewHolder>(){

    private val avatarList = arrayOf<Int>(R.drawable.mountain, R.drawable.airplane,R.drawable.deck_chair,R.drawable.taxi,R.drawable.suitcase,R.drawable.hot_air_balloon)

    private val db = DBHandler(context,null)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.split_bill_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val currentObject : SplitBillBucket = list.get(position)
        val imageIndex: Int
        val nullableInt: Int? = currentObject.splitBillImageIndex
        imageIndex = nullableInt ?: 0
        holder.splitBillBucketAvatar.setImageResource(avatarList[imageIndex])
        holder.splitBillBucketName.text = currentObject.splitBillName
        holder.splitBillCreatedDate.text = currentObject.splitBillCreatedDate

        if(activityName == Constants.CREATE_SPLIT_BILL){

        }

        if (activityName == Constants.TRASH_SPLIT_BILL) {
            holder.splitBillBucketCard.setOnClickListener{
                areYouSureWantToPermanentlyDeleteOrRestoreSplitBill(currentObject,position)
            }
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
       val splitBillBucketAvatar : ImageView = itemView.findViewById(R.id.splitBillBucketAvatar)
        val splitBillBucketName : TextView = itemView.findViewById(R.id.splitBillBucketName)
        val splitBillBucketCard : CardView = itemView.findViewById(R.id.split_bill_card)
        val splitBillCreatedDate : TextView = itemView.findViewById(R.id.split_bill_created_date)
    }

    fun notifyDeleteItem(position: Int) {
        val db = DBHandler(context,null)
        val result =  db.deleteSplitBill(list[position])
        if(result > 0){
            Toast.makeText(context, "${list[position].splitBillName} is successfully deleted", Toast.LENGTH_SHORT).show()
            list.removeAt(position)

            notifyItemRemoved(position)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(splitBillBucketList : ArrayList<SplitBillBucket>){
        this.list = splitBillBucketList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun areYouSureWantToPermanentlyDeleteOrRestoreSplitBill(splitBillBucket: SplitBillBucket, position: Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Restore/Delete")
        builder.setMessage("Do you want to restore or permanently delete ${list[position].splitBillName} ?")

        builder.setPositiveButton("Delete Permanently") { dialog, which ->
            permanentlyDeleteSplitBill(splitBillBucket,position)

        }

        builder.setNegativeButton("Restore") { dialog, which ->
            restoreSplitBill(splitBillBucket,position)
        }
        builder.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun permanentlyDeleteSplitBill(splitBillBucket : SplitBillBucket, position: Int){
        val success = db.permanentlyDeleteSplitBill(splitBillBucket)
        if(success > 0) {
            Toast.makeText(context, "${list[position].splitBillName} is permanently deleted", Toast.LENGTH_SHORT).show()
            list.removeAt(position)
            if(list.size == 0){
                splitBillAdapterInterface.displayEmptyLayout()
            }

            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun restoreSplitBill(splitBillBucket: SplitBillBucket, position: Int){
        val success = db.restoreSplitBill(splitBillBucket)
        if(success > 0) {
            Toast.makeText(context, "${list[position].splitBillName} is restored !!", Toast.LENGTH_SHORT).show()
            list.removeAt(position)
            if(list.size == 0){
                splitBillAdapterInterface.displayEmptyLayout()
            }
            notifyDataSetChanged()
        }
    }
}

interface SplitBillAdapterInterface{
    fun displayEmptyLayout()
}