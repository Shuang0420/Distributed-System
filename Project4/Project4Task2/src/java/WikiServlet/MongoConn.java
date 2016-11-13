/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WikiServlet;

import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Accumulators.sum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.Indexes.descending;
import java.text.SimpleDateFormat;
import static java.util.Collections.singletonList;

/**
 *
 * @author Shuang
 */
public class MongoConn {
    
    MongoClient mongoClient;
    MongoDatabase db;
    MongoCollection<Document> collection;
    final static String userName = "shuang";
    final static String password = "zhuxianlian1";
    

    public MongoConn() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://" + userName + ":" + password + "@ds149577.mlab.com:49577/ds_project4"));
        db = mongoClient.getDatabase("ds_project4");
        System.out.println("mongodb connection succeed!");
        collection = db.getCollection("WebLogs");
    }

    
    /**
     * Insert data into MongoDB.
     * @param data 
     */
    public void storeData(String data) {
        collection.insertOne(Document.parse(data));
    }


    /**
     * Returns log data as a string with html table tag. 
     * @return 
     */
    public String getLog() {
        FindIterable<Document> iterable = collection.find();
        StringBuilder sb = new StringBuilder();
        iterable.forEach(new Block<Document>() {
            public void apply(final Document document) {
                sb.append("<tr>");
                sb.append("<td>").append(document.get("user-agent")).append("</td>");
                sb.append("<td>").append(document.get("para")).append("</td>");
                sb.append("<td>").append(document.get("urlString").toString()).append("</td>");
                sb.append("<td> ").append(document.get("replyFromAPI").toString().substring(0, 20)).append("...</td>");
                sb.append("<td>").append(document.get("resJSON").toString().substring(0, 20)).append("...</td>");
                sb.append("<td>").append(document.get("startFrom")).append("</td>");
                sb.append("<td>").append(document.get("endAt")).append("</td>");
                sb.append("<td>").append(document.get("latency")).append("</td>");
                sb.append("</tr>");
            }
        });
        return sb.toString();
    }

    /**
     * Get total number of entries from table.
     * @return Returns total number of entries.
     */
    public long getNumOfEntries() {
        return collection.count();
    }

    /**
     * Returns average latency.
     * @return Returns average latency.
     */
    public String getAvgLatency() {
        Document results = collection.aggregate(singletonList(group("$log", avg("latency", "$latency")))).first();
        String latency = results.get("latency").toString();
        return latency.substring(0, latency.indexOf(".")) + " ms";
    }

    /**
     * Returns last visit date as a date format string.
     * @return Returns last visit date as a date format string.
     */
    public String getLastVisit() {
        Document results = collection.aggregate(singletonList(sort(descending("startFrom")))).first();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(Long.parseLong(results.get("startFrom").toString()));
        return d;
    }

    /**
     * Returns popular query/user-agent and other fields.
     * @param ascending
     * @param para
     * @return Returns popular query/user-agent and other fields as a string.
     */
    public String getPopular(boolean ascending, String para) {
        StringBuilder sb = new StringBuilder();
        Bson sumField = group(para, sum("total", 1));
        Bson sortField;
        if (ascending) {
            sortField = sort(descending("total"));
        } else {
            sortField = sort(ascending("total"));
        }
        Bson limitDoc = limit(5);
        List<Bson> pipeline = Arrays.asList(sumField, sortField, limitDoc);
        List<Document> results = collection.aggregate(pipeline).into(
                new ArrayList<>());

        results.forEach((cur) -> {
            sb.append(cur.get("_id").toString()).append("\t").append(cur.get("total").toString()).append("<br >");
        });
        return sb.toString();
    }

    /**
     * Returns peak time as a date format string.
     * @param peakTime
     * @return Returns peak time as a date format string.
     */
    public String getPeakTime(boolean peakTime) {
        String res = null;
        Bson sumField = group("$startFrom", sum("total", 1));
        Bson sortField;
        if (peakTime) {
            sortField = sort(descending("total"));
        } else {
            sortField = sort(ascending("total"));
        }
        Bson limitDoc = limit(1);
        List<Bson> pipeline = Arrays.asList(sumField, sortField, limitDoc);
        Document results = collection.aggregate(pipeline).first();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(Long.parseLong(results.get("_id").toString()));
        return d;
    }

}
