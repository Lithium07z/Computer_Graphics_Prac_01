package kr.ac.hallym.prac01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.hallym.prac01.databinding.LayoutViewBindingBinding

class BMIActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmiactivity)

        val binding = LayoutViewBindingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bmiButton.setOnClickListener {
            val height = binding.tallField.text.toString().toDouble()
            val weight = binding.weightField.text.toString().toDouble()
            val bmi = weight / Math.pow(height / 100.0, 2.0)

            binding.resultLabel.text = "키: ${binding.tallField.text}, 체중: ${binding.weightField.text}, BMI: $bmi"
        }
    }
}