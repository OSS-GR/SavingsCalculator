package com.example.savingscalculator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.savingscalculator.swipe.SwipeSelectionActivity;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    // Declaring Widgets
    TextView editHeader, txtPrincipal, txtRate, txtYears, txtCompPer, txtResult, txtInflation;
    EditText editPrincipal, editRate, editYears, editCompFreq, editInflation;
    Button btnCalculate;
    ArrayList<Plan> plans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Instantiating Widgets
        editHeader = findViewById(R.id.editHeader);
        txtPrincipal = findViewById(R.id.txtPrincipal);
        txtRate = findViewById(R.id.txtRate);
        txtYears = findViewById(R.id.txtYears);
        txtCompPer = findViewById(R.id.txtCompPer);
        txtResult = findViewById(R.id.txtResult);
        txtInflation = findViewById(R.id.txtInflation);

        editPrincipal = findViewById(R.id.editPrincipal);
        editRate = findViewById(R.id.editRate);
        editYears = findViewById(R.id.editYears);
        editCompFreq = findViewById(R.id.editCompFreq);
        editInflation = findViewById(R.id.editInflation);

        // Deserialize ArrayList<Plan>
        plans = (ArrayList<Plan>) getIntent().getSerializableExtra("PlanDetails");
        Plan plan = plans.get((int) getIntent().getSerializableExtra("PlanKey"));

        // Fill in EditTexts with Saved Plan Data
        editHeader.setText(plan.getName());
        editPrincipal.setText(Integer.toString(plan.getPrincipal()));
        editRate.setText(Double.toString(plan.getRate()));
        if (plan.getInflation() != 0) {
            editInflation.setText(Double.toString(plan.getInflation()));        //if inflation value is equal to 0 then nothing is displayed in order to show the user that the field is optional.
        }
        editYears.setText(Integer.toString((plan.getYears())));
        editCompFreq.setText(Integer.toString(plan.getCompFreq()));


        btnCalculate = findViewById(R.id.btnCalculate);


        editCompFreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Enter \'0\' for Continuous Compounding", Toast.LENGTH_SHORT).show(); // Presents informational text regarding Compounding Frequency
            }
        });


        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCapitalAmount(v);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        plans = updatePlansArrayList();
        savePlanToStorage();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        plans = updatePlansArrayList();
        Intent backIntent = new Intent(this, SwipeSelectionActivity.class);
        backIntent.putExtra("PlanDetails", plans);              //store data in intent to be accessed in the swipeselection activity, in case information like the plan name has changed.
        startActivity(backIntent);
    }

    /**
     * This makes sure the data in the plans array is the latest
     * This is used when onBackPressed or onPause calls are made to ensure the data is maintained.
     *
     * @return
     */
    private ArrayList<Plan> updatePlansArrayList() {
        plans = (ArrayList<Plan>) getIntent().getSerializableExtra("PlanDetails");
        Plan plan = plans.get((int) getIntent().getSerializableExtra("PlanKey"));

        plan.setName(editHeader.getText().toString());
        plan.setPrincipal(Integer.parseInt(editPrincipal.getText().toString()));
        plan.setRate(Double.parseDouble(editRate.getText().toString()));

        if (!(editInflation.getText().toString().equals(""))) {
            plan.setInflation(Double.parseDouble(editInflation.getText().toString()));
        } else
            plan.setInflation((int) 0);

        plan.setYears(Integer.parseInt(editYears.getText().toString()));
        plan.setCompFreq(Integer.parseInt(editCompFreq.getText().toString()));
        return plans;
    }

    /**
     * Calculates an approximation of your current account balance if placed in a savings plan.
     * If the user chooses to provide an inflation rate, the final value of the balance will be adjusted accordingly.
     *
     * @param v
     */
    private void calculateCapitalAmount(View v) {


        MathContext mc = MathContext.DECIMAL32; //Set precision for the rounding of the following calculations

        //Check if the necessary fields to perform a calculation are filled
        if (editPrincipal.getText().toString().equals("0") || editPrincipal.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter an initial amount", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editRate.getText().toString().equals("0.0") || editRate.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter the desired interest rate", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editYears.getText().toString().equals("0") || editYears.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter the desired number of years", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editCompFreq.getText().toString().equals("")) {        // 0 is used to signify continuous compounding so we don't check for that.
            Toast.makeText(this, "Please enter the desired Compounding Period", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal Principal = new BigDecimal(editPrincipal.getText().toString(), mc);
        BigDecimal Rate = new BigDecimal(editRate.getText().toString(),mc);
        BigDecimal Inflation = null;
        if (!(editInflation.getText().toString().equals(""))) {
            Inflation = new BigDecimal(editInflation.getText().toString(), mc);
        } else {
            Inflation = BigDecimal.ZERO;
        }
        BigDecimal Years = new BigDecimal(editYears.getText().toString());
        BigDecimal CompFreq = new BigDecimal(editCompFreq.getText().toString());


        if (CompFreq.toString().equals("0")) {
            // Continuously Compounded Interest Formula A = P*e^(r*t)), where A is the Capital amount
            // Inflation Adjusted Interest Rate (r-i)/(i+1)

            Rate = Rate.subtract(Inflation, mc);
            Inflation = Inflation.add(BigDecimal.ONE, mc);
            Rate = Rate.divide(Inflation, mc);
            BigDecimal rateTimesYears = Rate.multiply(Years, mc);
            BigDecimal expToRateTimeYears = new BigDecimal(Math.pow(Math.E, rateTimesYears.doubleValue()), mc);

            BigDecimal capital = Principal.multiply(expToRateTimeYears, mc);
            txtResult.setText(String.format(Locale.ENGLISH,"%s%s","Your savings will be: $",capital.toPlainString()));
        } else {
            // Discreetly Compounded Interest Formula A = P*(1+(r/n))^(t*n), where A is the Capital amount
            Rate = Rate.subtract(Inflation, mc).divide(Inflation.add(BigDecimal.ONE, mc), mc);
            BigDecimal rateByCompFreq = Rate.divide(CompFreq, mc);
            BigDecimal onePlusRateByCompFreq = BigDecimal.ONE.add(rateByCompFreq, mc);
            int exponentYearTimesCompFreq = Years.multiply(CompFreq, mc).intValue();
            BigDecimal inParentheses = onePlusRateByCompFreq.pow(exponentYearTimesCompFreq, mc);

            BigDecimal capital = Principal.multiply(inParentheses, mc);
            txtResult.setText(String.format(Locale.ENGLISH,"%s%s","Your savings will be: $",capital.toPlainString()));
        }

    }

    /**
     * Saves the ArrayList<Plan> plans in the file "createdplans.csv"
     */
    private void savePlanToStorage() {
        Log.d("MainActivity: ", "Saving...");
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
                Log.d("MainActivity: ", "Saving Completed Successfully");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
