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

public class Robberies extends Configured implements Tool {

    public static class CrimeCountMap extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        // initiate (X,Y) coordinate of location 3803 Forbes Avenue in Oakland
        private final static double X = 1354326.897;
        private final static double Y = 411447.7828;
        // initiate 300 meters distance square
        private final static double distanceSquare = Math.pow(300 * 3.2808399, 2);

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
            // check if it's ROBBERY
            if (type.equals("ROBBERY")) {
                // get the coordinate of the location
                double x = Double.parseDouble(columns[0]);
                double y = Double.parseDouble(columns[1]);
                // calculate the distance and check if it's within 300 meters of
                // 3803 Forbes Avenue in Oakland
                if (Math.pow((X - x), 2) + Math.pow((Y - y), 2) <= distanceSquare) {
                    // emits a key-value pair of <Robberies,1>
                    context.write(new Text("Robberies"), one);
                }
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
        job.setJarByClass(Robberies.class);
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
        int result = ToolRunner.run(new Robberies(), args);
        System.exit(result);
    }

}
