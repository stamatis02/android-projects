package com.example.ergasia1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //nitialize our views
TextView summoney, day,mikrotero, megalitero;
EditText inputmoney;
Button savebutton , displaybutt ;
String moneyspend;

SharedPreferences sp,sp2,sp3;// we create 3 different shared preferences to manage diferente type of data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sp = getSharedPreferences("total_expenses", MODE_PRIVATE);
        sp2 = getSharedPreferences("dates",MODE_PRIVATE);
        sp3 = getSharedPreferences("weeks",MODE_PRIVATE);
        displaybutt= findViewById(R.id.display);
        mikrotero = findViewById(R.id.textView2);
        megalitero=findViewById(R.id.textView3);
        savebutton = findViewById(R.id.savebutton1);
        inputmoney = findViewById(R.id.inputmoney1);
        summoney= findViewById(R.id.summoney1);
        day= findViewById(R.id.textView);
        Calendar cal = Calendar.getInstance(); // we get this instance to know to handle the data of the weeks
        int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

               moneyspend = inputmoney.getText().toString();//we take the input from the user

               if (moneyspend.isEmpty()){
                   Toast.makeText(MainActivity.this, "bale ena posoo",Toast.LENGTH_LONG).show();
               return;}
                //initializing editors
                SharedPreferences.Editor  editor = sp.edit();
                SharedPreferences.Editor  editor2 = sp2.edit();
                int dayCount = sp.getInt("day_count", 0);
                dayCount++;//counter cause the user might use the app from the middle of tha week
                editor.putInt("day_count", dayCount);
                editor.apply();

                // Αν δεν υπάρχει αποθηκευμένη εβδομάδα, βάλε την τωρινή για αρχή
                int lastWeek = sp3.getInt("last_week", currentWeek);

                // Αν η εβδομάδα είναι διαφορετική, σημαίνει ότι τελείωσε η προηγούμενη
                if (currentWeek != lastWeek) {
                    editor.putInt("day_count",0);
                 float sinolo = sp.getFloat("total_expenses",0f);
                 float mo = (sinolo / dayCount);
                 editor.putFloat("mo",mo);
                 editor.apply();

                    displaybutt.setVisibility(View.VISIBLE);//if the week is different we make the button of the dispaly info visibly, default its gone
                } else {
                    displaybutt.setVisibility(View.GONE);
                }
                String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())//excactl date for the minimun and maximun spend in a day
                        .format(new Date());
                day.setText(dateStr);
               float dayamount = Float.parseFloat(moneyspend);//kanoyme float ton string
               float currentTotal = sp.getFloat("total_expenses",0f);//gia to sinolo
                   float day2 = sp.getFloat("lessamount",Float.MAX_VALUE);
                   float day1 = sp.getFloat("bigamount",Float.MIN_VALUE);
                   if( dayamount <= day2 ){//we are lloking for the min and max spend with an if
                       editor.putFloat("lessamount",dayamount);
                       editor2.putString("mindate",dateStr);
                       //Toast.makeText(MainActivity.this,"informat111111ionsaved",Toast.LENGTH_LONG).show();
                   }
                   else if (dayamount > day1){
                       editor.putFloat("bigamount",dayamount);
                       editor2.putString("maxdate",dateStr);
                       //Toast.makeText(MainActivity.this,"informationsaved",Toast.LENGTH_LONG).show();
                   }
               editor.putFloat("dayamount" , dayamount );
               float newtotal = currentTotal + dayamount;
               editor.putFloat("total_expenses",newtotal);
                editor.apply();
                editor2.apply();
                Toast.makeText(MainActivity.this,"informationsaved",Toast.LENGTH_LONG).show();
                summoney.setText(String.valueOf(dayamount));

            }
        });
        displaybutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float total = sp.getFloat("total_expenses",0f);
                float mo = sp.getFloat("mo",0f);
                summoney.setText(String.valueOf("o mesos oros einai ths bdomadas:"+mo));
                day.setText(String.valueOf(total)+"ta ejoda ayths ths bdomadas htan");
                int lastWeek = sp3.getInt("last_week", currentWeek);
                String datemax  = sp2.getString("maxdate", "");
                 String datemin  = sp2.getString("mindate", "");
              float mikrotero1 = sp.getFloat("lessamount",0f);
               mikrotero.setText("Η ΛΙΓΟΤΕΡΗ ΣΠΑΤΑΛΗ ΣΕ ΜΙΑ ΜΕΡΑ" + String.valueOf(mikrotero1 )+"$ ΣΤΗΝ ΗΜΕΡΟΜΙΝΙΑ:"+ datemin);
               float megalitero1 = sp.getFloat("bigamount",0f);
               megalitero.setText("Η ΜΕΓΑΛΥΤΕΡΗ ΣΠΑΤΑΛΗ ΣΕ ΜΙΑ ΜΕΡΑ" + String.valueOf(megalitero1) +"$ ΣΤΗΝ ΗΜΕΡΟΜΙΝΙΑ:"+ datemax);
           summoney.setText("Α ΣΥΝΟΛΙΚΑ ΕΞΟΔΑ ΗΤΑΝ:"+String.valueOf(total)+"ΤΗΝ βδομαδα που περασε");
            }
        });
}}