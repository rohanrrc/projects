import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Math;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import java.util.ArrayList;


public class MapReduceProj{

    /*
     * Inputs is a set of (docID, document contents) pairs.
     */
    public static class Map1 extends Mapper<WritableComparable, Text, Text, DoublePair> {
        /** Regex pattern to find words (alphanumeric + _). */
        final static Pattern WORD_PATTERN = Pattern.compile("\\w+");

        private String targetGram = null;
        private int funcNum = 0;


        @Override
            public void setup(Context context) {
                targetGram = context.getConfiguration().get("targetWord").toLowerCase();
                try {
                    funcNum = Integer.parseInt(context.getConfiguration().get("funcNum"));
                } catch (NumberFormatException e) {
                    
                }
            }

        @Override
            public void map(WritableComparable docID, Text docContents, Context context)
            throws IOException, InterruptedException {
                Matcher matcher = WORD_PATTERN.matcher(docContents.toString());
                Func func = funcFromNum(funcNum);
                
                ArrayList<String> wordList = new ArrayList<String>();
                ArrayList<Double> targets = new ArrayList<Double>(); 
                double count=0;
                while (matcher.find()) {
                    String word = matcher.group().toLowerCase();
                    wordList.add(word);
                    if (word.equals(targetGram)){
                        targets.add(count);
                    }
                    count++;
                }
                for(int i = 0; i < wordList.size(); i++){
                    double distance;
                    if(targets.size()==0){
                     distance= Double.POSITIVE_INFINITY;
                    }else if(targets.size()==1 || Math.abs(targets.get(0)-i)<Math.abs(targets.get(1)-i)){
                        distance = Math.abs(targets.get(0)-i);
                    }else{
                        distance = Math.abs(targets.get(1)-i);
                        targets.remove(0);
                    }
                    if (!wordList.get(i).equals(targetGram)){
                        context.write(new Text(wordList.get(i)), new DoublePair(1, func.f(distance)));
                    }
                }

            }

        /** Returns the Func corresponding to FUNCNUM*/
        private Func funcFromNum(int funcNum) {
            Func func = null;
            switch (funcNum) {
                case 0:	
                    func = new Func() {
                        public double f(double d) {
                            return d == Double.POSITIVE_INFINITY ? 0.0 : 1.0;
                        }			
                    };	
                    break;
                case 1:
                    func = new Func() {
                        public double f(double d) {
                            return d == Double.POSITIVE_INFINITY ? 0.0 : 1.0 + 1.0 / d;
                        }
                    };
                    break;
                case 2:
                    func = new Func() {
                        public double f(double d) {
                            return d == Double.POSITIVE_INFINITY ? 0.0 : 1.0 + Math.sqrt(d);
                        }
                    };
                    break;
            }
            return func;
        }
    }

    /** Implementation of the Combiner**/
    public static class Combine1 extends Reducer<Text, DoublePair, Text, DoublePair> {
        @Override
            public void reduce(Text key, Iterable<DoublePair> values,
                    Context context) throws IOException, InterruptedException {
                 
                double Aw = 0;
                double SCw = 0 ;
                for (DoublePair value : values) {
                    Aw = Aw + value.getDouble1();
                    SCw = SCw + value.getDouble2();
                }
                context.write(key, new DoublePair(Aw, SCw));
            }
    }


    public static class Reduce1 extends Reducer<Text, DoublePair, DoubleWritable, Text> {
        @Override
            public void reduce(Text key, Iterable<DoublePair> values,
                    Context context) throws IOException, InterruptedException {
                
                double Aw = 0;
                double SCw = 0;
                double rate = 0;

                for (DoublePair value : values) {
                    Aw = Aw + value.getDouble1();
                    SCw = SCw + value.getDouble2();
                }
                if (SCw > 0){
                    rate = SCw * (Math.pow(Math.log(SCw), 3))/Aw;  
                } 
                context.write(new DoubleWritable(-rate), key);
            }
    }

    public static class Map2 extends Mapper<DoubleWritable, Text, DoubleWritable, Text> {
    }

    public static class Reduce2 extends Reducer<DoubleWritable, Text, DoubleWritable, Text> {

        int n = 0;
        static int N_TO_OUTPUT = 100;

        @Override
            protected void setup(Context c) {
            }

        @Override
            public void reduce(DoubleWritable key, Iterable<Text> values,
                    Context context) throws IOException, InterruptedException {
                
                for(Text value: values){
                    n++;
                    if (n <= N_TO_OUTPUT){
                        context.write(new DoubleWritable(-key.get()), value);
                    }
                }
                
            }
    }


    public static void main(String[] rawArgs) throws Exception {
        GenericOptionsParser parser = new GenericOptionsParser(rawArgs);
        Configuration conf = parser.getConfiguration();
        String[] args = parser.getRemainingArgs();

        boolean runJob2 = conf.getBoolean("runJob2", true);
        boolean combiner = conf.getBoolean("combiner", false);

        System.out.println("Target word: " + conf.get("targetWord"));
        System.out.println("Function num: " + conf.get("funcNum"));

        if(runJob2)
            System.out.println("running both jobs");
        else
            System.out.println("for debugging, only running job 1");

        if(combiner)
            System.out.println("using combiner");
        else
            System.out.println("NOT using combiner");

        Path inputPath = new Path(args[0]);
        Path middleOut = new Path(args[1]);
        Path finalOut = new Path(args[2]);
        FileSystem hdfs = middleOut.getFileSystem(conf);
        int reduceCount = conf.getInt("reduces", 32);

        if(hdfs.exists(middleOut)) {
            System.err.println("can't run: " + middleOut.toUri().toString() + " already exists");
            System.exit(1);
        }
        if(finalOut.getFileSystem(conf).exists(finalOut) ) {
            System.err.println("can't run: " + finalOut.toUri().toString() + " already exists");
            System.exit(1);
        }

        {
            Job firstJob = new Job(conf, "job1");

            firstJob.setJarByClass(Map1.class);

            firstJob.setMapOutputKeyClass(Text.class);
            firstJob.setMapOutputValueClass(DoublePair.class);
            firstJob.setOutputKeyClass(DoubleWritable.class);
            firstJob.setOutputValueClass(Text.class);


            firstJob.setMapperClass(Map1.class);
            firstJob.setReducerClass(Reduce1.class);
            firstJob.setNumReduceTasks(reduceCount);


            if(combiner)
                firstJob.setCombinerClass(Combine1.class);

            firstJob.setInputFormatClass(SequenceFileInputFormat.class);
            if(runJob2)
                firstJob.setOutputFormatClass(SequenceFileOutputFormat.class);

            FileInputFormat.addInputPath(firstJob, inputPath);
            FileOutputFormat.setOutputPath(firstJob, middleOut);

            firstJob.waitForCompletion(true);
        }

        if(runJob2) {
            Job secondJob = new Job(conf, "job2");

            secondJob.setJarByClass(Map1.class);
            secondJob.setMapOutputKeyClass(DoubleWritable.class);
            secondJob.setMapOutputValueClass(Text.class);
            secondJob.setOutputKeyClass(DoubleWritable.class);
            secondJob.setOutputValueClass(Text.class);

            secondJob.setMapperClass(Map2.class);
            secondJob.setReducerClass(Reduce2.class);

            secondJob.setInputFormatClass(SequenceFileInputFormat.class);
            secondJob.setOutputFormatClass(TextOutputFormat.class);
            secondJob.setNumReduceTasks(1);

            FileInputFormat.addInputPath(secondJob, middleOut);
            FileOutputFormat.setOutputPath(secondJob, finalOut);

            secondJob.waitForCompletion(true);
        }
    }

}
