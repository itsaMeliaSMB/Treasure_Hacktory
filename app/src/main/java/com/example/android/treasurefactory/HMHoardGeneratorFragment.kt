package com.example.android.treasurefactory

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HMHoardGeneratorFragment : Fragment() {

    /*/ *** Fragment ViewModel ***
    private val hoardGeneratorViewModel: HoardGeneratorViewModel by lazy {
        ViewModelProvider(this).get(HoardGeneratorViewModel::class.java)
    }*/
    //TODO See if it'd be better to use LiveData

    //region [ Property declarations ]

    private lateinit var hoardTitleField: EditText
    private lateinit var letterRadioButton: RadioButton
    private lateinit var specificRadioButton: RadioButton
    private lateinit var viewAnimator: ViewAnimator
    private lateinit var resetButton: Button
    private lateinit var generateButton: Button

    private lateinit var lairCardView: CardView
    private lateinit var lairHeaderGroup: RelativeLayout
    private lateinit var lairIndicator: ImageView
    private lateinit var lairRecyclerView: RecyclerView
    private var lairAdapter: LetterAdapter? = null

    private lateinit var smallCardView: CardView
    private lateinit var smallHeaderGroup: RelativeLayout
    private lateinit var smallIndicator: ImageView
    private lateinit var smallRecyclerView: RecyclerView
    private var smallAdapter: LetterAdapter? = null

    private var lairList = generateLetterArrayList(0)
    private var smallList= generateLetterArrayList(1)

    //endregion

    //region [ Overridden functions ]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.hackmaster_treasure_gen_fragment, container, false)

        // Wire widgets to fragment
        hoardTitleField = view.findViewById(R.id.hackmaster_gen_name_entry) as EditText
        letterRadioButton = view.findViewById(R.id.hackmaster_gen_method_letter) as RadioButton
        specificRadioButton = view.findViewById(R.id.hackmaster_gen_method_specific) as RadioButton
        viewAnimator = view.findViewById(R.id.hackmaster_gen_view_animator) as ViewAnimator

        lairCardView = view.findViewById(R.id.hackmaster_gen_lair_card) as CardView
        lairHeaderGroup = view.findViewById(R.id.hackmaster_gen_lair_header) as RelativeLayout
        lairIndicator = view.findViewById(R.id.hackmaster_gen_lair_indicator) as ImageView
        lairRecyclerView = view.findViewById(R.id.hackmaster_gen_lair_recyclerview) as RecyclerView

        smallCardView = view.findViewById(R.id.hackmaster_gen_small_card) as CardView
        smallHeaderGroup = view.findViewById(R.id.hackmaster_gen_small_header) as RelativeLayout
        smallIndicator = view.findViewById(R.id.hackmaster_gen_small_indicator) as ImageView
        smallRecyclerView = view.findViewById(R.id.hackmaster_gen_small_recyclerview) as RecyclerView

        // Define the letter adapters TODO consider moving to init block
        lairAdapter = LetterAdapter(lairList)
        smallAdapter = LetterAdapter(smallList)

        lairRecyclerView.apply{
            // Set up By-Letter recyclerview
            layoutManager = LinearLayoutManager(context)
            adapter = lairAdapter
            setHasFixedSize(true)
            // TODO Expandibility/Collapsiblity, remove divider when implemented
            visibility = View.GONE //Start off collapsed
        }
        smallRecyclerView.apply{
            // Set up By-Letter recyclerview
            layoutManager = LinearLayoutManager(context)
            adapter = smallAdapter
            setHasFixedSize(true)
            // TODO Expandibility/Collapsiblity, remove divider when implemented
            visibility = View.GONE //Start off collapsed
        }

        resetButton = view.findViewById(R.id.hackmaster_gen_reset_button) as Button
        generateButton = view.findViewById(R.id.hackmaster_gen_generate_button) as Button

        //Set adapter for expandable list view
        //byLetterExpandableList.setAdapter(HMLetterAdapter(requireContext(),byLetterExpandableList,letterGroupList,letterChildList))

        // Return inflated view
        return view
    }

    override fun onStart() {

        super.onStart()

        // Apply widget properties
        letterRadioButton.apply{
            setOnCheckedChangeListener { _, isChecked ->
                Toast.makeText(context,"By-Letter method is ${if (isChecked) "en" else "dis"}abled.",Toast.LENGTH_SHORT).show()
            }
        }

        lairHeaderGroup.setOnClickListener {

            // TODO Move functions outside listener.

            if (lairRecyclerView.visibility == View.VISIBLE) {

                val collapseAnimator = ObjectAnimator.ofFloat(lairIndicator, View.ROTATION, 90f, 0f)

                // Rotate the indicator
                collapseAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(lairHeaderGroup)
                    start() }

                // Hide the recycler view
                lairRecyclerView.visibility = View.GONE
                TransitionManager.beginDelayedTransition(lairCardView,AutoTransition())

            } else {

                val expandAnimator = ObjectAnimator.ofFloat(lairIndicator, View.ROTATION, 0f, 90f)

                // Rotate the indicator
                expandAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(lairHeaderGroup)
                    start() }

                // Reveal the recycler view
                TransitionManager.beginDelayedTransition(lairCardView,AutoTransition())
                lairRecyclerView.visibility = View.VISIBLE

                //TODO automatically collapse other cardview if it is open.
            }
        }

        smallHeaderGroup.setOnClickListener {

            // TODO Move functions outside listener.

            if (smallRecyclerView.visibility == View.VISIBLE) {

                val collapseAnimator = ObjectAnimator.ofFloat(smallIndicator, View.ROTATION, 90f, 0f)

                // Rotate the indicator
                collapseAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(smallHeaderGroup)
                    start() }

                // Hide the recycler view
                smallRecyclerView.visibility = View.GONE
                TransitionManager.beginDelayedTransition(smallCardView,AutoTransition())

            } else {

                val expandAnimator = ObjectAnimator.ofFloat(smallIndicator, View.ROTATION, 0f, 90f)

                // Rotate the indicator
                expandAnimator.apply{
                    duration = 250
                    disableViewDuringAnimation(smallHeaderGroup)
                    start() }

                // Reveal the recycler view
                TransitionManager.beginDelayedTransition(smallCardView,AutoTransition())
                smallRecyclerView.visibility = View.VISIBLE

                //TODO automatically collapse other cardview if it is open.
            }
        }

        resetButton.apply{

            setOnClickListener { resetLetterEntries() }
        }

        generateButton.apply {

            isEnabled = true //TODO only enable button when valid input is available.
            setOnClickListener {
                //Toast.makeText(context,"Generate button clicked.",Toast.LENGTH_SHORT).show()
                var debugString = "DEBUG STRING:\n"
                val debugList = lairAdapter?.getAdapterLetterEntries()

                Toast.makeText(context,"Check debug logs.",Toast.LENGTH_SHORT).show()

                // Return list quantities
                debugList?.forEach {
                    Log.d("Generate Button","Type ${it.letter} x${it.quantity}")
                }
                // TODO retrieve data from adapters. May need to bind adapter lists w/ model
            }
        }
    }

    //endregion

    //region [ Inner classes ]

    private inner class LetterAdapter(var letterEntries: MutableList<HMLetterEntry>)
        : RecyclerView.Adapter<LetterHolder>() {

        var quantities = Array<Int>(letterEntries.size) {0}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterHolder {
            val view = layoutInflater.inflate(R.layout.hackmaster_treasure_gen_letter_list_child,
                parent,false)
            return LetterHolder(view)
        }

        override fun getItemCount() = letterEntries.size

        override fun onBindViewHolder(holder: LetterHolder, position: Int) { //TODO consider replacing position calls with holder.adapterPosition

            // Get the model to reference
            val entry = letterEntries[holder.adapterPosition]

            // Prepare literals
            val typeString = "Type ${entry.letter}"
            val oddsToast = letterEntries[holder.adapterPosition].odds

            // Bind items TODO move function to viewHolder per
            holder.typeLabel.text = typeString
            holder.quantityText.text = quantities[holder.adapterPosition].toString()

            // Attach listeners
            holder.infoDot.setOnClickListener {

                Toast.makeText(context,oddsToast,Toast.LENGTH_LONG)
                    .show()
            }
            holder.plusButton.setOnClickListener {

                quantities[holder.adapterPosition] = correctCount(quantities[holder.adapterPosition],1)
                letterEntries[holder.adapterPosition].quantity = quantities[holder.adapterPosition]

                notifyItemChanged(holder.adapterPosition)
            }
            holder.minusButton.setOnClickListener {

                quantities[holder.adapterPosition] = correctCount(quantities[holder.adapterPosition],-1)
                letterEntries[holder.adapterPosition].quantity = quantities[holder.adapterPosition]

                notifyItemChanged(position)
            }
        }

        fun getAdapterLetterEntries() : List<HMLetterEntry> = letterEntries

        fun zeroAdapterLetterEntries() {

            letterEntries.forEach { entry -> entry.quantity = 0 }
            notifyDataSetChanged()
        }
    }

    private inner class LetterHolder(view:View): RecyclerView.ViewHolder(view) {

        private lateinit var letterEntry: HMLetterEntry

        var infoDot     = view.findViewById(R.id.treasure_type_info) as ImageView
        var typeLabel   = view.findViewById(R.id.treasure_type_label) as TextView
        var minusButton = view.findViewById(R.id.treasure_type_decrement_button) as Button
        var quantityText= view.findViewById(R.id.treasure_type_counter) as TextView
        var plusButton  = view.findViewById(R.id.treasure_type_increment_button) as Button

    }

    //endregion

    //region [ Helper functions ]

    private fun updateUI() {


    }

    fun generateLetterArrayList(parentIndex: Int) : ArrayList<HMLetterEntry> {

        val list = ArrayList<HMLetterEntry>()

        oddsTable[parentIndex].forEach{ (key, value) ->
            list.add(HMLetterEntry( key, returnOddsString(value), 0))}

        return list
    }

    fun correctCount(initCount : Int, increment: Int = 0,
                     minCount: Int = 0, _maxCount: Int = 1000) : Int {

        val maxCount = if (_maxCount <= minCount) minCount + 1 else _maxCount

        var newCount = initCount + increment

        if (newCount < minCount) newCount = minCount
        if (newCount > maxCount) newCount = maxCount

        return newCount
    }

    private fun resetLetterEntries() {
        lairAdapter?.zeroAdapterLetterEntries()
        smallAdapter?.zeroAdapterLetterEntries()
        Toast.makeText(context,"Letter quantities reset.",Toast.LENGTH_SHORT).show()
    }

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {

        // This extension method listens for start/end events on an animation and disables
        // the given view for the entirety of that animation.
        // Taken from https://developer.android.com/codelabs/advanced-android-kotlin-training-property-animation

        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }

    //endregion

    companion object {

        val oddsTable = listOf(
            // Lair treasure types
            mapOf(
                "A" to arrayOf(
                    intArrayOf(25,1000,3000),
                    intArrayOf(30,200,2000),
                    intArrayOf(35,500,3000),
                    intArrayOf(40,1000,6000),
                    intArrayOf(35,500,3000),
                    intArrayOf(35,300,1800),
                    intArrayOf(60,10,40),
                    intArrayOf(50,2,12),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(30,3,3) ),
                "B" to arrayOf(
                    intArrayOf(50,1000,6000),
                    intArrayOf(25,1000,3000),
                    intArrayOf(25,300,1800),
                    intArrayOf(25,200,2000),
                    intArrayOf(25,150,1500),
                    intArrayOf(25,100,1000),
                    intArrayOf(30,1,8),
                    intArrayOf(20,1,4),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(10,1,1),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "C" to arrayOf(
                    intArrayOf(20,1000,10000),
                    intArrayOf(30,1000,6000),
                    intArrayOf(40,1000,3000),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(10,100,600),
                    intArrayOf(25,1,6),
                    intArrayOf(20,1,3),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(10,2,2) ),
                "D" to arrayOf(
                    intArrayOf(10,1000,6000),
                    intArrayOf(15,1000,10000),
                    intArrayOf(25,1000,12000),
                    intArrayOf(50,1000,3000),
                    intArrayOf(0,0,0),
                    intArrayOf(15,100,600),
                    intArrayOf(30,1,10),
                    intArrayOf(25,1,6),
                    intArrayOf(15,1,1),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(15,2,2) ),
                "E" to arrayOf(
                    intArrayOf(5,1000,6000),
                    intArrayOf(25,1000,10000),
                    intArrayOf(45,1000,12000),
                    intArrayOf(25,1000,4000),
                    intArrayOf(15,100,1200),
                    intArrayOf(25,300,1800),
                    intArrayOf(15,1,12),
                    intArrayOf(10,1,6),
                    intArrayOf(0,0,0),
                    intArrayOf(25,1,1),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(25,3,3) ),
                "F" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(10,3000,18000),
                    intArrayOf(25,2000,12000),
                    intArrayOf(40,1000,6000),
                    intArrayOf(30,500,5000),
                    intArrayOf(15,1000,4000),
                    intArrayOf(20,2,20),
                    intArrayOf(10,1,8),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(30,5,5),
                    intArrayOf(0,0,0) ),
                "G" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(15,3000,24000),
                    intArrayOf(50,2000,20000),
                    intArrayOf(50,1500,15000),
                    intArrayOf(50,1000,10000),
                    intArrayOf(30,3,18),
                    intArrayOf(25,1,6),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(35,5,5) ),
                "H" to arrayOf(
                    intArrayOf(25,3000,18000),
                    intArrayOf(35,2000,20000),
                    intArrayOf(45,2000,20000),
                    intArrayOf(55,2000,20000),
                    intArrayOf(45,2000,20000),
                    intArrayOf(35,1000,8000),
                    intArrayOf(50,3,30),
                    intArrayOf(50,2,20),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(15,6,6) ),
                "I" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(15,100,400),
                    intArrayOf(30,100,600),
                    intArrayOf(55,2,12),
                    intArrayOf(50,2,8),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(15,1,1) )
            ),
            // Individual and small lair treasure types
            mapOf(

                "J" to arrayOf(
                    intArrayOf(100,3,24),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "K" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(100,3,18),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "L" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,3,18),
                    intArrayOf(100,2,12),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "M" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,3,12),
                    intArrayOf(100,2,8),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "N" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,1,6),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "O" to arrayOf(
                    intArrayOf(100,10,40),
                    intArrayOf(100,10,30),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "P" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(100,10,60),
                    intArrayOf(100,3,30),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,1,20),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "Q" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,1,4),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "R" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,2,20),
                    intArrayOf(0,0,0),
                    intArrayOf(100,10,60),
                    intArrayOf(100,2,8),
                    intArrayOf(100,1,3),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "S" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,1,8),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "T" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,1,4),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "U" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(90,2,16),
                    intArrayOf(80,1,6),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(70,1,1) ),
                "V" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,2,2) ),
                "W" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,4,24),
                    intArrayOf(100,5,30),
                    intArrayOf(100,2,16),
                    intArrayOf(100,1,8),
                    intArrayOf(60,2,16),
                    intArrayOf(50,1,8),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(60,2,2) ),
                "X" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,2,2),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "Y" to arrayOf(
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(100,200,1200),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0) ),
                "Z" to arrayOf(
                    intArrayOf(100,100,300),
                    intArrayOf(100,100,400),
                    intArrayOf(100,100,500),
                    intArrayOf(100,100,600),
                    intArrayOf(100,100,500),
                    intArrayOf(100,100,500),
                    intArrayOf(55,1,6),
                    intArrayOf(50,2,12),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(0,0,0),
                    intArrayOf(50,3,3) )
            )
        )

        fun newInstance(): HMHoardGeneratorFragment {
            return HMHoardGeneratorFragment()
        }

        private val treasureLabels = listOf<String>(
            "copper piece(s)",
            "silver piece(s)",
            "electrum piece(s)",
            "gold piece(s)",
            "hard silver piece(s)",
            "platinum piece(s)",
            "gem(s)",
            "art object(s)",
            "potion(s)/oil(s)",
            "scroll(s)",
            "magic weapon(s)/armor",
            "non-weapon magic item(s)",
            "magic item(s)")

        private fun returnOddsString(odds: Array<IntArray>) : String {

            val oddsList = mutableListOf<String>()
            var oddsLineBuilder = StringBuilder()

            odds.forEachIndexed { index, oddsArray ->

                oddsLineBuilder.clear()

                if (oddsArray[0] != 0) {

                    oddsLineBuilder.append("${oddsArray[0]}% odds of: ")
                        .append(if (oddsArray[1] == oddsArray[2]) {
                                oddsArray[1].toString()
                            } else {
                                "${oddsArray[1]}-${oddsArray[2]}" })
                        .append(" ${treasureLabels[index]}")
                }

                if (oddsLineBuilder.isNotEmpty()) oddsList.add(oddsLineBuilder.toString())
            }

            var result: String = oddsList[0]

            if (oddsList.size > 1) {

                for( index in 1 until oddsList.size) {
                    result += "\n${oddsList[index]}"
                }
            }

            return result
        }
    }
}

//TODO https://stackoverflow.com/questions/52070524/edittext-and-textview-inside-recyclerview-android-kotlin-scrolling-issue
//TODO https://stackoverflow.com/a/61513452
//TODO https://stackoverflow.com/a/54842942