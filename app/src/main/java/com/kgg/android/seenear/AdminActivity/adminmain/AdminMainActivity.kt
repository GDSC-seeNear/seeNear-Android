package com.kgg.android.seenear.AdminActivity.adminmain

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.kgg.android.seenear.UserActivity.usermain.UserMainActivity
import com.kgg.android.seenear.data.userInfo
import com.kgg.android.seenear.databinding.ActivityAdminMainBinding
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.registerResponse
import java.text.SimpleDateFormat
import java.util.*

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
        val dialog = UserMainActivity.LoadingDialog(this)

        val repository = RetrofitRepository()
        val viewModelFactory = AdminMainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(AdminMainViewModel::class.java)
        viewModel.myName.observe(this, Observer {
            if (it!=null){
                Log.d("myName",it.toString())
                binding.myNameText.text = it + " 님의"
                dialog.dismiss()
            }
            else{
                App.prefs.refreshToken?.let { it1 -> viewModel.refreshToken(it1) }
                dialog.show() // token을 refresh하기 위한 로딩 구현
            }

        })
        App.prefs.accessToken?.let { viewModel.myInfo(it) }

        viewModel.tokenInfo.observe(this, Observer {
            App.prefs.accessToken?.let { viewModel.myInfo(it) }
            App.prefs.accessToken?.let { viewModel.managedElderly(it) }
            dialog.dismiss()
        })


        viewModel.userList.observe(this, Observer {
            Log.d("userList",it.toString())
            userList = it
            datas.addAll(userList)
            Log.d("datas", datas.toString())
            initRecycler()
        })

        App.prefs.accessToken?.let { viewModel.managedElderly(it) }


        binding.button2.setOnClickListener {
            val intent = Intent(this, RegisterActivity1::class.java)
            startActivity(intent)
        }

        initRecycler()

        // 로그아웃

        binding.logoutBtn2.setOnClickListener {

            App.prefs.accessToken = null
            App.prefs.refreshToken = null
            App.prefs.role = null
            App.prefs.id = null

            val intent = Intent(this, IntroActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)

        }
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


                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(item.birth)

                val dob = Calendar.getInstance()
                dob.time = date

                val today = Calendar.getInstance()
                var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
                if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }
                profile_birth.text = "만 "+ age.toString() +"세"

                profile_address.text = item.addressDetail

                itemView.setOnClickListener {
                    val intent = Intent(this@AdminMainActivity, AdminDetailActivity::class.java)
                    intent.putExtra("elderlyId", item.id)
                    startActivity(intent)
                }

            }
        }


    }


    class LoadingDialog(context: Context) : Dialog(context){

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_loading)

            // 취소 불가능
            setCancelable(false)

            // 배경 투명하게 바꿔줌
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }


    }
}
