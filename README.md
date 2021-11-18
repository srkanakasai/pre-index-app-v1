# Pre Indexing App

## Properties to be made sure before using the app
-
        ```
        ##Number of shards in each index
        preindex.shards=12
        
        ##Size of each shard in GB
        preindex.shardsize=20
        
        ##Fill percentage in each Index. Example : only 80 % of the Index memory allocated will be considered for pre-indexing. 
        preindex.fillThreshold=80
        
        ##INdex memory consumption percentage compared to the actual DOC size
        preindex.indexToDocMem=15
        
        ## Not used. Ignore this. Will be deprecated
        preindex.tenant=BNY
        
        ##Predefined outliers will be used
        preindex.enableOutliers=true
        
        ##upper bound threshold up to which the histogram data will be ignored.Anything below or eq to this date will be ignored.
        preindex.startDate=1899-12-31
        
        ##WOWO to enabled/disable ES persistance 
        preindex.isEsPersistenceEnabled=true
        
        ##WOWO to enabled/disable Index data persistance.
        preindex.isMongoPersistenceEnabled=true
        
        ## WOWO to run the app in test mod which would only provide the snapshot of what indexes are planned to get created. Actual indexing into ES and mongo are disabled when the value is `true`
        preindex.isTestMode=true
        
        ## Standard input file name 
        preindex.batchInput=batch.txt
        # Sample file format
        #	TENANT|REGION|FILE_NAME|OUTLIERS
        #	BNY|APAC|APACHistogram.txt|2012-01-01,2021-12-31
        #	BNY|EMEA|EMEAHistogram.txt|2009-01-01,2021-12-31
        #	BNY|NAM|NAMHistogram.txt|2006-01-01,2021-12-31
        #

        # Mongo Host and port
        mongo.host=192.168.204.129
        mongo.db=sridhar2

        #ES host and port
        es.url=192.168.204.129:9200

        server.archive.number_of_replicas=2
        alcatraz.site.id=1
        indexmanager.archive.schemaVersion=5

        ## Retry configs
        indexretry.maxAttempts=2
        #in MS
        indexretry.maxDelay=100
        ```

## How to RUN?

- Create a folder like - `C:/batch`
- Place the histogram files in above folder. Samples Checked in.
    ```
    APACHistogram.txt
    EMEAHistogram.tx
    NAMHistogram.txt
    ```
- Create a file `batch.txt` in the folder created. 
- Have enough entries as below for batch to process like below. FIle takes in the tenant name, the GEO name, histogram file name and ouliers are optional.
    ```
    TENANT|REGION|FILE_NAME|OUTLIERS
    BNY|APAC|APACHistogram.txt|2012-01-01,2021-12-31
    BNY|EMEA|EMEAHistogram.txt|2009-01-01,2021-12-31
    BNY|NAM|NAMHistogram.txt|2006-01-01,2021-12-31
    ```
- Use the JAR generated and run the app as below
    ```
    java -jar pre-index-app-v1-1.0-SNAPSHOT.jar --input_dir=C:\batch
    ```
- Indexes created in ES and Metadata are persisted in the MONGO. 
- A snapshot of all the indexes created is written to files in the same `input_dir`.
- Snapshot samples are checked in. 
    ```
    Examples:
    APAC_indexes.txt
    ```
- Logs are available in `pre-index-app.log` to debug. 
- Final Error logs are separately logger in `pre-index-app-error.log`
