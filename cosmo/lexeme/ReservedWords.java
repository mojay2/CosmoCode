package cosmo.lexeme;

import java.util.HashMap;

public class ReservedWords {
    public static HashMap<String, String> createReservedWordsMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Comet", "comet_token");
        map.put("Voyage", "voyage_token");
        map.put("reception", "reception_token");
        map.put("transmission", "transmission_token");
        map.put("Whirl", "whirl_token");
        map.put("Launch", "launchwhirl_token");
        map.put("Orbit", "orbit_token");
        map.put("Navigate", "navigate_token");
        map.put("Propel", "propel_token");
        return map;
    }
}
