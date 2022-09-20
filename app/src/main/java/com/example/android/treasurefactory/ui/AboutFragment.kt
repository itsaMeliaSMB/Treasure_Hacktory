package com.example.android.treasurefactory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.treasurefactory.databinding.LayoutAboutPageBinding

class AboutFragment : Fragment() {

    //region [ Property declarations ]

    private var shortAnimationDuration: Int = 0

    private var _binding: LayoutAboutPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    //endregion

    //region [ Overridden functions ]

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = LayoutAboutPageBinding.inflate(inflater, container, false)
        val view = binding.root

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        // Return inflated view
        return view
    }

    override fun onStart() {
        super.onStart()

        binding.apply {

            //region ( Apply card footers' OnClickListeners )

            kenzercoCardFooter.setOnClickListener {

                if (binding.kenzercoCardExpandedGroup.visibility == View.VISIBLE) {

                    val collapseAnimator = ObjectAnimator.ofFloat(binding.kenzercoCardIndicator, View.ROTATION, -90f, 90f)

                    // Rotate the indicator
                    collapseAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.kenzercoCardFooter)
                        start() }

                    // Hide the links
                    TransitionManager.beginDelayedTransition(binding.kenzercoCardExpandedGroup, AutoTransition())
                    binding.kenzercoCardExpandedGroup.visibility = View.GONE

                } else {

                    val expandAnimator = ObjectAnimator.ofFloat(binding.kenzercoCardIndicator, View.ROTATION, 90f, -90f)

                    // Rotate the indicator
                    expandAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.kenzercoCardFooter)
                        start() }

                    // Show the links
                    TransitionManager.beginDelayedTransition(binding.kenzercoCard, AutoTransition())
                    binding.kenzercoCardExpandedGroup.visibility = View.VISIBLE
                }
            }

            wotcCardFooter.setOnClickListener {

                if (binding.wotcCardExpandedGroup.visibility == View.VISIBLE) {

                    val collapseAnimator = ObjectAnimator.ofFloat(binding.wotcCardIndicator, View.ROTATION, 90f, -90f)

                    // Rotate the indicator
                    collapseAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.wotcCardFooter)
                        start() }

                    // Hide the links
                    TransitionManager.beginDelayedTransition(binding.wotcCard, AutoTransition())
                    binding.wotcCardExpandedGroup.visibility = View.GONE

                } else {

                    val expandAnimator = ObjectAnimator.ofFloat(binding.wotcCardIndicator, View.ROTATION, -90f, 90f)

                    // Rotate the indicator
                    expandAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.wotcCardFooter)
                        start() }

                    // Show the links
                    TransitionManager.beginDelayedTransition(binding.wotcCard, AutoTransition())
                    binding.wotcCardExpandedGroup.visibility = View.VISIBLE
                }
            }

            fraimCardFooter.setOnClickListener {

                if (binding.fraimCardExpandedGroup.visibility == View.VISIBLE) {

                    val collapseAnimator = ObjectAnimator.ofFloat(binding.fraimCardIndicator, View.ROTATION, 90f, -90f)

                    // Rotate the indicator
                    collapseAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.fraimCardFooter)
                        start() }

                    // Hide the links
                    TransitionManager.beginDelayedTransition(binding.fraimCard, AutoTransition())
                    binding.fraimCardExpandedGroup.visibility = View.GONE

                } else {

                    val expandAnimator = ObjectAnimator.ofFloat(binding.fraimCardIndicator, View.ROTATION, -90f, 90f)

                    // Rotate the indicator
                    expandAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.fraimCardFooter)
                        start() }

                    // Show the links
                    TransitionManager.beginDelayedTransition(binding.fraimCard, AutoTransition())
                    binding.fraimCardExpandedGroup.visibility = View.VISIBLE
                }
            }

            ponetiCardFooter.setOnClickListener {

                if (binding.ponetiCardExpandedGroup.visibility == View.VISIBLE) {

                    val collapseAnimator = ObjectAnimator.ofFloat(binding.ponetiCardIndicator, View.ROTATION, 90f, -90f)

                    // Rotate the indicator
                    collapseAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.ponetiCardFooter)
                        start() }

                    // Hide the links
                    TransitionManager.beginDelayedTransition(binding.ponetiCard, AutoTransition())
                    binding.ponetiCardExpandedGroup.visibility = View.GONE

                } else {

                    val expandAnimator = ObjectAnimator.ofFloat(binding.ponetiCardIndicator, View.ROTATION, -90f, 90f)

                    // Rotate the indicator
                    expandAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.ponetiCardFooter)
                        start() }

                    // Show the links
                    TransitionManager.beginDelayedTransition(binding.ponetiCard, AutoTransition())
                    binding.ponetiCardExpandedGroup.visibility = View.VISIBLE
                }
            }

            akizaCardFooter.setOnClickListener {

                if (binding.akizaCardExpandedGroup.visibility == View.VISIBLE) {

                    val collapseAnimator = ObjectAnimator.ofFloat(binding.akizaCardIndicator, View.ROTATION, 90f, -90f)

                    // Rotate the indicator
                    collapseAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.akizaCardFooter)
                        start() }

                    // Hide the links
                    TransitionManager.beginDelayedTransition(binding.akizaCard, AutoTransition())
                    binding.akizaCardExpandedGroup.visibility = View.GONE

                } else {

                    val expandAnimator = ObjectAnimator.ofFloat(binding.akizaCardIndicator, View.ROTATION, -90f, 90f)

                    // Rotate the indicator
                    expandAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.akizaCardFooter)
                        start() }

                    // Show the links
                    TransitionManager.beginDelayedTransition(binding.akizaCard, AutoTransition())
                    binding.akizaCardExpandedGroup.visibility = View.VISIBLE
                }
            }

            gameIconsCardFooter.setOnClickListener {

                if (binding.gameIconsCardExpandedGroup.visibility == View.VISIBLE) {

                    val collapseAnimator = ObjectAnimator.ofFloat(binding.gameIconsCardIndicator, View.ROTATION, 90f, -90f)

                    // Rotate the indicator
                    collapseAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.gameIconsCardFooter)
                        start() }

                    // Hide the links
                    TransitionManager.beginDelayedTransition(binding.gameIconsCard, AutoTransition())
                    binding.gameIconsCardExpandedGroup.visibility = View.GONE

                } else {

                    val expandAnimator = ObjectAnimator.ofFloat(binding.gameIconsCardIndicator, View.ROTATION, -90f, 90f)

                    // Rotate the indicator
                    expandAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.gameIconsCardFooter)
                        start() }

                    // Show the links
                    TransitionManager.beginDelayedTransition(binding.gameIconsCard, AutoTransition())
                    binding.gameIconsCardExpandedGroup.visibility = View.VISIBLE
                }
            }

            iconscoutCardFooter.setOnClickListener {

                if (binding.iconscoutCardExpandedGroup.visibility == View.VISIBLE) {

                    val collapseAnimator = ObjectAnimator.ofFloat(binding.iconscoutCardIndicator, View.ROTATION, 90f, -90f)

                    // Rotate the indicator
                    collapseAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.iconscoutCardFooter)
                        start() }

                    // Hide the links
                    TransitionManager.beginDelayedTransition(binding.iconscoutCard, AutoTransition())
                    binding.iconscoutCardExpandedGroup.visibility = View.GONE

                } else {

                    val expandAnimator = ObjectAnimator.ofFloat(binding.iconscoutCardIndicator, View.ROTATION, -90f, 90f)

                    // Rotate the indicator
                    expandAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.iconscoutCardFooter)
                        start() }

                    // Show the links
                    TransitionManager.beginDelayedTransition(binding.iconscoutCard, AutoTransition())
                    binding.iconscoutCardExpandedGroup.visibility = View.VISIBLE
                }
            }

            materialIconsCardFooter.setOnClickListener {

                if (binding.materialIconsCardExpandedGroup.visibility == View.VISIBLE) {

                    val collapseAnimator = ObjectAnimator.ofFloat(binding.materialIconsCardIndicator, View.ROTATION, 90f, -90f)

                    // Rotate the indicator
                    collapseAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.materialIconsCardFooter)
                        start() }

                    // Hide the links
                    TransitionManager.beginDelayedTransition(binding.materialIconsCard, AutoTransition())
                    binding.materialIconsCardExpandedGroup.visibility = View.GONE

                } else {

                    val expandAnimator = ObjectAnimator.ofFloat(binding.materialIconsCardIndicator, View.ROTATION, -90f, 90f)

                    // Rotate the indicator
                    expandAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.materialIconsCardFooter)
                        start() }

                    // Show the links
                    TransitionManager.beginDelayedTransition(binding.materialIconsCard, AutoTransition())
                    binding.materialIconsCardExpandedGroup.visibility = View.VISIBLE
                }
            }

            puzwedCardFooter.setOnClickListener {

                if (binding.puzwedCardExpandedGroup.visibility == View.VISIBLE) {

                    val collapseAnimator = ObjectAnimator.ofFloat(binding.puzwedCardIndicator, View.ROTATION, 90f, -90f)

                    // Rotate the indicator
                    collapseAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.puzwedCardFooter)
                        start() }

                    // Hide the links
                    TransitionManager.beginDelayedTransition(binding.puzwedCard, AutoTransition())
                    binding.puzwedCardExpandedGroup.visibility = View.GONE

                } else {

                    val expandAnimator = ObjectAnimator.ofFloat(binding.puzwedCardIndicator, View.ROTATION, -90f, 90f)

                    // Rotate the indicator
                    expandAnimator.apply{
                        duration = 100
                        disableViewDuringAnimation(binding.puzwedCardFooter)
                        start() }

                    // Show the links
                    TransitionManager.beginDelayedTransition(binding.puzwedCard, AutoTransition())
                    binding.puzwedCardExpandedGroup.visibility = View.VISIBLE
                }
            }

            //endregion

            // region ( Make links clickable )
            kenzercoMainLink.movementMethod = LinkMovementMethod.getInstance()
            kenzercoHackmasterLink.movementMethod = LinkMovementMethod.getInstance()
            wotcPolicyLink.movementMethod = LinkMovementMethod.getInstance()
            wotcDndLink.movementMethod = LinkMovementMethod.getInstance()
            fraimMainLink.movementMethod = LinkMovementMethod.getInstance()
            fraimInstagramLink.movementMethod = LinkMovementMethod.getInstance()
            ponetiLink.movementMethod = LinkMovementMethod.getInstance()
            akizaLink.movementMethod = LinkMovementMethod.getInstance()
            gameIconsSiteLink.movementMethod = LinkMovementMethod.getInstance()
            gameIconsSiteLink.movementMethod = LinkMovementMethod.getInstance()
            iconscoutLicenseLink.movementMethod = LinkMovementMethod.getInstance()
            iconscoutCreatorLink.movementMethod = LinkMovementMethod.getInstance()
            iconscoutOriginalLink.movementMethod = LinkMovementMethod.getInstance()
            materialIconsSiteLink.movementMethod = LinkMovementMethod.getInstance()
            materialIconsApacheLink.movementMethod = LinkMovementMethod.getInstance()
            puzwedDiscordLink.movementMethod = LinkMovementMethod.getInstance()
            puzwedChallongeLink.movementMethod = LinkMovementMethod.getInstance()
            puzwedTwitterLink.movementMethod = LinkMovementMethod.getInstance()
            puzwedYoutubeLink.movementMethod = LinkMovementMethod.getInstance()
            //endregion
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //endregion

    //region [ Helper functions ]

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {

        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = (view.visibility == View.VISIBLE)
            }
        })
    }

    private fun ObjectAnimator.setGoneAfterAnimation(view: View) {

        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.visibility = View.GONE
            }
        })
    }

    //endregion

    companion object {

        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}