package fm.last.api;

import java.io.Serializable;

/**
 * @author jennings Date: Oct 23, 2008
 */
public class Station implements Serializable {

        private static final long serialVersionUID = 1499806102972292532L;
        private String name;
        private String type;
        private String url;
        private String supportsDiscovery;

        public Station(String name, String type, String url, String supportsDiscovery) {
                this.name = name;
                this.type = type;
                this.url = url;
                this.supportsDiscovery = supportsDiscovery;
        }

        public String getName() {
                return name;
        }

        public String getType() {
                return type;
        }

        public String getUrl() {
                return url;
        }

        public String getSupportsDiscovery() {
                return supportsDiscovery;
        }
}