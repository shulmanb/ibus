import java.util.List;

import org.junit.Ignore;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;

public class SimpleDBRafiExtractor {
	public static void main(String[] args) {
		 AmazonSimpleDB sdb = new AmazonSimpleDBClient(new BasicAWSCredentials("AKIAIZANS6EFPJLXDW3Q", "WmZ9RYrSgQ5ORxvN8AAZP29JdtA26QU59w7gZExI"));
		 sdb.setEndpoint("sdb.eu-west-1.amazonaws.com");
		 String selectExpression = "select * from ibus_line_segments where line = '2' intersection ts is not null order by ts asc limit 2500";
         SelectRequest selectRequest = new SelectRequest(selectExpression);
         List<Item> items = sdb.select(selectRequest).getItems();
         System.out.println("Found "+items.size()+" points");
         for (Item item : items) {
        	 System.out.print("{'point':{");
             for (Attribute attribute : item.getAttributes()) {
                 System.out.print("'"+attribute.getName()+"':'"+attribute.getValue()+"',");
             }
        	 System.out.println("}}");
         }
         System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
         selectExpression = "select * from ibus_line_stations where line = '2' intersection ts is not null order by ts asc limit 500";
         selectRequest = new SelectRequest(selectExpression);
         items = sdb.select(selectRequest).getItems();
         System.out.println("Found "+items.size()+" stations");
         for (Item item : items) {
        	 System.out.print("{'station':{");
        	 String stationId = item.getName();
        	 GetAttributesResult station = sdb.getAttributes(new GetAttributesRequest("ibus_stations", stationId));
             for (Attribute attribute : item.getAttributes()) {
            	 if(attribute.getName().equals("station")){
            		 continue;
            	 }
                 System.out.print("'"+attribute.getName()+"':'"+attribute.getValue()+"',");
             }
             for(Attribute attr:station.getAttributes()){
                 System.out.print("'"+attr.getName()+"':'"+attr.getValue()+"',");
             }
        	 System.out.println("}}");
         }
         System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
}
