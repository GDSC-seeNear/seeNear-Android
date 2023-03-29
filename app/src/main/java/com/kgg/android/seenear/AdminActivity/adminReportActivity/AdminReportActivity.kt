package com.kgg.android.seenear.AdminActivity.adminReportActivity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.kgg.android.seenear.databinding.ActivityAdminReportBinding
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.Ner
import com.kgg.android.seenear.network.data.Report
import com.kgg.android.seenear.network.data.Sentiment
import com.kgg.android.seenear.network.data.StatusCheck


class AdminReportActivity : AppCompatActivity(){
    private lateinit var binding: ActivityAdminReportBinding
    private lateinit var viewModel : AdminReportViewModel
    lateinit var report: Report
    private var elderly_id : Int = 0
    private var date : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        elderly_id = intent.getIntExtra("elderlyId", 0)
        date = intent.getStringExtra("date")

        val repository = RetrofitRepository()
        val viewModelFactory = AdminReportViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(AdminReportViewModel::class.java)
//        viewModel.ReportInfo.observe(this, Observer {
//            if (it!=null){
//                Log.d("myName",it.toString())
//                report = it
//                binding.reportDate.text = report.date
//
//
//            }
//        })
//        App.prefs.accessToken?.let { viewModel.getUserReportByDate(it, elderly_id, date!!) }

        // 테스트를 위한 가짜 데이터

        report = Report(
            date= "2023-01-02",

            sentiment = arrayListOf<Sentiment>(
                Sentiment("기쁨", 3),
                Sentiment("우울", 5),
                Sentiment("불안", 2)

            ),

            ner = arrayListOf<Ner>(
                Ner(
                    id=  3,
                    type ="PS_NAME",
                    target= "아들" ,
                    fullText = "아들이 너무 보고싶어",
                    chatId = 3,
                    elderlyId =7 ),
                Ner(
                    id=  4,
                    type ="PS_NAME",
                    target= "허리" ,
                    fullText = "허리가 너무 아파",
                    chatId = 3,
                    elderlyId =7 ),
                Ner(
                    id=  4,
                    type ="PS_NAME",
                    target= "머리가" ,
                    fullText = "갑자기 머리가 지끈지끈해",
                    chatId = 3,
                    elderlyId =7 ),
            ),

            statusCheck = arrayListOf<StatusCheck>(
                StatusCheck("meal1", false),
                StatusCheck("meal2", true),
                StatusCheck("meal3", false),
                StatusCheck("health", false),
                StatusCheck("physicalActivity", false),
                StatusCheck("feel", true),
                StatusCheck("toilet", false),
                )
        )


        binding.reportDate.text = report.date

        // 식사 복용

        if(report.statusCheck.find { it.type == "meal1" }?.done == true)
            binding.meal1Text.text = "아침 식사 완료"
        else
            binding.meal1Text.text = "아침 식사 미완료"

        if(report.statusCheck.find { it.type == "meal2" }?.done == true)
            binding.meal2Text.text = "점심 식사 완료"
        else
            binding.meal2Text.text = "점심 식사 미완료"

        if(report.statusCheck.find { it.type == "meal3" }?.done == true)
            binding.meal3Text.text = "저녁 식사 완료"
        else
            binding.meal3Text.text = "저녁 식사 미완료"


        // 건강 상태
        if(report.statusCheck.find { it.type == "health" }?.done == true)
            binding.healthText.text = "양호합니다"
        else
            binding.healthText.text = "양호하지 않습니다."

        // 신체 활동
        if(report.statusCheck.find { it.type == "physicalActivity" }?.done == true)
            binding.activityText.text = "충분합니다."
        else
            binding.activityText.text = "부족합니다."

        // 기분 및 컨디션
        if(report.statusCheck.find { it.type == "feel" }?.done == true)
            binding.conditionText.text = "양호합니다."
        else
            binding.conditionText.text = "양호하지 않습니다."

        // 배변활동
        if(report.statusCheck.find { it.type == "toilet" }?.done == true)
            binding.toiletText.text = "원활합니다."
        else
           binding.toiletText.text = "원활하지 않습니다."


        val nerList = report.ner
        val builder = SpannableStringBuilder()
        for (n in nerList) {
            val start = n.fullText!!.indexOf(n.target!!)
            if (start != -1) {
                val end = start + n.target!!.length
                val highlighted = SpannableString(n.fullText)
                highlighted.setSpan(
                    ForegroundColorSpan(Color.RED),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.append(highlighted)
                builder.append("\n") // add line break
            } else {
                builder.append(n.fullText)
                builder.append("\n") // add line break
            }
        }
        binding.nerText.text = builder

        val dataSets = ArrayList<PieDataSet>()
        val sentList = report.sentiment
        val datavalues = ArrayList<PieEntry>()

        for (n in sentList) {
            datavalues.add(PieEntry(n.count.toFloat(), n.label))
        }

        // 표에 데이터별 색상 세팅

        // 표에 데이터별 색상 세팅
        val colorArray = intArrayOf(
            Color.GREEN,
            Color.RED,
            Color.LTGRAY,
            Color.BLUE,
            Color.CYAN,
            Color.DKGRAY,
        )


        // 표에 그릴 데이터 묶음 세팅
        // 데이터의 제목을 달아줍니다.


        // 표에 그릴 데이터 묶음 세팅
        // 데이터의 제목을 달아줍니다.
        val pieDataSet1 = PieDataSet(datavalues, "")
        pieDataSet1.setColors(*colorArray)
        dataSets.add(pieDataSet1)
        // 표에 데이터를 넣고, 표를 그려줍니다.
        val pieData = PieData()
        pieData.addDataSet(pieDataSet1)
        // View 에 데이터를 넣음
        binding.piechart.description = null
        binding.piechart.setData(pieData)
        binding.piechart.setEntryLabelTextSize(12f)
        val boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        binding.piechart.setEntryLabelTypeface(boldTypeface)
        // 원 그래프를 데이터를 따라서 그림
        // 원 그래프를 데이터를 따라서 그림
        binding.piechart.invalidate()

    }







}