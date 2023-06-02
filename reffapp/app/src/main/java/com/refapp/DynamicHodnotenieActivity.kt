package com.refapp

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.refapp.uiCustomViews.*
import com.refapp.viewmodels.DynamicHodnotenieViewModel
import com.refapp.databinding.ActivityDynamicHodnotenieBinding
import com.refapp.db.RefereeDatabase
import com.refapp.entities.BlokyDiscipliny
import com.refapp.entities.DynamicHodnotenie
import kotlinx.coroutines.*

class DynamicHodnotenieActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDynamicHodnotenieBinding
    private lateinit var dynHVM: DynamicHodnotenieViewModel
    private lateinit var db: RefereeDatabase
    private var blokyViews = mutableListOf<AbstractView>()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityDynamicHodnotenieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = RefereeDatabase.getInstance(this)

        dynHVM = ViewModelProvider(this)[DynamicHodnotenieViewModel::class.java]

        val bundle = intent.extras
        if (bundle != null) {
            dynHVM.currTeamName = bundle.getString("teamname").toString()
            dynHVM.currDisciplinaName = bundle.getString("disciplina").toString()

        }

        GlobalScope.launch(Dispatchers.IO) {
            val generateBlocks = db.blokyDisciplinyDAO.getBlocksOfDisc(dynHVM.currDisciplinaName)
            withContext(Dispatchers.Main) {
                generate(generateBlocks)
            }
        }


    }

    @SuppressLint("SetTextI18n")
    fun generate(generateBlocks: List<BlokyDiscipliny>) {

        val title = TextView(this)
        title.setText("Hodnotenie teamu ${dynHVM.currTeamName} pre disciplínu ${dynHVM.currDisciplinaName}")
        title.gravity = Gravity.CENTER
        title.setTextSize(25f)
        title.setTypeface(null,Typeface.BOLD)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(35, 0, 35, 100) // Set bottom margin of 16 pixels

        title.layoutParams = layoutParams



        binding.dynamicLayout.addView(title)

        if (generateBlocks.isEmpty()){
            val emptyText = TextView(this)
            emptyText.text = "Zatiaľ tejto kategórii nebol zadefinovaný spôsob hodnotenia :( " +
                    "Prosím pridajte do konfiguračného súboru potrebné informácie o tejto disciplíne !"
            emptyText.gravity = Gravity.CENTER
            emptyText.textSize = 20f


            val emptyTextLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            emptyTextLayoutParams.setMargins(35, 0, 35, 0) // Set bottom margin of 16 pixels

            emptyText.layoutParams = emptyTextLayoutParams



            binding.dynamicLayout.addView(emptyText)
        }

        for (i in generateBlocks) {

            when (i.blokType) {
                "plusminus" -> {
                    val r = PlusMinus(this@DynamicHodnotenieActivity)
                    r.label = i.blokName
                    r.setSize()
                    binding.dynamicLayout.addView(r)
                    blokyViews.add(r)
                }
                "trimoznost" -> {
                    val r = ThreeOption(this@DynamicHodnotenieActivity)
                    r.label = i.blokName
                    r.setSize()
                    binding.dynamicLayout.addView(r)
                    blokyViews.add(r)
                }
                "numinput" -> {
                    val r = CustomIntInput(this@DynamicHodnotenieActivity)
                    r.label = i.blokName
                    r.setSize()
                    binding.dynamicLayout.addView(r)
                    blokyViews.add(r)
                }
                "checkbox" -> {
                    val r = CheckBoxCustom(this@DynamicHodnotenieActivity)
                    r.label = i.blokName
                    r.setSize()
                    binding.dynamicLayout.addView(r)
                    blokyViews.add(r)
                }
            }

        }


        val exitButton = Button(this)
        val saveButton = Button(this)

        setupExitButton(exitButton)
        setupSaveButton(saveButton)

        val buttons = LinearLayout(this)
        buttons.orientation = LinearLayout.HORIZONTAL
        buttons.addView(exitButton)
        buttons.addView(saveButton)
        buttons.gravity = Gravity.CENTER
        binding.dynamicLayout.addView(buttons)

    }

    private fun setupSaveButton(saveButton : Button) {

        saveButton.text = "Uložiť hodnotenie"

        val saveButtonLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
//        saveButton.setBackgroundColor(Color.parseColor("#6200EE"))
//        saveButton.setTextColor(Color.WHITE);
        saveButtonLayoutParams.setMargins(35, 0, 0, 0) // Set bottom margin of 16 pixel
        saveButton.layoutParams = saveButtonLayoutParams
        saveButton.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Ste si istý že chcete hodnotenie uložiť ?")
                .setCancelable(false)
                .setPositiveButton("Áno") { _, _ ->
                    dynHVM.coroutineScope.launch {
                        for (i in blokyViews) {
                            db.dynamicHodnotenieDAO.insertDynHod(
                                DynamicHodnotenie(
                                    blockLabel = i.label,
                                    blockScore = i.currVal,
                                    teamName = dynHVM.currTeamName,
                                    discName = dynHVM.currDisciplinaName
                                )
                            )
                        }
                    }
                    Toast.makeText(applicationContext,"Hodnotenie úspešne uložené !", Toast.LENGTH_LONG).show()
                    finish()
                }
                .setNegativeButton("Nie") { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()



        }
    }

    private fun setupExitButton(exitButton : Button) {

//        exitButton.setBackgroundColor(Color.parseColor("#6200EE"))
//        exitButton.setTextColor(Color.WHITE);
        exitButton.text = "Späť"

        val exitButtonLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        exitButtonLayoutParams.setMargins(0, 0, 35, 0) // Set bottom margin of 16 pixels
        exitButton.layoutParams = exitButtonLayoutParams

        exitButton.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Späť do menu ?")
                .setCancelable(false)
                .setPositiveButton("Áno") { _, _ ->
                    finish()
                }
                .setNegativeButton("Nie") { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

}


