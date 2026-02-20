package com.example.akiriapp.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.akiriapp.R
import com.example.akiriapp.auth.RoleSelectionActivity
import com.example.akiriapp.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupDots()

        binding.btnGetStarted.setOnClickListener {
            if (binding.viewPager.currentItem + 1 < adapter.itemCount) {
                binding.viewPager.currentItem = binding.viewPager.currentItem + 1
            } else {
                startActivity(Intent(this, RoleSelectionActivity::class.java))
                finish()
            }
        }
    }

    private fun setupViewPager() {
        val pages = listOf(
            OnboardingPage(R.drawable.onboarding_hero, R.string.onboarding_title_1, R.string.onboarding_desc_1),
            OnboardingPage(R.drawable.onboarding_learn, R.string.onboarding_title_2, R.string.onboarding_desc_2),
            OnboardingPage(R.drawable.onboarding_community, R.string.onboarding_title_3, R.string.onboarding_desc_3)
        )
        adapter = OnboardingAdapter(pages)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
                if (position == adapter.itemCount - 1) {
                    binding.btnGetStarted.setText(R.string.onboarding_button_start)
                } else {
                    binding.btnGetStarted.setText(R.string.onboarding_button_next)
                }
            }
        })
    }

    private fun setupDots() {
        val dots = arrayOfNulls<ImageView>(adapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }

        for (i in dots.indices) {
            dots[i] = ImageView(this@OnboardingActivity).apply {
                setImageDrawable(ContextCompat.getDrawable(this@OnboardingActivity, R.drawable.dot_indicator))
                // We'll use a better dot drawable below
                alpha = 0.5f
            }
            binding.llDots.addView(dots[i], layoutParams)
        }
        updateDots(0)
    }

    private fun updateDots(position: Int) {
        for (i in 0 until binding.llDots.childCount) {
            val dot = binding.llDots.getChildAt(i) as ImageView
            if (i == position) {
                dot.alpha = 1.0f
                dot.scaleX = 1.2f
                dot.scaleY = 1.2f
            } else {
                dot.alpha = 0.5f
                dot.scaleX = 1.0f
                dot.scaleY = 1.0f
            }
        }
    }
}
