package com.refapp

import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.refapp.viewmodels.MyViewModel
import com.refapp.csvClasses.CSVReaderTeamsDisc
import com.refapp.csvClasses.ExportHodnotenieToCSV
import com.refapp.databinding.ActivityMainBinding
import com.refapp.db.RefereeDatabase
import com.refapp.entities.Disciplina
import com.refapp.entities.Team
import com.refapp.jsonUtilClasses.JsonParser
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MyViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var db: RefereeDatabase

    private var pathOfFile: String = ""
    private var pathOfJson: String = ""

    lateinit var allJson: JsonParser

    val CSV = 1
    val JSON = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var CSVADDED = false

        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        db = RefereeDatabase.getInstance(this)

        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        if (!root.exists()) {
            root.mkdirs()
        }
        requestPerms()
        permissions()
        setSelectedDisc()
        setSelectedTeam()
        makeNewHodnotenie()

        binding.jsonChooser.isEnabled = false

        viewModel.coroutineScope.launch {
            viewModel.setDropdownTeams(db.teamDao)
            viewModel.setDropdownDiscs(db.discDao)

        }


        binding.testSaveButton.setOnClickListener {

            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Exportovať všetky uložené hodnotenia disciplíny ${viewModel.currSelectedDisciplina} ?")


            alertDialog.setPositiveButton("Áno") { dialog, _ ->
                viewModel.coroutineScope.launch {
                    val ratingsOfDiscip = viewModel.currSelectedDisciplina?.let { it1 ->
                        db.dynamicHodnotenieDAO.getAllDynHodnoteniaByDisc(
                            it1
                        )
                    }

                    val writer = ExportHodnotenieToCSV()
                    if (ratingsOfDiscip != null) {
                        val colsCount = viewModel.currSelectedDisciplina?.let { it1 ->
                            db.discipColsDAO.getColsOfDiscip(
                                it1
                            )
                        }
                        if (colsCount != null) {
                            writer.saveDataToCSV(
                                ratingsOfDiscip,
                                colsCount

                            )
                        }
                    }
                }


                dialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Dáta boli úspešne exportované do priečinka ${pathOfFile}",
                    Toast.LENGTH_LONG
                ).show()
            }
            alertDialog.setNegativeButton("Ešte nie") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()


        }



        binding.reloadDiscip.setOnClickListener {
            setSelectedDisc()
        }

        binding.reloadTeams.setOnClickListener {
            setSelectedTeam()
        }
        binding.csvChooser.setOnClickListener {
            openCSVFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toUri())
        }
        binding.jsonChooser.setOnClickListener {
            openJsonFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toUri())
        }
    }


    fun setSelectedTeam() {
        val teams = viewModel.teamsOfSelectedDiscNames
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_spinner, teams)
        binding.spinner.adapter = arrayAdapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                viewModel.currSelectedTeam = teams[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(applicationContext, "Nie je nic vybrate", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun setSelectedDisc() {

        val disc = viewModel.allDiscNames
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_spinner, disc)
        binding.spinnerDiscip.adapter = arrayAdapter

        binding.spinnerDiscip.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {

                    viewModel.currSelectedDisciplina = disc[position]
                    viewModel.coroutineScope.launch {
                        viewModel.currSelectedDisciplina?.let { it1 ->
                            viewModel.setDropdownTeamsForDisc(
                                db.teamyDisciplinyDAO,
                                it1
                            )
                        }
                    }
                    Thread.sleep(200)
                    binding.reloadTeams.performClick()

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(applicationContext, "Nie je nic vybrate", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }


    fun makeNewHodnotenie() {
        binding.hodnotenieBtn.setOnClickListener {
            if (viewModel.currSelectedTeam != null) {
                val bundle = Bundle()
//                viewModel.currSelectedTeam!!.teamID?.let { it1 -> bundle.putInt("id", it1) }
                bundle.putString("teamname", viewModel.currSelectedTeam)
                bundle.putString("disciplina", viewModel.currSelectedDisciplina)


                val intent = Intent(this, DynamicHodnotenieActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }


    fun requestPerms() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    ),
                    1
                )
            }
        }
    }


    fun openCSVFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"

            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, CSV)
    }

    fun openJsonFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"

            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, JSON)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == CSV
            && resultCode == Activity.RESULT_OK
        ) {
            resultData?.data?.also { uri ->
                val path = uri.lastPathSegment.toString().removePrefix("home:").removePrefix("raw:")
                    .removePrefix("primary:Documents/")
                pathOfFile =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + "/" + path

                val c = CSVReaderTeamsDisc(pathOfFile)

                binding.jsonChooser.isEnabled = true


                viewModel.coroutineScope.launch {

                    if (db.teamyDisciplinyDAO.getAllTeamyDisc().isNotEmpty()) {
                        db.teamyDisciplinyDAO.deleteAllFromTeamydiscipliny()

                    }
                    viewModel.insertTeamsDiscToDB(db.teamyDisciplinyDAO, c.TEAMSDISC)

                    if (db.teamDao.getAllTeams().isNotEmpty()) db.teamDao.deleteAll()
                    if (db.discDao.getAllDiscipliny().isNotEmpty()) db.discDao.deleteAll()

                    for (i in c.TEAMS) {
                        db.teamDao.insertTeam(Team(name = i))
                    }
                    for (i in c.DISCS) {
                        db.discDao.insertDisciplina(Disciplina(name = i))
                    }

                    if (db.blokyDisciplinyDAO.getAllBlokyDiscipliny()
                            .isNotEmpty()
                    ) db.blokyDisciplinyDAO.deleteAll()


                    viewModel.setDropdownTeams(db.teamDao)
                    viewModel.setDropdownDiscs(db.discDao)



                }
            }
        } else if (requestCode == JSON
            && resultCode == Activity.RESULT_OK
        ) {
            resultData?.data?.also { uri ->
                val path = uri.lastPathSegment.toString().removePrefix("home:").removePrefix("raw:")
                    .removePrefix("primary:Documents/")
                pathOfJson =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + "/" + path
                allJson = JsonParser(pathOfJson)
                binding.jsonChooser.isEnabled = false

                viewModel.coroutineScope.launch {

                    allJson.remodelDB(db)

                }


            }
        }
    }


    var storagePermissionsLow = arrayOf(
        WRITE_EXTERNAL_STORAGE,
        READ_EXTERNAL_STORAGE,
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var storagePermissions33 = arrayOf(
        READ_MEDIA_IMAGES,
        READ_MEDIA_AUDIO,
        READ_MEDIA_VIDEO,
    )

    var storagePermissionsBetween = arrayOf(
        MANAGE_EXTERNAL_STORAGE
    )

    fun permissions(): Array<String> {
        val p: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermissions33
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && Build.VERSION_CODES.Q < Build.VERSION.SDK_INT) {
            storagePermissionsBetween
        } else {
            storagePermissionsLow
        }
        return p
    }

}





