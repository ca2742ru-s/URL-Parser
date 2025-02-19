import java.util.Map;
import java.util.Map.Entry;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class Parser{
    
    //Initialize base attributes
    final String URL;
    private String API;
    private String driver;
    
    private LinkedList<String> hostList;
    private LinkedList<String> portList;
    private String username;
    private String password;
    private String format;
    
    private String service;
    private Map<String, ArrayList<String>> options;
    private ArrayList<String> schemas;

    private boolean foundInOptions;

    //Parser constructor
    public Parser(String url) throws Exception{
        
        if(url == null || url.isEmpty())
            throw new IllegalArgumentException("URL is empty or null");
        
        this.URL = url;
        hostList = new LinkedList<String>();
        portList = new LinkedList<String>();
        options = new LinkedHashMap<String, ArrayList<String>>();

        schemas = new ArrayList<String>();
        schemas.add("jdbc:oracle:thin:@ldap");
        schemas.add("jdbc:mysql");
        schemas.add("jdbc:sqlserver");
        schemas.add("jdbc:postgresql");
        schemas.add("jdbc:mongodb");
        schemas.add("jdbc:mariadb");

        this.foundInOptions = false;

        //Find driver and API by separating at "://"
        String[] tmp = url.split("://");
        if(!schemas.contains(tmp[0])){
            throw new IllegalArgumentException("This Parser does not accept your provided schema: missing driver and/or API");
        }
        if(tmp[0].isEmpty())
            throw new MalformedURLException("This URL is malformed or follow wrong syntax");

        
        this.driver = tmp[0].split(":", 2)[0];
        this.API = tmp[0].split(":", 2)[1];
        
        parseURL(tmp[1]);
        
    }

    private void parseURL(String URL){
        /*
         * This parser tries to follow some set protocols.
         * Splitting chars like "@;,/" are handled differently based on what kind of protocol you follow.
         * I have tried to implement a default query parameter format with ? ; / as main separators.
         * I also implemented a Distinguished Name protocol with / , as main separator
         * and one Semicolon Delimited Properties protocol with semicolons ; as main separator
         */

        String[] tmp;
        //Find optional Username and Password by separating at "@"
        if(URL.contains("@")){
            tmp = URL.split("@");
            this.username = (tmp[0].contains(":")) ? tmp[0].split(":")[0] : tmp[0];
            this.password = (tmp[0].contains(":")) ? tmp[0].split(":")[1] : null;
            URL = tmp[1];
        }
        //If no username or password found, set to null
        else{
            this.username = null;
            this.password = null;
        }
        
        //Separate host and potential port with database and optionals by "/"
        String regex = "/";

        //Special case for MS SQL Server
        if(this.driver.contains("sqlserver"))
            regex = ";";
        tmp = URL.split(regex, 2);

        //Multiple hosts and ports can exist and are divided by ",". If present they will be split up and processed one by one
        if(tmp[0].contains(",")){
            //Multiple hosts-ports found
            String[] tmpHost = tmp[0].split(",");
                for(String entry : tmpHost){
                    String[] option = entry.split(":");
                    if(option.length > 1){
                        this.hostList.add(option[0]);
                        this.portList.add(option[1]);    
                    }
                    //Missing port (assuming number of ports <= number of hosts)
                    else{
                        this.hostList.add(option[0]);
                        this.portList.add(null);
                    }
                        
                }
        }
        //If only one host-port pair is present
        else if(!tmp[0].contains(",")){
            String[] option = tmp[0].split(":");
            if(option.length > 1){
                this.hostList.add(option[0]);
                this.portList.add(option[1]);
            }
            else{
                if(!option[0].isEmpty()){
                    this.hostList.add(option[0]);
                    this.portList.add(null);
                }
                    
            }
                
        }

        //If there is more to the given URL, find potential databases and optional arguments
        if(tmp.length > 1){
            URL = tmp[1];
            regex = "&";
            //Specific case for MS SQL
            if(URL.contains(";")){
                regex = ";";
                this.format = "PD"; //Format Property Delimiter
            }
                

            //Specific case for some Oracle
            else if(URL.contains(",")){
                regex = ",";
                tmp = URL.split(regex, 2);
                this.service = tmp[0];
                this.format = "DN"; //Distinguished Name
            }
            //For most cases split by "?" and "&"
            else{
                tmp = URL.split("\\?");
                this.service = tmp[0];
                this.format = "default";
            }
                

            //If optional arguments are present, find and add into optional map
            if(tmp.length > 1){
                URL = tmp[1];
                //Here the specific regex are used. Oracle splits by ",", MS SQL by ";", and the rest by "&"
                tmp = URL.split(regex);
                //Iterate each optional argument
                for(String entry : tmp){
                    //Optional argument and their value are always split by "="
                    String[] option = entry.split("=");
                    //Switch-case to handle username, password and service stored as optional arguments
                    switch (option[0]) {
                        case "user":
                            this.username = option[1];
                            this.foundInOptions = true;
                            break;
                        case "password":
                            this.password = option[1];
                            break;
                        case "databaseName":
                            this.service = option[1];
                            break;
                        default:
                            if(options.containsKey(option[0])){
                                this.options.get(option[0]).add(option[1]);
                            }
                            else{
                                ArrayList<String> tmpList = new ArrayList<>();
                                tmpList.add(option[1]);
                                this.options.put(option[0], tmpList);
                            }
                    }
                    
                }
            }
        }
        //printAll();
        //System.out.println(this.URL);
    }

    //Print all function to visualize
    public void printAll(){
        System.out.println("driver: " + driver);
        System.out.println("API: " + API);
        System.out.println("username: " + username);
        System.out.println("password: " + password);
        for(int i = 0; i < hostList.size(); i++){
            System.out.println("host: " + hostList.get(i));
            System.out.println("port: " + portList.get(i));
        }
        System.out.println("service: " + service);
        for(Entry<String, ArrayList<String>>  entry : options.entrySet()){
            System.out.println(entry);
        }
        System.out.println("_____________");
    }

    //Fallback method in case defaut port is needed
    private String fallbackPort(String DBMS){
        switch (DBMS) {
            case "oracle":
                return "1521";

            case "postgresql":
                return "5432";

            case "mysql":
                return "3306";
            
            case "sqlserver":
                return "1433";
            
            case "mongodb":
                return "27017";
        
            default:
                return null;
        }
    }

    public void addData(String name, String value){
        if(name.isEmpty() || value.isEmpty()){
            return;
        }
        if(this.options.containsKey(name))
            this.options.get(name).add(name);
        else{
            ArrayList<String> list = new ArrayList<>();
            list.add(value);
            this.options.put(name, list);
        }   
    }
    
    //Assemble a URL based on the saved object attributes
    public String getUrl(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.driver + ":" + this.API + "://");

        //MS SQL store username:password in it's optionals
        //The rest puts username:pass@host:port
        if(this.API != "sqlserver"){
            if(this.username != null && !foundInOptions){
                sb.append(this.username);
                if(this.password != null)
                    sb.append(":" + this.password + "@");
                else
                    sb.append("@");
            }
                
        }
        //Check if there are multiple or singular hosts and proceed accordingly
        //Singular
        if(hostList.size() < 2 && hostList.size() > 0){
            sb.append(hostList.get(0));
            if(portList.get(0) != null)
                sb.append(":" + portList.get(0));
        }
        //Multiple
        else if (hostList.size() >= 2){
            sb.append(hostList.get(0));
            if(portList.get(0)!=null)
                sb.append(":" + portList.get(0));
            for(int i = 1; i < hostList.size(); i++){
                sb.append("," + hostList.get(i));
                if(!portList.get(i).equals(null))
                    sb.append(":" + portList.get(i));
            }
        }
        //Distinguished Name protocol is followed
        if(this.format == "DN"){
            sb.append("/" + this.service);
            for(Entry<String, ArrayList<String>> entry : this.options.entrySet()){
                for(String str : entry.getValue()){
                    sb.append("," + entry.getKey() + "=" + str);
                }
            }
        }
        //Property Delimiter protocol is followed
        else if(this.format == "PD"){
            for(Entry<String, ArrayList<String>> entry : this.options.entrySet()){
                for(String str : entry.getValue()){
                    sb.append(";" + entry.getKey() + "=" + str);
                }
            }
        }
        //Default
        else if(this.format == "default"){
            sb.append("/" + this.service);
            if(!this.options.isEmpty()){
                sb.append("?");
                if(foundInOptions)
                    sb.append("user=" + this.username + "&password=" + this.password + "&");
                for(String str : this.options.keySet()){
                    for(String entry : this.options.get(str)){
                        sb.append(str + "=" + entry + "&");
                    }
                }
                sb.deleteCharAt(sb.length()-1);
            }
            
        }
        return sb.toString();
    }

    //Getters and Setters
    public String getAPI(){
        return this.API;
    }

    public void setAPI(String API){
        this.API = API;
    }

    public String getDriver(){
        return this.driver;
    }

    public void setDriver(String driver){
        this.driver = driver;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPort(){
        return portList.toString();
    }

    public void setPort(LinkedList<String> portList){
        this.portList = portList;
    }

    public String getHost(){
        return hostList.toString();
    }

    public void setHost(LinkedList<String> hostList){
        this.hostList = hostList;
    }
    
    public String getService(){
        return service;
    }

    public void setService(String service){
        this.service = service;
    }

    public String getOptions(){
        return options.toString();
    }

    public void setOptions(LinkedHashMap<String, ArrayList<String>> options){
        this.options = options;
    }

}