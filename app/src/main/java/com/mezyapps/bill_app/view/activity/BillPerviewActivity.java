package com.mezyapps.bill_app.view.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.mezyapps.bill_app.R;
import com.mezyapps.bill_app.adapter.BillitemAdapter;
import com.mezyapps.bill_app.database.DatabaseConstant;
import com.mezyapps.bill_app.database.DatabaseHandler;
import com.mezyapps.bill_app.model.BillHDModel;
import com.mezyapps.bill_app.model.LocalDBItemModel;
import com.mezyapps.bill_app.utils.SharedLoginUtils;
import com.mezyapps.bill_app.utils.ShowProgressDialog;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class BillPerviewActivity extends AppCompatActivity {

    private BillHDModel billHDModel;
    private String bill_no, date, cust_name, total_amt, total_qty,company_name="";
    private ImageView iv_back, iv_print;
    private TextView textBillNo, textCustName, textDate, textTotalQty, textTotalAmt;
    private RecyclerView recyclerViewBill;
    private DatabaseHandler databaseHandler;
    private ArrayList<LocalDBItemModel> localDBItemModelArrayList = new ArrayList<>();
    private BillitemAdapter billitemAdapter;
    private File pdfFile;
    private ShowProgressDialog showProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_perview);

        find_View_IDS();
        events();
    }

    private void find_View_IDS() {
        iv_back = findViewById(R.id.iv_back);
        textBillNo = findViewById(R.id.textBillNo);
        textCustName = findViewById(R.id.textCustName);
        textDate = findViewById(R.id.textDate);
        textTotalQty = findViewById(R.id.textTotalQty);
        textTotalAmt = findViewById(R.id.textTotalAmt);
        recyclerViewBill = findViewById(R.id.recyclerViewBill);
        iv_print = findViewById(R.id.iv_print);
        databaseHandler = new DatabaseHandler(BillPerviewActivity.this);
        showProgressDialog=new ShowProgressDialog(BillPerviewActivity.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BillPerviewActivity.this);
        recyclerViewBill.setLayoutManager(linearLayoutManager);

        company_name= SharedLoginUtils.getCompanyName(BillPerviewActivity.this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            billHDModel = bundle.getParcelable("BILL");
            bill_no = billHDModel.getId();
            date = billHDModel.getDate();
            cust_name = billHDModel.getCust_name();
            total_amt = billHDModel.getTotal_amt();
            total_qty = billHDModel.getTotal_qty();

            textBillNo.setText(bill_no);
            textCustName.setText(cust_name);
            textDate.setText(date);
            String total_amtStr = "Total Amt=" + total_amt;
            String total_qtyStr = "Total Qty=" + total_qty;
            textTotalAmt.setText(total_amtStr);
            textTotalQty.setText(total_qtyStr);
        }

        callListBillItem(bill_no);
    }

    private void callListBillItem(String bill_no) {
        try {
            String selectQuery = "SELECT  * FROM " + DatabaseConstant.BillDT.TABLE_NAME + " WHERE BILL_ID_DT=" + bill_no;

            localDBItemModelArrayList.clear();
            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            int sr_no = 1;
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillDT.ID));
                String item = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillDT.ITEM));
                String qty = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillDT.QTY));
                String rate = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillDT.RATE));
                String amt = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillDT.AMOUNT));


                LocalDBItemModel localDBItemModel = new LocalDBItemModel();
                localDBItemModel.setId(id);
                localDBItemModel.setItem(item);
                localDBItemModel.setQty(qty);
                localDBItemModel.setRate(rate);
                localDBItemModel.setAmt(amt);

                localDBItemModel.setSr_no(String.valueOf(sr_no));

                localDBItemModelArrayList.add(localDBItemModel);
                sr_no++;
            }
            billitemAdapter = new BillitemAdapter(BillPerviewActivity.this, localDBItemModelArrayList);
            recyclerViewBill.setAdapter(billitemAdapter);
            billitemAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void events() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iv_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdf();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void createPdf() throws FileNotFoundException, DocumentException {
        showProgressDialog.showDialog();
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Bill_APP");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i("TAG", "Created a new directory for PDF");
        }

        String pdfname = bill_no+".pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A5);
        document.setMargins(10, 10, 0f, 0f);
        float fontSize=10.0f;

        PdfPTable table = new PdfPTable(new float[]{1,2, 2, 2, 2});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(20);
        table.setTotalWidth(PageSize.A5.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("Sr.No");
        table.addCell("Item");
        table.addCell("Qty");
        table.addCell("Rate");
        table.addCell("Amt");

        table.setHeaderRows(1);

        PdfPCell[] cells = table.getRow(0).getCells();
        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(BaseColor.GRAY);
        }
        int sr_no=1;
        for (int i = 0; i < localDBItemModelArrayList.size(); i++) {
            String item = localDBItemModelArrayList.get(i).getItem();
            String qty = localDBItemModelArrayList.get(i).getQty();
            String rate = localDBItemModelArrayList.get(i).getRate();
            String amount=localDBItemModelArrayList.get(i).getAmt();

            table.addCell(String.valueOf(sr_no));
            table.addCell(String.valueOf(item));
            table.addCell(String.valueOf(qty));
            table.addCell(String.valueOf(rate));
            table.addCell(String.valueOf(amount));
            sr_no++;
        }
        Font cellFont = new Font(Font.FontFamily.TIMES_ROMAN, Font.DEFAULTSIZE, Font.BOLD, BaseColor.BLACK);
        table.addCell("");
        table.addCell(new Phrase(Element.ALIGN_CENTER,"Total Qty",cellFont));
        table.addCell(new Phrase(Element.ALIGN_CENTER,total_qty,cellFont));
        table.addCell(new Phrase(Element.ALIGN_CENTER,"Total Amt",cellFont));
        table.addCell(new Phrase(Element.ALIGN_CENTER,total_amt,cellFont));
     /*   int index=table.size()-1;
        PdfPCell[] cells1 = table.getRow(index).getCells();
        for (PdfPCell cell : cells1) {
            cell.setBackgroundColor(BaseColor.GRAY);


        }*/


        PdfWriter.getInstance(document, output);
        document.open();
        //Document Title
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.BOLD, BaseColor.BLACK);
        addNewItem(document,"Estimate",Element.ALIGN_CENTER,titleFont);

        if(!company_name.equalsIgnoreCase(""))
        {
            Font companyFont = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.BOLD,BaseColor.BLACK);
            addNewItem(document,company_name,Element.ALIGN_CENTER,companyFont);
        }

        Font billNoFont = new Font(Font.FontFamily.TIMES_ROMAN, fontSize, Font.NORMAL, BaseColor.BLACK);
        addNewBillDateItem(document,billNoFont);


        Font custFont = new Font(Font.FontFamily.TIMES_ROMAN, fontSize, Font.NORMAL, BaseColor.BLACK);
        addNewItem(document,"Customer Name :"+" "+cust_name,Element.ALIGN_LEFT,custFont);

        addLineSeperator(document);


        document.add(table);
        document.close();

        openGeneratedPDF();

    }

    private void addNewBillDateItem(Document document, Font font) throws DocumentException {
        Chunk glue = new Chunk(new VerticalPositionMark());
        Paragraph p = new Paragraph("Bill NO : "+bill_no,font);
        p.add(new Chunk(glue));
        p.add("Date : "+date);
        document.add(p);
    }

    private void addLineSeperator(Document document) throws DocumentException {
        LineSeparator lineSeparator=new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0,0,0,68));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }

    public  void addNewItem(Document document, String title, int align, Font font) throws DocumentException {
        Chunk chunk=new Chunk(title,font);
        Paragraph paragraph=new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);
    }

    private void openGeneratedPDF() {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/Bill_APP/"+bill_no+".pdf");
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
                intent.setDataAndType(uri, "application/pdf");

                // validate that the device can open your File!
                PackageManager pm = this.getPackageManager();
                if (intent.resolveActivity(pm) != null) {
                    startActivity(intent);
                    showProgressDialog.dismissDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showProgressDialog.dismissDialog();
        }
    }

}
