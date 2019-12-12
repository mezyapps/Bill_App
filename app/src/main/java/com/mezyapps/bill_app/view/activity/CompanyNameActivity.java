package com.mezyapps.bill_app.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mezyapps.bill_app.R;
import com.mezyapps.bill_app.utils.SharedLoginUtils;

public class CompanyNameActivity extends AppCompatActivity {

    private ImageView iv_back;
    private TextInputEditText edit_company_name;
    private Button btn_save;
    private String company_name = "", getCompany_name = "";
    private TextView textCompanyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_name);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        iv_back = findViewById(R.id.iv_back);
        edit_company_name = findViewById(R.id.edit_company_name);
        btn_save = findViewById(R.id.btn_save);
        textCompanyName = findViewById(R.id.textCompanyName);

        getCompany_name = SharedLoginUtils.getCompanyName(CompanyNameActivity.this);
        textCompanyName.setText(getCompany_name);

    }

    private void events() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                company_name = edit_company_name.getText().toString().trim();
                SharedLoginUtils.addCompany(CompanyNameActivity.this, company_name);
                getCompany_name = SharedLoginUtils.getCompanyName(CompanyNameActivity.this);
                if (getCompany_name.equalsIgnoreCase(""))
                {
                    Toast.makeText(CompanyNameActivity.this, "Company Remove Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(CompanyNameActivity.this, "Company Name Add Successfully", Toast.LENGTH_SHORT).show();
                }
                textCompanyName.setText(getCompany_name);
                edit_company_name.setText("");

            }
        });
    }

    private boolean validation() {
        company_name = edit_company_name.getText().toString().trim();
        if (company_name.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Company Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
