package bharath.uppalanchi.splittero.activities
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bharath.uppalanchi.splittero.R
import bharath.uppalanchi.splittero.databinding.ActivityAddBillsBinding
import dialogs.AddParticipant
import modals.SplitBillBucket
import utils.Constants


class AddBillsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddBillsBinding
    private  var splitBillBucket: SplitBillBucket? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBillsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if(intent.hasExtra(Constants.SPLIT_BILL_BUCKET_DETAILS)){
            splitBillBucket = intent.getParcelableExtra(Constants.SPLIT_BILL_BUCKET_DETAILS)
            setOnClickListeners()
        }



    }

    private fun setOnClickListeners(){
            binding.materialDesignFloatingActionMenuItem1.setOnClickListener(this)
            binding.materialDesignFloatingActionMenuItem2.setOnClickListener(this)

    }
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.material_design_floating_action_menu_item1 -> {
                showAddParticipantDialog()
            }
            R.id.material_design_floating_action_menu_item2-> {
                Toast.makeText(applicationContext,"Add Bill",Toast.LENGTH_SHORT).show()
                closeFabOptionsMenu()
            }
        }
    }

    private fun closeFabOptionsMenu(){
        binding.materialDesignAndroidFloatingActionMenu.close(true)
    }

    private fun showAddParticipantDialog(){
        val dialog = AddParticipant(this, splitBillBucket)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        closeFabOptionsMenu()
    }

}