package com.sbu.os;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.apache.storm.spout.Scheme;
import org.apache.storm.Config;

import com.rabbitmq.client.*;

import java.util.Map;
import java.util.Random;
import java.util.*;
import java.io.*;

//This spout randomly emits sentences
public class RabbitMQSpout extends BaseRichSpout{
	QueueingConsumer queueConsumer;
        //Collector used to emit output
        SpoutOutputCollector _collector;
        //Used to generate a random number
        //Random _rand;
	List<String> host_records = new ArrayList<String>();
        String filename = "output.txt";
	//ListIterator<String> listIterator = host_records.listIterator();
	//Open is called when an instance of the class is created
        @Override
                public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
                        //Set the instance collector to the one passed in
                        _collector = collector;
                        //For randomness
                        //_rand = new Random();
			//Read from MQ 
			try{
				//ProcessBuilder pb = new ProcessBuilder("node","app.js");
				//Process p =pb.start();
				BufferedReader reader = new BufferedReader(new FileReader(filename));
				String line;
				while((line = reader.readLine()) != null){
					host_records.add(line);	
				}
				reader.close();

			}
			catch (Exception e)
  			{
  				 System.err.format("Exception occurred trying to read '%s'.", filename);
    				 e.printStackTrace();
				
  			}
                }

        //Emit data to the stream
        @Override
                public void nextTuple() {
                        //Sleep for a bit
                        Utils.sleep(100);
			//String ipAddress = host_records.get(nextInt(host_records.size()));
			ListIterator<String> listIterator = host_records.listIterator();
			while(listIterator.hasNext()){
			String ipAddress = listIterator.next();
                        //Emit the sentence
		//	System.out.println("ipAddress:" + ipAddress);
                        _collector.emit(new Values(ipAddress));
			}
			
                }

        //Ack is not implemented since this is a basic example
        @Override
                public void ack(Object id) {
                }

        //Fail is not implemented since this is a basic example
        @Override
                public void fail(Object id) {
                }

        //Declare the output fields. In this case, an ipAddress
        @Override
                public void declareOutputFields(OutputFieldsDeclarer declarer) {
                        declarer.declare(new Fields("ipAddress"));
                }
}
