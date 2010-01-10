package dproxies.tuple;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Writable {

    void write(DataOutput dataOutput) throws IOException;

    void read(DataInput dataInput) throws IOException;
}
