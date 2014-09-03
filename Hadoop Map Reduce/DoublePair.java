import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class DoublePair implements Writable{

    double item1;
    double item2;
    /**
     * Constructs a DoublePair with both doubles set to zero.
     */
    public DoublePair() {
        this.item1 = 0;
        this.item2 = 0;
    }
    
    /**
     * Constructs a DoublePair containing double1 and double2.
     */
    public DoublePair(double double1, double double2) {
        this.item1 = double1;
        this.item2 = double2;
    }
    
    /**
     * Returns the value of the first double.
     */
    public double getDouble1() {
        return this.item1;
    }
    
    /**
     * Returns the value of the second double.
     */
    public double getDouble2() {
        return this.item2;
    }
    
    /**
     * Sets the first double to val.
     */
    public void setDouble1(double val) {
        this.item1 = val;
    }
    
    /**
     * Sets the second double to val.
     */
    public void setDouble2(double val) {
        this.item2 = val;
    }
    
    /**
     * write() is required for implementing Writable.
     */
    public void write(DataOutput out) throws IOException {
        out.writeDouble(this.item1);
        out.writeDouble(this.item2);
    }
    
    /**
     * readFields() is required for implementing Writable.
     */
    public void readFields(DataInput in) throws IOException {
        this.item1 = in.readDouble();
        this.item2 = in.readDouble();
    }

}
