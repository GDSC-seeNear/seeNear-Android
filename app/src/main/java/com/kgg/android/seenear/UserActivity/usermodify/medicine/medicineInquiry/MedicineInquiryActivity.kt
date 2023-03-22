package com.kgg.android.seenear.UserActivity.usermodify.medicine.medicineInquiry

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kgg.android.seenear.AdminActivity.admindetail.AdminDetailActivity
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainViewModel
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainViewModelFactory
import com.kgg.android.seenear.App
import com.kgg.android.seenear.R
import com.kgg.android.seenear.UserActivity.usermain.UserMainViewModel
import com.kgg.android.seenear.UserActivity.usermodify.MedicineModifyActivity
import com.kgg.android.seenear.UserActivity.usermodify.ModifyUserMedicineActivity
import com.kgg.android.seenear.databinding.ActivityAdminMainBinding
import com.kgg.android.seenear.databinding.ActivityMainBinding
import com.kgg.android.seenear.databinding.ActivityMedicineInquiryBinding
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.medicine
import com.kgg.android.seenear.network.data.registerResponse

class MedicineInquiryActivity : AppCompatActivity() {

    val datas = mutableListOf<medicine>()
    lateinit var userList: List<registerResponse>
    private lateinit var binding: ActivityMedicineInquiryBinding
    private lateinit var viewModel : MedicineInquiryViewModel
    lateinit var medicineAdapter: MedicineAdapter

    companion object{
        var userInfo : registerResponse = registerResponse()
        var medicine : medicine = medicine()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicineInquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userName.text = intent.getStringExtra("name") + " 님의"

        val repository = RetrofitRepository()
        val viewModelFactory = MedicineInquiryViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(MedicineInquiryViewModel::class.java)
        viewModel.medicineList.observe(this, Observer {
            Log.d("medicineList",it.toString())
            datas.addAll(it)
            medicineAdapter.notifyDataSetChanged()
        })
        App.prefs.refreshToken?.let { viewModel.medicineInquiry(it) }

        initRecycler()

        binding.medicineCreate.setOnClickListener {
            val intent = Intent(this@MedicineInquiryActivity, ModifyUserMedicineActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecycler() {
        medicineAdapter = MedicineAdapter(this)
        binding.recyclerView.adapter = medicineAdapter

        val lm = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = lm
        binding.recyclerView.setHasFixedSize(true)


        datas.apply {
            Log.d("datas in recycler", datas.toString())
            medicineAdapter.datas = datas
            medicineAdapter.notifyDataSetChanged()

        }

    }


    inner class MedicineAdapter(private val context: Context) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {

        var datas = mutableListOf<medicine>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.medicine_recycler,parent,false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = datas.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(datas[position])
            holder.itemView.setOnClickListener {

            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            private val medicine_name: TextView = itemView.findViewById(R.id.medicine_name)
            private val medicine_day: TextView = itemView.findViewById(R.id.medicine_day)
            private val medicine_deleteBtn: TextView = itemView.findViewById(R.id.medicine_deleteBtn)
            private val itemViewBtn: ConstraintLayout = itemView.findViewById(R.id.itemVIew)

            fun bind(item: medicine) {
                medicine_name.text = item.name

                when(item.period){
                    24 -> medicine_day.text = "1일 1회"
                    12 -> medicine_day.text = "1일 2회"
                    8 -> medicine_day.text = "1일 3회"
                    24 * 2 -> medicine_day.text = "2일 1회"
                    24 * 3 -> medicine_day.text = "3일 1회"
                    24 * 7 -> medicine_day.text = "일주일 1회"
                }

                medicine_deleteBtn.setOnClickListener {
                    App.prefs.refreshToken?.let { it1 -> viewModel.medicineDelete(item.id, it1) }
                    datas.removeAt(position)
                    notifyDataSetChanged()
                    notifyItemRemoved(position)
//                    val intent = Intent(this@MedicineInquiryActivity, MedicineInquiryActivity::class.java)
//                    startActivity(intent)
//                    finish()
                }

                itemViewBtn.setOnClickListener {
                    val selectedItem = datas[position]
                    medicine = selectedItem
                    val intent = Intent(this@MedicineInquiryActivity, MedicineModifyActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    }

}
