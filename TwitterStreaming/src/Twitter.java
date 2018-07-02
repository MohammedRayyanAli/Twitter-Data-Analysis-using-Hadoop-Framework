import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;



public class Twitter {

    private TwitterStream twitterStream;
    private String[] keywords;
    private String destinationFileLocation;
    private String propertiesFilelocation;

    Properties prop = new Properties();
    FileOutputStream fos;

    public Twitter(String ploc, String dloc){
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

    twitterStream = new TwitterStreamFactory(config.build()).getInstance();
    //TwitterStream twitter4j.TwitterStreamFactor
    }catch (FileNotFoundException e){
    e.printStackTrace();

    //block Note: This element neither has attached source nora
    }catch (IOException e){
    e.printStackTrace();
    }
    }

    public void startTwitter(){
    try{
    fos = new FileOutputStream(new File(destinationFileLocation));
    }catch (IOException e){
    e.printStackTrace();
    }
    //Add listener to the stream
    twitterStream. addListener(listener);

    String keywordsString = prop.getProperty("TWITTER_KEYWORDS");
    keywords = keywordsString.split("");
    for (int i=0; i < keywords.length; i++){
    keywords[i] = keywords[i].trim();
    }
    System.out.println("Starting Twitter stream...");

    //Filter only relevant tweets based on the keywords
    FilterQuery query = new FilterQuery();
    double[][] loc={{-122.75,36.8},{-121.75,37.8}};
    query.track(keywords);
    query.locations(loc);


    twitterStream.filter(query);

    }
    public void stopTwitter(){

    System.out.println("Shut down Twitter stream...");
    try{
    fos.close();
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

    public static void main(String[] args) throws InterruptedException{

    if(args.length != 2){
    System.err.println("Usage 2 parameters 1. properties files with tokes keywords 2. destination file location");
    System.exit(-1);
    }
    Twitter twitter = new Twitter(args[0],args[1]);
    twitter.startTwitter();
    Thread.sleep(300000);
    twitter.stopTwitter();

    }

}


