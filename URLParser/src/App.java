import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        
        List<String> stringList = new ArrayList<String>();
        List<Parser> parserList = new ArrayList<Parser>();
        
        
        stringList.add("jdbc:mysql://host,failoverhost.:port/database?propertyName1=propertyVal1&propertyName2=propertyValue2");
        /*stringList.add("jdbc:mysql://localhost:3306/mydatabase");
        stringList.add("jdbc:mysql://USER:PASSWORD@HOST:PORT/DATABASE?KEY1=VALUE&KEY2=VALUE&KEY3=VALUE");
        stringList.add("jdbc:mongodb://admin:mypassword@example.com:27017/?authSource=admin");
        stringList.add("jdbc:postgresql://other@/otherdb?connect_timeout=10&application_name=myap");
        stringList.add("jdbc:oracle:thin:@ldap://ldap.example.com:7777/sales,cn=salesdept,cn=OracleContext,dc=com");
        stringList.add("jdbc:sqlserver://server:port;encrypt=true;databaseName=AdventureWorks;user=user;password=password");
        stringList.add("jdbc:sqlserver://<server>:<port>;encrypt=true;databaseName=AdventureWorks;user=<user>;password=<password>");
        stringList.add("jdbc:mysql://host1:3306,host2:3306/mydatabase?loadBalanceStrategy=bestResponse");
        stringList.add("jdbc:mysql://localhost:3306/mydatabase?user=root&password=secret&useSSL=true&serverTimezone=UTC&failOverReadOnly=false&autoReconnect=true");
        stringList.add("jdbc:mongodb://localhost:27017,mySecondaryHost:27017/mydatabase?replicaSet=myReplicaSet&readPreference=secondaryPreferred&ssl=true");
        stringList.add("jdbc:postgresql://localhost:5432/mydatabase?user=postgres&password=secret&sslmode=require&applicationName=TestApp");
        stringList.add("jdbc:oracle:thin:@ldap://localhost:1521/xe?user=admin&password=secret&connectionCaching=true&defaultRowPrefetch=50");
        stringList.add("jdbc:sqlserver://localhost:1433;databaseName=mydatabase;user=sa;password=secret;encrypt=true;trustServerCertificate=true;loginTimeout=30");

        stringList.add("jdbc:mariadb://localhost:3306/testdb?user=admin&password=secret&serverTimezone=UTC&useSSL=true");
        stringList.add("jdbc:mongodb://user:password@host1:27017,host2:27018,host3:27019/mydb?replicaSet=myReplicaSet&ssl=true&authSource=admin");
        stringList.add("jdbc:sqlserver://dbserver.example.com:1433;databaseName=SalesDB;integratedSecurity=true;encrypt=true;trustServerCertificate=false");
        stringList.add("jdbc:oracle:thin:@ldap://ldap.example.com:7777/sales,cn=salesdept,cn=OracleContext,dc=example,dc=com");
        stringList.add("jdbc:postgresql://localhost:5432/mydatabase?user=postgres&password=secret&sslmode=require&applicationName=myApp");

        */
        

        for(String entry : stringList){
            
            Parser parse = new Parser(entry);
            parserList.add(parse);
            //System.out.println(entry);            //Uncomment to see the original URL provided
            //parse.printAll();                     //Uncomment to see all the attributes deconstructed from the URL
            //System.out.println(parse.getUrl());   //Uncomment to see the reconstructed URL based on the object's input
            
        }
    }
}
