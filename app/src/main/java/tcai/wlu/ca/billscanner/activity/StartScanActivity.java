package tcai.wlu.ca.billscanner.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tcai.wlu.ca.billscanner.R;
import tcai.wlu.ca.billscanner.bean.Record;
import tcai.wlu.ca.billscanner.bean.Type;
import tcai.wlu.ca.billscanner.common.Common;
import tcai.wlu.ca.billscanner.helper.ImageHelper;
import tcai.wlu.ca.billscanner.userDefinedStyle.MonIndicator;

public class StartScanActivity extends AppCompatActivity {

    private ImageView photo;
    private VisionServiceClient client;
    private Bitmap bmp;
    private Button start;
    private MonIndicator monIndicator;
    Map<Integer, List<Type>> textClassify;
    private List<Record> item = new ArrayList<Record>();
    private List price = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginscan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (client == null) {
            client = new VisionServiceRestClient(getString(R.string.subscription_key));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Intent intent = new Intent();
                intent.setClass(StartScanActivity.this, MainActivity.class);
                startActivity(intent);
                StartScanActivity.this.finish();
            }
        });

        photo = (ImageView) findViewById(R.id.photo);
        Intent intent = getIntent();
        bmp = ImageHelper.loadSizeLimitedBitmapFromUri(
                intent.getData(), getContentResolver());
        photo.setImageBitmap(bmp);
        start = (Button) findViewById(R.id.startScan);
        monIndicator = (MonIndicator) findViewById(R.id.monIndicator);

        textClassify = Common.getKeysMap(this.getBaseContext());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startScan:
                startScan();
                break;
        }
    }

    public void startScan() {
        start.setEnabled(false);
        monIndicator.setVisibility(View.VISIBLE);
        monIndicator.setColors(new int[]{0xFF942909, 0xFF577B8C, 0xFF201289, 0xFF000000, 0xFF38B549});

        try {
            new doRequest().execute();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//            mEditText.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        OCR ocr;
        ocr = this.client.recognizeText(inputStream, LanguageCodes.AutoDetect, true);

        String result = gson.toJson(ocr);
        Log.d("result", result);

        return result;
    }

    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence

            if (e != null) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                this.e = null;
            } else {
                Gson gson = new Gson();
                OCR r = gson.fromJson(data, OCR.class);

                String result = "";
                String lineStr = "";
                for (Region reg : r.regions) {
                    for (Line line : reg.lines) {
                        for (Word word : line.words) {
                            result += word.text + " ";
                            lineStr += word.text + " ";
                        }
                        if (ifPrice(lineStr) == 1)
                            price.add(contrivePrice(lineStr));
                        else if (ifItem(lineStr) == 1) {
                            Record record = new Record();
                            record.setItem(lineStr);
                            record.setType(classify(lineStr));
                            record.setTime(Common.formatDate(new Date()));
                            record.setYear(Common.getCurrentYear());
                            record.setMonth(Common.getCurrentMonth());
                            item.add(record);
                        }
                        lineStr = "";
                        result += "\n";
                    }
                    result += "\n\n";
                }
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
            start.setEnabled(true);
            monIndicator.setVisibility(View.INVISIBLE);

            if (item.size() <= price.size()) {
                for (int i = 0; i < item.size(); i++) {
                    item.get(i).setPrice(Float.parseFloat(price.get(i).toString()));
                    item.get(i).setTotalPrice(Float.parseFloat(price.get(i).toString()));
                }
            } else if (item.size() > price.size()) {
                for (int i = 0; i < item.size(); i++) {
                    if (i < price.size()) {
                        item.get(i).setPrice(Float.parseFloat(price.get(i).toString()));
                        item.get(i).setTotalPrice(Float.parseFloat(price.get(i).toString()));
                    } else {
                        item.get(i).setPrice(0);
                        item.get(i).setTotalPrice(0);
                    }
                }
            }
            Common.records = item;
            Intent intent = new Intent(StartScanActivity.this, ScanActivity.class);
            startActivityForResult(intent, 0);
            finish();
        }
    }

    public int ifItem(String line) {
//no '@'//no number only//no number x//no subtotal//no total
        if (line.indexOf("@") != -1)
            return 0;
        else if (line.matches("^[0-9]{10,}$"))
            return 0;
        else if (line.matches("^[1-9]{1,}[Xx]$"))
            return 0;
        else if (line.trim().toLowerCase().equals("mrj") || line.trim().toLowerCase().equals("subtotal") || line.trim().toLowerCase().equals("total") || line.trim().toLowerCase().equals("tax"))
            return 0;
        else if (ifPrice(line) == 1)
            return 0;
        return 1;
    }

    public Float contrivePrice(String price) {
        price = price.trim();
        price = price.replace("$", "");
        price = price.replace("T1", "");
        price = price.replace("T", "");
        price = price.replace("i", ".");
        price = price.replace(" ", ".");
        price = price.replace("l", ".");
        price = price.replace(",", ".");
        Float p;
        try {
            p = Float.parseFloat(price);
        } catch (Exception e) {
            p = Float.parseFloat("0");
        }
        return p;
    }

    public int classify(String item) {
        for (int i = 0; i < textClassify.size(); i++) {
            List<Type> list = textClassify.get(i);
            for (int j = 0; j < list.size(); j++) {
                String regex = "\\.*" + list.get(j).getType() + "\\.*";
                if (item.toLowerCase().indexOf(list.get(j).getType()) != -1)
                    return list.get(j).getCode();
            }
        }
        return textClassify.size() + 1;
    }

    public int ifPrice(String line) {
        String price = line.trim().toLowerCase();
        //only small number//only $+number+T1  ^\$([0-9]{1,})(\.|,|i|l)([0-9]{2})(T1|)
        if (price.indexOf("$") != -1) {
            if (price.indexOf("/") == -1)
                return 1;
            if (price.indexOf("bag") == -1)
                return 1;
        }

        return 0;
    }
}
