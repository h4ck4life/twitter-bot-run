package com.filaventscorp.orangterbang;

import com.ECS.client.jax.AWSECommerceService;
import com.ECS.client.jax.Item;
import com.ECS.client.jax.ItemSearchRequest;
import com.ECS.client.jax.Items;
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
    private static String keywords[] = {
            "i need bag",
            "i love him",
            "travel bag",
            "gift for him",
            "present for him",
            "i want new bag"};

    private static String replies[] = {
            " make a surprise, get a nice bag as a present for him, buy it here > ",
            " make him happy by getting a present. He might like this > ",
            " he probably loves this nice bag > ",
            " nice leather bag for him. Make him smile. Buy it here > "
    };

    public static void main(String[] args) throws IOException, TwitterException, de.malkusch.amazon.ecs.exception.RequestException {

        AMAZON_API = configureAmazonAPI();
        TWITTER_API = configureTwitterAuth();
        rnd = new Random();

        // ---- AMAZON ------ //
        ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
        itemSearchRequest.setSearchIndex("Luggage");
        itemSearchRequest.setKeywords("leather men");
        //itemSearchRequest.getResponseGroup().add("Offers");
        itemSearchRequest.setItemPage(BigInteger.valueOf(rnd.nextInt(10)));
        foundItems = AMAZON_API.getItemSearch().call(itemSearchRequest);
        Item selectItem = foundItems.getItem().get(rnd.nextInt(10));
        //System.out.println("TITLE: " + selectItem.getItemAttributes().getTitle());
        //System.out.println("URL: " + selectItem.getDetailPageURL());

        // ----- TwITTER ---- //
        Query query = new Query(keywords[rnd.nextInt(keywords.length)]);
        QueryResult result = TWITTER_API.search(query);


        Status sts = result.getTweets().get(rnd.nextInt(result.getTweets().size()));
        String stats = "@" + sts.getUser().getScreenName() + replies[rnd.nextInt(replies.length)] + selectItem.getDetailPageURL() ;

        StatusUpdate statReply = new StatusUpdate(stats);
        statReply.setInReplyToStatusId(sts.getId());

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
