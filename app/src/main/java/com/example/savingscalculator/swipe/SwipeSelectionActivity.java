package com.example.savingscalculator.swipe;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.savingscalculator.Plan;
import com.example.savingscalculator.R;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SwipeSelectionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeAdapter adapter;
    ArrayList<Plan> plans = new ArrayList<>();
    Button btnAdd, button;
    int PERMISSION_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_SavingsCalculator);
        setContentView(R.layout.activity_swipe_selection);

        recyclerView = findViewById(R.id.swipe_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new SwipeAdapter(this, plans);
        recyclerView.setAdapter(adapter);

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> promptPlanName());
        File f = new File(this.getFilesDir().getPath() + "createdplans.csv");
        if (f.exists() && !f.isDirectory())     //if file w data exists then proceed
            readPlanFromStorage();              //update activity with any plans previously stored in storage.


        button = findViewById(R.id.button);
        button.setOnClickListener(v -> isStoragePermissionGranted(SwipeSelectionActivity.this));

    }


    @Override
    protected void onPause() {
        super.onPause();
        savePlanToStorage();
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlans();
        savePlanToStorage();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        savePlanToStorage();
        this.finish();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                export();
            } else {
                Toast.makeText(this, "Permission to access storage has been denied. Please enter the app settings and give permission to use this feature.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Checks if the necessary permission to access external storage is granted.
     *
     * @param activity
     */
    private void isStoragePermissionGranted(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            export();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(activity).setTitle("Permission Required")
                    .setMessage("Grant storage permission to store the CSV file on the device")
                    .setPositiveButton("Grant", (dialog, which) -> ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
        }

    }

    /**
     * Creates dialog to enter the desired name for the plan the user is creating.
     */
    private void promptPlanName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        alert.setTitle("New Plan");
        alert.setMessage("Enter the name for your new plan");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setSingleLine(true);
        alert.setView(input);

        alert.setPositiveButton("OK", (dialog, whichButton) -> {                               //If OK is selected a plan is created
            String name = (!input.getText().toString().trim().equals("")) ? String.valueOf(input.getText()) : "New plan"; //if name entered is empty or comprised of whitespaces, plan is named the default "New Plan"
            AddList(name);
        });

        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            dialog.dismiss();// Canceled.
        });

        alert.show();
    }

    /**
     * Creates a new plan with the name provided and sets all values to default.
     * The plan is then added to the plans arrayList to be stored.
     * Lastly the adapter displaying the information in the recyclerview is updated.
     *
     * @param name The name for the new plan
     */
    private void AddList(String name) {
        // TO generate randomd ID use : UUID.randomUUID().toString(),
        Plan plan = new Plan(name, 0, 0, 0, 0, 0);
        plans.add(plan);
        //savePlanToStorage(plans);
        adapter.setPlans(plans);
    }

    /**
     * Reads from the file where the plan data is stored to on device and makes a copy of the file in the Documents folder in external storage.
     * This methods uses the openCSV library.
     */
    private void export() {
        File csvFile = new File(Environment.getExternalStorageDirectory(), "Documents/Saving Plans.csv");

        try (FileOutputStream fos = new FileOutputStream(csvFile)) {

            CSVReader reader = new CSVReader(new FileReader(this.getFilesDir().getPath() + "createdplans.csv"));

            List<String[]> lines = reader.readAll();
            fos.write("Plan Name,Principal Amount,Interest Rate,Inflation Rate,Years,Compounding Period\n".getBytes());
            for (String[] line : lines) {
                for (String token : line) {
                    fos.write(token.getBytes());
                    fos.write(",".getBytes());
                }
                fos.write("\n".getBytes());
            }
            Toast.makeText(this, "File Saved in Documents directory", Toast.LENGTH_SHORT).show();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the ArrayList<Plan> plans in the file "createdplans.csv"
     */
    private void savePlanToStorage() {

        try (ICSVWriter csvWriter = new CSVWriterBuilder(new FileWriter(this.getFilesDir().getPath() + "createdplans.csv"))
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                .build()) {
            for (Plan plan : plans) {
                String[] stringPlan = {plan.getName(),
                        Integer.toString(plan.getPrincipal()),
                        Double.toString(plan.getRate()),
                        Double.toString(plan.getInflation()),
                        Integer.toString(plan.getYears()),
                        Integer.toString(plan.getCompFreq())
                };

                csvWriter.writeNext(stringPlan);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads from the file "createdplans.csv" during "onCreate" and updates the adapter in order to display the plans stored in the file.
     */
    private void readPlanFromStorage() {

        try (CSVReader csvReader = new CSVReader(new FileReader(this.getFilesDir().getPath() + "createdplans.csv"))) {

            String[] tokens;
            while ((tokens = csvReader.readNext()) != null) {
                plans.add(new Plan(tokens[0],
                        Integer.parseInt(tokens[1]),
                        Double.parseDouble(tokens[2]),
                        Double.parseDouble(tokens[3]),
                        Integer.parseInt(tokens[4]),
                        Integer.parseInt(tokens[5]))
                );
            }

            adapter.setPlans(plans);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This update checks if there is an intent sent to the current activity named "PlanDetails"
     * If there is it accesses that information into the plans list and updates the adapter accordingly.
     */
    private void updatePlans() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra("PlanDetails")) {
            plans = (ArrayList<Plan>) getIntent().getSerializableExtra("PlanDetails");
            adapter.setPlans(plans);
        }
    }

}