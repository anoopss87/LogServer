# LogServer

1) Generate Logs:
  java -cp Quantil-0.0.1-SNAPSHOT.jar quantil.tool.Generator data_path [YYYY-MM-DD]
  
2) Query
  java -cp Quantil-0.0.1-SNAPSHOT.jar quantil.tool.Query data_path
  
  Query format:
  QUERY IP Core_Num YYYY-MM-DD HH1:MM1 YYYY-MM-DD HH2:MM2
  
  Note : YYYY-MM-DD should be same as we are generating logs for one day.
