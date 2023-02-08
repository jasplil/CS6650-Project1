package client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class PropertiesHandler {
    private final Properties prop = new Properties();

    /**
     * Initialize client.PropertiesHandler
     */
    private PropertiesHandler() {
        // reads properties file
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("sc.properties");
        System.out.println("All properties to be sent: ");

        // reads a property list
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new client.PropertiesHandler instance
     */
    private static class InstanceInit {
        private static final PropertiesHandler INSTANCE = new PropertiesHandler();
    }

    /**
     * Return an instance of client.PropertiesHandler
     * @return instance of client.PropertiesHandler
     */
    public static PropertiesHandler getInstance() {
        return InstanceInit.INSTANCE;
    }

    /**
     * Get the value according to a key
     * @param key key of a key-value pair
     * @return value of a key value pair
     */
    public String getValue(String key){
        System.out.println("returning key: "+ key + " and value: " + prop.getProperty(key));
        return prop.getProperty(key);
    }

    /**
     * Get all the properties
     * @return all the values
     */
    public Set<String> getAllValues(){
        return prop.stringPropertyNames();
    }

    /**
     * Get whether there is existing key-value pair
     * @param key key of a key-value pair
     * @return whether there is existing key-value pair
     */
    public boolean containsKey(String key){
        return prop.containsKey(key);
    }
}
