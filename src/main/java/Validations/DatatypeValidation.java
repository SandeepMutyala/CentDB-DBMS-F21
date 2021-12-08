package Validations;

import java.util.Arrays;
import java.util.List;

public class DatatypeValidation {

    private static List<String> dataType= Arrays.asList("INT","int","VARCHAR","varchar","BOOLEAN","boolean","BIGINT","bigint");
    private DatatypeValidation(){}

    public List<String> getDataType() {
        return dataType;
    }

    public static boolean validateTableDataType(String[] columnDatatype ){
        boolean result=true;
        //String[] str=columnDatatype.split(",");
        for(int i=0;i<columnDatatype.length;i++){
            // to split Varchar(30)
            String[] strVarchar=columnDatatype[i].split("\\(");
            if(strVarchar.length>1){
                if(!dataType.contains(strVarchar[0])){
                	System.out.println(strVarchar[0]);
                    result=false;
                    break;
                }
            }
            else{
                if(!dataType.contains(columnDatatype[i])){
                	
                	System.out.println(columnDatatype[i]);
                    result=false;
                    break;
                }
            }

        }
        return result;
    }
}
