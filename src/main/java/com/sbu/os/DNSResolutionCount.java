package com.sbu.os;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import org.apache.storm.Constants;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.Config;

// For logging
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;

//There are a variety of bolt types. In this case, use BaseBasicBolt
public class DNSResolutionCount extends BaseBasicBolt {
	//Create logger for this class
//	private static final Logger logger = LogManager.getLogger(DNSResolutionCount.class);
	//For holding words and counts
	Map<String, Integer> counts = new HashMap<String, Integer>();

	//execute is called to process tuples
	@Override
		public void execute(Tuple tuple, BasicOutputCollector collector) {
			//If it's a tick tuple, emit all words and counts
			String word = tuple.getString(0);
			Integer count = counts.get(word);
			if (count == null) {
				//logger.info("Anomaly: The website "+word+" has been accessed for the first time!!!");
				System.out.println("Anomaly: The website "+word+" has been accessed for the first time!!!");
				count=0;

			}
				collector.emit(new Values(word));
				count++;
				counts.put(word, count);
				/*
				Set keys = counts.entrySet();
				Iterator i = keys.iterator(); 
				while(i.hasNext()){
					Map.Entry me = (Map.Entry)i.next();
					String key =(String)( me.getKey());
					int value =(Integer) me.getValue();
					System.out.println(key + ":" + value);
				}
				*/
			
		}

	@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}
}
