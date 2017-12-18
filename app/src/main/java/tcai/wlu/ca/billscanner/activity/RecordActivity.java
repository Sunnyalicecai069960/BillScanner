package tcai.wlu.ca.billscanner.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tcai.wlu.ca.billscanner.R;
import tcai.wlu.ca.billscanner.bean.Record;
import tcai.wlu.ca.billscanner.bean.Type;
import tcai.wlu.ca.billscanner.common.Common;
import tcai.wlu.ca.billscanner.controller.RecordController;
import tcai.wlu.ca.billscanner.view.MySpinnerAdapter;

public class RecordActivity extends AppCompatActivity {

    private ImageView edit;
    private Button confirm;
    private Spinner type;
    private Record record;
    private EditText item;
    private EditText price;
    private EditText tax;
    private EditText discount;
    private EditText totalPrice;
    private TextView time;

    private int cYear;
    private int month;
    private int day;

    boolean isEditable = false;

    private RecordController recordController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        init();
        if(getIntent().getIntExtra("ifVisible",0)==1){
            edit.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.INVISIBLE);
            record = Common.records.get(getIntent().getIntExtra("index",0));
            uneditable();
            type.setSelection(Common.keys.indexOf(record.getType()));
            item.setText(record.getItem());
            price.setText(record.getPrice()+"");
            tax.setText(record.getTax()*100+"");
            discount.setText(record.getDiscount()*100+"");
            totalPrice.setText(record.getTotalPrice()+"");
            time.setText(record.getTime());
            cYear =  Common.getYear(Common.formatToDate(record.getTime()));
            month = Common.getMonth(Common.formatToDate(record.getTime()))-1;
            day = Common.getDay(Common.formatToDate(record.getTime()));
        }else {
            record = new Record();
            edit.setVisibility(View.INVISIBLE);
            confirm.setVisibility(View.VISIBLE);
            cYear = Common.getCurrentYear();
            month = Common.getCurrentMonth()-1;
            day = Common.getCurrentDay();
            time.setText(Common.getDayText(cYear,month+1,day));
            editable();
        }
    }

    private void uneditable(){
        type.setEnabled(false);
        item.setEnabled(false);
        price.setEnabled(false);
        tax.setEnabled(false);
        discount.setEnabled(false);
        totalPrice.setEnabled(false);
        time.setEnabled(false);
    }

    private void editable(){
        type.setEnabled(true);
        item.setEnabled(true);
        price.setEnabled(true);
        tax.setEnabled(true);
        discount.setEnabled(true);
        totalPrice.setEnabled(true);
        time.setEnabled(true);

    }


    private void init(){
        edit = (ImageView) findViewById(R.id.edit);
        confirm = (Button) findViewById(R.id.confirm);
        type = (Spinner) findViewById(R.id.type);
        item = (EditText) findViewById(R.id.item);
        price = (EditText) findViewById(R.id.price);
        tax = (EditText) findViewById(R.id.tax);
        discount = (EditText) findViewById(R.id.discount);
        totalPrice = (EditText) findViewById(R.id.totalPrice);
        time = (TextView) findViewById(R.id.time);

        type.setAdapter(new MySpinnerAdapter(this.getBaseContext()));
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                record.setType(Common.keys.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!price.getText().toString().equals("")) {
                    record.setPrice(Float.parseFloat(price.getText().toString()));
                    Common.setRecordTotalPrice(record);
                    totalPrice.setText(record.getTotalPrice()+"");
                }else{
                    record.setPrice(0);
                    record.setTotalPrice(0);
                    totalPrice.setText(record.getTotalPrice()+"");
                }
            }
        });

        tax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!tax.getText().toString().equals("")) {
                    record.setTax(Float.parseFloat(tax.getText().toString())/100);
                }else{
                    record.setTax(0);
                }
                Common.setRecordTotalPrice(record);
                totalPrice.setText(record.getTotalPrice() + "");
            }
        });

        discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!discount.getText().toString().equals("")) {
                    record.setDiscount(Float.parseFloat(discount.getText().toString())/100);
                }else{
                    record.setDiscount(0);
                }
                Common.setRecordTotalPrice(record);
                totalPrice.setText(record.getTotalPrice() + "");
            }
        });
        recordController = new RecordController(this.getBaseContext());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit:
                editBill();
                break;
            case R.id.confirm:
                addRecord();
                break;
            case R.id.time:
                datePicker();
                break;
        }
    }

    private void editBill() {
        isEditable = !isEditable;
        if(isEditable){
            editable();
            edit.setImageResource(R.drawable.correct);
        }else{
            uneditable();
            setRecord();
            if(record.getId()!=null)
                recordController.updateRecord(record);
            edit.setImageResource(R.drawable.edit);
        }
    }

    public void datePicker() {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cYear =year;
                day=dayOfMonth;
                month=monthOfYear;
                time.setText(Common.getDayText(cYear,month+1,day));
            }
        }, cYear, month, day).show();
    }


    public void setRecord(){
        record.setItem(item.getText().toString());
        record.setTotalPrice(Float.parseFloat(totalPrice.getText().toString()));
        Calendar c = Calendar.getInstance();
        c.set(cYear,month,day);
        record.setTime(time.getText().toString());
        record.setMonth(month+1);
        record.setYear(cYear);
    }

    public void addRecord() {
        if(record.getDiscount()<0||record.getDiscount()>100){
            Toast.makeText(getApplicationContext(), "discoount should in 0~100", Toast.LENGTH_LONG).show();
            return;
        }
        if(record.getTax()<0||record.getTax()>100){
            Toast.makeText(getApplicationContext(), "tax should in 0~100", Toast.LENGTH_LONG).show();
            return;
        }
        setRecord();
        recordController.insertRecord(record);
        Toast.makeText(getApplicationContext(), "add successfully!", Toast.LENGTH_LONG).show();
        this.finish();
    }

}
