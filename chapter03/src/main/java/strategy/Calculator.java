package strategy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public Integer calcSum(String filepath) throws IOException {
        /*
        BufferedReaderCallback sumCallback =
                new BufferedReaderCallback() {
                    @Override
                    public Integer doSomeThingWithReader(BufferedReader br) throws IOException {
                        Integer sum = 0;
                        String line = null;
                        while ((line = br.readLine())!= null){
                            sum += Integer.valueOf(line);
                        }
                        return sum;
                    }
                };
        return fileReadTemplate(filepath,sumCallback);
         */
        LineCallback<Integer> lineCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value + Integer.valueOf(line);
            }
        };
        return lineReadTemplate(filepath,lineCallback,0);
    }


    public Integer calcMultiply(String filepath) throws IOException {
        /*
        BufferedReaderCallback multiplyCallback =
                new BufferedReaderCallback() {
                    @Override
                    public Integer doSomeThingWithReader(BufferedReader br) throws IOException {
                        Integer sum = 1;
                        String line = null;
                        while ((line = br.readLine())!= null){
                            sum *= Integer.valueOf(line);
                        }
                        return sum;
                    }
                };
        return fileReadTemplate(filepath,multiplyCallback);
         */
        LineCallback<Integer> lineCallback = new LineCallback<>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value * Integer.valueOf(line);
            }
        };
        return lineReadTemplate(filepath,lineCallback,1);
    }

    public String concatenate(String filepath) throws IOException {
        LineCallback<String> concatenateCallback = new LineCallback<String>() {
            @Override
            public String doSomethingWithLine(String line, String value) {
                return value + line;
            }
        };
        return lineReadTemplate(filepath,concatenateCallback,"");
    }

    public Integer fileReadTemplate(String filePath,BufferedReaderCallback callback) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            int ret = callback.doSomeThingWithReader(br);
            return ret;
        }catch (IOException e){
            System.out.println(e.getMessage());
            throw e;
        }finally {
            if(br != null) {
                try {
                    br.close();
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    public <T> T lineReadTemplate(String filePath,LineCallback<T> callback, T initVal) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            T res = initVal;
            String line = null;
            while ((line = br.readLine())!= null){
                res = callback.doSomethingWithLine(line,res);
            }
            return res;
        }catch (IOException e){
            System.out.println(e.getMessage());
            throw e;
        }finally {
            if(br != null) {
                try {
                    br.close();
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }

}
