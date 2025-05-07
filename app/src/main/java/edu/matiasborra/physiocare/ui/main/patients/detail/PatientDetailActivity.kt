package edu.matiasborra.physiocare.ui.main.patients.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import edu.matiasborra.physiocare.R
import edu.matiasborra.physiocare.databinding.ActivityPatientDetailBinding

class PatientDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PATIENT_ID = "patient_id"
    }

    private lateinit var binding: ActivityPatientDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val patientId = intent.getStringExtra(EXTRA_PATIENT_ID)
            ?: run { finish(); return }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(
                    R.id.detail_fragment_container,
                    PatientDetailFragment.newInstance(patientId)
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}