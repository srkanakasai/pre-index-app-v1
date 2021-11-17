# Pre Indexing App


## How to RUN?

- Create a folder like - C:/batch
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
- Indexes created are created in the same `input_dir`.
- Samples are checked in. 
    ```
    APAC_indexes.txt
    ```
- Logs are available in `pre-index-app.log` to debug. 