package com.sbu.os;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class DNSMonitorTopology {

  //Entry point for the topology
  public static void main(String[] args) throws Exception {
  //Used to build the topology
    TopologyBuilder builder = new TopologyBuilder();
    builder.setSpout("RMQspout", new RabbitMQSpout(), 1);
    builder.setBolt("DNSRcount", new DNSResolutionCount(), 1).fieldsGrouping("RMQspout", new Fields("ipAddress"));
    builder.setBolt("DNSSpike", new DNSSpikeSpout(), 1).fieldsGrouping("RMQspout", new Fields("ipAddress"));
    builder.setBolt("DOSBolt", new DOSSpout(), 1).fieldsGrouping("RMQspout", new Fields("ipAddress"));

    //new configuration
    Config conf = new Config();
    //Set to false to disable debug information when
    // running in production on a cluster
    conf.setDebug(false);
	//If there are arguments, we are running on a cluster
    if (args != null && args.length > 0) {
      	//parallelism hint to set the number of workers
      	conf.setNumWorkers(3);
//	conf.STORM_ZOOKEEPER_SERVERS="130.245.168.119,130.245.168.120";
//	conf.STORM_LOCAL_DIR="/data";
//	conf.NIMBUS_SEEDS="[130.245.168.119]";
      	//submit the topology
      	StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
    }
    //Otherwise, we are running locally
    else {
      //Cap the maximum number of executors that can be spawned
      //for a component to 3
      conf.setMaxTaskParallelism(3);
      //LocalCluster is used to run locally
      LocalCluster cluster = new LocalCluster();
      //submit the topology
      cluster.submitTopology("dns-monitor", conf, builder.createTopology());
      //sleep
      Thread.sleep(10000000);
      //shut down the cluster
      cluster.shutdown();
    }
  }
}
