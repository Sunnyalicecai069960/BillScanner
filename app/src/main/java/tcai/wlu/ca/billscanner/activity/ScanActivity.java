package tcai.wlu.ca.billscanner.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tcai.wlu.ca.billscanner.R;
import tcai.wlu.ca.billscanner.bean.Record;
import tcai.wlu.ca.billscanner.common.Common;
import tcai.wlu.ca.billscanner.controller.RecordController;
import tcai.wlu.ca.billscanner.fragment.RecordFragment;
import tcai.wlu.ca.billscanner.view.HomeAdapter;

public class ScanActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int flag = 0;
    private RecordController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Intent intent = new Intent();
                intent.setClass(ScanActivity.this, MainActivity.class);
                startActivity(intent);
                ScanActivity.this.finish();
            }
        });

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        controller = new RecordController(this.getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.discount:
                applyDiscount();
                break;
            case R.id.tax:
                applyTax();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmChange:
                confirmChange();
                break;
        }
    }

    public void confirmChange() {
        System.out.println("flag---ccc---------->"+flag);
        if (flag == 0) {
            System.out.println("----------------->"+Common.records.size());
            for (int i = 0; i < Common.records.size(); i++) {
                System.out.println("insert---------->"+i);
                controller.insertRecord(Common.records.get(i));
            }
//            Common.records.clear();
            finish();
        } else {
            RecordFragment recordFragment = (RecordFragment) fragmentManager.findFragmentById(R.id.fragment);
            if (recordFragment == null)
                Toast.makeText(getApplicationContext(), "recordFragment not found", Toast.LENGTH_LONG).show();
            else {
                SparseBooleanArray sparseBooleanArray = recordFragment.mItemAdapter.getmCheckStates();
                final List<Record> records = new ArrayList<>();
                for (int i = 0; i < sparseBooleanArray.size(); i++) {
                    if (sparseBooleanArray.valueAt(i)) {
                        records.add(Common.records.get(sparseBooleanArray.keyAt(i)));
                        invisible(records);
                    }
                }
//                Toast.makeText(getApplicationContext(), sum, Toast.LENGTH_LONG).show();
//
                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

                final EditText text = new EditText(this);
                builder.setView(text, 20, 20, 20, 20);
                if (flag == 1) {
                    builder.setTitle("Discount:(%)");

                    builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //apply discount
                            Float discount = Float.parseFloat(text.getText().toString() == "" ? "0.0" : text.getText().toString());
                            if (discount < 0 || discount > 100) {
                                Toast.makeText(getApplicationContext(), "discount should be in 0~100", Toast.LENGTH_SHORT).show();
                                invisible(Common.records);
                                return;
                            }
                            for (int i = 0; i < records.size(); i++) {
                                int index = Common.records.indexOf(records.get(i));
                                Common.records.get(index).setDiscount(Common.formatMoney(discount/100));
                                Common.setRecordTotalPrice(Common.records.get(index));
                            }
                            invisible(Common.records);
                            flag=0;
                        }
                    });
                } else if (flag == 2) {
                    builder.setTitle("Tax:(%)");
                    builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //apply tax
                            Float tax = Float.parseFloat(text.getText().toString() == "" ? "0.0" : text.getText().toString());
                            if (tax < 0 || tax > 100) {
                                Toast.makeText(getApplicationContext(), "tax should be in 0~100", Toast.LENGTH_SHORT).show();
                                invisible(Common.records);
                                return;
                            }
                            for (int i = 0; i < records.size(); i++) {
                                int index = Common.records.indexOf(records.get(i));
                                Common.records.get(index).setTax(Common.formatMoney(tax/100));
                                Common.setRecordTotalPrice(Common.records.get(index));
                            }
                            invisible(Common.records);
                            flag=0;
                        }
                    });
                }
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        invisible(Common.records);
                        Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        }
        flag = 0;
    }

    public void visible() {
        RecordFragment recordFragment = (RecordFragment) fragmentManager.findFragmentById(R.id.fragment);
        System.out.println("set vvvv ------------>" + 1);
        HomeAdapter adapter = recordFragment.mItemAdapter;
        List<Record> records = Common.records;
        adapter.setmData(records, HomeAdapter.VISIBLE);
        adapter.setmData(new ArrayList<Record>(), HomeAdapter.INVISIBLE);
        adapter.notifyDataSetChanged();
        // recordFragment.onResume();
    }

    public void invisible(List<Record> records) {
        RecordFragment recordFragment = (RecordFragment) fragmentManager.findFragmentById(R.id.fragment);
        HomeAdapter adapter = recordFragment.mItemAdapter;
        adapter.setmData(records, HomeAdapter.INVISIBLE);
        adapter.setmData(new ArrayList<Record>(), HomeAdapter.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    public void applyDiscount() {
        flag = 1;
        visible();
    }

    public void applyTax() {
        flag = 2;
        visible();
    }
}
