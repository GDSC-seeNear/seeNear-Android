package com.kgg.android.seenear.AdminActivity.adminReportActivity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kgg.android.seenear.databinding.ActivityAdminReportBinding
import com.kgg.android.seenear.network.RetrofitRepository
import com.kgg.android.seenear.network.data.Report

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
        report = intent.getParcelableExtra<Report>("report")!!
        val name = intent.getStringExtra("name")
        binding.nameText.text = name


        Log.d("report!!!", report.toString())

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

        binding.reportDate.text = report.date

        // 식사 복용

        if(report.statusList.find { it.type == "meal1" }?.done == true)
            binding.checkedMorning.visibility = View.VISIBLE
        else
            binding.uncheckedMorning.visibility = View.VISIBLE

        if(report.statusList.find { it.type == "meal2" }?.done == true)
            binding.checkedLunch.visibility = View.VISIBLE
        else
            binding.uncheckedLunch.visibility = View.VISIBLE

        if(report.statusList.find { it.type == "meal3" }?.done == true)
            binding.checkedDinner.visibility = View.VISIBLE
        else
            binding.uncheckedDinner.visibility = View.VISIBLE


        // 건강 상태
        if(report.statusList.find { it.type == "health" }?.done == true)
            binding.healthText.text = "It is in good condition."
        else
            binding.healthText.text = "It is not in good condition. \nPlease contact the user to check their status."

        // 신체 활동
        if(report.statusList.find { it.type == "physicalActivity" }?.done == true)
            binding.activityText.text = "It is in a sufficient condition."
        else
            binding.activityText.text = "It is insufficient.  \n" +
                    "Please recommend appropriate physical activities to the user."

        // 기분 및 컨디션
        if(report.statusList.find { it.type == "feel" }?.done == true)
            binding.conditionText.text = "It is in good condition."
        else
            binding.conditionText.text = "It is not in good condition. \n" +
                    "Please contact the user to check their status."

        // 배변활동
        if(report.statusList.find { it.type == "toilet" }?.done == true)
            binding.toiletText.text = "Bowel movements are smooth."
        else
           binding.toiletText.text = "It is not smooth. " +
                   "\nPlease contact the user to check their status."


        val nerList = report.nerList
        val builder = SpannableStringBuilder()
        for (n in nerList) {
            val start = n.content!!.indexOf(n.target!!)
            if (start != -1) {
                val end = start + n.target!!.length
                val highlighted = SpannableString(n.content)
                highlighted.setSpan(
                    ForegroundColorSpan(Color.RED),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.append(highlighted)
                builder.append("\n") // add line break
            } else {
                builder.append(n.content)
                builder.append("\n") // add line break
            }
        }
        binding.nerText.text = builder

        val dataSets = ArrayList<PieDataSet>()
        val sentList = report.sentimentList
        val datavalues = ArrayList<PieEntry>()

        for ((key, value) in sentList) {
            datavalues.add(PieEntry(value.toFloat(), key))
        }

        // 표에 데이터별 색상 세팅

        // 표에 데이터별 색상 세팅
        val colorArray = intArrayOf(
            0xFFEA6D62.toInt(),
            0xFF94D0E1.toInt(),
            0xFFF2B95A.toInt(),
            0xFF398158.toInt(),
        )


        // 표에 그릴 데이터 묶음 세팅
        // 데이터의 제목을 달아줍니다.


        val pieDataSet1 = PieDataSet(datavalues, "")
//
//        // 숫자 포맷터 구현
//        pieDataSet1.setValueFormatter(object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return pieDataSet1.label // 라벨 텍스트만 반환
//            }
//        })
//
        pieDataSet1.setValueTextSize(12f) // 숫자 크기를 12로 설정함
        pieDataSet1.valueTypeface = Typeface.DEFAULT_BOLD
        pieDataSet1.setValueTextColor(Color.WHITE) // 숫자 색상을 검정색으로 변경함

        val totalValue = datavalues.map { it.value }.sum()
        // ValueFormatter 설정
        pieDataSet1.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val ratio = (value / totalValue) * 100
                return "%.0f %%".format(ratio) // 전체 대비 비율값을 계산하여 반환함
            }
        })

        // 표에 그릴 데이터 묶음 세팅
        // 데이터의 제목을 달아줍니다.
        pieDataSet1.setColors(*colorArray)
        dataSets.add(pieDataSet1)
        // 표에 데이터를 넣고, 표를 그려줍니다.
        val pieData = PieData()
        pieData.addDataSet(pieDataSet1)
        // View 에 데이터를 넣음
        binding.piechart.description = null
        binding.piechart.setData(pieData)
        binding.piechart.setDrawEntryLabels(false)
        binding.piechart.setCenterText("Sentiment")
        binding.piechart.setCenterTextColor(Color.parseColor("#414C55"))
        binding.piechart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
        binding.piechart.setCenterTextSize(16f)

        val boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        binding.piechart.setEntryLabelTypeface(boldTypeface)
        binding.piechart.setEntryLabelTextSize(18f)
        binding.piechart.setEntryLabelColor(Color.WHITE) // 숫자 색상을 검정색으로 변경함
        binding.piechart.legend.textSize = 14f
        // 원 그래프를 데이터를 따라서 그림
        binding.piechart.invalidate()

    }







}