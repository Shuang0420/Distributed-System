package org.myorg;

import java.io.IOException;
import java.util.StringTokenizer;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TotalTwoVowels2 extends Configured implements Tool {

    public static class WordCountMap extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

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
            // process one line at a time
            String line = value.toString();
            // tokenize the line
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                // use regex to check if the line contains exactly two vowels
                Pattern p = Pattern
                        .compile("[^aAeEiIoOuUyY]*[aAeEiIoOuUyY]{1}[^aAeEiIoOuUyY]*[aAeEiIoOuUyY]{1}[^aAeEiIoOuUyY]*");
                Matcher m = p.matcher(tokenizer.nextToken());
                // if the word contains two vowels
                if (m.matches()) {
                    // emits a key-value pair of <TwoVowels,1>
                    context.write(new Text("TwoVowels"), one);
                }
                // if the word doesn't contain two vowels
                else {
                    // emits a key-value pair of <NotTwoVowels,1>
                    context.write(new Text("NotTwoVowels"), one);
                }
            }
        }
    }

    /**
     * Sums up the values (the occurrence counts for each key)
     */
    public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
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
        job.setJarByClass(TotalTwoVowels2.class);
        job.setJobName("wordcount");

        // set output key value type
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // set mapper, combiner, reducer class
        job.setMapperClass(WordCountMap.class);
        // combiner: sort and aggregate the values for each map, the same effect
        // as
        // reducer
        job.setCombinerClass(WordCountReducer.class);
        job.setReducerClass(WordCountReducer.class);

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
        int result = ToolRunner.run(new TotalTwoVowels2(), args);
        System.exit(result);
    }

}