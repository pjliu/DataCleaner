import java.io.*;
import java.util.*;

 
public class TestTweet {

    public TestTweet ()
    {
    }

    private int findIndex(String[] theArray, String str) {
	if (theArray != null && str != null) {
		for (int i=0; i<theArray.length; i++) {
			if (str.equals(theArray[i])) {
				return i; // found
			}
		}
		return -1; // not found
	} else {
		return -1;
	}
    }

    private void perform(String inputFileName, String outputFileName) {

	  String[] header = new String[] {
		"user_id", "user_name", "user_location", "user_followers", "user_friends", 
		"status_id", "status_date", "status_text", "status_sentiment", "status_is_retweet", 
		"status_retweet_of", "status_retweet_count", "status_latitude", "status_longitude", "user_join_date", 
		"user_status_count", "user_listed", "user_verified", "user_lang", "user_utc_offset", 
		"matched"};

      String  line = null;
      try{
		BufferedReader br = new BufferedReader(new FileReader(inputFileName));
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFileName)));
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
 		
		Map kwMap = new HashMap();
		Map tweetMap = new HashMap();
		List keepList = new ArrayList();
		List deleteList = new ArrayList();

		int nameIndex = findIndex(header, "user_name");
		int kwIndex = findIndex(header, "matched");
		int tweetIndex = findIndex(header, "status_text");
		int lineCnt = 0;
         	while ((line = br.readLine()) != null) {
			lineCnt++;
			String[] tmp = line.split(",");
			if (tmp.length == header.length) {
				String name = tmp[nameIndex].trim();
                if (header[nameIndex].equals(name)) continue; // header
				String kw = tmp[kwIndex].trim();
				String tweet = tmp[tweetIndex].trim();
				
				// handle keyword
				Map nameCount = null;
				if (kwMap.containsKey(kw)) {
					nameCount = (Map)kwMap.get(kw);
				} else {
					nameCount = new HashMap();
					kwMap.put(kw, nameCount);
				}
				int cnt = 0;
				if (nameCount.containsKey(name)) {
					cnt = ((Integer)nameCount.get(name)).intValue();
				}
				nameCount.put(name, new Integer(cnt+1));
				
				// handle tweet
				int maxNumTweet = 10;
				List tweetList = null;
				if (tweetMap.containsKey(name)) {
					tweetList = (List)tweetMap.get(name);
				} else {
					tweetList = new ArrayList();
					tweetMap.put(name, tweetList);
				}
				if (tweetList.size() < maxNumTweet) {
					tweetList.add(tweet);
				} 
			} else {
				System.out.println("cannot process line "+lineCnt+", "+" numColumns="+tmp.length+": "+line);
			}			
         	}

		Set set = kwMap.entrySet(); 
		Iterator i = set.iterator(); 
		while(i.hasNext()) { 
			Map.Entry me = (Map.Entry)i.next();
			String kw = (String)me.getKey();
			Map nameCount = (Map)me.getValue();
			Map sortedNameCount = sortByComparator(nameCount);
			System.out.println(kw + ":"); 
			Iterator nameIterator = sortedNameCount.keySet().iterator();
			while(nameIterator.hasNext()) {
				String userName = (String)nameIterator.next();
				System.out.println("user:"+userName);
				System.out.println("count:"+sortedNameCount.get(userName));
				System.out.println("tweet:"+tweetMap.get(userName));
				System.out.println("Keep or Delete?");
				String input = stdin.readLine();
				if ("Keep".equalsIgnoreCase(input)) {
					keepList.add(userName);
				} else if ("Delete".equalsIgnoreCase(input)) {
					deleteList.add(userName);
				}
			}
			System.out.println();				 
		}
		System.out.println("Keep:"+keepList);
		System.out.println("Delete:"+deleteList);

        br.close();
        stdin.close();

        // reopen input file
        br = new BufferedReader(new FileReader(inputFileName));
        lineCnt = 0;
        while ((line = br.readLine()) != null) {
            lineCnt++;
            String[] tmp = line.split(",");
            String name = tmp[nameIndex].trim();
        	if (tmp.length == header.length && (header[nameIndex].equals(name) || keepList.contains(name))) {
                bw.write(line);
                bw.newLine();
            }
        }
        br.close();
        bw.close();
      } catch(Exception e){
		    e.printStackTrace();
      }
    }

    private static Map sortByComparator(Map unsortMap) {
 
	List list = new LinkedList(unsortMap.entrySet()); 
	// sort list based on comparator
	Collections.sort(list, new Comparator() {
		public int compare(Object o1, Object o2) {
			return -1*((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
		}
	});
 
	// put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
	Map sortedMap = new LinkedHashMap();
	int i = 0;
	int max = 10;
	for (Iterator it = list.iterator(); it.hasNext();) {
		if (i<max) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
			i++;
		} else {
			break;
		}
	}
	return sortedMap;
    }

    public static void main(String[] args) {
	TestTweet test = new TestTweet();
	test.perform("c:/temp/java/small_dataset.csv", "c:/temp/java/small_dataset_keep.csv");
   }
}



