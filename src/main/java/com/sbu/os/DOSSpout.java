package com.sbu.os;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.apache.storm.Constants;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.Config;
import java.util.concurrent.atomic.AtomicInteger;
// For logging
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;

//There are a variety of bolt types. In this case, use BaseBasicBolt
public class DOSSpout extends BaseBasicBolt {
	//Create logger for this class
//	private static final Logger logger = LogManager.getLogger(DNSSpikeSpout.class);
	//For holding total count of DNS
	AtomicInteger count = new AtomicInteger();
	//How often to emit a count of words
	private Integer emitFrequency;

	// Default constructor
	public DOSSpout() {
		emitFrequency=120; // Default to 60 seconds
	}

	// Constructor that sets emit frequency
	public DOSSpout(Integer frequency) {
		emitFrequency=frequency;
	}

	//Configure frequency of tick tuples for this bolt
	//This delivers a 'tick' tuple on a specific interval,
	//which is used to trigger certain actions
	@Override
		public Map<String, Object> getComponentConfiguration() {
			Config conf = new Config();
			conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequency);
			return conf;
		}

	//execute is called to process tuples
	@Override
		public void execute(Tuple tuple, BasicOutputCollector collector) {
			//If it's a tick tuple, emit all words and counts
			if(tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
					&& tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID)) {
				count.set(0);
			} else {
				//Increment the count and store it
				int i = count.incrementAndGet();
				//logger.info("Total count "+i);
				//System.out.println("Total count "+i);
				if(i>=25){
					//logger.info("Threshold reached for max DNS resolutions per second "+i);
					System.out.println("Threshold reached for max DNS resolutions per second "+i);
				}
			}
		}

	//Declare that this emits a tuple containing two fields; word and count
	  @Override
	    public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    //declarer.declare(new Fields("word"));
	    }
}

