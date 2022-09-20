package bharath.uppalanchi.splittero.activities

import adapters.SplitBillAdapter
import utils.GlobalInterface
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import bharath.uppalanchi.splittero.databinding.TrashBillsActivityBinding
import database.DBHandler
import modals.SplitBillBucket
import utils.Constants

class TrashBillsActivity : AppCompatActivity(), GlobalInterface {
    private lateinit var binding: TrashBillsActivityBinding
    private var trashBillBucketList = ArrayList<SplitBillBucket>()
    private lateinit var db : DBHandler;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrashBillsActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
         db = DBHandler(this, null)
        getTrashBills()
    }

    @SuppressLint("Range")
    private fun getTrashBills(){

        val cursor = db.getTrashSplitBucketDetails()
        trashBillBucketList.clear()
        while(cursor!!.moveToNext()){
            trashBillBucketList.add(
                SplitBillBucket(cursor.getInt(cursor.getColumnIndex(DBHandler.ID_COL)),cursor.getString(cursor.getColumnIndex(
                    DBHandler.SPLIT_BILL_NAME_COL)),cursor.getInt(cursor.getColumnIndex(DBHandler.IMAGE_INDEX)),cursor.getInt(cursor.getColumnIndex(
                    DBHandler.TEMPORARY_DELETE)), cursor.getString(cursor.getColumnIndex(DBHandler.CREATED_DATE)))
            )

        }

        if(trashBillBucketList.size > 0){
            binding.scrollView.visibility = View.VISIBLE
            binding.emptyLayout.visibility = View.GONE
            setupRecyclerViewForSplitBillBuckets(trashBillBucketList)
        }
        else {
            binding.scrollView.visibility = View.GONE
            binding.emptyLayout.visibility = View.VISIBLE
        }


    }

    private fun setupRecyclerViewForSplitBillBuckets(splitBillBucketList : ArrayList<SplitBillBucket>){
        binding.splitBillRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SplitBillAdapter(this@TrashBillsActivity,splitBillBucketList, Constants.TRASH_SPLIT_BILL,this@TrashBillsActivity)
        binding.splitBillRecyclerView.adapter = adapter


    }

    override fun displayEmptyLayout() {
        binding.scrollView.visibility = View.GONE
        binding.emptyLayout.visibility = View.VISIBLE
    }

    override fun updateListForView(index: Int) {

    }


}