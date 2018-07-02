import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;

import twitter4j.conf.ConfigurationBuilder;

public class TwitterSearch {
	private Twitter twitter;
	private String destinationFileLocation;
	private String propertiesFilelocation;
	Properties prop = new Properties();
	FileOutputStream fos;
	
	public TwitterSearch(String ploc, String dloc){
	propertiesFilelocation = ploc;
	destinationFileLocation = dloc;
	
    //load a properties file
	try 
	{
	    prop.load(new FileInputStream(propertiesFilelocation));

	ConfigurationBuilder config = new ConfigurationBuilder();
	config.setOAuthConsumerKey(prop.getProperty("CONSUMER_KEY"));
	config.setOAuthConsumerSecret(prop.getProperty("CONSUMER_SECRET"));
	config.setOAuthAccessToken(prop.getProperty("ACCESS_TOKEN"));
	config.setOAuthAccessTokenSecret(prop.getProperty("ACCESS_TOKEN_SECRET"));
	config.setJSONStoreEnabled(true);
	config.setIncludeEntitiesEnabled(true);

	twitter = new TwitterFactory(config.build()).getInstance();
	//TwitterStream twitter4j.TwitterStreamFactor
	}catch (FileNotFoundException e){
	e.printStackTrace();

	//block Note: This element neither has attached source nora
	}catch (IOException e){
	e.printStackTrace();
	}
	}
	public void loadTweets() throws TwitterException{
		//Twitter twitter = TwitterFactory.getSingleton();  
		try{ 
		fos = new FileOutputStream(new File(destinationFileLocation));
		}catch (IOException e){
		e.printStackTrace();
		}
		
		//twitterStream. addListener(listener);

		String hashtag1 = prop.getProperty("TWITTER_KEYWORD1");
		
		//twitter4j.Twitter twitter = new TwitterFactory().getInstance();
		
		try {
	            Query query1 = new Query(hashtag1);
	            
	            QueryResult result1;
	            do {
	                result1 = twitter.search(query1);
	          
	                
	                for(Status tweet : result1.getTweets()) {
	             
	                	try {
	                		String newline = "\r\n";
	                		System.out.println(tweet.getUser().getScreenName() +" " + tweet.getText());

	                		System.out.println("timestamp "+ String.valueOf(tweet.getCreatedAt().getTime()));
	            		    fos.write(TwitterObjectFactory.getRawJSON(tweet).getBytes());
	            		fos.write(newline.getBytes());
	            		}catch (IOException e){
	            		e.printStackTrace();
	            		}
	                }
	            }
	          while ((query1 = result1.nextQuery()) != null);
	            }
	            
	        catch (TwitterException te) {
	            te.printStackTrace();
	            System.out.println("Failed to search tweets: " + te.getMessage());
	            
	        }
	}
	public void stopTwitter(){

		System.out.println("Shut down Twitter stream...");
		try{ 
			
		fos.close();
		System.exit(0);
		}catch (IOException e){
		e.printStackTrace();
		}

		}

	StatusListener listener = new StatusListener(){

		//The onStatus method is executed every time a new tweets comes in.
		//When we get a tweets, write it to a file
		public void onStatus(Status status){
			

		String newline = "\r\n";
		System.out.println(status.getUser().getScreenName() +" " + status.getText());

		System.out.println("timestamp "+ String.valueOf(status.getCreatedAt().getTime()));
		try {
		    fos.write(TwitterObjectFactory.getRawJSON(status).getBytes());
		fos.write(newline.getBytes());
		}catch (IOException e){
		e.printStackTrace();
		}
		}
		//This listener will ignore everything except for new tweets
		
		public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice){}
		public void onTrackLimitationNotice(int numberoflimitedStatuses){}
		public void onScrubGeo(long userId, long upToStatusId){}
		public void onException(Exception ex){}
		public void onStallWarning(StallWarning stallWarning){}
		};
	 public static void main(String[] args) throws TwitterException, InterruptedException {
		 if(args.length != 2){
				System.err.println("Usage 2 parameters 1. properties files with tokes keywords 2. destination file location");
				System.exit(-1);
				}
				TwitterSearch twitterse = new TwitterSearch(args[0],args[1]);
				twitterse.loadTweets();
				
				Thread.sleep(300000);
				
				twitterse.stopTwitter();
	        
	       
	    }
}
