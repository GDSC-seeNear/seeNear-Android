package com.kgg.android.seenear.AdminActivity.admindetail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainViewModel
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainViewModelFactory
import com.kgg.android.seenear.App
import com.kgg.android.seenear.R
import com.kgg.android.seenear.databinding.ActivityAdminDetailBinding
import com.kgg.android.seenear.databinding.ActivityAdminMainBinding
import com.kgg.android.seenear.network.RetrofitRepository
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kgg.android.seenear.AdminActivity.adminReportActivity.AdminReportActivity
import com.kgg.android.seenear.AdminActivity.adminmain.AdminMainActivity
import com.kgg.android.seenear.UserActivity.usermodify.ModifyUserInfoActivity
import com.kgg.android.seenear.UserActivity.usermodify.medicine.medicineInquiry.MedicineInquiryActivity
import com.kgg.android.seenear.network.data.*
import java.text.SimpleDateFormat
import java.util.*


class AdminDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDetailBinding
    private lateinit var viewModel : AdminDetailViewModel
    private var elderly_id : Int = 0
    lateinit var reportAdapter: ReportAdapter
    val datas = mutableListOf<Report>()
    lateinit var userList: List<Report>

    companion object{
        var userInfo : registerResponse = registerResponse()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDetailBinding.inflate(layoutInflater)

        elderly_id = intent.getIntExtra("elderlyId", 0)
        var name = ""

        setContentView(binding.root)

        Log.d("lifecycle!!", "onCreate")

        val repository = RetrofitRepository()
        val viewModelFactory = AdminDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(AdminDetailViewModel::class.java)



        viewModel.userInfo.observe(this, Observer {
            Log.d("userInfo",it.toString())

            binding.profileNameDetail.text = it.name

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(it.birth)

            val dob = Calendar.getInstance()
            dob.time = date

            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--
            }

            binding.profileBirthDetail.text = "Age "+ age.toString()
            binding.profileAddressDetail.text = it.addressDetail
            elderly_id = it.id!!
            name = it.name!!
            userInfo = it
        })

        App.prefs.accessToken?.let { viewModel.getUserInfo(it, elderly_id) }


        // 유저 레포트 가져오기



        initRecycler()

        viewModel.userList.observe(this, Observer {
            Log.d("userInfo",it.toString())
            userList = it
            datas.addAll(userList)
            Log.d("datas", datas.toString())
            initRecycler()
        })

        App.prefs.accessToken?.let { viewModel.getUserReports(it, elderly_id) }


        // 유저 개인정보 수정

        binding.userInfoModify.setOnClickListener {
            val intent = Intent(this, ModifyUserInfoActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("elderlyId", elderly_id)
            startActivity(intent)
        }

        // 유저 챗봇정보 수정

        binding.userChatbotModify.setOnClickListener {
            val intent = Intent(this, MedicineInquiryActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("elderlyId", elderly_id)
            startActivity(intent)
        }


    }

    override fun onResume() {
        super.onResume()
        Log.d("lifecycle!!", "onResume")
        App.prefs.accessToken?.let { viewModel.getUserInfo(it, elderly_id) }
    }

    private fun initRecycler() {
        reportAdapter = ReportAdapter(this)
        binding.recyclerView.adapter = reportAdapter

        val lm = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = lm
        binding.recyclerView.setHasFixedSize(true)

        datas.apply {
            Log.d("datas in recycler", datas.toString())
            reportAdapter.datas = datas
            reportAdapter.notifyDataSetChanged()

        }
    }


    inner class ReportAdapter(private val context: Context) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

        var datas = mutableListOf<Report>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.report_recycler,parent,false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = datas.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(datas[position])

        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            private val date: TextView = itemView.findViewById(R.id.date)

            fun bind(item: Report) {
                date.text = item.date

                itemView.setOnClickListener {
                    val intent = Intent(this@AdminDetailActivity, AdminReportActivity::class.java)
                    intent.putExtra("report", item)
                    intent.putExtra("name",binding.profileNameDetail.text )
                    startActivity(intent)
                }

            }
        }


    }
}