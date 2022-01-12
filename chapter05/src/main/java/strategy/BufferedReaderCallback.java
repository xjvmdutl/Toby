package strategy;

import java.io.BufferedReader;
import java.io.IOException;

public interface BufferedReaderCallback {
    Integer doSomeThingWithReader(BufferedReader br) throws IOException;
}
