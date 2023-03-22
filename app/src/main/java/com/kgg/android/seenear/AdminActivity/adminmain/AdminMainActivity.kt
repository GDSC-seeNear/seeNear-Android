package com.kgg.android.seenear.AdminActivity.adminmain

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
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kgg.android.seenear.AdminActivity.RegisterActivity.RegisterActivity1
import com.kgg.android.seenear.AdminActivity.RegisterActivity.RegisterActivity2
import com.kgg.android.seenear.AdminActivity.admindetail.AdminDetailActivity
import com.kgg.android.seenear.App
import com.kgg.android.seenear.AuthActivity.IntroActivity
import com.kgg.android.seenear.R
import com.kgg.android.seenear.data.userInfo
import com.kgg.android.seenear.databinding.ActivityAdminMainBinding
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.registerResponse

class AdminMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMainBinding
    private lateinit var viewModel : AdminMainViewModel
    lateinit var profileAdapter: ProfileAdapter
    val datas = mutableListOf<registerResponse>()
    lateinit var userList: List<registerResponse>
    // View에 실시간 정보를 담기 위한 MVVM 패턴

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = RetrofitRepository()
        val viewModelFactory = AdminMainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(AdminMainViewModel::class.java)
        viewModel.myName.observe(this, Observer {
            if (it.isNotEmpty()){
                Log.d("myName",it.toString())
                binding.myNameText.text = it + " 님의"
            }

        })
        App.prefs.refreshToken?.let { viewModel.myInfo(it) }


        viewModel.userList.observe(this, Observer {
            Log.d("userList",it.toString())
            userList = it
            datas.addAll(userList)
            Log.d("datas", datas.toString())
            initRecycler()
        })

        App.prefs.refreshToken?.let { viewModel.managedElderly(it) }


        binding.button2.setOnClickListener {
            val intent = Intent(this, RegisterActivity1::class.java)
            startActivity(intent)
        }

        initRecycler()
    }
    private fun initRecycler() {
        profileAdapter = ProfileAdapter(this)
        binding.recyclerView.adapter = profileAdapter

        val lm = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = lm
        binding.recyclerView.setHasFixedSize(true)

        datas.apply {
            Log.d("datas in recycler", datas.toString())
            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()

        }

    }

    private var lastTimeBackPressed : Long = 0


    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed >= 1500) {
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else{
            finishAffinity()
        }
    }



    inner class ProfileAdapter(private val context: Context) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

        var datas = mutableListOf<registerResponse>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_recycler,parent,false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = datas.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(datas[position])

        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            private val profile_name: TextView = itemView.findViewById(R.id.profile_name)
            private val profile_birth: TextView = itemView.findViewById(R.id.profile_birth)
            private val profile_address: TextView = itemView.findViewById(R.id.profile_address)

            fun bind(item: registerResponse) {
                profile_name.text = item.name
                profile_birth.text = item.birth
                profile_address.text = item.addressDetail

                itemView.setOnClickListener {
                    val intent = Intent(this@AdminMainActivity, AdminDetailActivity::class.java)
                    intent.putExtra("elderlyId", item.id)
                    startActivity(intent)
                }

            }
        }


    }

}
