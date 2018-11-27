package eu.indiewalkabout.fridgemanager.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.*;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.contentManager.DBAttributes;
import eu.indiewalkabout.fridgemanager.contentManager.DBManager;


// http://www.html.it/pag/49180/database-e-sqlite/

public class NewDeadlineFormActivity extends AppCompatActivity {

    private DBManager dbManager=null;
    private CursorAdapter cursorAdapter=null;  // list view adapter
    private ListView listView=null;
    private Button saveBtn;

    private OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            int position= listView.getPositionForView(view);
            long id = cursorAdapter.getItemId(position);
            if (dbManager.deleteRec(id)){
                cursorAdapter.changeCursor(dbManager.showAll());

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_deadline_form);

        dbManager = new DBManager(this);
        listView = findViewById(R.id.listview);
        Cursor cs = dbManager.showAll();

        // dfine list view adapter
        cursorAdapter = new CursorAdapter(this, cs, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                View v = getLayoutInflater().inflate(R.layout.list_activity_row,null);
                return v;
            }

            @Override
            public void bindView(View view, Context context, Cursor cs) {
                String description = cs.getString(cs.getColumnIndex(DBAttributes.FIELD_DESCRIPTION));
                String date = cs.getString(cs.getColumnIndex(DBAttributes.FIELD_END_DATE));
                String status = cs.getString(cs.getColumnIndex(DBAttributes.FIELD_STATUS));

                TextView txt = (TextView) view.findViewById(R.id.txt_description);
                txt.setText(description);
                txt = (TextView) view.findViewById(R.id.txt_date);
                txt.setText(date);
                txt = (TextView) view.findViewById(R.id.txt_status);
                txt.setText(status);

                ImageButton imgBtn = (ImageButton) view.findViewById(R.id.btn_delete);
                imgBtn.setOnClickListener(clickListener);

            }


            @Override
            public long getItemId(int position) {
                Cursor cs = cursorAdapter.getCursor();
                cs.moveToPosition(position);

                return cs.getLong(cs.getColumnIndex(DBAttributes.FIELD_ID));
            }
        };

        listView.setAdapter(cursorAdapter);


        // define save btn
        saveBtn = (Button) findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText description = (EditText) findViewById(R.id.descriptionEdit);
                EditText date = (EditText) findViewById(R.id.dateEdit);
                EditText status = (EditText) findViewById(R.id.statusEdit);
                if ( (description.length()>0) && (date.length()>0) && (status.length()>0) ){
                        dbManager.insertNew(
                                description.getEditableText().toString(),
                                date.getEditableText().toString(),
                                status.getEditableText().toString());
                }
            }
        });
    }





}
