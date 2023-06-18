package com.example.pj4test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.pj4test.ProjectConfiguration
import com.example.pj4test.audioInference.HissClassifier
import com.example.pj4test.databinding.FragmentAudioBinding

class AudioFragment: Fragment(), HissClassifier.DetectorListener {
    private val TAG = "AudioFragment"

    private var _fragmentAudioBinding: FragmentAudioBinding? = null

    private val fragmentAudioBinding
        get() = _fragmentAudioBinding!!

    // classifiers
    lateinit var hissClassifier: HissClassifier

    // views
    lateinit var hissView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentAudioBinding = FragmentAudioBinding.inflate(inflater, container, false)

        return fragmentAudioBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hissView = fragmentAudioBinding.HissView

        hissClassifier = HissClassifier()
        hissClassifier.initialize(requireContext())
        hissClassifier.setDetectorListener(this)
    }

    override fun onPause() {
        super.onPause()
        hissClassifier.stopInferencing()
    }

    override fun onResume() {
        super.onResume()
        hissClassifier.startInferencing()
    }

    override fun onResults(score: Float) {
        activity?.runOnUiThread {
            val camFrag = CameraFragment.getInst()
            if (score > HissClassifier.THRESHOLD) {
                hissView.text = "HISS"
                hissView.setBackgroundColor(ProjectConfiguration.activeBackgroundColor)
                hissView.setTextColor(ProjectConfiguration.activeTextColor)
                camFrag.bindCamera()
            } else {
                hissView.text = "NO HISS: $score"
                hissView.setBackgroundColor(ProjectConfiguration.idleBackgroundColor)
                hissView.setTextColor(ProjectConfiguration.idleTextColor)
                camFrag.unbindCamera()
            }
        }
    }
}