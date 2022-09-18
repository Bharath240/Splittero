package dialogs

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import bharath.uppalanchi.splittero.R
import bharath.uppalanchi.splittero.databinding.ActivityAddParticipantBinding
import com.google.android.material.textfield.TextInputLayout
import database.DBHandler
import modals.ParticipantDetails
import modals.SplitBillBucket
import java.util.*

class AddParticipant(context: Context, var splitBillBucket : SplitBillBucket?) : Dialog(context) {
    private lateinit var binding: ActivityAddParticipantBinding
    private lateinit var db : DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddParticipantBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db = DBHandler(context, null)
        binding.add.setOnClickListener{
            addParticipant()
        }

    }

   private fun addParticipant(){
       binding.participantName.addTextChangedListener(textChangeListener( binding.addParticipantInput))
        val participantName = binding.participantName.text.toString().trim().uppercase()
       if(addParticipantValidate()){
           val participantDetails = ParticipantDetails(null,participantName,splitBillBucket?.splitBillId,null)
           val success : Long =    db.addParticipant(participantDetails)

           if(success > 0){
               Toast.makeText(context,"Participant $participantName is added successfully !! ",
                   Toast.LENGTH_SHORT).show()
               dismiss()
           }
       }
    }

    private fun textChangeListener(addParticipantInput : TextInputLayout) : TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(addParticipantInput.error !=null){
                    binding.addParticipantInput.error = "Participant Name should be at least 3 letters"
                }
                if(s?.length!! > 2){
                    addParticipantInput.error = null
                }
            }
        }

        return  textWatcher
    }

    private fun addParticipantValidate() : Boolean {
        val participantName : String = binding.participantName.text.toString().trim().uppercase()
        return when {
            binding.participantName.text.isNullOrEmpty() -> {
                binding.addParticipantInput.error = "Participant Name should not be empty"
                false
            }
            participantName.length <3 -> {
                binding.addParticipantInput.error = "Participant Name should be at least 3 letters"
                false
            }

            else -> true
        }

    }
}