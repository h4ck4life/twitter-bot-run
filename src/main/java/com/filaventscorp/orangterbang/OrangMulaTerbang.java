package com.filaventscorp.orangterbang;

import com.ECS.client.jax.*;
import de.malkusch.amazon.ecs.ProductAdvertisingAPI;
import de.malkusch.amazon.ecs.configuration.Configuration;
import de.malkusch.amazon.ecs.configuration.PropertiesConfiguration;
import de.malkusch.amazon.ecs.exception.RequestException;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Properties;
import java.util.Random;

/**
 * Created by MAAAMOHZ on 21/1/2015.
 */
public class OrangMulaTerbang {

    private static ProductAdvertisingAPI AMAZON_API;
    private static Twitter TWITTER_API;
    private static Random rnd;
    private static Items foundItems;
    private static String stats;
    private static String keywords[] = {
            "i need bag",
            "i love him",
            "travel bag",
            "gift for him",
            "present for him",
            "my husband gift",
            "my father gift",
            "my brother gift",
            "my boyfriend",
            "love husband",
            "love my father",
            "need backpack",
            "work bag",
            "i want new bag"};

    private static String replies[] = {
            " make a surprise, get a nice bag for someone you love > ",
            " make someone happy. The person might like this > ",
            " you probably love this nice bag > ",
            " nice leather bag for him. Make someone smile > ",
            " Best deal for a nice looking bag > "
    };

    public static void main(String[] args) throws IOException, TwitterException, de.malkusch.amazon.ecs.exception.RequestException, NullPointerException {
        try {
            facade();
        } catch (TwitterException e) {
            facade();
        }
    }

    public static void facade() throws IOException, TwitterException, de.malkusch.amazon.ecs.exception.RequestException, NullPointerException {
        AMAZON_API = configureAmazonAPI();
        TWITTER_API = configureTwitterAuth();
        rnd = new Random();

        // ----- TwITTER ---- //
        //Query query = new Query(keywords[rnd.nextInt(keywords.length)]);
        Query query = new Query(getTrendingTags());
        QueryResult result = TWITTER_API.search(query);

        // Get new Tweets collection from search
        Status sts = result.getTweets().get(rnd.nextInt(result.getTweets().size()));
        favoritesAllMentions();
        getRandomProductsFromAmazon(sts, getTrendingTags());
        tweetRandomProductToUser(sts);
    }

    public static String getTrendingTags() throws TwitterException {
        Trends trends = TWITTER_API.getPlaceTrends(23424977);
        Trend[] trend = trends.getTrends();
        /*for (int i = 0; i < trend.length; i++) {
            trend[i].getName();
            System.out.println(trend[i].getName());
        }*/
        return trend[0].getName();
    }

    public static void tweetRandomProductToUser(Status sts) throws TwitterException {
        StatusUpdate statReply = new StatusUpdate(stats);
        statReply.setInReplyToStatusId(sts.getId());

        //System.out.println(stats);
        //System.out.println(statReply.getStatus());
        TWITTER_API.updateStatus(statReply);
    }

    public static void getRandomProductsFromAmazon(Status sts, String trendingTags) throws RequestException {

        // ---- AMAZON ------ //
        ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
        itemSearchRequest.setSearchIndex("Luggage");
        itemSearchRequest.setKeywords("leather men");
        itemSearchRequest.getResponseGroup().add("Large");
        itemSearchRequest.setItemPage(BigInteger.valueOf(rnd.nextInt(9)));
        foundItems = AMAZON_API.getItemSearch().call(itemSearchRequest);
        Item selectItem = foundItems.getItem().get(rnd.nextInt(9));

        //System.out.println("TITLE: " + selectItem.getItemAttributes().getTitle());
        //System.out.println("URL: " + selectItem.getDetailPageURL());

        for (Offer offer : selectItem.getOffers().getOffer()) {
            for (OfferListing offerList : offer.getOfferListing()) {
                //System.out.println("Price: " + offerList.getPrice().getFormattedPrice());
                stats = "@" + sts.getUser().getScreenName() + replies[rnd.nextInt(replies.length)] + selectItem.getDetailPageURL() + " ONLY " + offerList.getPrice().getFormattedPrice() + " Please RT. TQ. " + trendingTags;
                if (offerList.getSalePrice() != null) {
                    //System.out.println("Sale Price: " + offerList.getSalePrice().getFormattedPrice());
                }
                if (offerList.getAmountSaved() != null) {
                    //System.out.println("Amount Saved: " + offerList.getAmountSaved().getFormattedPrice());
                }
                if (offerList.getPercentageSaved() != null) {
                    //.out.println("%" + offerList.getPercentageSaved() + " saved!");
                    stats = "@" + sts.getUser().getScreenName() + replies[rnd.nextInt(replies.length)] + selectItem.getDetailPageURL() + " ONLY " + offerList.getPrice().getFormattedPrice() + " [" + offerList.getPercentageSaved() + "% SAVED!] Please RT. TQ. " + trendingTags;
                }
                //System.out.println("Availability: " + offerList.getAvailability());
            }
        }
    }

    public static void favoritesAllMentions() throws TwitterException {
        // Find all mentions and FAVOURITE THEM!
        ResponseList<Status> stsReply = TWITTER_API.getMentionsTimeline();
        for (Status stsMention : stsReply) {
            try {
                TWITTER_API.createFavorite(stsMention.getId());
            } catch (TwitterException e) {
            }
        }
    }

    public static Twitter configureTwitterAuth() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("yPwCnQGRBFvYDU9jLeYjk5alr")
                .setOAuthConsumerSecret("IXjKzMa8FDTQifyxzpZojwMaAIzRdfJ567QXbW1jNKehP97jqY")
                .setOAuthAccessToken("2989825245-vQAkBvSYLox8QyxtZ1fBc2I2JLvpyEnTMt5Nwmm")
                .setOAuthAccessTokenSecret("Sus3N6uImWHvfLtjC9CRmUZr5CnMkBlOIJpFFukUKVwf0");
        TwitterFactory tf = new TwitterFactory(cb.build());
        return tf.getInstance();
    }

    public static ProductAdvertisingAPI configureAmazonAPI() throws IOException {
        // Instantiate the API
        Properties properties = new Properties();
        properties.load(ProductAdvertisingAPI.class.getResourceAsStream("/properties/amazon.properties"));
        Configuration configuration = new PropertiesConfiguration(properties);
        ProductAdvertisingAPI api = new ProductAdvertisingAPI(configuration,
                new AWSECommerceService().getAWSECommerceServicePortUS());
        return api;
    }
}
