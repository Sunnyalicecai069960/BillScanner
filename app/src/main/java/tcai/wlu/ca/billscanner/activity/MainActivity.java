package tcai.wlu.ca.billscanner.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tcai.wlu.ca.billscanner.R;
import tcai.wlu.ca.billscanner.bean.MonthBill;
import tcai.wlu.ca.billscanner.bean.Record;
import tcai.wlu.ca.billscanner.common.Common;
import tcai.wlu.ca.billscanner.controller.MonthBillController;
import tcai.wlu.ca.billscanner.controller.RecordController;
import tcai.wlu.ca.billscanner.fragment.RecordFragment;


public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private TextView moneyView,monthView;
    private MonthBillController billController;
    private RecordController recordController;
    Map<Integer, MonthBill> currentMonthBill;
    List<Record> recordList;
    RecordFragment fragment;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 1;
    private static final int PHOTO_RESOULT = 2;

    // The URI of photo taken from gallery
    private Uri mUriPhotoTaken;

    // File of the photo taken with camera
    private File mFilePhotoTaken;


    private int year1;
    private int month;
    private int day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.month=Common.getCurrentMonth();
        Common.year=Common.getCurrentYear();
        billController = new MonthBillController(this.getBaseContext());
        recordController=new RecordController(this.getBaseContext());
        recordList = recordController.getRecordsByMonth(Common.month,Common.year);
        currentMonthBill = billController.getBillByMonth(Common.getCurrentMonth(),Common.getCurrentYear());
        Common.records = recordList;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        moneyView = (TextView) findViewById(R.id.money);
        monthView = (TextView) findViewById(R.id.month);
        MonthBill total = currentMonthBill.get(Common.TOTAL_CODE);
        monthView.setText(Common.getMonthText(Common.year,Common.month));
        moneyView.setText(total==null?0.0+"":total.getAmount()+"");

        year1= Common.getCurrentYear();
        month = Common.getCurrentMonth()-1;
        day = Common.getCurrentDay();

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = (RecordFragment) fragmentManager.findFragmentById(R.id.fragment);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                addRecord();
                break;
            case R.id.camera:
                takePic();
                break;
            case R.id.album:
                selectPhoto();
                break;
            case R.id.money:
                goToChart();
                break;
            case R.id.month:
                viewOrders();
                break;

        }
    }

    public void takePic() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            // Save the photo taken to a temporary file.
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                mFilePhotoTaken = File.createTempFile(
                        "IMG_",  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );

                // Create the File where the photo should go
                // Continue only if the File was successfully created
                if (mFilePhotoTaken != null) {
                    mUriPhotoTaken = Uri.fromFile(mFilePhotoTaken);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);

                    // Finally start camera activity
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    startPhotoZoom(Uri.fromFile(mFilePhotoTaken));
//                    Intent intent = new Intent(this,StartScanActivity.class);
//                    intent.setData(Uri.fromFile(mFilePhotoTaken));
//                    intent.putExtra("select",1);
//                    startActivityForResult(intent,REQUEST_TAKE_PHOTO);
                }
                break;
            case REQUEST_SELECT_IMAGE_IN_ALBUM:
                if (resultCode == RESULT_OK) {
                    Uri imageUri;
                    if (data == null || data.getData() == null) {
                        imageUri = mUriPhotoTaken;
                    } else {
                        imageUri = data.getData();
                    }
                    startPhotoZoom(imageUri);
//                    Intent intent = new Intent(this,StartScanActivity.class);
//                    intent.setData(imageUri);
//                    intent.putExtra("type",2);
//                    startActivityForResult(intent,REQUEST_SELECT_IMAGE_IN_ALBUM);
                }

                break;
            case PHOTO_RESOULT:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Intent intent = new Intent(this, StartScanActivity.class);
                        intent.setData(data.getData());
                        startActivity(intent);
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void startPhotoZoom(Uri uri) {
        Uri imageUri = null;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", "true");
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat",
                Bitmap.CompressFormat.PNG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.setData(uri);
        startActivityForResult(intent, PHOTO_RESOULT);
    }


    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_SELECT_IMAGE_IN_ALBUM);
        Toast.makeText(getApplicationContext(), "selectPhoto", Toast.LENGTH_SHORT).show();
    }

    public void addRecord() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, RecordActivity.class);
        intent.putExtra("ifVisible",0);
        startActivity(intent);

    }

    public void goToChart() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,ChartActivity.class);
        startActivity(intent);
    }

    public void viewOrders() {
        Toast.makeText(getApplicationContext(), "viewOrders", Toast.LENGTH_SHORT).show();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                year1=year;
                day=dayOfMonth;
                month=monthOfYear;
                TextView time=(TextView)findViewById(R.id.month);
                time.setText(Common.getMonthText(year1,month+1));
                recordList = recordController.getRecordsByMonth(month+1,year);
                Common.records = recordList;
                fragment.dataChange();
                currentMonthBill = billController.getBillByMonth(month+1,year);
                MonthBill total = currentMonthBill.get(Common.TOTAL_CODE);
                moneyView.setText(total==null?0.0+"":total.getAmount()+"");
            }
        }, year1, month, day).show();
    }


    public void updateData(){
        currentMonthBill = billController.getBillByMonth(Common.getCurrentMonth(),Common.getCurrentYear());
        MonthBill total = currentMonthBill.get(Common.TOTAL_CODE);
        moneyView.setText(total==null?0.0+"":Common.formatMoney(total.getAmount())+"");
        recordList = recordController.getRecordsByMonth(Common.getCurrentMonth(),Common.getCurrentYear());
    }

    @Override
    protected void onResume() {
        updateData();
        Common.records = recordList;
        TextView time=(TextView)findViewById(R.id.month);
        time.setText(Common.getMonthText(Common.getCurrentYear(),Common.getCurrentMonth()));
        super.onResume();
    }

}
