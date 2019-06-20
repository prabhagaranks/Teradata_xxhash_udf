# Teradata deployment scripts:

```DATABASE pplib;
CALL SQLJ.INSTALL_JAR('CJ!JudfTablehashXXHash.jar', 'tablehash_xxhash', 0);

CREATE FUNCTION tablehash_jxxhash
(col VARCHAR(8000))
RETURNS VARCHAR(100)
CLASS AGGREGATE(256)
LANGUAGE JAVA
NO SQL
PARAMETER STYLE JAVA
DETERMINISTIC
CALLED ON NULL INPUT
EXTERNAL NAME 'tablehash_xxhash:judf.JudfTablehashXXHash.tablehash_xxhash(com.teradata.fnc.Phase,com.teradata.fnc.Context[],java.lang.String) returns java.lang.String';
```
