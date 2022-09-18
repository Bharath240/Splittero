package bharath.uppalanchi.splittero.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import bharath.uppalanchi.splittero.R
import bharath.uppalanchi.splittero.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        this.callOnClickListeners()
    }

    private fun callOnClickListeners(){
        binding.createSplitBill.setOnClickListener(this)
        binding.trashSplitBill.setOnClickListener(this)
    }
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.create_split_bill -> {
                val intent = Intent(this, CreateSplitBillActivity::class.java)
                startActivity(intent)

            }
            R.id.trash_split_bill-> {
                val intent = Intent(this, TrashBillsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}