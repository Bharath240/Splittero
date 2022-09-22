package dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.annotation.RequiresApi
import bharath.uppalanchi.splittero.databinding.ActivityAddSplitBillDialogBinding
import com.google.android.material.textfield.TextInputLayout
import database.DBHandler
import modals.SplitBillBucket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddSplitBillDialog(context: Context,private var listener: OnDialogCloseListener?) : Dialog(context) {
    private lateinit var binding: ActivityAddSplitBillDialogBinding
    private lateinit var db : DBHandler
    private var splitBillBucketList = ArrayList<SplitBillBucket>()
    private var splitBillBucketNames = ArrayList<String?>()



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSplitBillDialogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db = DBHandler(context, null)
        getSplitBillBucketNames()
        binding.create.setOnClickListener{
            insertSplitBillIntoTable()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    private fun insertSplitBillIntoTable(){
        binding.splitBillName.addTextChangedListener(textChangeListener( binding.createSplitBillInput))
        val imageIndex : Int = getImageIndex()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(createSplitBillValidate()){
                val success : Long =    db.insertSplitBill(binding.splitBillName.text.toString().trim()
                    .uppercase(Locale.getDefault()),imageIndex,createdDate())

                if(success > 0){
                    Toast.makeText(context,"Split Bill "+ binding.splitBillName.text.toString().trim()+ " is created!!",Toast.LENGTH_SHORT).show()
                }
                listener?.onDialogClose()
                dismiss()

            }

        }




    }

    @SuppressLint("Range")
    fun getImageIndex():Int {
        val cursor = db.getPreviousSplitBillBucketIndex()
        val imageIndex : Int = when {
            cursor?.moveToFirst() == false -> {
                0
            }
            cursor?.getInt(cursor.getColumnIndex(DBHandler.IMAGE_INDEX)) == 5 -> {
                0
            }
            else -> {
                cursor?.getInt(cursor.getColumnIndex(DBHandler.IMAGE_INDEX))!!.plus(1)
            }
        }
        return imageIndex
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createdDate(): String {
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")

        return current.format(formatter)
    }

   private fun createSplitBillValidate() : Boolean {
       val splitBillName : String = binding.splitBillName.text.toString().trim().uppercase()
       return when {
           binding.splitBillName.text.isNullOrEmpty() -> {
               binding.createSplitBillInput.error = "Split Bill Name should not be empty"
               false
           }
           splitBillName.length <3 -> {
               binding.createSplitBillInput.error = "Split Bill Name should be at least 3 letters"
               false
           }
           splitBillBucketNames.contains(splitBillName) -> {
               binding.createSplitBillInput.error = "Split Bill Name already exists !! Please check in CREATE/TRASH"
               false
           }
           else -> true
       }

   }

    private fun textChangeListener(createSplitBillInput : TextInputLayout) : TextWatcher{
         val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
           if(createSplitBillInput.error !=null){
               binding.createSplitBillInput.error = "Split Bill Name should be at least 3 letters"
           }
                if(s?.length!! > 2){
                  createSplitBillInput.error = null
              }
            }
        }

        return  textWatcher
    }

    @SuppressLint("Range")
    private fun getSplitBillBucketNames(){
        val db = DBHandler(context, null)
        val cursor = db.getSplitBillBucketNames()
        splitBillBucketList.clear()
        while(cursor!!.moveToNext()){
            splitBillBucketList.add(SplitBillBucket(cursor.getInt(cursor.getColumnIndex(DBHandler.ID_COL)),cursor.getString(cursor.getColumnIndex(DBHandler.SPLIT_BILL_NAME_COL)),cursor.getInt(cursor.getColumnIndex(DBHandler.IMAGE_INDEX)),cursor.getInt(cursor.getColumnIndex(DBHandler.TEMPORARY_DELETE)),cursor.getString(cursor.getColumnIndex(DBHandler.CREATED_DATE))))

        }

        splitBillBucketNames.clear()
        for (item in splitBillBucketList) {
            splitBillBucketNames.add(item.splitBillName)
        }
    }


}

interface OnDialogCloseListener{
    fun onDialogClose()
}