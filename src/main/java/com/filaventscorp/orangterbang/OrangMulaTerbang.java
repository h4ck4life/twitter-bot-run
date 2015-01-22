package com.filaventscorp.orangterbang;

import com.ECS.client.jax.*;
import de.malkusch.amazon.ecs.ProductAdvertisingAPI;
import de.malkusch.amazon.ecs.configuration.Configuration;
import de.malkusch.amazon.ecs.configuration.PropertiesConfiguration;
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
            " make a surprise, get a nice bag as a present for him, buy it here > ",
            " make him happy by getting a present. He might like this > ",
            " he probably loves this nice bag > ",
            " nice leather bag for him. Make him smile. Buy it here > "
    };

    public static void main(String[] args) throws IOException, TwitterException, de.malkusch.amazon.ecs.exception.RequestException, NullPointerException {

        AMAZON_API = configureAmazonAPI();
        TWITTER_API = configureTwitterAuth();
        rnd = new Random();


        // ----- TwITTER ---- //
        Query query = new Query(keywords[rnd.nextInt(keywords.length)]);
        QueryResult result = TWITTER_API.search(query);


        Status sts = result.getTweets().get(rnd.nextInt(result.getTweets().size()));


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
                stats = "@" + sts.getUser().getScreenName() + replies[rnd.nextInt(replies.length)] + selectItem.getDetailPageURL() + " [" + offerList.getPrice().getFormattedPrice() + "]";
                if(offerList.getSalePrice() != null) {
                    //System.out.println("Sale Price: " + offerList.getSalePrice().getFormattedPrice());
                }
                if(offerList.getAmountSaved() != null) {
                    //System.out.println("Amount Saved: " + offerList.getAmountSaved().getFormattedPrice());
                }
                if(offerList.getPercentageSaved() != null) {
                    //.out.println("%" + offerList.getPercentageSaved() + " saved!");
                    stats = "@" + sts.getUser().getScreenName() + replies[rnd.nextInt(replies.length)] + selectItem.getDetailPageURL() + " [" + offerList.getPrice().getFormattedPrice() + "]. %" + offerList.getPercentageSaved() + " SAVED!";
                }
                //System.out.println("Availability: " + offerList.getAvailability());
            }
        }

        StatusUpdate statReply = new StatusUpdate(stats);
        statReply.setInReplyToStatusId(sts.getId());

        //System.out.println(stats);
        TWITTER_API.updateStatus(statReply);


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
