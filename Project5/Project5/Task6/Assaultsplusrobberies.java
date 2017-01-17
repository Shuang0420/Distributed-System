import java.io.IOException;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Assaultsplusrobberies extends Configured implements Tool {

    public static class CrimeCountMap extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);

        /**
         * Maps input key/value pairs to a emits key-value pairs of <<word>, 1>.
         * 
         * @param key
         *            input key
         * @param value
         *            input value
         * @param context
         *            output
         * @throws IOException
         * @throws InterruptedException
         */
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            // split the line by tab and get the crime type
            String[] columns = line.split("\t");
            String type = columns[4];
            // check if it's ROBBERY or AGGRAVATED ASSAULT
            if (type.equals("ROBBERY") || type.equals("AGGRAVATED ASSAULT")) {
                // emits a key-value pair of <Assaultsplusrobberies,1>
                context.write(new Text("Assaultsplusrobberies"), one);
            }
        }
    }

    /**
     * Sums up the values (the occurrence counts for each key)
     */
    public static class CrimeCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            // sum values with the same key after sorting
            for (IntWritable value : values) {
                sum += value.get();
            }
            // emits key-value final result
            context.write(key, new IntWritable(sum));
        }

    }

    /**
     * Set job configuration and run.
     */
    public int run(String[] args) throws Exception {

        Job job = new Job(getConf());
        job.setJarByClass(Assaultsplusrobberies.class);
        job.setJobName("CrimeCount");

        // set output key value type
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // set mapper, combiner, reducer class
        job.setMapperClass(CrimeCountMap.class);
        // combiner: sort and aggregate the values for each map, the same effect
        // as
        // reducer
        job.setCombinerClass(CrimeCountReducer.class);
        job.setReducerClass(CrimeCountReducer.class);
        // set input and output format
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        // set input and output path
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // calls the job.waitForCompletion to submit the job and monitor its
        // progress
        boolean success = job.waitForCompletion(true);
        return success ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        int result = ToolRunner.run(new Assaultsplusrobberies(), args);
        System.exit(result);
    }

}
