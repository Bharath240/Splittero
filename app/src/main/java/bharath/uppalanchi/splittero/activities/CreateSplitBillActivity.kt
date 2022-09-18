package bharath.uppalanchi.splittero.activities

import adapters.SplitBillAdapter
import adapters.SplitBillAdapterInterface
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bharath.uppalanchi.splittero.R
import bharath.uppalanchi.splittero.databinding.ActivityCreateSplitBillBinding
import database.DBHandler
import dialogs.AddSplitBillDialog
import dialogs.OnDialogCloseListener
import modals.SplitBillBucket
import utils.Constants
import utils.SwipeToDeleteCallback


class CreateSplitBillActivity : AppCompatActivity(), View.OnClickListener, OnDialogCloseListener, SplitBillAdapterInterface {
    private lateinit var binding: ActivityCreateSplitBillBinding
    private var splitBillBucketList = ArrayList<SplitBillBucket>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSplitBillBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getSplitBillDetails()
        callOnClickListeners()
    }



    private fun callOnClickListeners(){
        binding.addSplitBill.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.add_split_bill -> {
                val dialog = AddSplitBillDialog(this,this@CreateSplitBillActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()

            }
        }
    }

    @SuppressLint("Range")
    private fun getSplitBillDetails(){
        val db = DBHandler(this, null)
        val cursor = db.getSplitBucketDetails()
        splitBillBucketList.clear()
        while(cursor!!.moveToNext()){
            splitBillBucketList.add(SplitBillBucket(cursor.getInt(cursor.getColumnIndex(DBHandler.ID_COL)),cursor.getString(cursor.getColumnIndex(DBHandler.SPLIT_BILL_NAME_COL)),cursor.getInt(cursor.getColumnIndex(DBHandler.IMAGE_INDEX)),cursor.getInt(cursor.getColumnIndex(DBHandler.TEMPORARY_DELETE)),cursor.getString(cursor.getColumnIndex(DBHandler.CREATED_DATE))))

        }

        if(splitBillBucketList.size > 0){
            binding.scrollView.visibility = View.VISIBLE
            binding.emptyLayout.visibility = View.GONE
            setupRecyclerViewForSplitBillBuckets(splitBillBucketList)
        }
        else {
            binding.scrollView.visibility = View.GONE
            binding.emptyLayout.visibility = View.VISIBLE
        }


    }


    private fun setupRecyclerViewForSplitBillBuckets(splitBillBucketList : ArrayList<SplitBillBucket>){
        binding.splitBillRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SplitBillAdapter(this@CreateSplitBillActivity,splitBillBucketList,Constants.CREATE_SPLIT_BILL,this@CreateSplitBillActivity)
        binding.splitBillRecyclerView.adapter = adapter

        swipeToDeleteSplitBill(splitBillBucketList)

    }

    private fun swipeToDeleteSplitBill(splitBillBucketList : ArrayList<SplitBillBucket>){
        val editSwiperHandler = object : SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                areYouSureWantToDeleteSplitBill(viewHolder,splitBillBucketList)
            }
        }


            val editItemTouchHelper = ItemTouchHelper(editSwiperHandler)

            editItemTouchHelper.attachToRecyclerView(binding.splitBillRecyclerView)



    }

   private fun areYouSureWantToDeleteSplitBill(viewHolder: RecyclerView.ViewHolder,splitBillBucketList : ArrayList<SplitBillBucket>){
       val adapter = binding.splitBillRecyclerView.adapter as SplitBillAdapter
       val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Split Bill")
        builder.setMessage("Are you sure want to delete ${splitBillBucketList.get(viewHolder.adapterPosition).splitBillName}?")

        builder.setPositiveButton("Yes") { dialog, which ->
            adapter.notifyDeleteItem(viewHolder.adapterPosition)
            getSplitBillDetails()

        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            adapter.updateList(splitBillBucketList)
        }
        builder.show()
    }

    override fun onDialogClose() {
        getSplitBillDetails()
    }

    override fun displayEmptyLayout() {

    }


}