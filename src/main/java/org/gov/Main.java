package org.gov;

import org.gov.elasticsearch.ESClient;
import org.gov.imaging.ExifJSON;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) {
	// write your code here

        ESClient esclient = new ESClient();

        esclient.createClientSession();
        ArrayList<String> arrHits = esclient.searchIndex("stagedfiles", null);

        ArrayList<JSONObject> arrUpdateStaged = new ArrayList<JSONObject>(arrHits.size());

        ArrayList<JSONObject> arrFinal = new ArrayList<JSONObject>(arrHits.size());
        for(String hit: arrHits) {

            JSONObject jsonHit = new JSONObject(hit);
            System.out.println("[hit]: " + jsonHit.getString("path"));
            File file1 = new File(jsonHit.getString("path"));

            JSONObject jsonExif = ExifJSON.getExifJSON(file1);
            JSONObject jsonFinal = (new JSONObject(hit)).put("exif", jsonExif.getJSONObject("exif"));

            jsonFinal.remove("status");
            arrFinal.add(jsonFinal);
            jsonHit.put("status", "staged");
            arrUpdateStaged.add(jsonHit);


            System.out.println("With exif: " + jsonHit.getString("status"));
            System.out.println("With exif: " + jsonFinal.toString());
        }

        // this below line is working
        esclient.indexBuilkDocuments("photos", arrFinal);

        // TODO: this below line has issues updating
        esclient.updateBuilkDocuments("stagedfiles", arrUpdateStaged);








    }
}
